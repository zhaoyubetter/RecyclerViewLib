package ui.github.com.ui.loadmore;

import ui.github.com.R;
import ui.github.com.library.recycler.loadmore.SimpleLoadMoreView;

/**
 * Created by zhaoyu on 2017/4/14.
 */

public class CustomLoadMoreView extends SimpleLoadMoreView {

	@Override
	public int getLayoutId() {
		return R.layout.custom_load_more_simple_view;
	}

}
