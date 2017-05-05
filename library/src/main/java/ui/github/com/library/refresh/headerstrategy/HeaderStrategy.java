package ui.github.com.library.refresh.headerstrategy;

import android.view.View;
import android.view.ViewGroup;

import ui.github.com.library.refresh.PullToRefreshLayout;
import ui.github.com.library.refresh.RefreshState;
import ui.github.com.library.refresh.header.RefreshHeader;

/**
 * 刷新头策略，通过策略来布局
 */
public abstract class HeaderStrategy {

	protected final String TAG = "HeaderStrategy";
	protected final boolean DEBUG = true;

	protected PullToRefreshLayout mPullToRefreshLayout;

	public HeaderStrategy(PullToRefreshLayout layout) {
		this.mPullToRefreshLayout = layout;
	}

	/**
	 * 切换头
	 *
	 * @param newHeaderView
	 */
	protected void swapHeaderView(View newHeaderView) {
		final RefreshHeader refreshHeader = mPullToRefreshLayout.getRefreshHeader();
		// 有老的，先删除
		if (refreshHeader != null && null != refreshHeader.getRefreshHeaderView()) {
			mPullToRefreshLayout.removeView(refreshHeader.getRefreshHeaderView());
		}
		mPullToRefreshLayout.addView(newHeaderView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

	}


	/**
	 * 初始化刷新头
	 *
	 * @param refreshHeader
	 */
	public abstract void onInitRefreshHeader(RefreshHeader refreshHeader);

	/**
	 * 布局
	 *
	 * @param changed
	 * @param l
	 * @param t
	 * @param r
	 * @param b
	 */
	public abstract void onLayout(boolean changed, int l, int t, int r, int b);

	/**
	 * 是否拦截事件
	 *
	 * @param distanceY 拉动的距离
	 * @return
	 */
	public abstract boolean isIntercept(float distanceY);

	/**
	 * move 事件处理
	 *
	 * @param mDistanceY
	 */
	public abstract void onMoveOffset(float mDistanceY);

	/**
	 * 是否往上移动
	 *
	 * @return
	 */
	public abstract boolean isMoveToTop();

	/**
	 * 刷新状态改变
	 *
	 * @param state
	 */
	public abstract void onResetRefresh(RefreshState state);

	/**
	 * 刷新完成
	 */
	public abstract void onRefreshComplete();

	/**
	 * 自动执行下拉刷新
	 *
	 * @param anim
	 */
	public abstract void autoRefreshing(boolean anim);
}
