package ui.github.com.ui.refresh;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ui.github.com.R;
import ui.github.com.library.recycler.base.BaseRecyclerViewAdapter;
import ui.github.com.library.recycler.base.BaseRecyclerViewHolder;
import ui.github.com.library.recycler.decoration.Divider;
import ui.github.com.library.recycler.loadmore.BaseLoadMoreRecyclerAdapter;

/**
 * Created by zhaoyu on 2017/4/14.
 */

public class RefreshActivity1 extends AppCompatActivity {

	private SwipeRefreshLayout refreshLayout;
	private RecyclerView mRecycler;
	private int mCurrentPage = 1;
	private LoadMoreAdapter mLoadMoreAdapter;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("系统的SwipeRefreshLayout 刷新与加载");

		setContentView(R.layout.activity_refresh_1);
		refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
		mRecycler = (RecyclerView) findViewById(R.id.recyclerView);
		mRecycler.setLayoutManager(new LinearLayoutManager(this));
		mRecycler.addItemDecoration(new Divider(null, LinearLayoutManager.VERTICAL));
		mLoadMoreAdapter = new LoadMoreAdapter(mRecycler, null);
		mLoadMoreAdapter.setSwipeRefreshLayout(refreshLayout);        // 设置刷新控件
		mRecycler.setAdapter(mLoadMoreAdapter);

		mLoadMoreAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.ItemOnClickListener() {
			@Override
			public void onItemClick(View view, int position, Object item) {
				mLoadMoreAdapter.removeItemAt(position);
			}
		});

		// 创建数据
		createData();

		// loadMore Listener
		mLoadMoreAdapter.setOnLoadMoreListener(new BaseLoadMoreRecyclerAdapter.OnLoadMoreListener() {
			@Override
			public void onLoadMore() {
				createData();
			}
		});

		// refreshData
		refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				refreshData();
			}
		});
	}

	/**
	 * 刷新获取数据
	 */
	private void refreshData() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				SystemClock.sleep(new Random().nextInt(2000) + 100);
				final List<String> currentData = new ArrayList<>(12);
				for (int i = 64; i < 64 + 12; i++) {
					String title = "刷新加入的数据：-->   " + (char) i + "";
					currentData.add(title);
				}

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						refreshLayout.setRefreshing(false);
//						mLoadMoreAdapter.addItems(0, currentData);
//						mRecycler.scrollToPosition(0);
						mLoadMoreAdapter.replaceData(currentData);
					}
				});
			}
		}).start();
	}

	private void createData() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				SystemClock.sleep(new Random().nextInt(2000) + 100);
				if (new Random().nextInt(10) > 2) {
					List<String> currentData = new ArrayList<>(12);
					for (int i = 64; i < 64 + 12; i++) {
						String title = "page: " + mCurrentPage + " " + (char) i + "";
						currentData.add(title);
					}

					finishDataCreated(currentData);
				} else {
					failedDataCreated();
				}
			}
		}).start();
	}

	/**
	 * 加载成功
	 */
	private void finishDataCreated(final List<String> currentData) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mLoadMoreAdapter.addItems(currentData);
				mCurrentPage++;
				mLoadMoreAdapter.setStateLoadedAuto();
			}
		});
	}

	/**
	 * 设置加载失败
	 */
	private void failedDataCreated() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mLoadMoreAdapter.setStateLoadedFail();
			}
		});
	}


	// =====================================================================================
	// =====================================================================================
	// =====================================================================================
	// =====================================================================================
	// =====================================================================================
	private class LoadMoreAdapter extends BaseLoadMoreRecyclerAdapter<String> {


		public LoadMoreAdapter(RecyclerView recyclerView, List<String> datas) {
			super(recyclerView, datas);
		}

		@Override
		public int getItemLayoutId(int viewType) {
			return android.R.layout.simple_list_item_2;
		}

		@Override
		protected void onConvert(BaseRecyclerViewHolder holder, String item, int position) {
			holder.setText(android.R.id.text1, item);
			holder.setText(android.R.id.text2, "Path 表示路径，可使用Canvas.drawPath方" +
					"法将其绘制出来，Path不仅可以使用Paint的填充模式和描边模式，也可以用画布裁剪和或者画文字");
			if (position % 2 == 0) {
				holder.getView(android.R.id.text2).setVisibility(View.GONE);
			} else {
				holder.getView(android.R.id.text2).setVisibility(View.VISIBLE);
			}
		}
	}

}
