package com.vunke.tv_sharehome.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.huawei.rcs.contact.ContactApi;
import com.huawei.rcs.contact.ContactSummary;
import com.huawei.rcs.login.LoginApi;
import com.huawei.rcs.login.LoginCfg;
import com.huawei.rcs.login.UserInfo;
import com.vunke.tv_sharehome.Config;

import java.util.List;

public final class APIUtils {
	private static APIUtils instance;
	private String tag = this.getClass().getSimpleName();
	private APIUtils() {
	}

	public static APIUtils getInstance() {
		if (instance == null) {
			instance = new APIUtils();
		}
		return instance;
	}

	/**
	 * 获取联系人
	 * 
	 * @return
	 */
	public static List<ContactSummary> getContactList() {
		return ContactApi.getContactSummaryList(ContactApi.LIST_FILTER_ALL);
	}

	public void Login(final Context context) {
		if (CommonUtil.isNetConnected(context)) {
			String userName = SPUtils.getInstance(context).getUserName();
			String pass = SPUtils.getInstance(context).getPassWrod();
			
			
			if (!TextUtils.isEmpty(userName)) {
				login(userName, pass);
			}
		}else {
			UiUtils.showToast("网络未连接");
		}
		
		
	}

	private void login(String userName, String pass) {
		// 查询用户的配置数据
		LoginCfg loginCfg = LoginApi.getLoginCfg(Config.ACCOUNT_BEFORE + "8" +userName);
		if (loginCfg == null) {
			loginCfg = new LoginCfg();
			loginCfg.isAutoLogin = true;
			loginCfg.isVerified = true;
			loginCfg.isRememberPassword = true;
		}
		UserInfo userInfo = new UserInfo();
		userInfo.username = Config.ACCOUNT_BEFORE + "8" + userName;
		userInfo.password = pass;
		Logger.d(tag, "~~~~~登录~~~~~", "userName:"+userName);
		LoginApi.login(userInfo, loginCfg);

	}
	

	public String getBusinessId(Context mContext){
		Uri uri = Uri
				.parse("content://com.hunantv.operator.mango.hndxiptv/userinfo");
		Cursor mCursor = mContext.getContentResolver().query(uri, null, null,
				null, null);
		
		String user_id = "";
		String user_token = "";
		if (mCursor != null) {
			while (mCursor.moveToNext()) {
				String name = mCursor.getString(mCursor.getColumnIndex("name"));
				if ("user_id".equals(name)) {
					user_id = mCursor
							.getString(mCursor.getColumnIndex("value"));
						Logger.e("APIUtils", "获取业务账号=>businessId:", user_id);
					return user_id;
				} else if ("user_token".equals(name)) {
					user_token = mCursor.getString(mCursor
							.getColumnIndex("value"));
				
					Logger.e("APIUtils", "获取业务账号=>userToken:", user_token);
				}
			}

			mCursor.close();
		}
		
		return null;
	}

}
