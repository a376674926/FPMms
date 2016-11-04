package com.android.mms.activities;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.android.mms.R;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

import com.android.mms.activities.MmsOptionsMenuBaseActivity.BaseMenuItemAdapter;
import com.android.mms.app.Constants;
import com.android.mms.entitis.SmsInfo;
import com.android.mms.models.MmsErrorHelper;
import com.android.mms.models.MmsModel;
import com.android.mms.models.MmsModelImpl;
import com.android.mms.models.MmsModelListener;

/**
 * 写信息菜单选项类
 * @author jackey
 *
 */
public class CreateNewMmsOptionsMenuActivity extends MmsOptionsMenuBaseActivity implements MmsModelListener{
	
	private static final String TAG = CreateNewMmsOptionsMenuActivity.class.getSimpleName() ;
	
    //短信
	private SmsInfo mSmsInfo ;
	
	//编辑短信类型
    private int editMmsType ;
	
	//业务处理
	private MmsModel mMmsModel ;
	
	//写信息 menu options
    private static enum MenuNewMms {
        SEND, INSERT_TEMPLATE,ADVANCED_OPTIONS,SAVE,ADD_CONTACTS
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMmsModel = new MmsModelImpl(this) ;
	}
	
	@Override
	public void handleIntent() {
		Intent intent = getIntent();
		if(intent != null){
			Bundle data = intent.getExtras() ;
			if(data != null){
				editMmsType = data.getInt(Constants.KEY_SMS_TYPE) ;
				mSmsInfo = data.getParcelable(Constants.KEY_SMSINFO) ;
			}
		}
	}
	
	/**
	 * 加载写信息菜单选项数据
	 */
	@Override
	public List<String> loadDatas() {
		String[] menuOptions = getResources().getStringArray(R.array.new_mms_menu_options) ;
		List<String> datas =  Arrays.asList(menuOptions) ;
		return datas ;
	}

	/**
	 * 菜单选项点击事件
	 */
	@Override
	public void onMenuItemClick(int position) {
		Log.d(TAG, " --->>onMenuItemOnClick position:" + position) ;
		if(position == MenuNewMms.SEND.ordinal()){
			sendMms(mSmsInfo) ;
		}else if(position == MenuNewMms.INSERT_TEMPLATE.ordinal()){
			insertTemplates() ;
		}else if(position == MenuNewMms.ADVANCED_OPTIONS.ordinal()){
			advancedOptions() ;
		}else if(position == MenuNewMms.SAVE.ordinal()){
			saveDraft(mSmsInfo) ;
		}else if(position == MenuNewMms.ADD_CONTACTS.ordinal()){
			addNewContacts() ;
		}
	}

	/**
	 * 添加联系人
	 */
	private void addNewContacts() {
		Bundle bundle = new Bundle() ;
		bundle.putString(ContactsListActivity.INSERT_TYPE, ContactsListActivity.INSERT_RECIPIENT) ;
		launchActivity(ContactsListActivity.class, bundle) ;
	}

	/**
	 * 保存到草稿箱
	 */
	private void saveDraft(SmsInfo smsInfo) {
		if(smsInfo != null){
			mMmsModel.saveMms(Uri.parse(Constants.SMSContentProviderMetaData.SMS_URI_DRAFT), smsInfo, this) ;
		}
		backMmsType(editMmsType) ;
	}

	/**
	 * 高级选项
	 */
	private void advancedOptions() {
		launchActivity(AdvancedOptionsMenuActivity.class) ;
	}

	/**
	 * 插入模板
	 */
	private void insertTemplates() {
		Intent intent = new Intent(this,InsertTemplateOptionsMenuActivity.class) ;
		startActivity(intent) ;
	}

	/**
	 * 发送信息
	 */
	private void sendMms(SmsInfo smsInfo) {
		if(validate(smsInfo)){
			mMmsModel.sendMms(smsInfo) ;
			backMmsType(editMmsType) ;
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
		launchActivity(CreateNewMmsActivity.class, bundle) ;
	}

	@Override
	public void onSuccess(Map<String, Object> responseData) {

	}
	
	/**
	 * 返回到不同短信类型界面
	 */
	public void backMmsType(int editMmsType){
		Class<?> startClass = null ;
		switch (editMmsType) {
		case SmsInfo.MESSAGE_TYPE_DRAFT:
			startClass = DraftListActivity.class ;
			break;
		case SmsInfo.MESSAGE_TYPE_INBOX:
			startClass = InBoxListActivity.class ;
			break;
		case SmsInfo.MESSAGE_TYPE_SENT:
			startClass = SentMessageListActivity.class ;
			break;
		default:
			startClass = MainActivity.class ;
			break;
		}
		launchActivity(startClass) ;
	}
	
}  

