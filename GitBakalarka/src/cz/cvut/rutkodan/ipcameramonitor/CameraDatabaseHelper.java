package cz.cvut.rutkodan.ipcameramonitor;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CameraDatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "DBName";
	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_CREATE = "create table Cameras(name text not null,"
			+ "address text not null, height text not null, width text not null, fps text not null);";

	public CameraDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(this.getClass().getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS Caneras");
		onCreate(database);
	}

}
