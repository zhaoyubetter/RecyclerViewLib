package ui.github.com.library.refresh.headerstrategy;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.VelocityTracker;
import android.view.View;

import ui.github.com.library.refresh.PullToRefreshLayout;
import ui.github.com.library.refresh.RefreshState;
import ui.github.com.library.refresh.header.RefreshHeader;

import static ui.github.com.library.refresh.RefreshState.RELEASE_REFRESHING_START;

/**
 * overlap策略，刷新头在 下层,位置不动，区别于Follow策略
 */
public class HeaderOverlapStrategy extends HeaderStrategy {

	public HeaderOverlapStrategy(PullToRefreshLayout layout) {
		super(layout);
	}

	@Override
	public void onInitRefreshHeader(RefreshHeader refreshHeader) {
		if (null == refreshHeader || null == refreshHeader.getRefreshHeaderView()) {
			throw new NullPointerException("the new header is null!");
		}
		// 获取HeaderView
		final View refreshHeaderView = refreshHeader.getRefreshHeaderView();
		// 添加HeaderView
		swapHeaderView(refreshHeaderView);
		// bringToFront
		mPullToRefreshLayout.getRefreshView().bringToFront();
	}

	@Override
	public void onLayout(boolean changed, int l, int t, int r, int b) {
		// 布局HeaderView
		View headerView = mPullToRefreshLayout.getRefreshHeader().getRefreshHeaderView();
		// refreshView
		View refreshView = mPullToRefreshLayout.getRefreshView();

		RefreshState refreshState = mPullToRefreshLayout.getRefreshState();

		/** 可 {@link HeaderOverlapStrategy#back}   requestLayout 的调用*/
		if (RefreshState.NONE == refreshState) {    // requestLayout 导致 onLayout 会执行
			// NONE 时，headerView 不可见
			if (null != headerView) {                // 因为 onLayout后，才能获取 getWidth/Height，所以这里用 MeasureWidth/Height
				headerView.layout(0, 0, headerView.getMeasuredWidth(), headerView.getMeasuredHeight());
			}
			refreshView.layout(0, 0, refreshView.getMeasuredWidth(), refreshView.getMeasuredHeight());
		}
	}

	@Override
	public boolean isIntercept(float distanceY) {
		// 区别于 FollowStrategy策略，这里使用 refreshView.getTop()
		View refreshView = mPullToRefreshLayout.getRefreshView();
		boolean isTop = mPullToRefreshLayout.isChildScrollToTop();
		return isTop && (distanceY > 0 || refreshView.getTop() > 20);
	}

	@Override
	public void onMoveOffset(float distanceY) {
		final float resistance = mPullToRefreshLayout.getResistance();
		final float maxHeight = mPullToRefreshLayout.getPullMaxHeight();
		final View refreshView = mPullToRefreshLayout.getRefreshView();
		final int top = refreshView.getTop();    // 区别于 Follow策略

		// move距离
		int moveDistance = (int) (distanceY / resistance);    // 阻尼运动
		if (distanceY > 0 && top >= maxHeight) {
			moveDistance = 0;
		}

		refreshView.offsetTopAndBottom(moveDistance);        // 使用新方案

		// 根据下拉距离设置 mPullToRefreshLayout 刷新状态
		final RefreshHeader refreshHeader = mPullToRefreshLayout.getRefreshHeader();
		final int headerHeight = refreshHeader.getHeaderHeight();
		float fraction = top * 1.0f / headerHeight;
		if (fraction > 1.0f) fraction = 1.0f;

		// move 时改变状态
		mPullToRefreshLayout.refreshStateChange(fraction);
		// 改变头
		refreshHeader.onRefreshOffset(fraction, top, headerHeight);
	}

	@Override
	public boolean isMoveToTop() {
		View refreshView = mPullToRefreshLayout.getRefreshView();
		boolean result = false;
		if (null != refreshView) {
			result = mPullToRefreshLayout.isChildScrollToTop() && Math.abs(refreshView.getTop()) < 40;
		}
		if (DEBUG)
			Log.e(TAG, "HeaderOverlapStrategy -----》 move to top:" + result);
		return result;
	}

