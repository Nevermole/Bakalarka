package cz.cvut.rutkodan.bakalarka.activities;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.VideoView;
import cz.cvut.rutkodan.bakalarka.R;

public class Test extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		final EditText editText = (EditText) findViewById(R.id.editText1);
		final VideoView videoView = (VideoView) findViewById(R.id.videoView1);
		MediaController mc = new MediaController(videoView.getContext());
		videoView.setMediaController(mc);
		videoView.setVideoURI(Uri.parse(editText.getText().toString()));
		videoView.requestFocus();
		Button button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				videoView.stopPlayback();
				videoView.setVideoURI(Uri.parse(editText.getText().toString()));
				videoView.start();
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
