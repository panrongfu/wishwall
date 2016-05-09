package net.wishwall.rong.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sea_monster.exception.BaseException;
import com.sea_monster.network.AbstractHttpRequest;

import net.wishwall.Constants;
import net.wishwall.R;
import net.wishwall.domain.GroupsDTO.ResultBean;
import net.wishwall.domain.GroupsDTO;
import net.wishwall.domain.ResultDTO;
import net.wishwall.rong.fragment.GroupListFragment;
import net.wishwall.rong.model.Status;
import net.wishwall.rong.widget.LoadingDialog;
import net.wishwall.rong.widget.WinToast;
import net.wishwall.service.ApiClient;
import net.wishwall.utils.SpUtil;
import net.wishwall.views.CustomProgressDialog;
import net.wishwall.views.CustomToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.rong.imkit.RongIM;
import io.rong.imkit.widget.AsyncImageView;
import io.rong.imlib.RongIMClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Bob on 2015/3/28.
 */
public class GroupDetailActivity extends BaseApiActivity implements View.OnClickListener, Handler.Callback {

    private static final String TAG = GroupDetailActivity.class.getSimpleName();
    private static final int HAS_JOIN = 1;
    private static final int NO_JOIN = 2;
    private AsyncImageView mGroupImg;
    private TextView mGroupName;
    private TextView mGroupId;
    private RelativeLayout mRelGroupIntro;
    private TextView mGroupIntro;
    private RelativeLayout mRelGroupNum;
    private TextView mGroupNum;
    private RelativeLayout mRelGroupMyName;
    private TextView mGroupMyName;
    private RelativeLayout mRelGroupCleanMess;
    private Button mGroupJoin;
    private Button mGroupQuit;
    private Button mGroupChat;
    private ResultBean mApiResult;
    private AbstractHttpRequest<Status> mJoinRequest;
    private AbstractHttpRequest<Status> mQuitRequest;
    private Handler mHandler;
    private LoadingDialog mDialog;
    private CustomToast customToast;
    private CustomProgressDialog progressDialog;
    private SpUtil userSpUtil;
    private String userId;
    private String groupId;
    private String number;
    private String maxNumber;
    private String groupName;
    private List<GroupsDTO.ResultBean> groupIdList;
    private Map<String,String>groupIdMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.de_fr_group_intro);

        Toolbar toolbar = (Toolbar) findViewById(R.id.conver_toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setTitle(getResources().getString(R.string.personal_title));
        ab.setHomeAsUpIndicator(R.mipmap.de_actionbar_back);
        ab.setDisplayHomeAsUpEnabled(true);
        initView();
        initData();
    }

    /**
     * 初始化控件、布局、工具类
     */
    private void initView() {
        customToast = CustomToast.createToast(this);
        progressDialog = CustomProgressDialog.createDialog(this);
        userSpUtil = new SpUtil(this,"userInfo");

        mGroupImg = (AsyncImageView) findViewById(R.id.group_portrait);
        mGroupName = (TextView) findViewById(R.id.group_name);
        mGroupId = (TextView) findViewById(R.id.group_id);
        mRelGroupIntro = (RelativeLayout) findViewById(R.id.rel_group_intro);
        mGroupIntro = (TextView) findViewById(R.id.group_intro);
        mRelGroupNum = (RelativeLayout) findViewById(R.id.rel_group_number);
        mGroupNum = (TextView) findViewById(R.id.group_number);
        mRelGroupMyName = (RelativeLayout) findViewById(R.id.rel_group_myname);
        mGroupMyName = (TextView) findViewById(R.id.group_myname);
        mRelGroupCleanMess = (RelativeLayout) findViewById(R.id.rel_group_clear_message);
        mGroupJoin = (Button) findViewById(R.id.join_group);
        mGroupQuit = (Button) findViewById(R.id.quit_group);
        mGroupChat = (Button) findViewById(R.id.chat_group);

        mGroupJoin.setOnClickListener(this);
        mGroupChat.setOnClickListener(this);
        mGroupQuit.setOnClickListener(this);
        mHandler = new Handler(this);
        mDialog = new LoadingDialog(this);
    }

    /**
     *初始化数据
     */
    protected void initData() {
        if (getIntent().hasExtra("INTENT_GROUP")) {
            // mApiResult = (ApiResult) getIntent().getSerializableExtra("INTENT_GROUP");
            Bundle bundle = getIntent().getBundleExtra("INTENT_GROUP");
            if(bundle !=null){
                groupName = bundle.getString("groupName");
                number = bundle.getString("number");
                maxNumber = bundle.getString("maxNumber");
                String introduce= bundle.getString("introduce");
                groupId = bundle.getString("groupId");
                mGroupName.setText(groupName);
                //mGroupId.setText("ID:" + mApiResult.getId().toString());
                mGroupIntro.setText(introduce);
                mGroupNum.setText(number);
            }
        }
         userId = userSpUtil.getKeyValue("userId");
         groupIdMap = new HashMap<String,String>();
         groupIdList = new ArrayList<GroupsDTO.ResultBean>();

//        if (DemoContext.getInstance() != null) {
//            HashMap<String, Group> groupHashMap = DemoContext.getInstance().getGroupMap();
            ApiClient.findMyGroups(new Callback<GroupsDTO>() {
                @Override
                public void onResponse(Call<GroupsDTO> call, Response<GroupsDTO> response) {
                    GroupsDTO body = response.body();
                    if(body.getCode() == 200){
                        groupIdList = body.getResult();
                        if(groupIdList != null){
                            for(GroupsDTO.ResultBean mr: groupIdList){
                                groupIdMap.put(mr.getGroupid(),mr.getGroupid());
                            }
                            Message mess = Message.obtain();
                            if (groupIdMap != null && groupIdMap.containsKey(groupId)) {
                                mess.what = HAS_JOIN;
                            } else {
                                mess.what = NO_JOIN;
                            }
                            mHandler.sendMessage(mess);
                        }
                    }
                }
                @Override
                public void onFailure(Call<GroupsDTO> call, Throwable t) {

                }
            });
        }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case HAS_JOIN:
                if (mDialog != null)
                    mDialog.dismiss();
                mGroupJoin.setVisibility(View.GONE);
                mGroupChat.setVisibility(View.VISIBLE);
                mGroupQuit.setVisibility(View.VISIBLE);
                break;
            case NO_JOIN:
                if (mDialog != null)
                    mDialog.dismiss();
                mGroupQuit.setVisibility(View.GONE);
                mGroupJoin.setVisibility(View.VISIBLE);
                mGroupChat.setVisibility(View.GONE);
                break;

        }
        return false;
    }

    @Override
   public void onClick(View v) {
       switch (v.getId()) {
            case R.id.join_group:
                if(number.equals(maxNumber)){
                    customToast.setMessage("群满员了，你可以加入其它群哦").show();
                    break;
                }
                joinGroup();
                break;
            case R.id.quit_group:
                quitGroup();
                break;

            case R.id.chat_group:
                if (RongIM.getInstance() != null)
                    RongIM.getInstance().getRongIMClient().joinGroup(groupId, groupName, new RongIMClient.OperationCallback() {
                        @Override
                        public void onSuccess() {
                            RongIM.getInstance().startGroupChat(GroupDetailActivity.this, groupId, groupName);
                        }
                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {
                        }
                    });

                break;
        }
    }
    /**
     * 加入群
     */
    private void joinGroup() {

        progressDialog.setMessage("申请加入中...").show();
        ApiClient.joinGroup(userId, groupId, groupName, new Callback<ResultDTO>() {
            @Override
            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                ResultDTO body = response.body();
                if(body.getCode() ==200 ){
                    if (RongIM.getInstance() != null)
                        RongIM.getInstance().getRongIMClient().joinGroup(groupId, groupName, new RongIMClient.OperationCallback() {
                            @Override
                            public void onSuccess() {
                                Message mess = Message.obtain();
                                mess.what = HAS_JOIN;
                                mHandler.sendMessage(mess);
                                RongIM.getInstance().startGroupChat(GroupDetailActivity.this, groupId, groupName);
                                Intent intent = new Intent();
                                //  intent.putExtra("result", DemoContext.getInstance().getGroupMap());
                               GroupDetailActivity.this.setResult(Constants.GROUP_JOIN_REQUESTCODE, intent);
                                customToast.setMessage("加群成功").show();
                            }
                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {
                            }
                        });

                }else{
                    customToast.setMessage(body.getMessage()).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResultDTO> call, Throwable t) {
                customToast.setMessage("加群失败").show();
                progressDialog.dismiss();
            }
        });
    }

    /**
     * 退出群
     */
    private void quitGroup() {
        progressDialog.setMessage("退出中...").show();
        ApiClient.quitGroup(userId, groupId, new Callback<ResultDTO>() {
            @Override
            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                ResultDTO body = response.body();
                if(body.getCode() == 200){
                    if (RongIM.getInstance() != null && RongIM.getInstance().getRongIMClient()!=null) {
                        RongIM.getInstance().getRongIMClient().quitGroup(groupId, new RongIMClient.OperationCallback() {
                            @Override
                            public void onSuccess() {
                                Intent intent = new Intent();
                                //intent.putExtra("result", DemoContext.getInstance().getGroupMap());
                                GroupDetailActivity.this.setResult(Constants.GROUP_QUIT_REQUESTCODE, intent);
                                Log.e(TAG, "-----------quit success ----");
                                customToast.setMessage("退群成功").show();
                                GroupDetailActivity.this.finish();
                            }
                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {
                            }
                        });
                    }
                }else{
                    customToast.setMessage(body.getMessage()).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResultDTO> call, Throwable t) {
                customToast.setMessage("退群失败").show();
                progressDialog.dismiss();
            }
        });

    }


    @Override
    public void onCallApiSuccess(AbstractHttpRequest request, Object obj) {

        if (mJoinRequest!=null && mJoinRequest.equals(request)) {
            if (obj instanceof Status) {
                final Status status = (Status) obj;
                if (status.getCode() == 200 && mApiResult != null) {
                    WinToast.toast(this, R.string.group_join_success);
                    Log.e(TAG, "-----------join success ----");
                    GroupListFragment.setGroupMap(mApiResult, 1);

                    if (RongIM.getInstance() != null)
                        RongIM.getInstance().getRongIMClient().joinGroup(mApiResult.getGroupid(), mApiResult.getName(), new RongIMClient.OperationCallback() {
                            @Override
                            public void onSuccess() {

                                Message mess = Message.obtain();
                                mess.what = HAS_JOIN;
                                mHandler.sendMessage(mess);
                                RongIM.getInstance().startGroupChat(GroupDetailActivity.this, mApiResult.getGroupid(), mApiResult.getName());

                            }
                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {
                            }
                        });

                    Intent intent = new Intent();
                   // intent.putExtra("result", DemoContext.getInstance().getGroupMap());
                    GroupDetailActivity.this.setResult(Constants.GROUP_JOIN_REQUESTCODE, intent);
                }
            }
        } else if (mQuitRequest!=null &&mQuitRequest.equals(request)) {
            if (obj instanceof Status) {
                final Status status = (Status) obj;
                if (status.getCode() == 200) {
//                    WinToast.toast(this, "quit success ");
                    GroupListFragment.setGroupMap(mApiResult, 0);

                    Message mess = Message.obtain();
                    mess.what = NO_JOIN;
                    mHandler.sendMessage(mess);

                    if (RongIM.getInstance() != null && RongIM.getInstance().getRongIMClient()!=null) {
                        RongIM.getInstance().getRongIMClient().quitGroup(mApiResult.getGroupid(), new RongIMClient.OperationCallback() {
                            @Override
                            public void onSuccess() {
                                WinToast.toast(GroupDetailActivity.this, R.string.group_quit_success);
                                Intent intent = new Intent();
                              //  intent.putExtra("result", DemoContext.getInstance().getGroupMap());
                                GroupDetailActivity.this.setResult(Constants.GROUP_QUIT_REQUESTCODE, intent);
                                Log.e(TAG, "-----------quit success ----");
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {

                            }
                        });
                    }
                }
            }
        }
    }

    @Override
    public void onCallApiFailure(AbstractHttpRequest request, BaseException e) {

        if(mQuitRequest!=null && mQuitRequest.equals(request)){

            if (mDialog != null)
                mDialog.dismiss();

        }else if(mJoinRequest!=null && mJoinRequest.equals(request)){

            if (mDialog != null)
                mDialog.dismiss();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == event.getKeyCode()) {
            finish();
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }


}
