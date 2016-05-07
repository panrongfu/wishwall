package net.wishwall.rong.fragment.setting.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sea_monster.resource.Resource;

import net.wishwall.R;
import net.wishwall.rong.adapter.BaseAdapter;

import io.rong.imkit.widget.AsyncImageView;
import io.rong.imlib.model.UserInfo;

/**
 * Created by Bob on 15/8/3.
 */
public class RongConversationAddMemberAdapter extends BaseAdapter<UserInfo> {

    LayoutInflater mInflater;
    Boolean isDeleteState = false;
    String mCreatorId = null;
    private OnDeleteIconListener mDeleteIconListener;

    class ViewHolder {
        AsyncImageView mMemberIcon;
        TextView mMemberName;
        ImageView mDeleteIcon;
        ImageView mMemberDeIcon;
    }

    public RongConversationAddMemberAdapter(Context context) {
        super();
        Log.e("tag","------------RongConversationAddMemberAdapter------------");
        mInflater = LayoutInflater.from(context);
        isDeleteState = false;
    }

    @Override
    protected View newView(Context context, int position, ViewGroup group) {
        View view = mInflater.inflate(R.layout.de_item_conversation_member, null);
        ViewHolder holder = new ViewHolder();
        holder.mMemberIcon = findViewById(view, R.id.icon);
        holder.mMemberName = findViewById(view, R.id.text1);
        holder.mDeleteIcon = findViewById(view, R.id.icon1);
        holder.mMemberDeIcon = findViewById(view, R.id.icon2);
        view.setTag(holder);
        return view;
    }

    @Override
    protected void bindView(View v, final int position, UserInfo data) {
        ViewHolder holder = (ViewHolder) v.getTag();

        if (data.getUserId().equals("RongAddBtn") || data.getUserId().equals("RongDelBtn")) {
            holder.mMemberIcon.setVisibility(View.INVISIBLE);
            holder.mMemberDeIcon.setVisibility(View.VISIBLE);
            if (data.getUserId().equals("RongAddBtn"))
                holder.mMemberDeIcon.setImageResource(R.mipmap.de_ic_setting_friends_add);
            else
                holder.mMemberDeIcon.setImageResource(R.mipmap.de_ic_setting_friends_delete);
            holder.mMemberName.setVisibility(View.INVISIBLE);
            holder.mDeleteIcon.setVisibility(View.GONE);

        } else {
            holder.mMemberIcon.setVisibility(View.VISIBLE);
            holder.mMemberDeIcon.setVisibility(View.GONE);
          //  holder.mMemberIcon.setDefaultDrawable(v.getContext().getResources().getDrawable(R.mipmap.de_default_portrait));
            holder.mMemberIcon.setBackgroundResource(R.mipmap.de_default_portrait);
            if (data.getPortraitUri() != null)
                holder.mMemberIcon.setResource(new Resource(data.getPortraitUri()));

            if (data.getName() != null){
                holder.mMemberName.setText(data.getName());
            }else{
                holder.mMemberName.setText("未定义");
            }

            if (isDeleteState() && (!data.getUserId().equals(getCreatorId()))) {
                holder.mDeleteIcon.setVisibility(View.VISIBLE);
                holder.mDeleteIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mDeleteIconListener != null) {
                            mDeleteIconListener.onDeleteIconClick(v, position);
                        }
                    }
                });
            } else
                holder.mDeleteIcon.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public long getItemId(int position) {
        UserInfo info = getItem(position);
        if (info == null)
            return 0;
        return info.hashCode();
    }

    public void setDeleteState(boolean state) {
        isDeleteState = state;
    }

    public boolean isDeleteState() {
        return isDeleteState;
    }

    public void setCreatorId(String id){
        mCreatorId = id;
    }

    public String getCreatorId(){
        return mCreatorId;
    }

    public void setDeleteIconListener(OnDeleteIconListener listener) {
        mDeleteIconListener = listener;
    }

    public interface OnDeleteIconListener {
         void onDeleteIconClick(View view, int position);
    }
}
