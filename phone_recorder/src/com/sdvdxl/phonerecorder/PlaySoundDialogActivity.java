package com.sdvdxl.phonerecorder;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.SeekBar;

public class PlaySoundDialogActivity extends Activity {
	String selectedFile;
	private MyMediaPlayer player;
	private ImageButton playsoundStart;
	private ImageButton playsoundStop;
	private ImageButton playsoundCancel;
	private SeekBar playProgressControlBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.playsound_dialog_activity);
		selectedFile = getIntent().getExtras().getString(
				VoiceFilesActivity.SELECTED_FILES);
		Log.d(MainActivity.TAG, "传递过来的文件名：" + selectedFile);
		player = new MyMediaPlayer(this, selectedFile);
		
		playProgressControlBar = (SeekBar)findViewById(R.id.playProgressControlBar);
		playsoundStart = (ImageButton)findViewById(R.id.playsoundStart);
		playsoundStop = (ImageButton)findViewById(R.id.playsoundStop);
		playsoundCancel = (ImageButton)findViewById(R.id.playsoundCancel);
		playsoundStop.setEnabled(true);
		playProgressControlBar.setMax(player.getDuration());
		Log.d("文件长度", String.valueOf(player.getDuration()));
		
		playsoundStart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (player.getPlayState()) {
				case MyMediaPlayer.STOPPED:
					player.start();
					Log.d("playsoundButton", "启动");
					break;
				case MyMediaPlayer.PLAYING:
					player.pause();
					Log.d("playsoundButton", "暂停");
					break;
				case MyMediaPlayer.PAUSED:
					player.resume();
					Log.d("playsoundButton", "恢复");
				}
			}
		});
		playsoundStop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				player.stop();
				Log.d("playsoundButton", "停止");
			}
		});
		playsoundCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		playProgressControlBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				player.seekTo(seekBar.getProgress());
				Log.d("SeekBar", "SeekBar触摸结束 位置" + seekBar.getProgress());
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				player.pause();
				Log.d("SeekBar", "SeekBar触摸开始 位置" + seekBar.getProgress());
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				/*if (fromUser) {
					player.seekTo(progress);
				}*/
			}
		});
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		player.stop();
		player.release();
		Log.d(MainActivity.TAG, player.toString());
	}
	
	@Override
	public void onPause() {
		super.onPause();
		player.pause();
	}
	
}
