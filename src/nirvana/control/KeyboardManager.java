package nirvana.control;

import javax.microedition.lcdui.Canvas;

public final class KeyboardManager {
	
	protected static final int KEY_NUM = 10;
	
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
	
	protected static final int LCTRIGGER = 10;
	
	protected static boolean[] flags = new boolean[KEY_NUM];
	protected static int[] ticks = new int[KEY_NUM];
	
	public static int current = KEY_VOID;
	public static int trigger = KEY_VOID;
	
	private KeyboardManager() {}
	
	public static void notify(int input, boolean flag) {
		int i = KEY_VOID;
		switch(input) {
		case -1:
		case Canvas.KEY_NUM2:
			i = KEY_UP;
			break;
		case -2:
		case Canvas.KEY_NUM8:
			i = KEY_DOWN;
			break;
		case -3:
		case Canvas.KEY_NUM4:
			i = KEY_LEFT;
			break;
		case -4:
		case Canvas.KEY_NUM6:
			i = KEY_RIGHT;
			break;
		case -5:
		case -7:
		case Canvas.KEY_NUM3:
		case Canvas.KEY_NUM5:
			i = KEY_FIRE;
			break;
		case -6:
		case Canvas.KEY_NUM1:
			i = KEY_CANCEL;
			break;
		case Canvas.KEY_NUM7:
			i = KEY_AUTO;
			break;
		case Canvas.KEY_NUM9:
			i = KEY_MORE;
			break;
		case Canvas.KEY_STAR:
			i = KEY_PAUSE;
			break;
		case Canvas.KEY_POUND:
			i = KEY_DEBUG;
			break;
		}
		if(i != KEY_VOID) {
			flags[i] = flag;
			if(!flag) ticks[i] = -1;
		}
	}
	
	public static void service() {
		int key = KEY_VOID;
		for(int i = 0; i < KEY_NUM; i++)
			if(flags[i]) {
				ticks[i]++;
				if(key == KEY_VOID) key = i;
				else key = -2;
			} else ticks[i] = -1;
		if(key >= 0) {
			current = key;
			if(ticks[key] % LCTRIGGER == 0 && ticks[key] != LCTRIGGER) trigger = key;
			else trigger = KEY_VOID;
		} else current = trigger = KEY_VOID;
	}
	
	public static void setFlag(int key, boolean flag) {
		flags[key] = flag;
		if(flag) {
			if(key != current) current = trigger = KEY_VOID;
			ticks[key] = 0;
		} else {
			if(key == current) current = trigger = KEY_VOID;
			ticks[key] = -1;
		}
	}
	public static void clearFlags() {
		current = trigger = KEY_VOID;
		for(int i = 0; i < KEY_NUM; i++) {
			flags[i] = false;
			ticks[i] = -1;
		}
	}
	
	public static boolean getFlag(int key) {
		return flags[key];
	}
	public static int getTicks(int key) {
		return ticks[key];
	}
	
}
