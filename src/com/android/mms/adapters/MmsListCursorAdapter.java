package com.android.mms.adapters;

import com.android.mms.R;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.mms.entitis.SmsInfo;
import com.android.mms.utils.DateFormat;

public class MmsListCursorAdapter extends MBaseCursorAdapter {

	private static final String TAG = MmsListAdapter.class.getSimpleName() ;
	private int mSelectedPosition = -1 ;
	
	public MmsListCursorAdapter(Context context, Cursor c) {
		super(context, c);
	}

	@Override
	public View newView(Context context, Cursor cursor,ViewGroup viewGroup) {
		ViewHolder viewHolder = new ViewHolder();
        View view = mInflater.inflate(R.layout.list_item_mms, null);
        viewHolder.itemText = ((TextView) view.findViewById(R.id.mms_list_item_text));
        viewHolder.itemDate = (TextView) view.findViewById(R.id.mms_list_item_date);
        view.setTag(viewHolder);
        return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor,int position) {
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.itemDate.setText(DateFormat.format(cursor.getLong(cursor.getColumnIndex(SmsInfo.DATE))));
        holder.itemText.setText(cursor.getString(cursor.getColumnIndex(SmsInfo.BODY))) ;
        if(mSelectedPosition == position){
        	Log.d(TAG, " selected true") ;
        	holder.itemText.setSelected(true) ;
        }else{
        	Log.d(TAG, " selected false") ;
        	holder.itemText.setSelected(false) ;
        }
	}
	
	private class ViewHolder {
        TextView itemDate;
        TextView itemText;
    }
	
	public void setSelectedItemPosition(int position){
		mSelectedPosition = position ;
		notifyDataSetChanged() ;
	}

}
