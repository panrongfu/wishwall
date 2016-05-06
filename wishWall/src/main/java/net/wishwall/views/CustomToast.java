package net.wishwall.views;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import net.wishwall.R;


public class CustomToast extends Toast {
	
	private static View mView;
	private static CustomToast customToast = null;


	public CustomToast(Context context) {
		super(context);		
		LayoutInflater inflater = LayoutInflater.from(context);
		this.mView = inflater.inflate(R.layout.custom_toast,null);
	}	
	public static CustomToast createToast(Context context) {
		
		customToast = new CustomToast(context);		
		customToast.setView(mView);
		customToast.setDuration(Toast.LENGTH_SHORT);
		customToast.setGravity(Gravity.BOTTOM, 0, 175);
		return customToast;
	}
	
	public CustomToast setMessage(String strMessage) {
		TextView tvMsg = (TextView) mView.findViewById(R.id.toast_tv);
		if (tvMsg != null) {
			tvMsg.setText(strMessage);
		}
		return customToast;
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
