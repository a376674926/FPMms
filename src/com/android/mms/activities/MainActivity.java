package com.android.mms.activities;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;

import com.android.mms.R;
import com.android.mms.R.layout;
import com.android.mms.R.menu;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Telephony;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

import com.android.mms.adapters.MmsMainAdapter;
import com.android.mms.app.Constants;
import com.android.mms.entitis.SmsInfo;

/**
 * Mms主界面
 * @author jackey
 * 
 */
public class MainActivity extends BaseActivity implements
BaseActivity.BottomKeyClickListener{
	
	protected static final String TAG = MainActivity.class.getSimpleName() ;
	private static final String MMS_EXITACTION = "mms.action.exit" ;

	private MmsMainAdapter mAdapter;
	
	private static enum MMS_MAIN_FUNCTION {
		NEW_MMS, IN_BOX, OUT_BOX, SENT_MESSAGE, DRAFTS_BOX
    }
	
	private int mSelectedPosition ;
	
	private ListView mListView ;
	
	//退出广播接收者
    private ExitReceiver mExitReceiver = new ExitReceiver();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mAboveViewStub.setLayoutResource(R.layout.activity_main_listview);
        setBottomKeyClickListener(this);
        
        initView() ;
        
        handleIntent(getIntent()) ;
        
		registerBroadcastReceiver() ;
	}

	@Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        final String myPackageName = getPackageName();
        if (android.os.Build.VERSION.SDK_INT >= 19 && !Telephony.Sms.getDefaultSmsPackage(this).equals(myPackageName)) {  
            // App is not default.  
            // Show the "not currently set as the default SMS app" interface  
            View viewGroup = findViewById(R.id.not_default_app);  
            viewGroup.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
  
            // Set up a button that allows the user to change the default SMS app  
            Button button = (Button) findViewById(R.id.change_default_app);  
            button.setOnClickListener(new View.OnClickListener() {  
                public void onClick(View v) {  
                    Intent intent =  
                            new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);  
                    intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,   
                            myPackageName);  
                    startActivity(intent);  
                }  
            });  
        } else {  
            // App is the default.  
            // Hide the "not currently set as the default SMS app" interface  
            View viewGroup = findViewById(R.id.not_default_app);  
            viewGroup.setVisibility(View.GONE); 
            mListView.setVisibility(View.VISIBLE);
        }  
    }
	
	/**
	 * 注册退出广播接收者
	 */
	private void registerBroadcastReceiver() {
		IntentFilter intentFilter = new IntentFilter(MMS_EXITACTION) ;
		registerReceiver(mExitReceiver, intentFilter) ;
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent) ;
		setIntent(intent) ;
		handleIntent(intent) ;
	}
	/**
	 * 处理Intent
	 */
	private void handleIntent(Intent intent) {
		
	}

	/**
	 * 初始化mms界面
	 */
	private void initView() {
		View listView = mAboveViewStub.inflate();
		String[] mmsItems = getResources().getStringArray(R.array.mms_templates);
        mAdapter = new MmsMainAdapter(this, Arrays.asList(mmsItems)) ;
        mListView = (ListView) listView.findViewById(R.id.main_list_view);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new OptionMmsItemClickListener());
        mListView.setOnItemSelectedListener(new ListItemSelectedListener()) ;
	}

	private class OptionMmsItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        	Log.d(TAG, " onItemClick-->>position:" + position) ;
            onMmsItemClick(position);
        }
    }
	
	private class ListItemSelectedListener implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position,
				long id) {
			Log.d(TAG, " onItemSelected-->>mSelectedPosition:" + position) ;
			mSelectedPosition = position ;
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
		
	}
	
	private void onMmsItemClick(int position) {
		if (position == MMS_MAIN_FUNCTION.NEW_MMS.ordinal()) {
			newMms() ;
		} else if (position == MMS_MAIN_FUNCTION.IN_BOX.ordinal()) {
			inBox();
		} else if (position == MMS_MAIN_FUNCTION.OUT_BOX.ordinal()) {
			outBox();
		} else if (position == MMS_MAIN_FUNCTION.SENT_MESSAGE.ordinal()) {
			sentMessage();
		} else if (position == MMS_MAIN_FUNCTION.DRAFTS_BOX.ordinal()) {
			draftsBox();
		}
	}
	
	/**
	 * 写信息
	 */
	private void newMms() {
		launchActivity(CreateNewMmsActivity.class);
	}
	
	/**
	 * 收件箱
	 */
	private void inBox() {
		launchActivity(InBoxListActivity.class) ;
	}
	
	/**
	 * 发件箱
	 */
	private void outBox() {
		launchActivity(OutBoxListActivity.class) ;
	}
	
	/**
	 * 已发信息
	 */
	private void sentMessage() {
		launchActivity(SentMessageListActivity.class) ;
	}
	
	/**
	 * 草稿箱
	 */
	private void draftsBox() {
		Intent intent = new Intent(this,DraftListActivity.class) ;
		startActivity(intent) ;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public Button BuildLeftBtn(Button v) {
	    v.setText(R.string.mms_options_ok) ;
		return v ;
	}

	@Override
	public Button BuildMiddleBtn(Button v) {
		return null;
	}

	@Override
	public Button BuildRightBtn(Button v) {
		v.setText(R.string.mms_options_back) ;
		return v ;
	}

	@Override
	public TextView BuildTopTitle(TextView v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onLeftKeyPress() {
		onMmsItemClick(mSelectedPosition);
	}

	@Override
	public void onMiddleKeyPress() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRightKeyPress() {
		launchActivity(MainActivity.class) ;
		sendBroadcast(new Intent(MMS_EXITACTION)) ;
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mExitReceiver) ;
	}

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

	class ExitReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
		    MainActivity.this.finish() ;
		}
		
	}

}
