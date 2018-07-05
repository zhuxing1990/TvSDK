package com.vunke.tv_sharehome.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.huawei.rcs.log.LogApi;
import com.huawei.rcs.login.LoginApi;
import com.huawei.rcs.login.LoginCfg;
import com.huawei.rcs.login.UserInfo;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.vunke.tv_sharehome.Config;
import com.vunke.tv_sharehome.modle.LoginSDK;
import com.vunke.tv_sharehome.utils.Encrypt3DES;
import com.vunke.tv_sharehome.utils.MD5Util;
import com.vunke.tv_sharehome.utils.SPUtils;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 第三方 登录 服务
 * 
 * @author zhuxi
 * 
 */
public class LoginService extends Service {

    /**
	 * 登录的SDK
	 */
	private LoginSDK login_sdk = new LoginSDK();

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e("LoginService", "LoginService---onStartCommand");
			if(intent != null){
				String action = intent.getAction();
				initService(intent, action);
			}
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 初始化登录
	 * 
	 * @param intent
	 * @param action
	 */
	private void initService(Intent intent, String action) {
		if (action.equals(login_sdk.LOGIN_ACTION)) {
			login_sdk = new LoginSDK();
			String mobile_no = intent.getStringExtra("mobile_no");
			if (TextUtils.isEmpty(mobile_no)) {
				login_sdk.setLOGIN_RESON(LoginSDK.RESON_MOBILE_IS_NULL);
				login_sdk.setLOGIN_STATUS(LoginSDK.LOGIN_ERROR);
				SendMessage();
				Log.e("LoginService", "获取帐号失败");
				return;
			}
			login_sdk.setMobile_no(mobile_no);
			long channel_Id = intent.getLongExtra("channel_Id", -1);
			if (channel_Id == -1) {
				login_sdk.setLOGIN_RESON(LoginSDK.RESON_CHANNEL_ID_IS_NULL);
				login_sdk.setLOGIN_STATUS(LoginSDK.LOGIN_ERROR);
				SendMessage();
				Log.e("LoginService", "获取渠道ID失败");
				return;
			}
			login_sdk.setChannel_Id(channel_Id);
			long timestamp = intent.getLongExtra("timestamp", -1);
			if (timestamp == -1) {
				login_sdk.setLOGIN_RESON(LoginSDK.RESON_WRONG_LOCAL_TIME);
				login_sdk.setLOGIN_STATUS(LoginSDK.LOGIN_ERROR);
				SendMessage();
				Log.e("LoginService", "获取登录时间失败");
				return;
			}
			login_sdk.setTimestamp(timestamp);
			String sign = intent.getStringExtra("sign");
			if (TextUtils.isEmpty(sign)) {
				login_sdk.setLOGIN_RESON(LoginSDK.RESON_SIGN_IS_ERROR);
				login_sdk.setLOGIN_STATUS(LoginSDK.LOGIN_ERROR);
				SendMessage();
				Log.e("LoginService", "获取签名失败");
				return;
			}
			login_sdk.setSign(sign);
			String calledParty = intent.getStringExtra("calledParty");
			if (!TextUtils.isEmpty(calledParty)) {
				login_sdk.setCalledParty(calledParty);
			}
			String calledType = intent.getStringExtra("calledType");
			if (!TextUtils.isEmpty(calledType)) {
				login_sdk.setCalledType(calledType);
			}
			initLogin();
		} else if (action.equals(login_sdk.LOGIN_TEST_ACTION)) {
			Log.e("LoginService", "测试登录");
			login_sdk = new LoginSDK();
			login_sdk.setMobile_no("13278875981");
			login_sdk.setChannel_Id(20180701);
			login_sdk.setTimestamp(System.currentTimeMillis());
			login_sdk.setCalledParty("13278875981");
			login_sdk.setCalledType("8");
			String str = LoginSDK.key + login_sdk.getChannel_Id()
					+ login_sdk.getMobile_no() + login_sdk.getTimestamp();
			login_sdk.setSign(MD5Util.encryptByMD5(str, login_sdk.key));
			initLogin();
		}
	}

	/**
	 * 发送 登录状态的 广播 
	 */
	private void SendMessage() {
		Config.intent = new Intent(login_sdk.LOGIN_STATUS_CHAGED);
		Config.intent.putExtra("login_status", login_sdk.getLOGIN_STATUS());
		Config.intent.putExtra("calledParty", login_sdk.getCalledParty());
		Config.intent.putExtra("calledType", login_sdk.getCalledType());
		if (!TextUtils.isEmpty(login_sdk.getLOGIN_RESON())) {
			Config.intent.putExtra("login_reson", login_sdk.getLOGIN_RESON());
		}
		sendBroadcast(Config.intent);
	}

