
package com.android.mms.activities;

import com.android.mms.R;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.mms.app.Constants;
import com.android.mms.entitis.SmsInfo;
import com.android.mms.models.MmsModel;
import com.android.mms.models.MmsModelImpl;

/**
 * 添加联系人
 * @author jackey
 *
 */
public class AddNewContactActivity extends BaseActivity implements
        BaseActivity.BottomKeyClickListener {
	
    private static final String TAG = AddNewContactActivity.class.getSimpleName() ;
    
	private View mRootView;
    private EditText mNameEditText;
    private EditText mNumberEditText;
    private int editMmsType ;
    private SmsInfo mSmsInfo ;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAboveViewStub.setLayoutResource(R.layout.phone_add_new_contact);
        mRootView = mAboveViewStub.inflate();
        mNameEditText = (EditText) mRootView.findViewById(R.id.contact_name);
        mNumberEditText = (EditText) mRootView.findViewById(R.id.contact_item_number);
        
        mNameEditText.addTextChangedListener(new MmsTextWatcher());
        mNumberEditText.addTextChangedListener(new MmsTextWatcher());
        mNumberEditText.setOnFocusChangeListener(new MmsFocusChangeListener()) ;
		mNameEditText.setOnFocusChangeListener(new MmsFocusChangeListener()) ;

        setBottomKeyClickListener(this);
        setActivityBgResource(0);
        
        handleIntent(getIntent()) ;
    }

    /**
     * 处理intent
     * @param intent
     */
    private void handleIntent(Intent intent) {
		if(intent != null){
			Bundle bundle = intent.getExtras() ;
			if(bundle != null){
				editMmsType = bundle.getInt(Constants.KEY_SMS_TYPE) ;
				mSmsInfo = bundle.getParcelable(Constants.KEY_SMSINFO) ;
				setContactData(mSmsInfo) ;
			}
		}
	}

    /**
     * 设置编辑联系人数据
     * @param mSmsInfo2
     */
	private void setContactData(SmsInfo smsInfo) {
		if(smsInfo != null){
			String phoneNumber = smsInfo.getPhoneNumber() ;
			mNumberEditText.setText(phoneNumber) ;
			mNameEditText.requestFocus() ;
		}
	}

	@Override
    public Button BuildLeftBtn(Button v) {
        v.setText(R.string.save);
        return v;
    }

    @Override
    public Button BuildMiddleBtn(Button v) {
        return null;
    }

    @Override
    public Button BuildRightBtn(Button v) {
        v.setText(R.string.back);
        return v;
    }

    @Override
    public TextView BuildTopTitle(TextView v) {
        return null;
    }

    @Override
    public void onLeftKeyPress() {
        saveContact();
        if(editMmsType == SmsInfo.MESSAGE_TYPE_INBOX){
        	launchActivity(InBoxListActivity.class) ;
        }else{
        	finish();
        }
    }

    @Override
    public void onMiddleKeyPress() {

    }

    @Override
    public void onRightKeyPress() {
    	Log.d(TAG, " onRightKeyPress-->>") ;
		if (mNameEditText.isFocused()) {
			deleteEditTextInfo(mNameEditText) ;
		} else {
			deleteEditTextInfo(mNumberEditText) ;
		}
    }
    
    public void deleteEditTextInfo(EditText editText){
		String mmsInfo = editText.getText().toString();
		if (TextUtils.isEmpty(mmsInfo)) {
			switch (editMmsType) {
			case SmsInfo.MESSAGE_TYPE_INBOX:
				launchActivity(InBoxListActivity.class) ;
				break;
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
    
    private boolean saveContact() {
        String name = mNameEditText.getText().toString();
        String number = mNumberEditText.getText().toString();

        if(validte(name,number)){
        	mMmsModel.saveContacts(name, number) ;
        	return true;
        }
        return false ;
    }
    
    public boolean validte(String name,String number){
    	if (TextUtils.isEmpty(name.trim())) {
            showToast(getString(R.string.toast_name_null));
            return false;
        }
        if (TextUtils.isEmpty(number.trim())) {
            showToast(getString(R.string.toast_number_null));
            return false;
        }
        return true ;
    }
    
}
