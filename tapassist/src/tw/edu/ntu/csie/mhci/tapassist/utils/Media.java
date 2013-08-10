package tw.edu.ntu.csie.mhci.tapassist.utils;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class Media {

	public static void play(Context context, int resId) {

		try {
			MediaPlayer meidaPlayer = MediaPlayer.create(context, resId);
//			meidaPlayer.prepare();
			meidaPlayer.start();
			meidaPlayer.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					mp.release();
				}
			});
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}

}
