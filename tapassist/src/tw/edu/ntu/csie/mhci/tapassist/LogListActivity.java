package tw.edu.ntu.csie.mhci.tapassist;

import java.io.File;
import java.io.IOException;

import tw.edu.ntu.csie.mhci.tapassist.utils.LogHelper;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class LogListActivity extends ListActivity {

	private static final int DETAIL_LOG = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setListViewData();
	}

	private void setListViewData() {
		File logDir = LogHelper.getLogDir(this);
		if (logDir.isDirectory()) {
			String[] files = logDir.list();
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, files);
			setListAdapter(adapter);
		}
		getListView().setOnItemLongClickListener(listViewLongClick);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		TextView logFileText = (TextView) v.findViewById(android.R.id.text1);
		String fileName = logFileText.getText().toString();

		Intent intent = new Intent();
		intent.putExtra("fileName", fileName);
		intent.setClass(this, LogDetailActivity.class);

		startActivityForResult(intent, DETAIL_LOG);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.log_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.log_backup:

			File sdCard = Environment.getExternalStorageDirectory();
			File backupDir = new File(sdCard.getAbsolutePath()
					+ "/tapassist_back");
			if (backupDir.exists() == false) {
				backupDir.mkdirs();
			}

			File logDir = LogHelper.getLogDir(this);
			if (logDir.isDirectory()) {
				String[] filesStr = logDir.list();
				for (int i = 0; i < filesStr.length; i++) {
					File oldFile = new File(logDir, filesStr[i]);
					File backFile = new File(backupDir, filesStr[i]);
					Log.d("backup", "oldfile:" + oldFile.toString());
					Log.d("backup", "backfile:" + backFile.toString());
					try {
						LogHelper.copy(oldFile, backFile);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			break;

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == DETAIL_LOG) {
			if (resultCode == LogDetailActivity.DETELTE_LOG_FILE) {
				setListViewData();
			}
		}
	}

	private OnItemLongClickListener listViewLongClick = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					parent.getContext());
			builder.setTitle("File path");
			builder.setMessage(LogHelper.getLogDir(parent.getContext())
					+ File.separator
					+ (String) parent.getItemAtPosition(position));
			builder.create().show();

			return true;
		}
	};
}
