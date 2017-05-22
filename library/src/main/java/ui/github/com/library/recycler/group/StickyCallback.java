package ui.github.com.library.recycler.group;

import android.view.View;

public interface StickyCallback {
	void initStickyView(View view, int position);

	boolean isStickyPosition(int position);
}
