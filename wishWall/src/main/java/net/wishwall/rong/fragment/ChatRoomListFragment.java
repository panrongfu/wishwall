package net.wishwall.rong.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import net.wishwall.R;
import net.wishwall.domain.AllChatroomsDTO;
import net.wishwall.service.ApiClient;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imkit.widget.AsyncImageView;
import io.rong.imlib.model.Conversation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author panRongFu on 2016/4/1.
 * @Description
 * @email pan@ipushan.com
 */
public class ChatRoomListFragment extends Fragment implements AdapterView.OnItemClickListener {

    /**
     * 聊天室的 ListView
     */
    private ListView mListView;

    /**
     * 数据适配器
     */
    private MyAdapter mAdapter;

    /**
     * 填充数据的集合
     */
    private List<AllChatroomsDTO.ResultBean> mList;

    public MyHolder mViewHolder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.de_fr_chatroom_list, container, false);
        mListView = (ListView) view.findViewById(R.id.lv_chatroomlist);
        initData();
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (RongIM.getInstance() != null) {
            String roomId = mList.get(position).getId();
            String roomName = mList.get(position).getRoom_name();
            RongIM.getInstance().startConversation(getActivity(), Conversation.ConversationType.CHATROOM, roomId, roomName);
        }
    }

    /**
     * 填充数据
     */
    private void initData() {

        mList = new ArrayList<AllChatroomsDTO.ResultBean>();
        ApiClient.findAllChatrooms(new Callback<AllChatroomsDTO>() {
            @Override
            public void onResponse(Call<AllChatroomsDTO> call, Response<AllChatroomsDTO> response) {
                AllChatroomsDTO body = response.body();
                if(body.getCode() == 200){
                    mList = body.getResult();
                    mAdapter = new MyAdapter();
                    mListView.setAdapter(mAdapter);
                    mListView.setOnItemClickListener(ChatRoomListFragment.this);
                }
            }
            @Override
            public void onFailure(Call<AllChatroomsDTO> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            mViewHolder = new MyHolder();
            if (convertView != null) {
                mViewHolder = (MyHolder) convertView.getTag();
            } else {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.de_item_chatroom, parent, false);
                mViewHolder.mIV_Icon = (AsyncImageView) convertView.findViewById(R.id.iv_chatroom_icon);
                mViewHolder.mTV_Title = (TextView) convertView.findViewById(R.id.tv_chatroom_title);
                mViewHolder.mTV_DescribeA = (TextView) convertView.findViewById(R.id.tv_chatroom_d_a);
                mViewHolder.mTV_DescribeB = (TextView) convertView.findViewById(R.id.tv_chatroom_d_b);
                convertView.setTag(mViewHolder);
            }
            if (mViewHolder != null) {
                mViewHolder.mIV_Icon.setImageResource(R.mipmap.icon1);
                mViewHolder.mTV_Title.setText(mList.get(position).getRoom_name());
                mViewHolder.mTV_DescribeA.setText(mList.get(position).getTitle().toString());
                mViewHolder.mTV_DescribeB.setText(mList.get(position).getDescribe().toString());
            }
            return convertView;
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

    }
    /**
     * 减少子控件查询次数的 viewhodler
     */
    public class MyHolder {
        public AsyncImageView mIV_Icon;
        public TextView mTV_Title;
        public TextView mTV_DescribeA;
        public TextView mTV_DescribeB;
    }
}