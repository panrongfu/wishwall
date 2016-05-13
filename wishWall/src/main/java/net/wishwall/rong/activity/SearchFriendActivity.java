package net.wishwall.rong.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.sea_monster.network.AbstractHttpRequest;

import net.wishwall.Constants;
import net.wishwall.R;
import net.wishwall.activities.PersonDetailActivity;
import net.wishwall.domain.FriendLikeNameDTO;
import net.wishwall.rong.adapter.SearchFriendAdapter;
import net.wishwall.rong.model.Friends;
import net.wishwall.rong.widget.LoadingDialog;
import net.wishwall.service.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Bob on 2015/3/26.
 */
public class SearchFriendActivity extends BaseActivity {

    private EditText mEtSearch;
    private ListView mListSearch;
    private AbstractHttpRequest<Friends> searchHttpRequest;
    private List<FriendLikeNameDTO.ResultBean> mResultList;
    private SearchFriendAdapter adapter;
    private LoadingDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.de_ac_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.conver_toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setTitle(getResources().getString(R.string.public_account_search));
        ab.setHomeAsUpIndicator(R.mipmap.de_actionbar_back);
        ab.setDisplayHomeAsUpEnabled(true);

        mEtSearch = (EditText) findViewById(R.id.de_ui_search);
        Button mBtSearch = (Button) findViewById(R.id.de_search);
        mListSearch = (ListView) findViewById(R.id.de_search_list);
        mResultList = new ArrayList<FriendLikeNameDTO.ResultBean>();
        mDialog = new LoadingDialog(this);

        assert mBtSearch != null;
        mBtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = mEtSearch.getText().toString();
                if (mDialog != null && !mDialog.isShowing())
                    mDialog.show();

                ApiClient.findUsersLikeName(userName, new Callback<FriendLikeNameDTO>() {
                    @Override
                    public void onResponse(Call<FriendLikeNameDTO> call, Response<FriendLikeNameDTO> response) {
                        FriendLikeNameDTO body = response.body();
                        if (mDialog != null) mDialog.dismiss();
                        if(body.getCode() == 200){
                            mResultList = body.getResult();
                            adapter = new SearchFriendAdapter(mResultList, SearchFriendActivity.this);
                            mListSearch.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<FriendLikeNameDTO> call, Throwable t) {
                        if (mDialog != null)
                            mDialog.dismiss();
                    }
                });
            }
        });

        mListSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent in = new Intent(SearchFriendActivity.this, PersonDetailActivity.class);
//                UserInfo userInfo = new UserInfo(mResultList.get(position).getUserid(), mResultList.get(position).getUsername(), Uri.parse(mResultList.get(position).getIcon()));
//                in.putExtra("USER", userInfo);
                in.putExtra("USER_SEARCH", mResultList.get(position).getUserid());
                startActivityForResult(in, Constants.SEARCH_REQUESTCODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Constants.PERSONAL_REQUESTCODE) {
            Intent intent = new Intent();
            this.setResult(Constants.SEARCH_REQUESTCODE, intent);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
