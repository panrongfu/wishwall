package net.wishwall.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.wishwall.Constants;
import net.wishwall.R;
import net.wishwall.activities.MainActivity;
import net.wishwall.adapter.WishAdapter;
import net.wishwall.domain.WishsDTO;
import net.wishwall.service.ApiClient;
import net.wishwall.utils.SpUtil;

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
public class FindFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener
       {

    private static int page = 1;
    private static MainActivity activity;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefreshlayout;
    private LinearLayoutManager layoutManager;
    private WishAdapter adapter;
    boolean isLoading = false;
    private SpUtil localSputil;
    private Type mType = Type.REFRESH;
    private  List<WishsDTO.ResultBean> wishList = new ArrayList<WishsDTO.ResultBean>();
    private SpUtil userSputi;
    private static String userId;
    private String searchName;
    private LoadType mLoadType = LoadType.COMMON;

    /**
     * 请求类型 普通请求，搜索请求
     */
    private enum LoadType{
        COMMON,SEARCH
    }

    /**
     * 加载类型 刷新、加载
     */
    private enum Type{
        REFRESH,LOAD
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        localSputil = new SpUtil(getActivity(),"location");
        userSputi = new SpUtil(getActivity(), Constants.USER_SPUTIL);
        userId = userSputi.getKeyValue("userId");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity)context;
        activity.setOnSelectCityListener(new MainActivity.OnSelectCityListener() {
            @Override
            public void finish(String cityName) {
                initData(mType,cityName);
                mLoadType = LoadType.COMMON;
            }
        });
        activity.setOnLocationFinishListener(new MainActivity.OnLocationFinishListener() {
            @Override
            public void finish(String cityName) {
                if(wishList ==null){
                    initData(mType,cityName);
                    mLoadType = LoadType.COMMON;
                }
            }
        });
        activity.setOnSearchListener(new MainActivity.OnSearchListener() {
            @Override
            public void search(String content) {
                searchName = content;
                //如果搜索名称为空则按照当前用户的城市查询
                if(TextUtils.isEmpty(searchName)){
                    initData(mType,MainActivity.cityName);
                    mLoadType = LoadType.COMMON;
                }else{
                    initSearchData(Type.REFRESH,searchName);
                    mLoadType = LoadType.SEARCH;
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.find_fragment, null, false);
        initViewUI(view);
        return view;
    }

    private void initViewUI(View view) {

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRefreshlayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_view);
        mRefreshlayout.setOnRefreshListener(this);
        mRecyclerView.addOnScrollListener(new LoadMoreScrollListener());
        String cityName = localSputil.getKeyValue("cityName");
        initData(mType,"广州市");
    }
    /**
     * 初始化数据
     */
    private void initData( final Type mType, String cityName) {

        ApiClient.findWishByCity(page, cityName, new Callback<WishsDTO>() {
            @Override
            public void onResponse(Call<WishsDTO> call, Response<WishsDTO> response) {
                WishsDTO body = response.body();
                if(body.getCode() == 200){
                    wishList = body.getResult();
                    initRecyclerView(wishList,mType);
                }
            }
            @Override
            public void onFailure(Call<WishsDTO> call, Throwable t) {
                    t.printStackTrace();
            }
        });
    }

    /**
     * 加载搜索的数据
     * @param content
     */
    private void initSearchData(final Type type,String content) {
        ApiClient.findWishByName(page, content, new Callback<WishsDTO>() {
            @Override
            public void onResponse(Call<WishsDTO> call, Response<WishsDTO> response) {
                WishsDTO body = response.body();
                if(body.getCode() == 200){
                    wishList = body.getResult();
                    initRecyclerView(wishList,type);
                }
            }
            @Override
            public void onFailure(Call<WishsDTO> call, Throwable t) {
                    t.printStackTrace();
            }
        });
    }

    /**
     * 为recyclerView 填充数据
     * @param wishList
     */
    private void initRecyclerView(List<WishsDTO.ResultBean> wishList,Type type) {
        if(type == Type.REFRESH){
            mRefreshlayout.setRefreshing(false);
        }else if(type == Type.LOAD){
            isLoading = false;
        }
        if(adapter == null){
            adapter = new WishAdapter(getActivity());
            adapter.setWishList(wishList);
            mRecyclerView.setAdapter(adapter);
        }else {
            adapter.setWishList(wishList);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onRefresh() {
        mRefreshlayout.setRefreshing(true);
        page = 1;
        wishList.clear();
        if(mLoadType == LoadType.COMMON){
            initData(Type.REFRESH,MainActivity.cityName);
        }else if(mLoadType == LoadType.SEARCH){
            initSearchData(Type.REFRESH,searchName);
        }
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
                    if(mLoadType == LoadType.COMMON){
                        initData(Type.LOAD,MainActivity.cityName);
                    }else if(mLoadType == LoadType.SEARCH){
                        initSearchData(Type.LOAD,searchName);
                    }
                }
            }
        }
    }
}

