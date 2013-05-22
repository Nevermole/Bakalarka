package cz.cvut.rutkodan.ipcameramonitor.activities;

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
import cz.cvut.rutkodan.ipcameramonitor.*;

public class CameraAddActivity extends Activity {
    private ImageView image;
    private int width;
    private int height;
    private String oldName = "";
    private String oldAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_add);

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
        Bundle bundle = getIntent().getExtras();
        if (bundle.get("request").equals(RequestCodes.EDIT_CAMERA)) {
            nazev.setText(bundle.getString("Name"));
            oldName = bundle.getString("Name");
            adressa.setText(bundle.getString("Address"));
            oldAddress = bundle.getString("Address");
            fps.setText(Double.toString(bundle.getDouble("FPS", 5.0)));
            width = bundle.getInt("Width", (int) Math.round((getResources()
                    .getDisplayMetrics().widthPixels / 2.0)
                    * getResources().getDisplayMetrics().density));
            height = bundle.getInt("Height", (int) Math.round((getResources()
                    .getDisplayMetrics().widthPixels / 4.0)
                    * getResources().getDisplayMetrics().density));
            new GetImage().execute(adressa.getText().toString());
        } else {
            width = (int) Math
                    .round((getResources().getDisplayMetrics().widthPixels / 2.0));
            height = (int) Math
                    .round((getResources().getDisplayMetrics().heightPixels / 4.0));
        }

        Button save = (Button) findViewById(R.id.button_add_save);
        save.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                CameraList list = new CameraList(getApplicationContext());

                if (list.contains(nazev.getText().toString())) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.camera_exists), Toast.LENGTH_LONG).show();
                } else {
                    Intent result = new Intent();
                    result.putExtra("Name", nazev.getText().toString());
                    result.putExtra("Address", adressa.getText().toString());
                    result.putExtra("FPS",
                            Double.parseDouble(fps.getText().toString()));
                    result.putExtra("Width", width);
                    result.putExtra("Height", height);
                    result.putExtra("OldName", oldName);
                    result.putExtra("OldAddress", oldAddress);
                    System.out.println("added new cam");
                    CameraSettings cam = new CameraSettings(nazev.getText().toString(), adressa.getText().toString(),
                            height, width, Double.parseDouble(fps.getText().toString()));
                    saveCameraToDatabase(cam);
                    setResult(RESULT_OK, result);
                    finish();
                }
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

    private void saveCameraToDatabase(CameraSettings cam) {
        CameraDatabase cameraDatabase = new CameraDatabase(getApplicationContext());
        if (oldName != null && cameraDatabase.getAllCamerasFromDB().contains(oldName)) {
            cameraDatabase.updateCamera(oldName, oldAddress, cam);
        } else {
            cameraDatabase.addCameraToDB(cam.getName(), cam.getAddress(),
                    cam.getHeight(), cam.getWidth(), cam.getMaxFPS());
        }
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
                        getResources().getString(R.string.camera_connection_error), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}
