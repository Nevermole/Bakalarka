package cz.cvut.rutkodan.ipcameramonitor;

import java.io.Serializable;

public class CameraSettings implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8641113749333874914L;
	private String name;
	private String address;
	private int height;
	private int width;
	private double maxFPS = 5.0;

	public CameraSettings(String name, String address, int height,
			int width, double maxFPS) {
		super();
		this.name = name;
		this.address = address;
		this.height = height;
		this.width = width;
		this.maxFPS = maxFPS;
	}

	public CameraSettings(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (CameraSettings.class.isInstance(o)) {
			return ((CameraSettings) o).getName().equals(this.name);
		} else if (String.class.isInstance(o)) {
			return ((String) o).equals(this.name);
		}
		return false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public double getMaxFPS() {
		return maxFPS;
	}

	public void setMaxFPS(double maxFPS) {
		this.maxFPS = maxFPS;
	}
}
