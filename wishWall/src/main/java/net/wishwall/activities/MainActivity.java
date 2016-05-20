package net.wishwall.activities;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

import net.wishwall.Constants;
import net.wishwall.R;
import net.wishwall.fragments.FindFragment;
import net.wishwall.fragments.MoreFragment;
import net.wishwall.fragments.MsgFragment;
import net.wishwall.fragments.MeFragment;
import net.wishwall.rong.activity.ContactsActivity;
import net.wishwall.rong.activity.CreateGroup;
import net.wishwall.rong.activity.FriendListActivity;
import net.wishwall.rong.activity.SelectGroupActivity;
import net.wishwall.utils.DensityUtil;
import net.wishwall.utils.SpUtil;
import net.wishwall.views.CustomToast;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import io.codetail.widget.RevealFrameLayout;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

/**
 * @author panRongFu on 2016/4/22.
 * @Description
 * @email pan@ipushan.com
 */
public class MainActivity extends AppCompatActivity implements AHBottomNavigation.OnTabSelectedListener
        ,NavigationView.OnNavigationItemSelectedListener, AMapLocationListener,View.OnClickListener {

    public static String cityName;
    private String location;
    private Fragment currentFragment;
    private FindFragment findFragment;
    private MeFragment meFragment;
    private Toolbar toolbar;
    private ActionBar ab;
    private TextView toolbarTitle;
    private Menu mMenu;
    public static AHBottomNavigation bottomNavigation;
    public static  int index;
    private static final int SELECT_CITY=0;
    private SpUtil localSpUtil;
    private OnSelectCityListener mListener;
    public AMapLocationClient mLocationClient = null;
    public AMapLocationClientOption mLocationOption = null;
    private OnLocationFinishListener mlocatinListener;
    private static  boolean isLocation=true;
    private static int currentIndex;
    public static View mainActiviyView;

    RevealFrameLayout mRevealFrameLayout;
    private SupportAnimator mAnimator;
    CardView mCardView ;
    ImageView mArrows;
    ImageView mSearch;
    EditText mSearchEditText;

    FrameLayout mFramelayout;
    private OnSearchListener mSearchListener;

    @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        mainActiviyView = LayoutInflater.from(this).inflate(R.layout.main_activity,null);
        initToolbar();
        initViewUI();
        initLocation();
        connectRongCloud();
  }

    private void initToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ab = getSupportActionBar();
    }

    private void initViewUI() {
        // currentFragment = new FindFragment();
        localSpUtil = new SpUtil(this,"location");
        mFramelayout = (FrameLayout)findViewById(R.id.toolbar_framelayout);
        mRevealFrameLayout = (RevealFrameLayout)findViewById(R.id.main_revealFrameLayout);
        mCardView = (CardView)findViewById(R.id.main_card_search);
        mArrows = (ImageView)findViewById(R.id.main_arrows);
        mSearch = (ImageView)findViewById(R.id.main_search_wish);
        mSearchEditText = (EditText)findViewById(R.id.main_edit_text_search);
        toolbarTitle =(TextView)findViewById(R.id.toolbar_title);
        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnTabSelectedListener(this);
        mArrows.setOnClickListener(this);
        mSearch.setOnClickListener(this);

        // Create items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("信息", R.mipmap.message, R.color.de_action_white);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("发现", R.mipmap.find, R.color.de_action_white);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("我的", R.mipmap.home, R.color.de_action_white);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem("更多", R.mipmap.more, R.color.de_action_white);

      // Add items
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
        bottomNavigation.addItem(item4);

        // Set background color
     //   bottomNavigation.setDefaultBackgroundColor(Color.WHITE);
        // Disable the translation inside the CoordinatorLayout
        bottomNavigation.setBehaviorTranslationEnabled(false);

        // Change colors
        bottomNavigation.setAccentColor(Color.parseColor("#5d1bcf"));
        bottomNavigation.setInactiveColor(Color.parseColor("#585692"));

        // Force to tint the drawable (useful for font with icon for example)
        bottomNavigation.setForceTint(true);

        // Force the titles to be displayed (against Material Design guidelines!)
        bottomNavigation.setForceTitlesDisplay(true);


        // Use colored navigation with circle reveal effect
    //    bottomNavigation.setColored(true);
    //    bottomNavigation.setBehaviorTranslationEnabled(false);
    //      bottomNavigation.setBackgroundColor(Color.WHITE);
    //


    // Set current item programmatically
    bottomNavigation.setCurrentItem(1);

    // Customize notification (title, background, typeface)
    bottomNavigation.setNotificationBackgroundColor(Color.parseColor("#F63D2B"));

    // Add or remove notification for each item

    bottomNavigation.setNotification(1, 0);
  }

    /**
     * 初始化定位
     */
    private void initLocation() {
        String cityName = localSpUtil.getKeyValue("cityName");
        if(TextUtils.isEmpty(location)){
            //初始化定位
            mLocationClient = new AMapLocationClient(getApplicationContext());
            //设置定位回调监听
            mLocationClient.setLocationListener(this);
            //声明mLocationOption对象

            //初始化定位参数
            mLocationOption = new AMapLocationClientOption();
            //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置是否返回地址信息（默认返回地址信息）
            mLocationOption.setNeedAddress(true);
            //设置是否只定位一次,默认为false
            mLocationOption.setOnceLocation(false);
            //设置是否强制刷新WIFI，默认为强制刷新
            mLocationOption.setWifiActiveScan(true);
            //设置是否允许模拟位置,默认为false，不允许模拟位置
            mLocationOption.setMockEnable(false);
            //设置定位间隔,单位毫秒,默认为2000ms
            mLocationOption.setInterval(2000);
            //给定位客户端对象设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            //启动定位
            mLocationClient.startLocation();
        }else {
            ab.setTitle(cityName);
        }
    }

    /**
     * 链接到融云
     */
    private void connectRongCloud() {
        SpUtil userSpUtil = new SpUtil(this, Constants.USER_SPUTIL);
        String token = userSpUtil.getKeyValue("token");
        if(TextUtils.isEmpty(token)) return;
        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {

            }
            @Override
            public void onSuccess(String s) {
                Log.i("onSuccess>", "userId:" + s);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                amapLocation.getLatitude();//获取纬度
                amapLocation.getLongitude();//获取经度
                amapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());
                df.format(date);//定位时间
                amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                amapLocation.getCountry();//国家信息
                String gpsProvince =amapLocation.getProvince();//省信息
                String gpsCity = amapLocation.getCity();//城市信息
                String gpsArea = amapLocation.getDistrict();//城区信息
                String gpsStreet = amapLocation.getStreet();//街道信息
                amapLocation.getStreetNum();//街道门牌号信息
                amapLocation.getCityCode();//城市编码
                amapLocation.getAdCode();//地区编码
                //amapLocation.getAOIName();//获取当前定位点的AOI信息
               // gps_local.setText(gpsProvince+gpsCity+area+street);
                location = gpsProvince +gpsCity+gpsArea+gpsStreet;
                cityName = amapLocation.getCity();
                localSpUtil.setKeyValue("cityName",cityName);
                ab.setTitle(cityName);
                if(mlocatinListener != null && isLocation){
                    mlocatinListener.finish(cityName);
                    isLocation = false;
                    mLocationClient.onDestroy();
                }
            }else{
                //如果定位不成功默认显示广州市的许愿条
                localSpUtil.setKeyValue("cityName",Constants.DEFALUT_CITY);
                ab.setTitle(Constants.DEFALUT_CITY);
                if(mlocatinListener != null){
                    mlocatinListener.finish(Constants.DEFALUT_CITY);
                    isLocation = false;
                    mLocationClient.onDestroy();
                }
            }
        }else{
            //如果定位不成功默认显示广州市的许愿条
            localSpUtil.setKeyValue("cityName",Constants.DEFALUT_CITY);
            ab.setTitle(Constants.DEFALUT_CITY);
            if(mlocatinListener != null){
                mlocatinListener.finish(Constants.DEFALUT_CITY);
                isLocation = false;
                mLocationClient.onDestroy();
            }
        }
    }

    @Override
    public void onTabSelected(int position, boolean wasSelected) {
      index = position;
      if(currentIndex == index) return ;
       currentIndex = index;
    switch (position){
        case 0:
            showFragment(new MsgFragment());
            break;
        case 1:
            showFragment(new FindFragment());
            break;
        case 2:
            showFragment(new MeFragment());
            break;
        case 3:
            showFragment(new MoreFragment());
            break;
    }
  }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.mMenu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Intent intent =new Intent(this,SelectProvCityActivity.class);
                intent.putExtra("location",location);
                startActivityForResult(intent,SELECT_CITY);
                 break;

            case R.id.menu_search:
                mCardView.setVisibility(View.VISIBLE);
                mRevealFrameLayout.setVisibility(View.VISIBLE);
                mSearchEditText.setFocusable(true);
                mSearchEditText.setFocusableInTouchMode(true);
                mSearchEditText.requestFocus();

                CustomToast.showMsg(this,"宽:"+mCardView.getWidth()+"高:"+mCardView.getHeight());
                mAnimator = ViewAnimationUtils.createCircularReveal(mCardView,mCardView.getWidth(), mCardView.getHeight()/2, 0, 3000);
                mAnimator.setInterpolator(new AccelerateInterpolator());
                mAnimator.setDuration(500);
                mAnimator.addListener(new SupportAnimator.AnimatorListener() {
                    @Override
                    public void onAnimationStart() {
                        ObjectAnimator aninimator = ObjectAnimator.ofFloat(mArrows, "translationX", mCardView.getWidth()/2,
                                DensityUtil.px2dip(MainActivity.this,5));
                        aninimator.setInterpolator(new AccelerateInterpolator());
                        aninimator.setDuration(300);
                        aninimator.start();
                    }

                    @Override
                    public void onAnimationEnd() {
                        ObjectAnimator sAnimator = ObjectAnimator.ofFloat(mSearch,"alpha",0,1,0,1);
                        sAnimator.setInterpolator(new AccelerateInterpolator());
                        sAnimator.setDuration(1000);
                        sAnimator.start();
                    }

                    @Override
                    public void onAnimationCancel() {

                    }

                    @Override
                    public void onAnimationRepeat() {

                    }
                });
                mAnimator.start();
                break;

            case R.id.menu_add:
                CustomToast.showMsg(this,"更多");
                break;

            case R.id.add_item1://发起聊天
                startActivity(new Intent(this, FriendListActivity.class));
                break;

            case R.id.add_item2://选择群组
               // if (RongIM.getInstance() != null)
                //    RongIM.getInstance().startSubConversationList(this, Conversation.ConversationType.GROUP);
                startActivity(new Intent(this, SelectGroupActivity.class));
                break;

            case R.id.add_item3://创建
                startActivity(new Intent(this, CreateGroup.class));

                break;

            case R.id.add_item4://通讯录
                startActivity(new Intent(this, ContactsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 要显示的界面
     * @param fragment
     */
    private void showFragment(Fragment fragment) {
        this.getWindow().invalidatePanelMenu(Window.FEATURE_OPTIONS_PANEL);
        invalidateOptionsMenu();
        mCardView.setVisibility(View.INVISIBLE);
        toolbar.setVisibility(View.VISIBLE);
        mFramelayout.setVisibility(View.VISIBLE);
        mRevealFrameLayout.setVisibility(View.INVISIBLE);
        if(fragment instanceof MsgFragment){
            toolbarTitle.setText("信息");
            ab.setTitle("");
            ab.setDisplayHomeAsUpEnabled(false);
            //ab.setHomeAsUpIndicator(null);
        }else if(fragment instanceof MeFragment){
            toolbar.setVisibility(View.GONE);
            mFramelayout.setVisibility(View.GONE);
        }else if(fragment instanceof FindFragment){
            ab.setHomeAsUpIndicator(R.mipmap.xiangxia);
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(cityName);
            toolbarTitle.setText("发现");
        }else if(fragment instanceof MoreFragment){
            ab.setTitle("");
            ab.setDisplayHomeAsUpEnabled(false);
            toolbarTitle.setText("更多");
        }
        FragmentTransaction transaction= getSupportFragmentManager().beginTransaction();
        if (currentFragment == fragment)
            return;
        if (!fragment.isAdded()) {
            // 如果当前fragment未被添加，则添加到Fragment管理器中
            // transaction.hide(currentFragment).add(R.id.content_frame, fragment).commitAllowingStateLoss();
            transaction.replace(R.id.content_frame, fragment).commitAllowingStateLoss();
        } else {
             //transaction.hide(currentFragment).show(fragment).commitAllowingStateLoss();
            transaction.replace(R.id.content_frame, fragment).commitAllowingStateLoss();
            currentFragment = fragment;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.main_arrows:
                if(mAnimator !=null&&!mAnimator.isRunning()){
                    mAnimator = mAnimator.reverse();
                    mAnimator.setDuration(1000);
                    mAnimator.addListener(new SupportAnimator.SimpleAnimatorListener() {
                        @Override
                        public void onAnimationEnd() {
                            mAnimator = null;
                            mCardView.setVisibility(View.GONE);
                            mRevealFrameLayout.setVisibility(View.GONE);
                        }
                    });
                    mAnimator.start();
                }
                break;
            case R.id.main_search_wish:
                if(mSearchListener !=null){
                    String searchName =mSearchEditText.getText().toString();
                    mSearchListener.search(searchName);
                }
                break;
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        switch (index){
            case 0:
                menu.findItem(R.id.action_add_conversation).setVisible(true);
                break;
            case 1:
                menu.findItem(R.id.menu_search).setVisible(true);
                break;
            case 2:
                menu.findItem(R.id.menu_add).setVisible(true);
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case SELECT_CITY:
                     cityName = data.getStringExtra("cityName");
                     ab.setTitle(cityName);
                    if(mListener !=null ){
                        mListener.finish(cityName);
                    }
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.onDestroy();
    }

    public void setOnSelectCityListener(OnSelectCityListener listener){
        this.mListener = listener;
    }


    public void setOnLocationFinishListener(OnLocationFinishListener listener){
        this.mlocatinListener = listener;
    }

    public void setOnSearchListener(OnSearchListener l){
        this.mSearchListener = l;
    }

    /**
     * 城市选择回调接口
     */
    public interface OnSelectCityListener {
        void finish(String cityName);
    }

    /**
     * 定位回调接口
     */
    public interface OnLocationFinishListener{
        void finish(String name);
    }

    /**
     * 搜索回调接口
     */
    public interface OnSearchListener{
        void search(String content);
    }
}
