package com.sdvdxl.phonerecorder;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.sdvdxl.common.CommonHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class VoiceFilesActivity extends Activity {
	private List<VoiceFile> voiceFiles;
	private static final int DIALOG_DELETE_FILE = 1;
	private ListView voiceFilesList;
	public static final String SELECTED_FILES = "com.sdvdxl.phonerecorder.VoiceFilesActivity.selectedFile";
	
	private static final int NORMAL_MODE = 0;
	private static final int EDITABLE_MODE = 1;
	private static int showMode;
	
	private static final int DIALOG_SDCARD_NOT_EXIST = 2;
	
	private EditableListAdapter editableAdapter;
	private NormalListAdapter normalAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.voicefileslist_activity);
		initData();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (showMode==NORMAL_MODE) {
			refreshListView(normalAdapter);
		} else {
			refreshListView(editableAdapter);
		}
	}
	
	/**
	 * 初始化数据
	 */
	private void initData() {
		voiceFiles = new ArrayList<VoiceFile>();
		
		voiceFilesList = (ListView)findViewById(R.id.voiceFilesList);
		listVoiceFiles();
		Log.d("voiceFilesList", voiceFilesList.toString());
		
		normalAdapter = new NormalListAdapter(this);
		editableAdapter = new EditableListAdapter(this);
		
		//加载正常模式
		changeShowMode(NORMAL_MODE);
		refreshListView(normalAdapter);
	}
	
	//列出录音文件
	private void listVoiceFiles() {
		if (!CommonHelper.isSDCardExist()) {
			showDialog(DIALOG_SDCARD_NOT_EXIST);
			return;
		}
		//清空链表数据
		voiceFiles.clear();

		File recordFolder =  new File(Environment.getExternalStorageDirectory() + "/My Record");
		//文件夹不存在，则创建一个
		if (!recordFolder.exists()) {
			recordFolder.mkdir();
		}
		
		File[] files = recordFolder.listFiles();
		int length = files.length;
		for (int i=0; i<length; i++) {
			try {
				String fileName = files[i].getName();
				String showName = fileName.split("#")[0];
				String fileTime = fileName.substring(
							fileName.indexOf('#')+1, fileName.indexOf('.'));
				String showTime = null;
				try {
					SimpleDateFormat sf = new SimpleDateFormat("yyy/MM/dd HH:mm:ss");
					showTime = sf.format(new SimpleDateFormat("yyy-MM-dd_HH-mm-ss")
							.parse(fileTime));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				VoiceFile voiceFile = new VoiceFile();
				voiceFile.setFullName(files[i].getAbsolutePath());
				voiceFile.setShowName(showName);
				voiceFile.setShowTime(showTime);
				
				voiceFiles.add(voiceFile);
			} catch (StringIndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 常规模式下长按对话框
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_DELETE_FILE:
			return new AlertDialog.Builder(this)
				.setTitle("删除选中录音文件")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						//执行删除操作
						deleteFiles();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						Log.d(MainActivity.TAG, "取消删除录音文件");
					}
				})
				.create();
		case DIALOG_SDCARD_NOT_EXIST:
			return new AlertDialog.Builder(this)
				.setTitle("提示")
				.setPositiveButton("关闭", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						dismissDialog(DIALOG_SDCARD_NOT_EXIST);
					}
				})
				.setMessage("SD Card 不存在")
				.create();
		}
		
		return null;
		
	}
	
	//刷新文件列表
	private void refreshListView(BaseAdapter adapter) {
		listVoiceFiles();
		setTitle("一共" + voiceFiles.size() + "个文件");
		Log.d("debug", String.valueOf(R.id.voiceFileName));
		//通知适配器数据发生变化
		adapter.notifyDataSetChanged();
	}

	/**
	 * 删除文件
	 */
	private void deleteFiles() {
		int length = voiceFiles.size();
		int delnum = 0;
		File file = null;
		VoiceFile voiceFile = new VoiceFile();
		
		for (int i=0; i<length; i++) {
			voiceFile = voiceFiles.get(i);
			if (voiceFile.isSelected()) {
				file = new File(voiceFile.getFullName());
				if (file.delete()) {
					delnum++;
					Log.d(MainActivity.TAG, "已删除录音文件" + voiceFile.getFullName());
				} else {
					Toast.makeText(VoiceFilesActivity.this, "删除文件失败" + voiceFile.getFullName(),
							Toast.LENGTH_SHORT).show();
					Log.d(MainActivity.TAG, "删除录音文件失败" + voiceFile.getFullName());
				}
			}
		}
		
		Toast.makeText(VoiceFilesActivity.this, "已删除" + delnum + "个录音文件",
				Toast.LENGTH_SHORT).show();
		Toast.makeText(VoiceFilesActivity.this, (length - delnum) + "个录音文件删除失败",
				Toast.LENGTH_SHORT).show();
		//刷新列表
		if (showMode==NORMAL_MODE) {
			refreshListView(normalAdapter);
		} else {
			refreshListView(editableAdapter);
		}
		//重新设置文件选中状态
		VoiceFile.setAllToUnCheckedState(voiceFiles);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.voicefileslist_menu, menu);
		menu.findItem(R.id.edit_voiceFiles);
		Log.d("Menu", "createOptionsMenu");
		return true;
	}

	/**
	 * 根据显示模式显示菜单
	 * @param menu 要加载的菜单
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		//先清空原有菜单，再重新加载新的
		menu.clear();
		if (showMode==NORMAL_MODE) {
			getMenuInflater().inflate(R.menu.voicefileslist_menu, menu);
		} else {
			getMenuInflater().inflate(R.menu.voicefileslist_menu2, menu);
		}
		Log.d("Menu", "preparedOptionsMenu");
		return true;
	}
	

	/**
	 * 常规模式菜单选项事件
	 * @param item 菜单项目
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (showMode==NORMAL_MODE) {
			doNormalOptions(item);
		} else {
			doEditableOptions(item);
		}
		
		return true;
	}
	
	
	/**
	 *  编辑模式菜单选项事件
	 * @param item 菜单项目
	 */
	private void doEditableOptions(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.cancel_edit:
			changeShowMode(NORMAL_MODE);
			Log.d(MainActivity.TAG, "取消编辑");
			break;
			//全选
			case R.id.check_all:
			checkAllFiles(true);
			Log.d(MainActivity.TAG, "全选");
			break;
			//反选
		case R.id.check_inverse:
			checkAllFiles(null);
			Log.d(MainActivity.TAG, "反选");
			break;
			//取消全选
		case R.id.uncheck_all:
			checkAllFiles(false);
			Log.d(MainActivity.TAG, "取消全选");
			break;
			//排序
		case R.id.del_files:
			confirmDeleteFilesInf();
			Log.d(MainActivity.TAG, "排序");
			break;
		}
	}

	/**
	 * 多选控制
	 */
	private void checkAllFiles(Boolean checkState) {
		//反选
		if (checkState==null) {
			VoiceFile.reverseAllCheckedState(voiceFiles);
		} else { 
			//全选
			if (checkState) { 
				VoiceFile.setAllToCheckedState(voiceFiles);
			} else {
				//全不选
				VoiceFile.setAllToUnCheckedState(voiceFiles);
			}
		}
		
		editableAdapter.notifyDataSetChanged();
	}

	private void doNormalOptions(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.edit_voiceFiles:
			//切换为编辑模式视图，菜单（菜单在preparedOptionsMenu中控制）
			changeShowMode(EDITABLE_MODE);
			Log.d(MainActivity.TAG, "编辑");
			break;
		/*case R.id.sort:
			//sortFiles
			//弹出排序模式
			Log.d(MainActivity.TAG, "排序");
			break;*/
		}
	}


	/**
	 * 列表编辑模式
	 * @author mrloong
	 *	
	 */
	class EditableListAdapter extends BaseAdapter {
		LayoutInflater inflater;
		
		class EditableListItem {
			TextView voiceFileName;
			TextView voiceFileTime;
			CheckBox voiceFileCheck;
		}
		
		public EditableListAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			return voiceFiles.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			EditableListItem item; //TODO 注意，这里item必须放在if里面，使其每一次都添加一个新对象，否则，只有第一个有checkbox
			if (convertView==null) {
				item =  new EditableListItem();
				convertView = inflater.inflate(R.layout.edit_voicefiles_items, null);
				item.voiceFileCheck = (CheckBox)convertView.findViewById(R.id.edit_voiceFileItem_check);
				item.voiceFileName = (TextView)convertView.findViewById(R.id.edit_voiceFileName);
				item.voiceFileTime = (TextView)convertView.findViewById(R.id.edit_voicFileTime);
				
				convertView.setTag(item);
			} else {
				item = (EditableListItem)convertView.getTag();
			}
			
			item.voiceFileName.setText(voiceFiles.get(position).getShowName());
			item.voiceFileTime.setText(voiceFiles.get(position).getShowTime());
			item.voiceFileCheck.setChecked(voiceFiles.get(position).isSelected());
			item.voiceFileCheck.setOnCheckedChangeListener(new CheckBoxStateChanged(position));
			Log.d("item", "" + position + item.voiceFileCheck.isChecked());
			return convertView;
		}
		
		class CheckBoxStateChanged implements CompoundButton.OnCheckedChangeListener {
			int position;
			public CheckBoxStateChanged(int position) {
				this.position = position;
			}
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				voiceFiles.get(position).setSelected(isChecked);
			}
		}
	}
	
	
	/**
	 * 列表正常模式
	 * @author mrloong
	 *	
	 */
	class NormalListAdapter extends BaseAdapter {
		LayoutInflater inflater;
		
		class EditableListItem {
			TextView voiceFileName;
			TextView voiceFileTime;
			CheckBox voiceFileCheck;
		}
		
		public NormalListAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			return voiceFiles.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			EditableListItem item; //TODO 注意，这里item必须放在if里面，使其每一次都添加一个新对象，否则，只有第一个有checkbox
			if (convertView==null) {
				item =  new EditableListItem();
				convertView = inflater.inflate(R.layout.voicefiles_items, null);
				item.voiceFileName = (TextView)convertView.findViewById(R.id.voiceFileName);
				item.voiceFileTime = (TextView)convertView.findViewById(R.id.voicFileTime);
				
				convertView.setTag(item);
			} else {
				item = (EditableListItem)convertView.getTag();
			}
			
			item.voiceFileName.setText(voiceFiles.get(position).getShowName());
			item.voiceFileTime.setText(voiceFiles.get(position).getShowTime());
			return convertView;
		}
	}
	
	//更改显示模式
	private void changeShowMode(int mode) {
		switch (mode) {
		case NORMAL_MODE:
			showMode = NORMAL_MODE;
			
			//初始化数据为常规模式
			VoiceFile.setAllToUnCheckedState(voiceFiles);
			voiceFilesList.setAdapter(normalAdapter);
			
			//item点击播放控制
			voiceFilesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> adapter, View view,
						int position, long id) {
					voiceFiles.get(position).setSelected(true);
					Log.d(MainActivity.TAG, "点击了录音文件:" + voiceFiles.get(position).getShowName());
					/**
					 * 弹出播放对话框
					 * 内容包括播放，暂停，停止
					 */
					Intent i = new Intent(VoiceFilesActivity.this, PlaySoundDialogActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString(SELECTED_FILES, voiceFiles.get(position).getFullName());
					i.putExtras(bundle);
					startActivity(i);
					
				}
			});
			
			//item 长按控制
			voiceFilesList.setOnItemLongClickListener(new OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> adapter, View view,
						int position, long id) {
					Log.d(MainActivity.TAG, "文件列表长按菜单");
					voiceFiles.get(position).setSelected(true);
					showDialog(DIALOG_DELETE_FILE);
					return false;
				}
			});
			break;
		case EDITABLE_MODE:
			showMode = EDITABLE_MODE;
			VoiceFile.setAllToUnCheckedState(voiceFiles);
			voiceFilesList.setAdapter(editableAdapter);
			//初始化数据为编辑模式
			voiceFilesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> adapter, View view,
						int position, long id) {
					voiceFiles.get(position).setSelected(!voiceFiles.get(position).isSelected());
					Log.d("编辑模式 单击", "" + position + voiceFiles.get(position).isSelected());
					editableAdapter.notifyDataSetChanged();
				}
			});
			break;
		}
	}
	
	/**
	 *  删除文件确认信息
	 */
	private void confirmDeleteFilesInf() {
		if (VoiceFile.selectedFilesCount==0) {
			Toast.makeText(this, "未选中任何文件", Toast.LENGTH_SHORT).show();
			return;
		}
		
		showDialog(DIALOG_DELETE_FILE);
	}

	@Override
	public void onBackPressed() {
		if (showMode==EDITABLE_MODE) {
			changeShowMode(NORMAL_MODE);
		} else {
			this.finish();
		}
	}
	
	
}
