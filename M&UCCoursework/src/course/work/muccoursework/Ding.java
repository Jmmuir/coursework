package course.work.muccoursework;

import android.content.Context;
import android.media.MediaPlayer;

public class Ding implements Runnable{
	
	//a class used to create a "ding" sound when a new location is unlocked.
	
	private Context context;

	public Ding(Context context) {
		this.context = context;
	}

	@Override
	public void run() {
		MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.ding);
		mediaPlayer.start();
		return;
	}

}
