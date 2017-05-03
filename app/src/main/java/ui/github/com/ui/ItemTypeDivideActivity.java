package ui.github.com.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import ui.github.com.R;
import ui.github.com.library.recycler.base.BaseRecyclerViewAdapter;
import ui.github.com.library.recycler.base.BaseRecyclerViewHolder;


/**
 * 分条目与分割线
 * Created by zhaoyu on 2017/4/6.
 */
public class ItemTypeDivideActivity extends AppCompatActivity {


	private RecyclerView mRecyclerView;
	private List<TypeItem> mData = new ArrayList<>();
	private CurrentAdapter mAdapter;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_type);

		setTitle("分条目与分割线");

		for (int i = 64; i < 64 + 15; i++) {
			String title = "" + (char) i;
			mData.add(new TypeItem(CurrentAdapter.TYPE_TITLE, title));

			for (int j = 0; j < 10; j++) {
				mData.add(new TypeItem(CurrentAdapter.TYPE_NORMAL, title + (j + 1)));
			}
		}


		// 1.创建RecyclerView对象
		mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
		// 2.设置显示规则
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
		// 3.设置adapter
		mAdapter = new CurrentAdapter();
		mRecyclerView.setAdapter(mAdapter);
		// 4.添加分割线
		mRecyclerView.addItemDecoration(new CurrentItemDecoration(this));

		mAdapter.replaceData(mData);
	}

	/**
	 * 适配器
	 */
	private class CurrentAdapter extends BaseRecyclerViewAdapter<TypeItem> {

		public final static int TYPE_NORMAL = 0;
		public final static int TYPE_TITLE = 1;

		@Override
		public int getItemViewType(int position) {
			return data.get(position).type;
		}

		@Override
		public int getItemLayoutId(int viewType) {
			int layoutRes = R.layout.item_normal;
			switch (viewType) {
				case TYPE_NORMAL:
					break;
				case TYPE_TITLE:
					layoutRes = R.layout.item_title;
					break;
			}
			return layoutRes;
		}

		@Override
		protected void onConvert(BaseRecyclerViewHolder holder, TypeItem item, int position) {
			switch (holder.getItemViewType()) {
				case TYPE_NORMAL:
					holder.setText(R.id.name, data.get(position).name);
					break;
				case TYPE_TITLE:
					holder.setText(R.id.name, data.get(position).name);
					break;
			}
		}
	}

	/**
	 * 分割线
	 */
	public static class CurrentItemDecoration extends RecyclerView.ItemDecoration {

		private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

		private final Drawable mDivider;
		private final int mNomalPaddingLeft;

		/**
		 * 传Activity
		 *
		 * @param context
		 */
		public CurrentItemDecoration(Context context) {
			final TypedArray a = context.obtainStyledAttributes(ATTRS);
			mDivider = a.getDrawable(0);
			mNomalPaddingLeft = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60f,
					context.getResources().getDisplayMetrics());
			a.recycle();
		}

		/**
		 * 画垂直的
		 *
		 * @param c
		 * @param parent
		 */
		@Override
		public void onDraw(Canvas c, RecyclerView parent) {
			super.onDraw(c, parent);

			final int left = parent.getPaddingLeft();
			final int right = parent.getWidth() - parent.getPaddingRight();

			int count = parent.getChildCount() - 1;    // 最底下不画
			for (int i = 0; i < count; i++) {
				final View child = parent.getChildAt(i);
				final int next_itemType = parent.getChildViewHolder(parent.getChildAt(i + 1)).getItemViewType();

				// 分割线判断逻辑
				int itemType = parent.getChildViewHolder(child).getItemViewType();
				if (itemType == CurrentAdapter.TYPE_TITLE || next_itemType == CurrentAdapter.TYPE_TITLE) {
					continue;
				}


				final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
				final int top = child.getBottom() + params.bottomMargin;
				final int bottom = top + mDivider.getIntrinsicHeight();

				mDivider.setBounds(left + mNomalPaddingLeft, top, right, bottom);
				mDivider.draw(c);
			}
		}

		@Override
		public void getItemOffsets(Rect outRect, int itemPosition,
								   RecyclerView parent) {
			// 偏移值
			outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
		}
	}

}
