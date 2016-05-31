package net.wishwall;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Looper;
import android.os.Process;
import android.view.WindowManager;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;

import net.wishwall.aliyunoss.ResuambleUploadSamples;
import net.wishwall.domain.UploadTokenDTO;
import net.wishwall.service.ApiClient;
import net.wishwall.utils.CustomUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Response;

/**
 * @author panRongFu on 2016/4/28.
 * @Description
 * @email pan@ipushan.com
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

   // private static  CrashHandler crashHandler = new CrashHandler();
    private Context mContext;
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private String accessKeyId;
    private String accessKeySecret;
    private String securityToken;
    private OSSClient oss;
    private static CrashHandler instance;

    //保证只有一个CrashHandler实例
    private CrashHandler() {

    }

    //获取CrashHandler实例 ,单例模式
    public static CrashHandler getInstance() {
        if (instance == null){
            synchronized (CrashHandler.class){
                if(instance == null){
                    instance = new CrashHandler();
                    return  instance;
                }
            }
        }
        return instance;
    }


    public void init(Context ctx){
        mContext = ctx;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果自定义的没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        }
    }

    private boolean handleException(final Throwable ex) {
        if (ex == null || mContext == null) {
            return false;
        }
        Thread thread = new Thread(){
            @Override
            public void run() {
             Looper.prepare();
             createDialog(ex);
             Looper.loop();
            }
        };
        thread.start();
        return true;
    }

    /**
     * 创建对话框
     */
    private void createDialog(final Throwable ex) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(Constants.ErrorReport)
                .setMessage(R.string.app_error_message)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String savePath = CustomUtils.getSDCradPath()+"/wishwall/crash/";
                        String carchReport = getCrashReport(mContext,ex);
                        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
                        String time = dateFormat.format(new Date());
                        String fileName = time+".txt";
                        CustomUtils.saveFile2SDcard(carchReport,savePath,fileName);
                        String uploadPath = savePath+fileName;
                        uploadFile2Server(uploadPath,fileName);
                    }
                });
        AlertDialog dialog = builder.create();
        //需要的窗口句柄方式，没有这句会报错的
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }

    /**
     * 上传文件到服务器
     */
    private void uploadFile2Server(String savePath, String fileName) {
//        final File reportFile = new File(savePath,fileName);
        // create RequestBody instance from file
//        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), reportFile);
//        MultipartBody.Part body = MultipartBody.Part.createFormData("text",reportFile.getName(),requestFile);
//        RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"), "crashReport");
//
//        ApiClient.uploadFile(description, body, new Callback<ResultDTO>() {
//            @Override
//            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
//                ResultDTO body = response.body();
//                if(body.getCode() == 200){
//                    dialog.dismiss();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResultDTO> call, Throwable t) {
//                dialog.dismiss();
//            }
//        });
        preUpLoad(savePath, fileName);

    }
    /**
     * 上传文件准备工作
     * @param
     */
    private void preUpLoad(final String savePath,final String fileName) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Response<UploadTokenDTO> res = ApiClient.synchGetUploadToken();
                UploadTokenDTO body = res.body();
                if(body.getCode() == 200){
                    accessKeyId = body.getResult().getAccessKeyId();
                    accessKeySecret = body.getResult().getAccessKeySecret();
                    securityToken = body.getResult().getSecurityToken();
                    startUpload(savePath,fileName);
                }
            }
        }).start();
    }

    /**
     * 开始上传
     */
    private void startUpload(final String savePath,final String fileName) {
        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(accessKeyId, accessKeySecret,securityToken);

        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        OSSLog.enableLog();
        oss = new OSSClient(mContext, Constants.endpoint, credentialProvider, conf);
        oss.updateCredentialProvider(new OSSStsTokenCredentialProvider(accessKeyId, accessKeySecret, securityToken));

        new Thread(new Runnable() {
            @Override
            public void run() {
                ResuambleUploadSamples resuamUpLoad = new ResuambleUploadSamples(oss,Constants.uploadBucket,"crash/"+fileName,savePath);
                resuamUpLoad.setUpLoadFinishListener(new ResuambleUploadSamples.UpLoadFinishListener() {
                    @Override
                    public void finish(String path) {
                        Process.killProcess(Process.myPid());
                        System.exit(1);
                    }

                    @Override
                    public void fail() {
                        Process.killProcess(Process.myPid());
                        System.exit(1);
                    }
                });
                resuamUpLoad.resumableUpload();
            }
        }).start();
    }

    /**
     * 获取异常报告
     * @param ctx
     * @param ex
     * @return
     */
    private String getCrashReport(Context ctx,Throwable ex){
        String packageName = CustomUtils.getPackageName(ctx);
        StringBuffer sb = new StringBuffer();
        sb.append("Exception"+ex.getMessage());
        StackTraceElement[] elements = ex.getStackTrace();
        for(int i = 0; i<elements.length; i++){
            sb.append(elements[i].toString()+"\n");
        }
        return sb.toString();
    }
}
