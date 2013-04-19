package cz.cvut.rutkodan.bakalarka;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class CameraAddActivity extends Activity {
	private ImageView image;
	private int width;
	private int height;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera_add);		
		width = Math.round(200 * getResources().getDisplayMetrics().density);
		height = Math.round(100 * getResources().getDisplayMetrics().density);
		final EditText nazev = (EditText) findViewById(R.id.editName);
		final EditText adressa = (EditText) findViewById(R.id.editAddress);
		image = (ImageView) findViewById(R.id.cemera_image);
		adressa.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					new GetImage().execute(adressa.getText().toString());
				}
			}

		});
		final EditText fps = (EditText) findViewById(R.id.editFPS);
		Button save = (Button) findViewById(R.id.button_add_save);
		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent result = new Intent();
				result.putExtra("Name", nazev.getText().toString());
				result.putExtra("Address", adressa.getText().toString());
				result.putExtra("FPS",
						Double.parseDouble(fps.getText().toString()));
				result.putExtra("Width", width);
				result.putExtra("Height", height);
				setResult(RESULT_OK, result);
				finish();
			}
		});
		Button cancel = (Button) findViewById(R.id.button_add_cancel);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.camera_add, menu);
		return true;
	}

	private class GetImage extends AsyncTask<String, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... params) {
			CameraStream stream = new CameraStream(params[0]);
			Bitmap bm = stream.getData();
			return bm;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (result != null) {
				image.setImageBitmap(result);
				width = result.getWidth();
				height = result.getHeight();
			} else {
				Toast.makeText(getApplicationContext(),
						"Nelze se p�ipojit ke kame�e", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}
}
