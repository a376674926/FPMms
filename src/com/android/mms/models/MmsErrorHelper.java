package com.android.mms.models;

import com.android.mms.R;
import android.R.integer;
import android.content.Context;
import android.widget.Switch;

import com.android.mms.app.Constants;

public class MmsErrorHelper {

	public static String getMessage(Context context, int errorCode) {
		switch (errorCode) {
		case Constants.ERRORCODE_NULL_RECIPIENT:
			return context.getResources().getString(R.string.null_recipient) ;
		case Constants.ERRORCODE_NULL_SMSBODY:
			return context.getResources().getString(R.string.null_smsbody) ;
		case Constants.DELETE_FAIL_CODE:
			return context.getResources().getString(R.string.toast_delete_fail) ;
		default:
			break;
		}
        return "" ;
    }
}
