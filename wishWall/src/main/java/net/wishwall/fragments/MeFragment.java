package net.wishwall.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.squareup.picasso.Picasso;

import net.wishwall.App;
import net.wishwall.Constants;
import net.wishwall.R;
import net.wishwall.activities.ImageSelectPopupWindow;
import net.wishwall.activities.IssueWishActivity;
import net.wishwall.activities.MainActivity;
import net.wishwall.activities.MorePopupWindow;
import net.wishwall.activities.OptionImgActivity;
import net.wishwall.activities.ScanQRcodeActivity;
import net.wishwall.adapter.WishAdapter;
import net.wishwall.aliyunoss.ResuambleUploadSamples;
import net.wishwall.domain.CenterPicDTO;
import net.wishwall.domain.MorePopupItemBean;
import net.wishwall.domain.ResultDTO;
import net.wishwall.domain.UploadTokenDTO;
import net.wishwall.domain.WishsDTO;
import net.wishwall.service.ApiClient;
import net.wishwall.utils.ImageTools;
import net.wishwall.utils.SaveBitmap2File;
import net.wishwall.utils.SpUtil;
import net.wishwall.views.CustomToast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author panRongFu on 2016/4/22.
 * @Description
 * @email pan@ipushan.com
 */
public class MeFragment extends Fragment implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener ,AppBarLayout.OnOffsetChangedListener,IssueWishActivity.OnIssueFinishListener{
    MainActivity activity;
    private TextView toolarTitle;
    private FloatingActionButton issueWish;
    private ImageView mImageView;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefreshlayout;
    private LinearLayoutManager layoutManager;
    private GridLayoutManager gridLayout;
    private WishAdapter adapter;
    boolean isLoading = false;
    private static List<WishsDTO.ResultBean> myWishList = new ArrayList<WishsDTO.ResultBean>();
    private AppBarLayout appBarLayout;
    private IssueWishActivity issueWishActivity;
    private ImageButton moreImageButton;
    private MorePopupWindow morePopupWindow;
    private ImageSelectPopupWindow imgPopupwindow;
    private static final int CROP = 2;
    private static final int CROP_PICTURE = 3;
    private static final int FROM_PHOTO = 7 ;
    private SpUtil tempSpUtil;
    private SpUtil userSpUtil;

     Toolbar toolbar;
    private Type mType = Type.REFRESH;
    private static int page=1;
    private OSSClient oss;

