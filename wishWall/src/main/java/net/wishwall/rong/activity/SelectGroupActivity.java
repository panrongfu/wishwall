package net.wishwall.rong.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import net.wishwall.Constants;
import net.wishwall.R;
import net.wishwall.domain.GroupsDTO;
import net.wishwall.rong.adapter.GroupListAdapter;
import net.wishwall.service.ApiClient;
import net.wishwall.utils.DensityUtil;

import java.util.HashMap;
import java.util.List;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import io.codetail.widget.RevealFrameLayout;
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
public class SelectGroupActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener,View.OnClickListener{

    private ListView mListView;
    private TextView title;
    private GroupListAdapter mAllGroupListAdapter;
    private List<GroupsDTO.ResultBean> mResultList;
    private HashMap<String, Group> mGroupMap;
    private int RESULTCODE = 100;
    RevealFrameLayout revealFrameLayout;
    private SupportAnimator mAnimator;
     CardView cardView ;
    ImageView arrows;
    ImageView search;
    EditText mSearchEditText;
    SupportAnimator animator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_group);
        initToolbar();
        initViewUI();
        findAllGroup();
    }

    /**
     * 初始化toolbar
     */
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.select_group_toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        String titleStr = "选择群组";
        SpannableString s = new SpannableString("选择群组");
        s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, titleStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ab.setTitle(s);
        ab.setHomeAsUpIndicator(R.mipmap.back);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    /**
     * 初始化ui
     */
    private void initViewUI() {

        mSearchEditText =(EditText)findViewById(R.id.edit_text_search);
        arrows = (ImageView)findViewById(R.id.arrows);
        search = (ImageView)findViewById(R.id.search_group_btn);
        cardView = (CardView) findViewById(R.id.card_search);
        revealFrameLayout = (RevealFrameLayout)findViewById(R.id.revealFrameLayout);
        revealFrameLayout.setVisibility(View.INVISIBLE);
        mListView = (ListView) findViewById(R.id.all_group_list);
        mListView.setOnItemClickListener(this);
        arrows.setOnClickListener(this);
        search.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.arrows:
                if(animator !=null&&!animator.isRunning()){
                    animator = animator.reverse();
                    animator.setDuration(1000);
                    animator.addListener(new SupportAnimator.SimpleAnimatorListener() {
                        @Override
                        public void onAnimationEnd() {
                            animator = null;
                            cardView.setVisibility(View.GONE);
                            revealFrameLayout.setVisibility(View.GONE);
                        }
                    });
                    animator.start();
                 }
                break;
            case R.id.search_group_btn:
                findGroupByName();
                break;
        }
    }

    /**
     * 根据名称查询群组
     */
    private void findGroupByName() {
        String searchName = mSearchEditText.getText().toString();
        if(TextUtils.isEmpty(searchName)){
            findAllGroup();
        }else{
            ApiClient.findGroupByName(searchName, new Callback<GroupsDTO>() {
                @Override
                public void onResponse(Call<GroupsDTO> call, Response<GroupsDTO> response) {
                    GroupsDTO body = response.body();
                    if(body.getCode() == 200){
                        if(mResultList != null){
                            mResultList.clear();
                        }
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

    /**
     * 查询所有的群
     */
    private void findAllGroup(){
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_group_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_search_group:
                revealFrameLayout.setVisibility(View.VISIBLE);
                cardView.setVisibility(View.VISIBLE);
                mSearchEditText.setEnabled(true);
                mSearchEditText.setFocusable(true);
                mSearchEditText.setFocusableInTouchMode(true);
                animator = ViewAnimationUtils.createCircularReveal(cardView,cardView.getWidth(), cardView.getHeight()/2, 0, 3000);
                animator.setInterpolator(new AccelerateInterpolator());
                animator.setDuration(500);
                animator.addListener(new SupportAnimator.AnimatorListener() {
                    @Override
                    public void onAnimationStart() {
                        ObjectAnimator aninimator = ObjectAnimator.ofFloat(arrows, "translationX", cardView.getWidth()/2,
                                DensityUtil.px2dip(SelectGroupActivity.this,5));
                        aninimator.setInterpolator(new AccelerateInterpolator());
                        aninimator.setDuration(300);
                        aninimator.start();
                    }

                    @Override
                    public void onAnimationEnd() {
                        ObjectAnimator sAnimator = ObjectAnimator.ofFloat(search,"alpha",0,1,0,1);
                        sAnimator.setInterpolator(new AccelerateInterpolator());
                        sAnimator.setDuration(1000);
                        sAnimator.start();
                    }

                    @Override
                    public void onAnimationCancel() {}

                    @Override
                    public void onAnimationRepeat() {}
                });
                animator.start();
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
            bundle.putString("groupIcon",mResultList.get(position).getIcon());
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
                findAllGroup();
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

    static float r(int a, int b) {
        return (float) Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
    }
}