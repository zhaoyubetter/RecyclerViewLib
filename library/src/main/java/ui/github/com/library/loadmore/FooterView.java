package ui.github.com.library.loadmore;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FooterView extends LinearLayout {
	protected String loadingText = "加载中…";
	protected String noMoreText = "已加载全部";
	protected String noDataText = "暂无内容";
	protected String loadTextByUser = "加载更多";
	protected String loadFailed = "加载失败，点击重试";

	private final TextView textView;
	private final ProgressBar progressBar;

	public FooterView(Context context) {
		super(context);
		textView = new TextView(context);
		progressBar = new ProgressBar(context);
		init();
	}

	public void init() {
		this.setOrientation(HORIZONTAL);
		this.setGravity(Gravity.CENTER);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		params.height = dip2px(36);
		this.setLayoutParams(params);

		progressBar.setIndeterminate(true);
		LayoutParams progressBarparams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		progressBarparams.width = progressBarparams.height = dip2px(22);
		progressBar.setLayoutParams(progressBarparams);

		LayoutParams textViewparams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		int padding = dip2px(8);
		textView.setPadding(padding, padding, padding, padding);
		textView.setTextColor(0xff666666);
		textView.setTextSize(14);
		textView.setLayoutParams(textViewparams);

		this.addView(progressBar);
		this.addView(textView);
	}


	// ==========  各种状态  ==========
	public void setDefaultState() {
		this.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.GONE);
		textView.setVisibility(View.VISIBLE);
		textView.setText(loadTextByUser);
	}

	public void setNoDataState() {
		this.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.GONE);
		textView.setVisibility(View.VISIBLE);
		textView.setText(noDataText);
	}

	public void setNoMoreState() {
		this.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.GONE);
		textView.setVisibility(View.VISIBLE);
		textView.setText(noMoreText);
	}

	public void setLoadingState() {
		this.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.VISIBLE);
		textView.setVisibility(View.VISIBLE);
		textView.setText(loadingText);
	}

	public void setLoadFailState() {
		this.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.GONE);
		textView.setVisibility(View.VISIBLE);
		textView.setText(loadFailed);
	}

	// ==========  各种状态  ==========

	public void setInVisibleState() {
		this.setVisibility(View.GONE);
	}

	public int dip2px(float dpValue) {
		Resources r = getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, r.getDisplayMetrics());
		return (int) px;
	}
}