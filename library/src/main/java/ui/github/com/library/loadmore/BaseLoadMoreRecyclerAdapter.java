package ui.github.com.library.loadmore;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ui.github.com.library.base.BaseRecyclerViewAdapter;
import ui.github.com.library.base.BaseRecyclerViewHolder;

/**
 * 加载更多抽象基类
 *
 * @param
 */
public abstract class BaseLoadMoreRecyclerAdapter<T> extends BaseRecyclerViewAdapter<T> {

	public static final int ITEM_TYPE_FOOTER = Integer.MIN_VALUE >> 2;

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
	 * 当前状态
	 */
	protected int state = STATE_LOAD_BY_USER;
	protected FooterView footerView;
	private OnLoadMoreListener pullUpListener;

	/**
	 * 是否正在加载
	 */
	protected boolean mIsLoading = false;
	/**
	 * 是否加载了全部
	 */
	protected boolean mIsLoadedAll = false;

	/**
	 * 点击重试
	 */
	private RetryListener mRetryListener = new RetryListener();

	/**
	 * 下拉刷新组件，配合下拉刷新使用
	 */
	protected SwipeRefreshLayout mSwipeRefreshLayout;

	/**
	 * 对应的 RecyclerView
	 */
	private RecyclerView mRecyclerView;

	public BaseLoadMoreRecyclerAdapter(@NonNull RecyclerView recyclerView, List<T> datas) {
		super(datas);
		this.mRecyclerView = recyclerView;
		footerView = new FooterView(recyclerView.getContext());
	}

	@CallSuper
	@Override
	public int getItemViewType(int position) {
		if (position == getItemCount() - 1) {
			autoLoadMore();
			return ITEM_TYPE_FOOTER;
		}
		return super.getItemViewType(position);
	}

