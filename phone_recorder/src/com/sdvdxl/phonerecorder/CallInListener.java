package com.sdvdxl.phonerecorder;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallInListener extends PhoneStateListener {
	static final String TAG = CallInListener.class.getName();
	private MyRecorder recorder;
	
	public CallInListener() {
		recorder = new MyRecorder();
	}
	
	public CallInListener(MyRecorder recorder) {
		this.recorder = recorder;
	}
	
	@Override
	public void onCallStateChanged(int state, String incomingNumber) {
		super.onCallStateChanged(state, incomingNumber);
		Log.d(TAG, "CallIn");
		switch (state) {
			//空闲（挂断）
		case TelephonyManager.CALL_STATE_IDLE:
			Log.d(TAG, "IDEL");
			recorder.stop();
			break;
			//摘机
		case TelephonyManager.CALL_STATE_OFFHOOK:
			Log.d(TAG, "OFFHOOK");
			if (recorder.isCommingNumber()) {
				recorder.start();
			}
			break;
			//响铃
		case TelephonyManager.CALL_STATE_RINGING:
			Log.d(TAG, "响铃");
			recorder.setPhoneNumber(incomingNumber);
			Log.d(TAG, "准备录音， 设置打入号码为：" + incomingNumber);
			recorder.setIsCommingNumber(true);
			
		}
	}
	
	public void setPhoneNumber(String phoneNumber) {
		recorder.setPhoneNumber(phoneNumber);
	}

}
