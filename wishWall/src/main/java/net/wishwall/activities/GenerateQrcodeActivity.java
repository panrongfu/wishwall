package net.wishwall.activities;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import net.wishwall.Constants;
import net.wishwall.R;
import net.wishwall.utils.DensityUtil;
import net.wishwall.utils.SpUtil;
import net.wishwall.views.CustomToast;
import net.wishwall.views.RoundImageView;

import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;

/**
 * @author panRongFu on 2016/5/13.
 * @Description
 * @email pan@ipushan.com
 */
public class GenerateQrcodeActivity extends AppCompatActivity {

    private TextView mTextView;
    private TextView mTitle;
    private RoundImageView mRoundImageView;
    private ImageView mQRcodeImageView;
    private SpUtil userSpUtil;
    private int LOADED= 0X01;
    Bitmap logoBitmap = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrcode_card);
        Toolbar toolbar = (Toolbar) findViewById(R.id.qrcode_toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setTitle("");
        ab.setHomeAsUpIndicator(R.mipmap.de_actionbar_back);
        ab.setDisplayHomeAsUpEnabled(true);
        initViewUI();
        preCreateQRCode();
    }
    private void initViewUI() {
        userSpUtil = new SpUtil(this, Constants.USER_SPUTIL);
//        mTextView = (TextView)findViewById(R.id.qrcode_card_user_name);
       mTitle = (TextView)findViewById(R.id.qrcode_card_title);
        mQRcodeImageView = (ImageView)findViewById(R.id.qrcode_card_qrcode_imge);
      //  mRoundImageView = (RoundImageView)findViewById(R.id.qrcode_card_user_logo);
        mTitle.setText(R.string.me_person_detail_qrcode);
    }

    /**
     * 生成二维码，名片
     */
    private void preCreateQRCode() {
 //       String userName = userSpUtil.getKeyValue("userName");
        String userIcon = userSpUtil.getKeyValue("userIcon");
//        mTextView.setText(userName);
//        Picasso.with(this).load(userIcon).into(mRoundImageView);
        if(TextUtils.isEmpty(userIcon)){
            createQRcodeNotLogo();
            return;
        }

        Target mTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                logoBitmap = bitmap;
                createQRcodeWithLogo();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        Picasso.with(this).load(userIcon).into(mTarget);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 生成带logo的二维码
     */
    public void createQRcodeWithLogo(){
        String userId = userSpUtil.getKeyValue("userId");
        QRCodeEncoder.encodeQRCode(
                userId,
                DensityUtil.dip2px(this, 145),
                Color.parseColor("#000000"),
                logoBitmap,
                new QRCodeEncoder.Delegate() {
                    @Override
                    public void onEncodeQRCodeSuccess(Bitmap bitmap) {
                        mQRcodeImageView.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onEncodeQRCodeFailure() {
                        CustomToast.showMsg(GenerateQrcodeActivity.this,"创建失败");
                    }
                });
    }

    /**
     * 生成不带logo的二维码
     */
    public void createQRcodeNotLogo(){
        String userId = userSpUtil.getKeyValue("userId");

        QRCodeEncoder.encodeQRCode(
                userId,
                DensityUtil.dip2px(this, 145),
                Color.parseColor("#000000"),
                new QRCodeEncoder.Delegate() {
                    @Override
                    public void onEncodeQRCodeSuccess(Bitmap bitmap) {
                        mQRcodeImageView.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onEncodeQRCodeFailure() {
                        CustomToast.showMsg(GenerateQrcodeActivity.this,"创建失败");
                    }
                });

    }
}
