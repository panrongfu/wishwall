package net.wishwall.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import net.wishwall.Constants;
import net.wishwall.R;
import net.wishwall.domain.LoginDTO;
import net.wishwall.domain.RegisterDTO;
import net.wishwall.service.ApiClient;
import net.wishwall.utils.SpUtil;
import net.wishwall.views.CustomProgressDialog;
import net.wishwall.views.CustomToast;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * @Description:登录界面
 * @author panRongFu pan@ipushan.com
 * @date 2015年9月18日 上午9:55:47
 */
public class LoginActivity extends BaseActivity
		implements OnClickListener,PlatformActionListener,Handler.Callback {
	

	private TextView register;
	private TextView forget;
	private TextView login_btn;
	private TextView qqLogin;
	private TextView wxLogin;
	private TextView wbLogin;
	private String userPhone;
	private String userPassWord;
	private EditText phone;
	private EditText password;
	private CheckBox remember;
	public static String sysInfo;
	public static String userInfo;
	public static String thirdUserName;
	public static String thirdUserlogo;

	private static final int MSG_SMSSDK_CALLBACK = 1;
	private static final int MSG_AUTH_CANCEL = 2;
	private static final int MSG_AUTH_ERROR= 3;
	private static final int MSG_AUTH_COMPLETE = 4;	

	private Handler handler;
	private CustomToast customToast;
	private SpUtil userSpUtil;
	private CustomProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        userSpUtil = new SpUtil(this, Constants.USER_SPUTIL);
//        boolean isLogin = userSpUtil.getBooleanCode("login");
//        if(isLogin){
//            Intent intent =  new Intent(this,MainActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//			this.finish();
//        }
		setContentView(R.layout.login_activity);
		initView();
	}
	@Override
	protected void onResume() {
		super.onResume();
		initUtils();
	}

	/**
	 * 初始化工具类
	 */
	private void initUtils() {
		customToast = CustomToast.createToast(this);
		progressDialog = CustomProgressDialog.createDialog(this);

	}
	/**
	 * 初始化布局、控件
	 */
	private void initView() {
        handler = new Handler(this);
		phone = (EditText) findViewById(R.id.login_username);
		password = (EditText) findViewById(R.id.login_password);
		register = (TextView) findViewById(R.id.register_btn);
		forget = (TextView) findViewById(R.id.login_forget_password);
		login_btn = (TextView) findViewById(R.id.login_btn);
		remember = (CheckBox) findViewById(R.id.remember_password);
		qqLogin = (TextView)findViewById(R.id.qq_login);
		wxLogin = (TextView)findViewById(R.id.wx_login);
		wbLogin = (TextView)findViewById(R.id.wb_login);
		wxLogin.setOnClickListener(this);
		qqLogin.setOnClickListener(this);
		wbLogin.setOnClickListener(this);
		register.setOnClickListener(this);
		forget.setOnClickListener(this);
		login_btn.setOnClickListener(this);
		setAcountPwd();
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.register_btn:
				Intent register = new Intent(this,RegisterActivity.class);
				startActivity(register);
				overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
				break;
			case R.id.login_forget_password:
//				Intent intent_forget = new Intent(LoginActivity.this,FindBackPassword.class);
//				startActivity(intent_forget);
				overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
				break;
			case R.id.login_btn:
				preLogin();
				break;
			case R.id.qq_login:
				Platform qq = ShareSDK.getPlatform(QQ.NAME);
				authorize(qq);
				break;
			case R.id.wx_login:
//				Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
//				authorize(wechat);
				CustomToast.showMsg(this,"暂不支持微信登录");
				break;
			case R.id.wb_login:
				Platform sina = ShareSDK.getPlatform(SinaWeibo.NAME);
				authorize(sina);
				break;
		}
	}

	/**
	 * 开始授权
	 * @param plat
     */
    private void authorize(Platform plat) {
        //判断指定平台是否已经完成授权
        if(plat.isAuthValid()) {
			PlatformDb platDB = plat.getDb();
			String userId = platDB.getUserId();
            if (userId != null) {
				final String userName = platDB.getUserName();
				startLogin(userName,Constants.DEFAULT_PASSWORD);
                return;
            }
        }
        plat.setPlatformActionListener(this);
        // true不使用SSO授权，false使用SSO授权
        plat.SSOSetting(true);
        //获取用户资料
        plat.showUser(null);
    }

	@Override
	public void onComplete(Platform platform, int action, HashMap<String, Object> hashMap) {
    //    handler.sendEmptyMessage(MSG_AUTH_COMPLETE);
        //用户资源都保存到res
		//通过打印res数据看看有哪些数据是你想要的
//		if (action == Platform.ACTION_USER_INFOR) {
//			PlatformDb platDB = platform.getDb();//获取数平台数据DB
//			//通过DB获取各种数据
//			platDB.getToken();
//			platDB.getUserGender();
//			platDB.getUserIcon();
//			platDB.getUserId();
//			platDB.getUserName();
//		}
        if (action == Platform.ACTION_USER_INFOR) {
            Message msg = new Message();
            msg.what = MSG_AUTH_COMPLETE;
            msg.obj = new Object[] {platform.getName(), hashMap};
            handler.sendMessage(msg);
        }
       // CustomToast.showMsg(LoginActivity.this,"授权成功>>>>>>>>>>>>>>>>>>>>>>>>");
		//解析部分用户资料字段
		String id,name,description,profile_image_url;
		id=hashMap.get("id").toString();//ID
		name=hashMap.get("name").toString();//用户名
		description=hashMap.get("description").toString();//描述
		profile_image_url=hashMap.get("profile_image_url").toString();//头像链接
		String str="ID: "+id+";\n"+
				"用户名： "+name+";\n"+
				"描述："+description+";\n"+
				"用户头像地址："+profile_image_url;
		System.out.println("用户资料: "+str);
//	    CustomToast.showMsg(LoginActivity.this,str);
//
//        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//        startActivity(intent);
//        LoginActivity.this.finish();
	}

	@Override
	public void onError(Platform platform, int i, Throwable throwable) {
            CustomToast.showMsg(this,"三方登录错误");
        handler.sendEmptyMessage(MSG_AUTH_ERROR);
	}

	@Override
	public void onCancel(Platform platform, int i) {
        handler.sendEmptyMessage(MSG_AUTH_CANCEL);

	}

    @Override
    public boolean handleMessage(Message msg) {

        switch(msg.what) {
            case MSG_AUTH_CANCEL: {
                //取消授权
                CustomToast.showMsg(this,"取消授权>>>>>>>>>>>>>>>>>>>>>>>>");
            } break;
            case MSG_AUTH_ERROR: {
                //授权失败
                CustomToast.showMsg(this,"授权失败>>>>>>>>>>>>>>>>>>>>>>>>");
            } break;
            case MSG_AUTH_COMPLETE: {
                //授权成功
                CustomToast.showMsg(this,"授权成功>>>>>>>>>>>>>>>>>>>>>>>>");
                Object[] objs = (Object[]) msg.obj;
                String platformName = (String) objs[0];;
                Platform platform = ShareSDK.getPlatform(platformName);
                PlatformDb platDB = platform.getDb();//获取数平台数据DB
                //通过DB获取各种数据
                String userToken =  platDB.getToken();
                String userGender = platDB.getUserGender();
                String userIcon = platDB.getUserIcon();
                String userId = platDB.getUserId();
                final String userName = platDB.getUserName();

                ApiClient.defaultRegister(userId, userName, userIcon, userGender, new Callback<RegisterDTO>() {
                    @Override
                    public void onResponse(Call<RegisterDTO> call, Response<RegisterDTO> response) {
						RegisterDTO body = response.body();
                        if(body.getCode() == 200){
                            startLogin(userName,Constants.DEFAULT_PASSWORD);
                        }
                    }

                    @Override
                    public void onFailure(Call<RegisterDTO> call, Throwable t) {

                    }
                });
                CustomToast.showMsg(this,userToken+userGender+userIcon+userId+userName);
            } break;
        }
        return false;

    }
    /**
	 * 当用户切换到登录界面的时候先判断 如果用户上次登录的时候选择了记住密码，
	 * 则系统自动获取账号密码 否则需要用户自己填写账号密码
	 */
	private void setAcountPwd() {
		boolean keep = userSpUtil.getBooleanCode("remember");
		if (keep) {
			remember.setChecked(true);
			String account = userSpUtil.getKeyValue("account");
			String pass = userSpUtil.getKeyValue("password");
			phone.setText(account);
			password.setText(pass);
		} else  {
			remember.setChecked(false);
		}
	}
	/**
	 * 开始登录前，先判断用户输入是否有误
	 */
	private void preLogin() {
		userPhone = phone.getText().toString();
		userPassWord = password.getText().toString();

		if(TextUtils.isEmpty(userPhone)){
			CustomToast.showMsg(this,"手机号不能为空");
			return;
		}
		if(TextUtils.isEmpty(userPassWord)){
			CustomToast.showMsg(this,"密码不能为空");
			return;
		}
		startLogin(userPhone,userPassWord);
	 }

	/**
	 * 开始登陆
	 */
	public void startLogin(String userPhone,String userPassWord){
		progressDialog.setMessage("登陆中...").show();
		ApiClient.userLogin(userPhone, userPassWord, new Callback<LoginDTO>() {
			@Override
			public void onResponse(Call<LoginDTO> call, Response<LoginDTO> response) {
				LoginDTO body = response.body();
				if(body.getCode() == 200){
					userSpUtil.putBooleanCode("login",true);
                    userSpUtil.setKeyValue("token",body.getResult().getToken());
                    userSpUtil.setKeyValue("userId",body.getResult().getUserid());
					userSpUtil.setKeyValue("nickName",body.getResult().getNickname());
					userSpUtil.setKeyValue("userIcon",body.getResult().getIcon());
					userSpUtil.setKeyValue("wToken","Bearer "+body.getResult().getWToken());
					mindPassword();
					progressDialog.dismiss();
					Intent intent = new Intent(LoginActivity.this, MainActivity.class);
					startActivity(intent);
					LoginActivity.this.finish();

				}else{
					customToast.setMessage(body.getMessage()).show();
					progressDialog.dismiss();
				}
			}
			@Override
			public void onFailure(Call<LoginDTO> call, Throwable t) {
				t.printStackTrace();
				progressDialog.dismiss();
			}
		});
	}

	/**
	 * 如果用户选择了记住密码 则把用户的账号密码保存在手机
	 */
	public void mindPassword() {
		if (remember.isChecked()) {				
			userSpUtil.putBooleanCode("remember",true);
		} else {
			userSpUtil.putBooleanCode("remember",false);
		}
		userSpUtil.setKeyValue("account", userPhone);
		userSpUtil.setKeyValue("password", userPassWord);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			LoginActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
