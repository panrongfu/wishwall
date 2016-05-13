package net.wishwall.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
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
import net.wishwall.aliyunoss.ResuambleUploadSamples;
import net.wishwall.domain.ResultDTO;
import net.wishwall.domain.UploadTokenDTO;
import net.wishwall.service.ApiClient;
import net.wishwall.utils.ImageTools;
import net.wishwall.utils.SaveBitmap2File;
import net.wishwall.utils.SpUtil;
import net.wishwall.views.CustomDialogFragment;
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
 * 
 *@Description: 修改个人资料
 *@author panRongFu pan@ipushan.com
 *@date 2015年10月21日 下午5:33:18
 */
public class EditPersonDetailActivity extends BaseActivity
        implements OnCheckedChangeListener,OnClickListener{



    private String uploadName;
    private String uploadPhone;
    private String uploadWx;
    private String uploadQq;
    private String uploadSex;
    private String provCode;
    private String cityCode;
    private String areaCode;

    private String bornYear;
    private String bornMonth;
    private String bornDay;

	private TextView back;
	private TextView change_save;
	private EditText detail_name;
	private RadioGroup detail_sex_group;
	private RadioButton detail_man;
	private RadioButton detail_girl;
	private TextView detail_phone;
	private EditText detail_qq;
	private EditText detail_weixin;
	private TextView detail_born;
	private TextView detail_local;
    private LinearLayout  edit_person_info_layout;
	private RelativeLayout layout_change_detail_born;
	private RelativeLayout layout_change_detail_loacl;
	private CircleImageView change_image;
    private OSS oss;

    private ImageSelectPopupWindow popupwindow;
    private static final int CROP = 2;
    private static final int CROP_PICTURE = 3;
    private static final int FROM_PHOTO = 7 ;
    private static final int CROP_FINISH = 8 ;
    private final int requestMyLogo = 4;
    private final int RequestTime = 5;
    private final int RequestLocal = 6;
    private SpUtil tempSpUtil;
    private String uploadPath;
    private SpUtil userSpUtil;
    private String imageUrl;
    private CustomProgressDialog progressDialog;
    private static final int FINISH = 0X01;
    private Handler updateInfoHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            uploadInfo();
        }
    };

    @Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.edit_person_detail);
		initUtils();
        initViewUI();
	}

	private void initUtils() {
        tempSpUtil = new SpUtil(this,"temp");
        userSpUtil = new SpUtil(this, Constants.USER_SPUTIL);
        progressDialog = CustomProgressDialog.createDialog(this);
		Intent intent = getIntent();
		if(null != intent){			
//			logoPath = intent.getStringExtra("logoPath");
//			name = intent.getStringExtra("name");
//            uploadSex = intent.getStringExtra("sex");
//			phone = intent.getStringExtra("phone");
//			bornId = intent.getStringExtra("bornId");
//			qq = intent.getStringExtra("qq");
//			wx = intent.getStringExtra("wx");
//			bornDay = intent.getStringExtra("bornDay");
//			bornLocal = intent.getStringExtra("bornLocal");
		}


	}

	private void initViewUI() {
        edit_person_info_layout = (LinearLayout)findViewById(R.id.edit_person_info_layout);
		back = (TextView)findViewById(R.id.me_change_detail_back);
		change_image = (CircleImageView) findViewById(R.id.me_change_detail_image);
		change_save = (TextView) findViewById(R.id.me_change_detail_save);
		detail_name = (EditText) findViewById(R.id.change_detail_name);
		detail_sex_group = (RadioGroup) findViewById(R.id.change_sex_group);
		detail_girl = (RadioButton)findViewById(R.id.sex_girl);
		detail_man = (RadioButton)findViewById(R.id.sex_man);
		detail_phone = (EditText) findViewById(R.id.change_detail_phone);
		detail_qq = (EditText) findViewById(R.id.change_detail_qq);
		detail_weixin = (EditText) findViewById(R.id.change_detail_weixin);
		detail_born = (TextView) findViewById(R.id.change_detail_born);
		detail_local = (TextView) findViewById(R.id.change_detail_local);
		layout_change_detail_born = (RelativeLayout) findViewById(R.id.layout_change_detail_born);
		layout_change_detail_loacl = (RelativeLayout) findViewById(R.id.layout_change_detail_loacl);

		detail_sex_group.setOnCheckedChangeListener(this);
		back.setOnClickListener(this);
		change_save.setOnClickListener(this);
		change_image.setOnClickListener(this);
		layout_change_detail_born.setOnClickListener(this);
		layout_change_detail_loacl.setOnClickListener(this);
        initPopupWindow();
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId){
            case R.id.sex_man:
                uploadSex="男";
                break;
            case R.id.sex_girl:
                uploadSex= "女";
                break;
        }
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.me_change_detail_image:
                popupwindow.setAnimationStyle(R.style.popupwindow);
                popupwindow.showAtLocation(edit_person_info_layout, Gravity.BOTTOM,0,20);
                lightOff();
				break;
			case R.id.layout_change_detail_born:
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                // Create and show the dialog.
                CustomDialogFragment newFragment = CustomDialogFragment.newInstance("dialog",R.layout.date_picker_dialog);
                newFragment.setOnSelectFinishListener(new CustomDialogFragment.OnSelectFinishListener() {
                    @Override
                    public void finish(DatePicker dp) {
                         bornYear = String.valueOf(dp.getYear());
                         bornMonth = String.valueOf(dp.getMonth()+1);
                         bornDay = String.valueOf(dp.getDayOfMonth());
                        String born = bornYear+"-"+bornMonth+"-"+bornDay;
                        detail_born.setText(born);
                    }
                });
                newFragment.show(ft, "dialog");
                break;
			case R.id.layout_change_detail_loacl:
				Intent local = new Intent(getApplicationContext(),ProvCityAreaActivity.class);
				startActivityForResult(local, RequestLocal);
				overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
				break;
			case R.id.me_change_detail_save:
                preUpLoad(uploadPath);
				break;
			case R.id.me_change_detail_back:
				finish();
		}
	}

    /**
     * 提交个人信息到服务器
     */
    private void uploadInfo() {
          String userId = userSpUtil.getKeyValue("userId");
//        String uploadName = detail_name.getText().toString();
//        String uploadPhone = detail_phone.getText().toString();
//        String uploadQq = detail_qq.getText().toString();
//        String uploadWx = detail_weixin.getText().toString();
//        String uploadCityCode = cityCode;
//        String uploadC =provCode;
//        String up = areaCode;
//
//        if(uploadName.trim() == null) CustomToast.showMsg(this,"用户名不能为空");
//        if(sex.trim() == null) CustomToast.showMsg(this,"性别能为空");
//        if(uploadPhone.trim() == null) CustomToast.showMsg(this,"电话不能为空");
//        if(uploadQq.trim() == null) CustomToast.showMsg(this,"QQ号不能为空");
//        if(uploadWx.trim() == null) CustomToast.showMsg(this,"微信号不能为空");
//        if(bornYear.trim()==null || bornMonth.trim() == null||bornDay ==null)
//            CustomToast.showMsg(this,"出生日期不能为空");
//        if(provCode.trim()==null || cityCode.trim() ==null||areaCode.trim()==null)
//            CustomToast.showMsg(this,"地址不能为空");

        ApiClient.updateUserInfo(
                userId, uploadName, imageUrl, uploadSex,uploadPhone,
                bornYear, bornMonth, bornDay, provCode, cityCode, areaCode,
                uploadWx, uploadQq, new Callback<ResultDTO>() {
                    @Override
                    public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                        ResultDTO body = response.body();
                        if(body.getCode() == 200){
                            CustomToast.showMsg(EditPersonDetailActivity.this,"保存成功");
                            progressDialog.dismiss();
                        }else {
                            CustomToast.showMsg(EditPersonDetailActivity.this,body.getMessage());
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultDTO> call, Throwable t) {
                            t.printStackTrace();
                    }
                });
    }

    private void initPopupWindow() {
        popupwindow = new ImageSelectPopupWindow(this);
        popupwindow.setOnItemClickListener(new ImageSelectPopupWindow.OnItemClickListener() {
            int REQUEST_CODE;
            @Override
            public void fromPhoto() {
                Intent intent = new Intent(EditPersonDetailActivity.this,OptionImgActivity.class);
                startActivityForResult(intent,FROM_PHOTO);
                popupwindow.dismiss();
            }

            @Override
            public void fromcamera() {
                Uri imageUri = null;
                String fileName = null;
                Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                REQUEST_CODE = CROP;

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
                startActivityForResult(openCameraIntent, REQUEST_CODE);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case FROM_PHOTO:
                    String path = data.getStringExtra("path");
                    Log.e("PHOTO", "onActivityResult: "+path );
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
                case requestMyLogo:
                   // savaImage(data);
                    break;
                case RequestTime:
                    String dateTime = data.getExtras().getString("Date");
                    detail_born.setText(dateTime);
                    break;
                case RequestLocal:
                    String provName = data.getStringExtra("provName");
                    String cityName = data.getStringExtra("cityName");
                    String areaName = data.getStringExtra("areaName");
                    provCode = data.getStringExtra("provCode");
                    cityCode = data.getStringExtra("cityCode");
                    areaCode = data.getStringExtra("areaCode");
                    String local =provName+" "+cityName+" "+areaName;
                    detail_local.setText(local);
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
                String path = SaveBitmap2File.getSDPath()+ "/wishwall/";
                SaveBitmap2File.saveFile(bitmap, path,name);// 保存图片到手机
                change_image.setImageBitmap(bitmap);
                uploadPath = path+name;
            } catch (Exception e) {
                e.printStackTrace();
              }
		}
	}

	/**
	 * 上传图片
	 * @param
     */
	private void preUpLoad(final String path) {

         uploadName = detail_name.getText().toString();
         uploadPhone = detail_phone.getText().toString();
         uploadQq = detail_qq.getText().toString();
         uploadWx = detail_weixin.getText().toString();

        if( null == uploadName || uploadName.length() <=0 ){
            CustomToast.showMsg(this,"用户名不能为空");
            return;
        }
        if(null == uploadSex || uploadSex.length() <=0){
            CustomToast.showMsg(this,"性别不能为空");
            return;
        }
        if(null == uploadPhone || uploadPhone.length() <= 0) {
            CustomToast.showMsg(this,"电话不能为空");
            return;
        }
        if(null == uploadQq || uploadQq.length()<=0) {
            CustomToast.showMsg(this,"QQ号不能为空");
            return;
        }
        if( null == uploadWx || uploadWx.length()<=0) {
            CustomToast.showMsg(this,"微信号不能为空");
            return;
        }
        if(null == bornYear || null == bornMonth||null == bornDay) {
            CustomToast.showMsg(this,"出生日期不能为空");
            return;
        }
        if(null==provCode ||null == cityCode||null== areaCode) {
            CustomToast.showMsg(this,"地址不能为空");
            return;
        }

        progressDialog.setMessage("保存中...").show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Response<UploadTokenDTO> res = ApiClient.synchGetUploadToken();
                UploadTokenDTO body = res.body();
                if(body.getCode() == 200){
                    String accessKeyId = body.getResult().getAccessKeyId();
                    String accessKeySecret = body.getResult().getAccessKeySecret();
                    String securityToken = body.getResult().getSecurityToken();
                    startUpload(accessKeyId,accessKeySecret,securityToken,path);
                }
            }
        }).start();
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
        oss = new OSSClient(getApplicationContext(), App.endpoint, credentialProvider, conf);
        oss.updateCredentialProvider(new OSSStsTokenCredentialProvider(accessKeyId, accessKeySecret, securityToken));

        new Thread(new Runnable() {
            @Override
            public void run() {
                String name = UUID.randomUUID().toString()+System.currentTimeMillis()+".jpg";
                ResuambleUploadSamples resuambleUploadSamples = new ResuambleUploadSamples(oss,App.uploadBucket,name,path);
                resuambleUploadSamples.setUpLoadFinishListener(new ResuambleUploadSamples.UpLoadFinishListener() {
                    @Override
                    public void finish(String path) {
                        //上传到阿里云oss上的存储图片的url
                        imageUrl = path;
                        updateInfoHandler.sendEmptyMessage(FINISH);
                    }

                    @Override
                    public void fail() {
                        CustomToast.showMsg(EditPersonDetailActivity.this,"保存失败");
                        progressDialog.dismiss();
                    }
                });
                resuambleUploadSamples.resumableUpload();
            }
        }).start();
    }
}
