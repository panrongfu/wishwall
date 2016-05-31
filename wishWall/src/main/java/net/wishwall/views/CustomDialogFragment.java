package net.wishwall.views;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import net.wishwall.R;

/**
 * 
 *@Description:日期选择
 *@author panRongFu pan@ipushan.com
 *@date 2015年10月12日 下午4:17:15
 */
public class CustomDialogFragment extends DialogFragment {

    private OnSelectFinishListener mListener;
    private DatePicker datePicker;
    private static int mResource;
    private OnUpdateVersionListener updateListener;
    private static Type mType;
    public enum Type{
        OKAY,OKAY_CANCLE;
    }


    public static CustomDialogFragment newInstance(String message,Type type,int res){
        mResource = res;
        mType = type;
        CustomDialogFragment dialog = new CustomDialogFragment();
        Bundle b = new Bundle();
        b.putString("message", message);
        dialog.setArguments(b);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE,android.R.style.Theme_Holo_Light_Dialog);
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        String content = bundle.getString("message");
        final View view = getActivity().getLayoutInflater().inflate(mResource,null);
        try{
            TextView textView = (TextView) view.findViewById(R.id.custom_dialog_content);
            textView.setText(content);
        }catch (Exception e){

        }
        AlertDialog.Builder buider= null;
        if(mType == Type.OKAY){
            buider = createBuiderWithOkay(view);
        }else if(mType == Type.OKAY_CANCLE){
            buider = createBuiderWithOkayAndCancle(view);
        }
        buider.setView(view);
        return buider.create();
    }

    /**
     * 创建一个只有确定的dialog
     * @param view
     * @return
     */
    public AlertDialog.Builder createBuiderWithOkay(final View view){
        AlertDialog.Builder buider = new AlertDialog.Builder(getActivity());
        buider.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    dismiss();
            }
        });
        return  buider;
    }
    public AlertDialog.Builder createBuiderWithOkayAndCancle(final View view){

        AlertDialog.Builder buider = new AlertDialog.Builder(getActivity());
        buider.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(mListener !=null){
                    mListener.finish((DatePicker)view.findViewById(R.id.dpPicker));
                    dismiss();
                }
                if(updateListener !=null){
                    updateListener.update();
                }
            }
        });
        return buider;
    }

    /**
     * 初始化OnSelectFinishListener
     * @param listener
     */
    public void setOnSelectFinishListener(OnSelectFinishListener listener){
        mListener = listener;
    }

    /**
     * 监听选择完成回调接口
     */
    public interface OnSelectFinishListener{
        void finish(DatePicker dp);
    }

    /**
     * 初始化OnUpdateVersionListener
     * @param listener
     */
    public void setOnUpdateVersionListener(OnUpdateVersionListener listener){
        updateListener = listener;
    }

    /**
     * 版本更新回调接口
     */
    public interface OnUpdateVersionListener{
        void update();
    }
}
