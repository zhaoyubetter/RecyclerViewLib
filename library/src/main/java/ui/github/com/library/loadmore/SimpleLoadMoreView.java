package ui.github.com.library.loadmore;

import ui.github.com.library.R;

/**
 * Created by zhaoyu on 2017/4/10.
 */
public class SimpleLoadMoreView extends LoadMoreView {
	@Override
	public int getLayoutId() {
		return R.layout.load_more_simple_view;
	}

	@Override
	public int getLoadingView() {
		return R.id.load_more_auto_loading;
	}

	@Override
	public int getLoadingFailView() {
		return R.id.load_more_load_fail;
	}

	@Override
	public int getLoadNoDataView() {
		return R.id.load_more_load_no_data;
	}

	@Override
	public int getLoadCompleteView() {
		return R.id.load_more_load_complete;
	}

	@Override
	public int getLoadByUserView() {
		return R.id.load_more_by_user;
	}
}
