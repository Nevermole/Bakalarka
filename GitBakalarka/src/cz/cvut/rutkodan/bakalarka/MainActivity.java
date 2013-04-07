package cz.cvut.rutkodan.bakalarka;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import cz.cvut.rutkodan.bakalarka.ui.MultilieLinearLayout;

public class MainActivity extends Activity {
	public String s;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		MultilieLinearLayout ml = (MultilieLinearLayout) findViewById(R.id.MultilineLinearLayout);

		CameraView camimage = new CameraView(this, new CameraStream());
		camimage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((CameraView) v).loadNewImage();
			}
		});	
		ml.addView(camimage);
		camimage = new CameraView(this, new CameraStream());
		camimage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((CameraView) v).loadNewImage();
			}
		});		
		ml.addView(camimage);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
