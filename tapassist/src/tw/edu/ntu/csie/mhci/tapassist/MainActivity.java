package tw.edu.ntu.csie.mhci.tapassist;

import tw.edu.ntu.csie.mhci.tapassist.utils.PreferenceHelper;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends ListActivity {

	private static final String[] DATA = { "Start Testing", "Settings", "Logs" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, DATA);
		setListAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		switch (position) {
		case 0:

			final EditText nickNameInput = new EditText(this);
			nickNameInput.setHint("nick name");

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Pleast enter tester's name");
			builder.setView(nickNameInput);
			builder.setPositiveButton("ok", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String userName = nickNameInput.getText().toString();
					PreferenceHelper.setUserNmae(MainActivity.this, userName);
					goToTesting();
				}
			});
			builder.show();

			break;
		case 1:
			goToSettings();
			break;
		case 2:
			goToLog();
			break;
		}

	}

	public void goToTesting() {
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, StartTestingActivity.class);
		startActivity(intent);
	}

	public void goToSettings() {
		Intent intent = new Intent();
		intent.setClass(this, Settings.class);
		startActivity(intent);
	}

	public void goToLog() {
		Intent intent = new Intent();
		intent.setClass(this, LogListActivity.class);
		startActivity(intent);
	}
}
