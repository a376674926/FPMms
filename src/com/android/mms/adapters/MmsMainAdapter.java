package com.android.mms.adapters;

import java.util.List;

import com.android.mms.R;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MmsMainAdapter extends MBaseAdapter<String> {

	public MmsMainAdapter(Context context, List<String> datas) {
		super(context, datas);
	}

	@Override
	public View createView(int position, View view, ViewGroup parent) {
		ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.list_item_mms_main, null);
            holder.itemIndex = ((TextView) view.findViewById(R.id.mms_main_list_index));
            holder.itemText = (TextView) view.findViewById(R.id.mms_main_list_item_text);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.itemIndex.setText(String.valueOf(position + 1));
        holder.itemText.setText(datas.get(position));
        return view;
	}
	
	private class ViewHolder {
        TextView itemIndex;
        TextView itemText;
    }

}
