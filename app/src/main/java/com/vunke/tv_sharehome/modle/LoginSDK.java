package com.vunke.tv_sharehome.modle;

public class LoginSDK {

	/**
	 * 登录服务启动的 ACTION
	 */
	public static final String LOGIN_ACTION = "com.vunke.sharehome2.login_action";
	/**
	 * 登录测试服务ACTION
	 */
	public static final String LOGIN_TEST_ACTION = "com.vunke.sharehome2.login_test_action";

	/**
	 * 广播的 IntentFilter
	 */
	public static final String LOGIN_STATUS_CHAGED = "com.vunke.sharehome2.login_status_chaged";

	private int LOGIN_STATUS;
	private String LOGIN_RESON;
	/**
	 * 登录失败
	 */
	public static final int LOGIN_ERROR = 1;
	/**
	 * 登录成功
	 */
	public static final int LOGIN_SUCCESS = 2;
	/**
	 * 正在登录中
	 */
	public static final int LOGIN_LOADING = 3;
	/**
	 * 手机号错误
	 */
	public static final String RESON_MOBILE_IS_NULL = "mobile_is_null ";
	/**
	 * 渠道ID错误
	 */
	public static final String RESON_CHANNEL_ID_IS_NULL = "channel_id_is_null ";
	/**
	 * 签名错误
	 */
	public static final String RESON_SIGN_IS_ERROR = "sign_is_error";
	/**
	 * 本地时间错误
	 */
	public static final String RESON_WRONG_LOCAL_TIME = "wrong_local_time ";
	/**
	 * 重复登录
	 */
	public static final String RESON_REPEAT_LOGIN = "repeat_login ";
	/**
	 * 获取密码失败
	 */
	public static final String RESON_MOBILE_AUTH_ERROR = "mobile_auth_error ";

	public int getLOGIN_STATUS() {
		return LOGIN_STATUS;
	}

	public void setLOGIN_STATUS(int lOGIN_STATUS) {
		LOGIN_STATUS = lOGIN_STATUS;
	}

	public String getLOGIN_RESON() {
		return LOGIN_RESON;
	}

	public void setLOGIN_RESON(String lOGIN_RESON) {
		LOGIN_RESON = lOGIN_RESON;
	}

	/**
	 * 手机号码
	 */
	private String mobile_no;
	/**
	 * 渠道ID
	 */
	private long channel_Id;
	/**
	 * 时间戳
	 */
	private long timestamp;
	/**
	 * 签名
	 */
	private String sign;
	/**
	 * 密码
	 */
	private String passWord;
	/**
	 * 判断登录状态
	 */
	private boolean isLogin = false;

	/**
	 * 获取密码状态
	 */
	private boolean getpassword = false;

	/**
	 * 密钥
	 */
	public static String key = "sharehome_bestv";

	/**
	 * 被叫方
	 * 
	 */
	private String calledParty;

	/**
	 * 被叫类型
	 */
	private String calledType;

	public String getCalledParty() {
		return calledParty;
	}

	public void setCalledParty(String calledParty) {
		this.calledParty = calledParty;
	}

	public String getCalledType() {
		return calledType;
	}

	public void setCalledType(String calledType) {
		this.calledType = calledType;
	}

	public String getMobile_no() {
		return mobile_no;
	}

	public void setMobile_no(String mobile_no) {
		this.mobile_no = mobile_no;
	}

	public long getChannel_Id() {
		return channel_Id;
	}

	public void setChannel_Id(long channel_Id) {
		this.channel_Id = channel_Id;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public boolean isLogin() {
		return isLogin;
	}

	public void setLogin(boolean isLogin) {
		this.isLogin = isLogin;
	}

	public boolean isGetpassword() {
		return getpassword;
	}

	public void setGetpassword(boolean getpassword) {
		this.getpassword = getpassword;
	}

}