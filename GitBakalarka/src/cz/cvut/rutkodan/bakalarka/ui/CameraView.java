package cz.cvut.rutkodan.bakalarka.ui;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import cz.cvut.rutkodan.bakalarka.CameraSettings;
import cz.cvut.rutkodan.bakalarka.CameraStream;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {

	private CameraStream stream;
	private Timer timer;
	private String name;
	private int width = 0;
	private int height = 0;
	private double fps = 0.5;
	// private long time = 0;
	private boolean canceled = true;
	private boolean hasFinished = true;

	public CameraView(Context context, CameraSettings cameraSettings) {
		super(context);
		this.name = cameraSettings.getName();
		this.stream = new CameraStream(cameraSettings.getAddress());
		this.width = cameraSettings.getWidth();
		this.height = cameraSettings.getHeight();
		getHolder().addCallback(this);
	}

	public CameraView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		System.err.println("onDraw" + name);
		super.onDraw(canvas);
	}

	public void loadNewImage() {
		new RunStream().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
		protected void onCancelled() {
			hasFinished = true;
			super.onCancelled();
		}

		@Override
		protected void onPreExecute() {
			hasFinished = false;
			super.onPreExecute();
		}

		@Override
		protected Bitmap doInBackground(Void... params) {
			Bitmap bm = null;
			try {
				bm = stream.getData();
			} catch (Exception e) {
				// System.err.println("Failed to load image on " + name);
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
				Canvas c = getHolder().lockCanvas();
				if (c != null) {
					c.drawBitmap(result, 0, 0, null);
					getHolder().unlockCanvasAndPost(c);
				}
				if ((result.getWidth() != width || result.getHeight() != height)) {
					width = result.getWidth();
					height = result.getHeight();
					// setLayoutParams(new LayoutParams(width, height));
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

	public void pause() {
		if (!canceled) {
			timer.cancel();
		}
		canceled = true;
	}

	public void play() {
		if (!canceled) {
			timer.cancel();
		}
		timer = new Timer();
		hasFinished = true;
		canceled = false;
		TimerTask task = new Update();
		timer.schedule(task, Math.round(1000 / fps));
	}

	public double getFps() {
		return fps;
	}

	public void setFps(double fps) {
		this.fps = fps;
	}

	
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		System.err.println("surface created");
		// Bitmap bm = Bitmap.createBitmap(width, height, Config.ARGB_8888);		
		Canvas c = holder.lockCanvas();
		// c.drawBitmap(bm, 0, 0, null);
		c.drawColor(Color.BLACK);
		Paint p = new Paint();
		p.setStyle(Paint.Style.FILL);
		p.setColor(Color.WHITE);
		p.setTextSize(25);
		c.drawText(name, 50, 50, p);
		holder.unlockCanvasAndPost(c);

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		System.err.println("surface destroyed");
		pause();
	}

	public String getName() {
		return name;
	}
}
