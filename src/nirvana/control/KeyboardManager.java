package nirvana.control;

import javax.microedition.lcdui.Canvas;

/**
 * 游戏的键盘管理静态类，不能创建实例。
 * 本类将设备的按键映射到虚拟按键上，并提供了简单判断按键与重复的方法。
 * 由当前Handler调用{@link #notify(int, boolean)}与{@link #service()}更新按键状态。
 * @version final
 * @author Wilderness Ranger
 */
public final class KeyboardManager {
	
	/* =====常量===== */
	
	/** 被映射按键总数。 */
	protected static final int KEY_NUM = 10;
	
	/** 无效键或未按键。 */
	public static final int KEY_VOID = -1;
	public static final int KEY_UP = 0;
	public static final int KEY_DOWN = 1;
	public static final int KEY_LEFT = 2;
	public static final int KEY_RIGHT = 3;
	public static final int KEY_FIRE = 4;
	public static final int KEY_CANCEL = 5;
	public static final int KEY_AUTO = 6;
	public static final int KEY_MORE = 7;
	public static final int KEY_PAUSE = 8;
	public static final int KEY_DEBUG = 9;
	
	/** 发生按键重复间隔的游戏tick数。（常用值：10） */
	protected static final int TRIGGER_RC = 10;
	
	/* =====静态变量===== */
	
	/** 新动作的按键列表。 */
	protected static boolean[] impacts = new boolean[KEY_NUM];
	/** 按下键的游戏tick数列表。如果键未按下，则对应元素为-1。 */
	protected static int[] ticks = new int[KEY_NUM];
	
	/** 当前按着的键。如果按下了多个键，则认为未按键。 */
	protected static int current = KEY_VOID;
	/** 当前按键动作或按键重复。如果按下了多个键，则不会重复。 */
	protected static int trigger = KEY_VOID;
	
	/* =====静态方法===== */
	
	/**
	 * 通知按键状态改变。应由Handler调用此方法。
	 * @param input 改变状态按键的键值。
	 * @param flag 按键状态。
	 */
	public static void notify(int input, boolean flag) {
		int key = KEY_VOID;
		switch(input) {
		case -1:
		case Canvas.KEY_NUM2:
			key = KEY_UP;
			break;
		case -2:
		case Canvas.KEY_NUM8:
			key = KEY_DOWN;
			break;
		case -3:
		case Canvas.KEY_NUM4:
			key = KEY_LEFT;
			break;
		case -4:
		case Canvas.KEY_NUM6:
			key = KEY_RIGHT;
			break;
		case -5:
		case -7:
		case Canvas.KEY_NUM3:
		case Canvas.KEY_NUM5:
			key = KEY_FIRE;
			break;
		case -6:
		case Canvas.KEY_NUM1:
			key = KEY_CANCEL;
			break;
		case Canvas.KEY_NUM7:
			key = KEY_AUTO;
			break;
		case Canvas.KEY_NUM9:
			key = KEY_MORE;
			break;
		case Canvas.KEY_STAR:
			key = KEY_PAUSE;
			break;
		case Canvas.KEY_POUND:
			key = KEY_DEBUG;
			break;
		}
		if(flag) {
			impacts[key] = true;
			ticks[key] = 0;
		} else ticks[key] = -1;
	}
	
	/**
	 * 更新按键按键动作或按键重复{@link #current}与{@link #trigger}。
	 * 应由Handler在游戏tick开始前调用此方法。
	 */
	public static void service() {
		current = trigger = KEY_VOID;
		for(int i = 0; i < KEY_NUM; i++) {
			if(impacts[i] || ticks[i] >= 0) {
				if(current == KEY_VOID) {
					current = i;
					if(impacts[i] || ticks[i] % TRIGGER_RC == 0)
						trigger = i;
				} else {
					current = -2;
					trigger = KEY_VOID;
				}
			}
			if(ticks[i] >= 0) ticks[i]++;
		}
		if(current == -2) current = KEY_VOID;
	}
	
	/**
	 * 手动设置按键状态。应在游戏tick内调用此方法。
	 * 如果设置为按下且未按其他键，则在当前与下一个游戏tick中可产生一次按键动作{@link #trigger}；
	 * 如果设置为松开，则会立即清除当前按键。
	 * @param key 将被设置按键；
	 * @param flag 按键状态。
	 */
	public static void setKey(int key, boolean flag) {
		impacts[key] = flag;
		if(flag) {
			ticks[key] = 0;
			if(current == KEY_VOID) current = trigger = key;
			else current = trigger = KEY_VOID;
		} else {
			ticks[key] = -1;
			current = trigger = KEY_VOID;
		}
	}
	
	/**
	 * 手动点击一次按键而不持续按下。应在游戏tick内调用此方法。
	 * 如果未按其他键，则在当前与下一个游戏tick中可产生一次按键动作{@link #trigger}；
	 * @param key 将被点击按键；
	 */
	public static void clickKey(int key) {
		impacts[key] = true;
		if(current == KEY_VOID) trigger = key;
		else trigger = KEY_VOID;
	}
	
	/**
	 * 手动清除所有按键，之后即使按着键也不会产生按键重复。
	 * 应在游戏tick内调用此方法。
	 */
	public static void clearKeys() {
		for(int i = 0; i < KEY_NUM; i++) {
			impacts[i] = false;
			ticks[i] = -1;
		}
		current = trigger = KEY_VOID;
	}
	
	/* =====静态Getters===== */
	
	/**
	 * 返回对应键按下的游戏tick数。如果键未按下，则返回-1。
	 * @param key 将被查询按键。
	 */
	public static int getTicks(int key) {
		return ticks[key];
	}
	
	/**
	 * 获取当前按着的键{@link #current}。如果按下了多个键，则认为未按键。
	 * 在获取时会同时清除按键动作{@link #trigger}。
	 */
	public static int getCurrent() {
		if(current != KEY_VOID) {
			impacts[current] = false;
			trigger = KEY_VOID;
		}
		return current;
	}
	/**
	 * 获取当前按键动作或按键重复{@link #trigger}。
	 * 在获取时会同时清除按键动作{@link #trigger}。
	 */
	public static int getTrigger() {
		if(trigger != KEY_VOID) {
			impacts[current] = false;
			trigger = KEY_VOID;
		}
		return trigger;
	}
	
	/* =====类实现===== */
	
	/** @deprecated */
	private KeyboardManager() {}
	
}
