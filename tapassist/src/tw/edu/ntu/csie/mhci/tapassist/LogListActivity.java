package tw.edu.ntu.csie.mhci.tapassist;

import java.io.File;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class LogListActivity extends ListActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		File logDir = new File(getFilesDir(), "/logs/");
		if (logDir.isDirectory()) {
			String[] files = logDir.list();
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, files);
			setListAdapter(adapter);
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		TextView logFileText = (TextView) v.findViewById(android.R.id.text1);
		String fileName = logFileText.getText().toString();

		Intent intent = new Intent();
		intent.putExtra("fileName", fileName);
		intent.setClass(this, LogDetailActivity.class);

		startActivity(intent);
	}
}
