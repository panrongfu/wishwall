package org.ecfex.app.teamwork.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

import org.ecfex.app.teamwork.R;
import org.ecfex.app.teamwork.fragments.AppFragment;
import org.ecfex.app.teamwork.fragments.HomeFragment;
import org.ecfex.app.teamwork.fragments.MeFragment;
import org.ecfex.app.teamwork.utils.SpUtil;


/**
 * @author panRongFu on 2016/4/22.
 * @Description
 * @email pan@ipushan.com
 */
public class MainActivity extends AppCompatActivity
                implements AHBottomNavigation.OnTabSelectedListener
                            ,NavigationView.OnNavigationItemSelectedListener{

    private Fragment currentFragment;
    private Toolbar toolbar;
    private ActionBar ab;
    private TextView toolbarTitle;
    private Menu mMenu;
    public static AHBottomNavigation bottomNavigation;
    public static  int index;
    private SpUtil localSpUtil;

    @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        initToolbar();
        initViewUI();
  }
    private void initToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ab = getSupportActionBar();
       // ab.setHomeAsUpIndicator(R.mipmap.xiangxia);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void initViewUI() {
        // currentFragment = new FindFragment();
        localSpUtil = new SpUtil(this,"location");
        toolbarTitle =(TextView)findViewById(R.id.toolbar_title);
        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnTabSelectedListener(this);

        // Create items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("应用", R.mipmap.app,android.R.color.white);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("首页", R.mipmap.home, android.R.color.white);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("我的", R.mipmap.me, android.R.color.white);

      // Add items
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);

        // Set background color
     //   bottomNavigation.setDefaultBackgroundColor(Color.WHITE);
        // Disable the translation inside the CoordinatorLayout
        bottomNavigation.setBehaviorTranslationEnabled(false);

        // Change colors
        bottomNavigation.setAccentColor(Color.parseColor("#5b25eb"));
        bottomNavigation.setInactiveColor(Color.parseColor("#747474"));

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

    @Override
    public void onTabSelected(int position, boolean wasSelected) {
      index = position;
    switch (position){
        case 0:
           showFragment(new AppFragment());
            break;
        case 1:
            showFragment(new HomeFragment());
            break;
        case 2:
            showFragment(new MeFragment());
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
        toolbar.setVisibility(View.VISIBLE);
        if(fragment instanceof AppFragment){
            toolbarTitle.setText("应用");
        }else if(fragment instanceof HomeFragment){
            toolbarTitle.setText("首页");
        }else if(fragment instanceof MeFragment){
            toolbarTitle.setText("我的");
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
    public boolean onNavigationItemSelected(MenuItem item) {

        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        switch (index){
            case 1:

                break;
            case 2:

                break;
            case 3:

                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
