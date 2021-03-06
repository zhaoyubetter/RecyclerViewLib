package ui.github.com.ui.loadmore;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ui.github.com.R;
import ui.github.com.library.recycler.base.BaseRecyclerViewHolder;
import ui.github.com.library.recycler.decoration.Divider;
import ui.github.com.library.recycler.loadmore.BaseLoadMoreRecyclerAdapter;

import static ui.github.com.R.id.recyclerView;

/**
 * 加载更多测试3 - 自定义加载尾
 */
public class LoadMoreTest3Activity extends AppCompatActivity {

	private RecyclerView mRecycler;
	private int mCurrentPage = 1;
	private BaseLoadMoreRecyclerAdapter mLoadMoreAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("加载更多-自定义加载View");
		setContentView(R.layout.activity_load_more1);
		mRecycler = (RecyclerView) findViewById(recyclerView);
		mRecycler.setLayoutManager(new LinearLayoutManager(this));
		Divider divider = new Divider(null, LinearLayoutManager.VERTICAL);
		divider.setIgnoreLastDivider(true);
		divider.setDivider(new ColorDrawable(Color.GRAY) {
			@Override
			public int getIntrinsicHeight() {
				return 2;
			}
		});
		mRecycler.addItemDecoration(divider);
		mLoadMoreAdapter = new LoadMoreAdapter(mRecycler, null);
		mRecycler.setAdapter(mLoadMoreAdapter);

		// 创建数据
		createData();

		// loadMore Listener
		mLoadMoreAdapter.setOnLoadMoreListener(new BaseLoadMoreRecyclerAdapter.OnLoadMoreListener() {
			@Override
			public void onLoadMore() {
				createData();
			}
		});
		mLoadMoreAdapter.setLoadMore(new CustomLoadMoreView());

		// 自动加载
		findViewById(R.id.autoLoad).setVisibility(View.INVISIBLE);
		findViewById(R.id.loadByUser).setVisibility(View.INVISIBLE);
	}

	private void createData() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				SystemClock.sleep(1000);
				if (new Random().nextInt(10) > 1) {
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
				if (mCurrentPage == 5) {
					mLoadMoreAdapter.setStateLoadedAll();
				}
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
