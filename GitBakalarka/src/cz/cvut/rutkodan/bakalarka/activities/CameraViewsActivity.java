package cz.cvut.rutkodan.bakalarka.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import cz.cvut.rutkodan.bakalarka.CameraList;
import cz.cvut.rutkodan.bakalarka.CameraSettings;
import cz.cvut.rutkodan.bakalarka.R;
import cz.cvut.rutkodan.bakalarka.RequestCodes;
import cz.cvut.rutkodan.bakalarka.ui.CameraGridFragment;
import cz.cvut.rutkodan.bakalarka.ui.CameraView;

public class CameraViewsActivity extends FragmentActivity {

	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	private static CameraList kamery;
	private UpdateHandler handler = new UpdateHandler();
	public static long dataUsed = 0;
	private Timer updater;
	private int visiblePage = 0;
	private String name;
	private int horizontalCount = 2;
	private int verticalCount = 2;
	public static Context appContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera_views);
		appContext = getApplicationContext();
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.

		// Set up the ViewPager with the sections adapter.
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
				initPages();
			}
		});
		if (kamery == null) {
			kamery = new CameraList(this);
		}
		String name = getIntent().getStringExtra("name");
		if (name != null) {
			// System.err.println(name);
			horizontalCount = 1;
			verticalCount = 1;
			this.name = name;
		}
		initPages();
		System.err.println("created");
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

	private void initPages() {
		kamery.loadFromDB();
		final List<Fragment> fragments = new Vector<Fragment>();
		for (int j = 0; j < (int) Math.ceil(kamery.size()
				/ (double) (horizontalCount * verticalCount)); j++) {
			ArrayList<CameraSettings> cams = new ArrayList<CameraSettings>();
			for (int i = j * horizontalCount * verticalCount; i < j
					* horizontalCount * verticalCount + horizontalCount
					* verticalCount
					&& i < kamery.size(); i++) {				
				cams.add(kamery.getCamera(i));
				if (name != null && name.equals(kamery.getCamera(i).getName())) {
					visiblePage = i;
				}
			}
			CameraGridFragment cameraGridFragment = new CameraGridFragment();
			cameraGridFragment.setRows(verticalCount);
			cameraGridFragment.setColumns(horizontalCount);
			cameraGridFragment.setCameras(cams);
			fragments.add(cameraGridFragment);
		}
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager(), fragments);

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		/*mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

					@Override
					public void onPageSelected(int arg0) {
						((CameraGridFragment) fragments.get(visiblePage))
								.pause();
						((CameraGridFragment) fragments.get(arg0)).play();
						visiblePage = arg0;
					}
				});*/
		mViewPager.setCurrentItem(visiblePage, true);
		if (fragments.size() > 0) {
			//((CameraGridFragment) fragments.get(visiblePage)).play();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.camera_views, menu);
		return true;
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		private List<Fragment> fragments;

		public SectionsPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
			super(fm);
			this.fragments = fragments;
		}

		@Override
		public Fragment getItem(int arg0) {
			return fragments.get(arg0);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			// Locale l = Locale.getDefault();
			if (horizontalCount == 1 && verticalCount == 1) {
				return kamery.getCamera(position).getName();
			} else {
				return getResources().getString(R.string.page) + (position + 1);
			}
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
			}
		} else if (requestCode == RequestCodes.MANAGE_CAMERAS.getNumber()) {
			if (resultCode == RESULT_OK) {
				if (data.getBooleanExtra("Edited", false)) {
					initPages();
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
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
