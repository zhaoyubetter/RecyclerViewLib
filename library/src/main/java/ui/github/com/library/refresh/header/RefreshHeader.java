package ui.github.com.library.refresh.header;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * 刷新头抽象类
 */
public abstract class RefreshHeader {

	private final String TAG = "RefreshHeader";

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
}
