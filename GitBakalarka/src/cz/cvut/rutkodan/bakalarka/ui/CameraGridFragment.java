package cz.cvut.rutkodan.bakalarka.ui;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import cz.cvut.rutkodan.bakalarka.CameraSettings;
import cz.cvut.rutkodan.bakalarka.R;
import cz.cvut.rutkodan.bakalarka.activities.CameraViewsActivity;

public class CameraGridFragment extends Fragment {

	public static final String ARG_SECTION_NUMBER = "section_number";
	public static final String ARG_OBJECT = "object";
	private ArrayList<CameraSettings> cameraList;
	private int columns = 2;
	private int rows = 1;
	private View rootView;

	public CameraGridFragment() {
		super();
		cameraList = new ArrayList<CameraSettings>();
	}

	public void setCameras(ArrayList<CameraSettings> cams) {
		this.cameraList = cams;
	}

	public void setColumns(int columns) {
		this.columns = columns;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	@Override
	public void onDestroyView() {
		System.err.println("destoy view");
		if (rootView != null) {
			MultilieLinearLayout parentViewGroup = (MultilieLinearLayout) rootView
					.findViewById(R.id.cameraGrid);
			if (parentViewGroup != null) {
				parentViewGroup.removeAllViews();
			}
		}
		super.onDestroyView();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setRetainInstance(true);
		rootView = inflater.inflate(R.layout.fragment_camera_grid, container,
				false);
		MultilieLinearLayout layout = (MultilieLinearLayout) rootView
				.findViewById(R.id.cameraGrid);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		layout.setLayoutParams(lp);
		layout.removeAllViews();
		layout.setColumnCount(columns);
		System.out.println(layout.getWidth());
		for (CameraSettings cameraSettings : cameraList) {
			CameraView cameraView = new CameraView(getActivity(),
					cameraSettings);
			if (rows != 1 && columns != 1) {
				final String name = cameraView.getName();
				cameraView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						System.err.println("Clicked");
						final Intent intent = new Intent(getActivity(),
								CameraViewsActivity.class);
						intent.putExtra("name", name);
						startActivity(intent);
					}
				});
			}
			/*LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.FILL_PARENT);*/
			layout.addView(cameraView);
		}
		return rootView;
	}
}
