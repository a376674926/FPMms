package com.android.mms.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.android.mms.entitis.SmsInfo;
import com.android.mms.services.SmsReceiverService;


public class MmsReceiver extends BroadcastReceiver {
    
    private static final String TAG = "MmsReceiver";
    static final Object mStartingServiceSync = new Object();
    static PowerManager.WakeLock mStartingService;

    @Override
    public void onReceive(Context context, Intent intent) {
        
    }
    

}
