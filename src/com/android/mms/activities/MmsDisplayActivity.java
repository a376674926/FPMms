package com.android.mms.activities;

import com.android.mms.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.mms.app.Constants;
import com.android.mms.entitis.SmsInfo;
import com.android.mms.utils.DateFormat;

/**
 * 草稿显示界面
 * @author jackey
 *
 */
public class MmsDisplayActivity extends BaseActivity implements BaseActivity.BottomKeyClickListener{
	
	private View mRootView ;
	
	private TextView mRecipientTextView ;
	private TextView mDateTextView ;
	private EditText mDraftInfoEditText ;
	
	//短信
	private SmsInfo mSmsInfo ;
	
	//短信类型
	private int mSmsInfoType ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAboveViewStub.setLayoutResource(R.layout.activity_mms_edit_layout);
        mRootView = mAboveViewStub.inflate();
        setBottomKeyClickListener(this);
        setActivityBgResource(0);
        
        mRecipientTextView = (TextView) mRootView.findViewById(R.id.mms_recipient) ;
        mDateTextView = (TextView) mRootView.findViewById(R.id.mms_date) ;
        mDraftInfoEditText = (EditText) mRootView.findViewById(R.id.mms_edit_info) ;
        
        Intent intent = getIntent() ;
        if(intent != null){
        	Bundle bundle = intent.getExtras() ;
        	if(bundle != null){
        		mSmsInfoType = bundle.getInt(Constants.KEY_SMS_TYPE) ;
        		mSmsInfo = bundle.getParcelable(Constants.KEY_SMSINFO) ;
        		if(mSmsInfo != null){
        			mRecipientTextView.setText(mSmsInfo.getPhoneNumber()) ;
            		mDateTextView.setText(DateFormat.format(mSmsInfo.getDate())) ;
            		mDraftInfoEditText.setText(mSmsInfo.getSmsbody()) ;
        		}
        	}
        }
	}

	
	@Override
	public Button BuildLeftBtn(Button v) {
		v.setText(R.string.option) ;
		return v ;
	}

	@Override
	public Button BuildMiddleBtn(Button v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Button BuildRightBtn(Button v) {
		v.setText(R.string.back) ;
		return v ;
	}

	@Override
	public TextView BuildTopTitle(TextView v) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void onLeftKeyPress() {
		Bundle bundle = new Bundle() ;
		Class<?> menuOptionsClass = null ;
		bundle.putParcelable(Constants.KEY_SMSINFO, mSmsInfo) ;
		bundle.putInt(Constants.KEY_SMS_TYPE, mSmsInfoType) ;
		bundle.putBoolean(Constants.KEY_SMS_VIEW, true) ;
		switch (mSmsInfoType) {
		case SmsInfo.MESSAGE_TYPE_DRAFT:
			menuOptionsClass = DraftsOptionsMenuActivity.class ;
			break;
		case SmsInfo.MESSAGE_TYPE_INBOX:
			menuOptionsClass = InBoxOptionsMenuActivity.class ;
			break;
		case SmsInfo.MESSAGE_TYPE_OUTBOX:
			menuOptionsClass = OutBoxOptionsMenuActivity.class ;
			break;
		case SmsInfo.MESSAGE_TYPE_SENT:
			menuOptionsClass = SentMessageOptionsMenuActivity.class ;
			break;
		default:
			break;
		}
		launchActivity(menuOptionsClass, bundle) ;
	}


	@Override
	public void onMiddleKeyPress() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onRightKeyPress() {
		Class<?> startClass = null ;
		switch (mSmsInfoType) {
		case SmsInfo.MESSAGE_TYPE_DRAFT:
			startClass = DraftListActivity.class ;
			break;
		case SmsInfo.MESSAGE_TYPE_INBOX:
			startClass = InBoxListActivity.class ;
			break;
		case SmsInfo.MESSAGE_TYPE_OUTBOX:
			startClass = OutBoxListActivity.class ;
			break;
		case SmsInfo.MESSAGE_TYPE_SENT:
			startClass = SentMessageListActivity.class ;
			break;
		default:
			break;
		}
		launchActivity(startClass) ;
	}

}
