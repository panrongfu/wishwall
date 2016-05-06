package org.ecfex.app.teamwork.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.ecfex.app.teamwork.R;
import org.ecfex.app.teamwork.views.CustomProgressDialog;
import org.ecfex.app.teamwork.views.CustomToast;

import java.util.HashMap;
import java.util.Map;


/** 
 *@Description:注册界面
 *@author panRongFu pan@ipushan.com
 *@date 2015年9月21日 下午7:55:58
 */
public class RegisterActivity extends BaseActivity {
	
	private TextView agree;
	private EditText phone;
	private EditText password;
	private EditText affirm_password;
	private EditText rg_get_validate;
	private TextView register_btn;
	private Button getValidate;
	private CheckBox checkbox;	
	private String userPhone;
	private String userPassWord;
	private String againPassWord;
	private String validate;
	private String seesion;
	private Map<String, String> params;
	private Map<String, String> headerId;
	private CustomToast customToast;
	private CustomProgressDialog progressDialog;


	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_activity);
		initView();
	}

	/**
	 * 初始化布局、控件
	 */
	private void initView() {	
		headerId = new HashMap<String, String>();
		params = new HashMap<String, String>();
		agree = (TextView) findViewById(R.id.agree_text);
		phone = (EditText) findViewById(R.id.rg_phone);
		password = (EditText) findViewById(R.id.rg_password);
		affirm_password = (EditText) findViewById(R.id.rg_affirm);
		register_btn = (TextView) findViewById(R.id.register_btn);
		rg_get_validate = (EditText) findViewById(R.id.rg_get_validate);
		getValidate = (Button) findViewById(R.id.get_verify_code);
		checkbox = (CheckBox)findViewById(R.id.register_agree_checkbox);
		rg_get_validate.setOnClickListener(new RegisterBtnClickListener());
		register_btn.setOnClickListener(new RegisterBtnClickListener());
		getValidate.setOnClickListener(new RegisterBtnClickListener());
		initAgree();
	}
	
	/**
	 * 设置超链接和颜色
	 */
	private void initAgree() {		
		SpannableString sp = new SpannableString(getResources().getString(R.string.agree));
		sp.setSpan(new NoLineClickSpan(getResources().getString(R.string.register_agree_url)), 3, 13, 0);
		sp.setSpan(new ForegroundColorSpan(Color.BLUE), 3, 13,Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		agree.setText(sp);
		agree.setMovementMethod(LinkMovementMethod.getInstance());
	}

	class RegisterBtnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.get_verify_code:
				getValCode();
				break;
			case R.id.register_btn:
				register();
				break;
			default:
				break;
			}
		}
	}

	class NoLineClickSpan extends URLSpan {

		public NoLineClickSpan(String url) {
			super(url);		
		}
		@Override
		public void updateDrawState(TextPaint ds) {			
			super.updateDrawState(ds);
			ds.setUnderlineText(false);
			ds.setColor(Color.GRAY);
		}	
	}

	/**
	 * 获取手机验证码
	 */
	private void getValCode() {	
	//	String url = getResources().getString(R.string.register_phone_code);
		userPhone = phone.getText().toString();
		userPassWord = password.getText().toString();
		againPassWord = affirm_password.getText().toString();
		if(TextUtils.isEmpty(userPhone)){
			CustomToast.showMsg(this,"手机号不能为空");
			return;
		}
		if(TextUtils.isEmpty(userPassWord)){
			CustomToast.showMsg(this,"密码不能为空");
			return;
		}
		if(TextUtils.isEmpty(againPassWord)){
			CustomToast.showMsg(this,"确认密码不能为空");
			return;
		}
		if(!againPassWord.equals(userPassWord)){
			CustomToast.showMsg(this,"两次密码输入不一致");
			return;
		}
	}

	/**
	 * 注册账号
	 */
	private void register() {
		if(TextUtils.isEmpty(userPhone)){
			CustomToast.showMsg(this,"手机号不能为空");
			return;
		}
		if(TextUtils.isEmpty(userPassWord)){
			CustomToast.showMsg(this,"密码不能为空");
			return;
		}
		if(TextUtils.isEmpty(againPassWord)){
			CustomToast.showMsg(this,"确认密码不能为空");
			return;
		}
		if(!againPassWord.equals(userPassWord)){
			CustomToast.showMsg(this,"两次密码输入不一致");
			return;
		}			
	}
}
