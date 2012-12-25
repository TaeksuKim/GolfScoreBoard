package org.dolicoli.android.golfscoreboard.net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import org.dolicoli.android.golfscoreboard.Constants;
import org.dolicoli.android.golfscoreboard.data.settings.GameSetting;
import org.dolicoli.android.golfscoreboard.data.settings.PlayerSetting;
import org.dolicoli.android.golfscoreboard.data.settings.Result;

import android.util.Log;

public class GameUploader {

	private static final String TAG = "GameUploader";

	private static final String PAGE = "updateGame";

	public static boolean upload(String host, GameSetting gameSetting,
			PlayerSetting playerSetting, ArrayList<Result> results) {
		URL url = null;
		try {
			url = new URL(host + PAGE);
		} catch (MalformedURLException e) {
			Log.e(TAG, e.getMessage(), e);
			return false;
		}

		HttpURLConnection httpURLCon = null;
		try {
			httpURLCon = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
			return false;
		}

		httpURLCon.setDefaultUseCaches(false);
		httpURLCon.setDoInput(true);
		httpURLCon.setDoOutput(true);
		try {
			httpURLCon.setRequestMethod("POST");
			httpURLCon.setRequestProperty("content-type",
					"application/x-www-form-urlencoded");
		} catch (ProtocolException e) {
			Log.e(TAG, e.getMessage(), e);
			return false;
		}

		Date date = gameSetting.getDate();
		String gameId = GameSetting.toGameIdFormat(date);
		StringBuffer buffer = new StringBuffer();
		buffer.append("gameId").append("=").append(gameId).append("&");
		buffer.append("gameDate").append("=").append(date.getTime())
				.append("&");
		buffer.append("holeCount").append("=")
				.append(gameSetting.getHoleCount()).append("&");
		buffer.append("playerCount").append("=")
				.append(gameSetting.getPlayerCount()).append("&");
		buffer.append("holeFee").append("=").append(gameSetting.getGameFee())
				.append("&");
		buffer.append("extraFee").append("=").append(gameSetting.getExtraFee())
				.append("&");
		buffer.append("rankingFee").append("=")
				.append(gameSetting.getRankingFee()).append("&");

		for (int i = 0; i < Constants.MAX_PLAYER_COUNT; i++) {
			buffer.append("holeFeePerRanking").append(i + 1).append("=")
					.append(gameSetting.getHoleFeeForRanking(i + 1))
					.append("&");
			buffer.append("rankingFeePerRanking").append(i + 1).append("=")
					.append(gameSetting.getRankingFeeForRanking(i + 1))
					.append("&");

			buffer.append("playerName").append(i).append("=")
					.append(playerSetting.getPlayerName(i)).append("&");
			buffer.append("handicap").append(i).append("=")
					.append(playerSetting.getHandicap(i)).append("&");
			buffer.append("extraScore").append(i).append("=")
					.append(playerSetting.getExtraScore(i)).append("&");
		}

		int resultCount = results.size();
		buffer.append("resultCount").append("=").append(resultCount)
				.append("&");

		for (int i = 0; i < resultCount; i++) {
			Result result = results.get(i);

			buffer.append("holeNumber_").append(i).append("=")
					.append(result.getHoleNumber()).append("&");
			buffer.append("parCount_").append(i).append("=")
					.append(result.getParNumber()).append("&");

			for (int j = 0; j < Constants.MAX_PLAYER_COUNT; j++) {
				buffer.append("result_score_").append(i).append("_").append(j)
						.append("=").append(result.getOriginalScore(j))
						.append("&");
				buffer.append("result_usedHandicap_").append(i).append("_")
						.append(j).append("=")
						.append(result.getUsedHandicap(j)).append("&");
			}
		}

		try {
			DataOutputStream pw = new DataOutputStream(
					httpURLCon.getOutputStream());
			pw.write(buffer.toString().getBytes("UTF-8"));
			pw.flush();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
			return false;
		}

		try {
			BufferedReader bf = new BufferedReader(new InputStreamReader(
					httpURLCon.getInputStream(), "UTF-8"));
			String line = bf.readLine();
			if (!line.equalsIgnoreCase("OK"))
				return false;
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, e.getMessage(), e);
			return false;
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
			return false;
		}

		return true;
	}
}
