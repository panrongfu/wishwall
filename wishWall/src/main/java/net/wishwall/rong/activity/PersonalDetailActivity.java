package net.wishwall.rong.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sea_monster.exception.BaseException;
import com.sea_monster.network.AbstractHttpRequest;
import com.sea_monster.resource.Resource;

import net.wishwall.App;
import net.wishwall.Constants;
import net.wishwall.R;
import net.wishwall.domain.FriendIdsDTO;
import net.wishwall.domain.ResultDTO;
import net.wishwall.domain.UserDTO;
import net.wishwall.rong.database.UserInfos;
import net.wishwall.rong.model.Status;
import net.wishwall.rong.model.User;
import net.wishwall.rong.widget.LoadingDialog;
import net.wishwall.rong.widget.WinToast;
import net.wishwall.service.ApiClient;
import net.wishwall.utils.SpUtil;
import net.wishwall.views.CustomToast;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imkit.widget.AsyncImageView;
import io.rong.imlib.model.UserInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Bob on 2015/4/7.
 * <p/>
 * 个人详情
 */
public class PersonalDetailActivity extends BaseApiActivity {

    private AbstractHttpRequest<Status> mDeleteFriendRequest;
    private AbstractHttpRequest<User> mUserHttpRequest;
    private AbstractHttpRequest<User> getUserInfoByUserIdHttpRequest;
    private SpUtil userSpUtil;
    private LoadingDialog mDialog;
    private CustomToast customToast;
    private String userId;
    /**
     * 好友id list
     */
    private List<FriendIdsDTO.ResultBean> friendList;
    private List<String> idList;
    /**
     * 当前页面用户的 UserInfo
     */
    private UserInfo userInfo;
    /**
     * 当前页面用户的 UserId
     */
    private String currentUserId;
    private boolean isSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.de_fr_personal_intro);

        Toolbar toolbar = (Toolbar) findViewById(R.id.conver_toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setTitle(getResources().getString(R.string.de_actionbar_detail));
        ab.setHomeAsUpIndicator(R.mipmap.de_actionbar_back);
        ab.setDisplayHomeAsUpEnabled(true);
        userSpUtil = new SpUtil(this, Constants.USER_SPUTIL);
        customToast = CustomToast.createToast(this);
        mPersonalImg = (AsyncImageView) findViewById(R.id.personal_portrait);
        mPersonalName = (TextView) findViewById(R.id.personal_name);
        mPersonalId = (TextView) findViewById(R.id.personal_id);
        mSendMessage = (Button) findViewById(R.id.send_message);
        mAddFriend = (Button) findViewById(R.id.add_friend);
        mDialog = new LoadingDialog(this);

        mSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (RongIM.getInstance() != null) {
                    if (currentUserId != null)
                        ApiClient.findUserById(currentUserId, new Callback<UserDTO>() {
                            @Override
                            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                                UserDTO body = response.body();
                                if(body.getCode() == 200){
                                    String userName = body.getResult().getUsername();
                                    RongIM.getInstance().startPrivateChat(PersonalDetailActivity.this, currentUserId,userName);
                                }
                            }
                            @Override
                            public void onFailure(Call<UserDTO> call, Throwable t) {
                                t.printStackTrace();
                            }
                        });
                }
            }
        });

        mAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUserId != null) {
                    if (mDialog != null && !mDialog.isShowing())
                        mDialog.show();
                    if(userId.trim() == null) return;
                    ApiClient.applyAddFriend(userId, currentUserId, new Callback<ResultDTO>() {
                        @Override
                        public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                            ResultDTO body = response.body();
                            if(body.getCode() == 200){
                                sendMessage(userId);
                            }
                        }
                        @Override
                        public void onFailure(Call<ResultDTO> call, Throwable t) {

                        }
                    });
                }
            }
        });
        initData();
    }

    /**
     * 发送单聊信息
     */
    private void sendMessage(String userId){

        ApiClient.sendMessage(userId, currentUserId, "请求添加好友", App.ContactNtf, new Callback<ResultDTO>() {
            @Override
            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                ResultDTO body = response.body();
                if(body.getCode() == 200){
                    customToast.setMessage("好友请求发送成功").show();
                }else{
                    customToast.setMessage("好友请求发送失败").show();
                }
                mDialog.dismiss();
            }
            @Override
            public void onFailure(Call<ResultDTO> call, Throwable t) {
                t.printStackTrace();
                mDialog.dismiss();
            }
        });
    }

    protected void initData() {
        friendList = new ArrayList();
        idList = new ArrayList<String>();
        userId = userSpUtil.getKeyValue("userId");
        ApiClient.findFriendIds(userId, new Callback<FriendIdsDTO>() {
            @Override
            public void onResponse(Call<FriendIdsDTO> call, Response<FriendIdsDTO> response) {
                FriendIdsDTO body = response.body();
                if(body.getCode() == 200){
                   friendList = body.getResult();
                }
            }
            @Override
            public void onFailure(Call<FriendIdsDTO> call, Throwable t) {
                    t.printStackTrace();
            }
        });

        if (getIntent().hasExtra("USER")) {
            userInfo = getIntent().getParcelableExtra("USER");
            currentUserId = userInfo.getUserId();
            if (userInfo != null && friendList != null) {
                //获取所有的好友id
                for(FriendIdsDTO.ResultBean fsb : friendList){
                    idList.add(fsb.getFriendid());
                }

                if (idList.contains(userInfo.getUserId())) {
                    mAddFriend.setVisibility(View.GONE);
                    mSendMessage.setVisibility(View.VISIBLE);
                    mPersonalId.setText("Id: " + userInfo.getUserId());
                } else {
                    mAddFriend.setVisibility(View.VISIBLE);
                    mSendMessage.setVisibility(View.GONE);
                }
                mPersonalImg.setResource(new Resource(userInfo.getPortraitUri()));
                mPersonalName.setText(userInfo.getName());
            }
        } else if (getIntent().hasExtra("CONTACTS_USER")) {
            clickFromContacts();
        } else if (getIntent().hasExtra("USER_SEARCH")) {
            isSearch = getIntent().getBooleanExtra("USER_SEARCH", false);
            mAddFriend.setVisibility(View.VISIBLE);
            mSendMessage.setVisibility(View.GONE);
            mPersonalImg.setResource(new Resource(userInfo.getPortraitUri()));
            mPersonalName.setText(userInfo.getName());
            currentUserId = userInfo.getUserId();
        }

          if (currentUserId != null)
          //  getUserInfoByUserIdHttpRequest = DemoContext.getInstance().getDemoApi().getUserInfoByUserId(currentUserId, this);

            ApiClient.findUserById(currentUserId, new Callback<UserDTO>() {
                @Override
                public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                    UserDTO body = response.body();
                    if(body.getCode() == 200){
                    }
                }
                @Override
                public void onFailure(Call<UserDTO> call, Throwable t) {
                }
            });

    }

    /**
     * 从通讯录跳转到个人详情界面
     */
    private void clickFromContacts() {
        currentUserId = getIntent().getStringExtra("CONTACTS_USER");
        //userInfo = DemoContext.getInstance().getUserInfoById(currentUserId);

        ApiClient.findUserById(currentUserId, new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                UserDTO body = response.body();
                if(body.getCode() == 200){
                    mPersonalImg.setResource(new Resource(body.getResult().getIcon()));
                    mPersonalName.setText(body.getResult().getUsername());
                    mPersonalId.setText("Id:" + body.getResult().getUserid());
                    mAddFriend.setVisibility(View.GONE);
                    mSendMessage.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {

            }
        });
    }

    @Override
    public void onCallApiSuccess(AbstractHttpRequest request, Object obj) {
        if (mDeleteFriendRequest != null && mDeleteFriendRequest.equals(request)) {
            if (mDialog != null)
                mDialog.dismiss();
            if (obj instanceof Status) {
                final Status status = (Status) obj;
                if (status.getCode() == 200) {
                    WinToast.toast(this, "删除好友成功");
//                    if (DemoContext.getInstance() != null && currentUserId != null) {
//                        //删除好友成功后，将这个好友的会话从会话列表删除
//                        RongIM.getInstance().getRongIMClient().removeConversation(Conversation.ConversationType.PRIVATE, currentUserId);
//                        DemoContext.getInstance().updateUserInfos(currentUserId, "2");
//
//                        Intent intent = new Intent();
//                        this.setResult(Constants.DELETE_USERNAME_REQUESTCODE, intent);
//
//                    }
                } else if (status.getCode() == 306) {
                    WinToast.toast(this, status.getMessage());
                }
            }
        } else if (getUserInfoByUserIdHttpRequest != null && getUserInfoByUserIdHttpRequest.equals(request)) {
            if (obj instanceof User) {
                final User user = (User) obj;

                if (user.getCode() == 200) {

                    UserInfos addFriend = new UserInfos();
                    addFriend.setUsername(user.getResult().getUsername());
                    addFriend.setUserid(user.getResult().getId());
                    addFriend.setPortrait(user.getResult().getPortrait());
                    if (friendList.contains(user.getResult().getId())) {
                        addFriend.setStatus("1");
                    } else {
                        addFriend.setStatus("0");
                    }

                  //  if (DemoContext.getInstance() != null)
                  //      DemoContext.getInstance().insertOrReplaceUserInfos(addFriend);

                    mPersonalName.setText(user.getResult().getUsername());

                    RongIM.getInstance().refreshUserInfoCache(new UserInfo(user.getResult().getId(), user.getResult().getUsername(), Uri.parse(user.getResult().getPortrait())));
                }
            }
        }else if(mUserHttpRequest!=null && mUserHttpRequest.equals(request)){
            if (mDialog != null)
                mDialog.dismiss();

            WinToast.toast(this, "好友请求发送成功");
        }
    }

    @Override
    public void onCallApiFailure(AbstractHttpRequest request, BaseException e) {
        if (mDialog != null)
            mDialog.dismiss();
        Log.e("PersonalDetailActivity","-----onCallApiFailure------"+e);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.per_menu, menu);

        if (userInfo != null && friendList != null) {
            if (!friendList.contains(userInfo.getUserId())) {
                menu.getItem(0).setVisible(false);
            }
        } else if (isSearch) {
            menu.getItem(0).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.per_item1://加入黑名单
//                if (DemoContext.getInstance() != null && RongIM.getInstance().getRongIMClient() != null && currentUserId != null) {
//                    RongIM.getInstance().getRongIMClient().addToBlacklist(currentUserId, new RongIMClient.OperationCallback() {
//                        @Override
//                        public void onSuccess() {
//                            WinToast.toast(PersonalDetailActivity.this, "加入黑名单成功");
//                        }
//
//                        @Override
//                        public void onError(RongIMClient.ErrorCode errorCode) {
//
//                        }
//                    });
//                }

                break;
            case R.id.per_item2://删除好友
                final AlertDialog.Builder alterDialog = new AlertDialog.Builder(this);
                alterDialog.setMessage("是否删除好友？");
                alterDialog.setCancelable(true);

                alterDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

//                        if (DemoContext.getInstance() != null && currentUserId != null) {
//
//                            if (mDialog != null && !mDialog.isShowing())
//                                mDialog.show();
//
//                            mDeleteFriendRequest = DemoContext.getInstance().getDemoApi().deletefriends(currentUserId, PersonalDetailActivity.this);
//                        }
                    }
                });
                alterDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alterDialog.show();

                break;
            case android.R.id.home:
                finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * 头像
     */
    private AsyncImageView mPersonalImg;
    /**
     * 昵称
     */
    private TextView mPersonalName;
    /**
     * 用户 id
     */
    private TextView mPersonalId;
    /**
     * 发送消息
     */
    private Button mSendMessage;
    /**
     * 添加到通讯录
     */
    private Button mAddFriend;


}
