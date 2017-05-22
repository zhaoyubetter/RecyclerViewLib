package ui.github.com.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

import ui.github.com.R;
import ui.github.com.library.recycler.base.BaseRecyclerViewAdapter;
import ui.github.com.library.recycler.base.BaseRecyclerViewHolder;
import ui.github.com.library.recycler.decoration.Divider;
import ui.github.com.library.recycler.decoration.SectionDecoration;

import static ui.github.com.ui.ItemTypeDivideActivity.CurrentAdapter.TYPE_TITLE;


public class FloatHeader2Activity extends AppCompatActivity {

	final String TAG = "FloatHeaderActivity";

	private RecyclerView mRecyclerView;
	private List<TypeItem> mData = new ArrayList<>();
	private CurrentAdapter mAdapter;
	private SparseArray<String> mScetionArray = new SparseArray<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_float_header2);
		mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

		setTitle("悬浮标题2");

		int x = 0;
		for (int i = 64; i < 64 + 15; i++) {
			String title = "" + (char) i;
			mScetionArray.put(x, title);
			for (int j = 0; j < 10; j++) {
				mData.add(new TypeItem(CurrentAdapter.TYPE_NORMAL, title + (j + 1)));
				x++;
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
		//mRecyclerView.addItemDecoration(new Divider(null, LinearLayoutManager.VERTICAL));
		SectionDecoration sectionDecoration = new SectionDecoration(getApplicationContext(), mScetionArray);
		sectionDecoration.setSectionPaddingLeft(100);
		mRecyclerView.addItemDecoration(new Divider(getDrawable(R.drawable.shape_section_bg), LinearLayoutManager.VERTICAL));
		mRecyclerView.addItemDecoration(sectionDecoration);
		mAdapter.replaceData(mData);
	}


	/**
	 * 适配器
	 */
	private class CurrentAdapter extends BaseRecyclerViewAdapter<TypeItem> {

		public final static int TYPE_NORMAL = 0;

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
