package com.sdvdxl.phonerecorder;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallOutListener extends PhoneStateListener {
	static final String TAG = CallOutListener.class.getName();
	private MyRecorder recorder;
	
	public CallOutListener() {
		recorder = new MyRecorder();
	}
	
	public CallOutListener(MyRecorder recorder) {
		this.recorder = recorder;
	}
	
	@Override
	public void onCallStateChanged(int state, String incomingNumber) {
		super.onCallStateChanged(state, incomingNumber);
		Log.d(TAG, "CallOut");
		switch (state) {
			//空闲（挂断）
		case TelephonyManager.CALL_STATE_IDLE:
			Log.d(TAG, "IDLE");
			recorder.stop();
			break;
			//摘机
		case TelephonyManager.CALL_STATE_OFFHOOK:
			Log.d(TAG, "OFFHOOK");
			if (!recorder.isCommingNumber()) {
				recorder.start();
			}
			break;
		}
	}
	
	public void setPhoneNumber(String phoneNumber) {
		recorder.setPhoneNumber(phoneNumber);
	}

}


