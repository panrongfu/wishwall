package net.wishwall.rong.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;

import net.wishwall.Constants;
import net.wishwall.R;
import net.wishwall.activities.ImageSelectPopupWindow;
import net.wishwall.activities.OptionImgActivity;
import net.wishwall.aliyunoss.ResuambleUploadSamples;
import net.wishwall.domain.ResultDTO;
import net.wishwall.domain.UploadTokenDTO;
import net.wishwall.service.ApiClient;
import net.wishwall.utils.CustomUtils;
import net.wishwall.utils.SpUtil;
import net.wishwall.views.CustomProgressDialog;
import net.wishwall.views.CustomToast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author panRongFu on 2016/4/14.
 * @Description
 * @email pan@ipushan.com
 */
public class CreateGroup extends BaseActivity implements View.OnClickListener {

    private TextView create;
    private EditText groupName;
    private EditText groupDes;
    private TextView back;
    private SpUtil userSpUtil;
    private static final int CROP = 2;
    private static final int CROP_PICTURE = 3;
    private static final int FROM_PHOTO = 7 ;
    private SpUtil tempSpUtil;
    private CircleImageView mImageView;
    private ImageSelectPopupWindow imgPopupwindow;
    private CustomProgressDialog progressDialog;
    private OSSClient oss;
    private String groupIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_group);
        initView();
        initImgPopupWindow();
    }

    /**
     * 初始化控件、界面、工具类
     */
    private void initView() {
        userSpUtil = new SpUtil(this,"userInfo");
        progressDialog = CustomProgressDialog.createDialog(this);
        mImageView = (CircleImageView)findViewById(R.id.group_icon);
        create = (TextView) findViewById(R.id.create_group);
        back = (TextView)findViewById(R.id.group_back);
        groupName = (EditText)findViewById(R.id.ed_group_name);
        groupDes = (EditText)findViewById(R.id.ed_group_des);
        create.setOnClickListener(this);
        back.setOnClickListener(this);
        mImageView.setOnClickListener(this);
    }

    private void initImgPopupWindow() {
        imgPopupwindow = new ImageSelectPopupWindow(this);
        imgPopupwindow.setOnItemClickListener(new ImageSelectPopupWindow.OnItemClickListener() {
            int REQUEST_CODE;
            @Override
            public void fromPhoto() {
                Intent intent = new Intent(CreateGroup.this,OptionImgActivity.class);
                startActivityForResult(intent,FROM_PHOTO);
                imgPopupwindow.dismiss();
            }

            @Override
            public void fromcamera() {
                Uri imageUri = null;
                String fileName = null;
                Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                REQUEST_CODE = CROP;

                // 删除上一次截图的临时文件
                String tempName = tempSpUtil.getKeyValue("tempName");
                String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                CustomUtils.deleteFileByName(CreateGroup.this,path,tempName);

                // 保存本次截图临时文件名字
                fileName = String.valueOf(System.currentTimeMillis()) + ".jpg";
                tempSpUtil.setKeyValue("tempName",fileName);

                imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), fileName));
                // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(openCameraIntent, REQUEST_CODE);
                imgPopupwindow.dismiss();
            }

            @Override
            public void cancel() {
                imgPopupwindow.dismiss();
            }
        });

        imgPopupwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                lightOn();
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case FROM_PHOTO:
                    String path = data.getStringExtra("path");
                    cropImage(Uri.fromFile(new File(path)), 155, 155, CROP_PICTURE);
                    break;
                case CROP:
                    Uri uri = null;
                    if (data != null) {
                        uri = data.getData();
                    }else {
                        String fileName = tempSpUtil.getKeyValue("tempName");
                        uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),fileName));
                    }
                    cropImage(uri, 155, 155, CROP_PICTURE);
                    break;

                case CROP_PICTURE:
                    Bitmap photo = null;
                    Uri photoUri = data.getData();
                    if (photoUri != null) {
                        photo = BitmapFactory.decodeFile(photoUri.getPath());
                    }
                    if (photo == null) {
                        Bundle extra = data.getExtras();
                        if (extra != null) {
                            photo = (Bitmap)extra.get("data");
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        }
                    }
                    savaImage(photo);
                    break;
            }
        }
    }

    //截取图片
    public void cropImage(Uri uri, int outputX, int outputY, int requestCode){
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, requestCode);
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
                final String path = CustomUtils.getSDCradPath()+ "/wishwall/";
                CustomUtils.saveBitmap2File(bitmap, path,name);// 保存图片到手机
                mImageView.setImageBitmap(bitmap);
                final String uploadPath = path + name;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Response<UploadTokenDTO> res = ApiClient.synchGetUploadToken();
                        UploadTokenDTO body = res.body();
                        if(body.getCode() == 200){
                            String accessKeyId = body.getResult().getAccessKeyId();
                            String accessKeySecret = body.getResult().getAccessKeySecret();
                            String securityToken = body.getResult().getSecurityToken();
                            startUpload(accessKeyId,accessKeySecret,securityToken,uploadPath);
                        }
                    }
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 开始上传图片
     * @param accessKeyId
     * @param accessKeySecret
     */
    private void startUpload(String accessKeyId, String accessKeySecret,String securityToken, final String path) {

        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(accessKeyId, accessKeySecret,securityToken);

        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        OSSLog.enableLog();
        oss = new OSSClient(CreateGroup.this, Constants.endpoint, credentialProvider, conf);
        oss.updateCredentialProvider(new OSSStsTokenCredentialProvider(accessKeyId, accessKeySecret, securityToken));

        new Thread(new Runnable() {
            @Override
            public void run() {
                String name = UUID.randomUUID().toString()+System.currentTimeMillis()+".jpg";
                ResuambleUploadSamples resuambleUploadSamples = new ResuambleUploadSamples(oss,Constants.uploadBucket,name,path);
                resuambleUploadSamples.setUpLoadFinishListener(new ResuambleUploadSamples.UpLoadFinishListener() {
                    @Override
                    public void finish(String url) {
                        //上传到阿里云oss上的存储图片的url
                    groupIcon = url;
                    }

                    @Override
                    public void fail() {
                    }
                });
                resuambleUploadSamples.resumableUpload();
            }
        }).start();
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
    protected void lightOff(float light) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = light;
        getWindow().setAttributes(lp);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.create_group:
                createGroup();
                break;
            case R.id.group_back:
                this.finish();
            case R.id.group_icon:
                imgPopupwindow.setAnimationStyle(R.style.popupwindow);
                imgPopupwindow.showAtLocation(v, Gravity.BOTTOM,0,20);
                lightOff(0.3f);
                break;
        }
    }

    /**
     * 创建群组
     */
    private void createGroup() {

        final String userId = userSpUtil.getKeyValue("userId");
        final String  name = groupName.getText().toString();
        String describe = groupDes.getText().toString();
        if(TextUtils.isEmpty(name)){
            CustomToast.showMsg(CreateGroup.this,"群名称不能为空");
            return;
        }
        progressDialog.setMessage("创建中...").show();
        ApiClient.createGroup(userId,name,describe, groupIcon, new Callback<ResultDTO>() {
            @Override
            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                ResultDTO body = response.body();
                if(body.getCode() == 200){
                    CustomToast.showMsg(CreateGroup.this,"创建成功");
                    CreateGroup.this.finish();
                }else{
                    CustomToast.showMsg(CreateGroup.this,"创建失败");
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResultDTO> call, Throwable t) {
                t.printStackTrace();
                progressDialog.dismiss();
                CustomToast.showMsg(CreateGroup.this,"创建失败");
            }
        });
    }
}
