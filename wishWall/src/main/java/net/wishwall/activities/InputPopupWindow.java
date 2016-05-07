package net.wishwall.activities;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import net.wishwall.R;

/**
 * Created by Administrator on 2016/5/7.
 */
public class InputPopupWindow extends PopupWindow {

    private int mWidth;
    private int mHeight;
    private View mContentView;
    private EditText mEditText;
    private TextView mTextView;
    OnCommSendListener mListener;

    public InputPopupWindow(Context context) {
        super(context);
        calWidthHeight(context);
        mContentView = LayoutInflater.from(context).inflate(R.layout.input_popup_window,null);
        setContentView(mContentView);
        setHeight(mHeight);
        setWidth(mWidth);
        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        ColorDrawable dw = new ColorDrawable(0xffffffff);
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
        initViewUI(context);

    }

    private void initViewUI(Context context) {
        mEditText = (EditText) mContentView.findViewById(R.id.wish_comm_input);
        mTextView = (TextView) mContentView.findViewById(R.id.wish_comm_send);
        mEditText.setFocusable(true);
        mEditText.setFocusableInTouchMode(true);
        mEditText.requestFocus();
        /**
         * 监听输入内容若不为空 则可以点击发送否则不可以
         */
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(TextUtils.isEmpty(s.toString())){
                    mTextView.setBackgroundResource(R.drawable.round_shape);
                    mTextView.setEnabled(false);
                }
                mTextView.setEnabled(true);
                mTextView.setBackgroundResource(R.drawable.round_blue_shape);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(TextUtils.isEmpty(s.toString())){
                    mTextView.setBackgroundResource(R.drawable.round_shape);
                    mTextView.setEnabled(false);
                }
                mTextView.setEnabled(true);
                mTextView.setBackgroundResource(R.drawable.round_blue_shape);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(TextUtils.isEmpty(s.toString())){
                    mTextView.setBackgroundResource(R.drawable.round_shape);
                    mTextView.setEnabled(false);
                }
                mTextView.setEnabled(true);
                mTextView.setBackgroundResource(R.drawable.round_blue_shape);
            }
        });
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.send(mEditText.getText().toString());
                }
            }
        });
    }


    /**
     * 计算宽高
     * @param context
     */
    private void calWidthHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mWidth = outMetrics.widthPixels;
        mHeight = (int) (outMetrics.heightPixels * 0.1);
    }
    public void setOnCommSendListener(OnCommSendListener listener){
        this.mListener = listener;
    }

    public interface OnCommSendListener{
        void send(String text);
    }
}
