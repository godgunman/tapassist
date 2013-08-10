package tw.edu.ntu.csie.mhci.tapassist.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class LogHelper {

	public static void wirteLogTouchEvent(Context context, MotionEvent event) {
		JSONObject object = eventToJSONObject(event);
		wirte(context, object.toString());
	}

	public static void wirteLogTask(Context context, String taskCategory,
			int taskNum, String result, float targetX, float targetY) {
		JSONObject object = new JSONObject();
		try {
			object.put("time", System.currentTimeMillis());
			object.put("taskCategory", taskCategory);
			object.put("result", result);
			object.put("taskNum", taskNum);
			object.put("targetX", targetX);
			object.put("targetY", targetY);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		wirte(context, object.toString());
	}

	public static void wirte(Context context, String data) {

		File file = new File(getLogDir(context),
				PreferenceHelper.getStoreFile(context));
		try {
			FileOutputStream fo = new FileOutputStream(file, true);
			data = data + "\n";
			fo.write(data.getBytes());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.d("LogHelper", data);
	}

	public static String read(Context context, String fileName) {
		try {
			File file = new File(getLogDir(context), fileName);
			FileInputStream fi = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			while (fi.read(buffer) != -1) {
				baos.write(buffer);
			}
			return new String(baos.toByteArray());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static JSONObject eventToJSONObject(MotionEvent event) {
		JSONObject object = new JSONObject();
		try {
			object.put("action", MotionEvent.actionToString(event.getAction()));

			JSONArray array = new JSONArray();
			int pointerCount = event.getPointerCount();
			for (int i = 0; i < pointerCount; i++) {
				JSONObject pointer = new JSONObject();
				pointer.put("id", event.getPointerId(i));
				pointer.put("x", event.getX(i));
				pointer.put("y", event.getY(i));
				pointer.put("toolType", event.getToolType(i));
				array.put(pointer);
			}
			object.put("pointers", array);
			object.put("buttonState",
					MotionEvent.buttonStateToString(event.getButtonState()));
			object.put("metaState",
					KeyEvent.metaStateToString(event.getMetaState()));
			object.put("flags", "0x" + Integer.toHexString(event.getFlags()));
			object.put("edgeFlags",
					"0x" + Integer.toHexString(event.getEdgeFlags()));
			object.put("pointerCount", pointerCount);
			object.put("historySize", event.getHistorySize());
			object.put("eventTime", event.getEventTime());
			object.put("downTime", event.getDownTime());
			object.put("deviceId", event.getDeviceId());
			object.put("source", "0x" + Integer.toHexString(event.getSource()));

			return object;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static File getLogDir(Context context) {
		File logDir = new File(context.getFilesDir() + "/logs/");
		if (logDir.exists() == false) {
			logDir.mkdir();
		}
		return logDir;
	}
}
