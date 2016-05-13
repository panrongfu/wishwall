package net.wishwall.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * @author panRongFu on 2016/5/12.
 * @Description
 * @email pan@ipushan.com
 */
public class ScreenWidthHeight {

    /**
     * 获取屏幕的宽
     * @return
     */
    public static int getWidth(Context ctx){
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获取屏幕的高
     * @return
     */
    public static int getHeight(Context ctx){
        WindowManager wm = (WindowManager)ctx.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

}
