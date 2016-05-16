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
public class WishCommAdapter extends RecyclerView.Adapter<WishCommAdapter.CommViewHolder> {

    private static List<WishsDTO.ResultBean.WishCommBean> commList;
    private Context mContext;

    public WishCommAdapter(Context mContext) {

        this.mContext = mContext;
    }

    public List<WishsDTO.ResultBean.WishCommBean> getCommList() {
        return commList;
    }

    public void setCommList(List<WishsDTO.ResultBean.WishCommBean> commList) {

        this.commList = commList;
    }

    @Override
    public int getItemCount() {
        return commList.size();
    }


    @Override
    public CommViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.wish_comm_item,parent,false);
        return new CommViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommViewHolder holder, int position) {
        holder.commName.setText(commList.get(position).getNickname()+":");
        holder.commText.setText(commList.get(position).getContent());
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public static class CommViewHolder extends RecyclerView.ViewHolder{
        TextView commName;
        TextView commText;
        public CommViewHolder(View itemView) {
            super(itemView);
            commName = (TextView) itemView.findViewById(R.id.wish_comm_item_name);
            commText = (TextView) itemView.findViewById(R.id.wish_comm_item_text);
        }
    }
}
