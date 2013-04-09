package cz.cvut.rutkodan.bakalarka;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;
import cz.cvut.rutkodan.bakalarka.ui.MultilieLinearLayout;

public class CameraView extends ImageView {

	private CameraStream stream;
	private Timer timer = new Timer();
	private int width = 0;
	private int height = 0;
	private double fps = 0.5;
	private MultilieLinearLayout ml;

	public CameraView(Context context, CameraStream stream,
			MultilieLinearLayout ml) {
		super(context);
		this.stream = stream;
		this.ml = ml;
	}

	public CameraView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void loadNewImage() {
		new RunStream().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	@Override
	protected void onAttachedToWindow() {
		TimerTask task = new Update();
		timer.cancel();
		timer = new Timer();
		timer.schedule(task, Math.round(1000 / fps));
		super.onAttachedToWindow();
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
			if (result != null
					&& (result.getWidth() != width || result.getHeight() != height)) {
				width = result.getWidth();
				height = result.getHeight();
				ml.recreate();
			}
		}
	}

	private class Update extends TimerTask {

		@Override
		public void run() {
			new RunStream().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			TimerTask task = new Update();
			timer.schedule(task, Math.round(1000 / fps));
		}

	}

	public void stop() {
		timer.cancel();
	}

}
