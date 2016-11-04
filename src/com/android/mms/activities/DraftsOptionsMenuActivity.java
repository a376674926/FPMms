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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.android.mms.app.Constants;
import com.android.mms.entitis.SmsInfo;
import com.android.mms.models.MmsErrorHelper;
import com.android.mms.models.MmsModel;
import com.android.mms.models.MmsModelImpl;
import com.android.mms.models.MmsModelListener;

import com.android.mms.R;

/**
 * 草稿箱菜单选项类
 * @author jackey
 *
 */
public class DraftsOptionsMenuActivity extends MmsOptionsMenuBaseActivity implements MmsModelListener {

	private static final String TAG = DraftsOptionsMenuActivity.class.getSimpleName() ;
	
	private MmsModel mMmsModel ;
	//短信模板数据
    private List<String> datas = new ArrayList<String>() ;
    //是否已经查看
    private boolean isSMSView ;
    
    //短信
    private SmsInfo mSmsInfo ;
    
  //写信息 menu options
    private static enum MenuDraft {
        VIEW, SEND,EDIT,DELETE,DELETEALL
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
		String[] menuOptions = getResources().getStringArray(R.array.draft_options) ;
		List<String> list =  Arrays.asList(menuOptions) ;
		datas = new ArrayList<String>(list);
		if(isSMSView){
			datas.remove(0) ;
			datas.remove(datas.size()-1) ;
		}
		return datas ;
	}

	@Override
	public void onMenuItemClick(int position) {
		if(isSMSView){
			position++ ;
		}
		if(position == MenuDraft.VIEW.ordinal()){
			viewDraft(mSmsInfo) ;
		}else if(position == MenuDraft.SEND.ordinal()){
			sendMms(mSmsInfo) ;
		}else if(position == MenuDraft.EDIT.ordinal()){
			editMms(mSmsInfo) ;
		}else if(position == MenuDraft.DELETE.ordinal()){
			deleteMms(mSmsInfo) ;
		}else if(position == MenuDraft.DELETEALL.ordinal()){
			deleteAllMms() ;
		}
	}

	private void deleteAllMms() {
		Bundle bundle = new Bundle() ;
	    bundle.putString(Constants.OPTION,Constants.KEY_DELETE_ALL_MMS) ;
	    launchActivity(DraftListActivity.class, bundle) ;
	}

	/**
	 * 删除短信
	 * @param mSmsInfo
	 */
	private void deleteMms(SmsInfo mSmsInfo) {
	    Bundle bundle = new Bundle() ;
	    bundle.putString(Constants.OPTION,Constants.KEY_DELETE_MMS) ;
	    launchActivity(DraftListActivity.class, bundle) ;
	}

	/**
	 * 编辑短信
	 * @param mSmsInfo2 短信
	 */
	private void editMms(SmsInfo mMmsInfo) {
		Bundle bundle = new Bundle() ;
		bundle.putParcelable(Constants.KEY_SMSINFO, mMmsInfo) ;
		Log.d(TAG, " editMms--->>mMmsInfo:" + mMmsInfo.toString());
		bundle.putInt(Constants.KEY_SMS_TYPE, SmsInfo.MESSAGE_TYPE_DRAFT) ;
		bundle.putInt(CreateNewMmsActivity.EDIT_FOR_TYPE, CreateNewMmsActivity.EDIT_FOR_EDIT) ;
		launchActivity(CreateNewMmsActivity.class, bundle) ;
	}

	/**
	 * 发送短信
	 * @param mSmsInfo2 短信
	 */
	private void sendMms(SmsInfo smsInfo) {
		if(validate(smsInfo)){
			Log.d(TAG, " sendMms-->>smsInfo:" + smsInfo.toString()) ;
			mMmsModel.sendMms(smsInfo) ;
			launchActivity(DraftListActivity.class) ;
		}
	}

	/**
	 * 验证短信
	 * @param smsInfo
	 * @return
	 */
	private boolean validate(SmsInfo smsInfo) {
		if(smsInfo == null){
			return false ;
		}
		final String mAddressee = smsInfo.getPhoneNumber() ;
		final String mMmsInfo = smsInfo.getSmsbody() ;
		if(TextUtils.isEmpty(mAddressee)){
			doNullError(Constants.ERRORCODE_NULL_RECIPIENT, MmsErrorHelper.getMessage(this, Constants.ERRORCODE_NULL_RECIPIENT)) ;
		    return false;
		}
		
		if(TextUtils.isEmpty(mMmsInfo)){
			doNullError(Constants.ERRORCODE_NULL_SMSBODY, MmsErrorHelper.getMessage(this, Constants.ERRORCODE_NULL_SMSBODY)) ;
		    return false ;
		}
		return true  ;
	}
	
	/**
	 * 查看草稿
	 * @param smsInfo 短信
	 */
	private void viewDraft(SmsInfo smsInfo) {
		Bundle bundle = new Bundle() ;
		bundle.putParcelable(Constants.KEY_SMSINFO, smsInfo) ;
		bundle.putInt(Constants.KEY_SMS_TYPE, SmsInfo.MESSAGE_TYPE_DRAFT) ;
		launchActivity(MmsDisplayActivity.class, bundle) ;
	}

	@Override
	public void onError(int errorCode, String errorMsg) {
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
		
	}
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    }

}
