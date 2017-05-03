package ui.github.com.library.recycler.base;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by zhaoyu1 on 2017/2/7.
 */
public class BaseRecyclerViewHolder extends RecyclerView.ViewHolder {

	/**
	 * 缓存子View，避免，多次执行findViewById
	 */
	private final SparseArray<View> mViews;

	public BaseRecyclerViewHolder(View itemView) {
		super(itemView);
		mViews = new SparseArray<>();
	}

	public <T extends View> T getView(int resId) {
		View v = mViews.get(resId);
		if (v == null) {
			v = itemView.findViewById(resId);
			mViews.put(resId, v);
		}
		return (T) v;
	}

	public BaseRecyclerViewHolder setText(@IdRes int id, String value) {
		TextView tv = getView(id);
		if (tv != null) {
			tv.setText(value);
		}
		return this;
	}

	/**
	 * 获取viewHolder
	 *
	 * @param context
	 * @param layoutId item布局
	 * @param parent
	 * @return viewHolder
	 */
	public static BaseRecyclerViewHolder getViewHolder(final Context context, @LayoutRes final int layoutId, final ViewGroup parent) {
		View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
		return new BaseRecyclerViewHolder(view);
	}
}
