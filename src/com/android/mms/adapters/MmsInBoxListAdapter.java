package com.android.mms.adapters;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.mms.entitis.SmsInfo;
import com.android.mms.utils.DateFormat;

import com.android.mms.R;

public class MmsInBoxListAdapter extends MBaseAdapter<SmsInfo> {

	private static final String TAG = MmsInBoxListAdapter.class.getSimpleName() ;
	private int mSelectedPosition ;

	public MmsInBoxListAdapter(Context context, List<SmsInfo> datas) {
		super(context, datas);
	}

	@Override
	public View createView(int position, View view, ViewGroup parent) {
		final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.list_item_inbox, null);
            holder.itemText = ((TextView) view.findViewById(R.id.mms_list_item_text));
            holder.itemDate = (TextView) view.findViewById(R.id.mms_list_item_date);
            holder.itemUnReadImg = (ImageView) view.findViewById(R.id.mms_list_item_readStatus) ;
            
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        SmsInfo smsInfo = datas.get(position) ;
        holder.itemDate.setText(DateFormat.format(smsInfo.getDate()));
        holder.itemText.setText(smsInfo.getSmsbody()) ;
        if(smsInfo.getRead() == 1){
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
        
        return view;
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
