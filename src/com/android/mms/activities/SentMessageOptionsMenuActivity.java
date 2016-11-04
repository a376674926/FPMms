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
 * 已发信息菜单选项类
 * @author jackey
 *
 */
public class SentMessageOptionsMenuActivity extends MmsOptionsMenuBaseActivity implements MmsModelListener {

	private static final String TAG = SentMessageOptionsMenuActivity.class.getSimpleName() ;
	
	//短信模板数据
    private List<String> datas = new ArrayList<String>() ;
    //短信是否已查看
    private boolean isSMSView ;
    
    //短信
    private SmsInfo mSmsInfo ;
    
  //发件箱信息 menu options
    private static enum MenuDraft {
        VIEW, FORWARD, DELETE
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		String[] menuOptions = getResources().getStringArray(R.array.sentmessage_options) ;
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
		}else if(position == MenuDraft.FORWARD.ordinal()){
			forwardMms(mSmsInfo) ;
		}else if(position == MenuDraft.DELETE.ordinal()){
			deleteMms(mSmsInfo) ;
		}
	}
	
	/**
	 * 转发短信
	 * @param mSmsInfo 短信
	 */
	private void forwardMms(SmsInfo smsInfo) {
		Bundle bundle = new Bundle() ;
		bundle.putInt(Constants.KEY_SMS_TYPE, SmsInfo.MESSAGE_TYPE_SENT) ;
		bundle.putInt(CreateNewMmsActivity.EDIT_FOR_TYPE, CreateNewMmsActivity.EDIT_FOR_FORWARD) ;
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
	    launchActivity(SentMessageListActivity.class, bundle) ;
	}

	/**
	 * 查看短信
	 * @param smsInfo 短信
	 */
	private void viewMms(SmsInfo smsInfo) {
		if(smsInfo != null){
			Bundle bundle = new Bundle() ;
			bundle.putParcelable(Constants.KEY_SMSINFO, smsInfo) ;
			bundle.putInt(Constants.KEY_SMS_TYPE, SmsInfo.MESSAGE_TYPE_SENT) ;
			launchActivity(MmsDisplayActivity.class, bundle) ;
		}
	}

	@Override
	public void onError(int errorCode, String errorMsg) {
	
	}

	@Override
	public void onSuccess(Map<String, Object> responseData) {
		// TODO Auto-generated method stub
		
	}

}
