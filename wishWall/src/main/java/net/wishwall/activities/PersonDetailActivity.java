package net.wishwall.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.wishwall.App;
import net.wishwall.Constants;
import net.wishwall.R;
import net.wishwall.domain.FriendIdsDTO;
import net.wishwall.domain.ResultDTO;
import net.wishwall.domain.UserDTO;
import net.wishwall.service.ApiClient;
import net.wishwall.utils.SpUtil;
import net.wishwall.views.CustomProgressDialog;
import net.wishwall.views.CustomToast;

import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonDetailActivity extends BaseActivity implements OnClickListener {

    private String logoUrl;
    private String name;
    private String sex;
    private String phone;
    private String bornId;
    private String qq;
    private String wx;
    private String bornYear;
    private String bornMonth;
    private String bornDay;
    private String bornLocal;
    private final int CHANGE = 1;
    private TextView edit;
    private TextView back;
    private TextView title;
    private TextView me_person_detail_change;
    private TextView person_detail_name;
    private TextView person_detail_sex;
    private TextView person_detail_phone;
    private TextView person_detail_qq;
    private TextView person_detail_weixin;
    private RelativeLayout qrcodeLayout;
    private View qrcodeDivide;
    private TextView person_detail_born;
    private TextView person_detail_local;
    private CircleImageView headportrait;
    private Map<String, Object> detailMap;
    private SpUtil userSpUtil;
    private UserInfo userInfo;
    private String friendId;
    String userId;
    private String EDIT="编辑";
    private String ADD="添加";
    private String CHAT="聊天";
    private CustomProgressDialog progressDialog;
    private CustomProgressDialog addFriendDialog;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.person_detail);
        initViewUI();
    }

    private void initViewUI() {
        progressDialog = CustomProgressDialog.createDialog(this);
      //  addFriendDialog = CustomProgressDialog.createDialog(this);
        userSpUtil = new SpUtil(this, Constants.USER_SPUTIL);
        back = (TextView)findViewById(R.id.person_detail_back);
        edit = (TextView)findViewById(R.id.person_detail_edit);
        title =(TextView)findViewById(R.id.person_title);
        headportrait = (CircleImageView) findViewById(R.id.profile_image);
        person_detail_name = (TextView) findViewById(R.id.person_detail_name);
        person_detail_sex = (TextView) findViewById(R.id.person_detail_sex);
        person_detail_phone = (TextView) findViewById(R.id.person_detail_phone);
        person_detail_qq = (TextView) findViewById(R.id.person_detail_qq);
        person_detail_weixin = (TextView) findViewById(R.id.person_detail_weixin);
        qrcodeLayout = (RelativeLayout)findViewById(R.id.qrcode_layout);
        qrcodeDivide = findViewById(R.id.qrcode_divide);
        person_detail_born = (TextView) findViewById(R.id.person_detail_born);
        person_detail_local = (TextView) findViewById(R.id.person_detail_local);
        qrcodeLayout.setOnClickListener(this);
        edit.setOnClickListener(this);
        back.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.person_detail_back:
                this.finish();
                break;
            case R.id.person_detail_edit:
                doWhatByText(edit.getText());
                break;
            case R.id.qrcode_layout:
                startActivity(new Intent(this,GenerateQrcodeActivity.class));
                overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
                break;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadUserInfo();
    }

    /**
     * 加载用户信息
     */
    private void loadUserInfo() {

        userId=userSpUtil.getKeyValue("userId");
        Intent intent = getIntent();
        if(intent.hasExtra("WISH_ITME")){
            friendId = intent.getStringExtra("WISH_ITME");
            getMyFriendIds(friendId);

        }else if(intent.hasExtra("USER")){//会话列表
            userInfo = intent.getParcelableExtra("USER");
            friendId = userInfo.getUserId();
            getMyFriendIds(friendId);

        }else if(intent.hasExtra("CONTACTS_USER")){//通讯录好友
            friendId = intent.getStringExtra("CONTACTS_USER");
            edit.setText(CHAT);
            findUserById(friendId);

        }else if(intent.hasExtra("USER_SEARCH")){//搜索好友
            friendId = intent.getStringExtra("USER_SEARCH");
            edit.setText(ADD);
            findUserById(friendId);

        }else{
            edit.setText(EDIT);
            findUserById(userId);
        }
    }

    /**
     * 根据用户id获取用户信息
     */
    private  void findUserById(String userId){
        progressDialog.setMessage("加载中...").show();
        ApiClient.findUserById(userId, new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                UserDTO body = response.body();
                if(body.getCode() == 200){
                    logoUrl = body.getResult().getIcon();
                    if(!TextUtils.isEmpty(logoUrl)){
                        Picasso.with(PersonDetailActivity.this).load(logoUrl).into(headportrait);
                    }
                    name = body.getResult().getNickname();
                    sex = body.getResult().getSex();
                    phone = body.getResult().getPhone();
                    qq = body.getResult().getQq();
                    wx = body.getResult().getWeixin();
                    bornYear = body.getResult().getBirth_year();
                    bornMonth = body.getResult().getBirth_month();
                    bornDay = body.getResult().getBirth_day();
                    String prov = body.getResult().getProvince();
                    String city = body.getResult().getCity();
                    String area = body.getResult().getArea();

                    person_detail_name.setText(name+"");
                    person_detail_sex.setText(sex+"");
                    person_detail_phone.setText(phone+"");
                    person_detail_qq.setText(qq+"");
                    person_detail_weixin.setText(wx+"");
                    person_detail_born.setText(bornYear+"-"+bornMonth+"-"+bornDay);
                    person_detail_local.setText(prov+" "+city+" "+area);

                }else{
                    CustomToast.showMsg(PersonDetailActivity.this,"信息加载失败");
                }
                progressDialog.dismiss();
            }
            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                t.printStackTrace();
                progressDialog.dismiss();
            }
        });
    }

    /**
     * 获取我朋友的id
     */
    private void getMyFriendIds(final String friendId) {

        ApiClient.findFriendIds(new Callback<FriendIdsDTO>() {
            @Override
            public void onResponse(Call<FriendIdsDTO> call, Response<FriendIdsDTO> response) {
                FriendIdsDTO body = response.body();
                if(body.getCode() == 200){
                    isFriend(body.getResult());
                    findUserById(friendId);
                }
            }
            @Override
            public void onFailure(Call<FriendIdsDTO> call, Throwable t) {
                t.printStackTrace();
                progressDialog.dismiss();
            }
        });
    }

    /**
     * 判断是否与该用户是朋友关系
     */
    private void isFriend(List<FriendIdsDTO.ResultBean> list) {
        if(list !=null){
            for(FriendIdsDTO.ResultBean fr : list){
                if(friendId.equals(fr.getFriendid())){
                    edit.setText(CHAT);
                    break;
                }else {
                    edit.setText(ADD);
                }
            }
        }

        if(userId.equals(friendId)){
            edit.setText(EDIT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHANGE && resultCode == RESULT_OK) {
            if (null != data) {
                Bundle bundle = data.getBundleExtra("changeInfo");
                person_detail_name.setText(bundle.getString("detailName"));
                String sex = bundle.getString("detailSex");
                if (sex.equals("0")) {
                    person_detail_sex.setText("女");
                } else if (sex.equals("1")) {
                    person_detail_sex.setText("男");
                }
                person_detail_qq.setText(bundle.getString("detailQq"));
                person_detail_weixin.setText(bundle.getString("detailWx"));
                person_detail_born.setText(bundle.getString("detailBorn"));
                person_detail_local.setText(bundle.getString("detailLocal"));
                String detaillogo = bundle.getString("detailLogo");
            }
        }
    }

    /**
     * 根据edit的文字做具体的事情
     * @param text
     */
    private void doWhatByText(CharSequence text) {
       if(text.equals(EDIT)){//编辑
           Intent person_detail = new Intent(this, EditPersonDetailActivity.class);
           //        person_detail.putExtra("logoPath", logoPath);
           //        person_detail.putExtra("name", name);
           //        person_detail.putExtra("sex", sex);
           //        person_detail.putExtra("qq", qq);
           //        person_detail.putExtra("wx", wx);
           //        person_detail.putExtra("phone", phone);
           //        person_detail.putExtra("bornId", bornId);
           //        person_detail.putExtra("bornDay", bornDay);
           //        person_detail.putExtra("bornLocal", bornLocal);
           //         startActivityForResult(person_detail, CHANGE);
           startActivity(person_detail);
       }else if(text.equals(ADD)){
           //发送好友请求
           applyAddFriend();
       }else if(text.equals(CHAT)){
           //发送信息开始聊天
           sendMessage();
       }
    }

    /**
     * 发送私聊信息
     */
    private void sendMessage() {
        if (RongIM.getInstance() != null) {
            if (friendId != null)
                ApiClient.findUserById(friendId, new Callback<UserDTO>() {
                    @Override
                    public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                        UserDTO body = response.body();
                        if(body.getCode() == 200){
                            String userName = body.getResult().getUsername();
                            RongIM.getInstance().startPrivateChat(PersonDetailActivity.this, friendId,userName);
                        }
                    }
                    @Override
                    public void onFailure(Call<UserDTO> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
        }
    }

    /**
     * 发送单聊信息
     */
    private void applyAddFriend(){
        addFriendDialog.setMessage("请求中...").show();
        ApiClient.sendMessage(userId, friendId,"请求添加好友", App.ContactNtf,App.Apply, new Callback<ResultDTO>() {
            @Override
            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                ResultDTO body = response.body();
                if(body.getCode() == 200){
                    CustomToast.showMsg(PersonDetailActivity.this,"好友请求发送成功");
                }else{
                    CustomToast.showMsg(PersonDetailActivity.this,"好友请求发送失败");
                }
                addFriendDialog.dismiss();
            }
            @Override
            public void onFailure(Call<ResultDTO> call, Throwable t) {
                t.printStackTrace();
                addFriendDialog.dismiss();
            }
        });
    }
}
