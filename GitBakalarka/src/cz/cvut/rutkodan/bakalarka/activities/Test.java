package cz.cvut.rutkodan.bakalarka.activities;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.VideoView;
import cz.cvut.rutkodan.bakalarka.R;

public class Test extends Activity implements OnPreparedListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		final EditText editText = (EditText) findViewById(R.id.editText1);
		final VideoView surfaceView = (VideoView) findViewById(R.id.surfaceView1);
		final MediaPlayer mediaPlayer = MediaPlayer.create(
				getApplicationContext(),
				Uri.parse(editText.getText().toString()));
		mediaPlayer.setOnPreparedListener(this);
		Button button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				surfaceView.setVideoURI(Uri.parse(editText.getText().toString()));
				surfaceView.start();
				/*
				if (mediaPlayer.isPlaying()) {
					
					mediaPlayer.stop();
				}
				mediaPlayer.setDisplay(surfaceView.getHolder());
				try {
					mediaPlayer.prepareAsync();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test, menu);
		return true;
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		mp.start();

	}

}
