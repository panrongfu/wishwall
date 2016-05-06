package net.wishwall.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;

/**
 * 
 *@Description:偏好存储工具类
 *@author panRongFu pan@ipushan.com
 *@date 2015年9月16日 下午6:48:05
 */
public class SpUtil {

	private Context mContext;
	private String mFileName;
	private String DATA_URL = "/data/data/";	

	public SpUtil(Context context, String fileName) {
		mContext = context;
		this.mFileName = fileName;
	}	
	/**
	 * 创建SharedPreferences
	 * @param key
	 * @param value 把数据以键值对形式存放在 mFileName.xml 文件中
	 */
	public void setKeyValue(String key, String value){
		
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(mFileName, Context.MODE_PRIVATE);//第二个参数值为0，作用是只用本应用可以读取
        SharedPreferences.Editor editor = sharedPreferences.edit();//获得Editor
        editor.putString(key, value);//存入值
        editor.commit();//编辑完成后提交	
	}
	/**
	 * 获取SharedPreferences文件的
	 * @param key
	 * @return String 根据key返回相应的值，如果没有找到默认为空值
	 */
	
	public  String getKeyValue(String key){		
	     SharedPreferences sharedPreferences = mContext.getSharedPreferences(mFileName, Context.MODE_PRIVATE);
	     String value = sharedPreferences.getString(key, "");//第二个参数为默认值,找不到则返回""
	     return value;
	}
	
	/**
	 * 获取SharedPreferences文件的
	 * @param key
	 * @return String 根据key返回相应的值，如果没有找到默认为空值
	 */
	public  void putBooleanCode(String key, boolean value){
	     SharedPreferences sharedPreferences = mContext.getSharedPreferences(mFileName, Context.MODE_PRIVATE);
	     SharedPreferences.Editor editor = sharedPreferences.edit();//获得Editor
	     editor.putBoolean(key, value);//存入值
	     editor.commit();//编辑完成后提交	
	}
	/**
	 * 获取SharedPreferences文件的
	 * @param key
	 * @return String 根据key返回相应的值，如果没有找到默认为false
	 */
	
	public  Boolean getBooleanCode(String key){		
	     SharedPreferences sharedPreferences = mContext.getSharedPreferences(mFileName, Context.MODE_PRIVATE);
	     Boolean value = sharedPreferences.getBoolean(key,false);//第二个参数为默认值,找不到则返回false
	     return value;
	}
	/**
	 * 删除SharedPreferences文件
	 * @param packageName 应用的包名
	 * @return boolean
	 */
	public boolean delete_xml(String packageName){
		
		File file = new File(DATA_URL+ packageName+ "/shared_prefs", mFileName+".xml");
		if (file.exists()) {
			file.delete();
			return true;
		}
		return false;
	}	
}