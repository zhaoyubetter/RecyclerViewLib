package ui.github.com.library.refresh;

/**
 * 刷新状态
 * Created by czz on 2016/8/13.
 */
public enum RefreshState {
	/**
	 * start scroll state
	 * 下拉开始
	 **/
	PULL_START,

	/**
	 * start header refresh
	 * 刷新中
	 */
	START_REFRESHING,

	/**
	 * refresh over or cancel refresh
	 * 释放
	 */
	RELEASE_START,

	/**
	 * release the refreshing
	 * 刷新状态下 - 释放
	 */
	RELEASE_REFRESHING_START,

	/**
	 * refreshing header complete
	 * 刷新完成
	 */
	REFRESHING_START_COMPLETE,

	/**
	 * 无动作
	 */
	NONE
}