	/**
	 * 加密解析
	 */
	private void initLogin() {
		try {
			String str = LoginSDK.key + login_sdk.getChannel_Id()
					+ login_sdk.getMobile_no() + login_sdk.getTimestamp();
			Log.e("LoginService","正在验证签名信息");
			String md5 = MD5Util.encryptByMD5(str, login_sdk.key);
			if (md5.equals(login_sdk.getSign())) {
				login_sdk.setLOGIN_STATUS(LoginSDK.LOGIN_LOADING);
				SendMessage();
				StartGetPassword();
				Log.e("LoginService", "验证签名信息成功，开始登录");
			} else {
				login_sdk.setLOGIN_RESON(LoginSDK.RESON_SIGN_IS_ERROR);
				login_sdk.setLOGIN_STATUS(LoginSDK.LOGIN_ERROR);
				SendMessage();
				Log.e("LoginService", "验证签名信息失败");
			}
		} catch (Exception e) {
//			e.printStackTrace();
			login_sdk.setLOGIN_RESON(LoginSDK.RESON_SIGN_IS_ERROR);
			login_sdk.setLOGIN_STATUS(LoginSDK.LOGIN_ERROR);
			SendMessage();
			Log.e("LoginService", "验证签名信息出现异常");
		}

	}

	private void StartGetPassword() {
		if (login_sdk.isLogin() == true) {
			login_sdk.setLOGIN_RESON(LoginSDK.RESON_REPEAT_LOGIN);
			login_sdk.setLOGIN_STATUS(LoginSDK.LOGIN_ERROR);
			SendMessage();
			Log.e("LoginService", "正在登录中，请不要重复请求");
			return;
		}
		Observable.interval(0, 1, TimeUnit.SECONDS)
				.filter(new Func1<Long, Boolean>() {
					@Override
					public Boolean call(Long aLong) {
						login_sdk.setLogin(true);
						return aLong <= 30;
					}
				}).map(new Func1<Long, Long>() {
					@Override
					public Long call(Long aLong) {
						return -(aLong - 30);
					}
				}).subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<Long>() {
					@Override
					public void onCompleted() {

					}

					@Override
					public void onError(Throwable e) {
						this.unsubscribe();
						login_sdk.setLogin(false);
					}

					@Override
					public void onNext(Long aLong) {
						if (aLong != 0) {
							// WorkLog.i("LoginService", "请等待" + aLong + "秒");
						} else {
							this.unsubscribe();
							login_sdk.setLogin(false);
						}
					}
				});
		getPassword();
	}

	/**
	 * 获取密码信息
	 */
	private void getPassword() {
		if (TextUtils.isEmpty(login_sdk.getMobile_no())) {
			// showToast("帐号不能为空");
			return;
		}
		JSONObject json = new JSONObject();
		try {
			json.put("username", login_sdk.getMobile_no());
			json.put("userType", "8");
			Log.e("LoginService", "request json:"+json.toString());

		OkHttpUtils.post(Config.SERVICE_URI + Config.GetkeyURL).tag(this)
				.params("json", json.toString()).execute(new StringCallback() {

							@Override
						public void onResponse(String t) {
						Log.e("LoginService", "get request" + t);
						// {"code":"200","encodePass":"a0fbcd3c2094361ae476f4b982a6d987","firstLogin":"1","message":"success"}
						try {
							JSONObject js = new JSONObject(t);
							int code = js.getInt("code");
							switch (code) {
							case 200:
								Log.e("LoginService", "reueset success:");
								String encodePass = js.getString("encodePass");
//								String firstLogin = js.getString("firstLogin");
								login_sdk.setPassWord(encodePass);
								Log.e("LoginService",
										login_sdk.getPassWord());
								login_sdk.setGetpassword(true);
								break;
							case 400:
							case 500:
								Log.e("LoginService", "code:"+code);
								login_sdk.setGetpassword(false);
								break;
							}
						} catch (Exception e) {
							e.printStackTrace();
							login_sdk.setGetpassword(false);
						}
					}

			@Override
			public void onError(Call call, @Nullable Response response, @Nullable Exception e) {
				super.onError(call, response, e);
						Log.e("LoginService", "OnError");
						login_sdk.setGetpassword(false);
						login_sdk.setLOGIN_RESON(LoginSDK.RESON_MOBILE_AUTH_ERROR);
						login_sdk.setLOGIN_STATUS(LoginSDK.LOGIN_ERROR);
						SendMessage();
					}

			@Override
			public void onAfter(@Nullable String s, Call call, Response response, @Nullable Exception e) {
				super.onAfter(s, call, response, e);
						if (TextUtils.isEmpty(login_sdk.getPassWord())) {
							login_sdk.setGetpassword(false);
							Log.e("LoginService", "get password error");
							login_sdk.setLOGIN_RESON(LoginSDK.RESON_MOBILE_AUTH_ERROR);
							login_sdk.setLOGIN_STATUS(LoginSDK.LOGIN_ERROR);
							SendMessage();
						} else {
							Log.e("LoginService", "get password success");
							StartLogin();
						}
					}
				});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 开始登录
	 */
	private void StartLogin() {
		Log.e("LoginService", "正在登录");
		UserInfo userInfo = new UserInfo();
		if (Config.countryCode.matches("([+]|[0-9])\\d{0,4}")) {
			userInfo.countryCode = Config.countryCode;
		}
		userInfo.username = Config.CALL_BEFORE + Config.EIGHT
				+ login_sdk.getMobile_no();
		try {
			userInfo.password = Encrypt3DES.getInstance().decrypt(login_sdk.getPassWord());
		} catch (Exception e) {
			e.printStackTrace();
		}
		LoginCfg loginCfg = new LoginCfg();
		loginCfg.isAutoLogin = true;
		loginCfg.isVerified = true;
		loginCfg.isRememberPassword = true;
		LoginApi.login(userInfo, loginCfg);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.e("LoginService", "LoginService---OnCreate");
		LocalBroadcastManager.getInstance(this).registerReceiver(
				mLoginStatusChangedReceiver,
				new IntentFilter(LoginApi.EVENT_LOGIN_STATUS_CHANGED));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e("LoginService", "LoginService---onDestroy");
		LocalBroadcastManager.getInstance(this).unregisterReceiver(
				mLoginStatusChangedReceiver);
	}

	private BroadcastReceiver mLoginStatusChangedReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int new_status = intent.getIntExtra(LoginApi.PARAM_NEW_STATUS, -1);
			LogApi.d("tag", "the status is " + new_status);
			int reason = intent.getIntExtra(LoginApi.PARAM_REASON, -1);
			switch (new_status) {
			case LoginApi.STATUS_DISCONNECTED:
				String reasonCode = mapReasonStringtoReasonCode(reason);
				login_sdk.setLOGIN_STATUS(LoginSDK.LOGIN_ERROR);
				login_sdk.setLOGIN_RESON(reasonCode);
				SendMessage();
				Log.e("LoginService", "登录失败");
				Log.e("reason", reasonCode);
				break;
			case LoginApi.STATUS_CONNECTED:
				Log.e("LoginService", "登录成功");
				login_sdk.setLOGIN_STATUS(LoginSDK.LOGIN_SUCCESS);
				SendMessage();
				SPUtils.getInstance(context)
						.setPassWrod(login_sdk.getPassWord())
						.setUserName(
								"8" + login_sdk.getMobile_no());
//				SharedPreferences sp = getSharedPreferences(Config.SP_NAME,
//						MODE_PRIVATE);
//				sp.edit()
//						.putString(Config.LOGIN_USER_NAME,
//								"8" + login_sdk.getMobile_no())
//						.putString(Config.LOGIN_PASSWORD,
//								login_sdk.getPassWord()).commit();
//				UiUtils.setLookHome(getApplicationContext());
				break;

			default:
				break;
			}

		}
	};

	/**
	 * 登录失败的原因
	 * */
	private String mapReasonStringtoReasonCode(int reason) {

		String reasonStr = null;
		switch (reason) {
		case LoginApi.REASON_AUTH_FAILED:// 鉴权失败，用户名或密码错误
			reasonStr = "auth failed";
			// showToast("登录失败，用户名或密码错误");
			break;
		case LoginApi.REASON_CONNCET_ERR:// 连接错误
			reasonStr = "connect error";
			// showToast("连接错误");
			break;
		case LoginApi.REASON_NET_UNAVAILABLE:// 没有网络
			reasonStr = "no network";
			// showToast("当前网络不可用");
			break;
		case LoginApi.REASON_NULL:// 空
			reasonStr = "none";
			break;
		case LoginApi.REASON_SERVER_BUSY:// 服务器繁忙
			reasonStr = "server busy";
			// showToast("服务器繁忙，请稍后再试！！");
			break;
		case LoginApi.REASON_SRV_FORCE_LOGOUT:// 强行注销
			reasonStr = "force logout";
			// showToast("账号异地登录，被服务器强制下线");
			break;
		case LoginApi.REASON_USER_CANCEL:// 用户取消了
			reasonStr = "user canceled";
			break;
		case LoginApi.REASON_WRONG_LOCAL_TIME:// 当地时间错了
			reasonStr = "wrong local time";
			break;
		case LoginApi.REASON_ACCESSTOKEN_INVALID:// 无效的访问令牌
			reasonStr = "invalid access token";
			break;
		case LoginApi.REASON_ACCESSTOKEN_EXPIRED:// 访问令牌过期
			reasonStr = "access token expired";
			break;
		case LoginApi.REASON_APPKEY_INVALID:// 无效的application 密钥
			reasonStr = "invalid application key";
			break;
		case LoginApi.REASON_UNKNOWN:// 未知的
		default:
			reasonStr = "unknown";
			break;
		}
		return reasonStr;
	}
}
