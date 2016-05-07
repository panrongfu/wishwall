package net.wishwall.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.wishwall.R;
import net.wishwall.domain.WishsDTO;

import java.util.List;

/**
 * Created by Administrator on 2016/5/7.
 */
public class WishLikeAdapter extends RecyclerView.Adapter<WishLikeAdapter.LikeViewHolder> {

    private List<WishsDTO.ResultBean.WishLikeBean> likeList;
    private Context mContext;

    public List<?> getLikeList() {
        return likeList;
    }

    public WishLikeAdapter(Context mContext, List<WishsDTO.ResultBean.WishLikeBean> likeList) {
        this.likeList = likeList;
        this.mContext = mContext;
    }

    public void setLikeList(List<WishsDTO.ResultBean.WishLikeBean> likeList) {
        this.likeList = likeList;
    }

    @Override
    public int getItemCount() {
        return likeList.size();
    }

    @Override
    public void onBindViewHolder(LikeViewHolder holder, int position) {
        holder.mTextView.setText(likeList.get(position).getUsername());

    }

    @Override
    public LikeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.wish_like_item,parent,false);
        return new LikeViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public static class LikeViewHolder extends RecyclerView.ViewHolder{

        TextView mTextView;
        public LikeViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.wish_item_like_name);
        }
    }
}
