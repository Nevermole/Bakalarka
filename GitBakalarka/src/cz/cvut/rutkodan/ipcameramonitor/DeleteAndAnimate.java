package cz.cvut.rutkodan.ipcameramonitor;

import android.widget.RelativeLayout;

public class DeleteAndAnimate {
	private RelativeLayout layout;
	private CameraSettings settings;

	public DeleteAndAnimate(RelativeLayout layout, CameraSettings settings) {
		super();
		this.layout = layout;
		this.settings = settings;
	}

	public RelativeLayout getLayout() {
		return layout;
	}

	public CameraSettings getSettings() {
		return settings;
	}

	@Override
	public boolean equals(Object o) {
		if (this.getClass().isInstance(o)) {
			if (settings.getName().equals(
					((DeleteAndAnimate) o).getSettings().getName())) {
				return true;
			}
		}
		return false;
	}
}
