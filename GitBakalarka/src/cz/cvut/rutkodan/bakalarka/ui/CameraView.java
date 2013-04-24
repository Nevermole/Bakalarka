package cz.cvut.rutkodan.bakalarka.ui;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import cz.cvut.rutkodan.bakalarka.CameraSettings;
import cz.cvut.rutkodan.bakalarka.CameraStream;

public class CameraView extends ImageView {

	private CameraStream stream;
	private Timer timer;
	private String name;
	private int width = 0;
	private int height = 0;
	private double fps = 0.5;
	private MultilieLinearLayout ml;
	// private long time = 0;
	private boolean canceled = false;
	private boolean hasFinished = true;

	public CameraView(Context context, CameraSettings cameraSettings,
			MultilieLinearLayout ml) {
		super(context);
		this.name = cameraSettings.getName();
		this.stream = new CameraStream(cameraSettings.getAddress());
		this.width = cameraSettings.getWidth();
		this.height = cameraSettings.getHeight();
		setLayoutParams(new LayoutParams(width, height));
		// this.fps = fps;
		this.ml = ml;
		Bitmap bm = Bitmap.createBitmap(width, height, Config.ARGB_8888);		
		Canvas c = new Canvas(bm);
		c.drawColor(Color.BLACK);
		Paint p = new Paint();
		p.setStyle(Paint.Style.FILL);
		p.setColor(Color.WHITE);
		p.setTextSize(25);
		c.drawText(cameraSettings.getName(), 50, 50, p);
		setImageBitmap(bm);
		TimerTask task = new Update();
		timer = new Timer();
		timer.schedule(task, Math.round((Math.random()*1000)));
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
		if (!canceled) {
			timer.cancel();
		}
		timer = new Timer();
		canceled = false;
		timer.schedule(task, Math.round(1000 / fps));
		super.onAttachedToWindow();
	}

	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		if (visibility == VISIBLE) {
			System.out.println(name + " visible");
			TimerTask task = new Update();
			if (!canceled) {
				timer.cancel();
			}
			timer = new Timer();
			canceled = false;
			timer.schedule(task, Math.round(1000 / fps));
		} else if (visibility == INVISIBLE) {
			System.out.println(name + " invisible");
			if (!canceled) {
				timer.cancel();
			}
			canceled = true;
		}
		super.onVisibilityChanged(changedView, visibility);
	}

	@Override
	protected void onDetachedFromWindow() {
		System.out.println("detached");
		if (!canceled) {
			timer.cancel();
		}
		canceled = true;
		super.onDetachedFromWindow();
	}

	private class RunStream extends AsyncTask<Void, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(Void... params) {
			Bitmap bm = null;
			try {
				bm = stream.getData();
			} catch (Exception e) {
				System.err.println("Failed to load image on " + name);
			}
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
					setLayoutParams(new LayoutParams(width, height));
					ml.recreate();
				}
			} else {
				System.err.println("Failed to load image on " + name);
			}
		}
	}

	private class Update extends TimerTask {

		@Override
		public void run() {
			TimerTask task = new Update();
			timer = new Timer();
			canceled = false;
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
		if (!canceled) {
			timer.cancel();
		}
		canceled = true;
	}

	public double getFps() {
		return fps;
	}

	public void setFps(double fps) {
		this.fps = fps;
	}
}
