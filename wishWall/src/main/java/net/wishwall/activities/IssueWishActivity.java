package net.wishwall.activities;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;

import net.wishwall.App;
import net.wishwall.Constants;
import net.wishwall.R;
import net.wishwall.adapter.IssueWishAdpter;
import net.wishwall.aliyunoss.ResuambleUploadSamples;
import net.wishwall.domain.ResultDTO;
import net.wishwall.domain.UploadTokenDTO;
import net.wishwall.service.ApiClient;
import net.wishwall.utils.ImageTools;
import net.wishwall.utils.SaveBitmap2File;
import net.wishwall.utils.SpUtil;
import net.wishwall.views.CustomProgressDialog;
import net.wishwall.views.CustomToast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author panRongFu on 2016/4/26.
 * @Description
 * @email pan@ipushan.com
 */
public class IssueWishActivity extends BaseActivity
        implements View.OnClickListener,IssueWishAdpter.IssueOnItemClickListener {

    private String accessKeyId;
    private String accessKeySecret;
    private String securityToken;

    private TextView sendWish;
    private TextView back;
    private EditText wishContent;
    private RecyclerView mRecyclerView;
    private GridLayoutManager gridLayout;
    private ImageSelectPopupWindow popupwindow;
    private static final int CAMERA = 2;
    private static final int CROP_PICTURE = 3;
    private LinearLayout mParent;
    private IssueWishAdpter adapter;
    private CustomProgressDialog progressDialog;
    private static List<String> mImagePath = new ArrayList<String>();
    private static final int PHOTO = 4;
    private OSS oss;
    private SpUtil userSpUtil;
    private static String wishId;
    private static int uploadIndex = 0;
    private static final int CREATE = 0X21;//创建许愿条完成
    private static final int LOADING = 0X01;//图片上传中
    private static final int FINISH = 0X11;//图片上传完成
    public  static IssueWishActivity wishActivity;
    private static OnIssueFinishListener mListener;
    private List<String> ossImagePath = new ArrayList<String>();
    private  Handler uploadHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == LOADING){
                Log.e("handleMessage", "handleMessage: "+msg.what+"####"+uploadIndex);
                startUpload();
            }else if(msg.what == FINISH){
                Log.e("handleMessage", "handleMessage: "+msg.what);
                addWishImage();
            }
        }
    };
    private SpUtil tempSpUtil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.issue_wish);
        wishActivity = this;
        initViewUI();
    }

    private void initViewUI() {
        mImagePath.clear();
        tempSpUtil = new SpUtil(this,"tempIssue");
        userSpUtil = new SpUtil(this, Constants.USER_SPUTIL);
        progressDialog = CustomProgressDialog.createDialog(this);
        sendWish = (TextView)findViewById(R.id.wish_send);
        back = (TextView)findViewById(R.id.wish_back);
        wishContent = (EditText)findViewById(R.id.wish_content);
        mRecyclerView = (RecyclerView)findViewById(R.id.wish_pic_list);
        mParent = (LinearLayout)findViewById(R.id.pareent_view);
        gridLayout = new GridLayoutManager(getApplicationContext(),4);
        mRecyclerView.setLayoutManager(gridLayout);
        adapter =new IssueWishAdpter(this,mImagePath);
        adapter.setIssueOnItemClickListener(this);
        mRecyclerView.setAdapter(adapter);
        sendWish.setOnClickListener(this);
        back.setOnClickListener(this);

        initPopupWindow();
    }

    private void initPopupWindow() {
        popupwindow = new ImageSelectPopupWindow(this);
        popupwindow.setOnItemClickListener(new ImageSelectPopupWindow.OnItemClickListener() {
            int REQUEST_CODE;
            @Override
            public void fromPhoto() {
              Intent intent =new Intent(IssueWishActivity.this,OptionImgActivity.class);
              startActivityForResult(intent,PHOTO);
              popupwindow.dismiss();
            }

            @Override
            public void fromcamera() {
                Uri imageUri = null;
                String fileName = null;
                Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
               // REQUEST_CODE = CAMERA;

                // 删除上一次截图的临时文件
                String tempName = tempSpUtil.getKeyValue("tempName");
                String path =Environment.getExternalStorageDirectory().getAbsolutePath();
                ImageTools.deletePhotoAtPathAndName(path,tempName);

                // 保存本次截图临时文件名字
                fileName = String.valueOf(System.currentTimeMillis()) + ".jpg";
                tempSpUtil.setKeyValue("tempName",fileName);

                imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), fileName));
                // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(openCameraIntent, CAMERA);
                popupwindow.dismiss();
            }

            @Override
            public void cancel() {
                popupwindow.dismiss();
            }
        });

        popupwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                lightOn();
            }
        });
    }

    /**
     * 内容区域变亮
     */
    protected void lightOn() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 1.0f;
        getWindow().setAttributes(lp);

    }
    /**
     * 内容区域变暗
     */
    protected void lightOff() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.3f;
        getWindow().setAttributes(lp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.wish_back:
                this.finish();
                break;
            case R.id.wish_send:
                createWish();
                break;
        }
    }

    /**
     * 创建许愿条
     */
    private void createWish() {
        String userId = userSpUtil.getKeyValue("userId");
        String cityName = MainActivity.cityName;
        String content = wishContent.getText().toString();
        if(TextUtils.isEmpty(content) || content.length()>=200){
            CustomToast.showMsg(this,"内容字数不限在1 ~200以内");
            return;
        }
        progressDialog.setMessage("发送中...").show();
        ApiClient.createWish(userId, content, cityName, new Callback<ResultDTO>() {
            @Override
            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                ResultDTO body = response.body();
                if(body.getCode() == 200){
                    wishId = body.getResult();
                    preUpLoad();
                }
            }
            @Override
            public void onFailure(Call<ResultDTO> call, Throwable t) {

            }
        });
    }

    /**
     * 为许愿条添加图片
     */
    private void addWishImage() {
        StringBuffer sb = new StringBuffer();
        for(int i=0; i<ossImagePath.size(); i++){
            sb.append(ossImagePath.get(i));
            if(i != ossImagePath.size() -1){
                sb.append(",");
            }
        }
        ApiClient.addWishImage(wishId,sb.toString(), new Callback<ResultDTO>() {
            @Override
            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                ResultDTO body = response.body();
                if(body.getCode() == 200){
                    CustomToast.showMsg(IssueWishActivity.this,"发送成功");
                    progressDialog.dismiss();
                    if(mListener !=null){
                        mListener.finish();
                    }
                    IssueWishActivity.this.finish();
                }else {
                    CustomToast.showMsg(IssueWishActivity.this,"发送失败");
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResultDTO> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void issueOnClickListener(View view, int position) {
        if(position == mImagePath.size()){
            popupwindow.setAnimationStyle(R.style.popupwindow);
            popupwindow.showAtLocation(mParent, Gravity.BOTTOM,0,20);
            lightOff();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case PHOTO:
                    String path = data.getStringExtra("path");
                    Bitmap bitmap = decodeSampledBitmapFromPath(path);
                    savaImage(bitmap);
                    //adapter = new IssueWishAdpter(this,mImagePath);
                   // mRecyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    break;
                case CAMERA:
                    Uri uri = data.getData();
                   // Bitmap cameraBitmap;
                    //cameraBitmap = BitmapFactory.decodeFile(uri.getPath());
                    Bitmap bmp = decodeSampledBitmapFromPath(uri.getPath());
                    savaImage(bmp);
                    break;
            }
        }
    }

    /**
     * 保存图片
     * @param data
     */
    private void savaImage(Bitmap data) {
        if (data != null) {
            Bitmap bitmap = data;
            try {
                String name = UUID.randomUUID().toString()+System.currentTimeMillis()+".jpg";  ;
                String path = SaveBitmap2File.getSDPath()+ "/wishwall/";
                SaveBitmap2File.saveFile(bitmap, path,name);// 保存图片到手机
                // change_image.setImageBitmap(bitmap);
                String uploadPath = path+name;
                mImagePath.add(uploadPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 压缩图片
     * @param path
     * @return
     */
    private Bitmap decodeSampledBitmapFromPath(String path) {
        //获得图片的宽和高，并不把图片加载到内存中
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds =true;
        BitmapFactory.decodeFile(path,options);

        options.inSampleSize = calculateInSampleSize(options,480,800);
        //使用获得到的InSampleSize再次解析图片,并把图片加载到内存中
        options.inJustDecodeBounds= false;

        Bitmap bitmap = BitmapFactory.decodeFile(path,options);

        return bitmap;
    }

    /**
     * 根据需求的宽高，和实际的宽高计算InSampleSize
     * @param options
     * @param reqwidth
     * @param reqheight
     * @return
     */
    private int calculateInSampleSize(BitmapFactory.Options options, int reqwidth, int reqheight) {
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;
        if(width>reqwidth || height>reqheight){
            int widthRadio = Math.round(width*1.0f/reqwidth);
            int heightRadio = Math.round(height*1.0f/reqheight);
            inSampleSize = Math.max(widthRadio,heightRadio);
        }
        return inSampleSize;
    }
    /**
     * 上传图片
     * @param
     */
    private void preUpLoad() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Response<UploadTokenDTO> res = ApiClient.synchGetUploadToken();
                UploadTokenDTO body = res.body();
                if(body.getCode() == 200){
                     accessKeyId = body.getResult().getAccessKeyId();
                     accessKeySecret = body.getResult().getAccessKeySecret();
                     securityToken = body.getResult().getSecurityToken();
                     startUpload();
                }
            }
        }).start();
    }

    /**
     * 开始上传图片
     */
    private void startUpload() {
        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(accessKeyId, accessKeySecret,securityToken);

        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        OSSLog.enableLog();
        oss = new OSSClient(getApplicationContext(), App.endpoint, credentialProvider, conf);
        oss.updateCredentialProvider(new OSSStsTokenCredentialProvider(accessKeyId, accessKeySecret, securityToken));

        new Thread(new Runnable() {
            @Override
            public void run() {
                String name =  UUID.randomUUID().toString();
                ResuambleUploadSamples resuamUpLoad = new ResuambleUploadSamples(oss,App.uploadBucket,name,mImagePath.get(uploadIndex));
                resuamUpLoad.setUpLoadFinishListener(new ResuambleUploadSamples.UpLoadFinishListener() {
                    @Override
                    public void finish(String path) {
                        Log.e("finish", "finish:"+uploadIndex +">>>>>"+mImagePath.size());
                        ossImagePath.add(path);
                        Message msg = Message.obtain();
                        uploadIndex++;
                        if(uploadIndex < mImagePath.size()){
                            msg.what = LOADING;
                            uploadHandler.sendMessage(msg);
                        }else{
                            msg.what = FINISH;
                            uploadIndex=0;
                            uploadHandler.sendMessage(msg);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void fail() {
                        progressDialog.dismiss();
                        CustomToast.showMsg(IssueWishActivity.this,"图片上传失败");
                    }
                });
                resuamUpLoad.resumableUpload();
            }
        }).start();
    }

    public static void setOnIssueFinishListener(OnIssueFinishListener listener){
       mListener = listener;
    }

    public  interface OnIssueFinishListener{
        void finish();
    }
}
