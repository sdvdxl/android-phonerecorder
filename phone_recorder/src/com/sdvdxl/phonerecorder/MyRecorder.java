package com.sdvdxl.phonerecorder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

public class MyRecorder {
	private String phoneNumber;
	private MediaRecorder mrecorder;
	private boolean started = false; //录音机是否已经启动
	private boolean isCommingNumber = false;//是否是来电
	private String TAG = "Recorder";
	private long offHookTime;
	
	public MyRecorder(String phoneNumber) {
		this.setPhoneNumber(phoneNumber);
	}
	
	public MyRecorder() {
	}

	public void start() {
		if (started) {
			return;
		}
		
		started = true;
		offHookTime = System.currentTimeMillis();
		mrecorder = new MediaRecorder();
		
		File recordPath = new File(
				Environment.getExternalStorageDirectory()
				, "/My record"); 
		if (!recordPath.exists()) {
			recordPath.mkdirs();
			Log.d("recorder", "创建目录");
		}
		
		String callDir = "呼出";
		if (isCommingNumber) {
			callDir = "呼入";
		}
		String fileName = callDir + "-" + phoneNumber + "#" 
				+ new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
					.format(new Date(System.currentTimeMillis())) + ".3gp";
		File recordName = new File(recordPath, fileName);
		
		try {
			recordName.createNewFile();
			Log.d("recorder", "创建文件" + recordName.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		mrecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mrecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(recordName.getAbsolutePath());
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			mrecorder.setOutputFile(fos.getFD());
		} catch (IllegalStateException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		mrecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		
		try {
			mrecorder.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mrecorder.start();
		Log.d(TAG , "录音开始");
	}
	
	public void stop() {
		try {
			//来电
			if (isCommingNumber) {
				long idelTime = System.currentTimeMillis();
				int middle = (int)(idelTime-offHookTime);
				Log.d(TAG, "间隔" + middle);
				//如果摘机时间和挂机时间太短，那么是手机种类造成的
				//不应该挂断
				if (mrecorder!=null && started && (middle>500)) {
					mrecorder.stop();
					started = false;
					mrecorder.release();
					mrecorder = null;
					Log.d(TAG , "录音结束");
				}
			} else {
				//去电
				long idelTime = System.currentTimeMillis();
				int middle = (int)(idelTime-offHookTime);
				Log.d(TAG, "间隔" + middle);
				//如果摘机时间和挂机时间太短，那么是手机种类造成的
				//不应该挂断
				if (mrecorder!=null && started && middle>500) {
					mrecorder.stop();
					started = false;
					mrecorder.release();
					mrecorder = null;
					Log.d(TAG , "录音结束");
				}
			}
			
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
	
	public void pause() {
		
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean hasStarted) {
		this.started = hasStarted;
	}

	public boolean isCommingNumber() {
		return isCommingNumber;
	}

	public void setIsCommingNumber(boolean isCommingNumber) {
		this.isCommingNumber = isCommingNumber;
	}

}
