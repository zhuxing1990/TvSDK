package com.vunke.tv_sharehome;

import android.content.Intent;

public final class Config {
	private Config() {
	}
	
	public static final String MOTIFY_TIME = "2016-10-13";

//	public static final String SERVICE_URI = "http://192.168.28.226:80/ShareHome";//me
	//public static final String SERVICE_URI = "http://134.175.18.53:8080/ShareHome2";//test
	 public static final String SERVICE_URI = "http://124.232.135.222:8080/ShareHome2";//正式

	public static final String KEY = "2:2";// 加密key

	/**
	 * 网络请求,同步联系人
	 */
	public static final int ASYN_CONTACT = 9900;
	public static final int INTENT_LOGIN_VALUE = 9901;
	public static final String INTENT_LOGIN_KEY = "intent_login";
	/**区号**/
	public static final String countryCode = "+86";
	public static final String ACCOUNT_BEFORE = "+8611831726";
	public static final String CALL_BEFORE = "11831726";

//	public static final String SIP = "222.246.189.244";// 手机
	public static final String SIP = "10.255.31.235";//tv
	public static final String SPORT = "443";
	public static final String DATABASE_TABLE_NAME = "whiteContanct";
	public static final String TABLE_COLUMN_USERNAME = "username";
	public static final String TABLE_COLUMN_MOBLIE = "moblie";
	public static final String TABLE_COLUMN_DATE = "date";
	public static final String SP_NAME = "config";
	public static final String LOGIN_PASSWORD = "login_password";
	public static final String LOGIN_USER_NAME = "login_username";
	public static final String STB_A40 = "STB_A40";// 中兴
	public static final String EIGHT = "8";
	public static final String NINE = "9";
	/** 热插拔广播 */
	public static final String ACTION_USB_CAMERA_PLUG_IN_OUT = "android.hardware.usb.action.USB_CAMERA_PLUG_IN_OUT";
	/** A40获取热插拔状态 */
	public static final String USB_CAMERA_STATE = "UsbCameraState";
	public static int net_connect_true = 0;
	public static int login_count = 0;
	public static Intent intent = null;
	// public static int LOGIN_ACTIVITY = 0;//1表示loginActivity存在，0表示已销毁

	public static final String GetkeyURL = "/sendMsg/NoPasslogin.do";

	/**
	 * 注册还是修改密码类型
	 */
	public static final String KEY_VALIDATE = "key_validate";

	/**
	 * Rxbus
	 */
	public static final int RXBUS_REFRESH_LOCALVIEW = 100001;
	public static final int RXBUS_REGISTER_SUCCESS = 100002;
	/**
	 * 通话类型
	 * 
	 */
	public static final String CALLRECORDER_TYPE_MISSED = "0";
	public static final String CALLRECORDER_TYPE_RECEIVED = "1";
	public static final String CALLRECORDER_TYPE_DIAL = "2";

	public static final String FIRST_ENTRY_APPLICATION = "first_entry_application";
	
	/**
	 * 红包活动是否进行标识 1进行中0活动结束
	 */
	public static  int LUCKY_ACTIVITY = 0;
	
	public static String GET_LUCKY_URL = "";

	public static String LIST_LUCKY_URL = "";
	/**
	 * 活动名称
	 */
	public static String ACTIVITY_NAME = "";
}
