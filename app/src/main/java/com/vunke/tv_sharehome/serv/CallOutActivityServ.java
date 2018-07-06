package com.vunke.tv_sharehome.serv;

import android.content.Intent;

import com.huawei.rcs.call.CallApi;
import com.huawei.rcs.call.CallSession;
import com.vunke.tv_sharehome.Config;
import com.vunke.tv_sharehome.RxBus;
import com.vunke.tv_sharehome.call.CallAudio_Activity;
import com.vunke.tv_sharehome.call.CallOut_Activity;
import com.vunke.tv_sharehome.call.CallVideo_Activity;
import com.vunke.tv_sharehome.utils.DBUtils;
import com.vunke.tv_sharehome.utils.Logger;


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


}
