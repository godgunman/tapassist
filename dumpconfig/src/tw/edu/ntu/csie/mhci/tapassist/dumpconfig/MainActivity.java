package tw.edu.ntu.csie.mhci.tapassist.dumpconfig;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MainActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		try {
			Class<?> c = Class.forName("android.view.ViewConfiguration");
			Field[] fields = c.getDeclaredFields();
			List<Map<String, String>> data = new ArrayList<Map<String, String>>();
			for (Field f : fields) {
				try {
					f.setAccessible(true);
					Log.d("debug", "" + f.getName());
					Log.d("debug", "" + f.get(c));
					Map<String, String> item = new HashMap<String, String>();
					item.put("name", f.getName());
					item.put("value", String.valueOf(f.get(c)));
					data.add(item);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			SimpleAdapter adapter = new SimpleAdapter(this, data,
					android.R.layout.simple_list_item_2, new String[] { "name",
							"value" }, new int[] { android.R.id.text1,
							android.R.id.text2 });
			setListAdapter(adapter);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
