package ui.github.com.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import ui.github.com.R;
import ui.github.com.library.recycler.base.BaseRecyclerViewAdapter;
import ui.github.com.library.recycler.base.SimpleAdapter;
import ui.github.com.library.recycler.decoration.Divider;
import ui.github.com.ui.refresh.RefreshActivity1;
import ui.github.com.ui.refresh.RefreshActivity2;


/**
 * 下拉刷新
 */
public class SwipeRefreshActivity extends AppCompatActivity {

	private RecyclerView mRecycler;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mRecycler = (RecyclerView) findViewById(R.id.recyclerView);
		mRecycler.setLayoutManager(new LinearLayoutManager(this));
		mRecycler.addItemDecoration(new Divider(null, LinearLayoutManager.VERTICAL));

		initEvents();
	}

	private void initEvents() {
		List<String> data = new ArrayList<>();
		data.add("下拉刷新-上拉加载");
		data.add("下拉刷新-FollowHeader");
		SimpleAdapter adapter = new SimpleAdapter(data);
		adapter.setOnItemClickListener(new BaseRecyclerViewAdapter.ItemOnClickListener<String>() {
			@Override
			public void onItemClick(View view, int position, String item) {
				if (position == 0) {
					// 使用系统的
					Intent intent = new Intent(getApplicationContext(), RefreshActivity1.class);
					startActivity(intent);
				} else if (position == 1) {
					// 自定义ViewGroup
					Intent intent = new Intent(getApplicationContext(), RefreshActivity2.class);
					startActivity(intent);
				} else if (position == 2) {
				} else if (position == 3) {
				}
			}
		});
		mRecycler.setAdapter(adapter);
	}
}
