package net.wishwall.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import net.wishwall.R;
import net.wishwall.utils.ImageLoader;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author panRongFu on 2016/4/26.
 * @Description
 * @email pan@ipushan.com
 */
public class OptionImageAdapter extends RecyclerView.Adapter<OptionImageAdapter.ViewHolder> {

    private static Set<String> mSelectImg = new HashSet<String>();
    private String mDirPath;
    private List<String> mImagePath;
    private LayoutInflater mInflater;
    private OnItemClickListener mListener;
    public OptionImageAdapter(Context context, List<String> list, String path){
        this.mDirPath = path;
        this.mImagePath = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gridview, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.image.setColorFilter(null);
        holder.image.setImageResource(R.mipmap.pictures_no);
        holder.mSelect.setImageResource(R.mipmap.picture_unselected);
        ImageLoader.getInstance(100, ImageLoader.Type.LIFO)
                .loadImage(mDirPath+"/"+mImagePath.get(position), holder.image);
        final String filePath = mDirPath+"/"+mImagePath.get(position).toString();
        holder.mView.setTag(mDirPath+"/"+mImagePath.get(position));
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //已经被选中
                if(mSelectImg.contains(filePath)){
                    mSelectImg.remove(filePath);
                    holder.image.setColorFilter(null);
                    holder.mSelect.setImageResource(R.mipmap.picture_unselected);
                }else{
                    mSelectImg.add(filePath);
                    holder.image.setColorFilter(Color.parseColor("#88000000"));
                    holder.mSelect.setImageResource(R.mipmap.pictures_selected);
                }
                mListener.onClickItemListener(holder.mView,position);
            }
        });
        if(mSelectImg.contains(filePath)){
            holder.image.setColorFilter(Color.parseColor("#77000000"));
            holder.mSelect.setImageResource(R.mipmap.pictures_selected);
        }
    }

    @Override
    public int getItemCount() {
        return mImagePath.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        ImageButton mSelect;
        View mView;
        public ViewHolder(View view) {
            super(view);
            mView = view;
            image = (ImageView) view.findViewById(R.id.id_item_image);
            mSelect = (ImageButton) view.findViewById(R.id.id_item_select);
        }
    }

    public void  setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }
    public interface OnItemClickListener{
         void onClickItemListener(View view, int position);
    }
}
