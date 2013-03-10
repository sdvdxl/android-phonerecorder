package com.sdvdxl.phonerecorder;

import java.util.List;

import com.sdvdxl.common.CommonHelper;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	private Button btnStart; // 电话监听启动
	private Button btnStop; // 电话监听关闭
	private Button btnBrowseVoiceFile; // 浏览录音文件
	public static final String TAG = "Recorder";

	private final int FUNCTION_INFO = 0;
	private final int UPDATE_INFO = 1;
	private final int AUTHOR_INFO = 2;
	private final int FEEDBACK_INFO = 3;
	private static final int SDCARD_NOTBEWRITABLE = 4;
	
	
	//private Button testStartRecord;
	//private Button testStopRecord;
	//MyRecorder recorder;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Log.d("sdcard", CommonHelper.getSDCardPath());
		Log.d("sdcard", String.valueOf(CommonHelper.isSDCardExist()));
		Log.d("sdcard", String.valueOf(CommonHelper.isSDCardWritable()));
		// 获取Button
		btnStart = (Button) findViewById(R.id.btnStart);
		btnStop = (Button) findViewById(R.id.btnStop);
		btnBrowseVoiceFile = (Button) findViewById(R.id.btnBrowseVoiceFile);

		/**
		 * 注册按钮点击事件
		 */
		// 启动按钮
		btnStart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 检查存储卡是否存在，不存在则不能启动录音服务
				if (CommonHelper.isSDCardWritable()) {
					// 启动电话状态监听服务
					// intent = new Intent(getBaseContext(),
					// PhoneCallStateService.class);
					startService(new Intent(getBaseContext(),
							PhoneCallStateService.class));

					// 将启动按钮设置为不可用状态
					btnStart.setEnabled(false);
					// 将停止按钮设置为不可用状态
					btnStop.setEnabled(true);
				} else {
					showDialog(SDCARD_NOTBEWRITABLE);
				}
			}
		});

		// 停止按钮
		btnStop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO 停止电话状态监听服务
				// 将停止按钮设置为不可用状态
				btnStart.setEnabled(true);
				// 将启动按钮设置为不可用状态
				btnStop.setEnabled(false);
				stopService(new Intent(getBaseContext(),
						PhoneCallStateService.class));
				// intent = null;
			}
		});

		// 浏览文件按钮
		btnBrowseVoiceFile.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(
						"com.sdvdxl.phonerecorder.VOICEFILESACTIVITY"));
			}
		});
		
		/*testStartRecord = (Button)findViewById(R.id.test_startRecord);
		testStopRecord = (Button)findViewById(R.id.test_stopRecord);
		
		testStartRecord.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				recorder = new MyRecorder();
				recorder.setPhoneNumber("test123");
				recorder.start();
			}
		});
		
testStopRecord.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				recorder.stop();
			}
		});*/
		
	}

	@Override
	protected void onResume() {
		super.onResume();

		btnStop.setEnabled(false);
		btnStart.setEnabled(true);

		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> infos = am.getRunningServices(50); // 50是最大值
		for (RunningServiceInfo info : infos) {
			Log.d("Recorder", info.service.getClassName());
			if (info.service.getClassName().equals(
					"com.sdvdxl.phonerecorder.PhoneCallStateService")) {
				btnStop.setEnabled(true);
				btnStart.setEnabled(false);
				// Toast.makeText(getBaseContext(), "通话监听服务已经启动",
				// Toast.LENGTH_SHORT).show();
				return;
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.function_info:
			showDialog(FUNCTION_INFO);
			Log.d("MainMenu", String.valueOf(item.getTitle()));
			break;
		case R.id.update_info:
			showDialog(UPDATE_INFO);
			Log.d("MainMenu", String.valueOf(item.getTitle()));
			break;
		case R.id.author_info:
			showDialog(AUTHOR_INFO);
			Log.d("MainMenu", String.valueOf(item.getTitle()));
			break;
		case R.id.feedback:
			showDialog(FEEDBACK_INFO);
			Log.d("MainMenu", String.valueOf(item.getTitle()));
			break;
		}

		return true;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case FUNCTION_INFO:
			return new AlertDialog.Builder(this)
					.setTitle("功能介绍")
					.setMessage(R.string.function_info)
					.setPositiveButton("关闭",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dismissDialog(FUNCTION_INFO);
								}
							}).create();
		case UPDATE_INFO:
			return new AlertDialog.Builder(this)
					.setTitle("更新说明")
					.setMessage(R.string.update_info)
					.setPositiveButton("关闭",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dismissDialog(UPDATE_INFO);
								}
							}).create();
		case AUTHOR_INFO:
			return new AlertDialog.Builder(this)
					.setTitle("作者简介")
					.setMessage(R.string.author_info)
					.setPositiveButton("关闭",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dismissDialog(AUTHOR_INFO);
								}
							}).create();
		case FEEDBACK_INFO:
			return new AlertDialog.Builder(this)
					.setTitle("反馈方式")
					.setMessage(R.string.feedback_info)
					.setPositiveButton("关闭",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dismissDialog(FEEDBACK_INFO);
								}
							}).create();
		case SDCARD_NOTBEWRITABLE:
			return new AlertDialog.Builder(this)
					.setTitle("提示")
					.setMessage(R.string.sdcard_info)
					.setPositiveButton("关闭",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dismissDialog(SDCARD_NOTBEWRITABLE);
								}
							}).create();
		}
		return null;
	}

}
