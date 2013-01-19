package org.dolicoli.android.golfscoreboard.fragments.main;

import java.util.ArrayList;

import org.dolicoli.android.golfscoreboard.GolfScoreBoardApplication;
import org.dolicoli.android.golfscoreboard.R;
import org.dolicoli.android.golfscoreboard.data.DateItem;
import org.dolicoli.android.golfscoreboard.data.OneGame;
import org.dolicoli.android.golfscoreboard.tasks.GameAndResultTask;
import org.dolicoli.android.golfscoreboard.tasks.GameAndResultTask.GameAndResultTaskListener;
import org.dolicoli.android.golfscoreboard.tasks.ThreeMonthsGameReceiveTask;
import org.dolicoli.android.golfscoreboard.tasks.ThreeMonthsGameReceiveTask.ReceiveProgress;
import org.dolicoli.android.golfscoreboard.tasks.ThreeMonthsGameReceiveTask.ReceiveResult;
import org.dolicoli.android.golfscoreboard.utils.Reloadable;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public abstract class HistoryItemListFragment extends ListFragment implements
		Reloadable, GameAndResultTaskListener, OnNavigationListener,
		ThreeMonthsGameReceiveTask.TaskListener {

	protected ProgressBar progressBar;
	protected TextView noResultTextView;
	protected ProgressDialog progressDialog;

	protected ArrayList<OneGame> resultList;
	protected ArrayAdapter<DateItem> navigationAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		resultList = new ArrayList<OneGame>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		navigationAdapter = new ArrayAdapter<DateItem>(getActivity(),
				android.R.layout.simple_list_item_1,
				((GolfScoreBoardApplication) getActivity().getApplication())
						.getNavigationItems());
		actionBar.setListNavigationCallbacks(navigationAdapter, this);
		actionBar.setTitle("");

		View view = inflate(inflater);

		progressBar = (ProgressBar) view.findViewById(R.id.ProgressBar);
		noResultTextView = (TextView) view.findViewById(R.id.NoResultTextView);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		ActionBar actionBar = getActivity().getActionBar();
		switch (getCurrentMode()) {
		case GolfScoreBoardApplication.MODE_THIS_MONTH:
			actionBar.setSelectedNavigationItem(1);
			break;
		case GolfScoreBoardApplication.MODE_LAST_MONTH:
			actionBar.setSelectedNavigationItem(2);
			break;
		case GolfScoreBoardApplication.MODE_LAST_THREE_MONTHS:
			actionBar.setSelectedNavigationItem(3);
			break;
		case GolfScoreBoardApplication.MODE_RECENT_FIVE_GAMES:
		default:
			actionBar.setSelectedNavigationItem(0);
			break;
		}

		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.main_fragment, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.ImportThreeMonth:
			importThreeMonthData();
			return true;
		}

		FragmentActivity activity = getActivity();
		if (activity == null) {
			return false;
		}

		return activity.onOptionsItemSelected(item);
	}

	@Override
	public void onGameAndResultStarted() {
		if (progressBar != null) {
			getListView().setVisibility(View.INVISIBLE);
			noResultTextView.setVisibility(View.INVISIBLE);
			progressBar.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onGameAndResultFinished(OneGame[] results) {
		resultList.clear();
		for (OneGame result : results) {
			resultList.add(result);
		}
		reload(true);

		if (progressBar != null) {
			getListView().setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		if (itemPosition < 0 || itemPosition > navigationAdapter.getCount() - 1)
			return false;

		GolfScoreBoardApplication application = (GolfScoreBoardApplication) getActivity()
				.getApplication();
		switch (itemPosition) {
		case 1:
			application
					.setNavigationMode(GolfScoreBoardApplication.MODE_THIS_MONTH);
			break;
		case 2:
			application
					.setNavigationMode(GolfScoreBoardApplication.MODE_LAST_MONTH);
			break;
		case 3:
			application
					.setNavigationMode(GolfScoreBoardApplication.MODE_LAST_THREE_MONTHS);
			break;
		case 0:
		default:
			application
					.setNavigationMode(GolfScoreBoardApplication.MODE_RECENT_FIVE_GAMES);
			break;
		}

		reloadData();
		return true;
	}

	protected void reloadData() {
		if (navigationAdapter == null || navigationAdapter.getCount() < 1
				|| getActivity() == null)
			return;

		int selection = getActivity().getActionBar()
				.getSelectedNavigationIndex();
		if (selection < 0 || selection > navigationAdapter.getCount() - 1)
			return;

		DateItem dateItem = navigationAdapter.getItem(selection);
		GameAndResultTask task = new GameAndResultTask(getActivity(), this);
		task.execute(dateItem.getDateRange());
	}

	protected int getCurrentMode() {
		if (navigationAdapter == null || navigationAdapter.getCount() < 1
				|| getActivity() == null)
			return GolfScoreBoardApplication.MODE_RECENT_FIVE_GAMES;

		GolfScoreBoardApplication application = (GolfScoreBoardApplication) getActivity()
				.getApplication();
		return application.getNavigationMode();
	}

	protected void importThreeMonthData() {
		final FragmentActivity activity = getActivity();
		final HistoryItemListFragment instance = this;

		new AlertDialog.Builder(activity)
				.setTitle(R.string.dialog_import_three_month)
				.setMessage(R.string.dialog_are_you_sure_to_import_three_month)
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								ThreeMonthsGameReceiveTask task = new ThreeMonthsGameReceiveTask(
										activity, instance);
								task.execute();
							}

						}).setNegativeButton(android.R.string.no, null).show();
	}

	@Override
	public void onThreeMonthsGameReceiveStart() {
		showProgressDialog();
	}

	@Override
	public void onThreeMonthsGameReceiveProgressUpdate(ReceiveProgress progress) {
		if (progress == null)
			return;

		int current = progress.getCurrent();
		int total = progress.getTotal();

		setProgressDialogStatus(current, total, progress.getMessage());
	}

	@Override
	public void onThreeMonthsGameReceiveFinished(ReceiveResult result) {
		FragmentActivity activity = getActivity();
		if (activity == null)
			return;

		if (result.isSuccess()) {
			if (!result.isCancel()) {
				Toast.makeText(
						activity,
						R.string.fragment_attendcount_import_three_month_success,
						Toast.LENGTH_LONG).show();
				reloadData();
			}
		} else {
			Toast.makeText(activity,
					R.string.fragment_attendcount_import_three_month_fail,
					Toast.LENGTH_LONG).show();
		}
		hideProgressDialog();
	}

	protected void showProgressDialog() {
		FragmentActivity activity = getActivity();
		if (activity == null)
			return;

		if (progressDialog != null && progressDialog.isShowing()) {
			return;
		}

		progressDialog = new ProgressDialog(activity);
		progressDialog.setTitle(R.string.dialog_import_three_month);
		progressDialog
				.setMessage(getString(R.string.dialog_import_three_month_please_wait));
		progressDialog.setIndeterminate(true);
		progressDialog.setMax(100);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setCancelable(true);
		progressDialog.show();
	}

	protected void setProgressDialogStatus(int current, int total,
			final String message) {
		if (progressDialog == null) {
			showProgressDialog();
		} else {
			if (total <= 0) {
				progressDialog.setIndeterminate(true);
			} else {
				progressDialog.setIndeterminate(false);

				progressDialog.setMax(total);
				progressDialog.setProgress(current);
			}
			progressDialog.setMessage(message);
		}
	}

	protected void hideProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	protected abstract View inflate(LayoutInflater inflater);
}
