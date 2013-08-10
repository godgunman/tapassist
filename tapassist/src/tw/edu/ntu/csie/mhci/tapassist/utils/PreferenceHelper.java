package tw.edu.ntu.csie.mhci.tapassist.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.format.DateFormat;

@SuppressLint("SimpleDateFormat")
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

		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		editor.putString("log_store_file",
				userName + "_" + dateFormat.format(new Date()) + ".txt");
		editor.commit();
	}

	public static String getUserName(Context context) {
		SharedPreferences sp = context.getSharedPreferences("profile",
				Context.MODE_PRIVATE);
		return sp.getString("user_name", null);
	}

	public static String getStoreFile(Context context) {
		SharedPreferences sp = context.getSharedPreferences("profile",
				Context.MODE_PRIVATE);
		return sp.getString("log_store_file", null);
	}
}
