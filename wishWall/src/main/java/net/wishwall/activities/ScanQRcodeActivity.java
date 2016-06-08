package net.wishwall.activities;

import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import net.wishwall.Constants;
import net.wishwall.R;
import net.wishwall.domain.ResultDTO;
import net.wishwall.service.ApiClient;
import net.wishwall.utils.SpUtil;
import net.wishwall.views.CustomToast;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author panRongFu on 2016/5/13.
 * @Description
 * @email pan@ipushan.com
 */
public class ScanQRcodeActivity extends AppCompatActivity implements QRCodeView.Delegate{

    private ZXingView mQRCodeView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_qrcode);
        Toolbar toolbar = (Toolbar) findViewById(R.id.scan_toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setTitle("");
        ab.setHomeAsUpIndicator(R.mipmap.de_actionbar_back);
        ab.setDisplayHomeAsUpEnabled(true);
        intViewUI();
    }

    private void intViewUI() {

        try {
            mQRCodeView = (ZXingView) findViewById(R.id.zxingview);
            mQRCodeView.setResultHandler(this);
            mQRCodeView.startSpot();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        CustomToast.showMsg(this,result);
        vibrate();
        mQRCodeView.startSpot();
        applyAddFriend(result);
    }

    @Override
    public void onScanQRCodeOpenCameraError() {

    }

    /**
     * 发送单聊信息(请求添加好友)
     */
    private void applyAddFriend(String friendId){
        String userId = new SpUtil(this, Constants.USER_SPUTIL).getKeyValue("userId");
        ApiClient.sendMessage(userId, friendId, "请求添加好友", Constants.ContactNtf,Constants.Apply, new Callback<ResultDTO>() {
            @Override
            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                ResultDTO body = response.body();
                if(body.getCode() == 200){
                    CustomToast.showMsg(ScanQRcodeActivity.this,body.getResult());
                }else{
                    CustomToast.showMsg(ScanQRcodeActivity.this,"请求失败");
                }
            }
            @Override
            public void onFailure(Call<ResultDTO> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        mQRCodeView.startCamera();
    }

    @Override
    protected void onStop() {
        mQRCodeView.stopCamera();
        super.onStop();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }
}
