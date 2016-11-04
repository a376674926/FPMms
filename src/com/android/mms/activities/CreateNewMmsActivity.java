package com.android.mms.activities;

import com.android.mms.R;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.mms.app.Constants;
import com.android.mms.entitis.SmsInfo;
import com.android.mms.views.CustomAlertDialog;

/**
 * 写信息主界面
 * 
 * @author jackey
 * 
 */
public class CreateNewMmsActivity extends BaseActivity implements
		BaseActivity.BottomKeyClickListener {

	private static final String TAG = CreateNewMmsActivity.class
			.getSimpleName();

	public static final String INSERT_TEMPLATE = "insert_template";
	public static final String ADVANCED_OPTIONS = "advanced_options";
	public static final String SAVE = "save";
	public static final String ADD_CONTACTS = "add_contacts";
	public static final String SMS_TEMPLATES = "sms_templates";
	public static final String INSERT_INFO = "insert_info" ;
	public static final String INSERT_RECIPIENT = "insert_recipient" ;
	public static final String EDIT_FOR_TYPE = "edit_for_type" ;
	public static final int EDIT_FOR_REPLY = 1 ;
	public static final int EDIT_FOR_FORWARD = 2 ;
	public static final int EDIT_FOR_EDIT = 3 ;
	
	private View mRootView;

	// 联系人编辑框
	private EditText mRecipientEditText;

	// 短信内容
	private EditText mMmsInfoEditText;

	// 提示对话框
	private CustomAlertDialog mAlertDialog;
	
	//编辑短信类型(收件箱，发件箱，已发信息，草稿箱)
	private int editMmsType = 0;
	
	//编辑短信作用类型（回复，转发，简单编辑，创建默认）
	private int editForType = 0;
	
	//短信
	private SmsInfo mSmsInfo ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		mAboveViewStub.setLayoutResource(R.layout.activity_create_new_mms);
		mRootView = mAboveViewStub.inflate();
		mRecipientEditText = (EditText) mRootView
				.findViewById(R.id.mms_recipient);
		mMmsInfoEditText = (EditText) mRootView
				.findViewById(R.id.mms_edit_info);
		mMmsInfoEditText.addTextChangedListener(new MmsTextWatcher());
		mRecipientEditText.addTextChangedListener(new MmsTextWatcher());
		mRecipientEditText.setOnFocusChangeListener(new MmsFocusChangeListener()) ;
		mMmsInfoEditText.setOnFocusChangeListener(new MmsFocusChangeListener()) ;

		setBottomKeyClickListener(this);
		setActivityBgResource(0);

		handleIntent(getIntent()) ;
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		setIntent(intent) ;
		handleIntent(intent) ;
	}

	/**
	 * 处理Intent
	 */
	private void handleIntent(Intent intent) {
		if (intent != null) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				editMmsType = bundle.getInt(Constants.KEY_SMS_TYPE) ;
				editForType = bundle.getInt(EDIT_FOR_TYPE) ;
				mSmsInfo = bundle.getParcelable(Constants.KEY_SMSINFO) ;
				if (editMmsType!=0) {
					setEditData(mSmsInfo) ;
				}
				String option = bundle.getString(Constants.OPTION);
				if(TextUtils.isEmpty(option))
					return ;
				if (option.equals(Constants.KEY_SEND_MMS)) {
					respSendMmsOption(bundle);
				} else if (option.equals(INSERT_TEMPLATE)) {
					respInsertTemplate(bundle);
				}else if(option.equals(ADVANCED_OPTIONS)){
					respAdvancedOptions(bundle);
				}else if(option.equals(ADD_CONTACTS)){
					respAddContacts(bundle);
				}
			}
		}
	}

	/**
	 * 设置编辑短信
	 * @param mSmsInfo2
	 */
	private void setEditData(SmsInfo smsInfo) {
		
		if(smsInfo != null){
			switch (editForType) {
			case EDIT_FOR_FORWARD:
				mMmsInfoEditText.setText(smsInfo.getSmsbody()) ;
				mRecipientEditText.requestFocus();// get the focus
				break;
            case EDIT_FOR_REPLY:
            	mRecipientEditText.setText(smsInfo.getPhoneNumber()) ;
            	mMmsInfoEditText.requestFocus();// get the focus
				break;
            case EDIT_FOR_EDIT:
                mRecipientEditText.setText(smsInfo.getPhoneNumber()) ;
                mMmsInfoEditText.setText(smsInfo.getSmsbody()) ;
                mMmsInfoEditText.requestFocus();// get the focus
                mMmsInfoEditText.setSelection(smsInfo.getSmsbody()==null?0:smsInfo.getSmsbody().length());
                break;
			default:
				break;
			}
			
		}
	}

	/**
	 * 响应添加联系人
	 * @param bundle
	 */
	private void respAddContacts(Bundle bundle) {
		String recipient = bundle.getString(INSERT_RECIPIENT);
		insertMmsRecipient(recipient) ;
	}

	/**
	 * 响应高级选项
	 * @param bundle
	 */
	private void respAdvancedOptions(Bundle bundle) {
		String info = bundle.getString(INSERT_INFO);
		insertMmsInfo(info) ;
	}

	/**
	 * 响应插入模板选项
	 */
	private void respInsertTemplate(Bundle bundle) {
		String smsData = bundle.getString(SMS_TEMPLATES);
		insertMmsInfo(smsData) ;
	}
	
	/**
	 * 插入短信内容
	 */
	public void insertMmsInfo(String info){
		String mmsInfo = mMmsInfoEditText.getText().toString() + info;
		mMmsInfoEditText.setText(mMmsInfoEditText.getText().toString()
				+ info);
		mMmsInfoEditText.requestFocus();// get the focus
		mMmsInfoEditText.setSelection(mmsInfo.length());
	}
	
	/**
	 * 插入短信收信人
	 */
	public void insertMmsRecipient(String recipient){
		mRecipientEditText.setText(recipient);
		mMmsInfoEditText.requestFocus();// get the focus
	}

	/**
	 * 响应发送选项
	 */
	private void respSendMmsOption(Bundle bundle) {

		int errorCode = bundle.getInt(Constants.ERROR_CODE);
		String errorMsg = bundle.getString(Constants.ERROR_MSG);
		Log.d(TAG, "respSendMmsOption -->> errorCode:" + errorCode
				+ " errorMsg:" + errorMsg);
		switch (errorCode) {
		case Constants.ERRORCODE_NULL_RECIPIENT:
		case Constants.ERRORCODE_NULL_SMSBODY:
			showDialog(errorMsg,null);
			break;
		default:
			break;
		}
	}

	@Override
	public Button BuildLeftBtn(Button v) {
		v.setText(R.string.option);
		return v;
	}

	@Override
	public Button BuildMiddleBtn(Button v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Button BuildRightBtn(Button v) {
		v.setText(R.string.back);
		return v;
	}

	@Override
	public TextView BuildTopTitle(TextView v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onLeftKeyPress() {
		Bundle bundle = new Bundle();
		if(editForType == 0){
		    mSmsInfo = new SmsInfo() ;
		}
		mSmsInfo.setPhoneNumber(mRecipientEditText.getText()
				.toString()) ;
		mSmsInfo.setSmsbody(mMmsInfoEditText.getText()
				.toString()) ;
		bundle.putParcelable(Constants.KEY_SMSINFO, mSmsInfo);
		Log.d(TAG, " onLeftKeyPress-->>smsInfo:" + mSmsInfo) ;
		bundle.putInt(Constants.KEY_SMS_TYPE, editMmsType) ;
		launchActivity(CreateNewMmsOptionsMenuActivity.class, bundle) ;
	}

	@Override
	public void onMiddleKeyPress() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRightKeyPress() {
		Log.d(TAG, " onRightKeyPress-->>") ;
		if (mMmsInfoEditText.isFocused()) {
			deleteEditTextInfo(mMmsInfoEditText) ;
		} else {
			deleteEditTextInfo(mRecipientEditText) ;
		}

	}
	
	public void deleteEditTextInfo(EditText editText){
		String mmsInfo = editText.getText().toString();
		if (TextUtils.isEmpty(mmsInfo)) {
			switch (editMmsType) {
			case SmsInfo.MESSAGE_TYPE_DRAFT:
				launchActivity(DraftListActivity.class) ;
				break;
			case SmsInfo.MESSAGE_TYPE_INBOX:
				launchActivity(InBoxListActivity.class) ;
				break;
			case SmsInfo.MESSAGE_TYPE_OUTBOX:
				launchActivity(OutBoxListActivity.class) ;
				break ;
			case SmsInfo.MESSAGE_TYPE_SENT:
				launchActivity(SentMessageListActivity.class) ;
				break ;
			default:
				break;
			}
			finish();
		} else {
			int mmsInfoLen = mmsInfo.length();
			mmsInfoLen-- ;
			mmsInfo = mmsInfo.substring(0, mmsInfoLen);
			editText.setText(mmsInfo) ;
			editText.setSelection(mmsInfo.length()) ;
		}
	}

	private class MmsTextWatcher implements TextWatcher {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			String mmsInfo = s.toString().replaceAll("\\s*", "");
			Log.d(TAG, " onTextChanged()-->> mmsInfo:" + mmsInfo);
			if (!TextUtils.isEmpty(mmsInfo)) {
				setRightText(getResources().getString(R.string.delete));
			} else {
				setRightText(getResources().getString(R.string.back));
			}
		}

		@Override
		public void afterTextChanged(Editable s) {

		}

	}

	private class MmsFocusChangeListener implements OnFocusChangeListener {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			String editInfo = ((EditText) v).getText().toString() ;
			if (hasFocus) {
				if (!TextUtils.isEmpty(editInfo)) {
					setRightText(getResources().getString(R.string.delete));
				} else {
					setRightText(getResources().getString(R.string.back));
				}
			}
		}
	}

}
