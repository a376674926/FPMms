package com.android.mms.models;

import java.net.URI;
import java.util.List;


import android.database.Cursor;
import android.net.Uri;

import com.android.mms.entitis.SmsInfo;

/**
 * mms业务处理接口类
 * @author jackey
 *
 */
public interface MmsModel {
    /**
     * 发短信
     * @param smsInfo
     */
	void sendMms(SmsInfo smsInfo) ;
	
	/**
	 * 保存联系人
	 * @param contactName 联系人姓名
	 * @param phoneNum 联系号码
	 */
	void saveContacts(String contactName, String phoneNum) ;
	
	/**
	 * 保存短信
	 * @param mAddressee 收信人
     * @param mMmsInfo 短信信息
     * @param listener 监听回调
	 */
	void saveMms(Uri uri, SmsInfo smsInfo, MmsModelListener listener) ;
	
	/**
	 * 删除短信
	 * @param smsInfo 短信
	 * @param listener 监听回调
	 */
	void deleteMms(Uri uri, SmsInfo smsInfo, MmsModelListener listener) ;
	
	/**
	 * 删除所有短信
	 * @param uri
	 * @param listener
	 */
	void deleteAllMms(Uri uri, MmsModelListener listener) ;

	/**
	 * 更改短信
	 * @param uri
	 * @param smsInfo
	 * @param listener
	 */
	void updateMms(Uri uri,SmsInfo smsInfo,MmsModelListener listener) ;
	
	
}
