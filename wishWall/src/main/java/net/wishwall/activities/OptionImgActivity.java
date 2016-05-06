package net.wishwall.activities;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.wishwall.R;
import net.wishwall.adapter.OptionImageAdapter;
import net.wishwall.domain.FolderBean;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author panRongFu on 2016/4/26.
 * @Description
 * @email pan@ipushan.com
 */
public class OptionImgActivity extends BaseActivity
        implements View.OnClickListener, OptionImageAdapter.OnItemClickListener{
    private RecyclerView mRecyclerView;
    private OptionImageAdapter mAdapter;
    private TextView mDirName;
    private TextView mDirCount;
    private RelativeLayout mBottomly;
    private File mCurrentDir;
    private int mMaxCount;
    private List<String> mImgs;
    public  List<FolderBean> mFolderBeans = new ArrayList<FolderBean>();
    private ProgressDialog mProgressDialog;
    private ListImgDirPopupWindow mDirPopupWindow;
    private GridLayoutManager gridLayout;
    private static final int DATA_LOADED=0X110;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            if(msg.what==DATA_LOADED){
                mProgressDialog.dismiss();
                //绑定数据到view中
                dataToView();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.option_image);
        initViewUI();
        initData();
    }
    private void initViewUI() {
        gridLayout = new GridLayoutManager(getApplicationContext(),3);
//        gridLayout.setSpanCount(3);
        mRecyclerView = (RecyclerView) findViewById(R.id.option_img_list);
        mBottomly = (RelativeLayout)findViewById(R.id.id_bottom_ly);
        mDirName = (TextView)findViewById(R.id.id_dir_name);
        mDirCount = (TextView)findViewById(R.id.id_dir_count);
        mBottomly.setOnClickListener(this);
    }

    /**
     * 扫描手机上所有的图片
     */
    private void initData() {
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Toast.makeText(this, "当前储存卡不可用", Toast.LENGTH_LONG).show();
            return ;
        }
        mProgressDialog = ProgressDialog.show(this, null, "正在加载");
        //创建线程扫描
        new Thread(){
            public void run() {
                Uri imgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver cr = OptionImgActivity.this.getContentResolver();
                //只查询jpeg和png的图片
                Cursor cursor = cr.query(imgUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[] { "image/jpeg", "image/png" }, MediaStore.Images.Media.DATE_MODIFIED);
                Set<String> mDirPahts = new HashSet<String>();

                while(cursor.moveToNext()){
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    File parentFile = new File(path).getParentFile();
                    if(parentFile == null)
                        continue;
                    String dirPath = parentFile.getAbsolutePath();
                    FolderBean folderBean = null;
                    if(mDirPahts.contains(dirPath)){
                        continue;
                    }else{
                        mDirPahts.add(dirPath);
                        folderBean  = new FolderBean();
                        folderBean.setDir(dirPath);
                        folderBean.setFirstImagePath(path);
                    }
                    if(parentFile.list()== null)
                        continue;

                    int picSize = parentFile.list(new FilenameFilter() {

                        @Override
                        public boolean accept(File dir, String filename) {
                            if(filename.endsWith(".jpg")
                                    ||filename.endsWith(".jpeg")
                                    ||filename.endsWith(".png"))
                                return true;
                            return false;
                        }
                    }).length;

                    folderBean.setCount(picSize);
                    mFolderBeans.add(folderBean);
                    if(picSize > mMaxCount){
                        mMaxCount = picSize;
                        mCurrentDir = parentFile;
                    }
                }
                cursor.close();

                //扫描图片完成
                mHandler.sendEmptyMessage(DATA_LOADED);
            };
        }.start();
    }
    private void dataToView() {
        if(mCurrentDir == null){
            Toast.makeText(this, "未扫描到任何图片", Toast.LENGTH_LONG).show();
            return;
        }

        mImgs = Arrays.asList(mCurrentDir.list());
        mAdapter = new OptionImageAdapter(this, mImgs, mCurrentDir.getAbsolutePath());
        mAdapter.setOnItemClickListener(this);
      //  Toast.makeText(this, mCurrentDir.getAbsolutePath(), 1000).show();
        mRecyclerView.setLayoutManager(gridLayout);
        mRecyclerView.setAdapter(mAdapter);

        mDirCount.setText(mMaxCount+"");
        mDirName.setText(mCurrentDir.getName());

        initPopupWindow();

    }
    protected void initPopupWindow() {
        mDirPopupWindow = new ListImgDirPopupWindow(this, mFolderBeans);
        mDirPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                lightOn();
            }
        });
        mDirPopupWindow.setOnDirSelectedListener(new ListImgDirPopupWindow.OnDirSelectedListener() {

            @Override
            public void onSelected(FolderBean folderBean) {
                mCurrentDir = new File(folderBean.getDir());
                mImgs = Arrays.asList(mCurrentDir.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
                        if(filename.endsWith(".jpg")
                                ||filename.endsWith(".jpeg")
                                ||filename.endsWith(".png"))
                            return true;
                        return false;
                    }
                }));
                mAdapter = new OptionImageAdapter(OptionImgActivity.this, mImgs, mCurrentDir.getAbsolutePath());
                mRecyclerView.setLayoutManager(gridLayout);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.setOnItemClickListener(OptionImgActivity.this);
                mDirName.setText(folderBean.getName());
                mDirCount.setText(mImgs.size()+"");
                mDirPopupWindow.dismiss();
            }
        });
    }

    /**
     * 内容区域变亮
     */
    protected void lightOn() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 1.0f;
        getWindow().setAttributes(lp);

    }
    /**
     * 内容区域变暗
     */
    protected void lightOff() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.3f;
        getWindow().setAttributes(lp);

    }

    @Override
    public void onClick(View v) {
        mDirPopupWindow.setAnimationStyle(R.style.popupwindow);
        mDirPopupWindow.showAsDropDown(mBottomly, 0, 0);
        lightOff();
    }

    @Override
    public void onClickItemListener(View view, int position) {
        String path = view.getTag().toString();
        Intent intent = new Intent();
        intent.putExtra("path",path);
        setResult(RESULT_OK,intent);
        this.finish();
    }
}
