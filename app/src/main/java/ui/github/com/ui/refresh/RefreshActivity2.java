package ui.github.com.ui.refresh;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ui.github.com.R;
import ui.github.com.library.recycler.base.BaseRecyclerViewAdapter;
import ui.github.com.library.recycler.base.BaseRecyclerViewHolder;
import ui.github.com.library.recycler.base.SimpleAdapter;
import ui.github.com.library.refresh.PullToRefreshLayout;

/**
 * 下拉刷新2，使用自定义控件
 * Created by zhaoyu on 2017/5/3.
 */
public class RefreshActivity2 extends AppCompatActivity {

	PullToRefreshLayout refreshLayout;
	RecyclerView mRecyclerView;

	SimpleAdapter mAdapter;
	private List<String> mData = new ArrayList<>();
	int index = 1;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		setTitle("下拉刷新-FollowHeader");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_refresh_2);
		refreshLayout = (PullToRefreshLayout) findViewById(R.id.pullToRefreshLayout);
		mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

		final View iv = findViewById(R.id.iv);

		// =================================
		// 下拉刷新
		refreshLayout.setOnPullToRefreshListener(new PullToRefreshLayout.OnPullToRefreshListener() {
			@Override
			public void onRefresh() {
				refresh();
			}
		});

		// 自动刷新（动画）
		findViewById(R.id.auto_refresh).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				refreshLayout.autoRefreshing(true);
			}
		});
		// 自动刷新（无动画）
		findViewById(R.id.no_anim).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				refreshLayout.autoRefreshing(false);
			}
		});
		// =================================


		mAdapter = new SimpleAdapter(mData) {
			@Override
			protected void onConvert(BaseRecyclerViewHolder holder, String item, int position) {
				super.onConvert(holder, item, position);
				holder.itemView.setBackgroundResource(R.drawable.item_selector);
			}
		};
		createData();

		// 1.创建RecyclerView对象
		mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
		mRecyclerView.setAdapter(mAdapter);
		// 4.添加分割线
		mRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
		mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.ItemOnClickListener<String>() {
			@Override
			public void onItemClick(View view, int position, String item) {
				Toast.makeText(getApplicationContext(), item, Toast.LENGTH_SHORT).show();
			}
		});


	}

	private void refresh() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				mData.clear();
				createData();
				SystemClock.sleep(1200);
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

	private void createData() {
		for (int i = 64; i < 64 + 15; i++) {
			String title = "" + (char) i;
			mData.add(index + " 下拉刷新-" + title);
		}
		index++;
	}
}