package cz.cvut.rutkodan.ipcameramonitor.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import cz.cvut.rutkodan.ipcameramonitor.CameraList;
import cz.cvut.rutkodan.ipcameramonitor.CameraSettings;
import cz.cvut.rutkodan.ipcameramonitor.R;
import cz.cvut.rutkodan.ipcameramonitor.RequestCodes;
import cz.cvut.rutkodan.ipcameramonitor.ui.CameraGridFragment;

import java.util.*;

public class CameraViewsActivity extends FragmentActivity {

    public static long dataUsed = 0;
    public static Context appContext;
    private static CameraList kamery;
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    private UpdateHandler handler = new UpdateHandler();
    private Timer updater;
    private int visiblePage = 0;
    private String name;
    private int cols = 0;
    private int rows = 0;

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
        imageButton = (ImageButton) findViewById(R.id.button_setup_grid);
        imageButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                GridDialog dialogFragment = new GridDialog();
                dialogFragment.show(getSupportFragmentManager(), "grid");
            }
        });
        if (kamery == null) {
            kamery = new CameraList(this);
        }
        String name = getIntent().getStringExtra("name");
        imageButton = (ImageButton) findViewById(R.id.button_record);
        if (name != null) {
            imageButton.setVisibility(View.VISIBLE);
            cols = 1;
            rows = 1;
            this.name = name;
        } else {
            imageButton.setVisibility(View.INVISIBLE);
        }
        initPages();
        System.err.println("created");
        updater = new Timer();
        updater.scheduleAtFixedRate(new UpdateDataCounter(), 1000, 1000);
    }

    private void initPages() {
        kamery.loadFromDB();
        int horizontalCount = cols;
        int verticalCount = rows;
        ImageButton imageButton = (ImageButton) findViewById(R.id.button_record);
        if (kamery.size() == 1 || (rows == 1 && cols == 1)) {
            imageButton.setVisibility(View.VISIBLE);
        } else {
            imageButton.setVisibility(View.INVISIBLE);
        }
        if (cols == 0 || rows == 0) {
            if (kamery.size() < 2) {
                horizontalCount = verticalCount = 1;
            } else if (kamery.size() < 3) {
                horizontalCount = 1;
                verticalCount = 2;
            } else if (kamery.size() < 5) {
                horizontalCount = 2;
                verticalCount = 2;
            } else if (kamery.size() < 7) {
                horizontalCount = 2;
                verticalCount = 3;
            } else {
                horizontalCount = 2;
                verticalCount = 4;
            }
            cols = horizontalCount;
            rows = verticalCount;
        }
        if (mSectionsPagerAdapter != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            for (Fragment fragment : mSectionsPagerAdapter.fragments) {
                transaction.remove(fragment);
            }
            transaction.commitAllowingStateLoss();
        }
        List<Fragment> fragments = new Vector<Fragment>();
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
        mViewPager.setCurrentItem(visiblePage, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.camera_views, menu);
        return true;
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
                rows = 0;
                cols = 0;
                initPages();
            }
        } else if (requestCode == RequestCodes.MANAGE_CAMERAS.getNumber()) {
            if (resultCode == RESULT_OK) {
                if (data.getBooleanExtra("Edited", true)) {
                    rows = 0;
                    cols = 0;
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

    public void recreate() {
        initPages();
    }

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
            if ((cols == 1 && rows == 1) || kamery.size() == 1) {
                return kamery.getCamera(position).getName();
            } else {
                return getResources().getString(R.string.page) + (position + 1);
            }
        }

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

    private class GridDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(getActivity());

            LayoutInflater inflater = getActivity().getLayoutInflater();
            View dialog = inflater.inflate(R.layout.dialog_grid, null);

            final NumberPicker numberPicker1 = (NumberPicker) dialog.findViewById(R.id.numberPicker);
            numberPicker1.setMaxValue(4);
            numberPicker1.setValue(cols);
            numberPicker1.setMinValue(1);

            final NumberPicker numberPicker2 = (NumberPicker) dialog.findViewById(R.id.numberPicker2);
            numberPicker2.setMaxValue(4);
            numberPicker2.setValue(rows);
            numberPicker2.setMinValue(1);
            builder.setView(dialog);
            builder.setMessage("Mřížka");
            builder.setPositiveButton("Uložit", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    cols = numberPicker1.getValue();
                    rows = numberPicker2.getValue();
                    recreate();
                }
            });
            builder.setNegativeButton("Zavřít", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }


}
