package tw.edu.ntu.csie.mhci.tapassist.utils;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

public class Media {

	public static void play(Context context, String fileStr) {

		// try {
		// AssetFileDescriptor descriptor = context.getAssets()
		// .openFd(fileStr);
		// MediaPlayer meidaPlayer = new MediaPlayer();
		// meidaPlayer.setDataSource(descriptor.getFileDescriptor(),
		// descriptor.getStartOffset(), descriptor.getLength());
		// descriptor.close();
		//
		// } catch (IOException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		try {
			AssetFileDescriptor afd = context.getAssets().openFd(fileStr);
			MediaPlayer meidaPlayer = new MediaPlayer();
			meidaPlayer.setDataSource(afd.getFileDescriptor(),
					afd.getStartOffset(), afd.getLength());
			afd.close();
			meidaPlayer.prepare();
			meidaPlayer.start();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
