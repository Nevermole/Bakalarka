package cz.cvut.rutkodan.bakalarka.ui;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class MultilieLinearLayout extends LinearLayout {

	private ArrayList<LinearLayout> lines = new ArrayList<LinearLayout>();
	private ArrayList<View> views = new ArrayList<View>();
	private LinearLayout linearLayout;
	private int cols;
	private int rows;

	public MultilieLinearLayout(Context context) {
		super(context);
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

	public MultilieLinearLayout(Context context, int rows, int cols) {
		super(context);
		this.rows = rows;
		this.cols = cols;
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

	public void setRowCount(int rows) {
		this.rows = rows;
	}

}
