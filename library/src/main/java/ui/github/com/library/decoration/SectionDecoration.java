package ui.github.com.library.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;

/**
 * 悬浮的 SectionHeader
 * Created by zhaoyu on 2017/4/7.
 */
public class SectionDecoration extends RecyclerView.ItemDecoration {

	private SparseArray<String> mSecitonMap = new SparseArray<>();
	private int mSectionAreaHeight = 24;
	private int mTextColor = Color.WHITE;
	private Drawable mBgDrawable;
	private TextPaint mTextPaint;
	private Context mContext;
	private int mTextSize = 14;
	private int mTextHeight;
	private int mTextBaselineOffset;
	private Paint mPaint;
	private int mSectionPaddingLeft;

	public SectionDecoration(Context context, SparseArray<String> sectionMap, int sectionPaddingLeft) {
		this(context, sectionMap);
		this.mSectionPaddingLeft = sectionPaddingLeft;
	}

	public SectionDecoration(Context context, SparseArray<String> sectionMap) {
		this.mSecitonMap = sectionMap;
		this.mContext = context.getApplicationContext();
		mBgDrawable = new ColorDrawable(Color.GRAY);

		mPaint = new Paint();
		mPaint.setColor(Color.GRAY);
		mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

		mTextPaint = new TextPaint();
		mTextPaint.setColor(mTextColor);
		mTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, mContext.getResources().getDisplayMetrics());
		mTextPaint.setTextSize(mTextSize);

		mSectionAreaHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, mContext.getResources().getDisplayMetrics());
		mSectionPaddingLeft = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, mContext.getResources().getDisplayMetrics());

		Paint.FontMetrics fm = mTextPaint.getFontMetrics();
		mTextHeight = (int) (fm.bottom - fm.top);        // 文字高度
		mTextBaselineOffset = (int) fm.bottom;
	}

	/**
	 * sectionHeader paddingLeft
	 *
	 * @param sectionPaddingLeft
	 * @return
	 */
	public SectionDecoration setSectionPaddingLeft(int sectionPaddingLeft) {
		this.mSectionPaddingLeft = sectionPaddingLeft;
		return this;
	}

	/**
	 * SectionHeaderBg
	 *
	 * @param bgDrawable
	 * @return
	 */
	public SectionDecoration setSectionHeaderBackground(Drawable bgDrawable) {
		this.mBgDrawable = bgDrawable;
		return this;
	}

	/**
	 * Section 高度
	 *
	 * @param height
	 * @return
	 */
	public SectionDecoration setSectionHeigth(int height) {
		this.mSectionAreaHeight = height;
		return this;
	}

	public SectionDecoration setTextColor(int color) {
		this.mTextColor = color;
		return this;
	}

	public SectionDecoration setTextSize(int size) {
		this.mTextSize = size;
		return this;
	}

	/**
	 * 设置指定itemview的paddingLeft，paddingTop， paddingRight， paddingBottom
	 *
	 * @param outRect
	 * @param view
	 * @param parent
	 * @param state
	 */
	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
		super.getItemOffsets(outRect, view, parent, state);
		int position = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewAdapterPosition();
		outRect.set(0, mSecitonMap.get(position) != null ? mSectionAreaHeight : 0, 0, 0);
	}

	/**
	 * 在绘制itemView之前绘制我们需要的内容
	 *
	 * @param c
	 * @param parent
	 * @param state
	 */
	@Override
	public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
		super.onDraw(c, parent, state);
		final int left = parent.getPaddingLeft();
		final int right = parent.getWidth() - parent.getPaddingRight();
		final int childCount = parent.getChildCount();
		for (int i = 0; i < childCount; i++) {
			final View child = parent.getChildAt(i);
			final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
			int position = params.getViewAdapterPosition();
			if (mSecitonMap.get(position) != null) {
				drawSectionArea(c, left, right, child, params, position);
			}
		}
	}

	private void drawSectionArea(Canvas c, int left, int right, View child,
								 RecyclerView.LayoutParams params, int position) {
		final int rectBottom = child.getTop() - params.topMargin;
		// 绘制悬浮栏
		//c.drawRect(left, rectBottom - mSectionAreaHeight, right, rectBottom, mPaint);
		Rect clipRect = new Rect(left, rectBottom - mSectionAreaHeight, right, rectBottom);
		c.save();
		c.clipRect(clipRect);
		mBgDrawable.getBounds().set(clipRect);
		mBgDrawable.draw(c);
		// 垂直居中绘制文字
		c.drawText(mSecitonMap.get(position), left + mSectionPaddingLeft,
				rectBottom - (mSectionAreaHeight - mTextHeight) / 2 - mTextBaselineOffset, mTextPaint);
		c.restore();
	}

	/**
	 * 在绘制itemView之后绘制，具体表现形式，就是绘制的内容在itemview上层。
	 *
	 * @param c
	 * @param parent
	 * @param state
	 */
	@Override
	public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
		super.onDrawOver(c, parent, state);
		final int firstVisPos = ((LinearLayoutManager) parent.getLayoutManager()).findFirstVisibleItemPosition();
		if (firstVisPos == RecyclerView.NO_POSITION) {
			return;
		}
		View child = parent.findViewHolderForAdapterPosition(firstVisPos).itemView;
		String sectionHeader = getSectionHeaderTag(firstVisPos);
		if (sectionHeader == null) {
			return;
		}

		boolean flag = false;
		String sectionHeader2 = getSectionHeaderTag(firstVisPos + 1);
		if (sectionHeader2 != null && !sectionHeader.equals(sectionHeader2)) {
			if (child.getHeight() + child.getTop() < mSectionAreaHeight) {
				c.save();
				flag = true;
				c.translate(0, child.getHeight() + child.getTop() - mSectionAreaHeight);
			}
		}

		// 裁剪
		Rect clipRect = new Rect(parent.getPaddingLeft(), parent.getPaddingTop(),
				parent.getRight() - parent.getPaddingRight(), parent.getPaddingTop() + mSectionAreaHeight);
		c.clipRect(clipRect);
		mBgDrawable.getBounds().set(clipRect);
		mBgDrawable.draw(c);

		c.drawText(sectionHeader, child.getPaddingLeft() + mSectionPaddingLeft,
				parent.getPaddingTop() + mSectionAreaHeight - (mSectionAreaHeight - mTextHeight) / 2 - mTextBaselineOffset, mTextPaint);

		if (flag) {
			c.restore();
		}
	}

	private String getSectionHeaderTag(int position) {
		// 不断往上找
		while (position >= 0) {
			if (mSecitonMap.get(position) != null) {
				return mSecitonMap.get(position);
			}
			position--;
		}
		return null;
	}

}
