package net.wishwall.fragments;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.wishwall.Constants;
import net.wishwall.R;
import net.wishwall.activities.MainActivity;
import net.wishwall.rong.activity.ContactsActivity;
import net.wishwall.rong.activity.CreateGroup;
import net.wishwall.rong.activity.FriendListActivity;
import net.wishwall.rong.adapter.ConversationListAdapterEx;
import net.wishwall.rong.fragment.ChatRoomListFragment;
import net.wishwall.rong.fragment.GroupListFragment;
import net.wishwall.utils.SpUtil;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * @author panRongFu on 2016/5/3.
 * @Description
 * @email pan@ipushan.com
 */
public class MsgFragment extends Fragment implements View.OnClickListener,
        ViewPager.OnPageChangeListener, ActionBar.OnMenuVisibilityListener{

    public static final String ACTION_DMEO_RECEIVE_MESSAGE = "action_demo_receive_message";
    public static final String ACTION_DMEO_AGREE_REQUEST = "action_demo_agree_request";
    private boolean hasNewFriends = false;
    private Menu mMenu;
    private ReceiveMessageBroadcastReciver mBroadcastReciver;
    private TextView toolbarTitle;

    ActivityManager activityManager;
    private SpUtil userSpUtil;
    MainActivity mainActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.de_ac_main,container,false);
        initViewUI(view);
        return view;
    }

    private void initViewUI(View view) {
        userSpUtil = new SpUtil(getActivity(), Constants.USER_SPUTIL);
       // connect2RongIM();
        RongIM.getInstance().enableNewComingMessageIcon(true);
        RongIM.getInstance().enableUnreadMessageIcon(true);
        mFragmentManager = getActivity().getSupportFragmentManager();
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitle("");
//        setSupportActionBar(toolbar);
//        final ActionBar ab = getSupportActionBar();
//        // ab.setTitle(getResources().getString(R.string.app_name));
//        ab.setHomeAsUpIndicator(R.mipmap.back);
//        ab.setDisplayHomeAsUpEnabled(true);
        DisplayMetrics dm = new DisplayMetrics();
        mainActivity.getWindowManager().getDefaultDisplay().getMetrics(dm); // 获取屏幕信息
        indicatorWidth = dm.widthPixels / 3;// 指示器宽度为屏幕宽度的3/1
//        toolbarTitle = (TextView)view.findViewById(R.id.toolbar_title);
        mMainShow = (LinearLayout) view.findViewById(R.id.main_show);
        mMainConversationLiner = (RelativeLayout) view.findViewById(R.id.main_conversation_liner);
        mMainGroupLiner = (RelativeLayout) view.findViewById(R.id.main_group_liner);
        mMainChatroomLiner = (RelativeLayout)view. findViewById(R.id.main_chatroom_liner);
        mMainConversationTv = (TextView) view.findViewById(R.id.main_conversation_tv);
        mMainGroupTv = (TextView) view.findViewById(R.id.main_group_tv);
        mMainChatroomTv = (TextView) view.findViewById(R.id.main_chatroom_tv);
        mViewPager = (ViewPager) view.findViewById(R.id.main_viewpager);
        mMainSelectImg = (ImageView) view.findViewById(R.id.main_switch_img);
        mUnreadNumView = (TextView) view.findViewById(R.id.de_num);
        ViewGroup.LayoutParams cursor_Params = mMainSelectImg.getLayoutParams();
        cursor_Params.width = indicatorWidth;// 初始化滑动下标的宽
        mMainSelectImg.setLayoutParams(cursor_Params);
        // 获取布局填充器
        mInflater = (LayoutInflater) mainActivity .getSystemService(mainActivity.LAYOUT_INFLATER_SERVICE);

        activityManager = (ActivityManager) mainActivity.getSystemService(mainActivity.ACTIVITY_SERVICE);
        mMainChatroomLiner.setOnClickListener(this);
        mMainConversationLiner.setOnClickListener(this);
        mMainGroupLiner.setOnClickListener(this);
        mFragmentPagerAdapter = new FragmentPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mFragmentPagerAdapter);
        mViewPager.setOnPageChangeListener(this);
        initData();

    }


    /**
     * 链接到融云服务器
     */
    private void connect2RongIM() {
        String token = userSpUtil.getKeyValue("token");
        if("".equals(token) || token == null) return;
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

    protected void initData() {
       // toolbarTitle.setText("信息");
        final Conversation.ConversationType[] conversationTypes = {Conversation.ConversationType.PRIVATE, Conversation.ConversationType.DISCUSSION,
                Conversation.ConversationType.GROUP, Conversation.ConversationType.SYSTEM,
                Conversation.ConversationType.PUBLIC_SERVICE, Conversation.ConversationType.APP_PUBLIC_SERVICE};

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                RongIM.getInstance().setOnReceiveUnreadCountChangedListener(mCountListener, conversationTypes);
            }
        }, 500);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_DMEO_RECEIVE_MESSAGE);
        if (mBroadcastReciver == null) {
            mBroadcastReciver = new ReceiveMessageBroadcastReciver();
        }
        getActivity().registerReceiver(mBroadcastReciver, intentFilter);
        getConversationPush();
        getPushMessage();
    }

    /**
     *
     */
    private void getConversationPush() {
//        if (getIntent() != null && getIntent().hasExtra("PUSH_CONVERSATIONTYPE") && getIntent().hasExtra("PUSH_TARGETID")) {
//            final String conversationType = getIntent().getStringExtra("PUSH_CONVERSATIONTYPE");
//            final String targetId = getIntent().getStringExtra("PUSH_TARGETID");
//            if (RongIM.getInstance() != null && RongIM.getInstance().getRongIMClient() != null) {
//                RongIM.getInstance().getRongIMClient().getConversation(Conversation.ConversationType.valueOf(conversationType), targetId, new RongIMClient.ResultCallback<Conversation>() {
//                    @Override
//                    public void onSuccess(Conversation conversation) {
//                        if (conversation != null) {
//                            if (conversation.getLatestMessage() instanceof ContactNotificationMessage) {
//                                startActivity(new Intent(getActivity(), NewFriendListActivity.class));
//                            } else {
//                                Uri uri = Uri.parse("rong://" + App.packageName).buildUpon().appendPath("conversation")
//                                        .appendPath(conversationType).appendQueryParameter("targetId", targetId).build();
//                                Intent intent = new Intent(Intent.ACTION_VIEW);
//                                intent.setData(uri);
//                                startActivity(intent);
//                            }
//                        }
//                    }
//                    @Override
//                    public void onError(RongIMClient.ErrorCode e) {
//                    }
//                });
//            }
//        }
   }

    /**
     * 得到不落地 push 消息
     */
    private void getPushMessage() {

        Intent intent = mainActivity.getIntent();
        if (intent != null && intent.getData() != null && intent.getData().getScheme().equals("rong")) {
            String content = intent.getData().getQueryParameter("pushContent");
            String data = intent.getData().getQueryParameter("pushData");
            String id = intent.getData().getQueryParameter("pushId");
            RongIMClient.recordNotificationEvent(id);
            Log.e("RongPushActivity", "--content--" + content + "--data--" + data + "--id--" + id);
            if (RongIM.getInstance() != null && RongIM.getInstance().getRongIMClient() != null) {
                RongIM.getInstance().getRongIMClient().clearNotifications();
            }
            if (RongIM.getInstance() != null && RongIM.getInstance().getRongIMClient() != null) {
                RongIMClient.ConnectionStatusListener.ConnectionStatus status = RongIM.getInstance().getRongIMClient().getCurrentConnectionStatus();
                if (RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED.equals(status)) {
                    return;
                } else if (RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTING.equals(status)) {
                    return;
                }
            }
        }
    }


    public RongIM.OnReceiveUnreadCountChangedListener mCountListener = new RongIM.OnReceiveUnreadCountChangedListener() {
        @Override
        public void onMessageIncreased(int count) {
            if (count == 0) {
                mUnreadNumView.setVisibility(View.GONE);
            } else if (count > 0 && count < 100) {
                mUnreadNumView.setVisibility(View.VISIBLE);
                mUnreadNumView.setText(count + "");
            } else {
                mUnreadNumView.setVisibility(View.VISIBLE);
                mUnreadNumView.setText(R.string.no_read_message);
            }
        }
    };

    @Override
    public void onMenuVisibilityChanged(boolean b) {

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        MenuInflater inflater = getMenuInflater();
//        this.mMenu = menu;
//
//        inflater.inflate(R.menu.de_main_menu, menu);
//        if (hasNewFriends) {
//            mMenu.getItem(0).setIcon(getResources().getDrawable(R.mipmap.de_ic_add_hasmessage));
//            mMenu.getItem(0).getSubMenu().getItem(2).setIcon(getResources().getDrawable(R.mipmap.de_btn_main_contacts_select));
//        } else {
//            mMenu.getItem(0).setIcon(getResources().getDrawable(R.mipmap.de_ic_add));
//            mMenu.getItem(0).getSubMenu().getItem(2).setIcon(getResources().getDrawable(R.mipmap.de_btn_main_contacts));
//        }
//
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_item1://发起聊天
                startActivity(new Intent(getActivity(), FriendListActivity.class));
                break;

            case R.id.add_item2://选择群组
                if (RongIM.getInstance() != null)
                    RongIM.getInstance().startSubConversationList(getActivity(), Conversation.ConversationType.GROUP);

                break;
            case R.id.add_item3://创建
                startActivity(new Intent(getActivity(), CreateGroup.class));

                break;

            case R.id.add_item4://通讯录
                startActivity(new Intent(getActivity(), ContactsActivity.class));
                break;
            case android.R.id.home:
                MainActivity.bottomNavigation.setCurrentItem(MainActivity.index);
               // ChatFragmentActivity.get.finish();

                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int i) {

        switch (i) {
            case 0:
                selectNavSelection(0);
                break;
            case 1:
                selectNavSelection(1);
                break;
            case 2:
                selectNavSelection(2);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    private class FragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {

        public FragmentPagerAdapter(FragmentManager fm)

        {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = null;
            switch (i) {
                case 0:
                    mMainConversationTv.setTextColor(getResources().getColor(R.color.de_title_bg));
                    //TODO
                    if (mConversationFragment == null) {
                        ConversationListFragment listFragment = ConversationListFragment.getInstance();
                        listFragment.setAdapter(new ConversationListAdapterEx(RongContext.getInstance()));
                        Uri uri = Uri.parse("rong://" + getActivity().getApplicationInfo().packageName).buildUpon()
                                .appendPath("conversationlist")
                                .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话是否聚合显示
                                .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")//群组
                                .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")//讨论组
                                .appendQueryParameter(Conversation.ConversationType.PUBLIC_SERVICE.getName(), "false")//公共服务号
                                .appendQueryParameter(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName(), "false")//公共服务号
                                .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//系统
                                .build();
                        listFragment.setUri(uri);
                        fragment = listFragment;
                    } else {
                        fragment = mConversationFragment;
                    }
                    break;
                case 1:
                    if (mGroupListFragment == null) {
                        mGroupListFragment = new GroupListFragment();
                    }
                    fragment = mGroupListFragment;
                    break;

                case 2:
                    if (mChatroomFragment == null) {
                        fragment = new ChatRoomListFragment();
                    } else {
                        fragment = mChatroomFragment;
                    }
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    private void selectNavSelection(int index) {
        clearSelection();
        switch (index) {
            case 0:
                mMainConversationTv.setTextColor(getResources().getColor(R.color.de_title_bg));
                TranslateAnimation animation = new TranslateAnimation(0, 0, 0f, 0f);
                animation.setInterpolator(new LinearInterpolator());
                animation.setDuration(100);
                animation.setFillAfter(true);
                mMainSelectImg.startAnimation(animation);

                break;
            case 1:
                mMainGroupTv.setTextColor(getResources().getColor(R.color.de_title_bg));
                TranslateAnimation animation1 = new TranslateAnimation(
                        indicatorWidth, indicatorWidth, 0f, 0f);
                animation1.setInterpolator(new LinearInterpolator());
                animation1.setDuration(100);
                animation1.setFillAfter(true);
                mMainSelectImg.startAnimation(animation1);

                break;
            case 2:
                mMainChatroomTv.setTextColor(getResources().getColor(R.color.de_title_bg));
                TranslateAnimation animation2 = new TranslateAnimation(
                        2 * indicatorWidth, indicatorWidth * 2,0f, 0f);
                animation2.setInterpolator(new LinearInterpolator());
                animation2.setDuration(100);
                animation2.setFillAfter(true);
                mMainSelectImg.startAnimation(animation2);

                break;
        }
    }

    private void clearSelection() {
        mMainConversationTv.setTextColor(getResources().getColor(R.color.black_textview));
        mMainGroupTv.setTextColor(getResources().getColor(R.color.black_textview));
        mMainChatroomTv.setTextColor(getResources().getColor(R.color.black_textview));
    }

    private class ReceiveMessageBroadcastReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //收到好友添加的邀请，需要更新 Actionbar
            if (action.equals(ACTION_DMEO_RECEIVE_MESSAGE)) {
                hasNewFriends = intent.getBooleanExtra("has_message", false);
                //supportInvalidateOptionsMenu();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_conversation_liner:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.main_group_liner:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.main_chatroom_liner:
                mViewPager.setCurrentItem(2);
                break;
        }
    }


//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//
//        if (KeyEvent.KEYCODE_BACK == event.getKeyCode()) {
//            MainActivity.bottomNavigation.setCurrentItem(MainActivity.index);
//            ChatFragmentActivity.this.finish();
//        }
//
//        return false;
//    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mBroadcastReciver != null) {
            getActivity().unregisterReceiver(mBroadcastReciver);
        }
    }

//    @Override
//    protected void onDestroy() {
//
//        if (mBroadcastReciver != null) {
//            this.unregisterReceiver(mBroadcastReciver);
//        }
//        super.onDestroy();
//    }


    private RelativeLayout mMainConversationLiner;
    private RelativeLayout mMainGroupLiner;
    private RelativeLayout mMainChatroomLiner;
    /**
     * 聊天室的fragment
     */
    private Fragment mChatroomFragment = null;

    /**
     * 会话列表的fragment
     */
    private Fragment mConversationFragment = null;
    /**
     * 群组的fragment
     */
    private Fragment mGroupListFragment = null;
    /**
     * 会话TextView
     */
    private TextView mMainConversationTv;
    /**
     * 群组TextView
     */
    private TextView mMainGroupTv;

    private TextView mUnreadNumView;
    /**
     * 聊天室TextView
     */
    private TextView mMainChatroomTv;

    private FragmentManager mFragmentManager;

    private ViewPager mViewPager;
    /**
     * 下划线
     */
    private ImageView mMainSelectImg;

    private FragmentPagerAdapter mFragmentPagerAdapter;

    private LayoutInflater mInflater;
    /**
     * 下划线长度
     */
    int indicatorWidth;
    private LinearLayout mMainShow;
}
