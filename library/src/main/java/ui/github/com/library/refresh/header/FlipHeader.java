package ui.github.com.library.refresh.header;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import ui.github.com.library.R;
import ui.github.com.library.refresh.RefreshState;

import static ui.github.com.library.refresh.RefreshState.RELEASE_START;

/**
 * 参考: cz
 */

public class FlipHeader extends RefreshHeader {

	ImageView mIv;
	View progressBar;
	TextView mInfoText;
	RefreshState mLastState;
	Animation rotateAnimation;
	Animation resetRotateAnimation;


	public FlipHeader(Context context, ViewGroup parent) {
		super(context, parent);
		View view = LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header_flip_layout, parent, false);
		this.mHeaderView = view;
		mIv = (ImageView) view.findViewById(R.id.iv_indicator);
		progressBar = view.findViewById(R.id.progress);
		mInfoText = (TextView) view.findViewById(R.id.refresh_text);

		mLastState = RefreshState.NONE;


		Interpolator ANIMATION_INTERPOLATOR = new LinearInterpolator();
		rotateAnimation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		rotateAnimation.setInterpolator(ANIMATION_INTERPOLATOR);
		rotateAnimation.setDuration(200);
		rotateAnimation.setFillAfter(true);

		resetRotateAnimation = new RotateAnimation(180, 360, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		resetRotateAnimation.setInterpolator(ANIMATION_INTERPOLATOR);
		resetRotateAnimation.setDuration(200);
		resetRotateAnimation.setFillAfter(true);
	}

	@Override
	public View getRefreshHeaderView() {
		return mHeaderView;
	}

	@Override
	public void onRefreshStateChange(RefreshState state) {
		switch (state) {
			case RELEASE_START:
				mInfoText.setText(R.string.pull_to_refresh_release);
				progressBar.setVisibility(View.GONE);
				mIv.setVisibility(View.VISIBLE);
				mIv.startAnimation(rotateAnimation);
				break;
			case START_REFRESHING:
				mHeaderView.setVisibility(View.VISIBLE);
			case RELEASE_REFRESHING_START:
				mInfoText.setText(R.string.pull_to_refresh_refreshing);
				mIv.clearAnimation();
				mIv.setVisibility(View.GONE);
				progressBar.setVisibility(View.VISIBLE);
				break;
			case REFRESHING_START_COMPLETE:
				progressBar.setVisibility(View.GONE);
				mIv.setVisibility(View.GONE);
				mInfoText.setText(R.string.pull_to_refresh_complete);
				break;
			case PULL_START:
				mHeaderView.setVisibility(View.VISIBLE);
				if (RELEASE_START == mLastState) {
					mIv.startAnimation(resetRotateAnimation);
				}
				progressBar.setVisibility(View.GONE);
				mIv.setVisibility(View.VISIBLE);
				mInfoText.setText(R.string.pull_to_refresh_pull);
				break;
			case NONE:
				mHeaderView.setVisibility(View.GONE);
				break;
		}
		mLastState = state;
	}

	@Override
	public void onRefreshOffset(float fraction, int scrollY, int headerHeight) {
		// do nothing
	}
}
