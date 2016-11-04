package com.android.mms.views;

import com.android.mms.R;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

/**
 * 自定义Dialog
 * 
 * @author jackey
 * 
 */
public class CustomAlertDialog {

	private Context mContext;
	private Dialog mAlertDialog;
	private TextView mMsgTextView;
	 //对话框界面
    private View mDialogView;

	public CustomAlertDialog(Context mContext) {
		super();
		this.mContext = mContext;
		
		mDialogView = LayoutInflater.from(mContext)
                .inflate(R.layout.alertdialog_layout, null);
		
		mMsgTextView = (TextView) mDialogView.findViewById(R.id.alertdialog_message);
		
		initDialog() ;
	}

	private void initDialog() {
		mAlertDialog = new Dialog(mContext,R.style.CustomDailogStyle) ;
		mAlertDialog.setContentView(mDialogView) ;
	}

	public void setMessage(int resId) {
		mMsgTextView.setText(resId);
	}

	public void setMessage(String message) {
		mMsgTextView.setText(message);
	}
	
	public void show() {
        if (mAlertDialog != null && !isShowing()) {
        	mAlertDialog.show();
        }
    }

    public void dismiss() {
        if (mAlertDialog != null && isShowing()) {
        	mAlertDialog.dismiss();
        }
    }

    public boolean isShowing() {
        return mAlertDialog.isShowing();
    }

    public void setCanceledOnTouchOutside(boolean isCancle){
        if(mAlertDialog != null){
        	mAlertDialog.setCanceledOnTouchOutside(isCancle);
        }
    }

    /**
     * 设置取消监听
     *
     * @param listener
     */
    public void setCancleListener(DialogInterface.OnCancelListener listener) {
        if(mAlertDialog != null){
        	mAlertDialog.setOnCancelListener(listener);
        }
    }

}
