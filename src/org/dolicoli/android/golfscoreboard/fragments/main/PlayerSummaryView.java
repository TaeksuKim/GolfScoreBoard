package org.dolicoli.android.golfscoreboard.fragments.main;

import org.dolicoli.android.golfscoreboard.R;
import org.dolicoli.android.golfscoreboard.utils.PlayerUIUtil;
import org.dolicoli.android.golfscoreboard.utils.UIUtil;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class PlayerSummaryView {

	private View parent;

	private View largeView, mediumView;

	private ImageView playerLargeImageView, playerMediumImageView;
	private View playerLargeTagView, playerMediumTagView;
	private TextView playerLargeNameTextView, playerMediumNameTextView;
	private TextView playerLargeScoreTextView, playerMediumScoreTextView;
	private TextView playerLargeTotalFeeTextView, playerMediumTotalFeeTextView;

	public PlayerSummaryView(View parent) {
		this.parent = parent;

		largeView = parent.findViewById(R.id.PlayerLargeView);
		playerLargeTagView = parent.findViewById(R.id.PlayerLargeTagView);
		playerLargeImageView = (ImageView) parent
				.findViewById(R.id.PlayerLargeImageView);
		playerLargeNameTextView = (TextView) parent
				.findViewById(R.id.PlayerLargeNameTextView);
		playerLargeScoreTextView = (TextView) parent
				.findViewById(R.id.PlayerLargeScoreTextView);
		playerLargeTotalFeeTextView = (TextView) parent
				.findViewById(R.id.PlayerLargeTotalFeeTextView);

		mediumView = parent.findViewById(R.id.PlayerMediumView);
		playerMediumTagView = parent.findViewById(R.id.PlayerMediumTagView);
		playerMediumImageView = (ImageView) parent
				.findViewById(R.id.PlayerMediumImageView);
		playerMediumNameTextView = (TextView) parent
				.findViewById(R.id.PlayerMediumNameTextView);
		playerMediumScoreTextView = (TextView) parent
				.findViewById(R.id.PlayerMediumScoreTextView);
		playerMediumTotalFeeTextView = (TextView) parent
				.findViewById(R.id.PlayerMediumTotalFeeTextView);
	}

	public void setValue(Context context, String playerName, int ranking,
			int score, int fee) {

		if (ranking <= 1) {
			playerLargeImageView.setImageResource(PlayerUIUtil
					.getRoundResourceId(playerName));
			playerLargeTagView.setBackgroundColor(PlayerUIUtil
					.getTagColor(playerName));
			playerLargeNameTextView.setText(playerName);
			UIUtil.setScoreTextView(context, playerLargeScoreTextView, score);
			UIUtil.setFeeTextView(context, playerLargeTotalFeeTextView, fee);

			largeView.setVisibility(View.VISIBLE);
			mediumView.setVisibility(View.GONE);
		} else {
			playerMediumImageView.setImageResource(PlayerUIUtil
					.getRoundResourceId(playerName));
			playerMediumTagView.setBackgroundColor(PlayerUIUtil
					.getTagColor(playerName));
			playerMediumNameTextView.setText(playerName);
			UIUtil.setScoreTextView(context, playerMediumScoreTextView, score);
			UIUtil.setFeeTextView(context, playerMediumTotalFeeTextView, fee);

			largeView.setVisibility(View.GONE);
			mediumView.setVisibility(View.VISIBLE);
		}
	}

	public void setVisibility(int visibility) {
		parent.setVisibility(visibility);
	}
}
