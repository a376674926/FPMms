package com.android.mms.activities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.android.mms.R;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.android.mms.app.Constants;
import com.android.mms.models.MmsModel;
import com.android.mms.models.MmsModelImpl;
import com.android.mms.models.MmsModelListener;

/**
 * 插入模板短信模板选项类
 * @author jackey
 *
 */
public class InsertTemplateOptionsMenuActivity extends MmsOptionsMenuBaseActivity{
	
	private static final String TAG = InsertTemplateOptionsMenuActivity.class.getSimpleName() ;
	
	//短信模板数据
	private List<String> datas = new ArrayList<String>() ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void handleIntent() {
		
	}
	
	/**
	 * 加载写信息菜单选项数据
	 */
	@Override
	public List<String> loadDatas() {
		String[] menuOptions = getResources().getStringArray(R.array.sms_templates) ;
		datas =  Arrays.asList(menuOptions) ;
		return datas ;
	}

	/**
	 * 菜单选项点击事件
	 */
	@Override
	public void onMenuItemClick(int position) {
		Bundle datasBundle = new Bundle() ;
		Intent intent = new Intent(this,CreateNewMmsActivity.class) ;
		datasBundle.putString(Constants.OPTION, CreateNewMmsActivity.INSERT_TEMPLATE) ;
		datasBundle.putString(CreateNewMmsActivity.SMS_TEMPLATES, datas.get(position)) ;
		intent.putExtras(datasBundle) ;
		startActivity(intent) ;
	}
	
	@Override
    public Button BuildLeftBtn(Button v) {
        v.setText(R.string.option_ok);
        return v;
    }

    @Override
    public void onRightKeyPress() {
        launchActivity(CreateNewMmsActivity.class) ;
    }
}
