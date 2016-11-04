package com.android.mms.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;

import com.android.mms.app.Constants;
import com.android.mms.entitis.SmsInfo;
import com.android.mms.utils.DateFormat;

/**
 * Mms业务处理实现类
 * 
 * @author jackey
 * 
 */
public class MmsModelImpl implements MmsModel {

	protected static final String TAG = MmsModelImpl.class.getSimpleName();
	private static final String[] SEND_PROJECTION = new String[] {
    	SmsInfo.ID, SmsInfo.ADDRESS, SmsInfo.BODY, SmsInfo.DATE, SmsInfo.TYPE, SmsInfo.READ
    };
	public static Object sendSync = new Object();
	public static Object saveSync = new Object();
	public static Object deleteSync = new Object();
	public static Object updateSync = new Object();
	private Context mContext;
	
	public MmsModelImpl(Context mContext) {
		super();
		this.mContext = mContext;
	}

	@Override
	public void sendMms(final SmsInfo smsInfo) {

		new Thread(new Runnable() {
			@Override
			public void run() {
				synchronized (sendSync) {
					ContentValues cv = new ContentValues();
                    cv.put(SmsInfo.ADDRESS, smsInfo.getPhoneNumber()==null?"":smsInfo.getPhoneNumber());
                    cv.put(SmsInfo.BODY, smsInfo.getSmsbody()==null?"":smsInfo.getSmsbody());
                    cv.put(SmsInfo.READ, smsInfo.getRead());
                    cv.put(SmsInfo.STATUS, smsInfo.getStatus());
					if(smsInfo.getType() == 0){
						Uri insertUri = insert(Uri.parse(Constants.SMSContentProviderMetaData.SMS_URI_OUTBOX),cv) ;
						if(insertUri != null){
							Cursor cursor = query(Uri.parse(Constants.SMSContentProviderMetaData.SMS_URI_OUTBOX),null,null,null,null) ;
							if(cursor != null && cursor.getCount() > 0){
								cursor.moveToFirst() ;
								smsInfo.setId(cursor.getInt(cursor.getColumnIndex(SmsInfo.ID))) ;
								smsInfo.setPhoneNumber(cursor.getString(cursor.getColumnIndex(SmsInfo.ADDRESS))) ;
								smsInfo.setSmsbody(cursor.getString(cursor.getColumnIndex(SmsInfo.BODY))) ;
								smsInfo.setDate(cursor.getLong(cursor.getColumnIndex(SmsInfo.DATE))) ;
								smsInfo.setType(cursor.getInt(cursor.getColumnIndex(SmsInfo.TYPE)));
							}
						}
					}else{
					    //草稿箱信息发送，已发信息转发，发件箱的短信重发，收件箱的短信转发，此时短信类型都要更改为发件箱类型
				        smsInfo.setType(SmsInfo.MESSAGE_TYPE_OUTBOX) ;
                        updateMms(Uri.parse(Constants.SMSContentProviderMetaData.SMS_URI_ALL), smsInfo, null) ;
					    
					}
					SmsManager smsManager = SmsManager.getDefault();
					Intent sendIntent = new Intent("SENT_SMS_ACTION") ;
					sendIntent.putExtra("body", smsInfo.getSmsbody()) ;
					sendIntent.putExtra(Constants.KEY_SMSINFO, smsInfo) ;
					Intent deliveryIntent = new Intent("DELIVERED_SMS_ACTION") ;
					deliveryIntent.putExtra(Constants.KEY_SMSINFO, smsInfo) ;
					// 短信发送成功或失败后会产生一条SENT_SMS_ACTION的广播
					PendingIntent sendPendingIntent = PendingIntent.getBroadcast(mContext,
							0, sendIntent, PendingIntent.FLAG_UPDATE_CURRENT);
					// 接收方成功收到短信后,发送方会产生一条DELIVERED_SMS_ACTION广播
					PendingIntent deliveryPendingIntent = PendingIntent.getBroadcast(
							mContext, 0, deliveryIntent,PendingIntent.FLAG_UPDATE_CURRENT);
					try {
						if (smsInfo.getSmsbody().length() > 70) { // 如果字数超过70,需拆分成多条短信发送
							List<String> msgs = smsManager.divideMessage(smsInfo
									.getSmsbody());
							for (String msg : msgs) {
								smsManager.sendTextMessage(smsInfo.getPhoneNumber(),
										null, msg, sendPendingIntent, deliveryPendingIntent);
							}
						} else {
							smsManager.sendTextMessage(smsInfo.getPhoneNumber(), null,
									smsInfo.getSmsbody(), sendPendingIntent, deliveryPendingIntent);
						}
					} catch (Exception ex) {
						Log.d(TAG, "sendFirstQueuedMessage: failed to send message");
						smsInfo.setType(SmsInfo.MESSAGE_TYPE_FAILED) ;
						updateMms(Uri.parse(Constants.SMSContentProviderMetaData.SMS_URI_ALL), smsInfo, null) ;
					}
				}
			}
		}).start();

	}

