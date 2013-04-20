package cz.cvut.rutkodan.bakalarka;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.widget.ImageView;
import cz.cvut.rutkodan.bakalarka.ui.MultilieLinearLayout;

public class CameraView extends ImageView {

	private CameraStream stream;
	private Timer timer;
	private int width = 0;
	private int height = 0;
	private double fps = 0.5;
	private MultilieLinearLayout ml;	
	private long time = 0;
	private boolean hasFinished = true;

	
	public CameraView(Context context, CameraSettings cameraSettings, MultilieLinearLayout ml) {
		super(context);
		this.stream = new CameraStream(cameraSettings.getAddress());
		this.width = cameraSettings.getWidth();
		this.height = cameraSettings.getHeight();
		//this.fps = fps;
		this.ml = ml;
		Bitmap bm = Bitmap.createBitmap(width,height,Config.ARGB_8888);
		Canvas c = new Canvas(bm);
		c.drawColor(Color.BLACK);
		Paint p = new Paint();
		p.setStyle(Paint.Style.FILL);		
		p.setColor(Color.WHITE);
		p.setTextSize(25);
		c.drawText(cameraSettings.getName(),50, 50, p);
		setImageBitmap(bm);
		TimerTask task = new Update();
		timer = new Timer();
		timer.schedule(task, 100);
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
			hasFinished = true;
			if (result != null) {
				/*
				 * long now = new Date().getTime(); System.out.println(Double
				 * .toString(1000 / (double) (now - time))); time = now;
				 */
				setImageBitmap(result);
				if ((result.getWidth() != width || result.getHeight() != height)) {
					width = result.getWidth();
					height = result.getHeight();
					ml.recreate();
				}
			}
		}
	}

	private class Update extends TimerTask {

		@Override
		public void run() {
			TimerTask task = new Update();
			timer = new Timer();
			if (hasFinished) {
				new RunStream()
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				timer.schedule(task, Math.round(1000 / fps));
			} else {
				timer.schedule(task, 100);
			}

		}

	}

	public void stop() {
		timer.cancel();
	}

	public double getFps() {
		return fps;
	}

	public void setFps(double fps) {
		this.fps = fps;
	}
}
