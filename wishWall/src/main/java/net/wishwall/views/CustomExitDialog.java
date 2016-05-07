package net.wishwall.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import net.wishwall.R;


public class CustomExitDialog extends Dialog implements View.OnClickListener {

	/** 布局文件 **/
	int layoutRes;
	Context context;
	private Button confirmBtn;
	private Button cancelBtn;
	private TextView exit_dialog_title;
	private String title;
	public static final int TOAST_TIME = 1000;
	public OnDialogBtnClick onDialogBtnClick;
	private int mScreenWidth, mScreenHeight;// 屏幕的宽、高

	public CustomExitDialog(Context context) {
		super(context);
		this.context = context;
	}

	/**
	 * 自定义主题及布局的构造方法
	 * @param context
	 * @param theme
	 */
	public CustomExitDialog(Context context,String str, int theme) {
		super(context, theme);
		this.context = context;
		this.title = str;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 指定布局
		this.setContentView(R.layout.custom_exit_dialog);
		setWindowSize();
		
		// 根据id在布局中找到控件对象
		confirmBtn = (Button) findViewById(R.id.confirm_btn);
		cancelBtn = (Button) findViewById(R.id.cancel_btn);
		exit_dialog_title = (TextView)findViewById(R.id.exit_dialog_title);
		
		// 设置按钮的文本颜色
		confirmBtn.setTextColor(context.getResources().getColor(R.color.black));
		cancelBtn.setTextColor(context.getResources().getColor(R.color.black));
		exit_dialog_title.setText(title);

		// 为按钮绑定点击事件监听器
		confirmBtn.setOnClickListener(this);
		cancelBtn.setOnClickListener(this);
		
	}
	/** 
	 * 设置当前Activity在屏幕上显示的大小
	 */
	private void setWindowSize() {
		DisplayMetrics outMetrics = new DisplayMetrics();
		WindowManager windowManager = getWindow().getWindowManager();
		WindowManager.LayoutParams params = getWindow().getAttributes();
		windowManager.getDefaultDisplay().getMetrics(outMetrics);
		mScreenWidth = outMetrics.widthPixels;
		mScreenHeight = outMetrics.heightPixels;
		params.height = (int) (mScreenHeight * 0.3);
		params.width = (int) (mScreenWidth * 0.8);
		params.alpha = 1.0f;
		getWindow().setAttributes(params);
		getWindow().setGravity(Gravity.CENTER);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.confirm_btn:			
			onDialogBtnClick.loginOut();
			break;
		case R.id.cancel_btn:			
			onDialogBtnClick.cancel();
			break;
		default:
			break;
		}
	}
	public void setOnDialogBtnClick(OnDialogBtnClick s) {
		onDialogBtnClick = s;
	}
	
	public interface OnDialogBtnClick{
		public void loginOut();
		public void cancel();
	}
}