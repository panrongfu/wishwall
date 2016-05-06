package net.wishwall.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.wishwall.R;
import net.wishwall.domain.WishsDTO;
import net.wishwall.utils.DateTimeUtil;
import net.wishwall.views.RecyclerViewItemDecoration;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author panRongFu on 2016/4/30.
 * @Description
 * @email pan@ipushan.com
 */
public class WishAdapter extends RecyclerView.Adapter<WishAdapter.WishHolder>{

    private List<WishsDTO.ResultBean> wishList;
    private LayoutInflater mInflater;
    private Context mContext;
    //private static GridLayoutManager gridLayout;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

    public void setWishList(List<WishsDTO.ResultBean> wishList) {
        this.wishList = wishList;
    }

    public List<WishsDTO.ResultBean> getWishList() {
        return wishList;
    }

    public WishAdapter(Context context){
        mContext = context;
      //  gridLayout =  new GridLayoutManager(mContext, 4);
      //  gridLayout.setOrientation(OrientationHelper.VERTICAL);
        mInflater = LayoutInflater.from(context);

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
            return new WishHolder(view);
        }
            View footer = mInflater.inflate(R.layout.footerview,parent,false);
            return new FooterViewHolder(footer);
    }

    @Override
    public void onBindViewHolder(WishHolder holder, int position) {
        if(position+1 == getItemCount()&& getItemCount()>=10) return;
        //设置头item头像
        String path = wishList.get(position).getIcon();
        if(!TextUtils.isEmpty(path)){
            Picasso.with(mContext).load(path).into(holder.itemImg);
        }
        //设置item的用户名
        holder.itemNam.setText(wishList.get(position).getUsername());
        //设置item时间
        holder.itemTime.setText(DateTimeUtil.yyyyMMddHHmm(wishList.get(position).getTime()));
        //设置item内容
        holder.itemContent.setText(wishList.get(position).getContent());

        GridLayoutManager gridLayout = new GridLayoutManager(mContext, 4);
        gridLayout.setOrientation(OrientationHelper.VERTICAL);
        holder.itemImgGrid.setLayoutManager(gridLayout);
        holder.itemImgGrid.addItemDecoration(new RecyclerViewItemDecoration(
                RecyclerViewItemDecoration.MODE_GRID,
                Color.WHITE,10,5,0));
        holder.itemImgGrid.setAdapter(new WishItemAdapter(mContext,wishList.get(position).getWish_img()));
    }

    @Override
    public int getItemCount() {
        return wishList.size();
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

        public WishHolder(View v) {
            super(v);
            itemImg = (CircleImageView)v.findViewById(R.id.my_wish_item_img);
            itemNam = (TextView)v.findViewById(R.id.my_wish_item_name);
            itemTime = (TextView)v.findViewById(R.id.my_wish_item_time);
            itemContent = (TextView)v.findViewById(R.id.my_wish_item_content);
            itemImgGrid = (RecyclerView)v.findViewById(R.id.my_wish_img_grid);
           // itemImgGrid.setLayoutManager(gridLayout);
        }
    }
}
