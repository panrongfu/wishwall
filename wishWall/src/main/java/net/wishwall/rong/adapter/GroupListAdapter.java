package net.wishwall.rong.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.wishwall.R;
import net.wishwall.domain.GroupsDTO.ResultBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.rong.imkit.widget.AsyncImageView;
import io.rong.imlib.model.Group;

/**
 * @author panRongFu on 2016/4/1.
 * @Description
 * @email pan@ipushan.com
 */
public class GroupListAdapter extends android.widget.BaseAdapter {

    private static final String TAG = GroupListAdapter.class.getSimpleName();
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<ResultBean> mResults;
    private ArrayList<View> mViewList;
    HashMap<String, Group> groupMap;
    private Type mType = Type.MY;
    public enum Type{
        MY,ALL
    }

    public void setType(Type mType) {
        this.mType = mType;
    }

    public GroupListAdapter(Context context, List<ResultBean> result, HashMap<String, Group> group) {

        this.mResults = result;
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
        this.groupMap = group;
        mViewList = new ArrayList<View>();
    }

    @Override
    public int getCount() {
        return mResults.size();
    }

    @Override
    public ResultBean getItem(int position) {
        return mResults.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null || convertView.getTag() == null) {
            convertView = mLayoutInflater.inflate(R.layout.de_item_group, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mGroupName = (TextView) convertView.findViewById(R.id.group_adaper_name);
            viewHolder.mGroupCurrentNum = (TextView) convertView.findViewById(R.id.group_current_num);
            viewHolder.mGroupCurrentSum = (TextView) convertView.findViewById(R.id.group_current_sum);
            viewHolder.mGroupLastmessge = (TextView) convertView.findViewById(R.id.group_last_mess);
            viewHolder.mImageView = (AsyncImageView) convertView.findViewById(R.id.group_adapter_img);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (viewHolder != null) {
            viewHolder.mGroupName.setText(mResults.get(position).getName());
            viewHolder.mGroupCurrentNum.setText(mResults.get(position).getNumber() + "/");
            viewHolder.mGroupCurrentSum.setText(mResults.get(position).getMax_number()+"");
            viewHolder.mGroupLastmessge.setText(mResults.get(position).getIntroduce());
            String path = mResults.get(position).getIcon();
            if(!TextUtils.isEmpty(path)){
               // viewHolder.mImageView.
                Picasso.with(mContext).load(path).into(viewHolder.mImageView);
            }
        }
        return convertView;
    }

    static class ViewHolder {
        TextView mGroupName;
        TextView mGroupCurrentNum;
        TextView mGroupCurrentSum;
        TextView mGroupLastmessge;
        AsyncImageView mImageView;
    }
}