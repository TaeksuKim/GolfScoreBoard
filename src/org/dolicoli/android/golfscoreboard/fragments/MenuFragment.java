package org.dolicoli.android.golfscoreboard.fragments;

import org.dolicoli.android.golfscoreboard.MainActivity;
import org.dolicoli.android.golfscoreboard.R;
import org.dolicoli.android.golfscoreboard.fragments.history.NewGameResultHistoryFragment;
import org.dolicoli.android.golfscoreboard.fragments.history.NewPlayerRankingFragment;
import org.dolicoli.android.golfscoreboard.fragments.main.CurrentGameFragment;
import org.dolicoli.android.golfscoreboard.fragments.main.HandicapSimulatorFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MenuFragment extends ListFragment {
	public MenuFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.menu_fragment, null);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		MenuAdapter colorAdapter = new MenuAdapter(getActivity(),
				android.R.layout.simple_list_item_1, android.R.id.text1,
				new String[] { "오늘 게임", "지난 게임", "개인 기록", "핸디캡 계산" });
		setListAdapter(colorAdapter);
	}

	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {
		Fragment newContent = null;
		switch (position) {
		case 0:
			newContent = new CurrentGameFragment();
			break;
		case 1:
			newContent = new NewGameResultHistoryFragment();
			break;
		case 2:
			newContent = new NewPlayerRankingFragment();
			break;
		case 3:
			newContent = new HandicapSimulatorFragment();
			break;
		}
		if (newContent != null)
			switchFragment(newContent);
	}

	private void switchFragment(Fragment fragment) {
		if (getActivity() == null)
			return;

		if (getActivity() instanceof MainActivity) {
			MainActivity fca = (MainActivity) getActivity();
			fca.switchContent(fragment);
		}
	}

	private static class MenuAdapter extends ArrayAdapter<String> {

		private static int TYPE_SECTION_HEADER = 1;

		public MenuAdapter(Context context, int resource,
				int textViewResourceId, String[] objects) {
			super(context, resource, textViewResourceId, objects);
		}

		@Override
		public int getItemViewType(int position) {
			if (position == 1) {
				return TYPE_SECTION_HEADER;
			}
			return super.getItemViewType(position);
		}

	}
}
