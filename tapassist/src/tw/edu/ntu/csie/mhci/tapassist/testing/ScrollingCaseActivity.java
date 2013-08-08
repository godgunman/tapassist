package tw.edu.ntu.csie.mhci.tapassist.testing;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import tw.edu.ntu.csie.mhci.tapassist.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ScrollingCaseActivity extends Activity {

	private final static int LISTVIEW_SIZE = 30;
	private ListView listView;
	private ImageView redBoxImage;
	private LinearLayout inforLinearLayout;
	private TextView taskNumText;
	private View targetListItem;

	private int taskNum = 1;
	private int targetItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scrolling);

		redBoxImage = (ImageView) findViewById(R.id.redBoxImage);
		taskNumText = (TextView) findViewById(R.id.taskNum);
		inforLinearLayout = (LinearLayout) findViewById(R.id.ll);
		listView = (ListView) findViewById(R.id.listView);
		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				targetListItem = view.getChildAt(targetItem - firstVisibleItem);
			}
		});

		nextRound();
	}

	private boolean checkIfInBox() {
		if (targetListItem == null)
			return false;
		View targetImage = targetListItem.findViewById(R.id.listItemImage);
		if (targetImage == null)
			return false;

		float y1 = targetListItem.getY() + targetImage.getY()
				+ inforLinearLayout.getHeight();
		float y2 = y1 + targetImage.getHeight();

		return (redBoxImage.getY() <= y1 && y2 <= redBoxImage.getY()
				+ redBoxImage.getHeight());
	}

	private void nextRound() {
		taskNumText.setText("Task : " + taskNum++);

		new Thread(checkCorrect).start();
		List<Map<String, Integer>> data = new ArrayList<Map<String, Integer>>(
				LISTVIEW_SIZE);

		targetItem = 5 + Math
				.abs(new Random().nextInt() % (LISTVIEW_SIZE - 10));
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

	private Runnable checkCorrect = new Runnable() {

		final static long TASK_TIMEOUT = 15 * 1000;

		@Override
		public void run() {
			long taskStartTime = new Date().getTime();
			long lastTimeInBox = Long.MAX_VALUE;
			while (true) {

				if (taskStartTime + TASK_TIMEOUT < new Date().getTime()) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(ScrollingCaseActivity.this,
									"time out", Toast.LENGTH_SHORT).show();
							nextRound();
						}
					});
					return;
				}

				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (targetListItem != null && checkIfInBox() == true) {
					if (lastTimeInBox == Long.MAX_VALUE) {
						lastTimeInBox = new Date().getTime();
					}
				} else {
					lastTimeInBox = Long.MAX_VALUE;
				}
				long currentTime = new Date().getTime();
				if (currentTime - lastTimeInBox >= 500) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							nextRound();
							Toast.makeText(ScrollingCaseActivity.this,
									"correct", Toast.LENGTH_SHORT).show();
						}
					});
					return;
				}
			}
		}
	};
}
