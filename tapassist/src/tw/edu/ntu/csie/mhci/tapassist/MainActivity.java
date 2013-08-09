package tw.edu.ntu.csie.mhci.tapassist;

import tw.edu.ntu.csie.mhci.tapassist.testing.ScrollingCaseActivity;
import tw.edu.ntu.csie.mhci.tapassist.testing.TappingCaseActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void goToTappingTest(View view) {
		Intent intent = new Intent();
		intent.setClass(this, TappingCaseActivity.class);
		startActivity(intent);
	}

	public void goToScrollingTest(View view) {
		Intent intent = new Intent();
		intent.setClass(this, ScrollingCaseActivity.class);
		startActivity(intent);
	}

	public void goToSettings(View view) {
		Intent intent = new Intent();
		intent.setClass(this, Settings.class);
		startActivity(intent);
	}

	public void goToLog(View view) {
		Intent intent = new Intent();
		intent.setClass(this, LogActivity.class);
		startActivity(intent);
	}
}
