package net.wishwall.rong.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import net.wishwall.R;
import net.wishwall.domain.ResultDTO;
import net.wishwall.service.ApiClient;
import net.wishwall.utils.SpUtil;
import net.wishwall.views.CustomProgressDialog;
import net.wishwall.views.CustomToast;

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
    private CustomToast customToast;
    private CustomProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_group);
        initView();
    }

    /**
     * 初始化控件、界面、工具类
     */
    private void initView() {
        userSpUtil = new SpUtil(this,"userInfo");
        customToast = CustomToast.createToast(this);
        progressDialog = CustomProgressDialog.createDialog(this);

        create = (TextView) findViewById(R.id.create_group);
        back = (TextView)findViewById(R.id.group_back);
        groupName = (EditText)findViewById(R.id.ed_group_name);
        groupDes = (EditText)findViewById(R.id.ed_group_des);
        create.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.create_group:
                createGroup();
                break;
            case R.id.group_back:
                this.finish();
                break;
        }
    }

    /**
     * 创建群组
     */
    private void createGroup() {

        String userId = userSpUtil.getKeyValue("userId");
        String name = groupName.getText().toString();
        if("".equals(name)||null == name){
            customToast.setMessage("群名称不能为空").show();
            return;
        }
        progressDialog.setMessage("创建中...").show();
        ApiClient.createGroup(userId, name, new Callback<ResultDTO>() {
            @Override
            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                ResultDTO body = response.body();
                if(body.getCode() == 200){
                    customToast.setMessage("创建成功").show();
                }else{
                    customToast.setMessage("创建失败").show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResultDTO> call, Throwable t) {
                t.printStackTrace();
                progressDialog.dismiss();
                customToast.setMessage("创建失败").show();
            }
        });
    }
}
