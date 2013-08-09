package tw.edu.ntu.csie.mhci.tapassist.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceHelper {

	public static String getString(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences(
				"tw.edu.ntu.csie.mhci.tapassist_preferences",
				Context.MODE_PRIVATE);
		return sp.getString(key, null);
	}

}
