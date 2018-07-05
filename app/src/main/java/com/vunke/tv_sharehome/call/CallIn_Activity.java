//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.vunke.tv_sharehome.call;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.rcs.call.CallApi;
import com.huawei.rcs.call.CallSession;
import com.vunke.tv_sharehome.R.id;
import com.vunke.tv_sharehome.R.layout;
import com.vunke.tv_sharehome.RxBus;
import com.vunke.tv_sharehome.base.BaseActivity;
import com.vunke.tv_sharehome.greendao.dao.WhiteContanctDao;
import com.vunke.tv_sharehome.greendao.dao.WhiteContanctDao.Properties;
import com.vunke.tv_sharehome.greendao.dao.bean.WhiteContanct;
import com.vunke.tv_sharehome.greendao.dao.util.DbCore;
import com.vunke.tv_sharehome.utils.DBUtils;
import com.vunke.tv_sharehome.utils.UiUtils;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;

public class CallIn_Activity extends BaseActivity implements OnFocusChangeListener {
	private CallSession callSession = null;
	private Button callins_answer;
	private Button callouts_end;
	private Button switch_call;
	private ImageView callins_icon;
	private TextView callins_phoneNumber;
	private BroadcastReceiver callStatusChangedReceive = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			CallSession session = (CallSession)intent.getSerializableExtra("call_session");
			if(CallIn_Activity.this.callSession.equals(session)) {
				int newStatus = intent.getIntExtra("new_status", 0);
				switch(newStatus) {
					case 0:
						DBUtils.getInstance(context).insertCallRecorder(CallIn_Activity.this.split_moblie.toString(), "0", "");
						RxBus.getInstance().post(Integer.valueOf(100));
						CallIn_Activity.this.finish();
						break;
					case 4:
						intent = new Intent();
						if(session.getType() == 0) {
							intent.setClass(CallIn_Activity.this.mcontext, CallAudio_Activity.class);
						} else {
							intent.setClass(CallIn_Activity.this.mcontext, CallVideo_Activity.class);
						}

						CallIn_Activity.this.startActivity(intent);
						CallIn_Activity.this.finish();
				}

			}
		}
	};
	private String split_moblie;

	public CallIn_Activity() {
	}

	protected void onStart() {
		super.onStart();
		Log.e("CallIn_Activity", "only--onStart");
	}

	protected void onResume() {
		super.onResume();
		Log.e("CallIn_Activity", "only--onResume");
		UiUtils.openLocalView();
	}

	protected void onPause() {
		super.onPause();
		Log.e("CallIn_Activity", "only--onPause");
	}

	protected void onStop() {
		super.onStop();
		Log.e("CallIn_Activity", "only--onStop");
	}

	public void OnCreate() {
		this.setContentView(layout.activity_callins);
		Log.e("CallIn_Activity", "only--OnCreate");
		this.getExtras();
		this.initViews();
		this.initListener();
		LocalBroadcastManager.getInstance(this.getApplicationContext()).registerReceiver(this.callStatusChangedReceive, new IntentFilter("com.huawei.rcs.call.STATUS_CHANGED"));
	}

	private void initListener() {
		this.switch_call.setOnFocusChangeListener(this);
		this.callouts_end.setOnFocusChangeListener(this);
		this.callins_answer.setOnFocusChangeListener(this);
	}

	private void initWhiteListFromDB() {
		(new AsyncTask<Void, Void, Boolean>() {
			protected Boolean doInBackground(Void... params) {
				WhiteContanctDao whiteContanctDao = DbCore.getDaoSession().getWhiteContanctDao();
				QueryBuilder<WhiteContanct> queryBuilder = whiteContanctDao.queryBuilder();
				List<WhiteContanct> list = queryBuilder.where(Properties.HomePhone.eq(CallIn_Activity.this.split_moblie.substring(1)), new WhereCondition[0]).list();
				if(null != list && 0 != list.size()) {
					SystemClock.sleep(2000L);
					return Boolean.valueOf(true);
				} else {
					return Boolean.valueOf(false);
				}
			}

			protected void onPostExecute(Boolean result) {
				if(result.booleanValue()) {
					CallIn_Activity.this.callIn();
				} else {
					CallIn_Activity.this.initCallView();
				}

			}
		}).execute(new Void[0]);
	}

	private void initViews() {
		this.callins_answer = (Button)this.findViewById(id.callins_answer);
		this.callins_answer.requestFocus();
		this.callouts_end = (Button)this.findViewById(id.callouts_end);
		this.switch_call = (Button)this.findViewById(id.switch_call);
		this.callins_icon = (ImageView)this.findViewById(id.callins_icon);
		this.callins_phoneNumber = (TextView)this.findViewById(id.callins_phoneNumber);
		if(this.callSession != null) {
			if(this.callSession.getPeer() != null) {
				if(!TextUtils.isEmpty(this.callSession.getPeer().getNumber())) {
					String callNumber = this.callSession.getPeer().getNumber().trim();
					Log.e("ShareHome_Login", "接电话" + callNumber);
					this.split_moblie = callNumber.split("11831726")[1];
					if(!this.split_moblie.startsWith("8") && !this.split_moblie.startsWith("9")) {
						this.callins_phoneNumber.setText(this.split_moblie);
					} else {
						this.callins_phoneNumber.setText(this.split_moblie.substring(1));
					}

					this.initWhiteListFromDB();
				}
			}
		}
	}

	private void initCallView() {
		this.SetOnClickListener(new View[]{this.callins_answer, this.callouts_end, this.switch_call});
		if(this.callSession != null) {
			int var10000 = this.callSession.getType();
			CallSession var10001 = this.callSession;
			boolean isVideoCall;
			if(var10000 == 0) {
				var10000 = this.callSession.getType();
				var10001 = this.callSession;
				isVideoCall = var10000 == 0;
				this.switch_call.setVisibility(8);
			}

			var10000 = this.callSession.getType();
			var10001 = this.callSession;
			if(var10000 == 1) {
				var10000 = this.callSession.getType();
				var10001 = this.callSession;
				isVideoCall = var10000 == 1;
			}

		}
	}

	private void getExtras() {
		long sessionId = this.getIntent().getLongExtra("session_id", 255L);
		this.callSession = CallApi.getCallSessionById(sessionId);
		if(null == this.callSession) {
			this.finish();
		}
	}

	public void OnClick(View v) {
		if(v.getId() == id.callins_answer) {
			this.callIn();
		} else if(v.getId() == id.callouts_end) {
			if(this.callSession == null) {
				return;
			}

			this.callSession.terminate();
		} else if(v.getId() == id.switch_call) {
			if(this.callSession == null) {
				return;
			}

			if(this.callSession.getType() == 0) {
				this.callSession.accept(1);
			} else if(this.callSession.getType() == 1) {
				this.callSession.accept(0);
			}
		}

	}

	private void callIn() {
		if(this.callSession != null) {
			if(this.callSession.getType() == 0) {
				this.callSession.accept(0);
			} else if(this.callSession.getType() == 1) {
				this.callSession.accept(1);
			}

		}
	}

	public void onDestroy() {
		super.onDestroy();
		LocalBroadcastManager.getInstance(this.getApplicationContext()).unregisterReceiver(this.callStatusChangedReceive);
		Log.e("CallIn_Activity", "only--onDestroy");
		UiUtils.openLocalView();
	}

	protected void onSaveInstanceState(Bundle outState) {
	}

	public void onFocusChange(View v, boolean hasFocus) {
		if(hasFocus) {
			;
		}

	}

	public void onBackPressed() {
	}
}
