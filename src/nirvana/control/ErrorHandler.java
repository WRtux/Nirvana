package nirvana.control;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.midlet.MIDlet;

import nirvana.MIDletNirvana;
import nirvana.display.DisplayWrapper;
import nirvana.util.Position.Coord;

/**
 * 游戏的崩溃提示类，只能通过调用{@link #handle(MIDlet)}创建一个实例。
 * 在收到未被catch的异常后，弹出崩溃信息。
 * @author Wilderness Ranger
 */
public final class ErrorHandler extends Canvas implements Runnable {
	
	/* =====静态变量===== */
	
	/** Handler类的示例。 */
	private static ErrorHandler instance;
	
	public static boolean locked = false;
	
	protected static String errInfo = "";
	protected static String errField = "\\c\\1No Errors.\\0";
	
	protected boolean shown = false;
	
	/**
	 * 发生崩溃时，抛出的异常类。
	 * 为防止被{@code catch(Exception)}块处理，此类实际继承的是{@link Error}。
	 * @author Wilderness Ranger
	 */
	public static final class ProcessBreakException extends Error {
		
		public final Class catcher;
		public final Exception exception;
		
		public ProcessBreakException(Class catcher, Exception ex) {
			super();
			this.catcher = catcher;
			this.exception = ex;
			throw this;
		}
		public ProcessBreakException(Exception ex) {
			super();
			this.catcher = null;
			this.exception = ex;
			throw this;
		}
		
	}
	
	/* =====静态方法===== */
	
	public static ErrorHandler handle(MIDlet midlet) {
		if(instance != null) return instance; 
		instance = new ErrorHandler();
		Display.getDisplay(midlet).setCurrent(instance);
		instance.setFullScreenMode(true);
		instance.shown = true;
		new Thread(instance).start();
		System.gc();
		return instance;
	}
	
	private static void makeField() {
		errField = "\\c\\1Nirvana Crashed!\\0\n\\lAn unsolvable error has occurred, which forced Nirvana to stop.\n\n"
			+ ErrorHandler.errInfo
			+ "\n\nMore information will be in console if you are in \\1debug mode\\0.";
	}
	
	public static void riseUncaught(Exception ex) {
		if(ex == null) return;
		locked = true;
		System.gc();
		if(MIDletNirvana.isDebugMode()) {
			MIDletNirvana.log("Uncaught exception! Detail information:");
			ex.printStackTrace();
		} MIDletNirvana.log("Nirvana crashed because of an uncaught exception.");
		errInfo = "Error was not caught.\n\\1" + ex.toString() + "\\0";
		makeField();
	}
	
	public static void riseError(Class catcher, Exception ex) {
		if(catcher == null) riseUncaught(ex);
		if(ex == null) return;
		locked = true;
		System.gc();
		String classname = catcher.getName();
		if(MIDletNirvana.isDebugMode()) {
			MIDletNirvana.log("Nirvana crashed! Detail information:");
			MIDletNirvana.log("Error caught by: " + classname);
			ex.printStackTrace();
		} else MIDletNirvana.log("Nirvana crashed!");
		errInfo = "Error caught by: " + classname + "\n\\1" + ex.toString() + "\\0";
		makeField();
		throw new ProcessBreakException(catcher, ex);
	}
	
	/* =====类实现===== */
	
	/** Handler构造方法。在调用{@link #handle(MIDlet)}时自动构造。 */
	private ErrorHandler() {
		super();
	}
	
	public void run() {
		while(true) {
			if(this.shown) {
				this.repaint();
				KeyboardManager.service();
				if(KeyboardManager.trigger == KeyboardManager.KEY_CANCEL) MIDletNirvana.exit();
				this.serviceRepaints();
				System.gc();
			}
			try {
				Thread.sleep(50);
			} catch(InterruptedException ex) {}
		}
	}
	
	protected void showNotify() {
		KeyboardManager.clearKeys();
		this.shown = true;
	}
	protected void hideNotify() {
		KeyboardManager.clearKeys();
		this.shown = false;
	}
	protected void keyPressed(int key) {
		KeyboardManager.notify(key, true);
	}
	protected void keyReleased(int key) {
		KeyboardManager.notify(key, false);
	}
	
	protected void paint(Graphics g) {
		if(!this.shown) return;
		g.setColor(DisplayWrapper.bgColorNormal);
		g.fillRect(0, 0, DisplayWrapper.scrWidth, DisplayWrapper.scrHeight);
		DisplayWrapper.setBox(new Coord(4, 4), Coord.RIGHT_BOTTOM.subtract(8, 8));
		DisplayWrapper.setColor(DisplayWrapper.textColorNormal, DisplayWrapper.textColorEmp);
		DisplayWrapper.drawField(g, errField, 0);
		System.gc();
	}
	
}
