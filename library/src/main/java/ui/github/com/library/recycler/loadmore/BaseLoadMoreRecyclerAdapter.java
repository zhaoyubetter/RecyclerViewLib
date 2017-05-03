package ui.github.com.library.recycler.loadmore;

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

import ui.github.com.library.recycler.base.BaseRecyclerViewAdapter;
import ui.github.com.library.recycler.base.BaseRecyclerViewHolder;

import static ui.github.com.library.recycler.loadmore.LoadMoreView.STATE_INVISIBLE;
import static ui.github.com.library.recycler.loadmore.LoadMoreView.STATE_LOADING;
import static ui.github.com.library.recycler.loadmore.LoadMoreView.STATE_LOAD_BY_USER;
import static ui.github.com.library.recycler.loadmore.LoadMoreView.STATE_LOAD_COMPLETE;
import static ui.github.com.library.recycler.loadmore.LoadMoreView.STATE_LOAD_FAIL;
import static ui.github.com.library.recycler.loadmore.LoadMoreView.STATE_NO_DATA;

/**
 * 加载更多抽象基类
 *
 * @param
 */
public abstract class BaseLoadMoreRecyclerAdapter<T> extends BaseRecyclerViewAdapter<T> {

	public static final int ITEM_TYPE_FOOTER = Integer.MIN_VALUE >> 2;

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

	/**
	 * 加载更多
	 */
	private LoadMoreView mLoadMoreView;

	public BaseLoadMoreRecyclerAdapter(@NonNull RecyclerView recyclerView, List<T> datas) {
		super(datas);
		this.mRecyclerView = recyclerView;
		mLoadMoreView = new SimpleLoadMoreView();
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
			BaseRecyclerViewHolder holder = BaseRecyclerViewHolder.getViewHolder(parent.getContext(), mLoadMoreView.getLayoutId(), parent);
			mLoadMoreView.initLoadView(holder);
			return holder;
		}
		return super.onCreateViewHolder(parent, viewType);
	}

	@Override
	public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {
		if (position == getItemCount() - 1) {
			// loadmoreView写死了，这里不用再去设置
			// 没有数据的时候设置为默认状态，加载更多
			if (position == 0) {  // the first time come to page the position is zero
				this.setLoadViewState(STATE_INVISIBLE);
			}
		} else {
			super.onBindViewHolder(holder, position);
		}
	}


	/**
	 * 加载完成时，or 失败时，调用此方法还原状态
	 */
	public void setStateLoadedAuto() {
		mIsLoading = false;
		mIsLoadedAll = false;
		this.setLoadViewState(LoadMoreView.STATE_LOADING);
		resetSwipe();
	}

	/**
	 * 加载完成时，or 失败时，调用此方法还原状态
	 */
	public void setStateLoadedByUser() {
		mIsLoading = false;
		mIsLoadedAll = false;
		this.setLoadViewState(STATE_LOAD_BY_USER);
		resetSwipe();
	}

	/**
	 * 加载了全部数据,调用此方法，就不允许再加载数据
	 */
	public void setStateLoadedAll() {
		mIsLoading = false;
		mIsLoadedAll = true;
		this.setLoadViewState(STATE_LOAD_COMPLETE);
		resetSwipe();
	}

	/**
	 * 加载失败了，可点击重试
	 */
	public void setStateLoadedFail() {
		this.setLoadViewState(STATE_LOAD_FAIL);
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

	/**
	 * set custom load more view
	 *
	 * @param loadMore
	 */
	public void setLoadMore(LoadMoreView loadMore) {
		this.mLoadMoreView = loadMore;
	}

	public void clearData() {
		super.clearData();
		setStateLoadedByUser();
	}

	private void setLoadViewState(int state) {
		mLoadMoreView.setLoadState(state);

		// 设置点击事件
		mLoadMoreView.getLoadView().setOnClickListener(null);
		switch (state) {
			case STATE_NO_DATA:
				mLoadMoreView.getLoadView().setOnClickListener(mRetryListener);
				break;
			case STATE_LOAD_FAIL:
				mLoadMoreView.getLoadView().setOnClickListener(mRetryListener);
				break;
			case STATE_LOAD_BY_USER:
				mLoadMoreView.getLoadView().setOnClickListener(mRetryListener);
				break;
		}
	}

	/**
	 * 触发加载更多
	 */
	private void autoLoadMore() {
		// 3.判断是否是上拉加载
		if (!mIsLoading && !mIsLoadedAll && mLoadMoreView.getLoadState() != STATE_LOAD_BY_USER) {
			performLoadMore();
		}
	}

	/**
	 * 设置下拉刷新组件
	 *
	 * @param swipe
	 */
	public void setSwipeRefreshLayout(SwipeRefreshLayout swipe) {
		mSwipeRefreshLayout = swipe;
	}

	private void resetSwipe() {
		if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
			mSwipeRefreshLayout.setRefreshing(false);
			setLoadViewState(STATE_LOAD_BY_USER);        // 设置为默认，需要手动加载，避免一些问题
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
			setLoadViewState(STATE_LOADING); // 设置状态加载中
			pullUpListener.onLoadMore();
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

	/**
	 * 删除条目时，判断最后一个条目是否可见，如果可见设置上拉加载方式为手动加载
	 *
	 * @param position
	 */
	@Override
	public void removeItemAt(int position) {
		super.removeItemAt(position);
		if (getLastVisiblePosition() + 1 == getItemCount()) {
			setLoadViewState(STATE_LOAD_BY_USER);
		}
	}

	@Override
	public void removeItems(List<T> items) {
		super.removeItems(items);
		if (getLastVisiblePosition() + 1 == getItemCount()) {
			setLoadViewState(STATE_LOAD_BY_USER);
		}
	}

	@Override
	public void addItems(List<T> items) {
		super.addItems(items);
		setStateLoadedAuto();        // 一般自动加载多，这里设置为自动加载
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
			setLoadViewState(STATE_LOADING);
			performLoadMore();
		}
	}
}
