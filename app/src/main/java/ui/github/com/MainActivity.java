package ui.github.com;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import ui.github.com.ui.FloatHeaderActivity;
import ui.github.com.ui.ItemTypeDivideActivity;
import ui.github.com.library.base.BaseRecyclerViewAdapter;
import ui.github.com.library.base.BaseRecyclerViewHolder;
import ui.github.com.library.base.Divider;

public class MainActivity extends AppCompatActivity {

	private RecyclerView recyclerView;
	private List<ModelBean> datas = new ArrayList<>();
	private CurrentAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.addItemDecoration(new Divider(null, LinearLayoutManager.VERTICAL));
		adapter = new CurrentAdapter();
		recyclerView.setAdapter(adapter);
		adapter.setOnItemClickListener(new BaseRecyclerViewAdapter.ItemOnClickListener<ModelBean>() {
			@Override
			public void onItemClick(View view, int position, ModelBean item) {
				Intent intent = new Intent();
				intent.setClassName(getApplicationContext(), item.className);
				startActivity(intent);
			}
		});
		initData();
	}

	private void initData() {
		datas.add(new ModelBean("条目与分割线", ItemTypeDivideActivity.class.getName()));
		datas.add(new ModelBean("悬浮标题实现1", FloatHeaderActivity.class.getName()));
		adapter.replaceData(datas);
	}

	class CurrentAdapter extends BaseRecyclerViewAdapter<ModelBean> {

		@Override
		public int getItemLayoutId(int viewType) {
			return android.R.layout.simple_list_item_1;
		}

		@Override
		protected void onConvert(BaseRecyclerViewHolder holder, ModelBean item, int position) {
			holder.setText(android.R.id.text1, item.title);
		}
	}
}
