package ui.github.com.library.refresh.headerstrategy;

import android.view.View;
import android.view.ViewGroup;

import ui.github.com.library.refresh.PullToRefreshLayout;
import ui.github.com.library.refresh.RefreshState;
import ui.github.com.library.refresh.header.RefreshHeader;

/**
 * 刷新头策略
 * 主要职责：
 * 1. 布局刷新头、刷新View
 * 2. ViewGroup拦截事件控制
 * 3. onMoveOffset方法 处理MOVE事件，并产生相应的 刷新状态 {@link RefreshState} {@link PullToRefreshLayout#refreshStateChange(float)}
 * 4. onResetRefresh，手指抬起时，根据当前 刷新状态 来设置整个PullToRefresh控件,如：是否还原，是否刷新等 {@link PullToRefreshLayout#onTouchEvent}
 * 5. 设置自动刷新策略{@link HeaderStrategy#autoRefreshing}
 * 6. 设置刷新完成 {@link HeaderStrategy#onRefreshComplete}
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
	 * 刷新状态改变，控制刷新view的动作
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
