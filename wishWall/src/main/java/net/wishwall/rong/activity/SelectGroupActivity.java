package net.wishwall.rong.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import net.wishwall.Constants;
import net.wishwall.R;
import net.wishwall.domain.GroupsDTO;
import net.wishwall.rong.adapter.GroupListAdapter;
import net.wishwall.service.ApiClient;
import net.wishwall.views.CustomToast;

import java.util.HashMap;
import java.util.List;

import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Group;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author panRongFu on 2016/5/9.
 * @Description
 * @email pan@ipushan.com
 */
public class SelectGroupActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private ListView mListView;
    private TextView title;
    private GroupListAdapter mAllGroupListAdapter;
    private List<GroupsDTO.ResultBean> mResultList;
    private HashMap<String, Group> mGroupMap;
    private int RESULTCODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_group);
        initToolbar();
        initViewUI();
        initDatas();
    }

    /**
     * 初始化toolbar
     */
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.group_select_toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setTitle("");
        ab.setHomeAsUpIndicator(R.mipmap.back);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    /**
     * 初始化ui
     */
    private void initViewUI() {
        title = (TextView)findViewById(R.id.select_group_title);
        title.setText(R.string.add_select_group);
        mListView = (ListView) findViewById(R.id.all_group_list);
        mListView.setOnItemClickListener(this);
    }

    private void initDatas() {
        ApiClient.findAllGroups(new Callback<GroupsDTO>() {
            @Override
            public void onResponse(Call<GroupsDTO> call, Response<GroupsDTO> response) {
                GroupsDTO body = response.body();
                if (body.getCode() == 200) {
                    mResultList = body.getResult();
                    mAllGroupListAdapter = new GroupListAdapter(SelectGroupActivity.this, mResultList, mGroupMap);
                    mAllGroupListAdapter.setType(GroupListAdapter.Type.ALL);
                    mListView.setAdapter(mAllGroupListAdapter);
                    // mMyGroupListAdapter.setOnItemButtonClick(onItemButtonClick);
                }
            }

            @Override
            public void onFailure(Call<GroupsDTO> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_group,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_search_group:
                CustomToast.showMsg(this,"搜索群组");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mResultList != null && position != -1 && position < mResultList.size()) {

            Uri uri = Uri.parse("demo://" + getApplicationInfo().packageName).buildUpon()
                    .appendPath("conversationSetting")
                    .appendPath(String.valueOf(Conversation.ConversationType.GROUP))
                    .appendQueryParameter("targetId", mResultList.get(position).getGroupid()).build();

            Intent intent = new Intent(this, GroupDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("groupName", mResultList.get(position).getName());
            bundle.putString("number", mResultList.get(position).getNumber() + "");
            bundle.putString("maxNumber", mResultList.get(position).getMax_number() + "");
            bundle.putString("introduce", mResultList.get(position).getIntroduce());
            bundle.putString("groupId", mResultList.get(position).getGroupid());
            intent.putExtra("INTENT_GROUP", bundle);
            intent.setData(uri);
            startActivityForResult(intent, RESULTCODE);
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
     * 刷新群组列表
     */
    private void refreshAdapter() {

        ApiClient.findAllGroups(new Callback<GroupsDTO>() {
            @Override
            public void onResponse(Call<GroupsDTO> call, Response<GroupsDTO> response) {
                GroupsDTO body = response.body();
                if (body.getCode() == 200) {
                    mResultList = body.getResult();
                    mAllGroupListAdapter = new GroupListAdapter(SelectGroupActivity.this, mResultList, mGroupMap);
                    mListView.setAdapter(mAllGroupListAdapter);
                }
            }

            @Override
            public void onFailure(Call<GroupsDTO> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}