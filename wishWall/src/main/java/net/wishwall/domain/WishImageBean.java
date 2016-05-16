package net.wishwall.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author panRongFu on 2016/5/16.
 * @Description
 * @email pan@ipushan.com
 */
public class WishImageBean implements Parcelable {

    private String imageid;

    public String getImageid() {
        return imageid;
    }

    public void setImageid(String imageid) {
        this.imageid = imageid;
    }

    public WishImageBean(){

    }
    public WishImageBean(Parcel in) {
       imageid = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imageid);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        @Override
        public Object createFromParcel(Parcel source) {
            return new WishImageBean(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new WishImageBean[size];
        }
    };
}
