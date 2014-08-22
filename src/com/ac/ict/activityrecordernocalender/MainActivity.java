package com.ac.ict.activityrecordernocalender;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity {
	private final String dirName = "ActivityRecorderNoCalender";// Ŀ¼��
	private final String timeFileName = "ActivityTime.txt";// �ʱ���ļ���
	private final String typeFileName = "ActivityType.txt";// ������ļ���
	private Spinner spinnerActivity;
	private Button btnAddActivity;
	private Button btnStart;
	private TextView lblStatus;
	private static ArrayList<String> arrActivity = null;
	private FileUtils fileUtils;
	private String curActivity = null;// ��ǰ�

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Log.i("God", "onCreate------");
		try {
			fileUtils = new FileUtils(dirName, timeFileName, typeFileName);
		} catch (IOException e) {
			e.printStackTrace();
		}

		spinnerActivity = (Spinner) findViewById(R.id.spinnerActivity);
		btnAddActivity = (Button) findViewById(R.id.btnAddActivity);
		btnStart = (Button) findViewById(R.id.btnStart);
		lblStatus = (TextView) findViewById(R.id.lblStatus);
		lblStatus.setText("");
		fillSpinner();
		btnAddActivity.setOnClickListener(new AddActiviyButtonClick());
		btnStart.setOnClickListener(new StartActiviyButtonClick());

		// ��ʹ��
		curActivity = fileUtils.GetCurrentActivityName();
		if (curActivity != null && !curActivity.equals("")) {
			int index = arrActivity.indexOf(curActivity);
			if (index != -1) {
				spinnerActivity.setSelection(index);// ����Ĭ��ֵ
				btnStart.setText(R.string.end);
				lblStatus.setText(curActivity + "��...");
			}
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	private void fillSpinner() {
		arrActivity = fileUtils.getAllActivityType();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, arrActivity);
		adapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);
		spinnerActivity.setAdapter(adapter);

	}

	class StartActiviyButtonClick implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			if (arrActivity == null || arrActivity.size() == 0) {
				new AlertDialog.Builder(MainActivity.this)
						.setTitle("û�л���ͣ��������!")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setPositiveButton("ȷ��", null).show();
				return;
			}
			if (curActivity == null || curActivity.equals("")) {// ��ǰû�л��ִ�У���ʼ
				curActivity = spinnerActivity.getSelectedItem().toString();
				SaveData("start");
				btnStart.setText(R.string.end);
				lblStatus.setText(curActivity + "��...");

			} else {// ��ǰ�л��ִ�У���ֹͣ
				SaveData("stop");
				btnStart.setText(R.string.start);
				lblStatus.setText("");
				curActivity = null;
			}

		}

		// ��������
		private void SaveData(String status) {
			String time = new SimpleDateFormat("yyyyMMddHHmmss",
					Locale.getDefault()).format(new Date());
			String msg = curActivity + "," + status + "," + time;
			try {
				fileUtils.appendLine(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	class AddActiviyButtonClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			LayoutInflater factory = LayoutInflater.from(MainActivity.this);// ��ʾ��
			final View view = factory.inflate(R.layout.activity_add, null);// ���������final��???
			final EditText edit = (EditText) view
					.findViewById(R.id.txtaddactivityname);// �����������

			new AlertDialog.Builder(MainActivity.this)
					.setTitle(R.string.inputactivityname)
					// ��ʾ�����
					.setView(view)
					.setPositiveButton(
							R.string.ok,// ��ʾ���������ť
							new android.content.DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// �¼�
									String newActivityName = edit.getText()
											.toString();
									newActivityName = newActivityName.trim();
									if (newActivityName.equals("")) {
										return;// ����Ϊ��ʱ������
									}
									boolean res = fileUtils
											.addActivityType(newActivityName);
									if (res) {
										fillSpinner();
									}
									int index = arrActivity
											.indexOf(newActivityName);
									if (index != -1) {
										spinnerActivity.setSelection(index);// ����Ĭ��ѡ�������
									}
								}
							}).setNegativeButton(R.string.cancel, null)
					.create().show();
		}
	}

}
