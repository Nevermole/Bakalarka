package cz.cvut.rutkodan.bakalarka;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import cz.cvut.rutkodan.bakalarka.connection.Type;
import cz.cvut.rutkodan.bakalarka.ui.MultilieLinearLayout;

public class MainActivity extends Activity {
	private static CameraList kamery;
	private MultilieLinearLayout ml;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Intent intent = new Intent(this, CameraAddActivity.class);
		setContentView(R.layout.activity_main);
		ImageButton imageButton = (ImageButton) findViewById(R.id.add_camera_button);
		imageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivityForResult(intent, 0);
			}
		});
		kamery = new CameraList(this);
		ml = (MultilieLinearLayout) findViewById(R.id.multilineLinearLayout);
		kamery.loadFromDB();		
		// kameryURL
		// .add("http://160.218.184.211:5001/axis-cgi/mjpg/video.cgi?resolution=CIF&camera=1");
		// kameryURL
		// .add("http://89.24.105.222:5001/axis-cgi/mjpg/video.cgi?resolution=CIF&camera=1");
		// kameryURL
		// .add("http://89.24.105.226:5001/axis-cgi/mjpg/video.cgi?resolution=CIF&camera=1");
		// kameryURL
		// .add("http://160.218.189.228:5001/axis-cgi/mjpg/video.cgi?resolution=CIF&camera=1");
		// kameryURL
		// .add("http://109.107.218.33:5001/axis-cgi/mjpg/video.cgi?resolution=CIF&camera=1");
		// kameryURL
		// .add("http://85.207.84.10:5001/axis-cgi/mjpg/video.cgi?resolution=CIF&camera=1");

		// kameryURL.add("http://85.207.85.13:5001/video3.mjpg");
		// kameryURL.add("http://81.25.30.20:5001/video3.mjpg");
		for (CameraSettings cam : kamery.getAllCameras()) {
			CameraView cameraView = new CameraView(this, new CameraStream(
					cam.getAddress()), cam.getWidth(), cam.getHeight(),
					cam.getMaxFPS(), ml);
			/*
			 * camimage.setOnClickListener(new OnClickListener() {
			 * 
			 * @Override public void onClick(View v) { ((CameraView)
			 * v).loadNewImage(); } });
			 */
			ml.addView(cameraView);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				CameraSettings cam = new CameraSettings(Type.HTTP,
						data.getStringExtra("Name"),
						data.getStringExtra("Address"), data.getIntExtra(
								"Height", 0), data.getIntExtra("Width", 0),
						data.getDoubleExtra("FPS", 5.0));
				kamery.add(cam);
				CameraView cameraView = new CameraView(this, new CameraStream(
						cam.getAddress()), cam.getWidth(), cam.getHeight(),
						cam.getMaxFPS(), ml);
				ml.addView(cameraView);

			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onBackPressed() {
		/*
		 * for (CameraView cameraView : kamery) { cameraView.stop(); }
		 */
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		/*
		 * for (CameraView cameraView : kamery) { cameraView.stop(); }
		 */
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