	@Override
	public void onResetRefresh(RefreshState state) {
		RefreshHeader refreshHeader = mPullToRefreshLayout.getRefreshHeader();
		final View refreshView = mPullToRefreshLayout.getRefreshView();

		final int top = mPullToRefreshLayout.getRefreshView().getTop();            // top距离
		if (RefreshState.NONE == state) {
			mPullToRefreshLayout.layout(0, 0, refreshView.getMeasuredWidth(), refreshView.getMeasuredHeight());
		} else if (RefreshState.PULL_START == state) {    // 还原
			offsetOverlapHeader(top, new Runnable() {
				@Override
				public void run() {
					mPullToRefreshLayout.setRefreshState(RefreshState.NONE);
				}
			});
		} else if (RELEASE_REFRESHING_START == state) {     // 刷新中，拖动并释放
			VelocityTracker velocityTracker = mPullToRefreshLayout.getVelocityTracker();
			int scaledMinimumFlingVelocity = mPullToRefreshLayout.getScaledMinimumFlingVelocity();
			float yVelocity = Math.abs(velocityTracker.getYVelocity());
			if (yVelocity > scaledMinimumFlingVelocity) {
				View refreshHeaderView = refreshHeader.getRefreshHeaderView();
				int measuredHeight = refreshHeaderView.getMeasuredHeight();
				// 显示头
				offsetOverlapHeader(top - measuredHeight, null);
				mPullToRefreshLayout.setRefreshState(RefreshState.START_REFRESHING);
			} else { // 非 fling 状态，还原
				offsetOverlapHeader(top, null);
			}
		} else if (RefreshState.RELEASE_START == state || RefreshState.START_REFRESHING == state) {
			// 释放刷新，刷新中
			View refreshHeaderView = refreshHeader.getRefreshHeaderView();
			int measuredHeight = refreshHeaderView.getMeasuredHeight();
			// 显示头
			offsetOverlapHeader(top - measuredHeight, null);
			mPullToRefreshLayout.setRefreshState(RefreshState.START_REFRESHING);
		}
	}

	private void offsetOverlapHeader(int top, final Runnable action) {
		final View refreshView = mPullToRefreshLayout.getRefreshView();
		ValueAnimator valueAnimator = ValueAnimator.ofInt(top);
		valueAnimator.setDuration(mPullToRefreshLayout.getScrollDuration());
		valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			private int lastValue;

			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				Integer value = Integer.valueOf(valueAnimator.getAnimatedValue().toString());
				refreshView.offsetTopAndBottom(lastValue - value);
				lastValue = value;
			}
		});
		valueAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				super.onAnimationEnd(animation);
				if (null != action) {
					action.run();
				}
			}
		});
		valueAnimator.start();
	}

	@Override
	public void onRefreshComplete() {
		RefreshState refreshState = mPullToRefreshLayout.getRefreshState();
		if (RefreshState.START_REFRESHING == refreshState || RefreshState.RELEASE_REFRESHING_START == refreshState) {
			final int scrollDuration = mPullToRefreshLayout.getScrollDuration();
			// 需要显示刷新完成
			if (mPullToRefreshLayout.isShowRefreshCompleteInfo()) {
				// 显示一下刷新完成，并延迟回位
				mPullToRefreshLayout.setRefreshState(RefreshState.REFRESHING_START_COMPLETE);
				mPullToRefreshLayout.postDelayed(new Runnable() {
					@Override
					public void run() {
						back(scrollDuration);
					}
				}, scrollDuration);
			} else {
				back(scrollDuration);
			}
		} else {
			mPullToRefreshLayout.setRefreshState(RefreshState.NONE);
		}
	}

	/**
	 * 刷完回位
	 *
	 * @param scrollDuration
	 */
	private void back(int scrollDuration) {
		if (mPullToRefreshLayout != null) {
			final int top = mPullToRefreshLayout.getRefreshView().getTop();
			final View refreshView = mPullToRefreshLayout.getRefreshView();
			ValueAnimator animator = new ValueAnimator();
			animator.setIntValues(top);
			animator.setDuration(scrollDuration);
			animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
				int lastValue;

				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					Integer value = Integer.valueOf(animation.getAnimatedValue().toString());
					refreshView.offsetTopAndBottom(lastValue - value);
					lastValue = value;
				}
			});
			animator.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					super.onAnimationEnd(animation);
					mPullToRefreshLayout.setRefreshState(RefreshState.NONE);
					mPullToRefreshLayout.requestLayout();
				}
			});
			animator.start();
		}
	}

	@Override
	public void autoRefreshing(boolean anim) {
		if (mPullToRefreshLayout.getRefreshState() != RefreshState.START_REFRESHING) {
			int headerHeight = mPullToRefreshLayout.getRefreshHeader().getHeaderHeight();
			if (anim) {
				offsetOverlapHeader(-headerHeight, null);    // 负数
			} else {
				View refreshView = mPullToRefreshLayout.getRefreshView();
				refreshView.offsetTopAndBottom(headerHeight);
			}
			// 触发
			mPullToRefreshLayout.callRefreshListener();
			mPullToRefreshLayout.setRefreshState(RefreshState.START_REFRESHING);
		}
	}

}
