package cz.cvut.rutkodan.bakalarka.activities;

import java.io.IOException;

import android.app.Activity;
import android.media.MediaCodec;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import cz.cvut.rutkodan.bakalarka.R;

public class Test extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		final EditText editText = (EditText) findViewById(R.id.editText1);
		final SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
		final MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(editText.getText().toString()));
		
		Button button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {				
				if (mediaPlayer.isPlaying()) {
					mediaPlayer.stop();
				}				
				mediaPlayer.setDisplay(surfaceView.getHolder());								
				try {
					mediaPlayer.prepare();					
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mediaPlayer.start();				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test, menu);
		return true;
	}

}
