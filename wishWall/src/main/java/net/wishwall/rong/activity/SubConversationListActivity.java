package net.wishwall.rong.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import net.wishwall.R;
import net.wishwall.rong.adapter.SubConversationListAdapterEx;

import io.rong.imkit.RongContext;
import io.rong.imkit.fragment.SubConversationListFragment;

/**
 * Created by Bob on 15/11/3.
 * 聚合会话列表
 */
public class SubConversationListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.rong_activity);
        SubConversationListFragment fragment = new SubConversationListFragment();
        fragment.setAdapter(new SubConversationListAdapterEx(RongContext.getInstance()));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.rong_content, fragment);
        transaction.commit();

        Intent intent = getIntent();
        //聚合会话参数
        String type = intent.getData().getQueryParameter("type");

        if(type == null )
            return;
        
        Toolbar toolbar = (Toolbar) findViewById(R.id.conver_toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();

        if (type.equals("group")) {
            ab.setTitle(R.string.de_actionbar_sub_group);
        } else if (type.equals("private")) {
            ab.setTitle(R.string.de_actionbar_sub_private);
        } else if (type.equals("discussion")) {
            ab.setTitle(R.string.de_actionbar_sub_discussion);
        } else if (type.equals("system")) {
            ab.setTitle(R.string.de_actionbar_sub_system);
        } else {
            ab.setTitle(R.string.de_actionbar_sub_defult);
        }
    }

}
