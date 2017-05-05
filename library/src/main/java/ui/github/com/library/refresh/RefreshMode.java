package ui.github.com.library.refresh;

/**
 * Created by cz on 16/1/20
 * the refresh mode
 * 刷新模式
 */
enum RefreshMode {

	BOTH(0x0), PULL_FROM_START(0x1), PULL_FROM_END(0x2), DISABLED(0x3);

	static RefreshMode getDefault() {
		return BOTH;
	}

	private int intValue;

	RefreshMode(int modeInt) {
		intValue = modeInt;
	}

	public int getIntValue() {
		return intValue;
	}

	/**
	 * @return disable refresh
	 */
	public boolean disable() {
		return !(this == DISABLED);
	}

	/**
	 * @return enable header refresh
	 */
	public boolean enableHeader() {
		return this == PULL_FROM_START || this == BOTH;
	}

	/**
	 * @return enable footer refresh
	 */
	public boolean enableFooter() {
		return this == PULL_FROM_END || this == BOTH;
	}

}