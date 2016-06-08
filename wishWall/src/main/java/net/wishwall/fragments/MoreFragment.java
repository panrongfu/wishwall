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
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import net.wishwall.Constants;
import net.wishwall.R;
import net.wishwall.activities.AboutWishwallActivity;
import net.wishwall.activities.ActivityCollector;
import net.wishwall.activities.FeedBackActivity;
import net.wishwall.activities.MainActivity;
import net.wishwall.activities.PersonDetailActivity;
import net.wishwall.domain.VersionDTO;
import net.wishwall.service.ApiClient;
import net.wishwall.utils.CustomUtils;
import net.wishwall.utils.SpUtil;
import net.wishwall.views.CustomDialogFragment;
import net.wishwall.views.CustomExitDialog;
import net.wishwall.views.CustomToast;

import java.io.File;

import io.rong.imkit.RongIM;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


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

    private ImageView newVersion;
    private static final int NEWSET = 0X01;
    private static final int NO_NEWSET = 0X02;
    private FragmentTransaction ft;
    private android.app.Fragment prev;
    /**
     * 是否点击检查更新
     */
    private boolean checkUpdate= false;
    private Handler updateHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == NEWSET){//已经是最新版本了，隐藏new图标
                newVersion.setVisibility(View.INVISIBLE);
                if(checkUpdate){
                    showDialogFragment("已经是最新版本了",ft, CustomDialogFragment.Type.OKAY);
                }

            }else if(msg.what == NO_NEWSET){//不是最新版本，显示new图标
                newVersion.setVisibility(View.VISIBLE);
                if(checkUpdate){
                    String updateInfo = String.valueOf(msg.obj);
                    showDialogFragment(updateInfo,ft, CustomDialogFragment.Type.OKAY_CANCLE);
                }
            }
        }
    };


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
        newVersion = (ImageView) view.findViewById(R.id.id_new_version);
		personDetail = (RelativeLayout) view.findViewById(R.id.more_person_info);
		aboutwishwall = (RelativeLayout) view.findViewById(R.id.more_about_wishwall);
		versionUpdates = (RelativeLayout) view.findViewById(R.id.more_version_updates);
		ideaFeedback = (RelativeLayout)view.findViewById(R.id.more_idea_feedback);
		exitwishwall = (TextView)view.findViewById(R.id.more_exit_wishwall);
        ft = getActivity().getFragmentManager().beginTransaction();
        prev = getActivity().getFragmentManager().findFragmentByTag("dialog");
		
		personDetail.setOnClickListener(this);
		aboutwishwall.setOnClickListener(this);
		versionUpdates.setOnClickListener(this);
		ideaFeedback.setOnClickListener(this);
		exitwishwall.setOnClickListener(this);
        checkForUpdate();
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
                checkUpdate=true;
				if (prev != null) {
					ft.remove(prev);
				}
				ft.addToBackStack(null);
                checkForUpdate();

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
     * 本地的版本号与服务器上的版本号对比若不一样就更新
     */
    private void checkForUpdate() {
        final int versionCode = CustomUtils.getVersionCode(getActivity());
        ApiClient.checkForUpdate(new Callback<VersionDTO>() {
            @Override
            public void onResponse(Call<VersionDTO> call, Response<VersionDTO> response) {
                VersionDTO body = response.body();
                if(body.getCode() == 200){
                    int newVersionCode = body.getResult().getVersionCode();
                    if(versionCode == newVersionCode){
                        Message msg = Message.obtain();
                        msg.what=NEWSET;
                        updateHandler.sendMessage(msg);
                    }else if(versionCode != newVersionCode){
                        Message msg = Message.obtain();
                        msg.what = NO_NEWSET;
                        msg.obj = body.getResult().getUpdateInfo();
                        updateHandler.sendMessage(msg);
                    }
                }
            }

            @Override
            public void onFailure(Call<VersionDTO> call, Throwable t) {

            }
        });
    }

    /**
     * 显示对话框
     * @param ft
     */
    public void showDialogFragment(String msg,FragmentTransaction ft,CustomDialogFragment.Type mType){
        // Create and show the dialog.
        CustomDialogFragment newFragment = CustomDialogFragment.newInstance(msg, mType,R.layout.version_update);
        newFragment.show(ft, "dialog");
        newFragment.setOnUpdateVersionListener(new CustomDialogFragment.OnUpdateVersionListener() {
            @Override
            public void update() {
                versionUpdate();
            }
        });
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
            FileDownloader.getImpl().create(Constants.downloadUrl).setPath(savePath).setListener(new FileDownloadListener() {
                 int totalSize =0;
                @Override
                protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    totalSize = totalBytes;
                }

                @Override
                protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    notify.contentView.setProgressBar(R.id.notify_progressBar,totalBytes,soFarBytes,false);
                    float index = soFarBytes*1.0f/totalBytes*100;
                    notify.contentView.setTextViewText(R.id.notiy_title,(int)index+"%");
                    manager.notify(0,notify);
                }

                @Override
                protected void completed(BaseDownloadTask task) {
                    notify.contentView.setTextViewText(R.id.load_status,"下载完成");
                    notify.contentView.setTextViewText(R.id.notiy_title,100+"%");
                   // notify.contentView.setProgressBar(R.id.notify_progressBar,totalSize,totalSize,false);
                    manager.notify(0,notify);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setDataAndType(Uri.fromFile(new File(Environment
                                    .getExternalStorageDirectory()+"/wishwall/", "wishwall.apk")),
                            "application/vnd.android.package-archive");
                    startActivity(intent);
                    Process.killProcess(Process.myPid());
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
                RongIM.getInstance().logout();
                try {
                    Thread.sleep(500);
                    Process.killProcess(Process.myPid());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
