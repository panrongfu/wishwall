package org.ecfex.app.teamwork.activities;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 *@Description:activity管理器
 *@author panRongFu pan@ipushan.com
 *@date 2015年10月27日 下午3:16:25
 */
public class ActivityCollector {
	
	public static List<Activity> activities = new ArrayList<>();
	/**
	 * 添加活动activity
	 * @param activity
	 */
	public static void addActivity(Activity activity){
		activities.add(activity);
	}
	/**
	 * 删除指定活动activity
	 * @param activity
	 */
	public static void removeActivity(Activity activity){
		activities.remove(activity);
	}
	/**
	 * 删除全部已经添加的活动activities
	 */
	public static void finishAllActivities(){
		for(Activity activity:activities){
			if(!activity.isFinishing()){
				activity.finish();
			}
		}
	}
}
