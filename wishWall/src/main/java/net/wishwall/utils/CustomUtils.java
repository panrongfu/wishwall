package net.wishwall.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author panRongFu on 2016/5/23.
 * @Description
 * @email pan@ipushan.com
 */
public class CustomUtils {

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取版本名称
     * @param ctx
     * @return
     */
    public static String getVersionName(Context ctx){
        try {
            PackageInfo pi = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(),0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取版本号
     * @param ctx
     * @return
     */
    public static int getVersionCode(Context ctx){
        try {
            PackageInfo pi = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(),0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取包名
     * @param ctx
     * @return
     */
    public static String getPackageName(Context ctx){
        try {
            PackageInfo pi = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(),0);
            return pi.packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取屏幕的宽
     * @param ctx
     * @return
     */
    public static int getScreenWidth(Context ctx){
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获取屏幕高度
     * @param ctx
     * @return
     */
     public static int getScreenHeight(Context ctx){
         WindowManager wm = (WindowManager)ctx.getSystemService(Context.WINDOW_SERVICE);
         DisplayMetrics outMetrics = new DisplayMetrics();
         wm.getDefaultDisplay().getMetrics(outMetrics);
         return outMetrics.heightPixels;
     }

    /**
     * 获取sd卡的路径
     * @return
     */
    public static String getSDCradPath(){
        File sdDir = null;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            sdDir =Environment.getExternalStorageDirectory();
            return sdDir.getPath();
        }
        return null;
    }

    /**
     * 保存文件到一级文件夹，并压缩
     * @param bm
     * @param path
     * @param fileName
     * @throws IOException
     */
    public static void saveBitmap2File(Bitmap bm, String path, String fileName) throws IOException {
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            File dirFile = new File(path);
            if(!dirFile.exists()){
                dirFile.mkdir();
            }
            File file = new File(dirFile.getAbsolutePath()+"/" + fileName);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bm.compress(Bitmap.CompressFormat.JPEG, 40, bos);
            bos.flush();
            bos.close();
            return;
        }

    }

    /**
     * 保存到多级文件夹
     * @param bm
     * @param path
     * @param fileName
     * @throws IOException
     */
    public static void saveBitmap2Files(Bitmap bm, String path,String fileName)throws IOException{
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            //一级目录
            File dirFile = new File(path);
            if(!dirFile.exists()){
                dirFile.mkdirs();
            }
            File file = new File(dirFile.getAbsolutePath()+"/" + fileName);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bm.compress(Bitmap.CompressFormat.JPEG, 40, bos);
            bos.flush();
            bos.close();
            return;
        }
    }

    /**
     * 把文件存入sd卡
     * @param content
     * @param path
     */
    public static void saveFile2SDcard(String content, String path,String fileName){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            try {
                File dir = new File(path);
                if(!dir.exists()){
                    dir.mkdirs();
                }
                File file = new File(dir.getAbsolutePath()+"/"+fileName);
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(content.getBytes());
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 删除指定目录下的所有文件
     * @param path
     */
    public static void deleteFile(Context ctx,String path){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            File folder = new File(path);
            File[] files = folder.listFiles();
            for(int i=0; i<files.length; i++){
                files[i].delete();
            }
            return;
        }

    }

    /**
     * 删除指定的文件
     * @param path
     * @param fileName
     */
    public static void deleteFileByName(Context ctx,String path,String fileName){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            File folder = new File(path);
            File[] files = folder.listFiles();
            for(int i=0; i<files.length; i++){
                String name = files[0].getName();
                if(name.equals(fileName)){
                    files[i].delete();
                }
            }
            return ;
        }
    }
}
