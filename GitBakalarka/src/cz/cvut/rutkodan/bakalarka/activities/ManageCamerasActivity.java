package cz.cvut.rutkodan.bakalarka.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import cz.cvut.rutkodan.bakalarka.CameraDatabase;
import cz.cvut.rutkodan.bakalarka.CameraList;
import cz.cvut.rutkodan.bakalarka.CameraSettings;
import cz.cvut.rutkodan.bakalarka.R;
import cz.cvut.rutkodan.bakalarka.RequestCodes;
import cz.cvut.rutkodan.bakalarka.connection.Type;

public class ManageCamerasActivity extends Activity {
	private CameraDatabase database;
	private LinearLayout linearlayout;
	private boolean edited = false;
	private CameraList cameraList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_cameras);
		database = new CameraDatabase(this);
		cameraList = new CameraList(this);
		fillCameras();
	}

	private void fillCameras() {
		cameraList.loadFromDB();
		linearlayout = (LinearLayout) findViewById(R.id.manage_list);
		linearlayout.removeAllViews();
		for (CameraSettings cs : cameraList.getAllCameras()) {
			linearlayout.addView(createRow(cs));
		}
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private RelativeLayout createRow(final CameraSettings cameraSettings) {
		final RelativeLayout layout = new RelativeLayout(this);
		// layout.setClickable(true);

		final TextView textView = new TextView(this);
		final ImageButton buttonEdit = new ImageButton(this);
		final ImageButton buttonDelete = new ImageButton(this);
		buttonEdit.setId(buttonEdit.hashCode());
		buttonDelete.setId(buttonDelete.hashCode());

		textView.setText(cameraSettings.getName());
		textView.setTextAppearance(this,
				android.R.style.TextAppearance_Holo_Medium);
		android.widget.LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, (int) getResources().getDimension(
						R.dimen.button_48dp));
		lp.setMargins(0, 0, 0, 1);
		layout.setBackground(getResources().getDrawable(
				R.color.button_background_light));
		layout.setLayoutParams(lp);

		RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		rlp.addRule(RelativeLayout.CENTER_VERTICAL);
		rlp.setMargins(20, 0, 0, 0);
		rlp.addRule(RelativeLayout.LEFT_OF, buttonEdit.getId());
		textView.setClickable(false);
		layout.addView(textView, rlp);

		buttonEdit.setImageDrawable(getResources().getDrawable(
				R.drawable.collections_edit));
		buttonEdit.setScaleType(ScaleType.FIT_CENTER);
		buttonEdit.setAdjustViewBounds(true);
		buttonEdit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(),
						CameraAddActivity.class);
				i.putExtra("request", RequestCodes.EDIT_CAMERA);
				i.putExtra("Name", cameraSettings.getName());
				i.putExtra("Address", cameraSettings.getAddress());
				i.putExtra("FPS", cameraSettings.getMaxFPS());
				i.putExtra("Width", cameraSettings.getWidth());
				i.putExtra("Height", cameraSettings.getHeight());
				startActivityForResult(i, RequestCodes.EDIT_CAMERA.getNumber());

			}
		});
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			buttonEdit.setBackground(getResources().getDrawable(
					R.color.button_background_light));
		} else {
			buttonEdit.setBackgroundDrawable(getResources().getDrawable(
					R.color.button_background_light));
		}
		rlp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		rlp.addRule(RelativeLayout.CENTER_VERTICAL);
		rlp.addRule(RelativeLayout.LEFT_OF, buttonDelete.getId());
		layout.addView(buttonEdit, rlp);

		buttonDelete.setImageDrawable(getResources().getDrawable(
				R.drawable.collections_delete));
		buttonDelete.setScaleType(ScaleType.FIT_CENTER);
		buttonDelete.setAdjustViewBounds(true);
		buttonDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				layout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
				textView.setText(getResources().getString(R.string.deleted));
				buttonEdit.setVisibility(View.GONE);
				buttonDelete.setImageDrawable(getResources().getDrawable(R.drawable.content_new));				
				/*database.removeCameraFromDB(cameraSettings.getName(),
						cameraSettings.getAddress());
				linearlayout.removeView(layout);*/
			}
		});
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			buttonDelete.setBackground(getResources().getDrawable(
					R.color.button_background_light));
		} else {
			buttonDelete.setBackgroundDrawable(getResources().getDrawable(
					R.color.button_background_light));
		}
		rlp = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		rlp.addRule(RelativeLayout.CENTER_VERTICAL);
		layout.addView(buttonDelete, rlp);
		return layout;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RequestCodes.EDIT_CAMERA.getNumber()) {
			if (resultCode == RESULT_OK) {
				CameraSettings cam = new CameraSettings(Type.HTTP,
						data.getStringExtra("Name"),
						data.getStringExtra("Address"), data.getIntExtra(
								"Height", 0), data.getIntExtra("Width", 0),
						data.getDoubleExtra("FPS", 5.0));

				database.updateCamera(data.getStringExtra("OldName"),
						data.getStringExtra("OldAddress"), cam);
				edited = true;
				fillCameras();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manage_cameras, menu);
		return true;
	}

}
