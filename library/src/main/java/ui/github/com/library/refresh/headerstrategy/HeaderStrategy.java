package ui.github.com.library.refresh.headerstrategy;

import android.view.View;
import android.view.ViewGroup;

import ui.github.com.library.refresh.PullToRefreshLayout;
import ui.github.com.library.refresh.header.RefreshHeader;

/**
 * 刷新头策略，通过策略来布局
 */
public abstract class HeaderStrategy {

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
}
