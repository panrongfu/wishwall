package net.wishwall.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

import net.wishwall.R;

/**
 * @author panRongFu on 2016/5/6.
 * @Description
 * @email pan@ipushan.com
 */
public class RoundImageView extends ImageView {
    /**
     * 图片类型，圆形或者圆角
     */
    private int type;
    private static final int TYPE_CIRCLE = 0;
    private static final int TYPE_ROUND = 1;

    /**
     * 圆角的默认值
     */
    private static final int BODER_RADIUS_DEFAULT = 10;
    /**
     * 圆角的大小
     */
    private int mBorderRadius;
    /**
     * 绘图的笔
     */
    private Paint mBitmapPaint;
    /**
     * 圆的半径
     */
    private int mRadius;
    /**
     * 矩阵，主要用于放大缩小
     */
    private Matrix mMatrix;
    /**
     * 渲染图像，使用图像为绘制图形着色
     */
    private BitmapShader mBitmapShader;
    /**
     * view的宽度
     */
    private int mWidth;
    private RectF mRoundRect;

    public RoundImageView(Context context) {
        super(context);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mMatrix = new Matrix();
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView);
        mBorderRadius = array.getDimensionPixelSize(
                R.styleable.RoundImageView_borderRadius,(int)TypedValue
                        .applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                BODER_RADIUS_DEFAULT,getResources().getDisplayMetrics())
        );
        type = array.getInt(R.styleable.RoundImageView_type,TYPE_CIRCLE);//默认为圆角
        array.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        /**
         * 若类型是圆形，则强制改变view的宽高使他们一致，以小值为准
         */
        if(type == TYPE_CIRCLE){
            mWidth = Math.min(getMaxWidth(),getMaxHeight());
            mRadius = mWidth/2;
            setMeasuredDimension(mWidth,mWidth);
        }
    }

    /**
     * 初始化Bitmapshader
     */
    private void initShader(){
        Drawable drawable = getDrawable();
        if(drawable == null) return;

        Bitmap bmp = drawableToBitmap(drawable);
        mBitmapShader = new BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        float scale =1.0f;
        if(type == TYPE_CIRCLE){
            int bitmapSize = Math.min(bmp.getWidth(),bmp.getHeight());
            scale = mWidth * 1.0f/bitmapSize;
        }else if(type == TYPE_ROUND){

            if(!(bmp.getWidth() == getWidth() && bmp.getHeight() == getHeight())){
                scale = Math.max(getWidth()*1.0f/bmp.getWidth(),
                        getHeight()*1.0f/bmp.getHeight());

            }
        }
        mMatrix.setScale(scale,scale);
        mBitmapShader.setLocalMatrix(mMatrix);
        mBitmapPaint.setShader(mBitmapShader);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(getDrawable() == null) return;
        initShader();
        if(type == TYPE_ROUND){
            canvas.drawRoundRect(mRoundRect,mBorderRadius,mBorderRadius,mBitmapPaint);
        }else{
            canvas.drawCircle(mRadius,mRadius,mRadius,mBitmapPaint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //圆角的图片范围
        if(type == TYPE_ROUND)
            mRoundRect = new RectF(0,0,w,h);
    }

    /**
     * 把drawable转成bitmap
     * @param drawable
     * @return
     */
    private Bitmap drawableToBitmap(Drawable drawable) {
        if(drawable instanceof BitmapDrawable){
            BitmapDrawable bd = (BitmapDrawable)drawable;
            return bd.getBitmap();
        }
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0,0,width,height);
        drawable.draw(canvas);
        return bitmap;

    }
}
