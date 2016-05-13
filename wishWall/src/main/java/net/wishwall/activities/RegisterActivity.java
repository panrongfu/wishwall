package net.wishwall.activities;

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

import net.wishwall.R;
import net.wishwall.domain.RegisterDTO;
import net.wishwall.service.ApiClient;
import net.wishwall.views.CustomProgressDialog;
import net.wishwall.views.CustomToast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/** 
 *@Description:注册界面
 *@author panRongFu pan@ipushan.com
 *@date 2015年9月21日 下午7:55:58
 */
public class RegisterActivity extends BaseActivity implements OnClickListener{

	private TextView back;
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
	private CustomToast customToast;
	private CustomProgressDialog progressDialog;


	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_activity);
		initUtils();
		initView();
	}
	/**
	 * 初始化工具类、变量
	 */
	private void initUtils() {
		customToast = CustomToast.createToast(this);
		progressDialog = CustomProgressDialog.createDialog(this);
	}
	/**
	 * 初始化布局、控件
	 */
	private void initView() {
		back = (TextView)findViewById(R.id.register_back);
		agree = (TextView) findViewById(R.id.agree_text);
		phone = (EditText) findViewById(R.id.rg_phone);
		password = (EditText) findViewById(R.id.rg_password);
		affirm_password = (EditText) findViewById(R.id.rg_affirm);
		register_btn = (TextView) findViewById(R.id.register_btn);
		//rg_get_validate = (EditText) findViewById(R.id.rg_get_validate);
		//getValidate = (Button) findViewById(R.id.get_verify_code);
		checkbox = (CheckBox)findViewById(R.id.register_agree_checkbox);
		back.setOnClickListener(this);
		rg_get_validate.setOnClickListener(this);
		register_btn.setOnClickListener(this);
		//getValidate.setOnClickListener(this);
		initAgree();
	}
	
	/**
	 * 设置超链接和颜色
	 */
	private void initAgree() {		
		SpannableString sp = new SpannableString(getResources().getString(R.string.agree));
		sp.setSpan(new NoLineClickSpan(getResources().getString(R.string.register_agree_url)), 3, sp.length(), 0);
		sp.setSpan(new ForegroundColorSpan(Color.BLUE), 3, sp.length(),Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		agree.setText(sp);
		agree.setMovementMethod(LinkMovementMethod.getInstance());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.register_back:
				RegisterActivity.this.finish();
//			case R.id.get_verify_code:
//				getValCode(getValidate);
//				break;
			case R.id.register_btn:
				preRegister();
				break;
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
//	private void getValCode(final Button button) {
//		userPhone = phone.getText().toString();
//		userPassWord = password.getText().toString();
//		againPassWord = affirm_password.getText().toString();
//		if ("".equals(userPhone) || "".equals(userPassWord)|| "".equals(againPassWord)) {
//			if ("".equals(userPhone)) {
//				customToast.setMessage("手机号不能为空").show();
//			} else if ("".equals(userPassWord)) {
//				customToast.setMessage("密码不能为空").show();
//			} else if ("".equals(againPassWord)) {
//				customToast.setMessage("确认密码不能为空").show();
//			}
//            return;
//		}
//        /**
//         * 利用属性动画，实现倒计时重新获取验证码效果
//         */
//        button.setEnabled(false);
//        ValueAnimator valueAnimator = ValueAnimator.ofInt(60,0);
//        valueAnimator.setDuration(60000);
//        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                int value = (Integer) animation.getAnimatedValue();
//                button.setText("("+value+")"+ RegisterActivity.this.getResources().getString(R.string.register_refresh));
//                if(value == 0){
//                    button.setEnabled(true);
//                    button.setBackgroundResource(R.color.gray_light);
//                    button.setText(R.string.get_verify_code);
//                }
//            }
//        });
//        valueAnimator.start();
//	}

	/**
	 * 注册账号前准备，判断用户输入的信息是否有误
	 */
	private void preRegister() {
		//validate = rg_get_validate.getText().toString();
		userPhone = phone.getText().toString();
		userPassWord = password.getText().toString();
		againPassWord = affirm_password.getText().toString();
		if(TextUtils.isEmpty(userPhone)){
			CustomToast.showMsg(this,"账号不能为空");
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
		if(!userPassWord.equals(againPassWord)){
			CustomToast.showMsg(this,"两次密码不一致");
			return;
		}
		if(!checkbox.isChecked()){
			CustomToast.showMsg(this,"请阅读并确认您同意服务条款再确认");
			return;
		}
		//开始注册
		startRegister();
	}

	/**
	 * 开始注册
	 */
	public void startRegister(){
		progressDialog.setMessage("注册中...").show();
		ApiClient.userRegister(userPhone, userPassWord, new Callback<RegisterDTO>() {
			@Override
			public void onResponse(Call<RegisterDTO> call, Response<RegisterDTO> response) {
				RegisterDTO body = response.body();
				if(body.getCode() == 200 ){
					customToast.setMessage(getResources().getString(R.string.register_success)).show();
                    RegisterActivity.this.finish();
				}else {
					customToast.setMessage(body.getMessage()).show();
				}
                progressDialog.dismiss();
			}
			@Override
			public void onFailure(Call<RegisterDTO> call, Throwable t) {
                progressDialog.dismiss();
				t.printStackTrace();
			}
		});
	}
}
