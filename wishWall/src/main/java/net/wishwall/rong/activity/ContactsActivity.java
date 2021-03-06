package net.wishwall.rong.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import net.wishwall.Constants;
import net.wishwall.R;
import net.wishwall.activities.PersonDetailActivity;
import net.wishwall.domain.FriendListDTO;
import net.wishwall.rong.adapter.ContactsMultiChoiceAdapter;
import net.wishwall.rong.adapter.FriendListAdapter;
import net.wishwall.rong.fragment.ChatFragmentActivity;
import net.wishwall.rong.model.Friend;
import net.wishwall.rong.widget.PinnedHeaderListView;
import net.wishwall.rong.widget.SwitchGroup;
import net.wishwall.rong.widget.SwitchItemView;
import net.wishwall.service.ApiClient;
import net.wishwall.utils.SpUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2015/3/26.
 */
public class ContactsActivity extends BaseActivity implements SwitchGroup.ItemHander, View.OnClickListener, TextWatcher, FriendListAdapter.OnFilterFinished, AdapterView.OnItemClickListener{

    private String TAG = ContactsActivity.class.getSimpleName();
    protected ContactsMultiChoiceAdapter mAdapter;
    private PinnedHeaderListView mListView;
    private SwitchGroup mSwitchGroup;
    /**
     * 好友list
     */
    protected List<Friend> mFriendsList;
    protected List<FriendListDTO.ResultBean> mFriendsListDTO;
    private TextView textView;
    private ReceiveMessageBroadcastReciver mBroadcastReciver;
    private SpUtil userSpUtil;
    private TextView contactTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.de_ac_address_fragment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.contact_toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setTitle("");
        ab.setHomeAsUpIndicator(R.mipmap.de_actionbar_back);
        ab.setDisplayHomeAsUpEnabled(true);
        initViewUI();

    }

    private void initViewUI() {

        userSpUtil  = new SpUtil(this,Constants.USER_SPUTIL);
        contactTitle = (TextView)findViewById(R.id.contact_title);
        contactTitle.setText(R.string.add_contacts);
        mListView = (PinnedHeaderListView) findViewById(R.id.de_ui_friend_list);
        mSwitchGroup = (SwitchGroup) findViewById(R.id.de_ui_friend_message);

        mListView.setPinnedHeaderView(LayoutInflater.from(this).inflate(R.layout.de_item_friend_index, mListView, false));
        //TODO
        textView = (TextView) mListView.getPinnedHeaderView();

        mListView.setFastScrollEnabled(false);

        mListView.setOnItemClickListener(this);
        mSwitchGroup.setItemHander(this);

        mListView.setHeaderDividersEnabled(false);
        mListView.setFooterDividersEnabled(false);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ChatFragmentActivity.ACTION_DMEO_AGREE_REQUEST);
        if (mBroadcastReciver == null) {
            mBroadcastReciver = new ReceiveMessageBroadcastReciver();
        }
        registerReceiver(mBroadcastReciver, intentFilter);
        getFriendList();
    }

    private void getFriendList() {
        mFriendsListDTO = new ArrayList<FriendListDTO.ResultBean>();
        ArrayList<UserInfo> userInfos = null;
        String userid = userSpUtil.getKeyValue("userId");
        if(TextUtils.isEmpty(userid)) return;
        ApiClient.findFriendList(userid, new Callback<FriendListDTO>() {
            @Override
            public void onResponse(Call<FriendListDTO> call, Response<FriendListDTO> response) {
                FriendListDTO body = response.body();
                if(body.getCode() == 200){
                    mFriendsListDTO = body.getResult();
                    getFriendInfo(mFriendsListDTO);
                }
            }
            @Override
            public void onFailure(Call<FriendListDTO> call, Throwable t) {
            }
        });
    }
    private void getFriendInfo(List<FriendListDTO.ResultBean> mFriendsListDTO) {
        mFriendsList = new ArrayList<Friend>();
        for(FriendListDTO.ResultBean fds :mFriendsListDTO){
            Friend friend = new Friend();
            friend.setNickname(fds.getNickname());
            friend.setPortrait(fds.getIcon() + "");
            friend.setUserId(fds.getUserid());
            mFriendsList.add(friend);
        }
        mFriendsList = sortFriends(mFriendsList);
        mFriendsList.get(0).getSearchKey();
        mAdapter = new ContactsMultiChoiceAdapter(this, mFriendsList);
        mListView.setAdapter(mAdapter);
        fillData();
    }



    private class ReceiveMessageBroadcastReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ChatFragmentActivity.ACTION_DMEO_AGREE_REQUEST)) {
                updateDate();
            }
        }

    }

    private final void fillData() {
        //mAdapter.removeAll();
        mAdapter.setAdapterData(mFriendsList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

        if (v instanceof SwitchItemView) {
            CharSequence tag = ((SwitchItemView) v).getText();

            if (mAdapter != null && mAdapter.getSectionIndexer() != null) {
                Object[] sections = mAdapter.getSectionIndexer().getSections();
                int size = sections.length;

                for (int i = 0; i < size; i++) {
                    if (tag.equals(sections[i])) {
                        int index = mAdapter.getPositionForSection(i);
                        mListView.setSelection(index + mListView.getHeaderViewsCount());
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object tagObj = view.getTag();
        if (tagObj != null && tagObj instanceof ContactsMultiChoiceAdapter.ViewHolder) {
            ContactsMultiChoiceAdapter.ViewHolder viewHolder = (ContactsMultiChoiceAdapter.ViewHolder) tagObj;
            String friendId = viewHolder.friend.getUserId();
            if (friendId == "★001") {
                Intent intent = new Intent(this, NewFriendListActivity.class);
                startActivityForResult(intent,20);
            } else if (friendId == "★002") {
                if (RongIM.getInstance() != null) {
                    RongIM.getInstance().startSubConversationList(this, Conversation.ConversationType.GROUP);
                }
            }else {
                Intent intent = new Intent(this, PersonDetailActivity.class);
                intent.putExtra("CONTACTS_USER", viewHolder.friend.getUserId());
                startActivityForResult(intent, 19);
            }
        }
    }

    /**
     * 好友数据排序
     *
     * @param friends 好友 List
     * @return 排序后的好友 List
     */
    private ArrayList<Friend> sortFriends(List<Friend> friends) {

        String[] searchLetters = getResources().getStringArray(R.array.de_search_letters);
        HashMap<String, ArrayList<Friend>> userMap = new HashMap<String, ArrayList<Friend>>();
        ArrayList<Friend> friendsArrayList = new ArrayList<Friend>();

        for (Friend friend : friends) {
            String letter = new String(new char[]{friend.getSearchKey()});
            if (userMap.containsKey(letter)) {
                ArrayList<Friend> friendList = userMap.get(letter);
                friendList.add(friend);
            } else {
                ArrayList<Friend> friendList = new ArrayList<Friend>();
                friendList.add(friend);
                userMap.put(letter, friendList);
            }
        }
        ArrayList<Friend> friendList = new ArrayList<Friend>();
        friendList.add(new Friend("★001", "新的朋友", getResources().getResourceName(R.mipmap.de_address_new_friend)));
        friendList.add(new Friend("★002", "群聊",getResources().getResourceName(R.mipmap.de_address_group) ));
       // friendList.add(new Friend("★003", "公众号", getResources().getResourceName(R.mipmap.de_address_public)));
        userMap.put("★", friendList);
        for (int i = 0; i < searchLetters.length; i++) {
            String letter = searchLetters[i];
            ArrayList<Friend> fArrayList = userMap.get(letter);
            if (fArrayList != null) {
                friendsArrayList.addAll(fArrayList);
            }
        }

        return friendsArrayList;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, Constants.DEBUG + "-----onActivityResult-resultCode---");
        if (resultCode == Constants.DELETE_USERNAME_REQUESTCODE) {
            Log.e(TAG, Constants.DEBUG + "-----onActivityResult-resultCode---" + resultCode);
            updateDate();
        }

        if(requestCode == 20){
            Log.e(TAG, Constants.DEBUG + "-----onActivityResult-requestCode---" + requestCode);
            updateDate();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateDate() {
        if(mAdapter !=null){
            mAdapter = null;
        }
        ArrayList<UserInfo> userInfos = null;
        //获取好友列表
//        if (DemoContext.getInstance() != null) {
//            userInfos = DemoContext.getInstance().getFriendList();
//        }
        String userid = userSpUtil.getKeyValue("userId");
        if(userid.trim() == null) return;
        ApiClient.findFriendList(userid,new Callback<FriendListDTO>() {
            @Override
            public void onResponse(Call<FriendListDTO> call, Response<FriendListDTO> response) {
                FriendListDTO body = response.body();
                if(body.getCode() == 200){
                    mFriendsListDTO = body.getResult();
                    getFriendInfo(mFriendsListDTO);
                }
            }
            @Override
            public void onFailure(Call<FriendListDTO> call, Throwable t) {
            }
        });
//        mFriendsList = new ArrayList<Friend>();
//
//        if (userInfos != null) {
//            for (UserInfo userInfo : userInfos) {
//                Friend friend = new Friend();
//                friend.setNickname(userInfo.getName());
//                friend.setPortrait(userInfo.getPortraitUri() + "");
//                friend.setUserId(userInfo.getUserId());
//                mFriendsList.add(friend);
//            }
//        }
//        mFriendsList = sortFriends(mFriendsList);
////        mFriendsList.get(0).getSearchKey();
//        mAdapter = new ContactsMultiChoiceAdapter(this, mFriendsList);
//
//        mListView.setAdapter(mAdapter);
//        fillData();
    }

    @Override
    public void onDestroy() {
        if (mBroadcastReciver != null) {
           unregisterReceiver(mBroadcastReciver);
        }
        if (mAdapter != null) {
            mAdapter.destroy();
            mAdapter = null;
        }
        super.onDestroy();
    }

    @Override
    public void onFilterFinished() {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
