package cz.cvut.rutkodan.bakalarka.ui;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;

public class MultilieLinearLayout extends LinearLayout {

	private ArrayList<LinearLayout> lines = new ArrayList<LinearLayout>();
	private ArrayList<View> views = new ArrayList<View>();
	private LinearLayout linearLayout;
	private Context context;

	public MultilieLinearLayout(Context context) {
		super(context);
		this.context = context;
	}

	public MultilieLinearLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}

	public MultilieLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
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
		if (lines.isEmpty()) {
			linearLayout = new LinearLayout(context);
			linearLayout.setOrientation(LinearLayout.HORIZONTAL);
			super.addView(linearLayout, 0);
			lines.add(linearLayout);
		}
		linearLayout.measure(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		DisplayMetrics displayMetrics = context.getResources()
				.getDisplayMetrics();
		System.out.println(displayMetrics.widthPixels);
		System.out.println(linearLayout.getWidth());
		System.out.println(child.getWidth());
		if (linearLayout.getMeasuredWidth() + child.getMeasuredWidth() > displayMetrics.widthPixels) {
			linearLayout = new LinearLayout(context);
			linearLayout.setOrientation(LinearLayout.HORIZONTAL);
			super.addView(linearLayout);
			lines.add(linearLayout);
		}
		linearLayout.addView(child);
		views.add(child);
		// Log.d("Bakalarka", "view added" + getMeasuredWidth());
		// super.addView(child);
	}

}
