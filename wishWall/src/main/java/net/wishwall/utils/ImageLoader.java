package net.wishwall.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * @author panRongFu on 2016/4/26.
 * @Description
 * @email pan@ipushan.com
 */
public class ImageLoader {

    private static ImageLoader mInstance;

    /**
     * 图片缓存对象
     */
    private LruCache<String,Bitmap> mLruCache;

    /**
     * 线程池
     */
    private ExecutorService mThreadPool;

    /**
     * 默认线程1个
     */
    private static final int DEAFULT_THREAD_COUNT=1;

    /**
     * 队列的调度方式
     */
    private Type mType = Type.LIFO;
    public enum Type{
        FIFO,LIFO;
    }

    /**
     * 任务队列
     */
    private LinkedList<Runnable> mTaskQueue;

    /**
     * 后台轮询线程
     */
    private Thread mPoolThread;
    private Handler mPoolThreadHander;

    /**
     * 信号量
     */
    private Semaphore mSemaphorePoolThreadHander = new Semaphore(0);
    private Semaphore mSemaphoreThreadPool;

    /**
     * UI线程中的handler
     */
    private Handler mUIHandler;

    /**
     * ImageLoader构造方法
     * @param mThreadCount
     * @param type
     */
    private ImageLoader(int mThreadCount,Type type){
        init(mThreadCount,type);
    }

    /**
     * 使用默认的线程个数，默认的队列调度方式
     * @return
     */
    public static ImageLoader getInstance(){
        if(mInstance == null){

            synchronized (ImageLoader.class){

                if(mInstance == null){
                    mInstance = new ImageLoader(DEAFULT_THREAD_COUNT,Type.LIFO);
                    return mInstance;
                }
            }
        }
        return mInstance;
    }

    /**
     * 自定义线程个数，还有队列调度方式
     * @param mThreadCount
     * @param type
     * @return
     */
    public static ImageLoader getInstance(int mThreadCount,Type type){
        if(mInstance == null){

            synchronized (ImageLoader.class){

                if(mInstance == null){
                    mInstance = new ImageLoader(mThreadCount,type);
                    return mInstance;
                }
            }
        }
        return mInstance;
    }

    /**
     * 初始化
     * @param mThreadCount
     * @param type
     */
    private void init(int mThreadCount, Type type) {

        //后台轮询线程
        mPoolThread = new Thread(){
            @Override
            public void run() {
                super.run();
                Looper.prepare();
                mPoolThreadHander = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        mThreadPool.execute(getTask());

                        try {
                            mSemaphoreThreadPool.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                mSemaphorePoolThreadHander.release();
                Looper.loop();
            }
        };
        mPoolThread.start();

        //获取我们应用的最大可用内存
        int maxMemory = (int)Runtime.getRuntime().maxMemory();
        //缓存的大小是最大可用内存的1/8
        int cacheMemory =  maxMemory/8;
        mLruCache = new LruCache<String, Bitmap>(cacheMemory){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes()*value.getHeight();
            }
        };

        //创建线程池
        mThreadPool = Executors.newFixedThreadPool(mThreadCount);
        mTaskQueue = new LinkedList<Runnable>();
        mType=type;
        mSemaphoreThreadPool = new Semaphore(mThreadCount);
    }


