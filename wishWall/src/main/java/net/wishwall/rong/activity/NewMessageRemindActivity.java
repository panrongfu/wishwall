package net.wishwall.rong.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

import net.wishwall.R;


/**
 * Created by Administrator on 2015/3/2.
 */
public class NewMessageRemindActivity extends BaseActionBarActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.de_ac_new_message_remind);

      //  getSupportActionBar().setTitle(R.string.new_message_notice);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.de_actionbar_back);

        Toolbar toolbar = (Toolbar) findViewById(R.id.conver_toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setTitle(getResources().getString(R.string.app_name));
        ab.setHomeAsUpIndicator(R.mipmap.de_actionbar_back);
        ab.setDisplayHomeAsUpEnabled(true);

        RelativeLayout mNotice = (RelativeLayout) findViewById(R.id.re_notice);

        mNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NewMessageRemindActivity.this, DisturbActivity.class));
            }
        });
    }
}
