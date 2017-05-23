package ui.github.com.library.refresh;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IdRes;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import ui.github.com.library.R;
import ui.github.com.library.refresh.header.FlipHeader;
import ui.github.com.library.refresh.header.RefreshHeader;
import ui.github.com.library.refresh.headerstrategy.HeaderOverlapStrategy;
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
	/* ======== HeaderView 类型 ======== */

	/* ======== HeaderView 展示策略  ======== */
	public static final int STRATEGY_FOLLOW = 0x00;
	public static final int STRATEGY_OVERLAP = 0x01;
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
	 * 刷新头对应的策略（展示、是否拦截事件）
	 */
	private HeaderStrategy mHeaderStrategy;

	/**
	 * 指定的HeaderViewId
	 */
	private int mHeaderViewId;

	/**
	 * 指定的refreshViewId
	 */
	private int mRefreshViewId;
	/**
	 * 是否拦截事件
	 */
	private boolean mIntercept;
	/**
	 * 刷新模式
	 */
	private RefreshMode mRefreshMode;
	/**
	 * 刷新状态
	 */
	private RefreshState mRefreshState;

	/**
	 * 是否重新 dispatch
	 */
	private boolean mIsReDispatch;

	private boolean mIsReleasing;//release the refreshing

	/**
	 * 下拉刷新回调接口
	 */
	private OnPullToRefreshListener mListener;

	/**
	 * 速度追踪
	 */
	private VelocityTracker mVelocityTracker;

	private int mScaledMaximumFlingVelocity;
	private int mScaledMinimumFlingVelocity;

	/**
	 * 是否显示刷新完成提示
	 */
	private boolean mShowRefreshCompleteInfo = false;

	public PullToRefreshLayout(Context context) {
		this(context, null);
	}


	public PullToRefreshLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// 初始化操作
		mRefreshState = RefreshState.NONE;
		mScroller = new Scroller(context);
		mScaledMaximumFlingVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
		mScaledMinimumFlingVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();

		final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PullToRefreshLayout);
		try {
			// 刷新完成提示
			setShowRefreshCompleteInfo(a.getBoolean(R.styleable.PullToRefreshLayout_pull_show_refresh_complete, false));
			setPullMaxHeight(a.getDimension(R.styleable.PullToRefreshLayout_pull_max_height, MAX_PULL_INSTANCE));

			// 自己指定内容
			setHeaderViewId(a.getResourceId(R.styleable.PullToRefreshLayout_pull_header_resourceId, NO_ID));
			setRefreshViewId(a.getResourceId(R.styleable.PullToRefreshLayout_pull_refreshView_resourceId, NO_ID));

			// 设置刷新头类型
			setHeaderTypeInner(a.getInt(R.styleable.PullToRefreshLayout_pull_headerType, HEADER_INDICATOR));
			// 设置刷新头展示策略（某种类型下的）
			setHeaderStrategyInner(a.getInt(R.styleable.PullToRefreshLayout_pull_headerType, STRATEGY_FOLLOW));
			// 设置刷新模式
			setRefreshModeInner(a.getInt(R.styleable.PullToRefreshLayout_pull_refreshMode, RefreshMode.getDefault().getIntValue()));
		} finally {
			a.recycle();
		}
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		int childCount = getChildCount();

		if (mHeaderViewId != NO_ID && mRefreshViewId != NO_ID) {
			View headerView = findViewById(mHeaderViewId);
			View targetView = findViewById(mRefreshViewId);
		}

		mTargetView = (V) getChildAt(0);

		// 渲染完毕，初始化刷新头
		setHeaderStrategy(mHeaderStrategy);
	}

	private void setRefreshModeInner(int mode) {
		setRefreshMode(RefreshMode.values()[mode]);
	}

	/**
	 * 设置刷新头策略
	 *
	 * @param strategy
	 */
	private void setHeaderStrategyInner(int strategy) {
		switch (strategy) {
			case STRATEGY_FOLLOW:
				setHeaderStrategy(new HeaderOverlapStrategy(this), false);
				break;
			case STRATEGY_OVERLAP:
				break;
		}
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
				mRefreshHeader = new FlipHeader(context, this);
				break;
			case HEADER_FLIP:
				mRefreshHeader = new FlipHeader(context, this);
				break;
			case HEADER_MATERIAL:
				break;
		}
	}

	public void setPullMaxHeight(float pullMaxHeight) {
		this.mMaxPullInstance = pullMaxHeight;
	}

	/**
	 * 设置是否显示刷新完成提示
	 *
	 * @param showInfo
	 */
	public void setShowRefreshCompleteInfo(boolean showInfo) {
		this.mShowRefreshCompleteInfo = showInfo;
	}

	/**
	 * 是否显示刷新完成提示
	 *
	 * @return
	 */
	public boolean isShowRefreshCompleteInfo() {
		return mShowRefreshCompleteInfo;
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
			case MotionEvent.ACTION_DOWN:        // 按下
				if (!isHeaderRefresh() && isChildScrollToTop()) {            // 不处于刷新态 && 在顶部
					mIntercept = false;
				} else {
					mIntercept = mHeaderStrategy.isIntercept(mDistanceY);    // 策略中判断
				}
				if (DEBUG)
					Log.e(TAG, "dispatchTouchEvent DOWN -->  Intercept: " + mIntercept + " isTop:" + isChildScrollToTop() + " intercept: " + mHeaderStrategy.isIntercept(mDistanceY)
							+ " DistanceY: " + mDistanceY + ", scrollY: " + getScrollY());
				break;
			case MotionEvent.ACTION_MOVE:        // 移动
				if (null == mTargetView || Math.abs(mDistanceY) < Math.abs(mDistanceX)) {
					mIntercept = false;
				} else {
					mIntercept = mHeaderStrategy.isIntercept(mDistanceY);
				}

				if (DEBUG)
					Log.e(TAG, "dispatchTouchEvent MOVE -->  Intercept: " + mIntercept + " isTop:" + isChildScrollToTop() + " intercept: " + mHeaderStrategy.isIntercept(mDistanceY)
							+ " DistanceY: " + mDistanceY + ", scrollY: " + getScrollY());

				// 重发 dispatchTouchEvent
//				if (mIntercept && !mIsReDispatch) {
//					mIsReDispatch = true;
//					ev.setAction(MotionEvent.ACTION_CANCEL);
//					MotionEvent ev2 = MotionEvent.obtain(ev);
//					dispatchTouchEvent(ev);
//					ev2.setAction(MotionEvent.ACTION_DOWN);
//					return dispatchTouchEvent(ev2);
//				}

				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				if (DEBUG)
					Log.e(TAG, "dispatchTouchEvent UP_CANCEL -->  Intercept: " + mIntercept);
				break;
		}
		return super.dispatchTouchEvent(ev);
	}

	/**
	 * check header refresh status is drag
	 *
	 * @return
	 */
	private boolean isHeaderRefresh() {
		return RefreshState.RELEASE_START == mRefreshState || RefreshState.START_REFRESHING == mRefreshState;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return mIntercept && mRefreshMode.enableHeader();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();

		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				stopScrollAnimation();
				mLastX = x;
				mLastY = y;
				// when current state is not refreshing,set pull start
				if (RefreshState.START_REFRESHING != mRefreshState) {
					mRefreshHeader.onRefreshStateChange(mRefreshState = RefreshState.PULL_START);
				}
				break;
			case MotionEvent.ACTION_MOVE: {
				if (mIntercept) {        // 被打断
					if (RefreshState.NONE == mRefreshState) {    // 下拉开始
						mRefreshHeader.onRefreshStateChange(mRefreshState = RefreshState.PULL_START);
					}
					mHeaderStrategy.onMoveOffset(mDistanceY);
				} else if (0 != mDistanceY && mHeaderStrategy.isMoveToTop()) {

					// 因为 mIntercept 由 dispatchTouchEvent 来设置，有可能 mIntercept 设置成了false(上滑动)
					/**{@link #dispatchTouchEvent -> ACTION_MOVE}*/
					if (DEBUG)
						Log.e(TAG, "onTouchEvent：MOVE --------------========-->  move top");
					// 不拦截时，重发down事件
					mHeaderStrategy.onResetRefresh(RefreshState.PULL_START);
					event.setAction(MotionEvent.ACTION_DOWN);
					dispatchTouchEvent(event);
					mIsReDispatch = false;
				}
				break;
			}
			case MotionEvent.ACTION_UP:
				if (DEBUG)
					Log.e(TAG, "onTouchEvent up:" + mRefreshState);
				// 追踪速度
				mVelocityTracker.computeCurrentVelocity(1000, mScaledMaximumFlingVelocity);
				mIsReDispatch = false;
				if (RefreshState.RELEASE_START == mRefreshState) {       // 释放刷新
					if (DEBUG)
						Log.e(TAG, "onTouchEvent up: 触发刷新 ===》 refresh");
					callRefreshListener();
				}
				mHeaderStrategy.onResetRefresh(mRefreshState);
				releaseVelocityTracker();
				break;
			case MotionEvent.ACTION_CANCEL:
				stopScrollAnimation();
				releaseVelocityTracker();
				break;
		}
		return true;
	}

	private void releaseVelocityTracker() {
		if (null != mVelocityTracker) {
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}
	}

	public void callRefreshListener() {
		if (null != mListener) {
			mListener.onRefresh();
		}
	}

	@Override
	public void computeScroll() {
		super.computeScroll();
		if (mScroller.computeScrollOffset()) {
			scrollTo(0, mScroller.getCurrY());
			postInvalidate();
		}
	}

	private void stopScrollAnimation() {
		if (!mScroller.isFinished()) {
			mScroller.abortAnimation();
		}
	}

	/**
	 * 是否滑动到顶部
	 *
	 * @return
	 */
	public boolean isChildScrollToTop() {
		// ViewCompat.canScrollVertically(mTargetView, -1); 到顶了返回 false
		return !ViewCompat.canScrollVertically(mTargetView, -1);
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
	private void setHeaderViewId(@IdRes int mUserHeaderViewId) {
		this.mHeaderViewId = mUserHeaderViewId;
	}

	/**
	 * 自定义刷新内容Id
	 *
	 * @param mUserRefreshViewId
	 */
	private void setRefreshViewId(@IdRes int mUserRefreshViewId) {
		this.mRefreshViewId = mUserRefreshViewId;
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

	public void setRefreshMode(RefreshMode refreshMode) {
		this.mRefreshMode = refreshMode;
	}

	public float getResistance() {
		return mResistance;
	}

	/**
	 * 最大下拉距离
	 *
	 * @return
	 */
	public float getPullMaxHeight() {
		return mMaxPullInstance;
	}

	/**
	 * 改变刷新状态 {@link #mRefreshState}
	 *
	 * @param fraction 范围 [0, 1]
	 */
	public void refreshStateChange(float fraction) {
		if (RefreshState.PULL_START == mRefreshState && fraction >= 1.0f) {        // 开始下拉 -> 释放刷新  ( 拉满 >=1.0 )
			mRefreshState = RefreshState.RELEASE_START;
			// 刷新头根据状态，改变刷新头界面显示形式
			mRefreshHeader.onRefreshStateChange(mRefreshState);
		} else if (RefreshState.RELEASE_START == mRefreshState && fraction < 1.0f) { // 释放刷新 -> 开始下拉  ( 未拉满 < 1.0 )
			mRefreshState = RefreshState.PULL_START;
			mRefreshHeader.onRefreshStateChange(mRefreshState);
		}

		////////////////////////
		else if (RefreshState.START_REFRESHING == mRefreshState && fraction < 1.0f) { // 刷新中 -> 刷新下释放
			mRefreshState = RefreshState.RELEASE_REFRESHING_START;
			mRefreshHeader.onRefreshStateChange(mRefreshState);
		} else if (RefreshState.RELEASE_REFRESHING_START == mRefreshState && fraction >= 1.0f) { // 刷新下释放 -> 刷新中
			// when current state is release refreshing and fraction greater then 1.0 set refresh state start refreshing
			mRefreshState = RefreshState.START_REFRESHING;
			mRefreshHeader.onRefreshStateChange(mRefreshState);
		}


		/*
		 * 1. PULL_START  				-> 	RELEASE_START 				>= 1.0
		 * 2. RELEASE_START 			->  PULL_START					< 1.0
		 * 3. START_REFRESHING			->  RELEASE_REFRESHING_START	< 1.0
		 * 4. RELEASE_REFRESHING_START	->  START_REFRESHING			>= 1.0
		 */

		/**
		 * START_REFRESHING 刷新状态，在 onTouchEvent UP 事件中，由策略来设置
		 * {@link #onTouchEvent ACTION_UP}
		 * such as @see HeaderFollowStrategy#onResetRefresh(RefreshState)
		 */

		if (DEBUG) {
			Log.e(TAG, "refreshStateChange currentState is : " + mRefreshState + " and fraction: " + fraction);
		}
	}

	public int getScrollDuration() {
		return mDuration;
	}


	public void setReleasing(boolean releasing) {
		this.mIsReleasing = releasing;
	}

	public void startScroll(int startX, int startY, int dx, int dy, int duration) {
		mScroller.startScroll(startX, startY, dx, dy, duration);
	}

	/**
	 * 设置刷新状态
	 *
	 * @param refreshState
	 */
	public void setRefreshState(RefreshState refreshState) {
		this.mRefreshState = refreshState;
		if (mRefreshHeader != null) {
			mRefreshHeader.onRefreshStateChange(refreshState);
		}
	}

	public RefreshState getRefreshState() {
		return mRefreshState;
	}

	/**
	 * 刷新完成
	 */
	public void onRefreshComplete() {
		mIntercept = false;
		if (null != mHeaderStrategy) {
			mHeaderStrategy.onRefreshComplete();
		}
	}

	public void setOnPullToRefreshListener(OnPullToRefreshListener listener) {
		this.mListener = listener;
	}

	public VelocityTracker getVelocityTracker() {
		return mVelocityTracker;
	}

	public int getScaledMinimumFlingVelocity() {
		return mScaledMinimumFlingVelocity;
	}

	/**
	 * 执行自动刷新，直接交给策略
	 *
	 * @param anim 是否带动画
	 */
	public void autoRefreshing(boolean anim) {
		if (null != mHeaderStrategy && mRefreshMode.enableHeader()) {
			mHeaderStrategy.autoRefreshing(anim);
		}
	}

	/**
	 * pull to refresh listener
	 */
	public interface OnPullToRefreshListener {
		void onRefresh();
	}
}