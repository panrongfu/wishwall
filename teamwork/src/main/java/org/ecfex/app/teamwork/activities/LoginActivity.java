package org.ecfex.app.teamwork.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.ecfex.app.teamwork.R;
import org.ecfex.app.teamwork.utils.SpUtil;
import org.ecfex.app.teamwork.views.CustomToast;


/**
 * @Description:登录界面
 * @author panRongFu pan@ipushan.com
 * @date 2015年9月18日 上午9:55:47
 */
public class LoginActivity extends BaseActivity implements OnClickListener{
	
	private String loginFrom;
	private String loginBack;
	private String seesion;	
	private TextView register;
	private TextView forget;
	private TextView backHome;
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
	// 填写从短信SDK应用后台注册得到的APPKEY
	private static String APPKEY = "ba5af6809dab";
	// 填写从短信SDK应用后台注册得到的APPSECRET
	private static String APPSECRET = "5d5d88acf7a4a7b70fb7ea0437aaf3ba";
	private String smssdkAppkey;
	private String smssdkAppSecret;
//	private OnLoginListener signupListener;
	private Handler handler;
	private CustomToast customToast;
	private SpUtil userInfoPrefUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		initView();

	}
	/**
	 * 初始化布局、控件
	 */
	private void initView() {
		phone = (EditText) findViewById(R.id.login_username);
		password = (EditText) findViewById(R.id.login_password);
		register = (TextView) findViewById(R.id.register_btn);
		forget = (TextView) findViewById(R.id.login_forget_password);

		login_btn = (TextView) findViewById(R.id.login_btn);
		remember = (CheckBox) findViewById(R.id.remember_password);

		wxLogin.setOnClickListener(new ButtonClickListener());
		qqLogin.setOnClickListener(new ButtonClickListener());
		wbLogin.setOnClickListener(new ButtonClickListener());
		register.setOnClickListener(new ButtonClickListener());
		forget.setOnClickListener(new ButtonClickListener());
		backHome.setOnClickListener(new ButtonClickListener());
		login_btn.setOnClickListener(new ButtonClickListener());
		setAcountPassWord();
	}

	/**
	 * 当用户切换到登录界面的时候先判断 如果用户上次登录的时候选择了记住密码，
	 * 则系统自动获取账号密码 否则需要用户自己填写账号密码
	 */
	private void setAcountPassWord() {
		String keep = userInfoPrefUtil.getKeyValue("remember");
		if (keep.equals("isRemember")) {
			remember.setChecked(true);
			String account = userInfoPrefUtil.getKeyValue("account");
			String pass = userInfoPrefUtil.getKeyValue("password");
			phone.setText(account);
			password.setText(pass);
		} else if (keep.equals("notRemember")) {
			remember.setChecked(false);
		}
	}

	class ButtonClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.register_btn:
				Intent intent_register = new Intent(LoginActivity.this,RegisterActivity.class);
				startActivity(intent_register);
				overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
				break;
			case R.id.login_forget_password:
//				Intent intent_forget = new Intent(LoginActivity.this,FindBackPassword.class);
//				startActivity(intent_forget);
//				overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
				break;
			case R.id.login_btn:
				login();
				break;
			}
		}
   }
	/**
	 * 开始登录
	 */
	private void login() { 
		//String url = getResources().getString(R.string.login_url);
		userPhone = phone.getText().toString();
		userPassWord = password.getText().toString();
		if(TextUtils.isEmpty(userPhone)) {
			CustomToast.showMsg(this,"手机号不能为空");
			return;
		}
		if(TextUtils.isEmpty(userPassWord)){
			CustomToast.showMsg(this,"密码不能为空");
			return;
		}
	 }

	/**
	 * 如果用户选择了记住密码 则把用户的账号密码保存在手机
	 */
	public void keepPassword() {
		if (remember.isChecked()) {				
			userInfoPrefUtil.setKeyValue("remember", "isRemember");
		} else {
			userInfoPrefUtil.setKeyValue("remember", "notRemember");
		}
		userInfoPrefUtil.setKeyValue("account", userPhone);
		userInfoPrefUtil.setKeyValue("password", userPassWord);
	}



	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			LoginActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
}
