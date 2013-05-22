package cz.cvut.rutkodan.ipcameramonitor.activities;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AccelerateInterpolator;
import android.widget.*;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout.LayoutParams;
import cz.cvut.rutkodan.ipcameramonitor.*;

import java.io.*;
import java.util.ArrayList;

public class ManageCamerasActivity extends Activity implements OnTouchListener {
    private CameraDatabase database;
    private LinearLayout linearlayout;
    private boolean edited = false;
    private CameraList cameraList;
    private ArrayList<DeleteAndAnimate> toDelete = new ArrayList<DeleteAndAnimate>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_cameras);
        database = new CameraDatabase(this);
        cameraList = new CameraList(this);
        fillCameras();
        Button buttonImport = (Button) findViewById(R.id.button_import);
        buttonImport.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    ObjectInputStream in = new ObjectInputStream(
                            new FileInputStream(new File(Environment
                                    .getExternalStorageDirectory(),
                                    "Cameras.db")));
                    CameraSettings[] list = (CameraSettings[]) in.readObject();
                    in.close();
                    cameraList.add(list);
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.cams_imported),
                            Toast.LENGTH_LONG).show();
                    fillCameras();
                    edited = true;
                } catch (StreamCorruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        Button buttonExport = (Button) findViewById(R.id.button_export);
        buttonExport.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    File path = new File(Environment
                            .getExternalStorageDirectory(), "Cameras.db");
                    ObjectOutputStream out = new ObjectOutputStream(
                            new FileOutputStream(path));
                    out.writeObject(cameraList.getAllCameras());
                    out.flush();
                    out.close();
                    Toast.makeText(
                            getApplicationContext(),
                            getResources().getString(R.string.cams_exported)
                                    + " " + path.getAbsolutePath(),
                            Toast.LENGTH_LONG).show();
                } catch (StreamCorruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        ScrollView scrollView = (ScrollView) findViewById(R.id.manage_scroll);
        scrollView.setOnTouchListener(this);
        buttonExport.setOnTouchListener(this);
        buttonImport.setOnTouchListener(this);
    }

    private void fillCameras() {
        cameraList.loadFromDB();
        linearlayout = (LinearLayout) findViewById(R.id.manage_list);
        linearlayout.removeAllViews();
        for (CameraSettings cs : cameraList.getAllCameras()) {
            linearlayout.addView(createRow(cs));
        }
        ImageButton addNewCamera = new ImageButton(this);

        addNewCamera.setImageDrawable(getResources().getDrawable(
                R.drawable.content_new_light));
        addNewCamera.setScaleType(ScaleType.FIT_CENTER);
        addNewCamera.setAdjustViewBounds(true);
        addNewCamera.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),
                        CameraAddActivity.class);
                i.putExtra("request", RequestCodes.ADD_NEW_CAMERA);
                startActivityForResult(i, RequestCodes.ADD_NEW_CAMERA.getNumber());

            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            addNewCamera.setBackground(getResources().getDrawable(
                    R.color.button_background_light));
        } else {
            addNewCamera.setBackgroundDrawable(getResources().getDrawable(
                    R.color.button_background_light));
        }
        linearlayout.addView(addNewCamera);
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
                if (textView.getText().equals(cameraSettings.getName())) {
                    while (!toDelete.isEmpty()) {
                        delAndAnim(toDelete.remove(0));
                    }
                    layout.setBackgroundColor(getResources().getColor(
                            android.R.color.transparent));
                    textView.setText(getResources().getString(R.string.deleted));
                    textView.setTextColor(getResources().getColor(
                            android.R.color.background_light));
                    buttonEdit.setVisibility(View.GONE);
                    buttonDelete.setImageDrawable(getResources().getDrawable(
                            R.drawable.content_undo_dark));
                    buttonDelete.setBackground(getResources().getDrawable(
                            R.color.button_background_transparent));
                    toDelete.add(new DeleteAndAnimate(layout, cameraSettings));
                } else {
                    toDelete.remove(new DeleteAndAnimate(layout, cameraSettings));
                    layout.setBackground(getResources().getDrawable(
                            R.color.button_background_light));
                    textView.setText(cameraSettings.getName());
                    textView.setTextColor(getResources().getColor(
                            android.R.color.black));
                    buttonEdit.setVisibility(View.VISIBLE);
                    buttonDelete.setImageDrawable(getResources().getDrawable(
                            R.drawable.collections_delete));
                    buttonDelete.setBackground(getResources().getDrawable(
                            R.color.button_background_light));
                }
            }
        });
        buttonEdit.setOnTouchListener(this);
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

    private void delAndAnim(final DeleteAndAnimate data) {
        edited = true;
        database.removeCameraFromDB(data.getSettings());
        final RelativeLayout rel = data.getLayout();
        ValueAnimator va = ValueAnimator.ofInt(data.getLayout().getHeight(), 0);
        va.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer) animation.getAnimatedValue();
                System.out.println(value);
                rel.getLayoutParams().height = value;
                rel.requestLayout();
                linearlayout.requestLayout();
            }
        });
        va.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                linearlayout.removeView(data.getLayout());
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });
        va.setInterpolator(new AccelerateInterpolator());
        va.setDuration(250);
        va.start();
    }

    @Override
    protected void onDestroy() {
        while (!toDelete.isEmpty()) {
            delAndAnim(toDelete.remove(0));
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        while (!toDelete.isEmpty()) {
            delAndAnim(toDelete.remove(0));
        }
        Intent result = new Intent();
        result.putExtra("Edited", edited);
        setResult(RESULT_OK, result);
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            edited = true;
            fillCameras();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.manage_cameras, menu);
        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        while (!toDelete.isEmpty()) {
            delAndAnim(toDelete.remove(0));
        }
        return false;
    }

}
