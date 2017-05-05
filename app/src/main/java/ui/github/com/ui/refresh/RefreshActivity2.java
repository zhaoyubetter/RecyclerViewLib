package ui.github.com.ui.refresh;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ui.github.com.R;
import ui.github.com.library.recycler.base.BaseRecyclerViewAdapter;
import ui.github.com.library.refresh.PullToRefreshLayout;
import ui.github.com.ui.ItemTypeDivideActivity;
import ui.github.com.ui.TypeItem;

/**
 * 下拉刷新2，使用自定义控件
 * Created by zhaoyu on 2017/5/3.
 */
public class RefreshActivity2 extends AppCompatActivity {

	PullToRefreshLayout refreshLayout;
	RecyclerView mRecyclerView;

	private List<TypeItem> mData = new ArrayList<>();
	private ItemTypeDivideActivity.CurrentAdapter mAdapter;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		setTitle("自定义ViewGroup");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_refresh_2);
		refreshLayout = (PullToRefreshLayout) findViewById(R.id.pullToRefreshLayout);
		mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

		// 下拉刷新
		refreshLayout.setOnPullToRefreshListener(new PullToRefreshLayout.OnPullToRefreshListener() {
			@Override
			public void onRefresh() {
				refresh();
			}
		});


		for (int i = 64; i < 64 + 15; i++) {
			String title = "" + (char) i;
			mData.add(new TypeItem(ItemTypeDivideActivity.CurrentAdapter.TYPE_TITLE, title));

			for (int j = 0; j < 10; j++) {
				mData.add(new TypeItem(ItemTypeDivideActivity.CurrentAdapter.TYPE_NORMAL, title + (j + 1)));
			}
		}
		// 1.创建RecyclerView对象
		mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
		// 2.设置显示规则
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
		// 3.设置adapter
		mAdapter = new ItemTypeDivideActivity.CurrentAdapter();
		mRecyclerView.setAdapter(mAdapter);
		// 4.添加分割线
		mRecyclerView.addItemDecoration(new ItemTypeDivideActivity.CurrentItemDecoration(this));
		mAdapter.replaceData(mData);

		mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.ItemOnClickListener<TypeItem>() {
			@Override
			public void onItemClick(View view, int position, TypeItem item) {
				Toast.makeText(getApplicationContext(), item.name, Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void refresh() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				mData.clear();
				for (int i = 64; i < 64 + 15; i++) {
					String title = "刷新的数据：" + (char) i;
					mData.add(new TypeItem(ItemTypeDivideActivity.CurrentAdapter.TYPE_NORMAL, title ));
				}

				SystemClock.sleep(5000);

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mAdapter.replaceData(mData);
						refreshLayout.onRefreshComplete();
					}
				});
			}
		}).start();
	}
}