	@Override
	public void saveContacts(final String contactName, final String phoneNum) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				synchronized (saveSync) {
					ContentValues values = new ContentValues();
					Uri rawContactUri = mContext.getContentResolver().insert(
							RawContacts.CONTENT_URI, values);
					long rawContactId = ContentUris.parseId(rawContactUri);

					if (!TextUtils.isEmpty(contactName)) {
						values.clear();
						values.put(Data.RAW_CONTACT_ID, rawContactId);
						values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
						values.put(StructuredName.GIVEN_NAME, contactName);
						mContext.getContentResolver().insert(
								ContactsContract.Data.CONTENT_URI, values);
					}

					if (!TextUtils.isEmpty(phoneNum)) {
						values.clear();
						values.put(Data.RAW_CONTACT_ID, rawContactId);
						values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
						values.put(Phone.NUMBER, phoneNum);
						values.put(Phone.TYPE, Phone.TYPE_MOBILE);
						mContext.getContentResolver().insert(
								ContactsContract.Data.CONTENT_URI, values);
					}
				}
			}
		}).start();

	}

	@Override
	public void saveMms(final Uri uri, final SmsInfo smsInfo,
			final MmsModelListener listener) {

		new Thread(new Runnable() {
			@Override
			public void run() {
				synchronized (saveSync) {
				    ContentValues cv = new ContentValues();
			        cv.put(SmsInfo.ADDRESS, smsInfo.getPhoneNumber()==null?"":smsInfo.getPhoneNumber());
			        cv.put(SmsInfo.BODY, smsInfo.getSmsbody()==null?"":smsInfo.getSmsbody());
			        cv.put(SmsInfo.READ, smsInfo.getRead());
			        cv.put(SmsInfo.STATUS, smsInfo.getStatus());
					Uri inserUri = insert(uri,cv) ;
					if(inserUri != null && listener != null){
						Map<String, Object> responseMap = new HashMap<String, Object>();
						responseMap.put(Constants.KEY_SMS_SUCCESS_CODE,
								Constants.SMSContentProviderMetaData.SMS_URI_QUEUED.equals(uri.toString())?Constants.SAVE_QUEUEDSMS_SUCCESS:0);
						listener.onSuccess(responseMap);
					}
				}
			}
		}).start();

	}
	
	public Uri insert(Uri uri, ContentValues values){
		Uri uriInsert = mContext.getContentResolver().insert(uri, values);
		return uriInsert;
	}
	
	public Cursor query(Uri uri,String[] projection, String selection, String[] 
	        selectionArgs, String sortOrder){
	    if(projection == null){
	        projection = SEND_PROJECTION;
	    }
	    if(sortOrder == null){
	        sortOrder = SmsInfo.DEFAULT_SORT_ORDER;
	    }
		Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null, sortOrder) ;
		return cursor ;
	}

	@Override
	public void deleteMms(final Uri uri, final SmsInfo smsInfo,
			final MmsModelListener listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				synchronized (deleteSync) {
					String where = null;
					String[] selectionArgs = null;
					if (smsInfo != null) {
						where = SmsInfo.ID + "=?";
						selectionArgs = new String[] { smsInfo.getId() + "" };
					}
					int count = mContext.getContentResolver().delete(uri, where,
							selectionArgs);
					if (listener != null) {
						if (count > 0) {
							Map<String, Object> responseMap = new HashMap<String, Object>();
							responseMap.put(Constants.KEY_SMS_SUCCESS_CODE,
									Constants.DELETE_SMS_SUCCESS);
							listener.onSuccess(responseMap);
						} else {
							listener.onError(Constants.DELETE_FAIL_CODE,
									MmsErrorHelper.getMessage(mContext,
											Constants.DELETE_FAIL_CODE));
						}
					}
				}
			}
		}).start();
	}

	@Override
	public void deleteAllMms(Uri uri, MmsModelListener listener) {
		deleteMms(uri, null, listener);
	}

	@Override
	public void updateMms(final Uri uri, final SmsInfo smsInfo, final MmsModelListener listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				synchronized (updateSync) {
					String where = "_id=?";
					String[] selectionArgs = new String[] { smsInfo.getId() + "" };
					ContentValues cv = new ContentValues();
					cv.put(SmsInfo.ADDRESS, smsInfo.getPhoneNumber());
					cv.put(SmsInfo.BODY, smsInfo.getSmsbody());
					cv.put(SmsInfo.DATE, System.currentTimeMillis());
					cv.put(SmsInfo.READ, smsInfo.getRead());
					cv.put(SmsInfo.STATUS, smsInfo.getStatus());
					cv.put(SmsInfo.TYPE, smsInfo.getType()) ;
					int count = mContext.getContentResolver().update(uri, cv, where,
							selectionArgs);
					Log.d(TAG, " udpateMms-->>count:" + count) ;
					if (listener != null) {
						if (count > 0) {
							Map<String, Object> responseMap = new HashMap<String, Object>();
							responseMap.put(Constants.KEY_SMS_SUCCESS_CODE,
									Constants.UPDATE_SMS_SUCCESS);
							listener.onSuccess(responseMap);
						} else {
							listener.onError(Constants.DELETE_FAIL_CODE, MmsErrorHelper
									.getMessage(mContext, Constants.UPDATE_FAIL_CODE));
						}
					}
				}
				
			}
		}).start() ;
		
	}
	
	

}
