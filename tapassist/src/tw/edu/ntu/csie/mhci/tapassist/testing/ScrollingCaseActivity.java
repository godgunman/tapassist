package tw.edu.ntu.csie.mhci.tapassist.testing;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import tw.edu.ntu.csie.mhci.tapassist.R;
import tw.edu.ntu.csie.mhci.tapassist.utils.Sleep;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ScrollingCaseActivity extends Activity {

	final static long ONE_TASK_TIMEOUT = 60 * 1000;
	final static long ALL_TASK_TIMEOUT = 300 * 1000;
	final static long CORRECT_TIME_BOUND = 500;

	private final static int LISTVIEW_SIZE = 30;
	private final static int[] LISTVIEW_OFFSET = { 0, 400, 800 };

	private RelativeLayout listViewContainerRelativeLayout;
	private ListView listView;
	private ImageView redBoxImage;
	private TextView taskNumText;
	private TextView timeCounterText;
	private View targetListItem;

	private Handler handler = new Handler();

	private int taskNum = 1;
	private int targetItem;

	private long startTime = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scrolling);

		redBoxImage = (ImageView) findViewById(R.id.redBoxImage);
		taskNumText = (TextView) findViewById(R.id.taskNumText);
		timeCounterText = (TextView) findViewById(R.id.timeCounterText);
		listViewContainerRelativeLayout = (RelativeLayout) findViewById(R.id.listViewContainer);

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

		createListView();
		handler.postDelayed(timeCounter, 1000);
	}

	private boolean checkIfInBox() {
		if (targetListItem == null)
			return false;
		View targetImage = targetListItem.findViewById(R.id.listItemImage);
		if (targetImage == null)
			return false;

		float y1 = targetListItem.getY() + targetImage.getY();
		float y2 = y1 + targetImage.getHeight();

		return (redBoxImage.getY() <= y1 && y2 <= redBoxImage.getY()
				+ redBoxImage.getHeight());
	}

	private void nextRound(final long ms) {

		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPreExecute() {
				try {
					Thread.sleep(ms);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				listViewContainerRelativeLayout.setVisibility(View.GONE);
			}

			@Override
			protected void onPostExecute(Void result) {
				createListView();
			}

			@Override
			protected Void doInBackground(Void... params) {
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return null;
			}
		};
		task.execute();

	}

	private void createListView() {

		Random random = new Random();

		listViewContainerRelativeLayout.setVisibility(View.VISIBLE);
		listViewContainerRelativeLayout
				.setX(LISTVIEW_OFFSET[random.nextInt(3)]);
		taskNumText.setText("Task : " + taskNum++);

		new Thread(checkCorrect).start();
		List<Map<String, Integer>> data = new ArrayList<Map<String, Integer>>(
				LISTVIEW_SIZE);

		targetItem = 5 + random.nextInt(LISTVIEW_SIZE - 10);
		int selectionItem = targetItem - random.nextInt(5);
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
		listView.setSelection(selectionItem);

		Log.d("debug", "targetItem=" + targetItem + ", selectionItem="
				+ selectionItem);
	}

	private Runnable checkCorrect = new Runnable() {

		@Override
		public void run() {
			long taskStartTime = new Date().getTime();
			long lastTimeInBox = Long.MAX_VALUE;
			while (true) {

				if (taskStartTime + ONE_TASK_TIMEOUT < new Date().getTime()) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(ScrollingCaseActivity.this,
									"time out", Toast.LENGTH_SHORT).show();
							Sleep.sleep(500);
							createListView();
						}
					});
					return;
				}

				Sleep.sleep(100);

				if (targetListItem != null && checkIfInBox() == true) {
					if (lastTimeInBox == Long.MAX_VALUE) {
						lastTimeInBox = new Date().getTime();
					}
				} else {
					lastTimeInBox = Long.MAX_VALUE;
				}
				long currentTime = new Date().getTime();
				if (currentTime - lastTimeInBox >= CORRECT_TIME_BOUND) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(ScrollingCaseActivity.this,
									"correct", Toast.LENGTH_SHORT).show();
							nextRound(500);
						}
					});
					return;
				}
			}
		}
	};

	private Runnable timeCounter = new Runnable() {

		@Override
		public void run() {
			startTime++;
			timeCounterText.setText(startTime + " seconds");
			handler.postDelayed(this, 1000);

			if (startTime == ALL_TASK_TIMEOUT) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								ScrollingCaseActivity.this);
						builder.setTitle("你已經完成了測試囉!!");
						builder.setPositiveButton("好", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								ScrollingCaseActivity.this.finish();
							}
						});

						AlertDialog dialog = builder.create();
						dialog.show();

					}
				});
			}
		}
	};
}
