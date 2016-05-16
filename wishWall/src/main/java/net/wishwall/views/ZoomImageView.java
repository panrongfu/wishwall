package net.wishwall.views;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

/**
 * @author panRongFu on 2016/5/16.
 * @Description
 * @email pan@ipushan.com
 */
public class ZoomImageView extends ImageView
        implements ViewTreeObserver.OnGlobalLayoutListener,
        ScaleGestureDetector.OnScaleGestureListener,View.OnTouchListener{

    private boolean mOnce =false;

    /**
     * 初始化时缩放的值
     */
    private float mInitScale;
    /**
     * 放大的最大值
     */
    private float mMaxScale;
    /**
     * 双击放到最大的值
     */
    private float mMidScale;

    private Matrix mScaleMatrix;
    /**
     * 捕获用户多指触控缩放比例
     */
    private ScaleGestureDetector mScaleGestureDetector;

    //------------自由移动------------

    /**
     * 记录上次多点触控的数量
     */
    private int mLastPointerCount;

    private float mLastX;
    private float mLastY;

    private float mTouchSlop;
    private boolean isCanDrag;

    private boolean isCheckLeftAndRight;
    private boolean isCheckTopAndBottom;

    //---------------双击放大/缩小--------------

    private GestureDetector mGestureDetector;
    private boolean isAutoScale;

    /**
     * 自动放大缩小
     */
    private class AutoScaleRunnable implements Runnable{
        /**
         * 缩放的目标值
         */
        private float mTargetScale;
        //缩放的中心点
        private float x;
        private float y;

        private final float BIGGER = 1.05f;
        private final float SMALL = 0.93F;

        private float tempScale;

        public AutoScaleRunnable(float mTargetScale, float x, float y) {
            this.mTargetScale = mTargetScale;
            this.x = x;
            this.y = y;
            if(getScale() < mTargetScale){
                tempScale = BIGGER;
            }
            if(getScale() > mTargetScale){
                tempScale = SMALL;
            }
        }
        @Override
        public void run() {
            isAutoScale= true;
            //进行缩放
            mScaleMatrix.postScale(tempScale,tempScale,x,y);
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);
            float currentScale = getScale();
            if((tempScale>1.0f && currentScale<mTargetScale)
                    ||(tempScale<1.0f && currentScale<mTargetScale)){
                postDelayed(this,16);
            }else {
                float scale = mTargetScale/currentScale;
                mScaleMatrix.postScale(scale,scale,x,y);
                checkBorderAndCenterWhenScale();
                setImageMatrix(mScaleMatrix);
                isAutoScale = false;
            }
        }
    };

    public ZoomImageView(Context context) {
        this(context,null);

    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ZoomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setScaleType(ScaleType.MATRIX);
        //init
        mScaleMatrix = new Matrix();
        mScaleGestureDetector = new ScaleGestureDetector(context,this);
        setOnTouchListener(this);
        mGestureDetector = new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if(isAutoScale) return true;
                float x = e.getX();
                float y = e.getY();
                if(getScale() <mMidScale){
//                    mScaleMatrix.postScale(mMidScale/getScale(),mMidScale/getScale(),x,y);
//                    setImageMatrix(mScaleMatrix);
                    postDelayed(new AutoScaleRunnable(mMidScale,x,y),16);
                    isAutoScale = true;
                }else{
//                    mScaleMatrix.postScale(mInitScale/getScale(),mMidScale/getScale(),x,y);
//                    setImageMatrix(mScaleMatrix);
                    postDelayed(new AutoScaleRunnable(mInitScale,x,y),16);
                    isAutoScale = true;
                }
                return true;
            }
        });
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }

    /**
     * 获取ImageView 加载完成时的大小
     */
    @Override
    public void onGlobalLayout() {
        if(!mOnce){
            //得到控件的宽和高
            int width = getWidth();
            int height = getHeight();

            //得到我们的图片，已经宽和高
            Drawable d = getDrawable();
            if(d == null) return;

            int dw = d.getIntrinsicWidth();
            int dh = d.getIntrinsicHeight();

            float scale = 1.0f;
            //图片的高大于控件的高，图片的宽小于控件的宽
            if(dw > width && dh < height){
                scale = width*1.0f/dw;
            }
            //图片的宽大于控件的 ,图片的高小于控件的高
            if(dh > height&& dw < width){
                scale = height*1.0f/dh;
            }
            //图片的宽高都小于控件的宽高-->缩小
            if(dw > width && dh > height){
                scale=Math.min(width*1.0f/dw,height*1.0f/dh);
            }
            //图片的宽高都大于控件的宽高-->放大
            if(dw < width && dh < height){
                scale = Math.min(width*1.0f/dw,height*1.0f/dh);
            }
            /**
             * 得到初始化的缩放值
             */
            mInitScale = scale;
            mMaxScale = mInitScale*4;
            mMidScale = mInitScale*2;

            //将图片移动到当前控件的中心
            int dx = getWidth()/2-dw/2;
            int dy = getHeight()/2-dh/2;

            mScaleMatrix.postTranslate(dx,dy);
            mScaleMatrix.postScale(mInitScale,mInitScale,width/2,height/2);
            setImageMatrix(mScaleMatrix);
            mOnce = true;
        }
    }

    /**
     * 获取图片当前的缩放值
     * @return
     */
    public float getScale(){
        float[] values = new float[9];
        mScaleMatrix.getValues(values);
        return values[Matrix.MSCALE_X];
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        //缩放的比例initScale - maxScale
        float scale = getScale();
        float scaleFactor = detector.getScaleFactor();
        if(getDrawable() == null) return true;
        Log.e("onScale: ",scale+">>>>>>>>");
        //缩放范围控制
        if((scale < mMaxScale && scaleFactor>1.0f)
                ||(scale >mInitScale && scaleFactor <1.0f)){

               if(scale*scaleFactor < mInitScale){
                   scaleFactor = mInitScale/scale;
               }

               if(scale*scaleFactor > mMaxScale){
                   scaleFactor = mMaxScale/scale;
               }
            /**
             * 多指触摸的中心
             */
            mScaleMatrix.postScale(scaleFactor,scaleFactor,detector.getFocusX(),detector.getFocusX());
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);
        }
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(mGestureDetector.onTouchEvent(event)) return true;
        mScaleGestureDetector.onTouchEvent(event);

        //多点触控的中心
        float x = 0;
        float y = 0;

        //拿到多点触控的数量
        int pointerCount = event.getPointerCount();
        for(int i=0;i<pointerCount;i++){
            x += event.getX(i);
            y += event.getY(i);
        }

        x /= pointerCount;
        y /= pointerCount;

        if(mLastPointerCount != pointerCount){
            isCanDrag = false;
            mLastX = x;
            mLastY = y;
        }

        mLastPointerCount = pointerCount;
        RectF rectF = getMatrixRectF();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(rectF.width()>getWidth()+0.01||rectF.height()>getHeight()+0.01){
                    //请求父控件不拦截
                    if(getParent()instanceof ViewPager)
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(rectF.width()>getWidth()+0.01||rectF.height()>getHeight()+0.01){
                    if(getParent()instanceof ViewPager)
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                float dx = x - mLastX;
                float dy = y - mLastY;
                if(!isCanDrag){
                    isCanDrag = isMoveAction(dx,dy);
                }
                if(isCanDrag){
                    if(getDrawable() !=null){

                        isCheckLeftAndRight = isCheckTopAndBottom =true;
                        //如果图片宽度小于控件宽度不允许横向移动
                        if(rectF.width() < getWidth()){
                            isCheckLeftAndRight = false;
                            dx = 0;
                        }
                        //如果图片高度小于控件高度不允许纵向移动
                        if(rectF.height() < getHeight()){
                            isCheckTopAndBottom =false;
                            dy=0;
                        }

                        mScaleMatrix.postTranslate(dx,dy);
                        checkBorderAndCenterWhenTranslate();
                        setImageMatrix(mScaleMatrix);
                    }
                }
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mLastPointerCount = 0;
                break;

        }
        return true;
    }

    /**
     * 在缩放的时候进行边界控制以及位置的控制
     */
    private void checkBorderAndCenterWhenScale() {
        RectF rectF = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();


        if(rectF.width() >= width){
            //屏幕左边有空隙
            if(rectF.left>0){
                deltaX = -rectF.left;
            }

            if(rectF.right <width){
                deltaX = width - rectF.right;
            }
        }

        if(rectF.height() >= height){
            if(rectF.top > 0){
                deltaY = -rectF.top;
            }

            if(rectF.bottom <height){
                deltaY = height-rectF.bottom;
            }
        }
        //如果图片宽高小于控件宽高让其居中
        if(rectF.width()<width){
            deltaX = width/2 -rectF.right+rectF.width()/2;
        }
        if(rectF.height()<height){
            deltaY = height/2 -rectF.bottom+rectF.height()/2;
        }
        mScaleMatrix.postTranslate(deltaX,deltaY);
    }

    /**
     * 在移动时候进行边界检测
     */
    private void checkBorderAndCenterWhenTranslate() {

        RectF rectF = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();
        //顶部有白边
        if(rectF.top > 0 && isCheckTopAndBottom){
            deltaY = -rectF.top;
        }
        //底部有白边
        if(rectF.bottom <height && isCheckTopAndBottom){
            deltaY = height -rectF.bottom;
        }
        //左边有白边
        if(rectF.left > 0 && isCheckLeftAndRight){
            deltaX = -rectF.left;
        }
        //右边有白边
        if(rectF.right < width && isCheckLeftAndRight){
            deltaX = width-rectF.right;
        }

        mScaleMatrix.postTranslate(deltaX,deltaY);
    }
    /**
     * 获得图片放大缩小以后的宽和高
     * @return
     */
    private RectF getMatrixRectF(){
        Matrix matrix = mScaleMatrix;
        RectF rectF = new RectF();
        Drawable d = getDrawable();
        if(d !=null){
            rectF.set(0,0,d.getIntrinsicWidth(),d.getIntrinsicHeight());
            matrix.mapRect(rectF);
        }
        return rectF;
    }

    /**
     * 判断是否是move
     * @param dx
     * @param dy
     * @return
     */
    private boolean isMoveAction(float dx, float dy) {

        return Math.sqrt(dx*dx+dy*dy)>mTouchSlop;
    }
}
