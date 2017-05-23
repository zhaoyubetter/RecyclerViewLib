package ui.github.com.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ui.github.com.R;
import ui.github.com.library.recycler.base.BaseRecyclerViewAdapter;
import ui.github.com.library.recycler.base.BaseRecyclerViewHolder;
import ui.github.com.library.recycler.decoration.Divider;


public class FloatHeaderActivity extends AppCompatActivity {

	final String TAG = "FloatHeaderActivity";

	private RecyclerView mRecyclerView;
	private List<TypeItem> mData = new ArrayList<>();
	private CurrentAdapter mAdapter;
	private View float_title_view;
	private int mHeaderItemHeight;
	private int mCurrPos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_float_header);
		mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
		float_title_view = findViewById(R.id.float_title);

		setTitle("悬浮标题");

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
		mRecyclerView.addItemDecoration(new Divider(null, LinearLayoutManager.VERTICAL));
		mAdapter.replaceData(mData);

		mRecyclerView.post(new Runnable() {
			@Override
			public void run() {
				mHeaderItemHeight = float_title_view.getHeight();
			}
		});

		// 5.监听滑动事件，实现悬浮
		mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			LinearLayoutManager linearLayoutManager;

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				if (linearLayoutManager == null) {
					linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
				}


				View view = linearLayoutManager.findViewByPosition(mCurrPos + 1);
				if (view != null && linearLayoutManager.getItemViewType(view) == CurrentAdapter.TYPE_TITLE) {
					Log.e(TAG, "top: " + view.getTop() + ", headerHeight: " + mHeaderItemHeight + ", title: " +
							((TextView) float_title_view.findViewById(R.id.name)).getText() + ", mCurrPos: " + mCurrPos);

					if (view.getTop() <= mHeaderItemHeight) {
						// 盖住原有Header
//						float_title_view.setY(-(mHeaderItemHeight - view.getTop()));
						float_title_view.setTranslationY(-(mHeaderItemHeight - view.getTop()));		// 正下，负上
					} else {
//						float_title_view.setY(0);
						float_title_view.setTranslationY(0);
					}
				}

				// 更新 mCurrPos
				if (mCurrPos != linearLayoutManager.findFirstVisibleItemPosition()) {
					Log.e(TAG, mCurrPos + " , " + linearLayoutManager.findFirstVisibleItemPosition());
					mCurrPos = linearLayoutManager.findFirstVisibleItemPosition();
					float_title_view.setY(0);
					updateFloatTitle();
				}
			}
		});
		updateFloatTitle();
	}

	private void updateFloatTitle() {
		TextView tv = (TextView) float_title_view.findViewById(R.id.name);
		int pos = mCurrPos;
		String title = "";
		// 往上找
		while (pos >= 0) {
			if (mData.get(pos).type == CurrentAdapter.TYPE_TITLE) {
				title = mData.get(pos).name;
				break;
			}
			pos--;
		}
		tv.setText(title);
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
					layoutRes = R.layout.item_title_float;
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
}
