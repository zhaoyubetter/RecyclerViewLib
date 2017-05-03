package ui.github.com.library.refresh;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IdRes;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import ui.github.com.library.R;
import ui.github.com.library.refresh.header.IndicatorHeader;
import ui.github.com.library.refresh.header.RefreshHeader;
import ui.github.com.library.refresh.headerstrategy.HeaderFollowStrategy;
import ui.github.com.library.refresh.headerstrategy.HeaderStrategy;


/**
 * 下拉刷新 —— 参考：cz
 * Created by zhaoyu on 2017/5/2.
 */
public class PullToRefreshLayout<V extends View> extends ViewGroup {
	private final String TAG = "PullToRefreshLayout";

	private final boolean DEBUG = true;


	/* ======== HeaderView 类型 ======== */
	public static final int HEADER_INDICATOR = 0x00;
	public static final int HEADER_FLIP = 0x01;
	public static final int HEADER_MATERIAL = 0x02;
	public static final int HEADER_DISPLAY = 0x03;
	/* ======== HeaderView 类型 ======== */

	/* ======== HeaderView 展示策略  ======== */
	public static final int STRATEGY_FOLLOW = 0x00;
	public static final int STRATEGY_OVERLAP = 0x01;
	public static final int STRATEGY_FRONT = 0x02;
	public static final int STRATEGY_SCROLL = 0x03;
	/* ======== HeaderView 展示策略  ======== */

	/**
	 * 阻力系数
	 */
	private final float mResistance = 1.8f;

	private final float MAX_PULL_INSTANCE = 400;
	private final int DEFAULT_DURATION = 400;

	/**
	 * 拉动的最大距离
	 */
	private float mMaxPullInstance = MAX_PULL_INSTANCE;
	private int mDuration = DEFAULT_DURATION;

	/**
	 * target - 目标view
	 */
	protected V mTargetView;
	private float mLastX, mLastY;
	/**
	 * move的距离
	 */
	private float mDistanceX, mDistanceY;
	private Scroller mScroller;

	/**
	 * 刷新头
	 */
	private RefreshHeader mRefreshHeader;
	/**
	 * 刷新头对应的展示策略
	 */
	private HeaderStrategy mHeaderStrategy;

	/**
	 * 自定义HeaderViewId
	 */
	private int mUserHeaderViewId;

	/**
	 * 自定义refreshViewId
	 */
	private int mUserRefreshViewId;

	public PullToRefreshLayout(Context context) {
		this(context, null);
	}


