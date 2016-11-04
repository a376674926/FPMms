package com.android.mms.adapters;

import com.android.mms.R;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.mms.entitis.SmsInfo;
import com.android.mms.utils.DateFormat;

public class MmsInBoxListCursorAdapter extends MBaseCursorAdapter {

	private static final String TAG = MmsInBoxListCursorAdapter.class.getSimpleName() ;
	private int mSelectedPosition = -1 ;
	
	public MmsInBoxListCursorAdapter(Context context, Cursor c) {
		super(context, c);
	}

	@Override
	public View newView(Context context, Cursor cursor,ViewGroup viewGroup) {
		ViewHolder holder = new ViewHolder();
        View view = mInflater.inflate(R.layout.list_item_inbox, null);
        holder.itemText = ((TextView) view.findViewById(R.id.mms_list_item_text));
        holder.itemDate = (TextView) view.findViewById(R.id.mms_list_item_date);
        holder.itemUnReadImg = (ImageView) view.findViewById(R.id.mms_list_item_readStatus) ;
        view.setTag(holder);
        return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor,int position) {
		ViewHolder holder = (ViewHolder) view.getTag();
        holder.itemDate.setText(DateFormat.format(cursor.getLong(cursor.getColumnIndex(SmsInfo.DATE))));
        holder.itemText.setText(cursor.getString(cursor.getColumnIndex(SmsInfo.BODY))) ;
        int mmsReadStatus = cursor.getInt(cursor.getColumnIndex(SmsInfo.READ)) ;
        if(mmsReadStatus == 1){
        	holder.itemUnReadImg.setVisibility(View.GONE) ;
        }else{
        	holder.itemUnReadImg.setVisibility(View.VISIBLE) ;
        }
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
        ImageView itemUnReadImg ;
    }
	
	public void setSelectedItemPosition(int position){
		mSelectedPosition = position ;
		notifyDataSetChanged() ;
	}

}
