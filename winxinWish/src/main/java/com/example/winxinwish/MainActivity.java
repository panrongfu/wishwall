package com.example.winxinwish;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import cn.sharesdk.framework.ShareSDK;
import onekeyshare.OnekeyShare;

public class MainActivity extends Activity implements View.OnClickListener {
    private ImageView mPhoto;
    private Button mShare;
    private EditText mWord;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 初始化ShareSDK
        initView();
    }

    public void initView(){
        mPhoto = (ImageView) findViewById(R.id.id_image);
        mShare = (Button) findViewById(R.id.id_share);
        mWord = (EditText) findViewById(R.id.id_word);
        mPhoto.setOnClickListener(this);
        mShare.setOnClickListener(this);
        //设置自定义字体
        mWord.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/test.ttf"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_image:
                Intent intent = new Intent(Intent.ACTION_PICK,null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                startActivityForResult(intent, 100);
                break;
            case R.id.id_share:
                showShare(this,null,true);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK&& requestCode == 100){
            if(data !=null){
                mPhoto.setImageURI(data.getData());
            }
        }
    }

    /**
     * 演示调用ShareSDK执行分享
     *
     * @param context
     * @param platformToShare  指定直接分享平台名称（一旦设置了平台名称，则九宫格将不会显示）
     * @param showContentEdit  是否显示编辑页
     */
    public static void showShare(Context context, String platformToShare, boolean showContentEdit) {
        ShareSDK.initSDK(context);
//        OnekeyShare oks = new OnekeyShare();
//        oks.setSilent(!showContentEdit);
//        if (platformToShare != null) {
//            oks.setPlatform(platformToShare);
//        }
//        //ShareSDK快捷分享提供两个界面第一个是九宫格 CLASSIC  第二个是SKYBLUE
//        oks.setTheme(OnekeyShareTheme.CLASSIC);
//        // 令编辑页面显示为Dialog模式
//        oks.setDialogMode();
//        // 在自动授权时可以禁用SSO方式
//        oks.disableSSOWhenAuthorize();
//        //oks.setAddress("12345678901"); //分享短信的号码和邮件的地址
//        oks.setTitle("分享标题--Title");
//        oks.setTitleUrl("http://mob.com");
//        //oks.setText("分享测试文--Text");
//        oks.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
//        oks.setUrl("http://www.baidu.com");
//        oks.setText("百度下");
//        oks.setSite("wo ");
//        oks.setSiteUrl("http://www.baidu.com");
        //oks.setFilePath("/sdcard/test-pic.jpg");  //filePath是待分享应用程序的本地路劲，仅在微信（易信）好友和Dropbox中使用，否则可以不提供
        // oks.setVenueDescription("This is a beautiful place!");
        OnekeyShare oks=new OnekeyShare();
        oks.setTitle("ssss");
        oks.setTitleUrl("http://www.baidu.com");
        oks.setImageUrl("https://pic3.zhimg.com/2928ce5f9a729958171dca76aefd1d02_b.jpg");
        oks.setUrl("http://www.baidu.com");
        oks.setText("百度下");
        oks.setSite("wo ");
        oks.setSiteUrl("http://www.baidu.com");

        oks.show(context);

    }
}
