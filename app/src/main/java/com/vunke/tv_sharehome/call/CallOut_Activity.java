//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.vunke.tv_sharehome.call;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.huawei.rcs.call.CallApi;
import com.huawei.rcs.call.CallSession;
import com.vunke.tv_sharehome.R.id;
import com.vunke.tv_sharehome.R.layout;
import com.vunke.tv_sharehome.base.BaseActivity;
import com.vunke.tv_sharehome.serv.CallOutActivityServ;
import com.vunke.tv_sharehome.utils.APIUtils;

public class CallOut_Activity extends BaseActivity {
	private String tag = this.getClass().getSimpleName();
	private String PhoneNumber;
	private boolean isVideoCall;
	CallSession callSession = null;
	private TextView callout_Phonenumber;
	private Button callout_cancel;
	private CallOutActivityServ activityServ = new CallOutActivityServ();
	private String username = "";
	private String calledType = "9";
	private BroadcastReceiver callStatusChangedReceive = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			CallOut_Activity.this.activityServ.callStutusChange(CallOut_Activity.this, intent, CallOut_Activity.this.callSession, CallOut_Activity.this.PhoneNumber, CallOut_Activity.this.calledType, CallOut_Activity.this.username);
		}
	};

	public CallOut_Activity() {
	}

	public void OnCreate() {
		this.setContentView(layout.activity_callout);
		LocalBroadcastManager.getInstance(this.getApplicationContext()).registerReceiver(this.callStatusChangedReceive, new IntentFilter("com.huawei.rcs.call.STATUS_CHANGED"));
		this.getExtras();
		this.initViews();
		this.initCall();
		Log.e("CallOut_Activity", "only--OnCreate");
	}

	protected void onStart() {
		super.onStart();
		Log.e("CallOut_Activity", "only--onStart");
	}

	protected void onResume() {
		super.onResume();
		Log.e("CallOut_Activity", "only--onResume");
	}

	protected void onPause() {
		super.onPause();
		Log.e("CallOut_Activity", "only--onPause");
	}

	protected void onStop() {
		super.onStop();
		Log.e("CallOut_Activity", "only--onStop");
	}

	private void getExtras() {
		this.PhoneNumber = this.getIntent().getStringExtra("PhoneNumber");
		this.isVideoCall = this.getIntent().getBooleanExtra("is_video_call", false);
	}

	private void initViews() {
		this.callout_Phonenumber = (TextView)this.findViewById(id.callout_Phonenumber);
		this.username = this.PhoneNumber.substring(9);
		this.calledType = this.PhoneNumber.substring(8, 9);
		this.callout_Phonenumber.setText(this.username);
		this.callout_cancel = (Button)this.findViewById(id.callout_cancel);
		this.SetOnClickListener(this.callout_cancel);
	}

	private void call() {
		if(this.isVideoCall) {
			this.callSession = CallApi.initiateVideoCall(this.PhoneNumber);
		} else {
			this.callSession = CallApi.initiateAudioCall(this.PhoneNumber);
		}

	}

	private void initCall() {
		this.call();
		if(this.callSession.getErrCode() != 0) {
			APIUtils.getInstance().Login(this.mcontext);
			(new Handler()).postDelayed(new Runnable() {
				public void run() {
					CallOut_Activity.this.call();
				}
			}, 3000L);
		}

	}

	public void OnClick(View v) {
		if(v.getId() != id.callout_switch_video && v.getId() != id.callout_mute && v.getId() == id.callout_cancel) {
			if(this.callSession.getErrCode() != 0) {
				this.finish();
				return;
			}

			this.callSession.terminate();
		}

	}

	public void onDestroy() {
		super.onDestroy();
		LocalBroadcastManager.getInstance(this.getApplicationContext()).unregisterReceiver(this.callStatusChangedReceive);
		Log.e("CallOut_Activity", "only--onDestroy");
	}

	public void onBackPressed() {
	}
}
