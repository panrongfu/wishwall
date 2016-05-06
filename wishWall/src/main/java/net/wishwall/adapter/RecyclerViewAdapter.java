package net.wishwall.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mob.tools.utils.UIHandler;

import net.wishwall.R;
import net.wishwall.views.CustomToast;

import cn.sharesdk.MyPlatform;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.socialization.Comment;
import cn.sharesdk.socialization.CommentFilter;
import cn.sharesdk.socialization.CommentListener;
import cn.sharesdk.socialization.LikeListener;
import cn.sharesdk.socialization.QuickCommentBar;
import cn.sharesdk.socialization.Socialization;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>
                            implements Handler.Callback ,View.OnClickListener{

    private Context mContext;
    private OnItemClickListener mListener;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private OnekeyShare oks;
    private QuickCommentBar qcBar;
    private CommentFilter filter;
    private Context context;
    private View mView;
    private static final int INIT_SDK = 1;
    private static final int AFTER_LIKE = 2;

    // 模拟的主题id
    private String topicId;
    // 模拟的主题标题
    private String topicTitle;
    // 模拟的主题发布时间
    private String topicPublishTime;
    // 模拟的主题作者
    private String topicAuthor;

    public RecyclerViewAdapter(Context mContext) {
        this.mContext = mContext;

        new Thread() {
            public void run() {
                UIHandler.sendEmptyMessageDelayed(INIT_SDK, 100, RecyclerViewAdapter.this);
            }
        }.start();
        initSocialization();
    }

    public void setOnItemClickListener(OnItemClickListener listener){

        this.mListener = listener;
    }
    public interface OnItemClickListener{

        void onItemClick(View view, int position);
        void onItemLongClick(View view , int position);
    }

    @Override
    public int getItemViewType(int position) {
        if(position + 1 == getItemCount()){
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_ITEM){
            mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_card_main, parent, false);
            return new ViewHolder(mView);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.footerview, null);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT));
        return new FooterViewHolder(view);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final RecyclerViewAdapter.ViewHolder holder, final int position) {
        final View view = holder.mView;

        if(mListener != null){
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(view,position);
                }
            });
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mListener.onItemLongClick(view,position);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    class FooterViewHolder extends ViewHolder {
        public FooterViewHolder(View view) {
            super(view);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what){
            case INIT_SDK:
                //topicId = mContext.getString(R.string.comment_like_id);
                topicId = "pan511752921";
                topicTitle = mContext.getString(R.string.comment_like_title);
                topicPublishTime = mContext.getString(R.string.comment_like_publich_time);
                topicAuthor = mContext.getString(R.string.comment_like_author);
//
//                TopicTitle tt = (TopicTitle) findViewById(R.id.llTopicTitle);
//                String topicTitle = getString(R.string.comment_like_title);
//                tt.setTitle(topicTitle);
//                tt.setPublishTime(getString(R.string.comment_like_publich_time));
//                tt.setAuthor(getString(R.string.comment_like_author));

                Socialization service = ShareSDK.getService(Socialization.class);
                service.setCustomPlatform(new MyPlatform(mContext));
                initOnekeyShare();
                initQuickCommentBar();
                CustomToast.showMsg(mContext,"INIT_SDK INIT_SDK");
                break;
            case AFTER_LIKE:
                if(msg.arg1 == 1){
                    //success
                  //  int resId = getStringRes(context, "like_success");
                   // if (resId > 0) {
                      //  Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
                  //  }
                    CustomToast.showMsg(mContext,"点赞成功");
                }else {
                    //fail
//                    int resId = mContext.getResources().getStringRes(context, "like_fail");
//                    if (resId > 0) {
//                        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
//                    }
                    CustomToast.showMsg(mContext,"失败");
                }
                break;
            case 3:
                break;
            default:
                break;
        }
        return false;
        }

    @Override
    public void onClick(View v) {

    }

    private void initSocialization() {
        //设置评论监听
        Socialization.setCommentListener(new CommentListener() {
            @Override
            public void onSuccess(Comment comment) {
                CustomToast.showMsg(mContext,"评论成功");
            }
            @Override
            public void onFail(Comment comment) {
                CustomToast.showMsg(mContext,"失败--onFail");
            }
            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
                Log.e("onError", "onError: "+t.getMessage() );
                CustomToast.showMsg(mContext,t.getMessage());
            }
        });
        //设置点赞监听
        Socialization.setLikeListener(new LikeListener() {
            @Override
            public void onSuccess(String s, String s1, String s2) {
                Message msg = new Message();
                msg.what = AFTER_LIKE;
                msg.arg1 = 1;
                UIHandler.sendMessage(msg,RecyclerViewAdapter.this);
            }

            @Override
            public void onFail(String s, String s1, String s2, String s3) {
                Message msg = new Message();
                msg.what = AFTER_LIKE;
                msg.arg1 = 2;
                UIHandler.sendMessage(msg, RecyclerViewAdapter.this);
            }
        });
    }
    // Socialization服务依赖OnekeyShare组件
    // 此方法初始化一个OnekeyShare对象
    private void initOnekeyShare() {
        oks = new OnekeyShare();
        oks.setAddress("12345678901");
        oks.setTitle("许愿墙");
        oks.setTitleUrl("http://www.baidu.com");
        oks.setText("许愿墙Text");
        oks.setUrl("http://www.baidu.com");
        oks.setComment("评论");
        oks.setSite("许愿墙");
        oks.setSiteUrl("http://www.baidu.com");
        oks.setVenueName("ShareSDK");
        oks.setVenueDescription("This is a beautiful place!");
        oks.disableSSOWhenAuthorize();
        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            public void onShare(Platform platform, Platform.ShareParams paramsToShare) {
                // 改写twitter分享内容中的text字段，否则会超长，
                // 因为twitter会将图片地址当作文本的一部分去计算长度
                if ("Twitter".equals(platform.getName())) {
                    paramsToShare.setText(platform.getContext().getString(R.string.share_content_short));
                }
            }
        });
    }

    private void initQuickCommentBar() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_card_main,null);
        qcBar = (QuickCommentBar) view.findViewById(R.id.qcBar);
        Log.e("initQuickCommentBar","topicId++"+topicId);
        qcBar.setTopic(topicId, topicTitle, topicPublishTime, topicAuthor);
        qcBar.setTextToShare("setTextToShare");
        //qcBar.getBackButton().setOnClickListener(this);
        qcBar.setAuthedAccountChangeable(false);

        CommentFilter.Builder builder = new CommentFilter.Builder();
        // 非空过滤器
        builder.append(new CommentFilter.FilterItem() {
            // 返回true表示是垃圾评论
            public boolean onFilter(String comment) {
                if (TextUtils.isEmpty(comment)) {
                    return true;
                } else if (comment.trim().length() <= 0) {
                    return true;
                } else if (comment.trim().toLowerCase().equals("null")) {
                    return true;
                }
                return false;
            }

            @Override
            public int getFilterCode() {
                return 0;
            }
        });
        // 字数上限过滤器
        builder.append(new CommentFilter.FilterItem() {
            // 返回true表示是垃圾评论
            public boolean onFilter(String comment) {
                if (comment != null) {
                    String pureComment = comment.trim();
                    String wordText = com.mob.tools.utils.R.toWordText(pureComment, 140);
                    if (wordText.length() != pureComment.length()) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public int getFilterCode() {
                return 0;
            }
        });
        filter = builder.build();
        qcBar.setCommentFilter(filter);
        qcBar.setOnekeyShare(oks);
    }
}
