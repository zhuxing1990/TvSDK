package com.vunke.tv_sharehome.serv;

import android.content.Context;
import android.content.Intent;

import com.huawei.rcs.call.CallApi;
import com.huawei.rcs.call.CallSession;
import com.vunke.tv_sharehome.Config;
import com.vunke.tv_sharehome.RxBus;
import com.vunke.tv_sharehome.call.CallAudio_Activity;
import com.vunke.tv_sharehome.call.CallOut_Activity;
import com.vunke.tv_sharehome.call.CallVideo_Activity;
import com.vunke.tv_sharehome.net.MissedCallNet;
import com.vunke.tv_sharehome.utils.DBUtils;
import com.vunke.tv_sharehome.utils.Logger;
import com.vunke.tv_sharehome.utils.SPUtils;
import com.vunke.tv_sharehome.utils.UiUtils;


public class CallOutActivityServ {
	private String tag = this.getClass().getSimpleName();

	/*
	 * 如果调用状态已成为连接,它将会转到电话说的布局。否则,调用请求也许被拒绝了。
	 */
	/**
	 * 记录STATUS_ALERTING次数据 如果些状态出现一次则发送短信
	 * 
	 */
	private int alerting_count = 0;
	private long current_time = 0;
	private long dy_time = 0;
	private long dy_status_time = 0;

	/**
	 * 
	 * @param context
	 * @param intent
	 *            通话状态改变的广播中的intent
	 * @param callSession
	 *            会话
	 * @param phoneNumber
	 *            通话的电话 118372618390878104
	 * @param calledType
	 *            被叫类型
	 * @param username
	 *            主叫号码
	 */
	public void callStutusChange(CallOut_Activity context, Intent intent,
								 CallSession callSession, String phoneNumber, String calledType,
								 String username) {
		CallSession session = (CallSession) intent
				.getSerializableExtra(CallApi.PARAM_CALL_SESSION);
		/* Call session should be checked against. */
		if (!callSession.equals(session)) {
			return;
		}
		int newStatus = intent.getIntExtra(CallApi.PARAM_NEW_STATUS,
				CallSession.STATUS_IDLE);
		System.out.println("newStatus:" + newStatus);
		long status_code = intent
				.getLongExtra(CallApi.PARAM_SIP_STATUS_CODE, 0);
		System.out.println("status_code==>" + status_code);
		switch (newStatus) {
		case CallSession.STATUS_CONNECTED:// CallSession.STATUS_CONNECTED =
			intent = new Intent();
			if (session.getType() == CallSession.TYPE_AUDIO) {
				intent.setClass(context, CallAudio_Activity.class);
			} else {
				intent.setClass(context, CallVideo_Activity.class);
			}
			context.startActivity(intent);
			context.finish();
			break;
		case CallSession.STATUS_OUTGOING:// 1
			alerting_count = 0;
			dy_time = 0;
			current_time = System.currentTimeMillis();
			break;
		case CallSession.STATUS_ALERTING:// 3
			alerting_count++;
			if (alerting_count == 1) {
				dy_time = System.currentTimeMillis() - current_time;
				Logger.d(tag, "拨打电话1至3之间的时间隔", dy_time + "");
			}

			break;
		case CallSession.STATUS_IDLE:// CallSession.STATUS_IDLE = 0
			/**
			 * 插入通话记录
			 */
			DBUtils.getInstance(context)
					.insertCallRecorder(phoneNumber.substring(8),
							Config.CALLRECORDER_TYPE_DIAL, "");

			// 通知刷新通话记录
			RxBus.getInstance().post(100);
			context.finish();
			Logger.d(tag, "是否发送未接电话提醒:", "alerting_count:" + alerting_count
					+ ";dy_time:" + dy_time);
			// 调用未接来电提醒接口 ,表示对方已登录但对方没有网络(对方无法接通)
			/*
			 * if (alerting_count == 1 && dy_time > 2000) { //sendSms(1);
			 * Logger.d(tag, "only_status对方无法接通:", "only_status对方无法接通"); } //
			 * 表示对方不在线或对方不是想家用户 else if (alerting_count == 1 && dy_time < 1000)
			 * { // sendSms(2); Logger.d(tag, "only_status对方无法接通:",
			 * "only_status对方不在线"); }
			 */

			/**
			 * 1:被叫不在线 2:被叫在线但没有网络 3:被叫拒接但主叫不挂断 4:被叫正在通话中主叫不挂断
			 */
			if (status_code == 408) {
				dy_status_time = System.currentTimeMillis() - current_time;

				if (alerting_count == 1 && dy_time < 2000) {
					// 对方正忙,主叫不挂断
					if (dy_status_time > 90000) {
						Logger.d(tag, "only_status对方无法接通:", " 对方正忙,主叫不挂断");
						sendSms(Status.BUSY, context, username, calledType);
					}
					// 对方不在线,不挂断
					else if (dy_status_time > 10000) {
						sendSms(Status.OFFLINE, context, username, calledType);
						Logger.d(tag, "only_status对方无法接通:", " 对方不在线,不挂断");
					}

				}
				// 在线没网络,不挂断
				else if (alerting_count == 1 && dy_time >= 2000) {
					if (dy_status_time > 30000) {
						sendSms(Status.NOT_NET, context, username, calledType);
						Logger.d(tag, "only_status对方无法接通:", "在线没网络,不挂断");
					}
				}
				// 被叫拒接,不挂断
				else if (alerting_count == 3) {
					Logger.d(tag, "only_status对方无法接通:", "被叫拒接,不挂断");
				}
			} else {
				// 对方正忙或不在线,主叫挂断
				if (alerting_count == 1 && dy_time < 2000) {
					sendSms(Status.OFFLINE, context, username, calledType);
					Logger.d(tag, "only_status对方无法接通:", " 对方正忙或不在线,主叫挂断");
				}
				// 在线没网络,挂断
				else if (alerting_count == 1 && dy_time >= 2000) {
					sendSms(Status.NOT_NET, context, username, calledType);
					Logger.d(tag, "only_status对方无法接通:", "在线没网络,挂断");
				}
				// 被叫拒接,挂断
				else if (alerting_count == 3) {
					Logger.d(tag, "only_status对方无法接通:", "被叫拒接,挂断");
				}
			}
			System.out.println("dy_status_time:" + dy_status_time);

			break;

		default:
			break;
		}

	}

	public enum Status {
		/**
		 * NOT_NET：没有网络 OFFLINE:离线 BUSY:正在通话中 REJECT:拒接
		 */
		NOT_NET, OFFLINE, BUSY, REJECT

	}

	/**
	 * 发送短信通话对方有电话没有接听
	 * 
	 * @param status
	 *            没有接听的原因
	 * @param context
	 * @param username
	 *            主叫号码
	 * @param calledType
	 *            被叫类型
	 */
	private void sendSms(final Status status, Context context, String username,
			String calledType) {

		new MissedCallNet(SPUtils.getInstance(context).getUserName(), username,
				current_time + "", "8", calledType,
				new MissedCallNet.MissedCallNetCallback() {

					@Override
					public void onSuccess(String code, String messge) {
						
						String reson = null;
						switch (status) {
						case OFFLINE:
							reson = "对方不在线";
							break;
						case NOT_NET:
							reson = "对方网络不通";
							break;
						case BUSY:
							reson = "对方正在通话中";
							break;
						case REJECT:
							reson = "对方拒接了您的电话";
							break;
						default:
							break;
						}
						UiUtils.showToast(reson+",已经使用短信方式通知对方");
					}

					@Override
					public void onFail(String code, String messge) {
						
					}
				});
	}
}
