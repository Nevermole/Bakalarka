package cz.cvut.rutkodan.bakalarka;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

public class CameraView extends ImageView {

	private CameraStream stream;
	private Timer timer = new Timer();
	private int width = 0;
	private int height = 0;
	private int fps = 1;

	public CameraView(Context context, CameraStream stream) {
		super(context);
		this.stream = stream;
		TimerTask task = new Update();
		timer.schedule(task, 1000 / fps);
	}

	public CameraView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void loadNewImage() {
		new RunStream().execute();
	}

	@Override
	protected void onDetachedFromWindow() {
		timer.cancel();
		super.onDetachedFromWindow();
	}

	private class RunStream extends AsyncTask<Void, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(Void... params) {
			Bitmap bm = stream.getData();
			return bm;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			setImageBitmap(result);
			if (result.getWidth() != width || result.getHeight() != height) {

			}
		}

	}

	private class Update extends TimerTask {

		@Override
		public void run() {
			new RunStream().execute();
			TimerTask task = new Update();
			timer.schedule(task, 1000 / fps);
		}

	}

	public void stop() {
		timer.cancel();
	}

}
