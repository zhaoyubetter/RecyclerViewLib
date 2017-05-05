package ui.github.com.library.refresh.header;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import ui.github.com.library.R;
import ui.github.com.library.refresh.RefreshState;

/**
 * 指示器头
 */
public class IndicatorHeader extends RefreshHeader {

	ImageView mImageView;
	TextView mInfoText;
	RotateAnimation mAnimation;

	public IndicatorHeader(Context context, ViewGroup parent) {
		super(context, parent);
		View view = LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header_follow_layout, parent, false);
		this.mHeaderView = view;
		mImageView = (ImageView) view.findViewById(R.id.pull_to_refresh_iv_indicator);
		mInfoText = (TextView) view.findViewById(R.id.pull_to_refresh_text);
		mAnimation = new RotateAnimation(0, 720, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		mAnimation.setDuration(1000);
		mAnimation.setInterpolator(new LinearInterpolator());
		mAnimation.setRepeatCount(Animation.INFINITE);
		mAnimation.setRepeatMode(Animation.RESTART);
	}

	@Override
	public View getRefreshHeaderView() {
		return mHeaderView;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param state 刷新状态
	 */
	@Override
	public void onRefreshStateChange(RefreshState state) {
		if (DEBUG) {
			Log.e(TAG, "onRefreshStateChange -> RefreshState: " + state);
		}

		switch (state) {
			case RELEASE_START:
				mInfoText.setText(R.string.pull_to_refresh_release);            // 下拉刷新
				break;
			case START_REFRESHING:                // 准备刷新
				mHeaderView.setVisibility(View.VISIBLE);
			case RELEASE_REFRESHING_START:             // 释放刷新
				mInfoText.setText(R.string.pull_to_refresh_refreshing);         // 松开刷新
				mImageView.startAnimation(mAnimation);
				break;
			case NONE:
				mHeaderView.setVisibility(View.GONE); // 去掉
				break;
			case PULL_START:
			default:
				mHeaderView.setVisibility(View.VISIBLE);
				mImageView.clearAnimation();
				mInfoText.setText(R.string.pull_to_refresh_pull);
				break;
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param fraction
	 * @param scrollY
	 * @param headerHeight
	 */
	@Override
	public void onRefreshOffset(float fraction, int scrollY, int headerHeight) {
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
			mImageView.setRotation(360 * fraction);
		}
	}
}
