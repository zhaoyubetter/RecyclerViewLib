package ui.github.com.library.recycler.group;

import android.view.View;

public interface StickyCallback {

	/**
	 * 初始化 StickyView
	 *
	 * @param view
	 * @param position
	 */
	void initStickyView(View view, int position);

	/**
	 * 当前位置是否是 Sticky
	 *
	 * @param position
	 * @return
	 */
	boolean isStickyPosition(int position);
}
