package net.wishwall.utils;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;

import net.wishwall.adapter.WishAdapter;
import net.wishwall.adapter.WishItemAdapter;
import net.wishwall.domain.WishsDTO;
import net.wishwall.views.RecyclerViewItemDecoration;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * @author panRongFu on 2016/5/19.
 * @Description
 * @email pan@ipushan.com
 */
public class WishImgLoader {

    private static WishImgLoader mInstance;
    /**
     * 线程池
     */
    ExecutorService mThreadPool;

    /**
     * 默认的线程个数
     */
    private final static int DEFAULT_THREAD_COUNT = 8;

    private int mThreadCount;

    /**
     * 默认的调度方式
     */
    private Type mType = Type.LIFO;
    private WishAdapter.WishHolder holder;

    private enum Type{
        FIFO,LIFO
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

    public WishImgLoader(int threadCount,Type type) {
        this.mType = type;
        this.mThreadCount = threadCount;
        init(threadCount,type);
    }

    /**
     * 默认
     * @return
     */
    public static WishImgLoader getInstance(){
        if(mInstance == null){

            synchronized (WishImgLoader.class){
                if(mInstance == null){
                    mInstance = new WishImgLoader(DEFAULT_THREAD_COUNT,Type.FIFO);
                    return mInstance;
                }
            }
        }
        return mInstance;
    }

    /**
     * 带参数
     * @param threadCount
     * @param type
     * @return
     */
    public static WishImgLoader getInstance(int threadCount,Type type){
        if(mInstance == null){
            synchronized (WishImgLoader.class){
                if(mInstance == null){
                    mInstance = new WishImgLoader(threadCount,type);
                    return mInstance;
                }
            }
        }
        return mInstance;
    }

    /**
     * 初始化
     * @param threadCount
     * @param type
     */
    public void init(int threadCount,Type type){

        mPoolThread = new Thread(new Runnable() {
            @Override
            public void run() {
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
        });

        mPoolThread.start();
        //任务队列
        mTaskQueue = new LinkedList<Runnable>();
        //调度方式
        mType = type;
        //线程池
        mThreadPool = Executors.newFixedThreadPool(threadCount);
        mSemaphoreThreadPool = new Semaphore(threadCount);

    }

    /**
     * 加载图片
     * @param context
     * @param wishImgList
     * @param holder
     */
    public void loadImge(
            final Context context,
            final List<WishsDTO.ResultBean.WishImgBean> wishImgList,
            final WishAdapter.WishHolder holder){

     addTask(new Runnable(){
         @Override
         public void run() {
             GridLayoutManager gridLayout = new GridLayoutManager(context, 4);
             gridLayout.setOrientation(OrientationHelper.VERTICAL);
             holder.itemImgGrid.setLayoutManager(gridLayout);
             holder.itemImgGrid.addItemDecoration(new RecyclerViewItemDecoration(
                     RecyclerViewItemDecoration.MODE_GRID,
                     Color.WHITE,10,5,0));
             holder.itemImgGrid.setAdapter(new WishItemAdapter(context,wishImgList));
             mSemaphoreThreadPool.release();
         }
     });

    }

    /**
     * 添加任务
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
     * 获取一个任务
     * @return
     */
    private Runnable getTask() {

        if(mType == Type.FIFO){
            return  mTaskQueue.removeFirst();
        }else if(mType == Type.LIFO){
            return  mTaskQueue.removeLast();
        }
        return null;
    }
}
