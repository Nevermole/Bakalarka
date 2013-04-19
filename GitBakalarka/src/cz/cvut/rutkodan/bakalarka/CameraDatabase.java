package cz.cvut.rutkodan.bakalarka;

import java.util.ArrayList;

import cz.cvut.rutkodan.bakalarka.connection.Type;

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
		database = databaseHelper.getWritableDatabase();
	}

	public long addCameraToDB(String name, String address, int height,
			int width, double fps) {
		ContentValues values = new ContentValues();
		values.put(NAME, name);
		values.put(ADDRESS, address);
		values.put(HEIGHT, height);
		values.put(WIDTH, width);
		values.put(FPS, fps);
		return database.insert(TABLE, null, values);
	}

	public ArrayList<CameraSettings> getAllCamerasFromDB() {
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
				CameraSettings cam = new CameraSettings(Type.HTTP, name,
						address, height, width, maxFPS);
				kamery.add(cam);
				mCursor.moveToNext();
			}
		}
		return kamery; // iterate to get each value.
	}

	public void removeCameraFromDB(String name, String address) {
		database.delete(TABLE, NAME + "=?," + ADDRESS + "=?", new String[] {
				name, address });
	}

}
