package net.wishwall.activities;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import net.wishwall.R;
import net.wishwall.domain.MorePopupItemBean;
import net.wishwall.utils.DensityUtil;
import net.wishwall.utils.ScreenWidthHeight;
import net.wishwall.views.CustomToast;

import java.util.ArrayList;
import java.util.List;

/**
 * @author panRongFu on 2016/5/12.
 * @Description
 * @email pan@ipushan.com
 */
public class MorePopupWindow extends PopupWindow {

    //列表弹窗的间隔
    protected final int LIST_PADDING = 10;
    private Context mContext;
    //实例化一个矩形
    private Rect mRect = new Rect();
    //坐标的位置（x、y）
    private final int[] mLocation = new int[2];
    private List<MorePopupItemBean> itemList  = new ArrayList<MorePopupItemBean>();
    private OnMorePopupItemClickListener mMorePopupItemListener;
    View contentView;

    ListView mListView;
    public List<MorePopupItemBean> getItemList() {
        return itemList;
    }

    public void setItemList(List<MorePopupItemBean> itemList) {
        this.itemList = itemList;
    }
    public MorePopupWindow(Context context){
        this(context, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.mContext = context;
    }

    public MorePopupWindow(Context context, int width, int height) {

       contentView = LayoutInflater.from(context).inflate(R. layout.more_popupwindow,null);
        setContentView(contentView);
        //设置可以点击
        setTouchable(true);
        //设置可以获得焦点
        setFocusable(true);
        //设置弹窗外可以点击
        setOutsideTouchable(true);
        //设置宽度
        setWidth(width);
        //设置高度
        setHeight(height);
        //设置背景
        setBackgroundDrawable(new BitmapDrawable());
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //如果点击了弹框外面弹框消失
                if(event.getAction() == MotionEvent.ACTION_OUTSIDE){
                    dismiss();
                    return true;
                }
                return false;
            }
        });
        initViewUI(contentView);
    }

    /**
     * 初始化
     * @param contentView
     */
    private void initViewUI(View contentView) {
        mListView = (ListView)contentView.findViewById(R.id.more_popup_list);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mMorePopupItemListener != null){
                    mMorePopupItemListener.morePopupItemClick(position);
                }
            }
        });
    }

    /**
     * popupwindow显示的位置
     * @param view
     */
    public void showPopupWindow(View view){
        mListView.setAdapter(new MorePopupWindowAdapter());
        view.getLocationOnScreen(mLocation);
        //测量布局的大小
        contentView.measure(0, 0);
        //将布局大小设置为PopupWindow的宽高
        PopupWindow popWindow = new PopupWindow(contentView, contentView.getMeasuredWidth(), contentView.getMeasuredHeight(), true);
        int height = popWindow.getHeight();
        int width = popWindow.getWidth();
        int srceenWidth = ScreenWidthHeight.getWidth(mContext);
        int srceenHeight = ScreenWidthHeight.getHeight(mContext);

        //显示弹窗的位置
        showAsDropDown(view, -(srceenWidth-view.getRight()+width/2), 0);
        CustomToast.showMsg(mContext,"+view.getRight()"+view.getRight());
    }

    public void setOnMorePopupItemClickListener(OnMorePopupItemClickListener l){
        this.mMorePopupItemListener = l;
    }

    public interface OnMorePopupItemClickListener{
        void morePopupItemClick(int position);
    }

    class MorePopupWindowAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return itemList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = null;
            if(convertView == null){
                //创建textview对象
                textView = new TextView(mContext);
                //设置背景颜色
                textView.setTextColor(mContext.getResources().getColor(R.color.black));
                //设置字体大小
                textView.setTextSize(17);
                //设置文本居中
                textView.setGravity(Gravity.CENTER);
                //设置文本域的范围
                textView.setPadding(5,10,0,10);
                //设置文本在一行内显示（不换行）
                textView.setSingleLine(true);

            }else{
                textView = (TextView)convertView;
            }
            MorePopupItemBean item = itemList.get(position);
            //设置文字
            textView.setText(item.getItemTitle());
            //设置文字和图标的间隔
            textView.setCompoundDrawablePadding(10);
            //设置图标放在文字的左边
            textView.setCompoundDrawablesWithIntrinsicBounds(item.getDrawable(),null,null,null);
            return textView;
        }
    }
}
