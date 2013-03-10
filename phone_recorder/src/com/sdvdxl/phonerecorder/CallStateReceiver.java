package com.sdvdxl.phonerecorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

class CallStateReceiver extends BroadcastReceiver {
	public static final String TAG = CallStateReceiver.class.getName();
	private CallInListener callIn;
	private CallOutListener callOut;
	private MyRecorder recorder;
	
	TelephonyManager telmgr;
	
	{	recorder = new MyRecorder();
		callIn = new CallInListener(recorder);
		callOut = new CallOutListener(recorder);
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		telmgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String phoneState = intent.getAction();
		if (recorder==null) {
			recorder = new MyRecorder();
		}
		
		if (phoneState.equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
			String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);//拨出号码
			telmgr.listen(callOut, PhoneStateListener.LISTEN_CALL_STATE);
			recorder.setPhoneNumber(phoneNumber);
			recorder.setIsCommingNumber(false);
			Log.d(TAG, "设置为去电状态");
			Log.d(TAG, "去电状态 呼叫：" + phoneNumber);
		} else {
			//来电
			telmgr.listen(callIn, PhoneStateListener.LISTEN_CALL_STATE);
		}
	}
	
}