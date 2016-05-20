package net.wishwall;
import android.content.Context;
import android.widget.Toast;

/**
 * @author panRongFu on 2016/4/28.
 * @Description
 * @email pan@ipushan.com
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

   // private static  CrashHandler crashHandler = new CrashHandler();
    private Context mContext;
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    public static  CrashHandler getInstance(){

        return  new CrashHandler();
    }

    public void init(Context ctx){
        mContext = ctx;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        Toast.makeText(mContext,ex.getMessage(),Toast.LENGTH_SHORT).show();
    }
}
