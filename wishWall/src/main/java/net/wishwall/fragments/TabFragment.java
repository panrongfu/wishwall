package net.wishwall.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import net.wishwall.R;

/**
 * @author panRongFu on 2015/12/15.
 * @Description
 * @email pan@ipushan.com
 */
public class TabFragment extends Fragment{

    private  WebView webView;
    private  String linkUrl;
    private  String newUrl;
    private  String session;
    private String name;
    private ProgressDialog pDialog = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.child_fargment,null);
        Bundle bundle = getArguments();
        name = bundle.getString("name");
        initView(view);
        initUtil();
        return view;
    }

    private void initUtil() {
        pDialog = ProgressDialog.show(getActivity(),null,"页面加载中，请稍后..");
        pDialog.dismiss();

    }
    private void initView(View view){
        TextView textView = (TextView)view.findViewById(R.id.textView);
        textView.setText(name);
    }
}
