package net.wishwall.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import net.wishwall.R;
import net.wishwall.utils.ImageLoader;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author panRongFu on 2016/4/27.
 * @Description
 * @email pan@ipushan.com
 */
public class IssueWishAdpter extends  RecyclerView.Adapter<IssueWishAdpter.IViewHolder>{

    public static Set<String> isUpload = new HashSet<String>();
    private String mDirPath;
    private List<String> mImagePath;
    private LayoutInflater mInflater;
    private static IssueOnItemClickListener mListener;
    public IssueWishAdpter(Context context, List<String> list){
        this.mImagePath = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public IViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.issue_wish_pic_item, parent, false);
        return new IViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final IViewHolder holder, final int position) {
        if(position == mImagePath.size()){
            holder.image.setBackgroundResource(R.mipmap.addpic);
        }else{
            ImageLoader.getInstance().loadImage(mImagePath.get(position),holder.image);
            if(isUpload.contains(mImagePath.get(position))){
                holder.image.setColorFilter(null);
            }else{
                holder.image.setColorFilter(Color.parseColor("#77000000"));
            }
        }
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              mListener.issueOnClickListener(v,position);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mImagePath.size()+1;
    }

   public static class IViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        View mView;
        public IViewHolder(View view) {
            super(view);
            mView = view;
            image = (ImageView) view.findViewById(R.id.wish_pic);
        }
    }

    public void setIssueOnItemClickListener(IssueOnItemClickListener listener){
        this.mListener = listener;

    }
    public interface IssueOnItemClickListener {
        void issueOnClickListener(View view,int position);
    }
}
