package tw.edu.ntu.csie.mhci.tapassist;

import tw.edu.ntu.csie.mhci.tapassist.utils.LogHelper;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class LogDetailActivity extends Activity {
	private TextView logText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log);

		String fileName = getIntent().getStringExtra("fileName");

		logText = (TextView) findViewById(R.id.logText);
		logText.setText(LogHelper.read(this, fileName));
	}
}