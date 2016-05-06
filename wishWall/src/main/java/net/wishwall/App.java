package net.wishwall;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.liulishuo.filedownloader.FileDownloader;

import net.wishwall.rong.RongCloudEvent;
import net.wishwall.rong.message.AgreedFriendRequestMessage;
import net.wishwall.rong.message.provider.ContactNotificationMessageProvider;
import net.wishwall.rong.message.provider.NewDiscussionConversationProvider;
import net.wishwall.rong.message.provider.RealTimeLocationMessageProvider;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.socialization.Socialization;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imlib.ipc.RongExceptionHandler;

/**
 * @author panRongFu on 2016/3/31.
 * @Description
 * @email pan@ipushan.com
 */
public class App extends Application {
    //服务地址
    public static final String uploadBucket = "wishwall";
    public static final String endpoint = "http://oss-cn-shenzhen.aliyuncs.com";
    public static final String baseUrl = "http://10.1.1.179:3000/";
    public static final String downloadUrl ="http://10.1.1.179:4000/download/wishWall.apk";
    public static final String ContactNtf = "RC:ContactNtf";
    public static final String TxtMsg = "RC:TxtMsg";
    public static final String  packageName = "net.wishwall";
    static  Context mContext;
    private App app = null;

    private static  RongIM.LocationProvider.LocationCallback mLastLocationCallback;

    @Override
    public void onCreate() {
        super.onCreate();

        ShareSDK.initSDK(this);
        ShareSDK.registerService(Socialization.class);
        FileDownloader.init(this);
        /**
         * 注意：
         * IMKit SDK调用第一步 初始化
         * context上下文
         * 只有两个进程需要初始化，主进程和 push 进程
         */
        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext())) ||
                "io.rong.push".equals(getCurProcessName(getApplicationContext()))) {
            RongIM.init(this);

            /**
             * 融云SDK事件监听处理
             *
             * 注册相关代码，只需要在主进程里做。
             */
            if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext()))) {

                RongCloudEvent.init(this);

                Thread.setDefaultUncaughtExceptionHandler(new RongExceptionHandler(this));

                try {
                    RongIM.registerMessageType(AgreedFriendRequestMessage.class);

                    RongIM.registerMessageTemplate(new ContactNotificationMessageProvider());
                    RongIM.registerMessageTemplate(new RealTimeLocationMessageProvider());
                    //@ 消息模板展示
                    RongContext.getInstance().registerConversationTemplate(new NewDiscussionConversationProvider());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        //获取Context
        mContext = getApplicationContext();
        //捕获异常
        CrashHandler.getInstance().init(mContext);
    }

    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    public static Context getContext(){

        return mContext;
    }

    public static  RongIM.LocationProvider.LocationCallback getLastLocationCallback() {
        return mLastLocationCallback;
    }

    public static  void setLastLocationCallback(RongIM.LocationProvider.LocationCallback lastLocationCallback) {
        mLastLocationCallback = lastLocationCallback;
    }
}
