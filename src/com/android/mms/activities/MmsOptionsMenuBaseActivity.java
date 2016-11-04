
package com.android.mms.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import com.android.mms.R;

public abstract class MmsOptionsMenuBaseActivity extends BaseActivity implements
        BaseActivity.BottomKeyClickListener {

	private static final String TAG = MmsOptionsMenuBaseActivity.class.getSimpleName() ;
	
    private BaseMenuItemAdapter mAdapter;
    private int mSelectedPosition ;
    protected ListView lv ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAboveViewStub.setLayoutResource(R.layout.activity_main_listview);
        setBottomKeyClickListener(this);
        View listView = mAboveViewStub.inflate() ;
        handleIntent() ;
        List<String> menuItemsList = loadDatas() ;
        mAdapter = new BaseMenuItemAdapter(this, menuItemsList);
        lv = (ListView) listView.findViewById(R.id.main_list_view);
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(new OptionMenuItemClickListener());
        lv.setOnItemSelectedListener(new OptionMenuItemSelectedListener()) ;
    }
    
    /**
     * 处理Intent 
     */
    public abstract void handleIntent()  ;

	/**
     * 加载数据
     * @return
     */
    public abstract List<String> loadDatas() ;
    
    /**
     * 点击事件
     * @param position
     */
    public abstract void onMenuItemClick(int position) ;

	@Override
    public Button BuildLeftBtn(Button v) {
        v.setText(R.string.select);
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
        v.setText(R.string.option);
        return v;
    }

    @Override
    public void onLeftKeyPress() {
    	Log.d(TAG, "onLeftKeyPress--->>mSelectedPosition:" + mSelectedPosition) ;
        onMenuItemClick(mSelectedPosition) ;
    }

    @Override
    public void onMiddleKeyPress() {

    }

    @Override
    public void onRightKeyPress() {
        finish();
    }

    private class OptionMenuItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        	Log.d(TAG, " onItemClick-->>position:" + position) ;
            onMenuItemClick(position);
        }
    }
    
    private class OptionMenuItemSelectedListener implements OnItemSelectedListener{

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
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    
    protected class BaseMenuItemAdapter extends BaseAdapter {
        private List<String> menus;
        private LayoutInflater inflater;

        public BaseMenuItemAdapter(Context context, List<String> list) {
            this.menus = list;
            this.inflater = ((LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        }

        @Override
        public int getCount() {
            return menus.size();
        }

        @Override
        public Object getItem(int position) {
            return Integer.valueOf(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.list_item_mms_main, null);
                holder.itemIndex = ((TextView) view.findViewById(R.id.mms_main_list_index));
                holder.itemText = (TextView) view.findViewById(R.id.mms_main_list_item_text);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            holder.itemIndex.setText(String.valueOf(position + 1));
            holder.itemText.setText(menus.get(position));
            return view;
        }

        private class ViewHolder {
            TextView itemIndex;
            TextView itemText;
        }
    }
}
