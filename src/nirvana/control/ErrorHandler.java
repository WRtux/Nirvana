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
	
	private static ErrorHandler instance;
	
	public static boolean locked = false;
	
	protected static String errInfo = "";
	protected static String errField = "\\c\\1No Errors.\\0";
	
	protected boolean shown = false;
	
	public static final class ProcessBreakException extends RuntimeException {
		
		public final Class catcher;
		public final Exception exception;
		
		public ProcessBreakException(Class catcher, Exception exception) {
			super();
			this.catcher = catcher;
			this.exception = exception;
			throw this;
		}
		public ProcessBreakException(Exception exception) {
			super();
			this.catcher = null;
			this.exception = exception;
			throw this;
		}
		
	}
	
	private ErrorHandler() {
		super();
	}
	
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
	
	public static void riseUncaught(Exception exception) {
		if(exception == null) return;
		locked = true;
		System.gc();
		if(MIDletNirvana.isDebugMode()) {
			MIDletNirvana.log("Uncaught exception! Detail information:");
			exception.printStackTrace();
		} MIDletNirvana.log("Nirvana crashed because of an uncaught exception.");
		errInfo = "Error was not caught.\n\\1" + exception.toString() + "\\0";
		makeField();
	}
	
	public static void riseError(Class catcher, Exception exception) {
		if(catcher == null) riseUncaught(exception);
		if(exception == null) return;
		locked = true;
		System.gc();
		String classname = catcher.getName();
		if(MIDletNirvana.isDebugMode()) {
			MIDletNirvana.log("Nirvana crashed! Detail information:");
			MIDletNirvana.log("Error caught by: " + classname);
			exception.printStackTrace();
		} else MIDletNirvana.log("Nirvana crashed!");
		errInfo = "Error caught by: " + classname + "\n\\1" + exception.toString() + "\\0";
		makeField();
		throw new ProcessBreakException(catcher, exception);
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