	@Override
	public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (viewType == ITEM_TYPE_FOOTER) {
			refreshFooterView();
			return new BaseRecyclerViewHolder(footerView);
		}
		return super.onCreateViewHolder(parent, viewType);
	}

	@Override
	public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {
		if (position == getItemCount() - 1) {
			// 因为已经在footerview写死了，所以这里就不用再去设置
			// 没有数据的时候也不显示footer
			if (position == 0) {
				getFooterView().setVisibility(View.INVISIBLE);
			}
		} else {
			super.onBindViewHolder(holder, position);
		}
	}

	/**
	 * 瀑布流布局时 加载更多充满整行
	 *
	 * @param holder
	 */
	@Override
	public void onViewAttachedToWindow(BaseRecyclerViewHolder holder) {
		super.onViewAttachedToWindow(holder);
		if (holder.getItemViewType() == ITEM_TYPE_FOOTER) {
			setFullSpan(holder);
		}
	}

	protected void setFullSpan(RecyclerView.ViewHolder holder) {
		if (holder.itemView.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
			StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) holder
					.itemView.getLayoutParams();
			params.setFullSpan(true);
		}
	}

	/**
	 * GridLayout 加载更多充满整行
	 *
	 * @param recyclerView
	 */
	@Override
	public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
		super.onAttachedToRecyclerView(recyclerView);
		RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
		if (manager instanceof GridLayoutManager) {
			final GridLayoutManager gridManager = ((GridLayoutManager) manager);
			gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
				@Override
				public int getSpanSize(int position) {
					int type = getItemViewType(position);
					// 布满整行
					return type == ITEM_TYPE_FOOTER ? gridManager.getSpanCount() : 1;

				}
			});
		}
	}

	@Override
	public int getItemCount() {
		return super.getItemCount() + 1;
	}

	public FooterView getFooterView() {
		return footerView;
	}

	/**
	 * 删除条目时，判断最后一个条目是否可见，如果可见设置上拉加载方式为手动加载
	 *
	 * @param position
	 */
	@Override
	public void removeItemAt(int position) {
		super.removeItemAt(position);
		if (getLastVisiblePosition() + 1 == getItemCount()) {
			setState(STATE_LOAD_BY_USER);
		}
	}

	@Override
	public void removeItems(List<T> items) {
		super.removeItems(items);
		if (getLastVisiblePosition() + 1 == getItemCount()) {
			setState(STATE_LOAD_BY_USER);
		}
	}

	/**
	 * 加载完成时，or 失败时，调用此方法还原状态
	 */
	public void setStateLoadedAuto() {
		mIsLoading = false;
		mIsLoadedAll = false;
		this.setState(STATE_LOADING);
		resetSwipe();
	}

	/**
	 * 加载完成时，or 失败时，调用此方法还原状态
	 */
	public void setStateLoadedByUser() {
		mIsLoading = false;
		mIsLoadedAll = false;
		this.setState(STATE_LOAD_BY_USER);
		resetSwipe();
	}

	/**
	 * 加载了全部数据,调用此方法，就不允许再加载数据
	 */
	public void setStateLoadedAll() {
		mIsLoading = false;
		mIsLoadedAll = true;
		this.setState(STATE_LOAD_COMPLETE);
		resetSwipe();
	}

	/**
	 * 加载失败了，可点击重试
	 */
	public void setStateLoadedFail() {
		this.setState(STATE_LOAD_FAIL);
		resetSwipe();
	}

	/**
	 * 获取最后可见item
	 *
	 * @return
	 */
	protected int getLastVisiblePosition() {
		RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
		if (layoutManager != null) {
			if (layoutManager instanceof LinearLayoutManager) {
				return ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
			} else if (layoutManager instanceof GridLayoutManager) {
				return ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
			} else if (layoutManager instanceof StaggeredGridLayoutManager) {
				StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
				return findMax(staggeredGridLayoutManager.findLastVisibleItemPositions(new int[staggeredGridLayoutManager.getSpanCount()]));

			}
		}
		return 0;
	}

	public void clearData() {
		super.clearData();
		setStateLoadedByUser();
	}

	private void setState(int state) {
		this.state = state;
		refreshFooterView();
	}

	/**
	 * 触发加载更多
	 */
	private void autoLoadMore() {
		// 3.判断是否是上拉加载
		if (!mIsLoading && !mIsLoadedAll && state != STATE_LOAD_BY_USER) {
			performLoadMore();
		}
	}

	public void addItems(List<T> items) {
		super.addItems(items);
		setStateLoadedAuto();
	}

	public void setSwipeRefreshLayout(SwipeRefreshLayout swipe) {
		mSwipeRefreshLayout = swipe;
	}

	private void resetSwipe() {
		if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
			mSwipeRefreshLayout.setRefreshing(false);
			setState(STATE_LOAD_BY_USER);        // 设置为默认，需要手动加载，避免一些问题
		}
	}


	private void refreshFooterView() {
		footerView.setOnClickListener(null);
		switch (state) {
			case STATE_INVISIBLE:
				footerView.setInVisibleState();
				break;
			case STATE_LOADING:
				footerView.setLoadingState();
				break;
			case STATE_LOAD_COMPLETE:
				footerView.setNoMoreState();
				break;
			case STATE_NO_DATA:
				footerView.setNoDataState();
				footerView.setOnClickListener(mRetryListener);
				break;
			case STATE_LOAD_FAIL:
				footerView.setLoadFailState();
				footerView.setOnClickListener(mRetryListener);
				break;
			case STATE_LOAD_BY_USER:
				footerView.setDefaultState();
				footerView.setOnClickListener(mRetryListener);
				break;

		}
	}

	public boolean isLoading() {
		return mIsLoading;
	}

	public boolean isLoadedAll() {
		return mIsLoadedAll;
	}

	/**
	 * 滚动到底部时的监听器
	 *
	 * @param l
	 */
	public void setOnLoadMoreListener(OnLoadMoreListener l) {
		this.pullUpListener = l;
	}

	/**
	 * 滚动到底部时的监听器
	 */
	public interface OnLoadMoreListener {
		void onLoadMore();
	}

	/**
	 * 执行加载更多请求
	 */
	private void performLoadMore() {
		if (pullUpListener != null) {
			mIsLoading = true;
			setState(STATE_LOADING); // 设置状态加载中
			pullUpListener.onLoadMore();
		}
	}

	private int findMax(int[] lastPositions) {
		int max = lastPositions[0];
		for (int value : lastPositions) {
			if (value > max) {
				max = value;
			}
		}
		return max;
	}

	/**
	 * 重新请求
	 */
	private class RetryListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			setState(STATE_LOADING);
			performLoadMore();
		}
	}
}
