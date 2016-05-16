package net.wishwall.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.wishwall.Constants;
import net.wishwall.R;
import net.wishwall.activities.InputPopupWindow;
import net.wishwall.activities.MainActivity;
import net.wishwall.activities.PersonDetailActivity;
import net.wishwall.domain.ResultDTO;
import net.wishwall.domain.WishsDTO;
import net.wishwall.service.ApiClient;
import net.wishwall.utils.DateTimeUtil;
import net.wishwall.utils.SpUtil;
import net.wishwall.views.RecyclerViewItemDecoration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author panRongFu on 2016/4/30.
 * @Description
 * @email pan@ipushan.com
 */
public class WishAdapter extends RecyclerView.Adapter<WishAdapter.WishHolder>{

    private static List<WishsDTO.ResultBean> mWishList;
    private LayoutInflater mInflater;
    private Context mContext;
    //private static GridLayoutManager gridLayout;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private InputPopupWindow popupWindow;
    private InputMethodManager mMinputMethodManager;
    private SpUtil userSputil;
    private static String userId ;
    private static String nickName;
    private static int LIKE = 0X00;
    private static int COMM = 0X01;
    private static int UNLIKE = 0X11;
    private static WishHolder wishHolder;
   // private static WishCommAdapter wishCommAdapter;
    private EditText mEditext;
    private  Handler likeCommHanlder = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == LIKE || msg.what == UNLIKE){
                likeWish((WishHolder) msg.obj,msg.arg1);
            }else if(msg.what == COMM){
                commWish((WishHolder) msg.obj,msg.arg1);
            }
        }
    };

    public void setWishList(List<WishsDTO.ResultBean> wishList) {
        this.mWishList = wishList;
    }

    public List<WishsDTO.ResultBean> getWishList() {
        return mWishList;
    }

    public WishAdapter(Context context){
        mContext = context;
        userSputil = new SpUtil(context, Constants.USER_SPUTIL);
        mMinputMethodManager = (InputMethodManager) mContext.getSystemService(mContext.INPUT_METHOD_SERVICE);

        userId = userSputil.getKeyValue("userId");
        nickName = userSputil.getKeyValue("nickName");
        mInflater = LayoutInflater.from(context);
    }

    /**
     * 初始化popupwindow
     */
    private void initPopupWindow(final String wishId, final int position, final WishHolder holder) {
        popupWindow = new InputPopupWindow(mContext);

        popupWindow.setAnimationStyle(R.style.popupwindow);
        // popupWindow.showAtLocation(mContext.get., Gravity.BOTTOM,0,20);
        // popupWindow.showAsDropDown(v);
        popupWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        mMinputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        //位于底部，输入法出现时将其顶起，最终显示在输入法的顶部
        popupWindow.showAtLocation(MainActivity.mainActiviyView, Gravity.BOTTOM, 0, 0);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popupWindow.dismiss();
            }
        });
        popupWindow.setOnCommSendListener(new InputPopupWindow.OnCommSendListener() {
            @Override
            public void send(final EditText editText) {
                mEditext =editText;
                final String commText = editText.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ApiClient.commWish(wishId, commText, new Callback<ResultDTO>() {
                            @Override
                            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                                ResultDTO body = response.body();
                                if(body.getCode() == 200){
                                    //更新wishlist的数据，为了让用户马上看到评论的效果
                                    WishsDTO.ResultBean.WishCommBean commBean = new WishsDTO.ResultBean.WishCommBean();
                                    commBean.setUserid(userId);
                                    commBean.setNickname(nickName);
                                    commBean.setContent(commText);
                                    mWishList.get(position).getWish_comm().add(commBean);
                                    //commBean.setIcon();
                                    Message msg = Message.obtain();
                                    msg.what = COMM;
                                    msg.obj = holder;
                                    msg.arg1 = position;
                                    likeCommHanlder.sendMessage(msg);
                                }
                            }
                            @Override
                            public void onFailure(Call<ResultDTO> call, Throwable t) {

                            }
                        });
                    }
                }).start();
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if(position+1 == getItemCount()&& getItemCount()>=10){
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    @Override
    public WishHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_ITEM){
            View view = mInflater.inflate(R.layout.my_wish_list_item,parent,false);
            wishHolder = new WishHolder(view);
            return wishHolder;
        }
            View footer = mInflater.inflate(R.layout.footerview,parent,false);
            return new FooterViewHolder(footer);
    }

    @Override
    public void onBindViewHolder(final WishHolder holder, final int position) {

        if(position+1 == getItemCount()&& getItemCount()>=10) return;
        //设置头item头像
        String path = mWishList.get(position).getIcon();
        if(!TextUtils.isEmpty(path)){
            Picasso.with(mContext).load(path).into(holder.itemImg);
        }
        //设置item的用户名
        holder.itemNam.setText(mWishList.get(position).getNickname());
        //设置item时间
        holder.itemTime.setText(DateTimeUtil.yyyyMMddHHmm(mWishList.get(position).getTime()));
        //设置item内容
        holder.itemContent.setText(mWishList.get(position).getContent());

        if(mWishList.get(position).getWish_img().size() != 0){
            GridLayoutManager gridLayout = new GridLayoutManager(mContext, 4);
            gridLayout.setOrientation(OrientationHelper.VERTICAL);
            holder.itemImgGrid.setLayoutManager(gridLayout);
            holder.itemImgGrid.addItemDecoration(new RecyclerViewItemDecoration(
                    RecyclerViewItemDecoration.MODE_GRID,
                    Color.WHITE,10,5,0));
            holder.itemImgGrid.setAdapter(new WishItemAdapter(mContext,mWishList.get(position).getWish_img()));

        }
        if( mWishList.get(position).getWish_like().size() != 0 ){
            likeWish(holder,position);
        }

        if(mWishList.get(position).getWish_comm().size() != 0){
            commWish(holder,position);
        }
        /**
         * 用户头像点击监听
         */
        holder.itemImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemUserId = mWishList.get(position).getUserid();
                String itemUserName = mWishList.get(position).getUsername();
                Intent intent = new Intent(mContext, PersonDetailActivity.class);
                intent.putExtra("WISH_ITME",itemUserId);
                mContext.startActivity(intent);
            }
        });

        /**
         * 点赞监听
         */
        holder.itemLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.itemLike.setBackgroundResource(R.mipmap.like_blue);
                final String wishId = mWishList.get(position).getWishid();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ApiClient.likeWish(wishId, Constants.LIKE, new Callback<ResultDTO>() {
                            @Override
                            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                                ResultDTO body = response.body();
                                if(body.getCode() == 200){
                                    //更新wishlist的数据，为了让用户马上看到点赞的效果
                                    WishsDTO.ResultBean.WishLikeBean likeBean = new WishsDTO.ResultBean.WishLikeBean();
                                    likeBean.setUserid(userId);
                                    likeBean.setNickname(nickName);
                                    mWishList.get(position).getWish_like().add(likeBean);

                                    Message msg = Message.obtain();
                                    msg.what = LIKE;
                                    msg.obj = holder;
                                    msg.arg1 = position;
                                    likeCommHanlder.sendMessage(msg);
                                }
                            }
                            @Override
                            public void onFailure(Call<ResultDTO> call, Throwable t) {
                                        t.printStackTrace();
                            }
                        });

                    }
                }).start();
            }
        });
        /**
         * 评论监听
         */
        holder.itemComm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String wishId =mWishList.get(position).getWishid();
                initPopupWindow(wishId,position,holder);

            }
        });

        holder.itemLlyout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String wishId =mWishList.get(position).getWishid();
                initPopupWindow(wishId,position,holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mWishList.size();
    }

    public class FooterViewHolder extends WishHolder {
        public FooterViewHolder(View view) {
            super(view);
        }
    }

    public static class  WishHolder extends RecyclerView.ViewHolder{
        View mView;
        CircleImageView itemImg;
        TextView itemNam;
        TextView itemTime;
        TextView itemContent;
        RecyclerView itemImgGrid;
        TextView itemLikeList;
        RecyclerView itemCommGrid;
        ImageView itemLike;
        ImageView itemComm;
        LinearLayout itemLlyout;

        public WishHolder(View v) {
            super(v);
            itemImg = (CircleImageView)v.findViewById(R.id.my_wish_item_img);
            itemNam = (TextView)v.findViewById(R.id.my_wish_item_name);
            itemTime = (TextView)v.findViewById(R.id.my_wish_item_time);
            itemContent = (TextView)v.findViewById(R.id.my_wish_item_content);
            itemImgGrid = (RecyclerView)v.findViewById(R.id.wish_item_img_list);
            itemLikeList = (TextView)v.findViewById(R.id.wish_item_like_list);
            itemCommGrid = (RecyclerView)v.findViewById(R.id.wish_item_comm_list);
            itemLike =(ImageView)v.findViewById(R.id.item_wish_like);
            itemComm =(ImageView)v.findViewById(R.id.item_wish_comm);
            itemLlyout = (LinearLayout)v.findViewById(R.id.llyout);
           // itemImgGrid.setLayoutManager(gridLayout);
        }
    }


    /**
     * 点赞
     * @param holder
     * @param position
     */
    public void likeWish(WishHolder holder,int position){
        int likeListLenght = mWishList.get(position).getWish_like().size();
        //点赞列表
        holder.itemLikeList.setVisibility(View.VISIBLE);
        StringBuffer sb = new StringBuffer();
        for(int i=0; i<likeListLenght; i++){
            sb.append(mWishList.get(position).getWish_like().get(i).getNickname());
            if(i == likeListLenght -1){
                sb.append("觉得很赞");
            }else if (likeListLenght >=10){
                sb.append("...等" + (likeListLenght - i) +"人觉得很赞");
            }else{
                sb.append("、");
            }
        }
        Map<String,String> map = new HashMap<String,String>();
        for(WishsDTO.ResultBean.WishLikeBean wrw : mWishList.get(position).getWish_like()){
            map.put(wrw.getUserid(),wrw.getUserid());
        }
        //如果点赞列表中的userId包含了当前用户的id把点赞设置成蓝色,若没有则设置成灰色
        if(map.containsKey(userId)){
            holder.itemLike.setBackgroundResource(R.mipmap.like_blue);
        }else{
            holder.itemLike.setBackgroundResource(R.mipmap.like_gray);
        }
        holder.itemLikeList.setText(sb.toString());
    }

    /**
     * 评论
     * @param holder
     * @param position
     */
    public void commWish(WishHolder holder,int position){
        //评论列表
        WishCommAdapter wishCommAdapter = new WishCommAdapter(mContext);
        wishCommAdapter.setCommList(mWishList.get(position).getWish_comm());
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        holder.itemCommGrid.setLayoutManager(layoutManager);
        holder.itemCommGrid.setAdapter(wishCommAdapter);

        //关闭popupwindow
        if(popupWindow != null) popupWindow.dismiss();
        //关闭输入法
        if(mEditext != null) mMinputMethodManager.hideSoftInputFromWindow(mEditext.getWindowToken(), 0);
    }
}
