package com.android.mms.activities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.mms.app.Constants;
import com.android.mms.entitis.SmsInfo;
import com.android.mms.models.MmsModel;
import com.android.mms.models.MmsModelImpl;
import com.android.mms.models.MmsModelListener;

import com.android.mms.R;

/**
 * 收件箱菜单选项类
 * @author jackey
 *
 */
public class InBoxOptionsMenuActivity extends MmsOptionsMenuBaseActivity implements MmsModelListener {

	private static final String TAG = InBoxOptionsMenuActivity.class.getSimpleName() ;
	
	private MmsModel mMmsModel ;
	//短信模板数据
    private List<String> datas = new ArrayList<String>() ;
    //短信是否已查看
    private boolean isSMSView ;
    
    //短信
    private SmsInfo mSmsInfo ;
    
  //写信息 menu options
    private static enum MenuDraft {
        VIEW, REPLY,FORWARD,DELETE,CALLSENDER,SAVESENDER
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMmsModel = new MmsModelImpl(this) ;
	}
	
	@Override
	public void handleIntent() {
		Intent intent = getIntent() ;
		if(intent != null){
			Bundle bundle = intent.getExtras() ;
			if(bundle != null){
				isSMSView = bundle.getBoolean(Constants.KEY_SMS_VIEW) ;
				mSmsInfo = bundle.getParcelable(Constants.KEY_SMSINFO) ;
			}
		}
	}

	@Override
	public List<String> loadDatas() {
		String[] menuOptions = getResources().getStringArray(R.array.inbox_options) ;
		List<String> list =  Arrays.asList(menuOptions) ;
		datas = new ArrayList<String>(list);
		if(isSMSView){
			datas.remove(0) ;
		}
		return datas ;
	}

	@Override
	public void onMenuItemClick(int position) {
		if(isSMSView){
			position++ ;
		}
		if(position == MenuDraft.VIEW.ordinal()){
			viewMms(mSmsInfo) ;
		}else if(position == MenuDraft.REPLY.ordinal()){
			replyMms(mSmsInfo) ;
		}else if(position == MenuDraft.FORWARD.ordinal()){
			forwardMms(mSmsInfo) ;
		}else if(position == MenuDraft.DELETE.ordinal()){
			deleteMms(mSmsInfo) ;
		}else if(position == MenuDraft.CALLSENDER.ordinal()){
			callSender(mSmsInfo) ;
		}else if(position == MenuDraft.SAVESENDER.ordinal()){
			saveSender(mSmsInfo) ;
		}
	}

	
	/**
	 * 保存发件人
	 */
	private void saveSender(SmsInfo smsInfo) {
		Bundle bundle = new Bundle() ;
		bundle.putParcelable(Constants.KEY_SMSINFO, smsInfo) ;
		bundle.putInt(Constants.KEY_SMS_TYPE, SmsInfo.MESSAGE_TYPE_INBOX) ;
		launchActivity(AddNewContactActivity.class, bundle) ;
	}

	/**
	 * 呼叫发件人
	 */
	private void callSender(SmsInfo smsInfo) {
		Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+smsInfo.getPhoneNumber()));  
        startActivity(intent);
	}

	/**
	 * 转发短信
	 * @param mSmsInfo
	 */
	private void forwardMms(SmsInfo mSmsInfo) {
		Bundle bundle = new Bundle() ;
		bundle.putInt(Constants.KEY_SMS_TYPE, SmsInfo.MESSAGE_TYPE_INBOX) ;
		bundle.putInt(CreateNewMmsActivity.EDIT_FOR_TYPE, CreateNewMmsActivity.EDIT_FOR_FORWARD) ;
		bundle.putParcelable(Constants.KEY_SMSINFO, mSmsInfo) ;
		launchActivity(CreateNewMmsActivity.class, bundle) ;
	}

	/**
	 * 回复短信
	 * @param mSmsInfo2
	 */
	private void replyMms(SmsInfo smsInfo) {
		Bundle bundle = new Bundle() ;
		bundle.putInt(Constants.KEY_SMS_TYPE, SmsInfo.MESSAGE_TYPE_INBOX) ;
		bundle.putInt(CreateNewMmsActivity.EDIT_FOR_TYPE, CreateNewMmsActivity.EDIT_FOR_REPLY) ;
		bundle.putParcelable(Constants.KEY_SMSINFO, mSmsInfo) ;
		launchActivity(CreateNewMmsActivity.class, bundle) ;
		
	}

	/**
	 * 删除短信
	 * @param mSmsInfo
	 */
	private void deleteMms(SmsInfo mSmsInfo) {
		Bundle bundle = new Bundle() ;
	    bundle.putString(Constants.OPTION,Constants.KEY_DELETE_MMS) ;
	    launchActivity(InBoxListActivity.class, bundle) ;
	}

	/**
	 * 查看短信
	 * @param smsInfo 短信
	 */
	private void viewMms(SmsInfo smsInfo) {
		if(smsInfo != null){
			if(smsInfo.getRead() == SmsInfo.MESSAGE_UNREAD){
				smsInfo.setRead(SmsInfo.MESSAGE_READ) ;
				mMmsModel.updateMms(Uri.parse(Constants.SMSContentProviderMetaData.SMS_URI_INBOX), smsInfo, null) ;
			}
			Bundle bundle = new Bundle() ;
			bundle.putParcelable(Constants.KEY_SMSINFO, smsInfo) ;
			bundle.putInt(Constants.KEY_SMS_TYPE, SmsInfo.MESSAGE_TYPE_INBOX) ;
			launchActivity(MmsDisplayActivity.class, bundle) ;
		}
	}

	@Override
	public void onError(int errorCode, String errorMsg) {
		switch (errorCode) {
		case Constants.ERRORCODE_NULL_RECIPIENT:
		    doNullError(errorCode,errorMsg) ;
			break;
		case Constants.ERRORCODE_NULL_SMSBODY:
		    doNullError(errorCode,errorMsg) ;
			break;
		case Constants.DELETE_FAIL_CODE:
			doDeleteFail(errorCode,errorMsg) ;
			break ;
		default:
			break;
		}
	}

	/**
	 * 处理删除短信失败
	 * @param errorCode
	 * @param errorMsg
	 */
	private void doDeleteFail(int errorCode, String errorMsg) {
		Bundle bundle = new Bundle() ;
		bundle.putString(Constants.OPTION, Constants.KEY_DELETE_MMS) ;
		bundle.putString(Constants.KEY_DELETE_RESULT, errorMsg) ;
		launchActivity(DraftListActivity.class, bundle) ;
	}

	/**
	 * 处理收信人或者短信内容为空错误
	 * @param errorCode
	 * @param errorMsg
	 */
	private void doNullError(int errorCode, String errorMsg) {
		Log.d(TAG, "doNullError") ;
		Bundle bundle = new Bundle() ;
		bundle.putString(Constants.OPTION,Constants.KEY_SEND_MMS) ;
		bundle.putInt(Constants.ERROR_CODE,errorCode) ;
		bundle.putString(Constants.ERROR_MSG,errorMsg) ;
	    launchActivity(DraftListActivity.class, bundle) ;
	}

	@Override
	public void onSuccess(Map<String, Object> responseData) {
		Bundle bundle = new Bundle() ;
		bundle.putString(Constants.OPTION, Constants.KEY_DELETE_MMS) ;
		bundle.putString(Constants.KEY_DELETE_RESULT, getResources().getString(R.string.toast_delete_success)) ;
		launchActivity(DraftListActivity.class,bundle) ;
	}
	
}
