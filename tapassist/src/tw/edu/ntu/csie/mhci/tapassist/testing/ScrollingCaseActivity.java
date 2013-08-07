package tw.edu.ntu.csie.mhci.tapassist.testing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import tw.edu.ntu.csie.mhci.tapassist.R;
import tw.edu.ntu.csie.mhci.tapassist.utils.Media;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ScrollingCaseActivity extends Activity {

	private final static int LISTVIEW_SIZE = 30;
	private ListView listView;
	private ImageView redBoxImage;

	private int targetItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scrolling);

		redBoxImage = (ImageView) findViewById(R.id.redBoxImage);

		listView = (ListView) findViewById(R.id.listView);
		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				View childView = view.getChildAt(targetItem - firstVisibleItem);
				if (childView != null) {

					if (redBoxImage.getY() <= childView.getY()
							&& childView.getY() <= redBoxImage.getY()
									+ redBoxImage.getHeight()) {
						Media.play(ScrollingCaseActivity.this, "right.mp3");
					}
				}
			}
		});

		initListView();
	}

	private void initListView() {

		List<Map<String, Integer>> data = new ArrayList<Map<String, Integer>>(
				LISTVIEW_SIZE);

		targetItem = Math.abs(new Random().nextInt() % LISTVIEW_SIZE);
		Log.d("debug", "targetItem=" + targetItem);
		for (int i = 0; i < LISTVIEW_SIZE; i++) {
			Map<String, Integer> item = new HashMap<String, Integer>();
			if (i == targetItem) {
				item.put("image", R.drawable.face_normal);
			} else {
				item.put("image", R.drawable.face_bad);
			}
			data.add(item);
		}

		String[] from = new String[] { "image" };
		int[] to = new int[] { R.id.listItemImage };

		SimpleAdapter adapter = new SimpleAdapter(this, data,
				R.layout.listview_item, from, to);

		listView.setAdapter(adapter);
	}
}
