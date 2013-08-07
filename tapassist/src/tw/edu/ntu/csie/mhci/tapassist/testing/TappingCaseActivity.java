package tw.edu.ntu.csie.mhci.tapassist.testing;

import tw.edu.ntu.csie.mhci.tapassist.R;
import tw.edu.ntu.csie.mhci.tapassist.utils.Layout;
import tw.edu.ntu.csie.mhci.tapassist.utils.Media;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class TappingCaseActivity extends Activity {

	private RelativeLayout outerTapImage;
	private ImageView tapImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tapping);

		outerTapImage = (RelativeLayout) findViewById(R.id.outerTapImage);
		tapImage = (ImageView) findViewById(R.id.tapImage);
		
		outerTapImage.setOnTouchListener(outerTapImageTouchListener);
		tapImage.setOnTouchListener(tapImageTouchListener);
	}

	private OnTouchListener tapImageTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {

			if (event.getPointerCount() > 1) {
				Toast.makeText(TappingCaseActivity.this, "multi touch",
						Toast.LENGTH_SHORT).show();
				return false;
			}

			int action = event.getAction();

			float x = event.getRawX();
			float y = event.getRawY();

			switch (action) {
			case MotionEvent.ACTION_DOWN:
				Media.play(TappingCaseActivity.this, "hit.mp3");
				Log.d("TappingCaseActivity", "tapImage:ACTION_DOWN");
				tapImage.setImageResource(R.drawable.face_hit);
				break;
			case MotionEvent.ACTION_MOVE:
				Log.d("TappingCaseActivity", "tapImage:ACTION_MOVE");
				tapImage.setX(x - tapImage.getWidth() / 2);
				tapImage.setY(y - tapImage.getHeight() / 2
						- Layout.getStatusBar(TappingCaseActivity.this));
				break;
			case MotionEvent.ACTION_UP:
				Media.play(TappingCaseActivity.this, "right.mp3");
				Log.d("TappingCaseActivity", "tapImage:ACTION_UP");
				tapImage.setImageResource(R.drawable.face_normal);
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
			int action = event.getAction();
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				Media.play(TappingCaseActivity.this, "miss.mp3");
				Log.d("TappingCaseActivity", "outerTapImage:ACTION_DOWN");
				tapImage.setImageResource(R.drawable.face_bad);
				break;
			case MotionEvent.ACTION_MOVE:
				Media.play(TappingCaseActivity.this, "miss.mp3");
				Log.d("TappingCaseActivity", "outerTapImage:ACTION_MOVE");
				tapImage.setImageResource(R.drawable.face_bad);
				break;
			case MotionEvent.ACTION_UP:
				Log.d("TappingCaseActivity", "outerTapImage:ACTION_UP");
				tapImage.setImageResource(R.drawable.face_normal);
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
}
