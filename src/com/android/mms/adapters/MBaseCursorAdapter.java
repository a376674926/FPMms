package com.android.mms.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class MBaseCursorAdapter extends CursorAdapter {
	
	protected Cursor mMmsCursor ;
	protected Context mContext ;
	protected LayoutInflater mInflater ;

	public MBaseCursorAdapter(Context context, Cursor c) {
		super(context, c);
		this.mMmsCursor = c ;
		mContext = context;
        mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent) {
        if (!mDataValid) {
            throw new IllegalStateException( "this should only be called when the cursor is valid");
        }
        if (!mMmsCursor.moveToPosition(position)) {
            throw new IllegalStateException( "couldn't move cursor to position " + position);
        }
        View v;
        if (convertView == null) {
            v = newView( mContext, mMmsCursor, parent);
        } else {
            v = convertView;
        }
        bindView(v, mContext, mMmsCursor,position);
        return v;
    }
	
	public void changeCursor (Cursor cursor) {
		mMmsCursor = cursor ;
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }
	public void bindView(View view, Context context, Cursor cursor) {} ;
	
	public abstract View newView(Context context, Cursor cursor, ViewGroup viewGroup);
	
	public abstract void bindView(View view, Context context, Cursor cursor, int position);
	
}
