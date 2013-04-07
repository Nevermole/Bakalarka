package cz.cvut.rutkodan.bakalarka;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

public class CameraView extends ImageView {

	CameraStream stream;	

	public CameraView(Context context, CameraStream stream) {
		super(context);
		this.stream = stream;		
		Timer timer = new Timer();
		int fps = 3;
		TimerTask task = new Update();
		timer.scheduleAtFixedRate(task, 0, 1000/fps);
	}

	public CameraView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void loadNewImage() {
		new RunStream().execute();
	}

	private class RunStream extends AsyncTask<Void, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(Void... params) {
			stream.startCamera();
			Bitmap bm = stream.getData();
			return bm;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			setImageBitmap(result);			
		}

	}
	
	private class Update extends TimerTask{

		@Override
		public void run() {
			new RunStream().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);			
		}
		
	}
}
