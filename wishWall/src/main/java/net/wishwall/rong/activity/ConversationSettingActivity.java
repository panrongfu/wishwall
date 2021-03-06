package net.wishwall.rong.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import net.wishwall.R;


/**
 * Created by Bob on 2015/3/27.
 * 设置页面采用 Activity 嵌套 SettingFragment，在 SettingFragment 中嵌套 置顶，新消息通知，清空聊天信息 这三个 fragment
 * 目的：
 *      1，方便大家集成，会话的设置界面可以全部复制到自己的 app 中
 *      2，展示 Fragment 如何集成设置页面， SettingFragment 必须继承 DispatchResultFragment 才可以。
 *      3，在 Activity 中只需要继承 FragmentActivity 即可。
 *
 */
public class ConversationSettingActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.de_ac_setting);

        Toolbar toolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setTitle("");
        TextView title = (TextView)findViewById(R.id.conver_setting_title);
        title.setText("聊天设置");
    }
}