    private enum Type{
        REFRESH,LOAD_MORE
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IssueWishActivity.setOnIssueFinishListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity)context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.person_fragment,container,false);
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("个人中心");
        initViewUI(view);
        initData(mType);
        return view;
    }

    private void initViewUI(View view) {
        tempSpUtil = new SpUtil(getActivity(),"temp");
        userSpUtil = new SpUtil(getActivity(), Constants.USER_SPUTIL);
        mImageView = (ImageView)view.findViewById(R.id.me_background_pic);
        toolbar = (Toolbar) view.findViewById(R.id.person_toolbar);
        layoutManager = new LinearLayoutManager(getActivity());
        appBarLayout = (AppBarLayout) view.findViewById(R.id.appbar);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.person_recycler_view);
        mRefreshlayout = (SwipeRefreshLayout) view.findViewById(R.id.person_swipe_refresh_view);
        moreImageButton = (ImageButton)view.findViewById(R.id.me_more_btn);
        mRecyclerView.setLayoutManager(layoutManager);
        mRefreshlayout.setOnRefreshListener(this);
        mRecyclerView.addOnScrollListener(new LoadMoreScrollListener());
        issueWish =(FloatingActionButton) view.findViewById(R.id.issue_wish);
        issueWish.setOnClickListener(this);
        moreImageButton.setOnClickListener(this);
        appBarLayout.addOnOffsetChangedListener(this);
        initImgPopupWindow();
        initMyCenterPic();
    }

    /**
     * 个人中心的背景图片
     */
    private void initMyCenterPic() {
        ApiClient.findMyCenterPic(new Callback<CenterPicDTO>() {
            @Override
            public void onResponse(Call<CenterPicDTO> call, Response<CenterPicDTO> response) {
                CenterPicDTO body  = response.body();
                if(body.getCode() == 200){
                    CenterPicDTO.ResultBean result = body.getResult();
                    if(result != null){
                        String picUrl = result.getPic_url();
                        Picasso.with(getActivity()).load(picUrl).into(mImageView);
                    }
                }
            }
            @Override
            public void onFailure(Call<CenterPicDTO> call, Throwable t) {
                    t.printStackTrace();
            }
        });
    }

    private void initImgPopupWindow() {
        imgPopupwindow = new ImageSelectPopupWindow(getActivity());
        imgPopupwindow.setOnItemClickListener(new ImageSelectPopupWindow.OnItemClickListener() {
            int REQUEST_CODE;
            @Override
            public void fromPhoto() {
                Intent intent = new Intent(getActivity(),OptionImgActivity.class);
                startActivityForResult(intent,FROM_PHOTO);
                imgPopupwindow.dismiss();
            }

            @Override
            public void fromcamera() {
                Uri imageUri = null;
                String fileName = null;
                Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                REQUEST_CODE = CROP;

                // 删除上一次截图的临时文件
                String tempName = tempSpUtil.getKeyValue("tempName");
                String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                ImageTools.deletePhotoAtPathAndName(path,tempName);

                // 保存本次截图临时文件名字
                fileName = String.valueOf(System.currentTimeMillis()) + ".jpg";
                tempSpUtil.setKeyValue("tempName",fileName);

                imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), fileName));
                // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(openCameraIntent, REQUEST_CODE);
                imgPopupwindow.dismiss();
            }

            @Override
            public void cancel() {
                imgPopupwindow.dismiss();
            }
        });

        imgPopupwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                lightOn();
            }
        });
    }
    /**
     * 获取许愿条
     */
    private void initData(final Type type) {
        ApiClient.findMyWish(page, new Callback<WishsDTO>() {
            @Override
            public void onResponse(Call<WishsDTO> call, Response<WishsDTO> response) {
                WishsDTO body = response.body();
                if(body.getCode() == 200){
                    List<WishsDTO.ResultBean>list = body.getResult();

                    if(type == Type.REFRESH){
                        myWishList = list;
                        if(myWishList.size()==0){
                            CustomToast.showMsg(getActivity(),"暂无数据");
                        }
                        mRefreshlayout.setRefreshing(false);

                    }else if(type == Type.LOAD_MORE){
                        isLoading = false;
                        //加载更多的时候数据以及获取完毕
                        if(list.size() == 0){
                            WishAdapter.hasWish = false;
                        }
                        for(WishsDTO.ResultBean wr: list){
                            myWishList.add(wr);
                        }
                    }
                    if(adapter == null){
                        adapter = new WishAdapter(getActivity());
                        adapter.setWishList(myWishList);
                        mRecyclerView.setAdapter(adapter);
                    }else {
                        adapter.setWishList(myWishList);
                        adapter.notifyDataSetChanged();
                    }

                }
            }
            @Override
            public void onFailure(Call<WishsDTO> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.issue_wish:
                startActivity(new Intent(getActivity(), IssueWishActivity.class));
                break;
            case R.id.me_more_btn:
                initMorePopupWindow(v);
                lightOff(0.8f);
                break;
        }
    }

    @Override
    public void finish() {
        initData(Type.REFRESH);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (verticalOffset == 0) {
            mRefreshlayout.setEnabled(true);
            toolbar.setVisibility(View.INVISIBLE);
        } else {
            mRefreshlayout.setEnabled(false);
            toolbar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 初始化popupwindow
     */
    private void initMorePopupWindow(View view) {
        morePopupWindow = new MorePopupWindow(getActivity());
        morePopupWindow.setAnimationStyle(R.style.MorePopupwindow);

        String [] title = {"更换背景","扫一扫"};
        int [] imgs = {R.mipmap.more_popup_item_bg,R.mipmap.more_popup_scan};
        List<MorePopupItemBean> list = new ArrayList<MorePopupItemBean>();

        for(int i=0; i<imgs.length;i++){
            MorePopupItemBean item = new MorePopupItemBean(getActivity());
            item.setDrawable(imgs[i]);
            item.setItemTitle(title[i]);
            list.add(item);
        }

        morePopupWindow.setItemList(list);
        morePopupWindow.showPopupWindow(view);
        morePopupWindow.setOnMorePopupItemClickListener(new MorePopupWindow.OnMorePopupItemClickListener() {
            @Override
            public void morePopupItemClick(int position) {
                CustomToast.showMsg(getActivity(),">>>>>>>>"+position);
                switch (position){
                    case 0:
                        imgPopupwindow.setAnimationStyle(R.style.popupwindow);
                        imgPopupwindow.showAtLocation(MainActivity.mainActiviyView, Gravity.BOTTOM,0,20);
                        lightOff(0.3f);
                        break;
                    case 1:
                        startActivity(new Intent(getActivity(), ScanQRcodeActivity.class));
                        getActivity().overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
                        break;
                }
            }
        });
        morePopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                morePopupWindow.dismiss();
                lightOn();
            }
        });
    }

    @Override
    public void onRefresh() {
        mRefreshlayout.setRefreshing(true);
        page = 1;
        myWishList.clear();
        initData(Type.REFRESH);
    }

    /**
     * 加载更多
     */
    class LoadMoreScrollListener extends RecyclerView.OnScrollListener{

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            mRefreshlayout.setEnabled(layoutManager.findFirstCompletelyVisibleItemPosition() == 0);
            super.onScrolled(recyclerView, dx, dy);
            int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
            //若最后一个可见tiem的
            if(lastVisibleItemPosition + 1 == adapter.getItemCount() && adapter.getItemCount()>=10){
                boolean isRefreshing = mRefreshlayout.isRefreshing();
                if(isRefreshing){
                    adapter.notifyItemRemoved(adapter.getItemCount());
                    return;
                }
                if(!isLoading){
                    isLoading = true;
                    page = page+1;
                    initData(Type.LOAD_MORE);
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case FROM_PHOTO:
                    String path = data.getStringExtra("path");
                    Log.e("PHOTO", "onActivityResult: "+path );
                    cropImage(Uri.fromFile(new File(path)), 155, 155, CROP_PICTURE);
                    break;
                case CROP:
                    Uri uri = null;
                    if (data != null) {
                        uri = data.getData();
                    }else {
                        String fileName = tempSpUtil.getKeyValue("tempName");
                        uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),fileName));
                    }
                    cropImage(uri, 155, 155, CROP_PICTURE);
                    break;

                case CROP_PICTURE:
                    Bitmap photo = null;
                    Uri photoUri = data.getData();
                    if (photoUri != null) {
                        photo = BitmapFactory.decodeFile(photoUri.getPath());
                    }
                    if (photo == null) {
                        Bundle extra = data.getExtras();
                        if (extra != null) {
                            photo = (Bitmap)extra.get("data");
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        }
                    }
                    savaImage(photo);
                    break;
            }
        }
    }

    //截取图片
    public void cropImage(Uri uri, int outputX, int outputY, int requestCode){
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, requestCode);
    }

    /**
     * 保存图片
     * @param data
     */
    private void savaImage(Bitmap data) {
        if (data != null) {
            Bitmap bitmap = data;
            try {
                String name = UUID.randomUUID().toString()+System.currentTimeMillis()+".jpg";  ;
                final String path = SaveBitmap2File.getSDPath()+ "/wishwall/";
                SaveBitmap2File.saveFile(bitmap, path,name);// 保存图片到手机
                mImageView.setImageBitmap(bitmap);
                final String uploadPath = path + name;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Response<UploadTokenDTO> res = ApiClient.synchGetUploadToken();
                        UploadTokenDTO body = res.body();
                        if(body.getCode() == 200){
                            String accessKeyId = body.getResult().getAccessKeyId();
                            String accessKeySecret = body.getResult().getAccessKeySecret();
                            String securityToken = body.getResult().getSecurityToken();
                            startUpload(accessKeyId,accessKeySecret,securityToken,uploadPath);
                        }
                    }
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 开始上传图片
     * @param accessKeyId
     * @param accessKeySecret
     */
    private void startUpload(String accessKeyId, String accessKeySecret,String securityToken, final String path) {

        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(accessKeyId, accessKeySecret,securityToken);

        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        OSSLog.enableLog();
        oss = new OSSClient(getActivity(), App.endpoint, credentialProvider, conf);
        oss.updateCredentialProvider(new OSSStsTokenCredentialProvider(accessKeyId, accessKeySecret, securityToken));

        new Thread(new Runnable() {
            @Override
            public void run() {
                String name = UUID.randomUUID().toString()+System.currentTimeMillis()+".jpg";
                ResuambleUploadSamples resuambleUploadSamples = new ResuambleUploadSamples(oss,App.uploadBucket,name,path);
                resuambleUploadSamples.setUpLoadFinishListener(new ResuambleUploadSamples.UpLoadFinishListener() {
                    @Override
                    public void finish(String url) {
                        //上传到阿里云oss上的存储图片的url
                        ApiClient.addMyCenterPic(url, new Callback<ResultDTO>() {
                            @Override
                            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                                ResultDTO body = response.body();
                                if(body.getCode()==200){
                                    CustomToast.showMsg(getActivity(),"添加成功");
                                }
                            }

                            @Override
                            public void onFailure(Call<ResultDTO> call, Throwable t) {
                                CustomToast.showMsg(getActivity(),"添加失败");
                            }
                        });
                    }

                    @Override
                    public void fail() {
                    }
                });
                resuambleUploadSamples.resumableUpload();
            }
        }).start();
    }

    /**
     * 内容区域变亮
     */
    protected void lightOn() {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = 1.0f;
        getActivity().getWindow().setAttributes(lp);

    }
    /**
     * 内容区域变暗
     */
    protected void lightOff(float light) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = light;
        getActivity().getWindow().setAttributes(lp);

    }

    @Override
    public void onPause() {
        super.onPause();
        appBarLayout.removeOnOffsetChangedListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        appBarLayout.addOnOffsetChangedListener(this);
    }
}
