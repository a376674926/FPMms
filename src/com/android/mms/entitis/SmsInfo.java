package com.android.mms.entitis;

import java.sql.Date;

import android.os.Parcel;
import android.os.Parcelable;

public class SmsInfo implements Parcelable{
	
	public static final String ID = "_id" ;
	public static final String THREAD_ID = "thread_id" ;
	public static final String ADDRESS = "address" ;
	public static final String DATE = "date" ;
	public static final String BODY = "body" ;
	public static final String SUBJECT = "subject" ;
	public static final String STATUS = "status" ;
	public static final String TYPE = "type" ;
	public static final String READ = "read" ;
	public static final String DEFAULT_SORT_ORDER = "date DESC";
	public static final int MESSAGE_UNREAD = 0 ;
	public static final int MESSAGE_READ = 1 ;
	public static final int MESSAGE_TYPE_ALL = 0 ;
	public static final int MESSAGE_TYPE_INBOX  = 1;
	public static final int MESSAGE_TYPE_SENT   = 2;
	public static final int MESSAGE_TYPE_DRAFT  = 3;
	public static final int MESSAGE_TYPE_OUTBOX = 4;
	public static final int MESSAGE_TYPE_FAILED = 5; // for failed outgoing messages 
	public static final int MESSAGE_TYPE_QUEUED = 6; // for messages to send later
	
	//短信序号
	private long id ;
	
	//对话序号
	private long thread_id ;
	
	//发件人手机号码
	private String phoneNumber ;
	
	//发件人，如果发件人在通讯录中则为具体姓名，陌生人为null
	private String person ;
	
	//发件日期
	private long date ;
	
	//read：是否阅读0未读，1已读 
	private int read ;
	
	//短信状态 -1 接收，0 complete,64 pending,128 failed
	private int status ;
	
	//类型 ALL = 0;INBOX = 1;SENT = 2;DRAFT = 3;OUTBOX = 4;FAILED = 5;QUEUED = 6;
	private int type ;
	
	//短信内容
	private String smsbody ;
	
	//短信主题
	private String subject ;


	public SmsInfo() {
		super();
	}


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public long getThread_id() {
		return thread_id;
	}


	public void setThread_id(long thread_id) {
		this.thread_id = thread_id;
	}


	public String getPerson() {
		return person;
	}


	public void setPerson(String person) {
		this.person = person;
	}


	public String getSmsbody() {
		return smsbody;
	}

	public void setSmsbody(String smsbody) {
		this.smsbody = smsbody;
	}

	public long getDate() {
		return date;
	}


	public void setDate(long date) {
		this.date = date;
	}

	public int getRead() {
		return read;
	}

	public void setRead(int read) {
		this.read = read;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	

	@Override
	public String toString() {
		return "SmsInfo [_id=" + id + ", thread_id=" + thread_id + ", phoneNumber=" + phoneNumber
				+ ", name=" + person + ", date=" + date + ", read=" + read
				+ ", status=" + status + ", type=" + type + ", smsbody="
				+ smsbody + ", subject=" + subject + "]";
	}

	public SmsInfo(Parcel in){
		this.id = in.readLong() ;
        this.phoneNumber = in.readString();
        this.thread_id = in.readLong() ;
        this.person = in.readString();
        this.date = in.readLong();
        this.read = in.readInt();
        this.status = in.readInt();
        this.type = in.readInt();
        this.smsbody = in.readString();
        this.subject = in.readString() ;
    }

    public static Parcelable.Creator<SmsInfo> CREATOR = new Creator<SmsInfo>() {
        @Override
        public SmsInfo createFromParcel(Parcel source) {
            return new SmsInfo(source);
        }

        @Override
        public SmsInfo[] newArray(int size) {
            return new SmsInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    	dest.writeLong(id) ;
        dest.writeString(phoneNumber);
        dest.writeLong(thread_id) ;
        dest.writeString(person);
        dest.writeLong(date);
        dest.writeInt(read);
        dest.writeInt(status);
        dest.writeInt(type);
        dest.writeString(smsbody);
        dest.writeString(subject);
    }
	
	
}
