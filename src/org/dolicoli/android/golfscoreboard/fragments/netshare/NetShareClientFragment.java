package org.dolicoli.android.golfscoreboard.fragments.netshare;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.dolicoli.android.golfscoreboard.GolfScoreBoardApplication;
import org.dolicoli.android.golfscoreboard.NetShareClientActivity;
import org.dolicoli.android.golfscoreboard.R;
import org.dolicoli.android.golfscoreboard.data.GameHistoryListItem;
import org.dolicoli.android.golfscoreboard.data.settings.GameSetting;
import org.dolicoli.android.golfscoreboard.data.settings.PlayerSetting;
import org.dolicoli.android.golfscoreboard.data.settings.Result;
import org.dolicoli.android.golfscoreboard.db.GameSettingDatabaseWorker;
import org.dolicoli.android.golfscoreboard.db.HistoryGameSettingDatabaseWorker;
import org.dolicoli.android.golfscoreboard.db.PlayerSettingDatabaseWorker;
import org.dolicoli.android.golfscoreboard.db.ResultDatabaseWorker;
import org.dolicoli.android.golfscoreboard.net.GameListParser;
import org.dolicoli.android.golfscoreboard.net.GameParser;
import org.dolicoli.android.golfscoreboard.net.HttpScraper;
import org.dolicoli.android.golfscoreboard.net.ResponseException;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class NetShareClientFragment extends ListFragment implements
		OnClickListener, OnItemSelectedListener {

	private static final String TAG = "NetShareClientFragment";

	private View progressPanel;
	private Spinner dateRangeSpinner;
	private Button refreshButton, receiveButton;
	private TextView notAvailableStatusTextView, statusTextView;
	private ProgressBar progressBar;
	private ArrayAdapter<DateItem> dateRangeSpinnerAdapter;

	private GameHistoryAdapter adapter;
	private BroadcastReceiver broadcastReceiver;

	private GameHistoryListReceiveTask gameListTask;
	private GameReceiveTask gameTask;

	private String currentGameId;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.netshare_client_fragment, null);

		broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
					setUIStatus();
					return;
				}
			}
		};

		progressPanel = view.findViewById(R.id.ProgressPanel);

		progressBar = (ProgressBar) view
				.findViewById(R.id.ReceivingProgressBar);
		notAvailableStatusTextView = (TextView) view
				.findViewById(R.id.NotAvailableStatusTextView);
		statusTextView = (TextView) view.findViewById(R.id.StatusTextView);

		dateRangeSpinner = (Spinner) view.findViewById(R.id.DateRangeSpinner);

		dateRangeSpinnerAdapter = new ArrayAdapter<DateItem>(getActivity(),
				android.R.layout.simple_spinner_dropdown_item,
				getDateRangeItems());
		dateRangeSpinner.setAdapter(dateRangeSpinnerAdapter);

		refreshButton = (Button) view.findViewById(R.id.RefreshButton);
		refreshButton.setOnClickListener(this);
		receiveButton = (Button) view.findViewById(R.id.ReceiveButton);
		receiveButton.setOnClickListener(this);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		adapter = new GameHistoryAdapter(getActivity(),
				R.layout.netshare_client_list_item);
		setListAdapter(adapter);
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				setUIStatus();
			}
		});
		getListView().setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				setUIStatus();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				setUIStatus();
			}
		});

		dateRangeSpinner.setOnItemSelectedListener(this);

		currentGameId = null;
		Intent intent = getActivity().getIntent();
		if (intent != null) {
			currentGameId = intent
					.getStringExtra(NetShareClientActivity.IK_GAME_ID);
		}
		if (isOnline()) {
			requestList(true);
		}
		setUIStatus();
	}

	@Override
	public void onResume() {
		super.onResume();
		setUIStatus();
		IntentFilter intentFilter1 = new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION);
		getActivity().registerReceiver(broadcastReceiver, intentFilter1);
	}

	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(broadcastReceiver);
		super.onDestroy();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		switch (id) {
		case R.id.RefreshButton:
			requestList(false);
			return;
		case R.id.ReceiveButton:
			receive();
			return;
		}
	}

	private void setUIStatus() {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (!isOnline()) {
					progressPanel.setVisibility(View.GONE);

					notAvailableStatusTextView
							.setText(R.string.fragment_netshare_client_message_not_avaliable);
					notAvailableStatusTextView.setVisibility(View.VISIBLE);
					getListView().setVisibility(View.GONE);
					dateRangeSpinner.setVisibility(View.GONE);

					receiveButton.setEnabled(false);
					return;
				}

				if (gameListTask != null && gameListTask.running) {
					progressPanel.setVisibility(View.VISIBLE);

					getListView().setEnabled(false);
					dateRangeSpinner.setEnabled(false);
					receiveButton.setEnabled(false);
					return;
				}

				if (gameTask != null && gameTask.running) {
					progressPanel.setVisibility(View.VISIBLE);

					getListView().setEnabled(false);
					dateRangeSpinner.setEnabled(false);
					receiveButton.setEnabled(false);
					return;
				}

				getListView().setEnabled(true);
				dateRangeSpinner.setEnabled(true);
				progressPanel.setVisibility(View.GONE);
				notAvailableStatusTextView.setVisibility(View.GONE);
				getListView().setVisibility(View.VISIBLE);
				dateRangeSpinner.setVisibility(View.VISIBLE);

				boolean selected = (getSelectedData() != null);
				receiveButton.setEnabled(selected);
			}
		});
	}

	private GameHistoryListItem getSelectedData() {
		int pos = getListView().getCheckedItemPosition();
		if (pos < 0)
			return null;

		if (pos > adapter.getCount() - 1)
			return null;

		return adapter.getItem(pos);
	}

	private ArrayList<DateItem> getDateRangeItems() {
		ArrayList<DateItem> items = new ArrayList<DateItem>();

		Calendar toCalendar = Calendar.getInstance();
		toCalendar.set(Calendar.HOUR_OF_DAY, 23);
		toCalendar.set(Calendar.MINUTE, 59);

		Calendar fromCalendar = Calendar.getInstance();
		fromCalendar.setTimeInMillis(toCalendar.getTimeInMillis());
		int dayOfMonth = fromCalendar.get(Calendar.DAY_OF_MONTH);
		fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
		fromCalendar.set(Calendar.MINUTE, 1);
		fromCalendar.add(Calendar.DAY_OF_MONTH, dayOfMonth * -1 + 1);

		String text = DateUtils.formatDateTime(getActivity(),
				fromCalendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE
						| DateUtils.FORMAT_SHOW_YEAR);
		items.add(new DateItem(GameSetting.toGameIdFormat(fromCalendar
				.getTime()), GameSetting.toGameIdFormat(toCalendar.getTime()),
				text + " - " + getString(R.string.today)));

		toCalendar.setTimeInMillis(fromCalendar.getTimeInMillis());
		toCalendar.add(Calendar.DAY_OF_YEAR, -1);
		toCalendar.set(Calendar.HOUR_OF_DAY, 23);
		toCalendar.set(Calendar.MINUTE, 59);

		fromCalendar.add(Calendar.MONTH, -3);

		String fromText = DateUtils.formatDateTime(getActivity(),
				fromCalendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE
						| DateUtils.FORMAT_SHOW_YEAR);
		String toText = DateUtils.formatDateTime(getActivity(),
				toCalendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE
						| DateUtils.FORMAT_SHOW_YEAR);
		items.add(new DateItem(GameSetting.toGameIdFormat(fromCalendar
				.getTime()), GameSetting.toGameIdFormat(toCalendar.getTime()),
				fromText + " - " + toText));

		fromCalendar.add(Calendar.MONTH, -3);
		toCalendar.add(Calendar.MONTH, -3);

		fromText = DateUtils.formatDateTime(getActivity(),
				fromCalendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE
						| DateUtils.FORMAT_SHOW_YEAR);
		toText = DateUtils.formatDateTime(getActivity(),
				toCalendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE
						| DateUtils.FORMAT_SHOW_YEAR);
		items.add(new DateItem(GameSetting.toGameIdFormat(fromCalendar
				.getTime()), GameSetting.toGameIdFormat(toCalendar.getTime()),
				fromText + " - " + toText));

		fromCalendar.add(Calendar.MONTH, -3);
		toCalendar.add(Calendar.MONTH, -3);

		fromText = DateUtils.formatDateTime(getActivity(),
				fromCalendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE
						| DateUtils.FORMAT_SHOW_YEAR);
		toText = DateUtils.formatDateTime(getActivity(),
				toCalendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE
						| DateUtils.FORMAT_SHOW_YEAR);
		items.add(new DateItem(GameSetting.toGameIdFormat(fromCalendar
				.getTime()), GameSetting.toGameIdFormat(toCalendar.getTime()),
				fromText + " - " + toText));

		fromCalendar.add(Calendar.MONTH, -3);
		toCalendar.add(Calendar.MONTH, -3);

		toText = DateUtils.formatDateTime(getActivity(),
				toCalendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE
						| DateUtils.FORMAT_SHOW_YEAR);
		items.add(new DateItem(null, GameSetting.toGameIdFormat(toCalendar
				.getTime()), " - " + toText));

		return items;
	}

	private static class DateItem {
		private String from, to;
		private String title;

		public DateItem(String from, String to, String string) {
			this.from = from;
			this.to = to;
			this.title = string;
		}

		@Override
		public String toString() {
			return title;
		}
	}

	private boolean isOnline() {
		if (getActivity() == null)
			return false;

		ConnectivityManager cm = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	private void setCheckCurrentGame() {
		if (currentGameId == null) {
			return;
		}

		int count = adapter.getCount();
		for (int i = 0; i < count; i++) {
			GameHistoryListItem item = adapter.getItem(i);
			if (item.getGameId().equals(currentGameId)) {
				getListView().setItemChecked(i, true);
				return;
			}
		}
	}

	private static class NetShareDeviceListViewHolder {
		TextView date1TextView, date2TextView, playerTextView;
	}

	private static class GameHistoryAdapter extends
			ArrayAdapter<GameHistoryListItem> {

		private NetShareDeviceListViewHolder holder;

		public GameHistoryAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.netshare_client_list_item, null);
				holder = new NetShareDeviceListViewHolder();
				holder.date1TextView = (TextView) v
						.findViewById(R.id.Date1TextView);
				holder.date2TextView = (TextView) v
						.findViewById(R.id.Date2TextView);
				holder.playerTextView = (TextView) v
						.findViewById(R.id.PlayerTextView);
				v.setTag(holder);
			} else {
				holder = (NetShareDeviceListViewHolder) v.getTag();
			}

			int count = getCount();
			if (count < 1)
				return v;

			if (position < 0 || position > count - 1)
				return v;

			GameHistoryListItem gameSetting = getItem(position);
			if (gameSetting == null)
				return v;

			String date1String = DateUtils.formatDateTime(getContext(),
					gameSetting.getDate().getTime(), DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_SHOW_YEAR);
			holder.date1TextView.setText(date1String);

			String date2String = DateUtils.formatDateTime(getContext(),
					gameSetting.getDate().getTime(), DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_ABBREV_WEEKDAY
							| DateUtils.FORMAT_SHOW_WEEKDAY
							| DateUtils.FORMAT_12HOUR);
			holder.date2TextView.setText(date2String);

			StringBuffer buffer = new StringBuffer();
			int playerCount = gameSetting.getPlayerCount();
			for (int i = 0; i < playerCount; i++) {
				buffer.append(gameSetting.getPlayerName(i));
				buffer.append(" ");
			}
			holder.playerTextView.setText(buffer.toString());

			return v;
		}
	}

	private void receive() {
		GameHistoryListItem selection = getSelectedData();
		if (selection == null)
			return;

		gameTask = new GameReceiveTask(getActivity());
		gameTask.execute(selection.getGameId());
	}

	private void requestList(boolean force) {
		DateItem item = null;
		if (!force) {
			item = (DateItem) dateRangeSpinner.getSelectedItem();
		}

		if (item == null)
			return;

		gameListTask = new GameHistoryListReceiveTask(getActivity());
		gameListTask.execute(item);
	}

	private void requestFailed(int msgId) {
		statusTextView.setText(msgId);
		Toast.makeText(getActivity(), msgId, Toast.LENGTH_LONG).show();
		setUIStatus();
	}

	private void requestFinished(int msgId) {
		statusTextView.setText(msgId);
		setUIStatus();
	}

	private static class ReceiveProgress {
		@SuppressWarnings("unused")
		public ReceiveProgress(int total, int current, int msgId) {
			this.msgId = msgId;
		}

		int msgId;
	}

	private static class GameListResponse {
		private boolean error;
		@SuppressWarnings("unused")
		private String errorMessage;
		private List<GameHistoryListItem> games;
	}

	private class GameHistoryListReceiveTask extends
			AsyncTask<DateItem, ReceiveProgress, GameListResponse> {

		// private static final int RESULT_OK = 100;
		// private static final int RESULT_RECEIVE_ERROR = 10;
		// private static final int RESULT_CONNECTION_ERROR = 11;

		private String host;
		private boolean running;

		public GameHistoryListReceiveTask(Context context) {
			running = true;

			GolfScoreBoardApplication application = (GolfScoreBoardApplication) context
					.getApplicationContext();
			host = application.getWebHost();
		}

		@SuppressWarnings("unused")
		public void cancel() {
		}

		@Override
		protected void onProgressUpdate(ReceiveProgress... values) {
			super.onProgressUpdate(values);
			if (values.length < 1)
				return;

			setUIStatus();
			ReceiveProgress value = values[0];
			progressBar.setVisibility(View.VISIBLE);
			statusTextView.setText(value.msgId);
		}

		protected void onPreExecute() {
			super.onPreExecute();

			running = true;
			progressBar.setVisibility(View.VISIBLE);
			statusTextView
					.setText(R.string.fragment_netshare_client_message_connecting);

			setUIStatus();
		}

		@Override
		protected void onPostExecute(GameListResponse response) {
			super.onPostExecute(response);

			running = false;
			progressBar.setVisibility(View.GONE);

			if (response.error) {
				requestFailed(R.string.fragment_netshare_client_message_receive_error);
			} else {
				requestFinished(R.string.fragment_netshare_client_message_receive_finished);

				adapter.clear();
				for (GameHistoryListItem setting : response.games) {
					adapter.add(setting);
				}
				adapter.notifyDataSetChanged();

				setCheckCurrentGame();
			}

			setUIStatus();
		}

		private static final String PAGE = "games";

		@Override
		protected GameListResponse doInBackground(DateItem... args) {
			String url = host + PAGE;

			if (args != null && args.length > 0 && args[0] != null) {
				DateItem dateItem = args[0];
				if (dateItem.to != null) {
					url += "?to=" + dateItem.to;
				}
				if (dateItem.from != null) {
					url += "&from=" + dateItem.from;
				}
			}

			GameListResponse response = new GameListResponse();

			String contents = null;
			try {
				contents = HttpScraper.scrap(url, HttpScraper.UTF_8);
			} catch (Exception e) {
				Log.e(TAG, e.getLocalizedMessage(), e);
				response.error = true;
				contents = null;
			}

			if (contents == null || contents.isEmpty()) {
				response.error = true;
				response.errorMessage = "";
				return response;
			}

			try {
				List<GameHistoryListItem> gameList = GameListParser
						.getGameList(getActivity(), contents);
				response.error = false;
				response.games = gameList;
			} catch (ResponseException e) {
				response.error = true;
				response.errorMessage = e.getErrorMessage();
				return response;

			}

			return response;
		}
	}

	private class GameReceiveTask extends
			AsyncTask<String, ReceiveProgress, Boolean> {

		private String host;
		private boolean running;

		public GameReceiveTask(Context context) {
			running = true;

			GolfScoreBoardApplication application = (GolfScoreBoardApplication) context
					.getApplicationContext();
			host = application.getWebHost();
		}

		@Override
		protected void onProgressUpdate(ReceiveProgress... values) {
			super.onProgressUpdate(values);
			if (values.length < 1)
				return;

			setUIStatus();
			ReceiveProgress value = values[0];
			progressBar.setVisibility(View.VISIBLE);
			statusTextView.setText(value.msgId);
		}

		protected void onPreExecute() {
			super.onPreExecute();

			running = true;
			progressBar.setVisibility(View.VISIBLE);
			statusTextView
					.setText(R.string.fragment_netshare_client_message_connecting);

			setUIStatus();
		}

		@Override
		protected void onPostExecute(Boolean error) {
			super.onPostExecute(error);

			running = false;
			progressBar.setVisibility(View.GONE);

			if (error) {
				requestFailed(R.string.fragment_netshare_client_message_receive_error);
			} else {
				getActivity().setResult(Activity.RESULT_OK);
				getActivity().finish();
				return;
			}

			setUIStatus();
		}

		private static final String PAGE = "game";

		@Override
		protected Boolean doInBackground(String... args) {
			if (args == null || args.length < 1) {
				return true;
			}

			String url = host + PAGE + "?gameId=" + args[0];

			String contents = null;
			try {
				contents = HttpScraper.scrap(url, HttpScraper.UTF_8);
			} catch (Exception e) {
				Log.e(TAG, e.getLocalizedMessage(), e);
				return true;
			}

			if (contents == null || contents.isEmpty()) {
				return true;
			}

			try {
				GameParser parser = new GameParser();
				if (!parser.parse(getActivity(), contents)) {
					return true;
				}

				GameSetting gameSetting = parser.getGameSetting();
				PlayerSetting playerSetting = parser.getPlayerSetting();
				ArrayList<Result> results = parser.getResults();

				GameSettingDatabaseWorker gameSettingWorker = new GameSettingDatabaseWorker(
						getActivity());
				gameSettingWorker.updateGameSetting(gameSetting);

				PlayerSettingDatabaseWorker playerSettingWorker = new PlayerSettingDatabaseWorker(
						getActivity());
				playerSettingWorker.updatePlayerSetting(playerSetting);

				ResultDatabaseWorker resultWorker = new ResultDatabaseWorker(
						getActivity());
				resultWorker.cleanUpAllData();

				for (Result result : results) {
					resultWorker.updateResult(result);
				}

				HistoryGameSettingDatabaseWorker historyWorker = new HistoryGameSettingDatabaseWorker(
						getActivity());
				historyWorker.addCurrentHistory(false, gameSetting,
						playerSetting, results);
			} catch (ResponseException e) {
				return true;
			}

			return false;
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		requestList(false);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

}
