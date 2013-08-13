package tw.edu.ntu.csie.mhci.tapassist.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class LogHelper {

	public static void wirteLogTouchEvent(Context context, MotionEvent event,
			String metadata) {
		JSONObject object = eventToJSONObject(event);
		if (metadata != null) {
			try {
				object.put("metadata", metadata);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		write(context, object.toString());
	}

	public static void wirteLogTaskStart(Context context, String taskType,
			int taskNum, JSONObject metadata) {
		JSONObject object = new JSONObject();
		try {
			object.put("time", System.currentTimeMillis());
			object.put("taskType", taskType);
			object.put("taskAction", "start");
			object.put("taskNum", taskNum);
			if (metadata != null) {
				object.put("metadata", metadata);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		write(context, object.toString());
	}

	public static void wirteLogTaskEnd(Context context, String taskType,
			int taskNum, JSONObject metadata) {
		JSONObject object = new JSONObject();
		try {
			object.put("time", System.currentTimeMillis());
			object.put("taskType", taskType);
			object.put("taskAction", "end");
			object.put("taskNum", taskNum);
			if (metadata != null) {
				object.put("metadata", metadata);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		write(context, object.toString());
	}

	public static void write(Context context, String data) {

		String fileName = PreferenceHelper.getStoreFile(context);
		fileName = fileName.replaceAll("[: \\-]", "_");
		File file = new File(getLogDir(context), fileName);
		try {
			FileOutputStream fo = new FileOutputStream(file, true);
			data = data + "\n";
			fo.write(data.getBytes());
			fo.flush();
			fo.close();
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
			object.put("time", System.currentTimeMillis());

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

	public static File getLogDir(Context context) {
		File sdCard = Environment.getExternalStorageDirectory();
		File dir = new File(sdCard + "/tapassist/logs");
		if (dir.exists() == false) {
			dir.mkdirs();
		}
		return dir;
	}

	public static void copy(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);

		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}
}
