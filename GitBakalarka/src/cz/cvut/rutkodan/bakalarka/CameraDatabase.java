package cz.cvut.rutkodan.bakalarka;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CameraDatabase {

	private CameraDatabaseHelper databaseHelper;

	private SQLiteDatabase database;

	public final static String TABLE = "Cameras";

	public final static String NAME = "name";
	public final static String ADDRESS = "address";
	public final static String HEIGHT = "height";
	public final static String WIDTH = "width";
	public final static String FPS = "fps";

	public CameraDatabase(Context context) {
		databaseHelper = new CameraDatabaseHelper(context);

	}

	public long addCameraToDB(String name, String address, int height,
			int width, double fps) {
		database = databaseHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(NAME, name);
		values.put(ADDRESS, address);
		values.put(HEIGHT, height);
		values.put(WIDTH, width);
		values.put(FPS, fps);
		long res = database.insert(TABLE, null, values);
		database.close();
		return res;
	}

	public ArrayList<CameraSettings> getAllCamerasFromDB() {
		database = databaseHelper.getWritableDatabase();
		String[] cols = new String[] { NAME, ADDRESS, HEIGHT, WIDTH, FPS };
		Cursor mCursor = database.query(TABLE, cols, null, null, null, null,
				null, null);
		ArrayList<CameraSettings> kamery = new ArrayList<CameraSettings>();
		if (mCursor != null) {
			mCursor.moveToFirst();
			while (!mCursor.isAfterLast()) {
				String name = mCursor.getString(0);
				String address = mCursor.getString(1);
				int height = mCursor.getInt(2);
				int width = mCursor.getInt(3);
				double maxFPS = mCursor.getDouble(4);
				CameraSettings cam = new CameraSettings(name,
						address, height, width, maxFPS);
				kamery.add(cam);
				mCursor.moveToNext();
			}
		}
		database.close();
		return kamery; // iterate to get each value.
	}

	public void removeCameraFromDB(String name, String address) {
		database = databaseHelper.getWritableDatabase();
		database.delete(TABLE, NAME + "=? and " + ADDRESS + "=?", new String[] {
				name, address });
		database.close();
	}

	public void updateCamera(String oldName, String oldAddress,
			CameraSettings cam) {
		database = databaseHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(NAME, cam.getName());
		values.put(ADDRESS, cam.getAddress());
		values.put(HEIGHT, cam.getHeight());
		values.put(WIDTH, cam.getWidth());
		values.put(FPS, cam.getMaxFPS());
		database.update(TABLE, values, NAME + "=? and " + ADDRESS + "=?",
				new String[] { oldName, oldAddress });
		database.close();
		System.out.println("renamed");
	}

	public void removeCameraFromDB(CameraSettings settings) {
		String name = settings.getName();
		String address = settings.getAddress();
		database = databaseHelper.getWritableDatabase();
		database.delete(TABLE, NAME + "=? and " + ADDRESS + "=?", new String[] {
				name, address });
		database.close();
	}
}
