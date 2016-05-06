package net.wishwall.activities;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import net.wishwall.R;
import net.wishwall.domain.ResultDTO;
import net.wishwall.service.ApiClient;
import net.wishwall.views.CustomToast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author panRongFu on 2016/5/4.
 * @Description
 * @email pan@ipushan.com
 */
public class FeedBackActivity extends BaseActivity implements View.OnClickListener{
    TextInputEditText mEditText;
    TextView back;
    TextView send;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.feedback);
        initViewUI();
    }

    private void initViewUI() {
        mEditText = (TextInputEditText) findViewById(R.id.user_advise);
        back =(TextView)findViewById(R.id.feedback_back);
        send =(TextView)findViewById(R.id.feedback_send);
        back.setOnClickListener(this);
        send.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.feedback_back:
                finish();
                break;
            case R.id.user_advise:
                sendAdvise();
                break;
        }
    }

    /**
     * 反馈建议
     */
    private void sendAdvise() {
        String advise = mEditText.getText().toString();
        if(TextUtils.isEmpty(advise)){
            CustomToast.showMsg(this,"反馈内容不能为空");
            return;
        }else if( advise.length() >=200){
            CustomToast.showMsg(this,"反馈内容字数不能大于200哦~");
            return;
        }

        ApiClient.addUserAdvise(advise, new Callback<ResultDTO>() {
            @Override
            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                ResultDTO body = response.body();
                if(body.getCode() == 200){
                    CustomToast.showMsg(FeedBackActivity.this,"意见反馈成功,谢谢亲的宝贵建议");
                }else {
                    CustomToast.showMsg(FeedBackActivity.this,"出错了,再试一次");
                }
            }
            @Override
            public void onFailure(Call<ResultDTO> call, Throwable t) {
                CustomToast.showMsg(FeedBackActivity.this,"出错了,再试一次");
            }
        });
    }
}
