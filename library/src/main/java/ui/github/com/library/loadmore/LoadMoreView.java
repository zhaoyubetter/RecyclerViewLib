package ui.github.com.library.loadmore;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.View;

import ui.github.com.library.base.BaseRecyclerViewHolder;

/**
 * 加载更多抽象类
 * 参考：https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 * Created by zhaoyu on 2017/4/10.
 */
public abstract class LoadMoreView {
	/**
	 * 不可见状态
	 */
	public static final int STATE_INVISIBLE = 0;
	/**
	 * 自动加载
	 */
	public static final int STATE_LOADING = 1;
	/**
	 * 没有更多数据
	 */
	public static final int STATE_LOAD_COMPLETE = 2;
	/**
	 * 加载无数据
	 */
	public static final int STATE_NO_DATA = 3;
	/**
	 * 加载失败
	 */
	public static final int STATE_LOAD_FAIL = 5;
	/**
	 * 手动加载
	 */
	public static final int STATE_LOAD_BY_USER = 4;


	/**
	 * 默认状态
	 */
	private int mLoadMoreState = STATE_LOAD_BY_USER;

	private View mLoadView;
	private BaseRecyclerViewHolder mHolder;

	/**
	 * 实际View；
	 *
	 * @return
	 */
	public View getLoadView() {
		return mLoadView;
	}

	public void setLoadState(int state) {
		this.mLoadMoreState = state;

		if (mLoadMoreState == STATE_INVISIBLE) {
			mHolder.itemView.setVisibility(View.GONE);
			return;
		}

		mHolder.itemView.setVisibility(View.VISIBLE);

		final View byUser = mHolder.getView(getLoadByUserView());
		final View complete = mHolder.getView(getLoadCompleteView());
		final View fail = mHolder.getView(getLoadingFailView());
		final View loading = mHolder.getView(getLoadingView());
		final View noData = mHolder.getView(getLoadNoDataView());

		if (byUser != null)
			byUser.setVisibility(mLoadMoreState == STATE_LOAD_BY_USER ? View.VISIBLE : View.INVISIBLE);
		if (complete != null)
			complete.setVisibility(mLoadMoreState == STATE_LOAD_COMPLETE ? View.VISIBLE : View.INVISIBLE);
		if (fail != null)
			fail.setVisibility(mLoadMoreState == STATE_LOAD_FAIL ? View.VISIBLE : View.INVISIBLE);
		if (loading != null)
			loading.setVisibility(mLoadMoreState == STATE_LOADING ? View.VISIBLE : View.INVISIBLE);
		if (noData != null)
			noData.setVisibility(mLoadMoreState == STATE_NO_DATA ? View.VISIBLE : View.INVISIBLE);
	}

	public int getLoadState() {
		return mLoadMoreState;
	}

	public void convert(BaseRecyclerViewHolder holder) {
		this.mHolder = holder;
		mLoadView = holder.itemView;
	}

	/**
	 * 加载更多布局文件
	 *
	 * @return
	 */
	public abstract
	@LayoutRes
	int getLayoutId();

	/**
	 * 加载中布局ID
	 *
	 * @return
	 */
	public abstract
	@IdRes
	int getLoadingView();

	/**
	 * 加载失败布局ID
	 *
	 * @return
	 */
	public abstract
	@IdRes
	int getLoadingFailView();

	/**
	 * 加载无数据布局
	 *
	 * @return
	 */
	public abstract
	@IdRes
	int getLoadNoDataView();

	/**
	 * 没有更多数据布局，全部加载完毕
	 *
	 * @return
	 */
	public abstract
	@IdRes
	int getLoadCompleteView();

	/**
	 * 手动加载布局
	 *
	 * @return
	 */
	public abstract
	@IdRes
	int getLoadByUserView();
}
