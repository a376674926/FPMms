package com.android.mms.broadcastreceiver;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.PowerManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.android.mms.entitis.SmsInfo;
import com.android.mms.services.SmsReceiverService;


public class SmsReceiver extends BroadcastReceiver {
    
    private static final String TAG = "SmsReceiver";
    static final Object mStartingServiceSync = new Object();
    static PowerManager.WakeLock mStartingService;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "-------------->>onReceive()");
        /*Bundle bundle = intent.getExtras();
        Object[] pdus = (Object[]) bundle.get("pdu");
        SmsMessage[] smsMessages = new SmsMessage[pdus.length];
        for (int i = 0; i < smsMessages.length; i++) {
            smsMessages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
        }
        String smsAddress = smsMessages[0].getOriginatingAddress();
        Log.d(TAG, "-------------->>onReceive() smsAddress:" + smsAddress);
        long smsDateTime = smsMessages[0].getTimestampMillis();
        String smsBody = "" ;
        for(SmsMessage smsMessage:smsMessages){
            smsBody += smsMessage.getMessageBody() ;
        }
        
        SmsInfo smsInfo = new SmsInfo();
        smsInfo.setPhoneNumber(smsAddress);
        smsInfo.setSmsbody(smsBody);
        smsInfo.setDate(smsDateTime);
        Log.d(TAG, " smsInfo: " + smsInfo.toString());*/
        intent.setClass(context, SmsReceiverService.class);
        intent.putExtra("result", getResultCode());
        beginStartingService(context, intent);
    }
    
    public static void beginStartingService(Context context, Intent intent) {
        synchronized (mStartingServiceSync) {
            if (mStartingService == null) {
                PowerManager pm =
                    (PowerManager)context.getSystemService(Context.POWER_SERVICE);
                mStartingService = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
                        | PowerManager.SCREEN_DIM_WAKE_LOCK,
                        "StartingAlertService");
            }
            mStartingService.acquire(3000);
            context.startService(intent);
        }
    }
    

}
