package net.wishwall.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import net.wishwall.R;
import net.wishwall.utils.CustomUtils;

/**
 * @author panRongFu on 2016/5/6.
 * @Description
 * @email pan@ipushan.com
 */
public class AboutWishwallActivity extends BaseActivity implements View.OnClickListener{

    TextView back;
    TextView version;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_wishwall);
        initViewUI();
    }

    private void initViewUI() {
        back = (TextView)findViewById(R.id.about_back);
        version = (TextView)findViewById(R.id.about_version);
        version.setText("许愿墙"+CustomUtils.getVersionName(this));
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.about_back:
                finish();
                break;
        }
    }
}
