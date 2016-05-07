package net.wishwall.rong.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;

import net.wishwall.Constants;
import net.wishwall.R;
import net.wishwall.domain.FriendListDTO;
import net.wishwall.rong.adapter.FriendListAdapter;
import net.wishwall.rong.adapter.FriendMultiChoiceAdapter;
import net.wishwall.rong.model.Friend;
import net.wishwall.rong.widget.PinnedHeaderListView;
import net.wishwall.rong.widget.SwitchGroup;
import net.wishwall.rong.widget.SwitchItemView;
import net.wishwall.service.ApiClient;
import net.wishwall.utils.SpUtil;
import net.wishwall.views.CustomToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Discussion;
import io.rong.imlib.model.UserInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendListFragment extends Fragment implements SwitchGroup.ItemHander, OnClickListener, TextWatcher, FriendListAdapter.OnFilterFinished, OnItemClickListener {

    private static final String TAG = FriendListFragment.class.getSimpleName();
    protected FriendListAdapter mAdapter;
    private PinnedHeaderListView mListView;
    private SwitchGroup mSwitchGroup;
    private EditText mEditText;

    protected List<Friend> mFriendsList;
    protected List<FriendListDTO.ResultBean> mFriendsListDTO;

    private boolean isMultiChoice = false;

    private ArrayList<String> mSelectedItemIds;
    private ArrayList<String> mHaveSelectedItemIds;
    private boolean isFromSetting = false;
    private Conversation.ConversationType mConversationType;
    private String mTargetId;
    private SpUtil userSpUtil;
    private CustomToast customToast;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.de_list_friend, container, false);
        mListView = (PinnedHeaderListView) view.findViewById(R.id.de_ui_friend_list);
        mSwitchGroup = (SwitchGroup) view.findViewById(R.id.de_ui_friend_message);
        mEditText = (EditText) view.findViewById(R.id.de_ui_search);
        mListView.setPinnedHeaderView(LayoutInflater.from(this.getActivity()).inflate(R.layout.de_item_friend_index, mListView, false));
        mListView.setFastScrollEnabled(false);
        mListView.setOnItemClickListener(this);
        mSwitchGroup.setItemHander(this);
        mEditText.addTextChangedListener(this);
        mListView.setHeaderDividersEnabled(false);
        mListView.setFooterDividersEnabled(false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        customToast = CustomToast.createToast(getActivity());
        userSpUtil = new SpUtil(getActivity(), Constants.USER_SPUTIL);
        String userId = userSpUtil.getKeyValue("userId");
        if(userId.trim()== null)return;

//        if (DemoContext.getInstance() != null) {
//            userInfos = DemoContext.getInstance().getFriendList();
//        }
        mFriendsList = new ArrayList<Friend>();
        mFriendsListDTO = new ArrayList<FriendListDTO.ResultBean>();
        ApiClient.findFriendList(userId, new Callback<FriendListDTO>() {
            @Override
            public void onResponse(Call<FriendListDTO> call, Response<FriendListDTO> response) {
                FriendListDTO body = response.body();
                if(body.getCode() == 200){
                    mFriendsListDTO = body.getResult();
                    getFriendInfo();
                }else{
                    customToast.setMessage(body.getMessage()).show();
                }
            }
            @Override
            public void onFailure(Call<FriendListDTO> call, Throwable t) {
                t.printStackTrace();
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * 获取朋友的信息
     */
    private void getFriendInfo() {
        Intent intent = getActivity().getIntent();
        ArrayList<UserInfo> userInfos = null;
        userSpUtil = new SpUtil(getActivity(), Constants.USER_SPUTIL);
        String userId = userSpUtil.getKeyValue("userId");
        if(userId.trim()== null)return;
        for (FriendListDTO.ResultBean flr : mFriendsListDTO) {
            Friend friend = new Friend();
            friend.setNickname(flr.getUsername());
            friend.setPortrait(flr.getIcon() + "");
            friend.setUserId(flr.getUserid());
            mFriendsList.add(friend);
        }
        mFriendsList = sortFriends(mFriendsList);
        if (mSelectedItemIds != null && isMultiChoice) {
            for (String id : mSelectedItemIds) {
                for (Friend friend : mFriendsList) {
                    if (id.equals(friend.getUserId())) {
                        friend.setSelected(true);
                        break;
                    }
                }
            }
        }

        if (intent.hasExtra("DEMO_FRIEND_TARGETID") && intent.hasExtra("DEMO_FRIEND_CONVERSATTIONTYPE") && intent.hasExtra("DEMO_FRIEND_ISTRUE")) {
            mTargetId = intent.getStringExtra("DEMO_FRIEND_TARGETID");
            isFromSetting = intent.getBooleanExtra("DEMO_FRIEND_ISTRUE", false);
            String conversationType = intent.getStringExtra("DEMO_FRIEND_CONVERSATTIONTYPE").toUpperCase();
            mConversationType = Conversation.ConversationType.valueOf(conversationType);
            Log.e(TAG, "0705--onViewCreated--mTargetId" + mTargetId + "--conversationType===" + conversationType);

        }

        if (isFromSetting) {
            mHaveSelectedItemIds = new ArrayList<String>();
            if (mConversationType.equals(Conversation.ConversationType.PRIVATE)) {
                mSelectedItemIds.add(mTargetId);
                if(mTargetId!=null && isMultiChoice){
                    for (Friend friend : mFriendsList) {
                        if(mTargetId.equals(friend.getUserId())){
                            friend.setSelected(true);
                            break;
                        }
                    }
                }
                mAdapter = isMultiChoice ? new FriendMultiChoiceAdapter(getActivity(), mFriendsList, mSelectedItemIds) : new FriendListAdapter(getActivity(), mFriendsList);
                fillData();
            } else if (mConversationType.equals(Conversation.ConversationType.DISCUSSION)) {
                if (RongIM.getInstance() != null && RongIM.getInstance().getRongIMClient() != null)
                    RongIM.getInstance().getRongIMClient().getDiscussion(mTargetId, new RongIMClient.ResultCallback<Discussion>() {
                        @Override
                        public void onSuccess(Discussion discussion) {
                            isMultiChoice = true;
                            ArrayList<String> lists = (ArrayList<String>) discussion.getMemberIdList();
                            for (int i = 0; i < lists.size(); i++) {
                                mSelectedItemIds.add(lists.get(i));
                            }
                            if (mSelectedItemIds != null && isMultiChoice) {
                                for (String id : mSelectedItemIds) {
                                    for (Friend friend : mFriendsList) {
                                        if (id.equals(friend.getUserId())) {
                                            friend.setSelected(true);
                                            break;
                                        }
                                    }
                                }
                            }
                            mAdapter = isMultiChoice ? new FriendMultiChoiceAdapter(getActivity(), mFriendsList, mSelectedItemIds) : new FriendListAdapter(getActivity(), mFriendsList);
                            mListView.setAdapter(mAdapter);
                            fillData();
                        }
                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {

                        }
                    });
            }

        } else {
            mAdapter = isMultiChoice ? new FriendMultiChoiceAdapter(this.getActivity(), mFriendsList, mSelectedItemIds) : new FriendListAdapter(this.getActivity(), mFriendsList);
          //  mListView.setAdapter(mAdapter);
            fillData();
        }
    }

    private final void fillData() {
//      mAdapter.removeAll();

        mListView.setAdapter(mAdapter);
        mAdapter.setAdapterData(mFriendsList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFilterFinished() {
        if (mFriendsList != null && mFriendsList.size() == 0) {
            return;
        }

        if (mAdapter == null || mAdapter.isEmpty()) {

        } else {
        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (mAdapter != null) {
            mAdapter.getFilter().filter(s);
        }
    }
    @Override
    public void afterTextChanged(Editable s) {

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
        if (tagObj != null && tagObj instanceof FriendListAdapter.ViewHolder) {
            FriendListAdapter.ViewHolder viewHolder = (FriendListAdapter.ViewHolder) tagObj;
            mAdapter.onItemClick(viewHolder.friend.getUserId(), viewHolder.choice);
            return;
        }
    }

    @Override
    public void onDestroyView() {
        if (mAdapter != null) {
            mAdapter.destroy();
            mAdapter = null;
        }
        super.onDestroyView();
    }
    public boolean isMultiChoice() {
        return isMultiChoice;
    }
    public void setMultiChoice(boolean isMultiChoice, ArrayList<String> selectedItemIds) {
        this.isMultiChoice = isMultiChoice;
        this.mSelectedItemIds = selectedItemIds;
    }


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
        for (int i = 0; i < searchLetters.length; i++) {
            String letter = searchLetters[i];
            ArrayList<Friend> fArrayList = userMap.get(letter);
            if (fArrayList != null) {
                friendsArrayList.addAll(fArrayList);
            }
        }
        return friendsArrayList;
    }
}
