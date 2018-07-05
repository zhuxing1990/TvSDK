/**
 * 
 */
package com.vunke.tv_sharehome.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vunke.tv_sharehome.utils.Logger;


/**
 * @Author: 陈庚 Description:开机启动广播 Date: 2016-9-12 Time: 上午10:20:51
 */
public class BootCompletedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {
		//Toast.makeText(context, "开机启动~~~~", 1).show();
		Logger.e("BootCompletedReceiver", "开机启动", "开机启动");
		
		
	}

}
