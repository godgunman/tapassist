package tw.edu.ntu.csie.mhci.tapassist;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Settings extends PreferenceActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//TODO should use PreferenceFragement
		addPreferencesFromResource(R.xml.settings);
	}
}
