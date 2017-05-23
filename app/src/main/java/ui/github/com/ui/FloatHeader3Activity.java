package ui.github.com.ui;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import ui.github.com.Data;
import ui.github.com.R;
import ui.github.com.library.recycler.base.BaseRecyclerViewAdapter;
import ui.github.com.library.recycler.base.BaseRecyclerViewHolder;
import ui.github.com.library.recycler.callback.BinaryCondition;
import ui.github.com.library.recycler.group.GroupingStrategy;
import ui.github.com.library.recycler.group.StickyCallback;

/**
 * 悬浮头实现3
 */
public class FloatHeader3Activity extends AppCompatActivity {

	private RecyclerView recyclerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_float_header3);
		recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

		final InnerAdapter innerAdapter = new InnerAdapter(Arrays.asList(Data.ITEMS));
		recyclerView.setAdapter(innerAdapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
		recyclerView.addItemDecoration(dividerItemDecoration);
		dividerItemDecoration.setDrawable(new ColorDrawable(Color.GREEN) {
			@Override
			public int getIntrinsicHeight() {
				return 3;
			}
		});

		innerAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.ItemOnClickListener<String>() {
			@Override
			public void onItemClick(View view, int position, String item) {
				innerAdapter.removeItemAt(position);
			}
		});
	}


	class InnerAdapter extends BaseRecyclerViewAdapter<String> implements StickyCallback {

		private GroupingStrategy mGroupStrategy;

		public InnerAdapter(List<String> data) {
			super(data);

			// 自定义分组策略
			mGroupStrategy = GroupingStrategy.of(this).reduce(new BinaryCondition<String>() {
				@Override
				public boolean apply(String t1, String t2) {
					return t1.charAt(0) != t2.charAt(0);
				}
			});
		}

		@Override
		public int getItemLayoutId(int viewType) {
			return R.layout.sticky_item_1;
		}

		@Override
		protected void onConvert(BaseRecyclerViewHolder holder, String item, int position) {
			TextView stickyView = holder.getView(R.id.title);

			if (isStickyPosition(position)) {
				stickyView.setVisibility(View.VISIBLE);
				stickyView.setText(String.valueOf(item.charAt(0)));
			} else {
				stickyView.setVisibility(View.GONE);
			}
			holder.setText(R.id.content, item);
		}

		@Override
		public void initStickyView(View view, int position) {
			if (view instanceof TextView) {
				TextView tv = (TextView) view;
				String title = data.get(position);
				if (!TextUtils.isEmpty(title)) {
					tv.setText(String.valueOf(title.charAt(0)));
				}
			}
		}

		@Override
		public boolean isStickyPosition(int position) {
			return mGroupStrategy.isGroupIndex(position);
		}
	}
}
