package tw.edu.ntu.csie.mhci.tapassist.testing;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import tw.edu.ntu.csie.mhci.tapassist.R;
import tw.edu.ntu.csie.mhci.tapassist.utils.LogHelper;
import tw.edu.ntu.csie.mhci.tapassist.utils.Media;
import tw.edu.ntu.csie.mhci.tapassist.utils.PreferenceHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ScrollingCaseActivity extends Activity {

	private static long SINGLE_TASK_TIMEOUT = 60 * 1000;
	private static long ALL_TASK_TIMEOUT = 300 * 1000;
	private static long TOUCH_SLOP = 100;

	private final static long CORRECT_TIME_BOUND = 500;

	private final static int LISTVIEW_SIZE = 30;
	private final static int[] LISTVIEW_OFFSET = { 0, 400, 800 };

	// for some hack ...
	private static int VISIBLE_ITEM_COUNT = -1;

	private static boolean active = false;
	private static boolean isMoving = false;

	private RelativeLayout listViewContainerRelativeLayout;
	private ListView listView;
	private ImageView redBoxImage;
	private TextView taskNumText;
	private TextView timeCounterText;
	private View targetListItem;

	private Handler handler = new Handler();

	private int taskNum = -1;
	private int targetItem;

	private long startTime = 1;
	private Thread checkCorrectThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scrolling);

		redBoxImage = (ImageView) findViewById(R.id.redBoxImage);
		taskNumText = (TextView) findViewById(R.id.taskNumText);
		timeCounterText = (TextView) findViewById(R.id.timeCounterText);
		listViewContainerRelativeLayout = (RelativeLayout) findViewById(R.id.listViewContainer);

		listView = (ListView) findViewById(R.id.listView);
		listView.setOnScrollListener(listViewOnScrollListener);
		listView.setOnTouchListener(listViewOnTouchListener);

		// TODO(ggm) lazy to check
		try {
			SINGLE_TASK_TIMEOUT = Long.valueOf(PreferenceHelper.getString(this,
					"scrolling_single_task_timeout")) * 1000;
			ALL_TASK_TIMEOUT = Long.valueOf(PreferenceHelper.getString(this,
					"scrolling_all_task_timeout"));
			TOUCH_SLOP = Long.valueOf(PreferenceHelper.getString(this,
					"scrolling_touch_slop"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		createListView();
		handler.postDelayed(timeCounter, 1000);
	}

	@Override
	protected void onStart() {
		super.onStart();
		active = true;
	}

	@Override
	protected void onStop() {
		super.onStop();
		active = false;
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
				firstTouchX = firstTouchY = -1;
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

		taskNum++;
		Random random = new Random();

		listViewContainerRelativeLayout.setVisibility(View.VISIBLE);
		listViewContainerRelativeLayout
				.setX(LISTVIEW_OFFSET[random.nextInt(3)]);
		taskNumText.setText("Task : " + taskNum);

		checkCorrectThread = new Thread(checkCorrect);
		checkCorrectThread.start();
		List<Map<String, Integer>> data = new ArrayList<Map<String, Integer>>(
				LISTVIEW_SIZE);

		targetItem = 10 + random.nextInt(LISTVIEW_SIZE - 20);
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

		if (VISIBLE_ITEM_COUNT != -1) {
			setSelectionItem();
		}
	}

	private void setSelectionItem() {
		Random random = new Random();

		int offset = random.nextInt(VISIBLE_ITEM_COUNT);
		while (Math.abs(VISIBLE_ITEM_COUNT / 2 - offset) <= 2)
			offset = random.nextInt(VISIBLE_ITEM_COUNT);

		int selectionItem = targetItem - offset;
		listView.setSelection(selectionItem);

		JSONObject metedata = new JSONObject();
		try {
			metedata.put("targetSelection", targetItem);
			metedata.put("initialSelection", selectionItem);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		LogHelper.wirteLogTaskStart(this, "scrolling", taskNum, metedata);

		Log.d("debug", "targetItem=" + targetItem + ", selectionItem="
				+ selectionItem);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		checkCorrectThread.interrupt();
		Log.d("debug", "onDestory");
	}

	private Runnable checkCorrect = new Runnable() {

		@Override
		public void run() {
			long taskStartTime = new Date().getTime();
			long lastTimeInBox = Long.MAX_VALUE;
			while (true) {

				if (taskStartTime + SINGLE_TASK_TIMEOUT < new Date().getTime()) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(ScrollingCaseActivity.this,
									"time out", Toast.LENGTH_SHORT).show();

							JSONObject metadata = new JSONObject();
							try {
								metadata.put("result", "fail");
								metadata.put("reason", "timeout");
							} catch (JSONException e) {
								e.printStackTrace();
							}

							LogHelper.wirteLogTaskEnd(
									ScrollingCaseActivity.this, "scroll",
									taskNum, metadata);

							nextRound(500);
						}
					});
					return;
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					return;
				}

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
							Media.play(ScrollingCaseActivity.this, R.raw.right);

							JSONObject metadata = new JSONObject();
							try {
								metadata.put("result", "success");
							} catch (JSONException e) {
								e.printStackTrace();
							}

							LogHelper.wirteLogTaskEnd(
									ScrollingCaseActivity.this, "scroll",
									taskNum, metadata);
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
			if (active == false) {
				return;
			}

			timeCounterText.setText(startTime + " seconds");

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
						// TODO(ggm)
						try {
							dialog.show();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			} else {
				handler.postDelayed(this, 1000);
			}
		}
	};
	private float firstTouchX;
	private float firstTouchY;

	private OnTouchListener listViewOnTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {

			int action = event.getAction();

			float x = event.getRawX();
			float y = event.getRawY();

			switch (action) {
			case MotionEvent.ACTION_DOWN:
				isMoving = false;
				firstTouchX = x;
				firstTouchY = y;
				LogHelper.wirteLogTouchEvent(ScrollingCaseActivity.this, event,
						v, "");
				break;

			case MotionEvent.ACTION_MOVE:
				// ignore this case

				if (isMoving) {
					LogHelper.wirteLogTouchEvent(ScrollingCaseActivity.this,
							event, v, "is_moving");
				} else if (Math.abs(x - firstTouchX) < TOUCH_SLOP
						&& Math.abs(y - firstTouchY) < TOUCH_SLOP) {
					LogHelper.wirteLogTouchEvent(ScrollingCaseActivity.this,
							event, v, "in_slop");
					return true;
				} else {
					isMoving = true;
					LogHelper.wirteLogTouchEvent(ScrollingCaseActivity.this,
							event, v, "over_slop");
				}
				break;
			case MotionEvent.ACTION_UP:
				isMoving = false;
				if (Math.abs(x - firstTouchX) < TOUCH_SLOP
						&& Math.abs(y - firstTouchY) < TOUCH_SLOP) {
					Media.play(ScrollingCaseActivity.this, R.raw.miss);
				}
				LogHelper.wirteLogTouchEvent(ScrollingCaseActivity.this, event,
						v, "");
				break;
			default:
				LogHelper.wirteLogTouchEvent(ScrollingCaseActivity.this, event,
						v, "");
			}
			return false;
		}
	};

	private OnScrollListener listViewOnScrollListener = new OnScrollListener() {
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			if (visibleItemCount > 0) {
				if (VISIBLE_ITEM_COUNT == -1) {
					VISIBLE_ITEM_COUNT = visibleItemCount;
					setSelectionItem();
				}
			}
			targetListItem = view.getChildAt(targetItem - firstVisibleItem);
		}
	};
}
