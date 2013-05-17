package cz.cvut.rutkodan.bakalarka.activities;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import cz.cvut.rutkodan.bakalarka.CameraList;
import cz.cvut.rutkodan.bakalarka.CameraSettings;
import cz.cvut.rutkodan.bakalarka.R;
import cz.cvut.rutkodan.bakalarka.RequestCodes;
import cz.cvut.rutkodan.bakalarka.connection.Type;
import cz.cvut.rutkodan.bakalarka.ui.MultilieLinearLayout;

public class MainActivity extends Activity {
	private CameraList kamery;
	private MultilieLinearLayout ml;
	private UpdateHandler handler = new UpdateHandler();
	public static long dataUsed = 0;
	private Timer updater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		/*
		 * VideoView videoView = (VideoView) findViewById(R.id.videoView1);
		 * MediaController mc = new MediaController(this);
		 * videoView.setMediaController(mc);
		 * videoView.setVideoURI(Uri.parse("rtsp://81.25.30.20:554/live4.sdp"));
		 * ImageView imageView = (ImageView) findViewById(R.id.imageView1);
		 * imageView.setImageURI(Uri.parse("rtsp://81.25.30.20:554/live3.sdp"));
		 */
		final Intent intentAddCamera = new Intent(this, CameraAddActivity.class);
		intentAddCamera.putExtra("request", RequestCodes.ADD_NEW_CAMERA);
		ImageButton imageButton = (ImageButton) findViewById(R.id.button_add_camera);
		imageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivityForResult(intentAddCamera,
						RequestCodes.ADD_NEW_CAMERA.getNumber());
			}
		});
		final Intent intentManageCameras = new Intent(this,
				ManageCamerasActivity.class);
		imageButton = (ImageButton) findViewById(R.id.button_manage_cameras);
		imageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivityForResult(intentManageCameras,
						RequestCodes.MANAGE_CAMERAS.getNumber());
			}
		});

		final Intent intentRunTest = new Intent(this, Test.class);
		imageButton = (ImageButton) findViewById(R.id.run_test);
		imageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(intentRunTest);
			}
		});
		imageButton = (ImageButton) findViewById(R.id.button_recreate);
		imageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				fillLayout();
			}
		});
		kamery = new CameraList(this);
		fillLayout();
		// kameryURL.add("http://160.218.184.211:5001/axis-cgi/mjpg/video.cgi?resolution=CIF&camera=1");
		// kameryURL.add("http://89.24.105.222:5001/axis-cgi/mjpg/video.cgi?resolution=CIF&camera=1");
		// kameryURL.add("http://89.24.105.226:5001/axis-cgi/mjpg/video.cgi?resolution=CIF&camera=1");
		// kameryURL.add("http://160.218.189.228:5001/axis-cgi/mjpg/video.cgi?resolution=CIF&camera=1");
		// kameryURL.add("http://109.107.218.33:5001/axis-cgi/mjpg/video.cgi?resolution=CIF&camera=1");
		// kameryURL.add("http://85.207.84.10:5001/axis-cgi/mjpg/video.cgi?resolution=CIF&camera=1");
		// kameryURL.add("http://85.207.85.13:5001/video3.mjpg");
		// kameryURL.add("http://81.25.30.20:5001/video3.mjpg");

		updater = new Timer();
		updater.scheduleAtFixedRate(new UpdateDataCounter(), 1000, 1000);
	}

	private void fillLayout() {
		ml = new MultilieLinearLayout(this);
		ml.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		ml.setGravity(Gravity.TOP);
		ml.setOrientation(LinearLayout.VERTICAL);
		ScrollView scrollView = (ScrollView) findViewById(R.id.main_scrollview);
		scrollView.removeAllViews();
		scrollView.addView(ml);
		kamery.loadFromDB();
		for (CameraSettings cam : kamery.getAllCameras()) {
			// CameraView cameraView = new CameraView(this, cam, ml);
			// ml.addView(cameraView);
		}
	}

	public void updateData() {
		TextView dataView = (TextView) findViewById(R.id.data_view);
		String used = Double.toString(dataUsed / 1000000.0);
		dataView.setText((used.length() > 3 ? used.substring(0,
				used.indexOf(".") + 2) : used)
				+ " MB");
		System.out.println("updated");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RequestCodes.ADD_NEW_CAMERA.getNumber()) {
			if (resultCode == RESULT_OK) {
				System.out.println("added new cam");
				CameraSettings cam = new CameraSettings(
						data.getStringExtra("Name"),
						data.getStringExtra("Address"), data.getIntExtra(
								"Height", 0), data.getIntExtra("Width", 0),
						data.getDoubleExtra("FPS", 5.0));
				kamery.add(cam);
				// CameraView cameraView = new CameraView(this, cam, ml);
				// ml.addView(cameraView);
			}
		} else if (requestCode == RequestCodes.MANAGE_CAMERAS.getNumber()) {
			if (resultCode == RESULT_OK) {
				if (data.getBooleanExtra("Edited", false)) {
					fillLayout();
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		updater.cancel();
		super.onDestroy();
	}

	@SuppressLint("HandlerLeak")
	private class UpdateHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			updateData();
		}

	}

	private class UpdateDataCounter extends TimerTask {

		@Override
		public void run() {
			handler.sendMessage(new Message());
		}

	}
}
