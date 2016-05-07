package net.wishwall.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.Window;

/**
 * 
 *@Description: 其他activity的基类，作用隐藏标题栏
 *@author panRongFu pan@ipushan.com
 *@date 2015年9月16日 下午4:27:00
 */
public class BaseActivity extends FragmentActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//隐藏标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);	
		ActivityCollector.addActivity(this);
	}	
	public int getScreenWidth (){
		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);	
		return outMetrics.widthPixels;
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ActivityCollector.removeActivity(this);
	}
}
