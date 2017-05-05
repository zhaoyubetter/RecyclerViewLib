package ui.github.com.library.refresh.header;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import ui.github.com.library.refresh.RefreshState;

/**
 * 刷新头抽象类
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
