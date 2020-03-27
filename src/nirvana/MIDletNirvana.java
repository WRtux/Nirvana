package nirvana;

import javax.microedition.midlet.MIDlet;

import nirvana.control.GameHandler;

/**
 * Nirvana的MIDlet类，只能创建一个实例。
 * 本类类似于main函数。
 * TODO 迁移模式设定、帧长、内存信息与log方法至{@link GameHandler}。
 * @author Wilderness Ranger
 */
public final class MIDletNirvana extends MIDlet {
	
	/* =====静态变量===== */
	
	/** MIDlet类的示例。 */
	private static MIDletNirvana instance; 
	
	/** 调试模式设定，在发布时应为{@code false}。 */
	protected static boolean debugMode = true;//false;
	/** 表现模式设定。处于表现模式时，Nirvana不会主动调用垃圾收集，并会生成更多缓存以提升性能。 */
	protected static boolean perfMode = true;
	
	/** 帧长，以毫秒为单位。{@link GameHandler}参考此数据控制游戏线程。（常用值：25, 33, 50） */
	protected static int frameLen = 33;
	
	/** 总计内存，以字节为单位。 */
	protected static int totMem;
	/** 可用内存，以字节为单位。 */
	protected static int freeMem;
	
	/* =====静态Getters===== */
	
	/** 返回Nirvana是否处于调试模式{@link #debugMode}。 */
	public static boolean isDebugMode() {
		return debugMode;
	}
	/** 返回Nirvana是否处于表现模式{@link #perfMode}。 */
	public static boolean isPerformanceMode() {
		return perfMode;
	}
	
	/** 返回当前帧长{@link #frameLen}。 */
	public static int getFrameLength() {
		return frameLen;
	}
	
	/** 返回总计内存{@link #totMem}。 */
	public static int getTotalMem() {
		return totMem;
	}
	/** 返回可用内存{@link #freeMem}。 */
	public static int getFreeMem() {
		return freeMem;
	}
	
	/* =====静态Setters===== */
	
	/** 设置Nirvana的调试模式{@link #debugMode}。 */
	public static void setDebugMode(boolean mode) {
		debugMode = mode;
	}
	/** 设置Nirvana的表现模式{@link #perfMode}。 */
	public static void setPerformanceMode(boolean mode) {
		perfMode = mode;
	}
	
	/** 设置当前帧长{@link #frameLen}。 */
	public static void setFrameLength(int len) {
		if(len <= 0) throw new IllegalArgumentException("Illegaal frame length.");
		frameLen = len;
	}
	
	/* =====静态方法===== */
	
	/**
	 * 输出控制台消息。类似于{@code System.out.println()}。
	 * 其他类应使用此方法代替{@code System.out.println()}。
	 */
	public static void log() {
		System.out.println();
	}
	/**
	 * 输出控制台消息。类似于{@code System.out.println(int)}。
	 * 其他类应使用此方法代替{@code System.out.println(int)}。
	 * @param i 将被输出的整数。
	 */
	public static void log(int i) {
		System.out.println(i);
	}
	/**
	 * 输出控制台消息。类似于{@code System.out.println(String)}。
	 * 其他类应使用此方法代替{@code System.out.println(String)}。
	 * @param str 将被输出的字符串。
	 */
	public static void log(String str) {
		System.out.println(str);
	}
	/**
	 * 输出控制台消息。类似于{@code System.out.println(Object)}。
	 * 其他类应使用此方法代替{@code System.out.println(Object)}。
	 * @param obj 将被输出的对象。
	 */
	public static void log(Object obj) {
		System.out.println(obj);
	}
	
	/**
	 * 如果可用内存少于{@link #freeMem}，则刷新可用内存信息。
	 * 如果未处于表现模式{@link #perfMode}，则调用垃圾收集。
	 * 应在复杂逻辑完成后调用此方法，以更新最高内存占用数据。
	 */
	public static void updateStats() {
		int i = (int)Runtime.getRuntime().freeMemory();
		if(i < freeMem) freeMem = i;
		if(!perfMode) System.gc();
	}
	/**
	 * 刷新所有内存信息。如果未处于表现模式{@link #perfMode}，会调用垃圾收集。
	 * 应在启动时或开始新帧时调用此方法。
	 */
	public static void flushStats() {
		if(!perfMode) System.gc();
		totMem = (int)Runtime.getRuntime().totalMemory();
		freeMem = (int)Runtime.getRuntime().freeMemory();
	}
	
	/** 退出Nirvana。（不会通知{@link GameHandler}，因此应由{@link GameHandler}调用） */
	public static void exit() {
		instance.destroyApp(false);
	}
	
	/* =====类实现===== */
	
	/** MIDlet构造方法（必须为公有的）。应由系统构造MIDlet。 */
	public MIDletNirvana() {
		super();
		if(instance == null) instance = this;
		else throw new IllegalStateException("Cannot run two threads at the same time!");
	}
	
	/** 当MIDlet启动时被调用，将控制权交给{@link GameHandler}。 */
	protected void startApp() {
		log("Starting Nirvana...");
		flushStats();
		GameHandler.handle(this);
	}
	/** 当MIDlet暂停时被调用。 */
	protected void pauseApp() {
		log("Nirvana paused.");
	}
	
	/**
	 * 当MIDlet将被销毁时被调用。可能从内部或外部被调用。
	 * @param force 如果为真，则无条件退出。（Nirvana总是无条件退出）
	 */
	protected void destroyApp(boolean force) {
		flushStats();
		log("Free memory: " + (freeMem / 1024) + "KB in " + (totMem / 1024) + "KB");
		log("Nirvana destroyed.");
		this.notifyDestroyed();
	}
	
}
