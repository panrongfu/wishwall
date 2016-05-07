package net.wishwall.utils;

import com.alibaba.sdk.android.oss.common.utils.DateUtil;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 
 *@Description:时间类型处理类
 *@author panRongFu pan@ipushan.com
 *@date 2015年9月23日 下午2:31:21
 */
public class DateTimeUtil {

	/**
	 * 格式类型： 年月日 时分
	 * @param ts
	 * @return
	 */
	public static String yyyyMMddHHmm(String ts){
		try {
			Long dateTime = DateUtil.parseIso8601Date(ts).getTime();
			Date d = new Date(dateTime);
			SimpleDateFormat  fm = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			return fm.format(d);
		}catch(ParseException p){
			p.printStackTrace();
		}
		return null;
	}
}
