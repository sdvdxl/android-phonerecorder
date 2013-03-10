package com.sdvdxl.phonerecorder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.SeekBar;

/**
 *  问题： 添加播放按钮状态切换方法，替换在PlaySoundDialog中的相关代码
 * @author sdvdxl
 *
 */
public class MyMediaPlayer {
	private MediaPlayer mplayer;
	private String voiceFileName;
	public static final int PAUSED = 0;
	public static final int PLAYING = 1; 
	public static final int STOPPED = 2;
	private static int playState;
	private int position;
	
	private ImageButton playsoundStart;
	private ImageButton playsoundStop;
	private SeekBar playProgressControlBar;
	
	public MyMediaPlayer(Activity activity, String voiceFileName) {
		this.voiceFileName = voiceFileName;
		playState = STOPPED;
		mplayer = new MediaPlayer();
		playsoundStart = (ImageButton) activity.findViewById(R.id.playsoundStart);
		playsoundStop = (ImageButton) activity.findViewById(R.id.playsoundStop);
		playProgressControlBar = (SeekBar) activity.findViewById(R.id.playProgressControlBar);
		
		mplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				changePlayerButtonState(STOPPED);
				Log.d("Player", "playing finished");
			}
		});
		
		prepare();
	}
	

	public void setVoiceFile(String voiceFileName) {
		this.voiceFileName = voiceFileName;
	}
	
	/**
	 * 启动播放器
	 */
	public void start() {
		if (mplayer!=null) {
			try {
				prepare();
				mplayer.start();
				changePlayerButtonState(PLAYING);
				Log.d(MainActivity.TAG, "播放器已启动");
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 暂停播放
	 */
	public void pause() {
		if (mplayer.isPlaying()) {
			position = mplayer.getCurrentPosition();
			mplayer.pause();
			changePlayerButtonState(PAUSED);
			Log.d("Player", "paused");
		}
	}
	
	/**
	 * 停止播放
	 */
	public void stop() {
		if (playState!=STOPPED) {
			mplayer.stop();
			changePlayerButtonState(STOPPED);
			Log.d("Player", "stopped");
		}
	}
	
	private void prepare() {
		mplayer.reset();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(voiceFileName);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			mplayer.setDataSource(fis.getFD());
			mplayer.prepare();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fis!=null) {
				try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		
	}

	/**
	 * 恢复播放
	 */
	public void resume() {
		mplayer.start();
		mplayer.seekTo(position);
		changePlayerButtonState(PLAYING);
		Log.d("Player", "resume");
	}
	
	public void release() {
		if (mplayer!=null) {
			mplayer.release();
			mplayer = null;
			Log.d("Player", "release");
		}
	}

	public int getPlayState() {
		return playState;
	}
	
	/**
	 * 更改播放器按钮功能，图片
	 * @param state
	 */
	private void changePlayerButtonState(int state) {
		switch (state) {
		case PLAYING:
			playState = PLAYING;
			//改变为暂停按钮图片
			playsoundStart.setImageResource(R.drawable.btn_pause);
			//改变为停止可执行图片
			playsoundStop.setImageResource(R.drawable.btn_stop);
			//更新SeekBar显示
			new BarUpdate(this, playProgressControlBar).start();
			Log.d("changePlayerButtonState", "playing");
			break;
		case PAUSED:
			playState = PAUSED;
			//改变为播放按钮图片
			playsoundStart.setImageResource(R.drawable.btn_start);
			//改变为停止可执行图片
			playsoundStop.setImageResource(R.drawable.btn_stop);
			Log.d("changePlayerButtonState", "paused");
			break;
		case STOPPED:
			playState = STOPPED;
			//改变为播放按钮图片
			playsoundStart.setImageResource(R.drawable.btn_start);
			//改变为停止不可执行图片
			playsoundStop.setImageResource(R.drawable.btn_stop_unable);
			//播放进度条初始化
			playProgressControlBar.setProgress(0);
			Log.d("changePlayerButtonState", "stopped");
			break;
		}
	}


	public int getDuration() {
		return mplayer.getDuration();
	}


	public void seekTo(int progress) {
		mplayer.start();
		mplayer.seekTo(progress);
		changePlayerButtonState(PLAYING);
	}
	
	public int getCurrentPosition() {
		return mplayer.getCurrentPosition();
	}
}

class BarUpdate extends Thread {
	MyMediaPlayer mplayer;
	SeekBar mbar;
	
	public BarUpdate(MyMediaPlayer mplayer, SeekBar bar) {
		this.mplayer = mplayer;
		this.mbar = bar;
	}
	
	public void run() {
		while (mplayer.getPlayState()==MyMediaPlayer.PLAYING) {
			mbar.setProgress(mplayer.getCurrentPosition());
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}