package cz.cvut.rutkodan.ipcameramonitor.ui;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.SurfaceHolder;
import android.widget.VideoView;
import cz.cvut.rutkodan.ipcameramonitor.CameraSettings;
import cz.cvut.rutkodan.ipcameramonitor.CameraStream;
import cz.cvut.rutkodan.ipcameramonitor.connection.Type;

public class CameraView extends VideoView implements SurfaceHolder.Callback {

	private Type type;
	private CameraStream stream;
	private Timer timer;
	private String name;
	private String address;
	private int width = 0;
	private int height = 0;
	private double fps = 0.5;
	// private long time = 0;
	private boolean canceled = true;
	private boolean hasFinished = true;

	@SuppressLint("DefaultLocale")
	public CameraView(Context context, CameraSettings cameraSettings) {
		super(context);
		this.address = cameraSettings.getAddress();
		this.name = cameraSettings.getName();
		this.stream = new CameraStream(cameraSettings.getAddress());
		this.width = cameraSettings.getWidth();
		this.height = cameraSettings.getHeight();
		if (address.toLowerCase().startsWith("http")) {
			this.type = Type.HTTP;
		} else {
			this.type = Type.RTSP;
			setVideoURI(Uri.parse(address));
		}
		getHolder().addCallback(this);
		System.err.println(type);
	}

	public CameraView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void loadNewImage() {
		new GetMJPEGData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	@Override
	protected void onDetachedFromWindow() {
		System.out.println("detached");
		pause();
		super.onDetachedFromWindow();
	}

	private class GetMJPEGData extends AsyncTask<Void, Void, Bitmap> {

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
				new GetMJPEGData()
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				timer.schedule(task, Math.round(1000 / fps));
			} else {
				timer.schedule(task, 100);
			}
		}

	}

	public void pause() {
		if (type.equals(Type.RTSP)) {
			stopPlayback();
		} else {
			if (!canceled) {
				timer.cancel();
			}
			canceled = true;
		}
	}

	public void play(SurfaceHolder holder) {
		if (type.equals(Type.RTSP)) {
			start();
		} else {
			if (!canceled) {
				timer.cancel();
			}
			timer = new Timer();
			hasFinished = true;
			canceled = false;
			TimerTask task = new Update();
			timer.schedule(task, Math.round(1000 / fps));
		}
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
		if (type.equals(Type.HTTP)) {
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
		play(holder);
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
