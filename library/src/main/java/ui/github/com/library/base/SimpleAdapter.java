package ui.github.com.library.base;


import java.util.List;

/**
 * simpleAdapter
 * Created by zhaoyu on 2017/4/10.
 */
public class SimpleAdapter extends BaseRecyclerViewAdapter<String> {

	public SimpleAdapter(List<String> data) {
		this.data = data;
	}

	@Override
	public int getItemLayoutId(int viewType) {
		return android.R.layout.simple_list_item_1;
	}

	@Override
	protected void onConvert(BaseRecyclerViewHolder holder, String item, int position) {
		holder.setText(android.R.id.text1, item);
	}
}
