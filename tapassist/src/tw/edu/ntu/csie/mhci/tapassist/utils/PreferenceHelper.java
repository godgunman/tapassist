package tw.edu.ntu.csie.mhci.tapassist.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferenceHelper {

	public static String getString(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences(
				"tw.edu.ntu.csie.mhci.tapassist_preferences",
				Context.MODE_PRIVATE);
		return sp.getString(key, null);
	}

	public static void setUserNmae(Context context, String userName) {
		SharedPreferences sp = context.getSharedPreferences("profile",
				Context.MODE_PRIVATE);

		Editor editor = sp.edit();
		editor.putString("user_name", userName);
		editor.commit();
	}

	public static String getUserName(Context context) {
		SharedPreferences sp = context.getSharedPreferences("profile",
				Context.MODE_PRIVATE);
		return sp.getString("user_name", "undefined");
	}
}
