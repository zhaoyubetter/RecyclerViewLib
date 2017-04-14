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
import ui.github.com.library.base.BaseRecyclerViewAdapter;
import ui.github.com.library.base.SimpleAdapter;
import ui.github.com.library.decoration.Divider;
import ui.github.com.ui.loadmore.LoadMoreTest1Activity;
import ui.github.com.ui.loadmore.LoadMoreTest2Activity;
import ui.github.com.ui.loadmore.LoadMoreTest3Activity;


/**
 * 加载更多页面
 * Created by zhaoyu on 2017/4/10.
 */
public class LoadMoreActivity extends AppCompatActivity {

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
		data.add("加载更多-无限数据集");
		data.add("加载更多-有限数据集");
		data.add("加载更多-自定义加载View");
		SimpleAdapter adapter = new SimpleAdapter(data);
		adapter.setOnItemClickListener(new BaseRecyclerViewAdapter.ItemOnClickListener<String>() {
			@Override
			public void onItemClick(View view, int position, String item) {
				if (position == 0) {
					Intent intent = new Intent(getApplicationContext(), LoadMoreTest1Activity.class);
					startActivity(intent);
				} else if (position == 1) {
					Intent intent = new Intent(getApplicationContext(), LoadMoreTest2Activity.class);
					startActivity(intent);
				} else if (position == 2) {
					Intent intent = new Intent(getApplicationContext(), LoadMoreTest3Activity.class);
					startActivity(intent);
				}
			}
		});
		mRecycler.setAdapter(adapter);
	}
}
