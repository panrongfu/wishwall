package net.wishwall.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.wishwall.R;
import net.wishwall.domain.WishImageBean;
import net.wishwall.views.ZoomImageView;

import java.util.ArrayList;

/**
 * @author panRongFu on 2016/5/16.
 * @Description
 * @email pan@ipushan.com
 */
public class ZoomImageActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private ZoomImageView[] mImageViews;
    ArrayList<WishImageBean> imgList;
    private TextView pageIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zoom_image);
        initViewUI();
    }

    private void initViewUI() {
        imgList = ( ArrayList<WishImageBean>)getIntent().getSerializableExtra("list");
        mImageViews = new ZoomImageView[imgList.size()];
        mViewPager = (ViewPager) findViewById(R.id.zoom_image_viewpager);
        pageIndex = (TextView)findViewById(R.id.page_index);
        mViewPager.setAdapter(new ViewPagerAdapter());
    }

    public class ViewPagerAdapter extends PagerAdapter{
        @Override
        public int getCount() {
            return imgList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ZoomImageView imageView = new ZoomImageView(getApplicationContext());
            Picasso.with(getApplicationContext()).load(imgList.get(position).getImageid()).into(imageView);
            container.addView(imageView);
            mImageViews[position] = imageView;
            pageIndex.setText(position+1+"/"+imgList.size());
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mImageViews[position]);
        }
    }
}
