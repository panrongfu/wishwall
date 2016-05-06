package net.wishwall.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.wishwall.Constants;
import net.wishwall.R;
import net.wishwall.activities.ActivityCollector;
import net.wishwall.activities.MainActivity;
import net.wishwall.activities.PersonDetail;
import net.wishwall.utils.SpUtil;
import net.wishwall.views.CustomExitDialog;


/**
 * 
 *@Description:更多界面
 *@author panRongFu pan@ipushan.com
 *@date 2015年9月17日 下午12:07:52
 */
public class MoreFragment extends Fragment implements  OnClickListener{
	
	private RelativeLayout aboutwishwall;
	private RelativeLayout newHandHelp;
	private RelativeLayout versionUpdates;
	private RelativeLayout ideaFeedback;
	private RelativeLayout personDetail;
	private TextView exitwishwall;
	private CustomExitDialog exitDialog;
	private SpUtil userSpUtil;
	private MainActivity mainActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)context;
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.more_fragment, container,false);
		initViewUI(view);
		return view;
	}
	/**
	 * 初始化控件、为控件设置监听事件
	 * @param view
	 */
	private void initViewUI(View view) {
        userSpUtil = new SpUtil(getActivity(), Constants.USER_SPUTIL);
		personDetail = (RelativeLayout) view.findViewById(R.id.more_person_info);
		aboutwishwall = (RelativeLayout) view.findViewById(R.id.more_about_wishwall);
		versionUpdates = (RelativeLayout) view.findViewById(R.id.more_version_updates);
		ideaFeedback = (RelativeLayout)view.findViewById(R.id.more_idea_feedback);
		exitwishwall = (TextView)view.findViewById(R.id.more_exit_wishwall);
		
		personDetail.setOnClickListener(this);
		aboutwishwall.setOnClickListener(this);
		versionUpdates.setOnClickListener(this);
		ideaFeedback.setOnClickListener(this);
		exitwishwall.setOnClickListener(this);						
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.more_person_info:
				Intent intent =  new Intent(getActivity(), PersonDetail.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
				break;
			//关于
			case R.id.more_about_wishwall:

				break;

			//版本更新
			case R.id.more_version_updates:

				break;
			//意见反馈
			case R.id.more_idea_feedback:

				break;
			//退出
			case R.id.more_exit_wishwall:
				exit();
				break;
		}
	}
	
	public void exit() {

		exitDialog = new CustomExitDialog(getActivity(),"你确定要退出当前账号？", R.style.mystyle);
		exitDialog.setOnDialogBtnClick(new CustomExitDialog.OnDialogBtnClick() {				
			@Override
			public void loginOut() {
				ActivityCollector.finishAllActivities();
                userSpUtil.putBooleanCode("login",false);
                exitDialog.dismiss();
                mainActivity.finish();
            }
			@Override
			public void cancel() {
				exitDialog.dismiss();						
			}
		});
		exitDialog.show();				
	}

}
