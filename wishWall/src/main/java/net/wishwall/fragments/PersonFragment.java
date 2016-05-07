package net.wishwall.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.wishwall.R;
import net.wishwall.activities.IssueWishActivity;
import net.wishwall.activities.MainActivity;
import net.wishwall.adapter.WishAdapter;
import net.wishwall.domain.WishsDTO;
import net.wishwall.service.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author panRongFu on 2016/4/22.
 * @Description
 * @email pan@ipushan.com
 */
public class PersonFragment extends Fragment
        implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener {
    MainActivity activity;
    private TextView toolarTitle;
    private FloatingActionButton issueWish;

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefreshlayout;
    private LinearLayoutManager layoutManager;
    private GridLayoutManager gridLayout;
    private WishAdapter adapter;
    boolean isLoading = false;
    private static List<WishsDTO.ResultBean> myWishList = new ArrayList<WishsDTO.ResultBean>();
    private AppBarLayout appBarLayout;
    private IssueWishActivity issueWishActivity;
    private Type mType = Type.REFRESH;
    private static int page=1;

    private enum Type{
        REFRESH,LOAD
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IssueWishActivity.setOnIssueFinishListener(new IssueWishActivity.OnIssueFinishListener() {
            @Override
            public void finish() {
                initData(Type.REFRESH);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity)context;
        //drawerActivity.getActionBar().hide();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.person_fragment,container,false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.person_toolbar);
       // activity.setSupportActionBar(toolbar);

       // final ActionBar actionBar = getSupportActionBar();
       // actionBar.setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("个人中心");
        initViewUI(view);
        initData(mType);
        return view;
    }

    /**
     * 获取许愿条
     */
    private void initData(final Type type) {
        ApiClient.findMyWish(page, new Callback<WishsDTO>() {
            @Override
            public void onResponse(Call<WishsDTO> call, Response<WishsDTO> response) {
                WishsDTO body = response.body();
                if(body.getCode() == 200){
                    myWishList = body.getResult();
                    if(type == Type.REFRESH){
                        mRefreshlayout.setRefreshing(false);
                    }else if(type == Type.LOAD){
                        isLoading = false;
                    }
                    if(adapter == null){
                        adapter = new WishAdapter(getActivity());
                        adapter.setWishList(myWishList);
                        mRecyclerView.setAdapter(adapter);
                    }else {
                        adapter.setWishList(myWishList);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onFailure(Call<WishsDTO> call, Throwable t) {
                    t.printStackTrace();
            }
        });
    }

    private void initViewUI(View view) {
        layoutManager = new LinearLayoutManager(getActivity());
        appBarLayout = (AppBarLayout) view.findViewById(R.id.appbar);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.person_recycler_view);
        mRefreshlayout = (SwipeRefreshLayout) view.findViewById(R.id.person_swipe_refresh_view);
        mRecyclerView.setLayoutManager(layoutManager);
        mRefreshlayout.setOnRefreshListener(this);
        mRecyclerView.addOnScrollListener(new LoadMoreScrollListener());
        issueWish =(FloatingActionButton) view.findViewById(R.id.issue_wish);
        issueWish.setOnClickListener(this);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                mRefreshlayout.setEnabled(verticalOffset == 0);
            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.issue_wish:
                startActivity(new Intent(getActivity(), IssueWishActivity.class));
                break;
        }
    }

    @Override
    public void onRefresh() {
        mRefreshlayout.setRefreshing(true);
        page = 1;
        myWishList.clear();
        initData(Type.REFRESH);
    }

    /**
     * 加载更多
     */
    class LoadMoreScrollListener extends RecyclerView.OnScrollListener{

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            mRefreshlayout.setEnabled(layoutManager.findFirstCompletelyVisibleItemPosition() == 0);
            super.onScrolled(recyclerView, dx, dy);
            int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
            //若最后一个可见tiem的
            if(lastVisibleItemPosition + 1 == adapter.getItemCount() && adapter.getItemCount()>=10){
                boolean isRefreshing = mRefreshlayout.isRefreshing();
                if(isRefreshing){
                    adapter.notifyItemRemoved(adapter.getItemCount());
                    return;
                }
                if(!isLoading){
                    isLoading = true;
                    page = page+1;
                    initData(Type.LOAD);
                }
            }
        }
    }

}
