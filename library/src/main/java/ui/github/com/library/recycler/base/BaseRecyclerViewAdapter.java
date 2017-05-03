package ui.github.com.library.recycler.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaoyu1 on 2017/2/7.
 */
public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<BaseRecyclerViewHolder> {

	/**
	 * 独立维护数据集合
	 */
	protected List<T> data = new ArrayList<>();
	private ItemOnClickListener onItemClickListener;
	private ItemOnLongClickListener onItemLongClickListener;

	public BaseRecyclerViewAdapter() {
	}

	public void replaceData(List<T> list) {
		this.data = null;
		// 独立维护集合
		this.data = new ArrayList<>(list);
		notifyDataSetChanged();
	}

	public BaseRecyclerViewAdapter(List<T> datas) {
		if (datas != null) {
			this.data.addAll(datas);
		}
	}

	@Override
	public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return BaseRecyclerViewHolder.getViewHolder(parent.getContext(), getItemLayoutId(viewType), parent);
	}

	/**
	 * 类型对应的布局资源
	 *
	 * @param viewType
	 * @return
	 */
	public abstract int getItemLayoutId(final int viewType);

	@Override
	public void onBindViewHolder(final BaseRecyclerViewHolder holder, final int position) {
		if (null != onItemClickListener) {
			holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// holder.getLayoutPosition() 在 条目变动时操作，极有有可能 return -1
					try {
						onItemClickListener.onItemClick(holder.itemView, holder.getLayoutPosition(), data.get(holder.getLayoutPosition()));
					} catch (Exception e) {

					}
				}
			});
		}
		if (null != onItemLongClickListener) {
			holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					// holder.getLayoutPosition() 在 条目变动时操作，极有有可能 return -1
					try {
						onItemLongClickListener.onItemLongClick(holder.itemView, holder.getLayoutPosition(), data.get(holder.getLayoutPosition()));
					} catch (Exception e) {
					}
					return true;
				}
			});
		}
		onConvert(holder, data.get(position), position);
	}


	protected abstract void onConvert(final BaseRecyclerViewHolder holder, T item, final int position);

	@Override
	public int getItemCount() {
		return data == null ? 0 : data.size();
	}

	/**
	 * 添加数据
	 *
	 * @param items
	 */
	public void addItems(List<T> items) {
		if (items != null && items.size() > 0) {
			int oldSize = data.size();
			if (oldSize > 0) {
				data.addAll(items);
				notifyItemRangeInserted(oldSize, items.size());
			} else {
				replaceData(items);
			}
		}
	}

	public void addItems(int position, List<T> items) {
		if (items != null && items.size() > 0) {
			int oldSize = data.size();
			if (oldSize > 0) {
				data.addAll(position, items);
				notifyItemRangeInserted(position, items.size());
			} else {
				replaceData(items);
			}
		}
	}

	/**
	 * 移除单个
	 *
	 * @param position
	 */
	public void removeItemAt(int position) {
		if (position < data.size()) {
			data.remove(position);
			notifyItemRemoved(position);
			notifyItemRangeChanged(position, data.size() - position);
		}
	}

	/**
	 * 移除多个
	 *
	 * @param items
	 */
	public void removeItems(List<T> items) {
		data.removeAll(items);
		notifyDataSetChanged();
	}

	public void addItemAt(int position, T bean) {
		data.add(position, bean);
		notifyItemInserted(position);
	}

	public void clearData() {
		data.clear();
		notifyDataSetChanged();
	}

	public List<T> getData() {
		return this.data;
	}


	public void setOnItemClickListener(ItemOnClickListener listener) {
		this.onItemClickListener = listener;
	}

	public void setOnItemLongClickListener(ItemOnLongClickListener listener) {
		this.onItemLongClickListener = listener;
	}

	public interface ItemOnClickListener<T> {
		void onItemClick(View view, int position, T item);
	}

	public interface ItemOnLongClickListener<T> {
		void onItemLongClick(View view, int position, T item);
	}
}
