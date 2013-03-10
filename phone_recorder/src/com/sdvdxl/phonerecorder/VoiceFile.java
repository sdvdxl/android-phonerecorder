package com.sdvdxl.phonerecorder;

import java.util.List;

public class VoiceFile {
	private String showName; // 显示的名字
	private String fullName; // 绝对路径
	private String showTime; // 显示时间
	private boolean selected; // 是否选中
	private long size;//文件大小
	public static int selectedFilesCount;
	
	static {
		selectedFilesCount = 0;
	}
	
	public String getShowName() {
		return showName;
	}

	public void setShowName(String showName) {
		this.showName = showName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getShowTime() {
		return showTime;
	}

	public void setShowTime(String showTime) {
		this.showTime = showTime;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		//如果不同,才进行更改，然后计算 选中数量
		//因为在编辑模式，展现列表元素时，会设置选中状态，
		//如果不加比较，则逻辑出错（初始化 selectedFilesCount 为 列表元素数量的负数），
		if (this.selected ^ selected) {
			this.selected = selected;
			if (this.selected) {
				selectedFilesCount++;
			} else {
				selectedFilesCount--;
			}
		}
	
	}
	
	
	
	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	/**
	 * 按时间升序排序
	 * @param list 要排序的链表
	 */
	public static void sortByTimeAsc(List<VoiceFile> list) {
		//TODO
	}
	
	
	/**
	 * 按时间降序排序
	 * @param list 要排序的链表
	 */
	public static void sortByTimeDesc(List<VoiceFile> list) {
		//TODO
	}
	
	/**
	 *  按录音文件大小降序排序
	 * @param list 要排序的链表
	 */
	public static void sortByFileSizeDesc(List<VoiceFile> list) {
		//TODO
	}
	
	/**
	 *  按录音文件大小升序排序
	 * @param list 要排序的链表
	 */
	public static void sortByFileSizeAsc(List<VoiceFile> list) {
		//TODO
	}
	
	public static List<VoiceFile> search(String[] keys) {
		//TODO
		return null;
	}

	/**
	 * 设置所有文件为非选中状态
	 * @param list 需要设置所有文件为非选中状态的文件链表
	 */
	public static void setAllToUnCheckedState(List<VoiceFile> list) {
		int length = list.size();
		for (int i=0; i<length; i++) {
			list.get(i).setSelected(false);
		}
	}
	
	/**
	 * 设置所有文件为选中状态
	 * @param list 需要设置所有文件为选中状态的文件链表
	 */
	public static void setAllToCheckedState(List<VoiceFile> list) {
		int length = list.size();
		for (int i=0; i<length; i++) {
			list.get(i).setSelected(true);
		}
	}
	
	/**
	 * 文件选中状态反转
	 * @param list 需要状态反转的文件链表
	 */
	public static void reverseAllCheckedState(List<VoiceFile> list) {
		int length = list.size();
		for (int i=0; i<length; i++) {
			list.get(i).setSelected(!list.get(i).isSelected());
		}
	}
	
}
