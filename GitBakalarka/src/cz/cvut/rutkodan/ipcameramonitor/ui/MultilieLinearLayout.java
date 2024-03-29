package cz.cvut.rutkodan.ipcameramonitor.ui;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class MultilieLinearLayout extends LinearLayout {

	private ArrayList<LinearLayout> lines = new ArrayList<LinearLayout>();
	private ArrayList<View> views = new ArrayList<View>();
	private LinearLayout linearLayout;
	private int cols;
	private int heigth;
	private int width; 

	// private int rows;

	public MultilieLinearLayout(Context context) {
		super(context);
		WindowManager wm =  (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();		
		Point size = new Point();
		display.getSize(size);
		width = size.x;
		//height = size.y;
	}

	public MultilieLinearLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public MultilieLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public void recreate() {
		for (LinearLayout ll : lines) {
			ll.removeAllViews();
			removeView(ll);
		}
		lines.clear();
		int j = views.size();
		for (int i = 0; i < j; i++) {
			addView(views.remove(0));
		}
	}

	@Override
	public void removeAllViews() {
		for (LinearLayout ll : lines) {
			ll.removeAllViews();
			removeView(ll);
		}
		lines.clear();
		super.removeAllViews();
	}

	@Override
	public void addView(View child) {
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT, 1f);
		if (lines.isEmpty()) {
			linearLayout = new LinearLayout(getContext());
			linearLayout.setOrientation(LinearLayout.HORIZONTAL);
			super.addView(linearLayout, layoutParams);
			lines.add(linearLayout);
		}
		System.out.println(linearLayout.getChildCount());
		if (linearLayout.getChildCount() >= cols) {
			linearLayout = new LinearLayout(getContext());
			linearLayout.setOrientation(LinearLayout.HORIZONTAL);
			super.addView(linearLayout, layoutParams);
			lines.add(linearLayout);
		}

		layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT, 1f);
		linearLayout.addView(child, layoutParams);
		views.add(child);
		// Log.d("Bakalarka", "view added" + getMeasuredWidth());
		// super.addView(child);
	}

	public void setColumnCount(int columns) {
		this.cols = columns;
	}
}