    /**
     * 最核心的方法：
     * 加载图片根据路径path 为imageview设置图片
     * @param path
     * @param imageView
     */
    public void loadImage(final String path, final ImageView imageView){

        imageView.setTag(path);
        if(mUIHandler == null){
            mUIHandler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    //设置图片
                    ImageHolder hodler = (ImageHolder)msg.obj;
                    Bitmap bitmap = hodler.bitmap;
                    ImageView imageView = hodler.imageView;
                    String path = hodler.path;
                    //将path与gettag存储路径进行比较
                    if(imageView.getTag().toString().equals(path)){
                        imageView.setImageBitmap(bitmap);
                    }
                }
            };
        }

        //根据path在缓存中获取bitmap,
        // 若存在则发送显示图片
        // 若不存在则添加一个轮询任务
        Bitmap bitmap = getBitmapFromLruCache(path);
        if(bitmap !=null){
            refreshBitmap(path, imageView, bitmap);
        }else{
            addTask(new Runnable(){
                @Override
                public void run() {
                    //加载图片
                    //图片压缩
                    //获得图片需要显示的大小
                    ImageSize imageSize = getImageViewSize(imageView);
                    //压缩图片
                    Bitmap bm = decodeSampledBitmapFromPath(path, imageSize.width,imageSize.height);
                    //把bitmap加入到缓存中
                    addBtimapToLruCache(path,bm);
                    refreshBitmap(path, imageView, bm);
                    //释放一个信号量
                    mSemaphoreThreadPool.release();
                }
            });
        }
    }

    /**
     * 把bitmap加入到缓存中
     * @param path
     * @param bm
     */
    private void addBtimapToLruCache(String path, Bitmap bm) {
        if(path != null && bm !=null)
            mLruCache.put(path,bm);
    }

    /**
     * 压缩图片
     * @param path
     * @param width
     * @param height
     * @return
     */
    private Bitmap decodeSampledBitmapFromPath(String path, int width, int height) {
        //获得图片的宽和高，并不把图片加载到内存中
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds =true;
        BitmapFactory.decodeFile(path,options);

        options.inSampleSize = caculateInSampleSize(options,width,height);
        //使用获得到的InSampleSize再次解析图片,并把图片加载到内存中
        options.inJustDecodeBounds= false;

        Bitmap bitmap = BitmapFactory.decodeFile(path,options);
        return bitmap;
    }

    /**
     * 根据需求的宽高，和实际的宽高计算InSampleSize
     * @param options
     * @param reqwidth
     * @param reqheight
     * @return
     */
    private int caculateInSampleSize(BitmapFactory.Options options, int reqwidth, int reqheight) {
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;
        if(width>reqwidth || height>reqheight){
            int widthRadio = Math.round(width*1.0f/reqwidth);
            int heightRadio = Math.round(height*1.0f/reqheight);
            inSampleSize = Math.max(widthRadio,heightRadio);
        }
        return inSampleSize;
    }

    /**
     * 获取imageview的宽高
     * @param imageView
     * @return
     */
    private ImageSize getImageViewSize(ImageView imageView) {
        ImageSize imageSize = new ImageSize();
        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
        DisplayMetrics displayMetrics = imageView.getContext().getResources().getDisplayMetrics();
        
        int width = getImageViewFieldValue(imageView,"mMaxHeight");
        if(width <=0){
            width = lp.width; //获取iimageview在layout中神秘的宽度
        }
        if(width <=0 ){
            width=imageView.getMaxWidth();//检查最大值
        }
        if(width <=0){
            width =displayMetrics.widthPixels;
        }

        //int height = imageView.getHeight();//获取imageView的实际宽度
        int height = getImageViewFieldValue(imageView, "mMaxHeight");
        if(height <=0){
            height = lp.height; //获取iimageview在layout中神秘的宽度
        }
        if(height <=0 ){
            height=imageView.getMaxHeight();//检查最大值
        }
        if(height <=0){
            height =displayMetrics.heightPixels;
        }
        imageSize.width = width;
        imageSize.height = height;
        return imageSize;

    }

    /**
     * 通过反射获取view的某个属性值
     * @return
     */
    private static int getImageViewFieldValue(Object object, String fieldName){
        int value = 0;
        try {
            Field field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue = field.getInt(object);
            if(fieldValue>0 && fieldValue < Integer.MAX_VALUE){
                value = fieldValue;
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
        return value;
    }

    /**
     * 想任务队列添加任务,
     * 一旦添加任务，则通知线程池
     * 取出一个线程出来执行
     * @param runnable
     */
    private synchronized void addTask(Runnable runnable) {
        mTaskQueue.add(runnable);
        try {
            if(mPoolThreadHander == null)
                mSemaphorePoolThreadHander.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mPoolThreadHander.sendEmptyMessage(0x01);
    }

    /**
     * 从任务队列中取出一个线程（任务）
     * @return
     */
    private Runnable getTask() {
        if(mType == Type.LIFO){
            return mTaskQueue.removeLast();
        }else if(mType == Type.FIFO){
            return mTaskQueue.removeFirst();
        }
        return null;
    }

    /**
     * 显示图片
     * @param path
     * @param imageview
     * @param bitmap
     */
    private void refreshBitmap(String path, ImageView imageview, Bitmap bitmap) {
        Message msg = Message.obtain();
        ImageHolder hodler = new ImageHolder();
        hodler.bitmap = bitmap;
        hodler.imageView = imageview;
        hodler.path = path;
        msg.obj = hodler;
        mUIHandler.sendMessage(msg);
    }

    /**
     * 根据路径path在缓存中寻找bitmap
     * @param path
     * @return
     */
    private Bitmap getBitmapFromLruCache(String path) {
        return mLruCache.get(path)==null? null:mLruCache.get(path);
    }

    private class ImageHolder{
        Bitmap bitmap;
        ImageView imageView;
        String path;
    }

    private class ImageSize{
        int width;
        int height;
    }
}
