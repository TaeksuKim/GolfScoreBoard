package org.dolicoli.android.golfscoreboard.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DummySectionFragment extends Fragment {
	public DummySectionFragment() {
	}

	public static final String ARG_SECTION_NUMBER = "section_number";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		TextView textView = new TextView(getActivity());
		textView.setGravity(Gravity.CENTER);
		Bundle args = getArguments();
		textView.setText(Integer.toString(args.getInt(ARG_SECTION_NUMBER)));
		return textView;
	}
}
