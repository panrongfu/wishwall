package org.ecfex.app.teamwork.views;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.ecfex.app.teamwork.R;


/**
 * 自定义toast提示框
 */
public class CustomToast extends Toast {
	
	private static View mView;
	private static CustomToast customToast = null;

	public CustomToast(Context context) {
		super(context);		
		LayoutInflater inflater = LayoutInflater.from(context);
		this.mView = inflater.inflate(R.layout.custom_toast,null);
	}	

	public static void showMsg (Context context,String msg){
		init(context);
		TextView tvMsg = (TextView) mView.findViewById(R.id.toast_tv);
		if (tvMsg != null) {
			tvMsg.setText(msg);
		}
		customToast.show();
	}

	private static void init(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		mView = inflater.inflate(R.layout.custom_toast,null);
		customToast = new CustomToast(context);
		customToast.setView(mView);
		customToast.setDuration(Toast.LENGTH_SHORT);
		customToast.setGravity(Gravity.BOTTOM, 0, 175);
	}
}