	public PullToRefreshLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mScroller = new Scroller(context);
		final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PullToRefreshLayout);
		try {
			// 设置刷新头类型
			setHeaderTypeInner(a.getInt(R.styleable.PullToRefreshLayout_pull_headerType, HEADER_INDICATOR));
			// 设置刷新头展示策略（某种类型下的）
			setHeaderStrategyInner(a.getInt(R.styleable.PullToRefreshLayout_pull_headerType, STRATEGY_FOLLOW));
		} finally {
			a.recycle();
		}
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		int childCount = getChildCount();

		if (mUserHeaderViewId != NO_ID && mUserRefreshViewId != NO_ID) {
			View headerView = findViewById(mUserHeaderViewId);
			View targetView = findViewById(mUserRefreshViewId);
		}

		mTargetView = (V) getChildAt(0);

		// 渲染完毕，初始化刷新头
		setHeaderStrategy(mHeaderStrategy);
	}

	/**
	 * 设置刷新头 - 类型
	 *
	 * @param type
	 */
	private void setHeaderTypeInner(int type) {
		Context context = getContext();
		switch (type) {
			case HEADER_INDICATOR:
				mRefreshHeader = new IndicatorHeader(context, this);
				break;
			case HEADER_FLIP:
				break;
			case HEADER_MATERIAL:
				break;
			case HEADER_DISPLAY:
				break;
		}
	}

	/**
	 * 设置刷新头
	 *
	 * @param newHeader
	 */
	public void setRefreshHeader(RefreshHeader newHeader) {
		this.mRefreshHeader = newHeader;
		this.mHeaderStrategy.onInitRefreshHeader(newHeader);
	}

	/**
	 * 设置刷新头 展示策略
	 *
	 * @param headerStrategy
	 */
	public void setHeaderStrategy(HeaderStrategy headerStrategy) {
		setHeaderStrategy(headerStrategy, true);
	}

	/**
	 * 设置刷新头策略
	 *
	 * @param strategy
	 */
	private void setHeaderStrategyInner(int strategy) {
		switch (strategy) {
			case STRATEGY_FOLLOW:
				setHeaderStrategy(new HeaderFollowStrategy(this), false);
				break;
			case STRATEGY_OVERLAP:
				break;
		}
	}

	private void setHeaderStrategy(HeaderStrategy headerStrategy, boolean initRefreshHeader) {
		this.mHeaderStrategy = headerStrategy;
		if (initRefreshHeader) {
			this.mHeaderStrategy.onInitRefreshHeader(mRefreshHeader);
			requestLayout(); // 新增了子View，LayoutParams发生了变化，requestLayout()
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (mHeaderStrategy != null) {
			mHeaderStrategy.onLayout(changed, l, t, r, b);
		}
	}

	/**
	 * 对应的手指ID
	 */
	private int mActivePointerId = MotionEvent.INVALID_POINTER_ID;

	/**
	 * 处理多点
	 *
	 * @param ev
	 */
	private void dealMulTouchEvent(MotionEvent ev) {
		final int action = ev.getActionMasked();
		switch (action) {
			case MotionEvent.ACTION_DOWN: {
				final int pointerIndex = ev.getActionIndex();
				final float x = ev.getX(pointerIndex);
				final float y = ev.getY(pointerIndex);
				mLastX = x;
				mLastY = y;
				mActivePointerId = ev.getPointerId(0);        // 记录第一个手指索引
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				final int pointerIndex = ev.findPointerIndex(mActivePointerId);
				final float x = ev.getX(pointerIndex);
				final float y = ev.getY(pointerIndex);
				mDistanceX = x - mLastX;
				mDistanceY = y - mLastY;
				mLastX = x;
				mLastY = y;
				break;
			}
			case MotionEvent.ACTION_UP:        // 抬起还原
			case MotionEvent.ACTION_CANCEL:
				mActivePointerId = MotionEvent.INVALID_POINTER_ID;
				break;

			// 另一个手指按下，mLastX,mLastY以后一个手指为准
			case MotionEvent.ACTION_POINTER_DOWN: {
				final int pointerIndex = ev.getActionIndex();
				final int pointerId = ev.getPointerId(pointerIndex);// 获取另一个手指ID
				if (pointerId != mActivePointerId) {
					mLastX = ev.getX(pointerIndex);
					mLastY = ev.getY(pointerIndex);
					mActivePointerId = ev.getPointerId(pointerIndex);
				}

				if (DEBUG) {
					Log.e(TAG, String.format("action ----> ACTION_POINTER_DOWN, mActivePointerId : %s, pointerIndex : %s, pointerId : %s",
							mActivePointerId, pointerIndex, pointerId));
				}
				break;
			}

			// 一个手指抬起，如果抬起的是上一个按下的手指，更新lastX,lastY
			case MotionEvent.ACTION_POINTER_UP: {
				final int pointerIndex = ev.getActionIndex();
				final int pointerId = ev.getPointerId(pointerIndex);
				if (pointerId == mActivePointerId) {
					final int newPointerIndex = pointerIndex == 0 ? 1 : 0;        // 限前2个手指
					mLastX = ev.getX(newPointerIndex);
					mLastY = ev.getY(newPointerIndex);
					mActivePointerId = ev.getPointerId(newPointerIndex);
				}

				if (DEBUG) {
					Log.e(TAG, String.format("action ----> ACTION_POINTER_UP, mActivePointerId : %s, pointerIndex : %s, pointerId : %s",
							mActivePointerId, pointerIndex, pointerId));
				}
				break;
			}
		}

	}

	/**
	 * 事件分发
	 *
	 * @param ev
	 * @return
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		dealMulTouchEvent(ev);
		final int action = ev.getActionMasked();
		switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_MOVE:
				break;
			case MotionEvent.ACTION_UP:
				break;
		}
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mTargetView == null) {
			return false;
		}

		int x = (int) event.getX();
		int y = (int) event.getY();

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mLastX = x;
				mLastY = y;
				break;
			case MotionEvent.ACTION_MOVE: {
				int scrollY = Math.abs(getScrollY());                    // 垂直滚动距离
				int moveDistanceY = (int) (mDistanceY / mResistance);   // 阻尼运动，原距离/1.8f
				if (mDistanceY > 0 && MAX_PULL_INSTANCE <= scrollY) {
					moveDistanceY = 0;
				}
				scrollBy(0, -moveDistanceY);
				if (DEBUG) {
					Log.e(TAG, String.format("distanceY: %s, scrollY: %s, moveDistanceY: %s", mDistanceY, getScrollY(), moveDistanceY));
				}
				break;
			}
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				mScroller.startScroll(0, getScrollY(), 0, -getScrollY(), mDuration);
				invalidate();
				break;
		}
		return true;
		//return super.onTouchEvent(event);
	}

	@Override
	public void computeScroll() {
		super.computeScroll();
		if (mScroller.computeScrollOffset()) {
			scrollTo(0, mScroller.getCurrY());
			postInvalidate();
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			// 测量孩子
			measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
		}
	}

	/**
	 * 自定义刷新头Id
	 *
	 * @param mUserHeaderViewId
	 */
	private void setUserHeaderViewId(@IdRes int mUserHeaderViewId) {
		this.mUserHeaderViewId = mUserHeaderViewId;
	}

	/**
	 * 自定义刷新内容Id
	 *
	 * @param mUserRefreshViewId
	 */
	private void setUserRefreshViewId(@IdRes int mUserRefreshViewId) {
		this.mUserRefreshViewId = mUserRefreshViewId;
	}

	/**
	 * 获取刷新头
	 *
	 * @return
	 */
	public RefreshHeader getRefreshHeader() {
		return mRefreshHeader;
	}

	public V getRefreshView() {
		return mTargetView;
	}
}