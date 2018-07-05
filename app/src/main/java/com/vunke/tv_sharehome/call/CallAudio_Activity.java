//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.vunke.tv_sharehome.call;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnClickListener;
import android.media.AudioManager;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.huawei.rcs.call.CallApi;
import com.huawei.rcs.call.CallSession;
import com.huawei.rcs.log.LogApi;
import com.huawei.rcs.system.SysApi.PhoneUtils;
import com.vunke.tv_sharehome.RxBus;
import com.vunke.tv_sharehome.R.id;
import com.vunke.tv_sharehome.R.layout;
import com.vunke.tv_sharehome.base.BaseActivity;
import com.vunke.tv_sharehome.utils.DBUtils;
import java.util.Timer;
import java.util.TimerTask;

public class CallAudio_Activity extends BaseActivity {
	private CallSession videoShareCallsession = null;
	private boolean isVideoShareCaller = false;
	public static String PARAM_SESSION_ID = "PARAM_SESSION_ID";
	public static String PARAM_IS_CALLER = "PARAM_IS_CALLER";
	private AlertDialog alertDialog;
	private CallSession callSession;
	private boolean isMute = false;
	private Timer timer;
	private int callTime;
	private Handler handler = new Handler();
	private TextView call_phoneNumber;
	private TextView callout_status;
	private TextView callout_time;
	private ImageView callout_icon;
	private Button callout_switch_video;
	private Button callout_mute;
	private Button callout_cancel;
	private Button call_handsfree;
	private String callNumber;
	private BroadcastReceiver callInvitationReciever = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			CallAudio_Activity.this.videoShareCallsession = (CallSession)intent.getSerializableExtra("call_session");
			if(null != CallAudio_Activity.this.videoShareCallsession) {
				if(2 == CallAudio_Activity.this.videoShareCallsession.getType()) {
					CallAudio_Activity.this.isVideoShareCaller = false;
					Toast.makeText(CallAudio_Activity.this.mcontext, "A Video Share Invitation Incoming", 1).show();
				}

			}
		}
	};
	private BroadcastReceiver callStatusChangedReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			CallSession session = (CallSession)intent.getSerializableExtra("call_session");
			int newStatus;
			if(session == CallAudio_Activity.this.videoShareCallsession) {
				newStatus = intent.getIntExtra("new_status", 0);
				switch(newStatus) {
					case 0:
						if(CallAudio_Activity.this.isVideoShareCaller) {
							Toast.makeText(CallAudio_Activity.this.getApplicationContext(), "视频共享会话终止", 1).show();
						}
					case 1:
					case 2:
					default:
						break;
					case 3:
						Toast.makeText(CallAudio_Activity.this.getApplicationContext(), "视频共享会话提醒", 1).show();
						break;
					case 4:
						Intent newIntent = new Intent(CallAudio_Activity.this.mcontext, CallVideo_Activity.class);
						newIntent.putExtra(CallAudio_Activity.PARAM_SESSION_ID, session.getSessionId());
						newIntent.putExtra(CallAudio_Activity.PARAM_IS_CALLER, CallAudio_Activity.this.isVideoShareCaller);
						CallAudio_Activity.this.startActivityForResult(newIntent, 0);
				}

			} else if(CallAudio_Activity.this.callSession.equals(session)) {
				newStatus = intent.getIntExtra("new_status", 0);
				switch(newStatus) {
					case 0:
						String call_time = CallAudio_Activity.this.callout_time.getText().toString();
						DBUtils.getInstance(context).insertCallRecorder(CallAudio_Activity.this.callNumber + "", "1", call_time);
						RxBus.getInstance().post(Integer.valueOf(100));
						CallAudio_Activity.this.finish();
					case 1:
					case 2:
					case 3:
					case 4:
					case 5:
					case 6:
					default:
				}
			}
		}
	};
	private BroadcastReceiver callTypeChangeInvitationReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			CallSession session = (CallSession)intent.getSerializableExtra("call_session");
			if(CallAudio_Activity.this.callSession.equals(session)) {
				Builder dl = new Builder(CallAudio_Activity.this.mcontext);
				dl.setTitle("提示");
				dl.setMessage("对方是邀请一个视频电话，接受或不接受？");
				dl.setPositiveButton("接受", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						CallAudio_Activity.this.callSession.acceptAddVideo();
						CallAudio_Activity.this.alertDialog.dismiss();
						CallAudio_Activity.this.alertDialog = null;
					}
				});
				dl.setNegativeButton("拒绝", new OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						CallAudio_Activity.this.callSession.rejectAddVideo();
						CallAudio_Activity.this.alertDialog.dismiss();
						CallAudio_Activity.this.alertDialog = null;
					}
				});
				CallAudio_Activity.this.alertDialog = dl.create();
				CallAudio_Activity.this.alertDialog.show();
			}
		}
	};
	private BroadcastReceiver callTypeChangedReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			CallSession session = (CallSession)intent.getSerializableExtra("call_session");
			if(CallAudio_Activity.this.callSession.equals(session)) {
				int newType = intent.getIntExtra("new_type", -1);
				if(newType == 1) {
					Intent newIntent = new Intent(CallAudio_Activity.this.mcontext, CallVideo_Activity.class);
					CallAudio_Activity.this.startActivity(newIntent);
					CallAudio_Activity.this.finish();
				}

			}
		}
	};
	private BroadcastReceiver callTypeChangeRejectedReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			CallSession session = (CallSession)intent.getSerializableExtra("call_session");
			if(CallAudio_Activity.this.callSession.equals(session)) {
				Toast.makeText(CallAudio_Activity.this.getApplicationContext(), "你的邀请被拒绝", 1).show();
			}
		}
	};
	private BroadcastReceiver callQosReportReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			CallSession session = (CallSession)intent.getSerializableExtra("call_session");
			if(CallAudio_Activity.this.callSession.equals(session)) {
				int quality = intent.getIntExtra("call_qos", 1);
				switch(quality) {
					case 0:
					case 1:
					case 2:
					default:
				}
			}
		}
	};

	public CallAudio_Activity() {
	}

	public void OnCreate() {
		this.setContentView(layout.activity_callaudio);
		this.initCallSession();
		this.initData();
		this.initViews();
		this.registerReceivers();
		this.startCallTimeTask();
	}

	private void initData() {
		this.callNumber = this.callSession.getPeer().getNumber();
		if(this.callNumber.startsWith("11831726")) {
			this.callNumber = this.callNumber.substring(8);
		}

	}

	public void initCallSession() {
		this.callSession = CallApi.getForegroudCallSession();
		if(null == this.callSession) {
			LogApi.d("V2OIP", "没有发现通话");
			this.finish();
		}
	}

	private void initViews() {
		this.call_phoneNumber = (TextView)this.findViewById(id.call_phoneNumber);
		Log.e("tag", "语音:" + this.callSession.getPeer().getNumber());
		this.call_phoneNumber.setText(this.callNumber.substring(1));
		this.callout_status = (TextView)this.findViewById(id.callout_status);
		this.callout_time = (TextView)this.findViewById(id.callout_time);
		this.callout_icon = (ImageView)this.findViewById(id.callout_icon);
		this.callout_switch_video = (Button)this.findViewById(id.callout_switch_video);
		this.callout_mute = (Button)this.findViewById(id.callout_mute);
		this.callout_cancel = (Button)this.findViewById(id.callout_cancel);
		AudioManager audioManamger = (AudioManager)this.getSystemService("audio");
		boolean speakerState = audioManamger.isSpeakerphoneOn();
		this.callout_status.setText("通话中");
		this.SetOnClickListener(new View[]{this.callout_switch_video, this.callout_mute, this.callout_cancel, this.call_handsfree});
	}

	public void OnClick(View v) {
		if(v.getId() == id.callout_switch_video) {
			if(this.callSession.isAbleToAddVideo()) {
				this.callSession.addVideo();
				Toast.makeText(this.getApplicationContext(), "发送邀请,等待对方接受……", 1).show();
			}
		} else if(v.getId() == id.callout_mute) {
			this.isMute = !this.isMute;
			if(this.isMute) {
				this.callout_status.setText("静音");
				this.callSession.mute();
			} else {
				this.callout_status.setText("通话中");
				this.callSession.unMute();
			}
		} else if(v.getId() == id.callout_cancel) {
			this.callSession.terminate();
			this.showToast("通话结束");
		}

	}

	private void registerReceivers() {
		LocalBroadcastManager.getInstance(this.getApplicationContext()).registerReceiver(this.callInvitationReciever, new IntentFilter("com.huawei.rcs.call.INVITATION"));
		LocalBroadcastManager.getInstance(this.getApplicationContext()).registerReceiver(this.callStatusChangedReceiver, new IntentFilter("com.huawei.rcs.call.STATUS_CHANGED"));
		LocalBroadcastManager.getInstance(this.getApplicationContext()).registerReceiver(this.callTypeChangeInvitationReceiver, new IntentFilter("com.huawei.rcs.call.TYPE_CHANGE_INVITATION"));
		LocalBroadcastManager.getInstance(this.getApplicationContext()).registerReceiver(this.callTypeChangedReceiver, new IntentFilter("com.huawei.rcs.call.TYPE_CHANGED"));
		LocalBroadcastManager.getInstance(this.getApplicationContext()).registerReceiver(this.callTypeChangeRejectedReceiver, new IntentFilter("com.huawei.rcs.call.TYPE_CHANGE_REJECTED"));
		LocalBroadcastManager.getInstance(this.getApplicationContext()).registerReceiver(this.callQosReportReceiver, new IntentFilter("com.huawei.rcs.call.QOS_REPORT"));
	}

	private void unRegisterReceivers() {
		LocalBroadcastManager.getInstance(this.getApplicationContext()).unregisterReceiver(this.callInvitationReciever);
		LocalBroadcastManager.getInstance(this.getApplicationContext()).unregisterReceiver(this.callStatusChangedReceiver);
		LocalBroadcastManager.getInstance(this.getApplicationContext()).unregisterReceiver(this.callTypeChangeInvitationReceiver);
		LocalBroadcastManager.getInstance(this.getApplicationContext()).unregisterReceiver(this.callTypeChangedReceiver);
		LocalBroadcastManager.getInstance(this.getApplicationContext()).unregisterReceiver(this.callTypeChangeRejectedReceiver);
		LocalBroadcastManager.getInstance(this.getApplicationContext()).unregisterReceiver(this.callQosReportReceiver);
	}

	protected void onDestroy() {
		super.onDestroy();
		this.unRegisterReceivers();
		if(null != this.alertDialog) {
			this.alertDialog.dismiss();
			this.alertDialog = null;
		}

		this.stopCallTimeTask();
	}

	private void stopCallTimeTask() {
		if(this.timer != null) {
			this.timer.cancel();
			this.timer = null;
		}

	}

	private void startCallTimeTask() {
		this.timer = new Timer();
		this.callTime = (int)((System.currentTimeMillis() - this.callSession.getOccurDate()) / 1000L);
		this.timer.schedule(new TimerTask() {
			public void run() {
				CallAudio_Activity.this.callTime++;
				CallAudio_Activity.this.handler.post(new Runnable() {
					public void run() {
						CallAudio_Activity.this.callout_time.setText(PhoneUtils.getCallDurationTime(CallAudio_Activity.this.callTime));
					}
				});
			}
		}, 1000L, 1000L);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void onBackPressed() {
	}
}
