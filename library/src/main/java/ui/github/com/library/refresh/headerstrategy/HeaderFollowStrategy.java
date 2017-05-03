package ui.github.com.library.refresh.headerstrategy;

import android.view.View;

import ui.github.com.library.refresh.PullToRefreshLayout;
import ui.github.com.library.refresh.header.RefreshHeader;

/**
 * FollowHeader 展示策略
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
}
