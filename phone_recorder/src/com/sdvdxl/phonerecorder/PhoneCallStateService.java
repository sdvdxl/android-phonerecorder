package com.sdvdxl.phonerecorder;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class PhoneCallStateService extends Service {
	private CallStateReceiver callReciver;
	public static final String TAG = CallStateReceiver.class.getName();
	
	@Override
	public void onCreate() {
		super.onCreate();

		 /* 以下应放在onStartCommand中，
		  * 但2.3.5以下版本不会因service重新启动而重新调用
		  * 监听电话状态，如果是打入且接听 或者 打出 则开始自动录音
		  * 通话结束，保存文件到外部存储器上*/
		callReciver = new CallStateReceiver();
		IntentFilter callFilter = new IntentFilter();
		callFilter.addAction("android.intent.action.PHONE_STATE");
		callFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		registerReceiver(callReciver, callFilter);
		Log.d("Recorder", "正在监听中...");
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(callReciver);
		Toast.makeText(this, "已关闭电话监听服务", Toast.LENGTH_LONG).show();
		Log.d("Recorder", "已关闭电话监听服务");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(this, "电话监听服务已启动", Toast.LENGTH_LONG).show();
		return START_STICKY;
	}

}
