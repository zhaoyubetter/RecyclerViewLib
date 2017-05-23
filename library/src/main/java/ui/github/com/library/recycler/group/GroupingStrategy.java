package ui.github.com.library.recycler.group;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ui.github.com.library.recycler.base.BaseRecyclerViewAdapter;
import ui.github.com.library.recycler.callback.BinaryCondition;
import ui.github.com.library.recycler.callback.Condition;
import ui.github.com.library.recycler.callback.IRecyclerAdapter;

/**
 * 分组策略
 * 来自：陈圳
 */
public class GroupingStrategy {
	/**
	 * 对应的Adapter
	 */
	private final IRecyclerAdapter mAdapter;
	/**
	 * 分组索引
	 */
	private final List<Integer> mIndexItems;
	private Integer[] mIndexArray;
	/**
	 * 条件
	 */
	private BinaryCondition mBinaryCondition;
	private Condition mCondition;

	public static <T> GroupingStrategy of(BaseRecyclerViewAdapter<T> adapter) {
		return new GroupingStrategy(adapter);
	}

	public <T> GroupingStrategy reduce(Condition<T> condition) {
		this.mCondition = condition;
		refreshIndexItems();
		return this;
	}

	public <T> GroupingStrategy reduce(BinaryCondition<T> binCondition) {
		this.mBinaryCondition = binCondition;
		refreshIndexItems();
		return this;
	}

	/**
	 * 当前 position 是否是 分组标题
	 *
	 * @param position
	 * @return
	 */
	public boolean isGroupIndex(int position) {
		return 0 <= Arrays.binarySearch(getIndexArray(), position);
	}

	public GroupingStrategy(@NonNull BaseRecyclerViewAdapter adapter) {
		this.mAdapter = adapter;
		this.mIndexItems = new ArrayList<>();
		registerAdapterDataObserver(adapter);
	}

	/**
	 * 注册数据适配器数据监听,时时同步映射角标集
	 *
	 * @param adapter
	 */
	private void registerAdapterDataObserver(final BaseRecyclerViewAdapter adapter) {
		//同步整个列表数据变化
		adapter.setHasStableIds(true);
		adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
			@Override
			public void onChanged() {
				super.onChanged();
				refreshIndexItems();
			}

			@Override
			public void onItemRangeChanged(int positionStart, int itemCount) {
				super.onItemRangeChanged(positionStart, itemCount);
				refreshIndexItems();
			}

			@Override
			public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
				super.onItemRangeChanged(positionStart, itemCount, payload);
				refreshIndexItems();
			}

			@Override
			public void onItemRangeInserted(int positionStart, int itemCount) {
				super.onItemRangeInserted(positionStart, itemCount);
				refreshIndexItems();
			}

			@Override
			public void onItemRangeRemoved(int positionStart, int itemCount) {
				super.onItemRangeRemoved(positionStart, itemCount);
				refreshIndexItems();
			}

			@Override
			public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
				super.onItemRangeMoved(fromPosition, toPosition, itemCount);
				refreshIndexItems();
			}
		});
	}

	/**
	 * 刷新定位角标位置
	 */
	private void refreshIndexItems() {
		mIndexArray = null;
		if (null == mBinaryCondition && null == mCondition) {
			throw new NullPointerException("condition is null!");
		} else if (null != mBinaryCondition) {
			binaryConditionRefresh(mAdapter.getData());
		} else if (null != mCondition) {
			conditionRefresh(mAdapter.getData());
		}
	}

	/**
	 * 前一个item与后一个item对比
	 *
	 * @param data
	 */
	private void binaryConditionRefresh(List data) {
		mIndexItems.clear();
		Object lastItem = null;
		for (int i = 0; i < data.size(); i++) {
			Object item = data.get(i);
			// 分组
			if (lastItem == null || mBinaryCondition.apply(item, lastItem)) {
				mIndexItems.add(i);
			}
			lastItem = item;
		}
	}

	/**
	 * 单条件
	 *
	 * @param data
	 */
	private void conditionRefresh(List data) {
		mIndexItems.clear();
		for (int i = 0; i < data.size(); i++) {
			Object item = data.get(i);
			if (mCondition.apply(item)) {
				mIndexItems.add(i);
			}
		}
	}

	Integer[] getIndexArray() {
		if (null == mIndexArray) {
			mIndexArray = mIndexItems.toArray(new Integer[mIndexItems.size()]);
		}
		return mIndexArray;
	}
}
