package ui.github.com.library.headerfooter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ui.github.com.library.base.BaseRecyclerViewAdapter;

/**
 * 添加 Header —— footer 包装
 */
public class HeaderViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


	public static class HeaderViewItem {
		public final int viewType;
		public final View view;

		public HeaderViewItem(int viewType, View view) {
			this.viewType = viewType;
			this.view = view;
		}
	}


	/**
	 * 数据变化观察者对象
	 */
	public static class HeaderAdapterDataObserve extends RecyclerView.AdapterDataObserver {
		private HeaderViewAdapter mWrapAdapter;

		public HeaderAdapterDataObserve(HeaderViewAdapter mWrapAdapter) {
			this.mWrapAdapter = mWrapAdapter;
		}

		@Override
		public void onChanged() {
			mWrapAdapter.notifyDataSetChanged();
		}


		@Override
		public void onItemRangeInserted(int positionStart, int itemCount) {
			mWrapAdapter.notifyItemRangeInserted(mWrapAdapter.getHeaderViewsCount() + positionStart, itemCount);
		}

		@Override
		public void onItemRangeChanged(int positionStart, int itemCount) {
			mWrapAdapter.notifyItemRangeChanged(mWrapAdapter.getHeaderViewsCount() + positionStart, itemCount);
		}

		@Override
		public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
			mWrapAdapter.notifyItemRangeChanged(mWrapAdapter.getHeaderViewsCount() + positionStart, itemCount, payload);
		}

		@Override
		public void onItemRangeRemoved(int positionStart, int itemCount) {
			mWrapAdapter.notifyItemRangeRemoved(mWrapAdapter.getHeaderViewsCount() + positionStart, itemCount);
		}

		@Override
		public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
			mWrapAdapter.notifyItemRangeRemoved(mWrapAdapter.getHeaderViewsCount() + fromPosition, toPosition);
		}
	}


	private final int TYPE_HEADER = -1;        // 从1 开始减，逐渐减
	private final int TYPE_FOOTER = Integer.MIN_VALUE / 2;  // 逐渐减
	private final int TYPE_NORMAL = 0;         // 原来的 item_type
	private RecyclerView.Adapter mInnerAdapter;
	private ArrayList<HeaderViewItem> mHeaderViews;
	private ArrayList<HeaderViewItem> mFooterViews;

	private HeaderAdapterDataObserve mDataObserve;
	private BaseRecyclerViewAdapter.ItemOnClickListener mItemClickListener;

	public void setOnItemClickListener(BaseRecyclerViewAdapter.ItemOnClickListener listener) {
		this.mItemClickListener = listener;
	}


	public HeaderViewAdapter(RecyclerView.Adapter adapter) {
		mHeaderViews = new ArrayList<>();
		mFooterViews = new ArrayList<>();
		setAdapter(adapter);
	}

	public void setAdapter(RecyclerView.Adapter adapter) {
		if (mInnerAdapter != null && mDataObserve != null) {
			mInnerAdapter.unregisterAdapterDataObserver(mDataObserve);
		}
		mDataObserve = null;

		mDataObserve = new HeaderAdapterDataObserve(this);
		this.mInnerAdapter = adapter;
		mInnerAdapter.registerAdapterDataObserver(mDataObserve);

		notifyDataSetChanged();
	}

	public int getHeaderViewsCount() {
		return mHeaderViews.size();
	}

	public int getFooterViewsCount() {
		return mFooterViews.size();
	}

	/**
	 * 添加 Header
	 *
	 * @param view
	 */
	public void addHeaderView(View view) {
		// 每个头部对应一个 itemType
		int viewType = TYPE_HEADER - mHeaderViews.size();
		int index = mHeaderViews.size();
		this.mHeaderViews.add(new HeaderViewItem(viewType, view));
		this.notifyItemInserted(index);
	}


	/**
	 * 添加Footer
	 *
	 * @param view
	 */
	public void addFooterView(View view) {
		int viewType = TYPE_FOOTER - mFooterViews.size();
		int index = mFooterViews.size();
		this.mFooterViews.add(new HeaderViewItem(viewType, view));
		this.notifyItemInserted(getFooterStartIndex() + index);
	}

	/**
	 * 底部组起始位置
	 *
	 * @return
	 */
	public int getFooterStartIndex() {
		int count = mHeaderViews.size();
		if (null != mInnerAdapter) {
			count += mInnerAdapter.getItemCount();
		}
		return count;
	}

	/**
	 * 获取指定位置的header View
	 *
	 * @param index
	 * @return
	 */
	public View getHeaderView(int index) {
		View view = null;
		if (0 <= index && index < mHeaderViews.size()) {
			view = mHeaderViews.get(index).view;
		}

		return view;
	}

	/**
	 * 获取指定位置的 footer view
	 *
	 * @param index
	 * @return
	 */
	public View getFooterView(int index) {
		View view = null;
		if (0 <= index && index < mHeaderViews.size()) {
			view = mFooterViews.get(index).view;
		}
		return view;
	}

	public void removeHeaderView(View view) {
		if (null == view) {
			return;
		}
		removeHeaderView(indexOfValue(mHeaderViews, view));
	}

	/**
	 * 移除指定的HeaderView对象
	 *
	 * @param position
	 */
	public void removeHeaderView(int position) {
		if (0 > position || mHeaderViews.size() <= position) return;
		mHeaderViews.remove(position);
		notifyItemRemoved(position);
	}

	/**
	 * 移除指定的HeaderView对象
	 *
	 * @param view
	 */
	public void removeFooterView(View view) {
		if (null == view) return;
		removeFooterView(indexOfValue(mFooterViews, view));
	}

	/**
	 * 移除指定的HeaderView对象
	 *
	 * @param position
	 */
	public void removeFooterView(int position) {
		if (0 > position || mFooterViews.size() <= position) return;
		mFooterViews.remove(position);
		notifyItemRemoved(getItemCount() - mFooterViews.size() - position);
	}

	private int indexOfValue(ArrayList<HeaderViewItem> items, View view) {
		int index = -1;
		for (int i = 0; i < items.size(); i++) {
			HeaderViewItem viewItem = items.get(i);
			if (viewItem.view == view) {
				index = i;
				break;
			}
		}
		return index;
	}

	private View getItemValue(ArrayList<HeaderViewItem> items, int type) {
		View view = null;
		for (int i = 0; i < items.size(); i++) {
			HeaderViewItem viewItem = items.get(i);
			if (viewItem.viewType == type) {
				view = viewItem.view;
				break;
			}
		}
		return view;
	}

	// ==================================================================================

	@Override
	public int getItemViewType(int position) {
		int itemType = TYPE_NORMAL;

		if (isHeader(position)) {
			itemType = mHeaderViews.get(position).viewType;
		} else if (isFooter(position)) {
			itemType = mFooterViews.get(mFooterViews.size() - (getItemCount() - position)).viewType;
		} else {        // 原来的
			int itemPosition = position - mHeaderViews.size();
			if (mInnerAdapter != null) {
				int adapterCount = mInnerAdapter.getItemCount();
				if (itemPosition < adapterCount) {
					itemType = mInnerAdapter.getItemViewType(itemPosition);
				}
			}
		}

		return itemType;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		RecyclerView.ViewHolder holder;
		if (viewType <= TYPE_FOOTER) {            // footer
			holder = new ViewHolder(getItemValue(mFooterViews, viewType));
		} else if (viewType <= TYPE_HEADER) {     // header
			holder = new ViewHolder(getItemValue(mHeaderViews, viewType));
		} else {
			holder = mInnerAdapter.onCreateViewHolder(parent, viewType);
		}

		return holder;
	}

	@Override
	public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
		if (isHeader(position)) {
			return;
		}
		final int itemPosition = holder.getLayoutPosition() - mHeaderViews.size();
		if (null != mInnerAdapter && itemPosition < mInnerAdapter.getItemCount()) {
			mInnerAdapter.onBindViewHolder(holder, itemPosition);
			if (mItemClickListener != null) {
				holder.itemView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mItemClickListener.onItemClick(holder.itemView, itemPosition, null);
					}
				});
			}
		}
	}

	@Override
	public int getItemCount() {
		int itemCount = mFooterViews.size() + mHeaderViews.size();
		if (null != mInnerAdapter) {
			itemCount += mInnerAdapter.getItemCount();
		}
		return itemCount;
	}


	@Override
	public void onAttachedToRecyclerView(RecyclerView recyclerView) {
		super.onAttachedToRecyclerView(recyclerView);
		RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
		if (manager instanceof GridLayoutManager) {
			final GridLayoutManager gridManager = (GridLayoutManager) manager;
			gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
				@Override
				public int getSpanSize(int position) {
					return (isHeader(position) || isFooter(position)) ? gridManager.getSpanCount() : 1;
				}
			});
		}
	}

	@Override
	public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
		super.onViewAttachedToWindow(holder);
		ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
		if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams
				&& (isHeader(holder.getLayoutPosition()) || isFooter(holder.getLayoutPosition()))) {
			StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
			p.setFullSpan(true);
		}
	}

	/**
	 * 是否头部
	 *
	 * @param position
	 * @return
	 */
	public boolean isHeader(int position) {
		return position >= 0 && position < mHeaderViews.size();
	}

	/**
	 * 是否尾部
	 *
	 * @param position
	 * @return
	 */
	public boolean isFooter(int position) {
		int itemCount = getItemCount();
		return position < itemCount && position >= itemCount - mFooterViews.size();
	}

	private class ViewHolder extends RecyclerView.ViewHolder {
		public ViewHolder(View itemView) {
			super(itemView);
		}
	}
}
