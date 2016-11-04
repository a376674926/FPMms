package com.android.mms.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.android.mms.R;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.android.mms.adapters.MmsListAdapter;
import com.android.mms.adapters.MmsListCursorAdapter;
import com.android.mms.app.Constants;
import com.android.mms.entitis.Contact;
import com.android.mms.entitis.SmsInfo;
import com.android.mms.models.MmsModel;
import com.android.mms.models.MmsModelImpl;
import com.android.mms.models.MmsModelListener;
import com.android.mms.utils.DateFormat;

/**
 * 草稿箱
 * @author jackey
 *
 */
public class DraftListActivity extends BaseActivity implements
BaseActivity.BottomKeyClickListener,MmsModelListener{
	
	private static final String TAG = DraftListActivity.class.getSimpleName() ;
	
	private static final int MMS_DRAFT_LOADER_ID = 5;
	
	private static final String[] SMS_PROJECTION = new String[] {
    	"_id", "address", "person", "body", "date", "type"
    };
	
	private TextView mEmptyTextView ;
	private ListView mListView ;
	private MmsListCursorAdapter mCursorAdapter ;
	private DraftsLoader mDraftsLoaderCallBack= new DraftsLoader() ;
	private Cursor mDraftCursor ;
	private MmsModel mMmsModel  ;
	
	private SmsInfo mSelectedSmsInfo ;
	
	private String option ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		mMiddleViewStub.setLayoutResource(R.layout.activity_main_listview);
        View listView = mMiddleViewStub.inflate();
        mEmptyTextView = (TextView) listView.findViewById(R.id.empty);
        
        mListView = (ListView) listView.findViewById(R.id.main_list_view);
        
        mCursorAdapter = new MmsListCursorAdapter(this,null) ;
        mListView.setAdapter(mCursorAdapter) ;
        
        mListView.setOnItemSelectedListener(new ListItemSelectedListener()) ;
        mListView.setOnItemClickListener(new ListItemClickListener());
        
        setBottomKeyClickListener(this) ;
        setActivityBgResource(0) ;
        
        mMmsModel = new MmsModelImpl(this) ;
        
        loadDatas() ;
        handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		Log.d(TAG, "onNewIntent stared");
		setIntent(intent) ;
		handleIntent(intent);
	}
	
	/**
	 * 处理Intent
	 */
	private void handleIntent(Intent intent) {
		if (intent != null) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				option = bundle.getString(Constants.OPTION);
				if(TextUtils.isEmpty(option))
					return ;
				if(option.equals(Constants.KEY_SEND_MMS)){
					resSendMmsOption(bundle) ;
				}else if (option.equals(Constants.KEY_DELETE_MMS)) {
					resDeleteMmsOption(bundle) ;
				}else if(option.equals(Constants.KEY_DELETE_ALL_MMS)){
					resDeleteAllMmsOption(bundle) ;
				}
			}
		}
	}

	/**
	 * 处理发送选项
	 */
	private void resSendMmsOption(Bundle bundle) {
		int errorCode = bundle.getInt(Constants.ERROR_CODE);
		String errorMsg = bundle.getString(Constants.ERROR_MSG);
		Log.d(TAG, "respSendMmsOption -->> errorCode:" + errorCode
				+ " errorMsg:" + errorMsg);
		switch (errorCode) {
		case Constants.ERRORCODE_NULL_RECIPIENT:
		case Constants.ERRORCODE_NULL_SMSBODY:
			showDialog(errorMsg, null) ;
			break;
		default:
			break;
		}
	}
	
	/**
	 * 删除所有短信
	 * @param bundle
	 */
    private void resDeleteAllMmsOption(Bundle bundle) {
		showDeletePop() ;
	}

	/**
     * 响应删除短信
     * @param bundle
     */
	private void resDeleteMmsOption(Bundle bundle) {
		showDeletePop() ;
	}

	/**
	 * 加载数据
	 */
	private void loadDatas() {
		getLoaderManager().initLoader(MMS_DRAFT_LOADER_ID, null, mDraftsLoaderCallBack) ;
	}
	
	@Override
	public Button BuildLeftBtn(Button v) {
		v.setText(R.string.option);
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
		v.setText(R.string.mms_drafts_box) ;
		return v;
	}

	@Override
	public void onLeftKeyPress() {
		if(mDeletePop != null && mDeletePop.isShowing()){
			if(Constants.KEY_DELETE_MMS.equals(option)){
				mMmsModel.deleteMms(Uri.parse(Constants.SMSContentProviderMetaData.SMS_URI_ALL), mSelectedSmsInfo,this) ;
			}else if(Constants.KEY_DELETE_ALL_MMS.equals(option)){
				mMmsModel.deleteAllMms(Uri.parse(Constants.SMSContentProviderMetaData.SMS_URI_ALL),this) ;
			}
			mDeletePop.dismiss() ;
		}else{
			Bundle bundle = new Bundle() ;
			bundle.putBoolean(Constants.KEY_SMS_VIEW, false) ;
			bundle.putParcelable(Constants.KEY_SMSINFO, mSelectedSmsInfo) ;
			launchActivity(DraftsOptionsMenuActivity.class, bundle) ;
		}
		
	}

	@Override
	public void onMiddleKeyPress() {
		
	}

	@Override
	public void onRightKeyPress() {
		Log.d(TAG, "onRightKeyPress---->>") ;
		if(mDeletePop != null && mDeletePop.isShowing()){
			Log.d(TAG, "onRightKeyPress---->>mDeletePop!=null") ;
			mDeletePop.dismiss() ;
		}else{
		    finish() ;
		}
	}

	@Override
	public void onSuccess(final Map<String, Object> responseData) {
		runOnUiThread(new Runnable(){
			@Override
			public void run() {
				int successCode = (Integer) responseData.get(Constants.KEY_SMS_SUCCESS_CODE) ;
				switch (successCode) {
				case Constants.LOAD_SMS_SUCCESS:
					break;
		        case Constants.DELETE_SMS_SUCCESS:
		        	showDialog(getResources().getString(R.string.delete_done), null) ;
					break;
		        case Constants.DELETE_ALL_SMS_SUCCESS:
		        	showDialog(getResources().getString(R.string.delete_done), null) ;
					break;
				default:
					break;
				}
			}
		}) ;
	}

	@Override
	public void onError(int errorCode, String errorMsg) {
		switch (errorCode) {
		case Constants.DELETE_FAIL_CODE:
			showDialog(errorMsg, null) ;
			break;
		default:
			break;
		}
		
	}

	private class ListItemSelectedListener implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position,
				long id) {
			mSelectedSmsInfo = getSelectedSmsInfo(position) ;
            Log.d(TAG, " onItemSelected-->mSelectedSmsInfo:" + mSelectedSmsInfo.toString() +  mDraftCursor.getString(mDraftCursor.getColumnIndex(SmsInfo.BODY))) ;
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
		
	}
	
	private class ListItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			mDraftCursor.moveToPosition(position) ;
            mSelectedSmsInfo = getSelectedSmsInfo(position) ;
            viewDraft(mSelectedSmsInfo) ;
            Log.d(TAG, " onItemClick-->mSelectedSmsInfo:" + mSelectedSmsInfo.toString()) ;
		}

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
	
	/**
	 * 获取选中短信
	 * @param position
	 * @return
	 */
	public SmsInfo getSelectedSmsInfo(int position){
		mDraftCursor.moveToPosition(position) ;
		SmsInfo smsinfo = new SmsInfo();
		smsinfo.setId(mDraftCursor.getInt(mDraftCursor.getColumnIndex(SmsInfo.ID)));
		smsinfo.setDate(mDraftCursor.getLong(mDraftCursor.getColumnIndex(SmsInfo.DATE)));
		smsinfo.setPhoneNumber(mDraftCursor.getString(mDraftCursor.getColumnIndex(SmsInfo.ADDRESS)));
		smsinfo.setSmsbody(mDraftCursor.getString(mDraftCursor.getColumnIndex(SmsInfo.BODY)));
		smsinfo.setType(mDraftCursor.getInt(mDraftCursor.getColumnIndex(SmsInfo.TYPE)));
		smsinfo.setRead(mDraftCursor.getColumnIndex(SmsInfo.READ) != -1 ? mDraftCursor.getInt(mDraftCursor.getColumnIndex(SmsInfo.READ))
				: 1);
		return smsinfo ;
	}
	
	private class DraftsLoader implements LoaderCallbacks<Cursor> {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            
            return new CursorLoader(getApplicationContext(), Uri.parse(Constants.SMSContentProviderMetaData.SMS_URI_DRAFT),
            		SMS_PROJECTION, null, null, "date desc") ;
        }
        
        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        	
    		mEmptyTextView.setVisibility((cursor != null && cursor.getCount() > 0) ? View.GONE : View.VISIBLE);
			mListView.setVisibility((cursor != null && cursor.getCount() > 0) ? View.VISIBLE : View.GONE) ;
		    setLeftBtnText((cursor != null && cursor.getCount() > 0) ?getResources().getString(R.string.option):"") ;
		    
		    mDraftCursor = cursor ;
            mCursorAdapter.changeCursor(mDraftCursor);
		    
        }

        public void onLoaderReset(Loader<Cursor> loader) {
        }
    }
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		getLoaderManager().destroyLoader(MMS_DRAFT_LOADER_ID) ;
 	}

}
