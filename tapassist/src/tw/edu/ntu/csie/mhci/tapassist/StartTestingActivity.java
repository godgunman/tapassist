package tw.edu.ntu.csie.mhci.tapassist;

import tw.edu.ntu.csie.mhci.tapassist.testing.ScrollingCaseActivity;
import tw.edu.ntu.csie.mhci.tapassist.testing.TappingCaseActivity;
import tw.edu.ntu.csie.mhci.tapassist.utils.PreferenceHelper;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class StartTestingActivity extends Activity {

	private TextView userName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);

		userName = (TextView) findViewById(R.id.userNameText);
		userName.setText("Hi, " + PreferenceHelper.getUserName(this));
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
}
