package net.wishwall.fragments;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import net.wishwall.App;
import net.wishwall.Constants;
import net.wishwall.R;
import net.wishwall.activities.AboutWishwallActivity;
import net.wishwall.activities.ActivityCollector;
import net.wishwall.activities.FeedBackActivity;
import net.wishwall.activities.MainActivity;
import net.wishwall.activities.PersonDetailActivity;
import net.wishwall.utils.SpUtil;
import net.wishwall.views.CustomDialogFragment;
import net.wishwall.views.CustomExitDialog;
import net.wishwall.views.CustomToast;

import java.io.File;


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
				Intent intent =  new Intent(getActivity(), PersonDetailActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
				break;
			//关于
			case R.id.more_about_wishwall:
				Intent about =  new Intent(getActivity(), AboutWishwallActivity.class);
				startActivity(about);
				getActivity().overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
				break;

			//版本更新
			case R.id.more_version_updates:
				FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
				android.app.Fragment prev = getActivity().getFragmentManager().findFragmentByTag("dialog");
				if (prev != null) {
					ft.remove(prev);
				}
				ft.addToBackStack(null);
				// Create and show the dialog.
				CustomDialogFragment newFragment = CustomDialogFragment.newInstance("已经是最新版本了",R.layout.version_update);
				newFragment.show(ft, "dialog");
				newFragment.setOnUpdateVersionListener(new CustomDialogFragment.OnUpdateVersionListener() {
					@Override
					public void update() {
						versionUpdate();
					}
				});
				break;
			//意见反馈
			case R.id.more_idea_feedback:
				Intent feedback =  new Intent(getActivity(), FeedBackActivity.class);
				startActivity(feedback);
				getActivity().overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
				break;
			//退出
			case R.id.more_exit_wishwall:
				exit();
				break;
		}
	}

	/**
	 * 版本更新
	 */
	private void versionUpdate() {

         final NotificationManager manager = (NotificationManager)getActivity().getSystemService(Activity.NOTIFICATION_SERVICE);
         final Notification notify =new Notification();
         notify.icon = R.drawable.logo;
         notify.tickerText = "更新";
        // notify.setDefaults(Notification.DEFAULT_SOUND)
         notify.contentView = new RemoteViews(getActivity().getPackageName(), R.layout.update_notify_layout);
         Intent intent = new Intent(getActivity(),MainActivity.class);
         PendingIntent pIntent = PendingIntent.getActivity(getActivity(),0,intent,0);
         notify.contentIntent =pIntent;
         manager.notify(0,notify);

        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            String savePath =  Environment.getExternalStorageDirectory()+"/wishwall/wishwall.apk";
            FileDownloader.getImpl().create(App.downloadUrl).setPath(savePath).setListener(new FileDownloadListener() {
                @Override
                protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                }

                @Override
                protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    float index = soFarBytes*1.0f/totalBytes*100;
                    notify.contentView.setTextViewText(R.id.notiy_title,index+"%");
                    notify.contentView.setProgressBar(R.id.notify_progressBar,soFarBytes,totalBytes,false);
                    manager.notify(0,notify);
                }

                @Override
                protected void completed(BaseDownloadTask task) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(new File(Environment
                                    .getExternalStorageDirectory()+"/wishwall/", "wishwall.apk")),
                            "application/vnd.android.package-archive");
                    startActivity(intent);
                }

                @Override
                protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                }

                @Override
                protected void error(BaseDownloadTask task, Throwable e) {

                }

                @Override
                protected void warn(BaseDownloadTask task) {

                }
            }).start();
        }else {
            CustomToast.showMsg(getActivity(),"SD卡不可用");
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
