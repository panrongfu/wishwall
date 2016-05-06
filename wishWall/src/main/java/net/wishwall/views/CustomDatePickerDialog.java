package net.wishwall.views;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.DatePicker;

import net.wishwall.R;

/**
 * 
 *@Description:日期选择
 *@author panRongFu pan@ipushan.com
 *@date 2015年10月12日 下午4:17:15
 */
public class CustomDatePickerDialog extends DialogFragment {

    private OnSelectFinishListener mListener;
    private DatePicker datePicker;

    public static CustomDatePickerDialog newInstance(String prompt){
        CustomDatePickerDialog dialog = new CustomDatePickerDialog();
        Bundle b = new Bundle();
        b.putString("prompt-message", prompt);
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
        final View view = getActivity().getLayoutInflater().inflate(R.layout.date_picker_dialog,null);
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
            }
        }).setView(view);
        return buider.create();
    }

    /**
     * 设置回调接口
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
}
