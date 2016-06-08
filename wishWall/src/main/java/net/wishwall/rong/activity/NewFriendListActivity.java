package net.wishwall.rong.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.sea_monster.network.AbstractHttpRequest;

import net.wishwall.Constants;
import net.wishwall.R;
import net.wishwall.domain.ApplyAddFriendListDTO;
import net.wishwall.domain.ResultDTO;
import net.wishwall.rong.adapter.NewFriendListAdapter;
import net.wishwall.rong.fragment.ChatFragmentActivity;
import net.wishwall.rong.model.Friends;
import net.wishwall.rong.model.Status;
import net.wishwall.rong.widget.LoadingDialog;
import net.wishwall.service.ApiClient;
import net.wishwall.utils.SpUtil;
import net.wishwall.views.CustomToast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Bob on 2015/3/26.
 */
public class NewFriendListActivity extends BaseActivity implements Handler.Callback {

    private static final String TAG = NewFriendListActivity.class.getSimpleName();
    private AbstractHttpRequest<Friends> getFriendHttpRequest;
    private AbstractHttpRequest<Status> mRequestFriendHttpRequest;

    private ListView mNewFriendList;
    private NewFriendListAdapter adapter;
    private List<ApplyAddFriendListDTO.ResultBean> mResultList;
    private LoadingDialog mDialog;
    private Handler mHandler;
    private SpUtil userSpUtil;
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.de_ac_new_friendlist);
        initView();

    }
    protected void initView() {
        userSpUtil = new SpUtil(this,Constants.USER_SPUTIL);
        Toolbar toolbar = (Toolbar) findViewById(R.id.conver_toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setTitle("");
        ab.setHomeAsUpIndicator(R.mipmap.de_actionbar_back);
        ab.setDisplayHomeAsUpEnabled(true);
        mNewFriendList = (ListView) findViewById(R.id.de_new_friend_list);
        mDialog = new LoadingDialog(this);
        mResultList = new ArrayList<ApplyAddFriendListDTO.ResultBean>();
        mHandler = new Handler(this);
        userId = userSpUtil.getKeyValue("userId");
        if(userId.trim() == null) return;
        ApiClient.applyAddFriendList(userId, new Callback<ApplyAddFriendListDTO>() {
            @Override
            public void onResponse(Call<ApplyAddFriendListDTO> call, Response<ApplyAddFriendListDTO> response) {
                ApplyAddFriendListDTO body = response.body();
                if(body.getCode() == 200){
                    mResultList = body.getResult();
                    adapter = new NewFriendListAdapter(mResultList, NewFriendListActivity.this);
                    mNewFriendList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    adapter.setOnItemButtonClick(mOnItemButtonClick);
                }else{
                    CustomToast.showMsg(NewFriendListActivity.this,body.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ApplyAddFriendListDTO> call, Throwable t) {
                t.printStackTrace();
            }
        });

        Intent in = new Intent();
        in.setAction(ChatFragmentActivity.ACTION_DMEO_RECEIVE_MESSAGE);
        in.putExtra("has_message", false);
        sendBroadcast(in);
    }

    NewFriendListAdapter.OnItemButtonClick mOnItemButtonClick = new NewFriendListAdapter.OnItemButtonClick() {

        @Override
        public boolean onButtonClick(final int position, View view, int status) {
            switch (status) {
                case 1://好友

                    break;
                case 2://请求添加

                    break;
                case 3://请求被添加
                    mResultList.get(position).getUserid();
                    if (mDialog != null && !mDialog.isShowing()) {
                        mDialog.show();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final String friendid = mResultList.get(position).getUserid();
                            ApiClient.addFriend(userId, friendid, new Callback<ResultDTO>() {
                                @Override
                                public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                                    ResultDTO body = response.body();
                                    if(body.getCode() == 200){
                                        ApplyAddFriendListDTO.ResultBean fr = mResultList.get(position);
                                        fr.setStatus(1);
                                        mResultList.set(position,mResultList.get(position));

                                        Message mess = Message.obtain();
                                        mess.obj = mResultList;
                                        mess.what = 1;
                                        mHandler.sendMessage(mess);
                                        sendMessage(friendid);
                                    }else {
                                        CustomToast.showMsg(getApplicationContext(),body.getMessage());
                                    }
                                }
                                @Override
                                public void onFailure(Call<ResultDTO> call, Throwable t) {
                                        t.printStackTrace();
                                }
                            });
                        }
                    });
                    break;
                case 4://请求被拒绝

                    break;
                case 5://我被对方删除

                    break;
            }

            return false;
        }
    };

    /**
     * 添加好友成功后，向对方发送一条消息
     *
     * @param friendid 对方id
     */
    private void sendMessage(String friendid) {
        ApiClient.sendMessage(userId, friendid,"我们开始对话吧", Constants.ContactNtf, Constants.ADD,new Callback<ResultDTO>() {
            @Override
            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                ResultDTO body = response.body();
                if(body.getCode() == 200){
                    CustomToast.showMsg(getApplicationContext(),"添加好友成功");
                    if(mDialog !=null) mDialog.dismiss();
                    mDialog.dismiss();
                }else{
                    CustomToast.showMsg(getApplicationContext(),"添加好友失败");
                    if(mDialog !=null) mDialog.dismiss();
                }
            }
            @Override
            public void onFailure(Call<ResultDTO> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void updateAdapter(List<ApplyAddFriendListDTO.ResultBean> mResultList) {
        if (adapter != null) {
            adapter = null;
        }
        adapter = new NewFriendListAdapter(mResultList, NewFriendListActivity.this);
        mNewFriendList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.setOnItemButtonClick(mOnItemButtonClick);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.de_add_friend_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Constants.SEARCH_REQUESTCODE) {

            if (adapter != null) {
                adapter = null;
                mResultList.clear();
            }

//            if (DemoContext.getInstance() != null) {
//                getFriendHttpRequest = DemoContext.getInstance().getDemoApi().getNewFriendlist(this);
//
//            }
            if (mDialog != null && !mDialog.isShowing()) {
                mDialog.show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.icon:
                Intent intent = new Intent(NewFriendListActivity.this, SearchFriendActivity.class);
                startActivityForResult(intent, Constants.FRIENDLIST_REQUESTCODE);
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 1:
                mResultList = (List<ApplyAddFriendListDTO.ResultBean>) msg.obj;
                updateAdapter(mResultList);
                if (mDialog != null)
                    mDialog.dismiss();
                break;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        if (adapter != null) {
            adapter = null;
        }
        super.onDestroy();
    }
}
