package ui.github.com.library.refresh.headerstrategy;

import android.util.Log;
import android.view.VelocityTracker;
import android.view.View;

import ui.github.com.library.refresh.PullToRefreshLayout;
import ui.github.com.library.refresh.RefreshState;
import ui.github.com.library.refresh.header.RefreshHeader;

import static ui.github.com.library.refresh.RefreshState.RELEASE_REFRESHING_START;


/**
 * FollowHeader 策略
 */
public class HeaderFollowStrategy extends HeaderStrategy {


	public HeaderFollowStrategy(PullToRefreshLayout layout) {
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
	public void onLayout(boolean isChange, int l, int t, int r, int b) {
		// 头
		View headerView = mPullToRefreshLayout.getRefreshHeader().getRefreshHeaderView();
		// refreshView
		View refreshView = mPullToRefreshLayout.getRefreshView();

		if (null != headerView) {
			headerView.layout(l, -headerView.getMeasuredHeight(), l + r, t);
		}
		refreshView.layout(l, t, r, b);
	}

	@Override
	public boolean isIntercept(float distanceY) {
		boolean isTop = mPullToRefreshLayout.isChildScrollToTop();
		// 是否移动到顶部 || 在下拉
		return ((distanceY > 0 && isTop) || (mPullToRefreshLayout.getScrollY() < 0));
	}

	/**
	 * move 处理
	 *
	 * @param distanceY 移动偏移量
	 */
	@Override
	public void onMoveOffset(float distanceY) {
		final int scrollY = Math.abs(mPullToRefreshLayout.getScrollY());   // 垂直滚动距离
		final float resistance = mPullToRefreshLayout.getResistance();
		int moveDistanceY = (int) (distanceY / resistance);     // 阻尼运动，原距离/1.8f
		final float pullMaxHeight = mPullToRefreshLayout.getPullMaxHeight();

		// 1. 下拉时 - 界面变动
		// 往下滑动，并且达到最大距离，设置不能滑动
		if (distanceY > 0 && scrollY >= pullMaxHeight) {
			moveDistanceY = 0;
		}
		mPullToRefreshLayout.scrollBy(0, -moveDistanceY);


		// 2. 下拉时 - 设置刷新状态
		final RefreshHeader refreshHeader = mPullToRefreshLayout.getRefreshHeader();
		final int headerHeight = refreshHeader.getHeaderHeight();
		float fraction = scrollY * 1.0f / headerHeight;
		if (fraction > 1.0f) {
			fraction = 1.0f;
		}

		mPullToRefreshLayout.refreshStateChange(fraction);                    // 改变控件刷新状态
		refreshHeader.onRefreshOffset(fraction, scrollY, headerHeight);     // 设置头相关

		if (DEBUG) {
			Log.e(TAG, String.format("distanceY: %s, scrollY: %s, moveDistanceY: %s", distanceY, mPullToRefreshLayout.getScrollY(), moveDistanceY));
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return
	 */
	@Override
	public boolean isMoveToTop() {
		int headerHeight = mPullToRefreshLayout.getRefreshHeader().getHeaderHeight();
		int scrollY = mPullToRefreshLayout.getScrollY();
		return Math.abs(scrollY) < headerHeight;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return
	 */
	@Override
	public void onResetRefresh(RefreshState state) {
		RefreshHeader refreshHeader = mPullToRefreshLayout.getRefreshHeader();
		int scrollDuration = mPullToRefreshLayout.getScrollDuration();

		int scrollY = mPullToRefreshLayout.getScrollY();
		if (RefreshState.NONE == state) {
			mPullToRefreshLayout.scrollTo(0, 0);        // 还原
		} else if (RefreshState.PULL_START == state) {
			mPullToRefreshLayout.setReleasing(true);    // 已释放
			mPullToRefreshLayout.startScroll(0, scrollY, 0, -scrollY, scrollDuration);    // 还原
		} else if (RELEASE_REFRESHING_START == state) {
			VelocityTracker velocityTracker = mPullToRefreshLayout.getVelocityTracker();
			int scaledMinimumFlingVelocity = mPullToRefreshLayout.getScaledMinimumFlingVelocity();
			float yVelocity = Math.abs(velocityTracker.getYVelocity());    // 当前y上的速度 注意： 负数问题
			if (yVelocity > scaledMinimumFlingVelocity) {    // fling 状态
				//scroll
				View refreshHeaderView = refreshHeader.getRefreshHeaderView();
				int measuredHeight = refreshHeaderView.getMeasuredHeight();
				// 显示刷新头
				mPullToRefreshLayout.startScroll(0, scrollY, 0, -scrollY - measuredHeight, scrollDuration);
				mPullToRefreshLayout.setRefreshState(RefreshState.START_REFRESHING);
			} else {    // 非 fling 状态，还原
				mPullToRefreshLayout.startScroll(0, scrollY, 0, -scrollY, scrollDuration);
			}
		} else if (RefreshState.RELEASE_START == state || RefreshState.START_REFRESHING == state) {
			View refreshHeaderView = refreshHeader.getRefreshHeaderView();
			int measuredHeight = refreshHeaderView.getMeasuredHeight();
			// -measureHeight 刷新头完全显示出来
			mPullToRefreshLayout.startScroll(0, scrollY, 0, -scrollY - measuredHeight, scrollDuration);
			mPullToRefreshLayout.setRefreshState(RefreshState.START_REFRESHING);
		}

		mPullToRefreshLayout.postInvalidate();
	}

	@Override
	public void onRefreshComplete() {
		RefreshState refreshState = mPullToRefreshLayout.getRefreshState();
		if (RefreshState.START_REFRESHING == refreshState || RefreshState.RELEASE_REFRESHING_START == refreshState) {
			final int scrollY = mPullToRefreshLayout.getScrollY();
			final int scrollDuration = mPullToRefreshLayout.getScrollDuration();

			// 需要显示刷新完成
			if (mPullToRefreshLayout.isShowRefreshCompleteInfo()) {
				// 显示一下刷新完成，并延迟回位
				mPullToRefreshLayout.setRefreshState(RefreshState.REFRESHING_START_COMPLETE);
				mPullToRefreshLayout.postDelayed(new Runnable() {
					@Override
					public void run() {
						back(scrollY, scrollDuration);
					}
				}, scrollDuration);
			} else {
				back(scrollY, scrollDuration);
			}
		} else {
			mPullToRefreshLayout.setRefreshState(RefreshState.NONE);
		}
	}

	@Override
	public void autoRefreshing(boolean anim) {
		final RefreshHeader refreshHeader = mPullToRefreshLayout.getRefreshHeader();
		final int headerHeight = refreshHeader.getHeaderHeight();
		if (mPullToRefreshLayout.getRefreshState() != RefreshState.START_REFRESHING) {
			if (anim) {
				mPullToRefreshLayout.startScroll(0, mPullToRefreshLayout.getScrollY(), 0, -mPullToRefreshLayout.getScrollY() - headerHeight, mPullToRefreshLayout.getScrollDuration());
			} else {
				mPullToRefreshLayout.scrollTo(0, -headerHeight);
			}
			mPullToRefreshLayout.postInvalidate();
			mPullToRefreshLayout.callRefreshListener();
			mPullToRefreshLayout.setRefreshState(RefreshState.START_REFRESHING);
		}
	}

	/**
	 * 刷完回位
	 *
	 * @param scrollY
	 * @param scrollDuration
	 */
	private void back(int scrollY, int scrollDuration) {
		if (mPullToRefreshLayout != null) {
			mPullToRefreshLayout.startScroll(0, scrollY, 0, -scrollY, scrollDuration);
			mPullToRefreshLayout.requestLayout();
			mPullToRefreshLayout.setRefreshState(RefreshState.NONE);
		}
	}
}
