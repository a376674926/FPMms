package com.android.mms.activities;

import java.util.Map;

import com.android.mms.R;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

import com.android.mms.adapters.MmsListCursorAdapter;
import com.android.mms.app.Constants;
import com.android.mms.entitis.SmsInfo;
import com.android.mms.models.MmsModelListener;
import com.android.mms.utils.DateFormat;

/**
 * 发件箱主界面
 * @author jackey
 *
 */
public class OutBoxListActivity extends BaseActivity implements
BaseActivity.BottomKeyClickListener,MmsModelListener{
	
	private static final String TAG = OutBoxListActivity.class.getSimpleName() ;
	
	private static final int MMS_OUTBOX_LOADER_ID = 3;
	
	private static final String[] SMS_PROJECTION = new String[] {
    	SmsInfo.ID, SmsInfo.ADDRESS, SmsInfo.BODY, SmsInfo.DATE, SmsInfo.TYPE, SmsInfo.READ
    };
	
	private TextView mEmptyTextView ;
	private ListView mListView ;
	private MmsListCursorAdapter mCursorAdapter ;
	private OutBoxsLoader mOutBoxsLoaderCallBack= new OutBoxsLoader() ;
	private Cursor mOutBoxCursor ;
	
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
				if (option.equals(Constants.KEY_DELETE_MMS)) {
					resDeleteMmsOption(bundle) ;
				}
			}
		}
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
		getLoaderManager().initLoader(MMS_OUTBOX_LOADER_ID, null, mOutBoxsLoaderCallBack) ;
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
		v.setText(R.string.mms_send_box) ;
		return v;
	}

	@Override
	public void onLeftKeyPress() {
		if(mDeletePop != null && mDeletePop.isShowing()){
			if(Constants.KEY_DELETE_MMS.equals(option)){
				mMmsModel.deleteMms(Uri.parse(Constants.SMSContentProviderMetaData.SMS_URI_ALL), mSelectedSmsInfo,this) ;
			}
			mDeletePop.dismiss() ;
		}else{
			Bundle bundle = new Bundle() ;
			bundle.putBoolean(Constants.KEY_SMS_VIEW, false) ;
			bundle.putParcelable(Constants.KEY_SMSINFO, mSelectedSmsInfo) ;
			launchActivity(OutBoxOptionsMenuActivity.class, bundle) ;
		}
	}

	@Override
	public void onMiddleKeyPress() {
		
	}

	@Override
	public void onRightKeyPress() {
		if(mDeletePop != null && mDeletePop.isShowing()){
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
            Log.d(TAG, " onItemSelected-->mSelectedSmsInfo:" + mSelectedSmsInfo.toString() +  mOutBoxCursor.getString(mOutBoxCursor.getColumnIndex(SmsInfo.BODY))) ;
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private class ListItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long id) {
			Log.d(TAG, " onItemClick-->") ;
			mOutBoxCursor.moveToPosition(position) ;
            mSelectedSmsInfo = getSelectedSmsInfo(position) ;
            viewMms(mSelectedSmsInfo) ;
            Log.d(TAG, " onItemClick-->mSelectedSmsInfo:" + mSelectedSmsInfo.toString() +  mOutBoxCursor.getString(mOutBoxCursor.getColumnIndex(SmsInfo.BODY))) ;
		}

    }
	
	/**
	 * 查看短信
	 * @param smsInfo 短信
	 */
	private void viewMms(SmsInfo smsInfo) {
		if(smsInfo != null){
			Bundle bundle = new Bundle() ;
			bundle.putParcelable(Constants.KEY_SMSINFO, mSelectedSmsInfo) ;
			bundle.putInt(Constants.KEY_SMS_TYPE, SmsInfo.MESSAGE_TYPE_OUTBOX) ;
			launchActivity(MmsDisplayActivity.class, bundle) ;
		}
	}
	
	/**
	 * 获取选中短信
	 * @param position
	 * @return
	 */
	public SmsInfo getSelectedSmsInfo(int position){
		mOutBoxCursor.moveToPosition(position) ;
		SmsInfo smsinfo = new SmsInfo();
		smsinfo.setId(mOutBoxCursor.getInt(mOutBoxCursor.getColumnIndex(SmsInfo.ID)));
		smsinfo.setDate(mOutBoxCursor.getLong(mOutBoxCursor.getColumnIndex(SmsInfo.DATE)));
		smsinfo.setPhoneNumber(mOutBoxCursor.getString(mOutBoxCursor.getColumnIndex(SmsInfo.ADDRESS)));
		smsinfo.setSmsbody(mOutBoxCursor.getString(mOutBoxCursor.getColumnIndex(SmsInfo.BODY)));
		smsinfo.setType(mOutBoxCursor.getInt(mOutBoxCursor.getColumnIndex(SmsInfo.TYPE)));
		smsinfo.setRead(mOutBoxCursor.getColumnIndex(SmsInfo.READ) != -1 ? mOutBoxCursor.getInt(mOutBoxCursor.getColumnIndex(SmsInfo.READ))
				: 1);
		return smsinfo ;
	}
	
	private class OutBoxsLoader implements LoaderCallbacks<Cursor> {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        	String selection = "type in ('"+ SmsInfo.MESSAGE_TYPE_FAILED + "','"+SmsInfo.MESSAGE_TYPE_OUTBOX +"')" ;
//        	String[] selectionArgs = {SmsInfo.MESSAGE_TYPE_FAILED +"",SmsInfo.MESSAGE_TYPE_OUTBOX+""} ;
            return new CursorLoader(getApplicationContext(), Uri.parse(Constants.SMSContentProviderMetaData.SMS_URI_ALL),
            		SMS_PROJECTION, selection, null, "date desc") ;
        }
        
        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        	
    		mEmptyTextView.setVisibility((cursor != null && cursor.getCount() > 0) ? View.GONE : View.VISIBLE);
			mListView.setVisibility((cursor != null && cursor.getCount() > 0) ? View.VISIBLE : View.GONE) ;
		    setLeftBtnText((cursor != null && cursor.getCount() > 0) ?getResources().getString(R.string.option):"") ;
		    
		    mOutBoxCursor = cursor ;
            mCursorAdapter.changeCursor(mOutBoxCursor);
		    
        }

        public void onLoaderReset(Loader<Cursor> loader) {
        }
    }
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		getLoaderManager().destroyLoader(MMS_OUTBOX_LOADER_ID) ;
 	}

}