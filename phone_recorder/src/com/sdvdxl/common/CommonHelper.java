package com.sdvdxl.common;

import java.io.File;

import android.os.Environment;

public class CommonHelper {
	/**
	 * 取得SDCard路径
	 * @return 返回sdCard路径 不管是否挂载都存在这个路径一般为 /mnt/sdcard
	 */
	public static String getSDCardPath() {
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}
	
	/**
	 * 取得SDCard路径
	 * @return 返回sdCard文件类型 不管是否挂载都存在这个路径一般为 /mnt/sdcard
	 */
	public File getSDCard() {
		File sdcard = Environment.getExternalStorageDirectory();
		return sdcard == null ? null : sdcard;
	}
	
	/**
	 * 检验SDCard 是否存在（已经挂载）
	 * @return SDCard 是否存在（已经挂载）
	 */
	public static boolean isSDCardExist() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}
	
	/**
	 * 检验SDCard 是否存在（已经挂载）且可写
	 * @return SDCard 是否存在（已经挂载）且可写
	 */
	public static boolean isSDCardWritable() {
		if (isSDCardExist()) {
			return !Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY);
		}
		
		return false;
	}
	
}
