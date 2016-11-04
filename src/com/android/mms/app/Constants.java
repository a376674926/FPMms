package com.android.mms.app;

/**
 * app全局常量类
 * @author jackey
 *
 */
public class Constants {
     //短信
	 public static final String KEY_SMSINFO = "key_smsinfo";
	 public static final String KEY_SMSINFOS = "key_smsinfos";
	 public static final String KEY_SEND_MMS = "key_send_mms";
	 public static final String KEY_DELETE_MMS = "key_delete_mms";
	 public static final String KEY_DELETE_ALL_MMS = "key_delete_all_mms";
	 public static final String KEY_DELETE_RESULT = "key_delete_result" ;
	 public static final String KEY_SMS_TYPE = "key_sms_type" ;
	 public static final String KEY_SMS_VIEW = "key_sms_view" ;
	 public static final String KEY_SMS_SUCCESS_CODE = "key_sms_success_code" ;
	
	 //联系人
	 public static final String KEY_CONTACT_NAME = "key_contact_name";
	 //联系人号码
	 public static final String KEY_CONTACT_NUMBER = "key_contact_number";
	 
	 public static final String ERROR_CODE = "error_code" ;
	 public static final String ERROR_MSG = "error_msg" ;
	 public static final String OPTION = "option" ;
	 
     //无效联系人
	 public static final int ERRORCODE_NULL_RECIPIENT = 101 ;
	 //短信内容为空
	 public static final int ERRORCODE_NULL_SMSBODY = 102 ;
	 //短信删除处理失败 
	 public static final int DELETE_FAIL_CODE = 103 ;
	 //更新短信失败
	 public static final int UPDATE_FAIL_CODE = 104 ;
	 //加载短信成功
	 public static final int LOAD_SMS_SUCCESS = 0 ;
	 //删除短信成功
	 public static final int DELETE_SMS_SUCCESS = 1 ;
	 //删除所有短信成功
	 public static final int DELETE_ALL_SMS_SUCCESS = 2 ;
	 //更新短信成功
	 public static final int UPDATE_SMS_SUCCESS = 3 ;
	 //保存发件箱短信成功
	 public static final int SAVE_OUTBOXSMS_SUCCESS = 4 ;
	 //保存待发送短信
	 public static final int SAVE_QUEUEDSMS_SUCCESS = 5 ;
	 
	 public static class SMSContentProviderMetaData {
		 /**
		  * 所有的短信
		  */
		 public static final String SMS_URI_ALL = "content://sms/";
		 
		 /**
		  * 收件箱短信
		  */
		 public static final String SMS_URI_INBOX = "content://sms/inbox";
		 
		 /**
		  * 已发送短信
		  */
		 public static final String SMS_URI_SENT = "content://sms/sent";
		 
		 /**
		  * 草稿箱短信
		  */
		 public static final String SMS_URI_DRAFT = "content://sms/draft";
		 
		 /**
		  * 发件箱短信
		  */
		 public static final String SMS_URI_OUTBOX = "content://sms/outbox";
		 
		 /**
		  * 发送失败
		  */
		 public static final String SMS_URI_FAILED = "content://sms/failed" ; 
		 
		 /**
		  * 待发送列表
		  */
		 public static final String SMS_URI_QUEUED = "content://sms/queued" ; 
		 
		 /**
		  * 草稿箱短信
		  */
		 public static final String SMS_ALL_ID = "content://sms/#";
		 
		 
	 }
	 
	 
}
