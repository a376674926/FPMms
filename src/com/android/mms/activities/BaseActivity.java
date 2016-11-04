package com.android.mms.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.location.GpsStatus.Listener;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewStub;
import android.view.Window;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.mms.app.Constants;
import com.android.mms.entitis.SmsInfo;
import com.android.mms.models.MmsModel;
import com.android.mms.models.MmsModelImpl;
import com.android.mms.views.CustomAlertDialog;

import com.android.mms.R;

/**
 * 界面的基类
 * 
 * @author jackey
 * 
 */
public abstract class BaseActivity extends Activity implements
		View.OnClickListener {

	private static final String TAG = BaseActivity.class.getSimpleName();

	private View mRootView;
	protected ViewStub mAboveViewStub;
	protected ViewStub mMiddleViewStub;
	protected ViewStub mBelowViewStub;
	private Button mInitLeftBtn;
	private Button mInitMidBtn;
	private Button mInitRightBtn;

	private Button mLeftBtn;
	private Button mMidBtn;
	private Button mRightBtn;
	private TextView mTopTitle;

	private View mInitTopTitle;
	private BottomKeyClickListener mBottomKeyClickListener;

	private CustomAlertDialog mAlertDialog;
	protected PopupWindow mDeletePop;
	private Button mOkBtn;
	private Button mNoBtn;

	protected MmsModel mMmsModel ;
	
	private SendReceiver sendReceiver = new SendReceiver();
	private DeliverReceiver deliverReceiver = new DeliverReceiver();

	/**
	 * 显示Toast提示框
	 * 
	 * @param msg
	 *            显示提示的字符串
	 */
	protected void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	/**
	 * 显示删除提示框pop
	 */
	protected void showDeletePop() {
		if (mDeletePop == null) {
			mDeletePop = initPopupWindow();
		}
		mDeletePop.showAtLocation(mRootView, Gravity.NO_GRAVITY, 0, 0);
	}
	
	protected void dismissDelePop(){
		if(mDeletePop != null && mDeletePop.isShowing()){
			mDeletePop.dismiss() ;
			mDeletePop = null ;
		}
	}

	private PopupWindow initPopupWindow() {
		View popWindowView = LayoutInflater.from(this).inflate(
				R.layout.delete_popwindow_layout, null);
		popWindowView.setFocusable(true); // 这个很重要  
		popWindowView.setFocusableInTouchMode(true);  
		popWindowView.getBackground().setAlpha(155) ;
		mDeletePop = new PopupWindow(popWindowView, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT, true);
//		mDeletePop.setAnimationStyle(R.style.delete_popwindow_anim);
		mDeletePop.setFocusable(true);
 
		mOkBtn = (Button) popWindowView.findViewById(R.id.pop_btn_ok);
		mNoBtn = (Button) popWindowView.findViewById(R.id.pop_btn_no);
		mOkBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mBottomKeyClickListener.onLeftKeyPress();
			}
		});
		mNoBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mBottomKeyClickListener.onRightKeyPress();
			}
		});
		
		// 重写onKeyListener  
		popWindowView.setOnKeyListener(new View.OnKeyListener() {  
		    @Override  
		    public boolean onKey(View v, int keyCode, KeyEvent event) {  
		        if (keyCode == KeyEvent.KEYCODE_BACK) {  
		        	dismissDelePop() ;  
		            return true;  
		        } else if(keyCode == KeyEvent.KEYCODE_MENU){
		        	//菜单键
					mBottomKeyClickListener.onLeftKeyPress() ;
		        	dismissDelePop() ;  
		        	return true;  
		        } 
		        return false;  
		    }  
		});  
		return mDeletePop;
	}

	/**
	 * 显示对话框
	 */
	protected void showDialog(String msg,
			DialogInterface.OnCancelListener listener) {
		if (mAlertDialog == null) {
			mAlertDialog = new CustomAlertDialog(this);
			mAlertDialog.setCancleListener(listener);
		}
		mAlertDialog.setMessage(msg);
		mAlertDialog.show();
	}

	/**
	 * 关闭对话框
	 */
	protected void dismissDialog() {
		if (mAlertDialog != null) {
			mAlertDialog.dismiss();
		}
	}

	/**
	 * 通过类名启动Activity
	 * 
	 * @param pClass
	 *            启动类的类型
	 */
	protected void launchActivity(Class<?> pClass) {
		launchActivity(pClass, null);
	}

	/**
	 * 通过类名启动Activity
	 * 
	 * @param pClass
	 *            启动类的类型
	 * @param pBundle
	 *            封装的数据
	 */
	protected void launchActivity(Class<?> pClass, Bundle pBundle) {
		launchActivity(pClass, pBundle, -1);
	}

	/**
	 * 通过类名启动Activity，并且含有Bundle数据
	 * 
	 * @param pClass
	 *            启动类的类型
	 * @param pBundle
	 *            封装的数据
	 */
	protected void launchActivity(Class<?> pClass, Bundle pBundle,
			int intentFlag) {
		Intent intent = new Intent(this, pClass);
		if (pBundle != null) {
			intent.putExtras(pBundle);
		}
		if (intentFlag != -1) {
			intent.addFlags(intentFlag);
		}
		startActivity(intent);
	}

	/**
	 * 通过Action启动Activity
	 * 
	 * @param pAction
	 */
	protected void launchActivity(String pAction) {
		launchActivity(pAction, null);
	}

	/**
	 * 通过Action启动Activity，并且含有Bundle数据
	 * 
	 * @param pAction
	 * @param pBundle
	 */
	protected void launchActivity(String pAction, Bundle pBundle) {
		Intent intent = new Intent(pAction);
		if (pBundle != null) {
			intent.putExtras(pBundle);
		}
		startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		mRootView = LayoutInflater.from(this).inflate(R.layout.activity_base,
				null);
		setContentView(mRootView);

		mAboveViewStub = (ViewStub) findViewById(R.id.middle_list_above_viewstub);
		mMiddleViewStub = (ViewStub) findViewById(R.id.middle_list_middle_viewstub);
		mBelowViewStub = (ViewStub) findViewById(R.id.middle_list_below_viewstub);
		buildButtons();
		mMmsModel = new MmsModelImpl(this) ;
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// 注册发送成功的广播
		registerReceiver(sendReceiver, new IntentFilter("SENT_SMS_ACTION"));
		// 注册接收成功的广播
		registerReceiver(deliverReceiver, new IntentFilter(
				"DELIVERED_SMS_ACTION"));
	}

	protected void setActivityBgDrawable(Drawable drawable) {
		mRootView.setBackgroundDrawable(drawable);
	}

	protected void setActivityBgResource(int resid) {
		mRootView.setBackgroundResource(resid);
	}

	protected void setTopTitleDrawable(Drawable drawable) {
		if (mInitTopTitle != null) {
			mInitTopTitle.setBackgroundDrawable(drawable);
		}
	}

	protected void setTopTitleBgResource(int resid) {
		if (mInitTopTitle != null) {
			mInitTopTitle.setBackgroundResource(resid);
		}
	}

	protected void setBottomButtonsDrawable(Drawable drawable) {
		RelativeLayout layout = (RelativeLayout) mRootView
				.findViewById(R.id.bottom_layout);
		layout.setBackgroundDrawable(drawable);
	}

	protected void setBottomButtonsResource(int resid) {
		RelativeLayout layout = (RelativeLayout) mRootView
				.findViewById(R.id.bottom_layout);
		layout.setBackgroundResource(resid);
	}

	private void buildButtons() {
		mLeftBtn = (Button) findViewById(R.id.bottom_left_button);
		mMidBtn = (Button) findViewById(R.id.bottom_middle_button);
		mRightBtn = (Button) findViewById(R.id.bottom_right_button);

		mLeftBtn.setOnClickListener(this);
		mMidBtn.setOnClickListener(this);
		mRightBtn.setOnClickListener(this);

		mTopTitle = (TextView) findViewById(R.id.top_title);

		mInitLeftBtn = BuildLeftBtn(mLeftBtn);
		mInitMidBtn = BuildMiddleBtn(mMidBtn);
		mInitRightBtn = BuildRightBtn(mRightBtn);
		mInitTopTitle = BuildTopTitle(mTopTitle);

		if (mInitLeftBtn == null) {
			mLeftBtn.setVisibility(View.GONE);
		}

		if (mInitMidBtn == null) {
			mMidBtn.setVisibility(View.GONE);
		}
		if (mInitRightBtn == null) {
			mRightBtn.setVisibility(View.GONE);
		}

		if (mInitTopTitle == null) {
			mTopTitle.setVisibility(View.GONE);
		}
	}

	protected void setLeftBtnText(String text) {
		if (TextUtils.isEmpty(text)) {
			mLeftBtn.setVisibility(View.GONE);
		} else {
			mLeftBtn.setVisibility(View.VISIBLE);
			mLeftBtn.setText(text);
		}

	}

	protected void setMidBtnText(String text) {
		if (TextUtils.isEmpty(text)) {
			mMidBtn.setVisibility(View.GONE);
		} else {
			mMidBtn.setVisibility(View.VISIBLE);
			mMidBtn.setText(text);
		}
	}

	protected void setRightText(String text) {
		if (TextUtils.isEmpty(text)) {
			mRightBtn.setVisibility(View.GONE);
		} else {
			mRightBtn.setVisibility(View.VISIBLE);
			mRightBtn.setText(text);
		}
	}

	protected void setTopTitleText(String text) {
		mTopTitle.setText(text);
	}

	@Override
	public void onClick(View v) {

		if (mBottomKeyClickListener == null) {
			return;
		}

		switch (v.getId()) {
		case R.id.bottom_left_button:
			mBottomKeyClickListener.onLeftKeyPress();
			break;
		case R.id.bottom_middle_button:
			mBottomKeyClickListener.onMiddleKeyPress();
			break;
		case R.id.bottom_right_button:
			mBottomKeyClickListener.onRightKeyPress();
			break;
		default:
			break;
		}
	}

	public interface BottomKeyClickListener {

		public void onLeftKeyPress();

		public void onMiddleKeyPress();

		public void onRightKeyPress();
	}

	public void setBottomKeyClickListener(BottomKeyClickListener l) {
		if (l != null) {
			mBottomKeyClickListener = l;
		}
	}

	private class SendReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
		    Log.d(TAG, " SendReceiver --->onReceive ");
			SmsInfo smsInfo = intent.getParcelableExtra(Constants.KEY_SMSINFO) ;
			if(smsInfo == null){
			    Log.d(TAG, " SendReceiver --->onReceive smsInfo == null");
			}
			switch (getResultCode()) {
			case RESULT_OK:
				doSendSuccess(smsInfo) ;
				break;
			default:
				doSendFail(smsInfo) ;
			}
		}
	}

	/**
	 * 处理发送失败
	 * @param smsInfo
	 */
	protected void doSendFail(SmsInfo smsInfo) {
	    if(smsInfo != null){
	        smsInfo.setType(SmsInfo.MESSAGE_TYPE_FAILED) ;
	        mMmsModel.updateMms(Uri.parse(Constants.SMSContentProviderMetaData.SMS_URI_ALL), smsInfo, null) ;
	    }
	    String sendResultStr = getResources().getString(R.string.send_failed);
		showDialog(sendResultStr, null);
	}
	
	/**
	 * 处理发送成功
	 * @param smsInfo
	 */
	protected void doSendSuccess(SmsInfo smsInfo) {
	    if(smsInfo != null){
	        smsInfo.setType(SmsInfo.MESSAGE_TYPE_SENT) ;
	        mMmsModel.updateMms(Uri.parse(Constants.SMSContentProviderMetaData.SMS_URI_ALL), smsInfo, null) ; 
	    }
		String sendResultStr = getResources().getString(R.string.send_success);
		showDialog(sendResultStr, null);
	}
	
	/**
	 * 发送方的短信发送到对方手机上之后,对方手机会返回给运营商一个信号, 运营商再把这个信号发给发送方,发送方此时可确认对方接收成功
	 * 模拟器不支持,真机上需等待片刻
	 * 
	 * @author user
	 * 
	 */
	private class DeliverReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, " DeliverReceiver --->onReceive");
			SmsInfo smsInfo = intent.getParcelableExtra(Constants.KEY_SMSINFO) ;
            if(smsInfo == null){
                Log.d(TAG, " DeliverReceiver --->onReceive smsInfo == null");
            }
		}
	}

	@Override
	protected void onPause() {
		if (mDeletePop != null) {
			mDeletePop.dismiss();
			mDeletePop = null;
		}
		if(mAlertDialog != null){
			mAlertDialog.dismiss() ;
			mAlertDialog = null ;
		}
		unregisterReceiver(sendReceiver) ;
		unregisterReceiver(deliverReceiver) ;
		super.onPause();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			//物理返回键
			mBottomKeyClickListener.onRightKeyPress() ;
			break ;
		case KeyEvent.KEYCODE_MENU:
			//菜单键
			mBottomKeyClickListener.onLeftKeyPress() ;
			break ;
		default:
			break;
		}
		return false ;
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	public abstract Button BuildLeftBtn(Button v);

	public abstract Button BuildMiddleBtn(Button v);

	public abstract Button BuildRightBtn(Button v);

	public abstract TextView BuildTopTitle(TextView v);

}
