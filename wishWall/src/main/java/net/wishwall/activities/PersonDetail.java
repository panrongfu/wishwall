package net.wishwall.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.wishwall.Constants;
import net.wishwall.R;
import net.wishwall.domain.UserDTO;
import net.wishwall.service.ApiClient;
import net.wishwall.utils.SpUtil;
import net.wishwall.views.CustomToast;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonDetail extends BaseActivity implements OnClickListener {

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
    private TextView person_detail_born;
    private TextView person_detail_local;
    private CircleImageView headportrait;
    private Map<String, Object> detailMap;
    private SpUtil userSpUtil;


    @Override
    protected void onCreate(Bundle arg0) {

        super.onCreate(arg0);
        setContentView(R.layout.person_detail);
        initViewUI();
    }

    private void initViewUI() {
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
        person_detail_born = (TextView) findViewById(R.id.person_detail_born);
        person_detail_local = (TextView) findViewById(R.id.person_detail_local);
        edit.setOnClickListener(this);
        back.setOnClickListener(this);

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
        String userId = userSpUtil.getKeyValue("userId");
        Intent intent = getIntent();
        String itemUserId = intent.getStringExtra("itemUserId");
        String itemUserName = intent.getStringExtra("itemUserName");
        if (TextUtils.isEmpty(itemUserId)){
            edit.setVisibility(View.VISIBLE);
            title.setText(R.string.personal_title);
        }else{
            userId = itemUserId;
            edit.setVisibility(View.GONE);
            title.setText(itemUserName);
        }
        ApiClient.findUserById(userId, new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                UserDTO body = response.body();
                if(body.getCode() == 200){
                    logoUrl = body.getResult().getIcon();
                    if(null != logoUrl && !"".equals(logoUrl)){
                        Picasso.with(PersonDetail.this).load(logoUrl).into(headportrait);
                    }
                    name = body.getResult().getUsername();
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
                    CustomToast.showMsg(PersonDetail.this,"信息加载失败");
                }
            }
            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                t.printStackTrace();
                CustomToast.showMsg(PersonDetail.this,"信息加载失败");
            }
        });
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.person_detail_back:
                this.finish();
                break;
            case R.id.person_detail_edit:
                  Intent person_detail = new Intent(this, EditPersonDetail.class);
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
                break;
        }
    }
}
