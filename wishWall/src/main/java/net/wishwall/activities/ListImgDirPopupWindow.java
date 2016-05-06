package net.wishwall.activities;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import net.wishwall.R;
import net.wishwall.domain.FolderBean;
import net.wishwall.utils.ImageLoader;

import java.util.List;

/**
 * @author panRongFu on 2016/4/26.
 * @Description
 * @email pan@ipushan.com
 */
public class ListImgDirPopupWindow extends PopupWindow
                    implements AdapterView.OnItemClickListener{

    private int mWidth;
    private int mHeight;
    private View mContentView;
    private ListView mListView;
    private List<FolderBean> mDatas ;
    private OnDirSelectedListener mListener;
    
    public ListImgDirPopupWindow(Context context,List<FolderBean> datas) {
        calWidthHeight(context);
        this.mDatas = datas;
        mContentView = LayoutInflater.from(context).inflate(R.layout.list_imgdir_popup_window,null);
        setContentView(mContentView);
        setWidth(mWidth);
        setHeight(mHeight);
        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        ColorDrawable dw = new ColorDrawable(0xffffffff);
        setBackgroundDrawable(dw);
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_OUTSIDE ){
                    dismiss();
                    return true;
                }
                return false;
            }
        });
        initViewUI(context);
    }

    /**
     * 初始化UI
     * @param context
     */
    private void initViewUI(Context context) {
        mListView = (ListView)mContentView.findViewById(R.id.id_list_dir);
        mListView.setAdapter(new ListDirAdapter(context,mDatas));
        mListView.setOnItemClickListener(this);
    }

    /**
     * 计算宽高
     * @param context
     */
    private void calWidthHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mWidth = outMetrics.widthPixels;
        mHeight = (int) (outMetrics.heightPixels * 0.6);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(mListener != null){
            mListener.onSelected(mDatas.get(position));
        }
    }

    public interface OnDirSelectedListener{
         void onSelected(FolderBean folderBean);
    }

    public void setOnDirSelectedListener(OnDirSelectedListener listener){
        this.mListener = listener;
    }

    private class ListDirAdapter extends ArrayAdapter<FolderBean> {
        private LayoutInflater mInflater;
        public ListDirAdapter(Context context,List<FolderBean> datas) {
            super(context, 0,datas);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if(convertView == null){
                viewHolder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.item_popup_window, parent,false);
                viewHolder.mImage = (ImageView)convertView.findViewById(R.id.id_list_dir_image);
                viewHolder.mDirName = (TextView)convertView.findViewById(R.id.id_dir_item_name);
                viewHolder.mDirCount = (TextView)convertView.findViewById(R.id.id_dir_item_count);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }
            FolderBean bean = getItem(position);
            //重置
            viewHolder.mImage.setImageResource(R.mipmap.pictures_no);
            ImageLoader.getInstance().loadImage(bean.getFirstImagePath(), viewHolder.mImage);
            viewHolder.mDirCount.setText(bean.getCount()+"");
            viewHolder.mDirName.setText(bean.getName());
            return convertView;
        }

        private class ViewHolder{
            ImageView mImage;
            TextView mDirName;
            TextView mDirCount;
        }
    }
}
