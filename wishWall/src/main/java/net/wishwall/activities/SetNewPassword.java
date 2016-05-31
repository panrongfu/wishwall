package net.wishwall.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import net.wishwall.R;
import net.wishwall.domain.ResultDTO;
import net.wishwall.service.ApiClient;
import net.wishwall.views.CustomProgressDialog;
import net.wishwall.views.CustomToast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 *
 *@Description:重新设置密码
 *@author panRongFu pan@ipushan.com
 *@date 2015年11月21日 上午11:59:14
 */
public class SetNewPassword extends BaseActivity implements OnClickListener {

	private EditText setPhone;
	private EditText setPasswrod;
	private TextView setNewPwd;
	private TextView back;
	private CustomProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_new_password);
        initViewUI();
    }

	public void initViewUI() {
		progressDialog = CustomProgressDialog.createDialog(this);
		back = (TextView)findViewById(R.id.set_new_pwd_back);
		setPhone = (EditText)findViewById(R.id.set_new_pwd_phone);
		setPasswrod = (EditText)findViewById(R.id.set_confrim_password);
		setNewPwd = (TextView)findViewById(R.id.set_new_pwd_confrim);
		setNewPwd.setOnClickListener(this);
        back.setOnClickListener(this);

	}

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_new_pwd_back:
                SetNewPassword.this.finish();
                break;
            case R.id.set_new_pwd_confrim:
                setNewPassword();
                break;
        }
    }

    /**
     * 设置新密码
     */
    private void setNewPassword() {
        progressDialog.setMessage("请稍等...").show();
        String phone = setPhone.getText().toString();
        String password = setPasswrod.getText().toString();
        if(TextUtils.isEmpty(phone)){
            CustomToast.showMsg(this,"手机号码不能为空");
            return;
        }
        if(TextUtils.isEmpty(password)){
            CustomToast.showMsg(this,"新密码不能为空");
            return;
        }

        ApiClient.setNewPassword(phone, password, new Callback<ResultDTO>() {
            @Override
            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                ResultDTO body = response.body();
                if(body.getCode() == 200){
                    CustomToast.showMsg(SetNewPassword.this,"密码重置成功");
                    SetNewPassword.this.finish();
                }else{
                    CustomToast.showMsg(SetNewPassword.this,"密码重置失败");
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResultDTO> call, Throwable t) {
                CustomToast.showMsg(SetNewPassword.this,"密码重置失败");
                progressDialog.dismiss();
            }
        });
    }
}
