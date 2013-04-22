package cz.cvut.rutkodan.bakalarka;

import java.util.ArrayList;

import android.content.Context;
import cz.cvut.rutkodan.bakalarka.connection.Type;

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
		saveCameraToDatabase(camera);
	}

	public void add(CameraSettings[] cameras) {
		if (cameras != null) {
			for (CameraSettings cameraSettings : cameras) {
				if (!this.cameras.contains(cameraSettings)) {
					this.cameras.add(cameraSettings);
					saveCameraToDatabase(cameraSettings);
				}
			}
		}

	}

	public void add(Type type, String name, String address, int height,
			int width, double maxFPS) {
		CameraSettings cam = new CameraSettings(type, name, address, height,
				width, maxFPS);
		cameras.add(cam);
		saveCameraToDatabase(cam);
	}

	private void saveCameraToDatabase(CameraSettings cam) {
		cameraDatabase.addCameraToDB(cam.getName(), cam.getAddress(),
				cam.getHeight(), cam.getWidth(), cam.getMaxFPS());
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
}
