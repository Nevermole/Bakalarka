package cz.cvut.rutkodan.ipcameramonitor;

import android.content.Context;
import cz.cvut.rutkodan.ipcameramonitor.connection.Type;

import java.util.ArrayList;

public class CameraList {

	private ArrayList<CameraSettings> cameras;
	private CameraDatabase cameraDatabase;

	public CameraList(Context context) {
		super();
		cameraDatabase = new CameraDatabase(context);
		cameras = new ArrayList<CameraSettings>();
	}

	public void add(CameraSettings camera) {
		cameras.add(camera);
	}

	public void add(CameraSettings[] cameras) {
		if (cameras != null) {
			for (CameraSettings cameraSettings : cameras) {
				if (!this.cameras.contains(cameraSettings)) {
					this.cameras.add(cameraSettings);
				}
			}
		}

	}

	public void add(Type type, String name, String address, int height,
			int width, double maxFPS) {
		CameraSettings cam = new CameraSettings(name, address, height,
				width, maxFPS);
		cameras.add(cam);
	}



	public CameraSettings[] getAllCameras() {
		CameraSettings[] cams = new CameraSettings[cameras.size()];
		cams = cameras.toArray(cams);
		return cams;
	}

	public void loadFromDB() {
		cameras.clear();
		cameras = cameraDatabase.getAllCamerasFromDB();
	}

	public boolean contains(String name) {
		cameras.contains(new CameraSettings(name));
		return false;
	}

	public int size() {
		return cameras.size();
	}

	public CameraSettings getCamera(int index) {
		return cameras.get(index);
	}

	public int getCameraIndex(String name) {		
		return cameras.indexOf(name);
	}
}
