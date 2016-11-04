package com.android.mms.services;

import static android.content.Intent.ACTION_BOOT_COMPLETED;
import static android.provider.Telephony.Sms.Intents.SMS_DELIVER_ACTION;
import static android.provider.Telephony.Sms.Intents.WAP_PUSH_RECEIVED_ACTION;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.Vibrator;
import android.provider.Telephony.Sms;
import android.provider.Telephony.Sms.Inbox;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import com.android.mms.R;
import com.android.mms.app.Constants;
import com.android.mms.entitis.SmsInfo;
import com.android.mms.models.MmsModel;
import com.android.mms.models.MmsModelImpl;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class SmsReceiverService extends Service {

    public static final String SMSINFO = "smsInfo";
    private static final String TAG = "SmsReceiverService";
    private MmsModelImpl mMmsModel ;
    private ServiceHandler mServiceHandler;
    private Looper mServiceLooper;
    private AlertDialog mAlertDialog = null;
    private Vibrator vibrator;
    
    @Override
    public void onCreate() {
        mMmsModel = new MmsModelImpl(this);
        HandlerThread thread = new HandlerThread(TAG, Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = intent;
        mServiceHandler.sendMessage(msg);
        return Service.START_NOT_STICKY;
    }

    private void handleSmsReceived(Intent intent, int error) {
        SmsMessage[] msgs = getMessagesFromIntent(intent);
        // SPRD: Add for multi-sim sms send&receive.
//           int subId = intent.getIntExtra(PhoneConstants.SUBSCRIPTION_KEY, SubscriptionManager.getDefaultSmsSubId());
        int subId = 0 ;
        String format = intent.getStringExtra("format");
        insertMessage(this, msgs, error, format, subId);
    }
    
    private Uri insertMessage(SmsReceiverService smsReceiverService, SmsMessage[] msgs, int error,
            String format, int subId) {
        SmsMessage sms = msgs[0];

        // Store the message in the content provider.
        ContentValues values = extractContentValues(sms);
        values.put(Sms.ERROR_CODE, error);
        int pduCount = msgs.length;

        if (pduCount == 1) {
            // There is only one part, so grab the body directly.
            values.put(Inbox.BODY, replaceFormFeeds(sms.getDisplayMessageBody()));
        } else {
              /*Modify by SPRD for Bug:524572  2016.01.19 Start */
//            // Build up the body from the parts.
//            StringBuilder body = new StringBuilder();
//            for (int i = 0; i < pduCount; i++) {
//                sms = msgs[i];
//                if (sms.mWrappedSmsMessage != null) {
//                    body.append(sms.getDisplayMessageBody());
//                }
//            }
//            values.put(Inbox.BODY, replaceFormFeeds(body.toString()));
              /*if(sms.getEncodeType()==3){
                  int bodylen = 0;
                      for (int i = 0; i < pduCount; i++) {
                          sms = msgs[i];
                          if (sms.mWrappedSmsMessage != null) {
                              bodylen += sms.getUserData().length;
                              //body.append(sms.getDisplayMessageBody());
                          }
                      }
                  byte[] bodybytes = new byte[bodylen];
                  int curlen = 0;
                  for (int i = 0; i < pduCount; i++) {
                      sms = msgs[i];
                      if (sms.mWrappedSmsMessage != null) {
                          System.arraycopy(sms.getUserData(), 0, bodybytes, curlen, sms.getUserData().length);
                      }
                          curlen += sms.getUserData().length;
                  }

                   try {
                       String bodystr = new String(bodybytes, "utf-16");
                       values.put(Inbox.BODY, replaceFormFeeds(bodystr));
                   } catch (UnsupportedEncodingException ex) {
                       Log.d("xzy", "implausible UnsupportedEncodingException", ex);
                   }
             }else{*/
             // Build up the body from the parts.
                 StringBuilder body = new StringBuilder();
                     for (int i = 0; i < pduCount; i++) {
                         sms = msgs[i];
                         body.append(sms.getDisplayMessageBody());
                      }
                 values.put(Inbox.BODY, replaceFormFeeds(body.toString()));
             }
//         }
        /*Modify by SPRD for Bug:524572  2016.01.19 End */

        // Make sure we've got a thread id so after the insert we'll be able to delete
        // excess messages.
        Long threadId = values.getAsLong(Sms.THREAD_ID);
        String address = values.getAsString(Sms.ADDRESS);

        // Code for debugging and easy injection of short codes, non email addresses, etc.
        // See Contact.isAlphaNumber() for further comments and results.
//        switch (count++ % 8) {
//            case 0: address = "AB12"; break;
//            case 1: address = "12"; break;
//            case 2: address = "Jello123"; break;
//            case 3: address = "T-Mobile"; break;
//            case 4: address = "Mobile1"; break;
//            case 5: address = "Dogs77"; break;
//            case 6: address = "****1"; break;
//            case 7: address = "#4#5#6#"; break;
//        }

        if (!TextUtils.isEmpty(address)) {
            /*Contact cacheContact = ContactCache.get(address,true);
            if (cacheContact != null) {
                address = cacheContact.getNumber();
            }*/
        } else {
            address = getString(R.string.unknown_sender);
            values.put(Sms.ADDRESS, address);
        }
        /*SPRD: Bug 340793, Comment this code,because already done in SmsProvider.
         *@orig
         *
        if (((threadId == null) || (threadId == 0)) && (address != null)) {
            threadId = Conversation.getOrCreateThreadId(context, address);
            values.put(Sms.THREAD_ID, threadId);
        }
        /*@}*/

//        values.put(Sms.SUBSCRIPTION_ID, subId);
        // SPRD: tel-mms task

//        if (MessageUtils.UNIVERSEUI_SUPPORT) {
//            try {
//                String iccId = null;
//                if (simManager != null) {
//                    iccId = simManager.getIccId(phoneId);
//                    if (iccId != null && !iccId.isEmpty()) {
//                        values.put(Sms.ICC_ID, iccId);
//                    }
//                }
//            } catch (Exception e) {
//                Log.i(TAG, "RunTimeException:" + e.getMessage());
//
//            }
//        }

        Uri insertedUri = mMmsModel.insert(Uri.parse(Constants.SMSContentProviderMetaData.SMS_URI_INBOX), values) ;
       
        return insertedUri;
    }

    private int getUnreadMessageCount(){
    	Cursor cursor = null;
    	int count = 0;
    	try{
    	cursor = mMmsModel.query(Uri.parse(Constants.SMSContentProviderMetaData.SMS_URI_INBOX),  
    			new String[] { SmsInfo.ID },  SmsInfo.READ + "=?", new String[]{"0"}, null);
    	count = cursor == null ? 0 : cursor.getCount();
    	}finally{
    		if(cursor != null){
    			cursor.close();
    		}
    	}
    	return count;
    }
    private void showNewMessageDialog(){
    	int unread = getUnreadMessageCount();
    	if(unread >0){
    		startVibrator();
    		if(mAlertDialog == null){
    			mAlertDialog = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT).create();
    			mAlertDialog.setCanceledOnTouchOutside(true);
    			mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
    		}
    		if(mAlertDialog.isShowing()){
    			mAlertDialog.dismiss();
    		}
    		mAlertDialog.setMessage(unread + getString(R.string.count_unread_messages));
    		mAlertDialog.show();
    	}
    }
    /**
     * Õð¶¯
     */
    private void startVibrator() {
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            long[] pattern = { 500, 300, 500, 300 }; // Í£Ö¹ ¿ªÆô Í£Ö¹ ¿ªÆô
            vibrator.vibrate(pattern, -1);
    }
    private SmsMessage[] getMessagesFromIntent(Intent intent) {
        Object[] messages = (Object[]) intent
                .getSerializableExtra("pdus");
        String format = intent.getStringExtra("format");
        // SPRD: Add for OTA.
//        int subId = intent.getIntExtra(PhoneConstants.SUBSCRIPTION_KEY, 0);
        byte[][] pduObjs = new byte[messages.length][];

        for (int i = 0; i < messages.length; i++) {
            pduObjs[i] = (byte[]) messages[i];
        }
        byte[][] pdus = new byte[pduObjs.length][];
        int pduCount = pdus.length;
        SmsMessage[] msgs = new SmsMessage[pduCount];
        for (int i = 0; i < pduCount; i++) {
            pdus[i] = pduObjs[i];
            // msgs[i] = SmsMessage.createFromPdu(pdus[i], format);
            msgs[i] = SmsMessage.createFromPdu(pdus[i]);
//            msgs[i].setSubId(subId);
        }
        return msgs;
    }
    
    /**
     * Extract all the content values except the body from an SMS
     * message.
     */
    private ContentValues extractContentValues(SmsMessage sms) {
        // Store the message in the content provider.
        ContentValues values = new ContentValues();

        values.put(Inbox.ADDRESS, sms.getDisplayOriginatingAddress());

        // Use now for the timestamp to avoid confusion with clock
        // drift between the handset and the SMSC.
        // Check to make sure the system is giving us a non-bogus time.
        Calendar buildDate = new GregorianCalendar(2011, 8, 18);    // 18 Sep 2011
        Calendar nowDate = new GregorianCalendar();
        long now = System.currentTimeMillis();
        nowDate.setTimeInMillis(now);

        if (nowDate.before(buildDate)) {
            // It looks like our system clock isn't set yet because the current time right now
            // is before an arbitrary time we made this build. Instead of inserting a bogus
            // receive time in this case, use the timestamp of when the message was sent.
            now = sms.getTimestampMillis();
        }

        values.put(Inbox.DATE, new Long(now));
        values.put(Inbox.DATE_SENT, Long.valueOf(sms.getTimestampMillis()));
        values.put(Inbox.PROTOCOL, sms.getProtocolIdentifier());
        values.put(Inbox.READ, 0);
        values.put(Inbox.SEEN, 0);
        if (sms.getPseudoSubject().length() > 0) {
            values.put(Inbox.SUBJECT, sms.getPseudoSubject());
        }
        values.put(Inbox.REPLY_PATH_PRESENT, sms.isReplyPathPresent() ? 1 : 0);
        values.put(Inbox.SERVICE_CENTER, sms.getServiceCenterAddress());
        return values;
    }
    
    public static String replaceFormFeeds(String s) {
        // Some providers send formfeeds in their messages. Convert those formfeeds to newlines.
        return s == null ? "" : s.replace('\f', '\n');
    }
    
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        /**
         * Handle incoming transaction requests.
         * The incoming requests are initiated by the MMSC Server or by the MMS Client itself.
         */
        @Override
        public void handleMessage(Message msg) {
            int serviceId = msg.arg1;
            Intent intent = (Intent)msg.obj;
            
                String action = intent.getAction();

                int error = intent.getIntExtra("errorCode", 0);
                if (SMS_DELIVER_ACTION.equals(action)
                		|| "android.provider.Telephony.SMS_RECEIVED".equals(action)) {
                    handleSmsReceived(intent, error);
                    showNewMessageDialog();
                }
        }

    }

}
