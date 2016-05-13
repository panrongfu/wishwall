package net.wishwall.rong.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sea_monster.exception.BaseException;
import com.sea_monster.network.AbstractHttpRequest;

import net.wishwall.Constants;
import net.wishwall.R;
import net.wishwall.domain.GroupsDTO;
import net.wishwall.domain.GroupsDTO.ResultBean;
import net.wishwall.rong.activity.GroupDetailActivity;
import net.wishwall.rong.adapter.GroupListAdapter;
import net.wishwall.rong.model.Groups;
import net.wishwall.rong.model.Status;
import net.wishwall.rong.widget.LoadingDialog;
import net.wishwall.rong.widget.WinToast;
import net.wishwall.service.ApiClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Group;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author panRongFu on 2016/4/1.
 * @Description
 * @email pan@ipushan.com
 */
public class GroupListFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private String TAG = GroupListFragment.class.getSimpleName();
    private int RESULTCODE = 100;
    private ListView mGroupListView;
    private GroupListAdapter mMyGroupListAdapter;
    private List<ResultBean> mResultList;
    private AbstractHttpRequest<Groups> mGetAllGroupsRequest;
    private AbstractHttpRequest<Status> mUserRequest;
    private HashMap<String, Group> mGroupMap;
    private ResultBean result;
    private Handler mHandler;
    private LoadingDialog mDialog;
    public static final String GroupListData = "GroupListData";
    Bundle mBundle;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.de_fr_group_list, container, false);

        mGroupListView = (ListView) view.findViewById(R.id.de_group_list);
        mGroupListView.setItemsCanFocus(false);
        mGroupListView.setOnItemClickListener(this);

        mDialog = new LoadingDialog(getActivity());
        mResultList = new ArrayList<ResultBean>();
        mHandler = new Handler();

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        savedInstanceState = mBundle;
//        if (savedInstanceState != null) {
//            mResultList = savedInstanceState.getParcelableArrayList(GroupListData);
//            mMyGroupListAdapter = new GroupListAdapter(getActivity(), mResultList, mGroupMap);
//            mGroupListView.setAdapter(mMyGroupListAdapter);
//            mMyGroupListAdapter.setOnItemButtonClick(onItemButtonClick);
//            mMyGroupListAdapter.notifyDataSetChanged();
//            return;
//        }

