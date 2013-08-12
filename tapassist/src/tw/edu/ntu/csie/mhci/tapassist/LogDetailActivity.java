package tw.edu.ntu.csie.mhci.tapassist;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import tw.edu.ntu.csie.mhci.tapassist.utils.LogHelper;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class LogDetailActivity extends Activity {

	public static final int DETELTE_LOG_FILE = 0;

	private TextView logText;
	private ListView listView;
	private String fileName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log);

		fileName = getIntent().getStringExtra("fileName");
		listView = (ListView) findViewById(R.id.logListView);

		String[] data = LogHelper.read(this, fileName).split("\n");
		List<JSONObject> jsonData = new ArrayList<JSONObject>();
		for (int i = 0; i < data.length; i++) {
			try {
				jsonData.add(new JSONObject(data[i]));
			} catch (JSONException e) {
//				Log.d("debug", "index:" + i + "data=" + data[i]);
				e.printStackTrace();
			}
		}

		// ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		// android.R.layout.simple_list_item_1, data);
		listView.setAdapter(new LogAdapter(this, jsonData));

		// logText = (TextView) findViewById(R.id.logText);
		// logText.setText(LogHelper.read(this, fileName));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.log, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.action_delete_log:
			File file = new File(LogHelper.getLogDir(this), fileName);
			file.delete();

			setResult(DETELTE_LOG_FILE);
			finish();

		}

		return super.onOptionsItemSelected(item);
	}

	class LogAdapter extends ArrayAdapter<JSONObject> {

		private LayoutInflater mInflater;
		private int resource;

		public LogAdapter(Context context, List<JSONObject> data) {
			super(context, android.R.layout.simple_list_item_1, data);

			resource = android.R.layout.simple_list_item_1;
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			TextView text;

			if (convertView == null) {
				view = mInflater.inflate(resource, parent, false);
			} else {
				view = convertView;
			}
			text = (TextView) view.findViewById(android.R.id.text1);

			JSONObject item = getItem(position);
			try {
				text.setText(item.toString(3));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return view;
		}
	}
}
