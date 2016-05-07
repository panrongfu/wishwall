package net.wishwall.activities;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import net.wishwall.R;

/**
 * @author panRongFu on 2016/4/26.
 * @Description
 * @email pan@ipushan.com
 */
public class ImageSelectPopupWindow extends PopupWindow implements View.OnClickListener{

    private int mWidth;
    private int mHeight;
    private View mContentView;
    private TextView fromPhotos;
    private TextView fromCamera;
    private TextView cancel;
    private OnItemClickListener mListener;

    public ImageSelectPopupWindow(Context context) {
        super(context);
        calWidthHeight(context);
        mContentView = LayoutInflater.from(context).inflate(R.layout.image_select_popup_window,null);
        setContentView(mContentView);
        setWidth(mWidth);
        setHeight(mHeight);
        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
      //  ColorDrawable dw = new ColorDrawable(0xffffffff);
        setBackgroundDrawable(new BitmapDrawable());
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_OUTSIDE ){
                    dismiss();
                    return true;
                }
                return false;
            }
        });
        initViewUI(mContentView);
    }

    /**
     * 初始化
     */
    private void initViewUI(View view) {
        fromPhotos = (TextView) view.findViewById(R.id.from_photos);
        fromCamera = (TextView) view.findViewById(R.id.from_camera);
        cancel = (TextView) view.findViewById(R.id.cancel);
        fromPhotos.setOnClickListener(this);
        fromCamera.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    /**
     * 计算宽高
     * @param context
     */
    private void calWidthHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mWidth = (int)(outMetrics.widthPixels*0.95);
        mHeight = (int) (outMetrics.heightPixels * 0.3);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.from_photos:
                mListener.fromPhoto();
                break;
            case R.id.from_camera:
                mListener.fromcamera();
            case R.id.cancel:
                mListener.cancel();
        }
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }
    public interface  OnItemClickListener{
        void fromPhoto();
        void fromcamera();
        void cancel();
    }
}
