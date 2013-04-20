package cz.cvut.rutkodan.bakalarka.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import cz.cvut.rutkodan.bakalarka.CameraList;
import cz.cvut.rutkodan.bakalarka.CameraSettings;
import cz.cvut.rutkodan.bakalarka.R;

public class ManageCamerasActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_cameras);
		CameraList cameraList = new CameraList(this);
		cameraList.loadFromDB();
		LinearLayout layout = (LinearLayout) findViewById(R.id.manage_list);
		for (CameraSettings cs : cameraList.getAllCameras()) {
			layout.addView(createRow(cs));
		}

	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private RelativeLayout createRow(CameraSettings cameraSettings) {
		RelativeLayout layout = new RelativeLayout(this);
		TextView textView = new TextView(this);
		ImageButton buttonEdit = new ImageButton(this);
		ImageButton buttonDelete = new ImageButton(this);
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
		layout.addView(textView, rlp);

		buttonEdit.setImageDrawable(getResources().getDrawable(
				R.drawable.collections_edit));
		buttonEdit.setScaleType(ScaleType.FIT_CENTER);
		buttonEdit.setAdjustViewBounds(true);
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manage_cameras, menu);
		return true;
	}

}
