package net.wishwall.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import net.wishwall.R;
import net.wishwall.activities.ZoomImageActivity;
import net.wishwall.domain.WishImageBean;
import net.wishwall.domain.WishsDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author panRongFu on 2016/4/30.
 * @Description
 * @email pan@ipushan.com
 */
public class WishItemAdapter extends RecyclerView.Adapter<WishItemAdapter.ItemViewHolder>{

    private List<WishsDTO.ResultBean.WishImgBean> wishItemGridList;
    private LayoutInflater mInflater;
    private Context mContext;
    GridLayoutManager gridLayout;


    public void setWishItemGridList(List<WishsDTO.ResultBean.WishImgBean> wishItemGridList) {
        this.wishItemGridList = wishItemGridList;
    }

    public WishItemAdapter(Context context, List<WishsDTO.ResultBean.WishImgBean> wishItemGridList){
        mContext = context;
        this.wishItemGridList = wishItemGridList;
        mInflater = LayoutInflater.from(context);
      //  gridLayout = new GridLayoutManager(context, 4);
    }
    @Override
    public WishItemAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.issue_wish_pic_item,parent,false);

        return new WishItemAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WishItemAdapter.ItemViewHolder holder, int position) {

        Picasso.with(mContext).load(wishItemGridList.get(position).getImageid()).into(holder.itemImage);
        holder.itemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<WishImageBean> wrw = new ArrayList<WishImageBean>();
                for(int i=0;i<wishItemGridList.size();i++){
                    WishImageBean wib = new WishImageBean();
                    wib.setImageid(wishItemGridList.get(i).getImageid());
                    wrw.add(wib);
                }
                Intent intent = new Intent(mContext, ZoomImageActivity.class);
                intent.putExtra("list",wrw);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return wishItemGridList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{
        private View mView;
        private ImageView itemImage;
        public ItemViewHolder(View view) {
            super(view);
            mView = view;
            itemImage = (ImageView) view.findViewById(R.id.wish_pic);
        }
    }
}
