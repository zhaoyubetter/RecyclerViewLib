package ui.github.com.library.refresh.header;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import ui.github.com.library.refresh.RefreshState;

/**
 * 刷新头抽象类
 * 主要职责：
 * 1. 刷新头布局
 * 2. 刷新状态 {@link RefreshState} 改变时, 刷新头变化，如：需要释放刷新时，提示文字 为：释放刷新
 * 3. 根据 move 事件引起的 刷新状态 变化，而及时更新新头界面显示，用来提醒用户，当前的刷新状态
 * {@link ui.github.com.library.refresh.headerstrategy.HeaderStrategy#onMoveOffset(float)}
 * {@link ui.github.com.library.refresh.PullToRefreshLayout#refreshStateChange} ,
 */
public abstract class RefreshHeader {

	protected final boolean DEBUG = true;
	protected final String TAG = "RefreshHeader";

	/**
	 * 头View
	 */
	protected View mHeaderView;

	public RefreshHeader(Context context, ViewGroup parent) {
		if (null == context || null == parent) {
			throw new NullPointerException("context or parent is null!");
		}
	}

	public abstract View getRefreshHeaderView();


	public int getHeaderHeight() {
		return mHeaderView.getMeasuredHeight();
	}

	/**
	 * when user refresh state changed callback
	 * 刷新状态改变
	 *
	 * @param refreshState 刷新状态
	 */
	public abstract void onRefreshStateChange(RefreshState refreshState);

	/**
	 * when user scroll return refresh offset value
	 * 拖动位移发生改变
	 *
	 * @param fraction
	 * @param scrollY
	 * @param headerHeight
	 */
	public abstract void onRefreshOffset(float fraction, int scrollY, int headerHeight);
}
