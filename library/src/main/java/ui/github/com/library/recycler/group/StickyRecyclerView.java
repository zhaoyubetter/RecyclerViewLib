package ui.github.com.library.recycler.group;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ui.github.com.library.R;

/**
 * 粘性recyclerview，
 * 参考 陈真
 * Created by zhaoyu on 2017/5/22.
 */
public class StickyRecyclerView extends RecyclerView {


	/**
	 * StickyView 资源ID
	 */
	private int mStickyViewResId;
	private View mStickyView;
	private StickyScrollListener mListener;
	private ViewGroup mParent;

	public StickyRecyclerView(Context context) {
		this(context, null);
	}

	public StickyRecyclerView(Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public StickyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StickyRecyclerView);
		setStickyView(a.getResourceId(R.styleable.StickyRecyclerView_stickyView, View.NO_ID));
		a.recycle();
	}

	private ViewGroup getParentView() {
		if (mParent == null) {
			mParent = (ViewGroup) this.getParent();
		}
		return mParent;
	}

	public void setStickyView(@LayoutRes int resourceId) {
		if (View.NO_ID != resourceId) {
			mStickyViewResId = resourceId;
			if (getParentView() != null) {
				mStickyView = LayoutInflater.from(getContext()).inflate(resourceId, getParentView(), false);
			}
		}
	}

	public void setStickyView(View view) {
		if (mStickyView != null && getParentView() != null) {
			getParentView().removeView(mStickyView);
		}
		// 不添加,等待setAdapter时添加,避免出现无数据显示一个空的头情况
		this.mStickyView = view;
	}

	@Override
	public void setAdapter(Adapter adapter) {
		super.setAdapter(adapter);
		if (!(adapter instanceof StickyCallback)) {
			throw new IllegalArgumentException("RecyclerView.Adapter must be implements StickyCallback!");
		}

		if (null == mStickyView && mStickyViewResId != View.NO_ID) {
			mStickyView = LayoutInflater.from(getContext()).inflate(mStickyViewResId, getParentView(), false);
		}

		if (null != mStickyView) {
			getParentView().removeView(mStickyView);
			getParentView().addView(mStickyView);        // mStickyView 是添加到 ParentView
			this.removeOnScrollListener(mListener);
			mListener = new StickyScrollListener((StickyCallback) adapter);
			this.addOnScrollListener(mListener);
		}
	}

	class StickyScrollListener extends RecyclerView.OnScrollListener {
		private final StickyCallback callback;
		/**
		 * 上一个 Striky
		 */
		private int lastVisibleItemPosition;

		public StickyScrollListener(StickyCallback callback) {
			this.callback = callback;
			//初始化第一个节点信息,若数据罗多,延持到滑动时,会导致初始化第一个失败
			this.callback.initStickyView(mStickyView, 0);
			this.lastVisibleItemPosition = RecyclerView.NO_POSITION;
		}

		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
			super.onScrolled(recyclerView, dx, dy);
			RecyclerView.LayoutManager layoutManager = getLayoutManager();
			int spanCount = 1;
			if (layoutManager instanceof GridLayoutManager) {
				spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
			}
			if (layoutManager instanceof LinearLayoutManager) {
				LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
				int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
				if (firstVisibleItemPosition <= NO_POSITION) {
					mStickyView.setVisibility(View.GONE);
				} else {
					mStickyView.setVisibility(View.VISIBLE);
					int realVisibleItemPosition = firstVisibleItemPosition;
					// 初始化当前位置Sticky信息
					int lastRealPosition = realVisibleItemPosition + spanCount;        // 定位到下一行
					// 第一个可见item，到下一行，最后一个可见item（grid时）
					for (int position = realVisibleItemPosition; position <= lastRealPosition; position++) {
						if (lastVisibleItemPosition != firstVisibleItemPosition && callback.isStickyPosition(position)) {
							lastVisibleItemPosition = firstVisibleItemPosition;
							callback.initStickyView(mStickyView, realVisibleItemPosition);        //
							break;
						}
					}
					mStickyView.setTranslationY(0);        // 悬浮

					// 在这个范围内,找到本页内可能出现的下一个阶段的条目位置.		--》悬浮移动
					int stickyPosition = findStickyPosition(realVisibleItemPosition + 1, linearLayoutManager.findLastVisibleItemPosition());
					if (RecyclerView.NO_POSITION != stickyPosition) {
						// 下一个 StickyView，到顶的时候进行位移
						View nextAdapterView = layoutManager.findViewByPosition(stickyPosition);
						if (null != nextAdapterView && nextAdapterView.getTop() < mStickyView.getHeight()) {
							mStickyView.setTranslationY(nextAdapterView.getTop() - mStickyView.getHeight());
						}
					}
				}
			}
		}

		int findStickyPosition(int position, int lastVisibleItemPosition) {
			int stickyPosition = RecyclerView.NO_POSITION;
			for (int index = position; index <= lastVisibleItemPosition; index++) {
				if (callback.isStickyPosition(index)) {
					stickyPosition = index;
					break;
				}
			}
			return stickyPosition;
		}
	}
}
