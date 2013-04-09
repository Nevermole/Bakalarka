package cz.cvut.rutkodan.bakalarka;

import java.util.ArrayList;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.VideoView;
import cz.cvut.rutkodan.bakalarka.ui.MultilieLinearLayout;

public class MainActivity extends Activity {
	public String s;
	private static ArrayList<String> kameryURL = new ArrayList<String>();
	private ArrayList<CameraView> kamery = new ArrayList<CameraView>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		MultilieLinearLayout ml = (MultilieLinearLayout) findViewById(R.id.MultilineLinearLayout);
		if (kameryURL.isEmpty()) {
			kameryURL
					.add("http://160.218.184.211:5001/axis-cgi/mjpg/video.cgi?resolution=CIF&camera=1");
			kameryURL
					.add("http://89.24.105.222:5001/axis-cgi/mjpg/video.cgi?resolution=CIF&camera=1");
			kameryURL
					.add("http://89.24.105.226:5001/axis-cgi/mjpg/video.cgi?resolution=CIF&camera=1");
			kameryURL
					.add("http://160.218.189.228:5001/axis-cgi/mjpg/video.cgi?resolution=CIF&camera=1");
			kameryURL
					.add("http://109.107.218.33:5001/axis-cgi/mjpg/video.cgi?resolution=CIF&camera=1");
			kameryURL
					.add("http://85.207.84.10:5001/axis-cgi/mjpg/video.cgi?resolution=CIF&camera=1");
			kameryURL.add("http://85.207.85.13:5001/video3.mjpg");
			kameryURL.add("http://81.25.30.20:5001/video3.mjpg");
		}
		for (String string : kameryURL) {
			CameraView camimage = new CameraView(this, new CameraStream(string), ml);
			camimage.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					((CameraView) v).loadNewImage();
				}
			});
			kamery.add(camimage);
			ml.addView(camimage);
		}		
	}

	@Override
	public void onBackPressed() {
		for (CameraView cameraView : kamery) {
			cameraView.stop();
		}
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		for (CameraView cameraView : kamery) {
			cameraView.stop();			
		}
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
