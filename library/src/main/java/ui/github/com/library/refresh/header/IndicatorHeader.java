package ui.github.com.library.refresh.header;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ui.github.com.library.R;

/**
 * 指示器
 */
public class IndicatorHeader extends RefreshHeader {

	final String TAG = "FollowHeader";

	public IndicatorHeader(Context context, ViewGroup parent) {
		super(context, parent);
		View view = LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header_follow_layout, parent, false);
		this.mHeaderView = view;
	}

	@Override
	public View getRefreshHeaderView() {
		return mHeaderView;
	}
}
