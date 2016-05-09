package net.wishwall.rong.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.sea_monster.network.AbstractHttpRequest;

import net.wishwall.Constants;
import net.wishwall.R;
import net.wishwall.domain.GroupsDTO;
import net.wishwall.rong.RongCloudEvent;
import net.wishwall.rong.fragment.ChatFragmentActivity;
import net.wishwall.rong.model.Groups;
import net.wishwall.rong.widget.LoadingDialog;
import net.wishwall.service.ApiClient;
import net.wishwall.utils.SpUtil;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Group;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Bob on 15/11/3.
 * 会话列表，需要做 2 件事
 * 1，push 重连，收到 push 消息的时候，做一下 connect 操作
 * 2，获取所加入的群组，并做 syncGroup 操作。demo 逻辑，需要同步当前所加入的群组
 */
public class ConversationListActivity extends BaseActivity {

    private static final String TAG = ConversationListActivity.class.getSimpleName();
    private AbstractHttpRequest<Groups> mGetMyGroupsRequest;
    private LoadingDialog mDialog;
    private SpUtil userSpUtil;
    private List<GroupsDTO.ResultBean> mMyGroups = new ArrayList<GroupsDTO.ResultBean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversationlist);
        Intent intent = getIntent();
        userSpUtil = new SpUtil(this, Constants.USER_SPUTIL);

        //push
        if (intent.getData().getScheme().equals("rong")
                && intent.getData().getQueryParameter("push") != null) {

            //通过intent.getData().getQueryParameter("push") 为true，判断是否是push消息
            if (intent.getData().getQueryParameter("push").equals("true")) {
                String id = intent.getData().getQueryParameter("pushId");
                RongIM.getInstance().getRongIMClient().recordNotificationEvent(id);
                enterActivity();
            }

        } else {//通知过来
            //程序切到后台，收到消息后点击进入,会执行这里
            if (RongIM.getInstance() == null || RongIM.getInstance().getRongIMClient() == null) {

                enterActivity();
            } else {
                startActivity(new Intent(ConversationListActivity.this, ChatFragmentActivity.class));
                finish();
            }
        }
    }

    /**
     * 收到 push 消息后，选择进入哪个 Activity
     * 如果程序缓存未被清理，进入 DrawerActivity
     * 程序缓存被清理，进入 LoginActivity，重新获取token
     * <p/>
     * 作用：由于在 manifest 中 intent-filter 是配置在 ConversationListActivity 下面，所以收到消息后点击notifacition 会跳转到 DemoActivity。
     * 以跳到 DrawerActivity 为例：
     * 在 ConversationListActivity 收到消息后，选择进入 DrawerActivity，这样就把 DrawerActivity 激活了，当你读完收到的消息点击 返回键 时，程序会退到
     * DrawerActivity 页面，而不是直接退回到 桌面。
     */
    private void enterActivity() {

        String token = userSpUtil.getKeyValue("token");
        if(token.trim() ==null)return;

        if (mDialog != null && !mDialog.isShowing()) {
            mDialog.show();
        }
        reconnect(token);
    }
    /**
     * @param token
     */
    private void reconnect(String token) {

        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {

                Log.e(TAG, "---onTokenIncorrect--");
            }

            @Override
            public void onSuccess(String s) {
                Log.i(TAG, "---onSuccess--" + s);
                if (RongCloudEvent.getInstance() != null)
                    RongCloudEvent.getInstance().setOtherListener();

//                if (DemoContext.getInstance() != null)
//                    mGetMyGroupsRequest = DemoContext.getInstance().getDemoApi().getMyGroups(ConversationListActivity.this);
                String userid = userSpUtil.getKeyValue("userId");
                ApiClient.findMyGroups(new Callback<GroupsDTO>() {
                    @Override
                    public void onResponse(Call<GroupsDTO> call, Response<GroupsDTO> response) {
                        GroupsDTO body = response.body();
                        if(body.getCode() ==200){
                            mMyGroups = body.getResult();
                            getMyGroups();
                        }
                    }

                    @Override
                    public void onFailure(Call<GroupsDTO> call, Throwable t) {

                    }
                });
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {
                Log.e(TAG, "---onError--" + e);
            }
        });

    }

    /**
     * 获取我加入的群组
     */
    private void getMyGroups() {

        List<Group> grouplist = new ArrayList<Group>();
        for(GroupsDTO.ResultBean mgr: mMyGroups){
            String groupId = mgr.getGroupid();
            String groupName = mgr.getName();
            String icon = mgr.getIcon();
            grouplist.add(new Group(groupId,groupName,Uri.parse(icon)));
        }
//        if (groups.getResult() != null) {
//            for (int i = 0; i < groups.getResult().size(); i++) {
//
//                String id = groups.getResult().get(i).getId();
//                String name = groups.getResult().get(i).getName();
//                if (groups.getResult().get(i).getPortrait() != null) {
//                    Uri uri = Uri.parse(groups.getResult().get(i).getPortrait());
//                    grouplist.add(new Group(id, name, uri));
//                } else {
//                    grouplist.add(new Group(id, name, null));
//                }
//            }
//            HashMap<String, Group> groupM = new HashMap<String, Group>();
//            for (int i = 0; i < grouplist.size(); i++) {
//                groupM.put(groups.getResult().get(i).getId(), grouplist.get(i));
//            }
//            if (DemoContext.getInstance() != null)
//                DemoContext.getInstance().setGroupMap(groupM);

            if (grouplist.size() > 0)
                RongIM.getInstance().getRongIMClient().syncGroup(grouplist, new RongIMClient.OperationCallback() {
                    @Override
                    public void onSuccess() {
                        Log.e(TAG, "---syncGroup-onSuccess---");
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        Log.e(TAG, "---syncGroup-onError---");
                    }
                });

            if (mDialog != null)
                mDialog.dismiss();
            startActivity(new Intent(ConversationListActivity.this, ChatFragmentActivity.class));
            finish();
        }
}

