package net.wishwall.domain;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * @author panRongFu on 2016/5/12.
 * @Description
 * @email pan@ipushan.com
 */
public class MorePopupItemBean {

    private int res;
    private Context mContext;
    private Drawable drawable;
    private String itemTitle;

    public MorePopupItemBean(Context context) {
        this.mContext = context;
//        this.drawable = context.getResources().getDrawable(res);
//        this.itemTitle = itemTitle;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(int res) {
        this.drawable = mContext.getResources().getDrawable(res);
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }
}