//        if (DemoContext.getInstance() == null)
//            return;
//
//        mGroupMap = DemoContext.getInstance().getGroupMap();
//        mGetAllGroupsRequest = DemoContext.getInstance().getDemoApi().getAllGroups(this);
        ApiClient.findMyGroups(new Callback<GroupsDTO>() {
            @Override
            public void onResponse(Call<GroupsDTO> call, Response<GroupsDTO> response) {
                GroupsDTO body = response.body();
                if(body.getCode() == 200){
                    mResultList = body.getResult();
                    mMyGroupListAdapter = new GroupListAdapter(getActivity(), mResultList, mGroupMap);
                    mMyGroupListAdapter.setType(GroupListAdapter.Type.MY);
                    mGroupListView.setAdapter(mMyGroupListAdapter);
                }
            }
            @Override
            public void onFailure(Call<GroupsDTO> call, Throwable t) {
            }
        });
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mResultList != null && position != -1 && position < mResultList.size()) {

            Uri uri = Uri.parse("demo://" + getActivity().getApplicationInfo().packageName).buildUpon()
                    .appendPath("conversationSetting")
                    .appendPath(String.valueOf(Conversation.ConversationType.GROUP))
                    .appendQueryParameter("targetId", mResultList.get(position).getGroupid()).build();

            Intent intent = new Intent(getActivity(), GroupDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("groupName",mResultList.get(position).getName());
            bundle.putString("number",mResultList.get(position).getNumber()+"");
            bundle.putString("maxNumber",mResultList.get(position).getMax_number()+"");
            bundle.putString("introduce",mResultList.get(position).getIntroduce());
            bundle.putString("groupId",mResultList.get(position).getGroupid());
            intent.putExtra("INTENT_GROUP", bundle);
            intent.setData(uri);
            startActivityForResult(intent, RESULTCODE);
        }

    }

    @Override
    public void onCallApiSuccess(final AbstractHttpRequest request, Object obj) {
        if (mGetAllGroupsRequest != null && mGetAllGroupsRequest.equals(request)) {

            if (obj instanceof Groups) {

                final Groups groups = (Groups) obj;
                if (groups.getCode() == 200) {
//                    for (int i = 0; i < groups.getResult().size(); i++) {
//                        mResultList.add(groups.getResult().get(i));
//                    }
//
//                    mMyGroupListAdapter = new GroupListAdapter(getActivity(), mResultList, mGroupMap);
//                    mGroupListView.setAdapter(mMyGroupListAdapter);
//                    mMyGroupListAdapter.setOnItemButtonClick(onItemButtonClick);
                } else {
                    WinToast.toast(getActivity(), groups.getCode());
                }
            }
        } else if (mUserRequest != null && mUserRequest.equals(request)) {
            WinToast.toast(getActivity(), R.string.group_join_success);

            if (result != null) {

                setGroupMap(result, 1);

                refreshAdapter();

                RongIM.getInstance().getRongIMClient().joinGroup(result.getGroupid(), result.getName(), new RongIMClient.OperationCallback() {
                    @Override
                    public void onSuccess() {
                        if (mDialog != null)
                            mDialog.dismiss();
                        RongIM.getInstance().startGroupChat(getActivity(), result.getGroupid(), result.getName());
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {

                    }
                });
            }
        }
    }

    @Override
    public void onCallApiFailure(AbstractHttpRequest request, BaseException e) {

        if (mUserRequest != null && mUserRequest.equals(request)) {
            if (mDialog != null)
                mDialog.dismiss();
        } else if (mGetAllGroupsRequest != null && mGetAllGroupsRequest.equals(request)) {
            Log.e(TAG, "---获取群组列表失败 ----");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case Constants.GROUP_JOIN_REQUESTCODE:
            case Constants.GROUP_QUIT_REQUESTCODE:
                refreshAdapter();
                break;
        }
    }

    /**
     * 设置群组信息提供者
     *
     * @param result
     * @param i      0,退出；1 加入
     */
    public static void setGroupMap(ResultBean result, int i) {

//        if (DemoContext.getInstance() != null && result != null) {
//            HashMap<String, Group> groupHashMap = DemoContext.getInstance().getGroupMap();
//
//            if (result.getId() == null)
//                return;
//
//            if (i == 1) {
//                if (result.getPortrait() != null)
//                    groupHashMap.put(result.getId(), new Group(result.getId(), result.getName(), Uri.parse(result.getPortrait())));
//                else
//                    groupHashMap.put(result.getId(), new Group(result.getId(), result.getName(), null));
//            } else if (i == 0) {
//                groupHashMap.remove(result.getId());
//            }
//            DemoContext.getInstance().setGroupMap(groupHashMap);
//
//        }
    }

    /**
     * 刷新群组列表
     */
    private void refreshAdapter() {

        ApiClient.findMyGroups(new Callback<GroupsDTO>() {
            @Override
            public void onResponse(Call<GroupsDTO> call, Response<GroupsDTO> response) {
                GroupsDTO body = response.body();
                if(body.getCode() == 200){
                    mResultList = body.getResult();
                    mMyGroupListAdapter = new GroupListAdapter(getActivity(), mResultList, mGroupMap);
                    mGroupListView.setAdapter(mMyGroupListAdapter);
                }
            }
            @Override
            public void onFailure(Call<GroupsDTO> call, Throwable t) {
                    t.printStackTrace();
            }
        });

//        if (mMyGroupListAdapter == null) {
//            mMyGroupListAdapter = new GroupListAdapter(getActivity(), mResultList, mGroupMap);
//            mGroupListView.setAdapter(mMyGroupListAdapter);
//
//        } else {
//            mMyGroupListAdapter.notifyDataSetChanged();
//        }
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
       // outState.putParcelableArrayList(GroupListData, (ArrayList<? extends Parcelable>) mResultList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBundle = new Bundle();
       // mBundle.putParcelableArrayList(GroupListData, (ArrayList<? extends Parcelable>) mResultList);
    }

    @Override
    public void onDestroy() {
        if (mMyGroupListAdapter != null) {
            mMyGroupListAdapter = null;
        }
        super.onDestroy();
    }
}