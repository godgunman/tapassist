package tw.edu.ntu.csie.mhci.tapassist.testing;

import java.util.Random;

import tw.edu.ntu.csie.mhci.tapassist.R;
import tw.edu.ntu.csie.mhci.tapassist.utils.Layout;
import tw.edu.ntu.csie.mhci.tapassist.utils.LogHelper;
import tw.edu.ntu.csie.mhci.tapassist.utils.Media;
import tw.edu.ntu.csie.mhci.tapassist.utils.PreferenceHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TappingCaseActivity extends Activity {

	private static long ALL_TASK_TIMEOUT = 300 * 1000;
	private static long TOUCH_SLOP = 8;

	private RelativeLayout outerTapImage;
	private ImageView tapImage;
	private TextView taskNumText;
	private TextView timeCounterText;

	private Handler handler = new Handler();

	private int taskNum = 0;
	private long startTime = 1;

	private boolean isTouchAvailable;
	private boolean isTouchMove;

	private float firstTouchX;
	private float firstTouchY;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tapping);

		outerTapImage = (RelativeLayout) findViewById(R.id.outerTapImage);
		tapImage = (ImageView) findViewById(R.id.tapImage);
		taskNumText = (TextView) findViewById(R.id.taskNumText);
		timeCounterText = (TextView) findViewById(R.id.timeCounterText);

		outerTapImage.setOnTouchListener(outerTapImageTouchListener);
		tapImage.setOnTouchListener(tapImageTouchListener);

		try {
			ALL_TASK_TIMEOUT = Long.valueOf(PreferenceHelper.getString(this,
					"tapping_all_task_timeout"));
			TOUCH_SLOP = Long.valueOf(PreferenceHelper.getString(this,
					"tapping_touch_slop"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		handler.postDelayed(timeCounter, 1000);
		createTargetImage();
	}

	private void nextTask() {

		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPreExecute() {
				try {
					Thread.sleep(700);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				tapImage.setImageResource(R.drawable.face_normal);
				tapImage.setVisibility(View.GONE);
				isTouchAvailable = false;
				isTouchMove = false;
				firstTouchX = firstTouchY = -1;
			}

			@Override
			protected void onPostExecute(Void result) {
				createTargetImage();
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

	private void fail() {
//		LogHelper.wirteLogTask(this, event, action, task);
	}
	
	private void success() {
		Media.play(TappingCaseActivity.this, R.raw.right);
//		LogHelper.wirteLogTask(this, event, action, task);
	}
	
	private void createTargetImage() {
		Random r = new Random();
		float x = 50 + r.nextFloat() * (outerTapImage.getWidth() - 200);
		float y = 50 + r.nextFloat()
				* (outerTapImage.getHeight() - 200);

		tapImage.setX(x);
		tapImage.setY(y);
		tapImage.setVisibility(View.VISIBLE);

		taskNumText.setText("Task " + taskNum++);

		isTouchAvailable = true;
	}

	private OnTouchListener tapImageTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {

			if (event.getPointerCount() > 1) {
				Toast.makeText(TappingCaseActivity.this, "multi touch",
						Toast.LENGTH_SHORT).show();
				return false;
			}

			LogHelper.wirteLogTouchEvent(TappingCaseActivity.this, event);

			int action = event.getAction();

			float x = event.getRawX();
			float y = event.getRawY();

			switch (action) {
			case MotionEvent.ACTION_DOWN:
				Media.play(TappingCaseActivity.this, R.raw.hit);
				Log.d("TappingCaseActivity", "tapImage:ACTION_DOWN");
				tapImage.setImageResource(R.drawable.face_hit);

				firstTouchX = x;
				firstTouchY = y;

				break;

			case MotionEvent.ACTION_MOVE:

				// ignore this case
				if (Math.abs(x - firstTouchX) < TOUCH_SLOP
						&& Math.abs(y - firstTouchY) < TOUCH_SLOP) {
					return true;
				}

				// first move
				if (isTouchMove == false) {
					Media.play(TappingCaseActivity.this, R.raw.slip);
				}

				isTouchMove = true;
				Log.d("TappingCaseActivity", "tapImage:ACTION_MOVE");
				tapImage.setImageResource(R.drawable.face_normal);
				tapImage.setX(x - tapImage.getWidth() / 2);
				tapImage.setY(y - tapImage.getHeight() / 2
						- Layout.getStatusBar(TappingCaseActivity.this));
				break;

			case MotionEvent.ACTION_UP:
				Log.d("TappingCaseActivity", "tapImage:ACTION_UP");
				if (isTouchMove == false) {
					success();
				} else {
					fail();
				}
				tapImage.setImageResource(R.drawable.face_normal);
				nextTask();
				break;

			case MotionEvent.ACTION_CANCEL:
				Log.d("TappingCaseActivity", "tapImage:ACTION_CANCEL");
				tapImage.setImageResource(R.drawable.face_normal);
				break;

			case MotionEvent.ACTION_SCROLL:
				Log.d("TappingCaseActivity", "tapImage:ACTION_SCROLL");
				break;
			}

			return true;
		}
	};

	private OnTouchListener outerTapImageTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {

			if (isTouchAvailable == false) {
				return false;
			}

			LogHelper.wirteLogTouchEvent(TappingCaseActivity.this, event);
			
			int action = event.getAction();
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				Media.play(TappingCaseActivity.this, R.raw.miss);
				Log.d("TappingCaseActivity", "outerTapImage:ACTION_DOWN");
				tapImage.setImageResource(R.drawable.face_bad);
				break;

			case MotionEvent.ACTION_MOVE:
				Log.d("TappingCaseActivity", "outerTapImage:ACTION_MOVE");
				tapImage.setImageResource(R.drawable.face_bad);
				break;

			case MotionEvent.ACTION_UP:
				Log.d("TappingCaseActivity", "outerTapImage:ACTION_UP");
				tapImage.setImageResource(R.drawable.face_normal);
				fail();
				nextTask();
				break;

			case MotionEvent.ACTION_CANCEL:
				Log.d("TappingCaseActivity", "outerTapImage:ACTION_CANCEL");
				tapImage.setImageResource(R.drawable.face_normal);
				break;

			case MotionEvent.ACTION_SCROLL:
				Log.d("TappingCaseActivity", "outerTapImage:ACTION_SCROLL");
				break;
			}
			return true;
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
								TappingCaseActivity.this);
						builder.setTitle("你已經完成了測試囉!!");
						builder.setPositiveButton("好",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										TappingCaseActivity.this.finish();
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
