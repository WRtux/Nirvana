package nirvana.control;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.midlet.MIDlet;

import nirvana.MIDletNirvana;
import nirvana.control.ErrorHandler.ProcessBreakException;
import nirvana.data.ConfigManager;
import nirvana.data.ResourceManager;
import nirvana.display.DisplayWrapper;
import nirvana.display.scene.Scene;
import nirvana.util.Position.Coord;

/**
 * 游戏的Handler静态类，不能创建实例。
 * 在启动后加载游戏配置和资源，创建游戏线程。
 * 游戏线程定期向场景管理器{@link Scene}产生游戏tick，同时控制渲染的唤起。
 * @author Wilderness Ranger
 */
public final class GameHandler {
	
	/* =====静态变量===== */
	
	/** 被Handle的MIDlet。 */
	private static MIDlet midlet;
	
	/** Handler的显示状态。 */
	protected static boolean shown = false;
	/** 加载状态，为{@code true}时显示加载中提示。 */
	protected static boolean loading = false;
	
	/** 当前的调试信息。 */
	protected static final String[] debugInfo = new String[] {"Nirvana Beta - Debug", "", ""};
	
	protected static final Runnable thread = new Runnable() {
		
		private long synchro;
		
		public void run() {
			while(true) {
				this.synchro = System.currentTimeMillis() + MIDletNirvana.getFrameLength();
				if(GameHandler.shown) {
					if(ErrorHandler.locked) {
						ErrorHandler.handle(midlet);
						break;
					}
					GameHandler.canvas.repaint();
					KeyboardManager.service();
					try {
						Scene.service();
					} catch(ProcessBreakException ex) {
					} catch(Exception ex) {
						ErrorHandler.riseUncaught(ex);
					}
					GameHandler.canvas.serviceRepaints();
					updateStats();
					GameHandler.debugInfo[1] =
						"FMEM:" + (MIDletNirvana.getFreeMem() / 1024) + "KB"
						+ "(" + (MIDletNirvana.getFreeMem() * 100 / MIDletNirvana.getTotalMem()) + "%)"
						+ ", Time:" + (System.currentTimeMillis() - synchro + MIDletNirvana.getFrameLength()) + "ms"
						+ ", KEY:" + KeyboardManager.current;
					MIDletNirvana.flushStats();
				}
				if(System.currentTimeMillis() > this.synchro) setWarning("Refreshing cannot meet FPS");
				try {
					Thread.sleep(Math.max(this.synchro - System.currentTimeMillis(), 10));
				} catch(InterruptedException ex) {}
			}
			log("Game thread stopped.");
		}
		
	};
	
	protected static final Canvas canvas = new Canvas() {
		
		protected void showNotify() {
			KeyboardManager.clearKeys();
			GameHandler.shown = true;
		}
		
		protected void hideNotify() {
			KeyboardManager.clearKeys();
			GameHandler.shown = false;
		}
		
		protected void keyPressed(int key) {
			KeyboardManager.notify(key, true);
		}
		
		protected void keyReleased(int key) {
			KeyboardManager.notify(key, false);
		}
		
		protected void paint(Graphics g) {
			if(!GameHandler.shown) return;
			if(ErrorHandler.locked) return;
			boolean flag = false;
			try {
				flag = Scene.refresh(g);
			} catch(ProcessBreakException ex) {
			} catch(Exception ex) {
				ErrorHandler.riseUncaught(ex);
			}
			if(!flag) {
				g.setColor(DisplayWrapper.voidColorNormal);
				g.fillRect(0, 0, DisplayWrapper.scrWidth, DisplayWrapper.scrHeight);
			}
			if(GameHandler.loading) {
				int y = DisplayWrapper.scrHeight - DisplayWrapper.lineHeight;
				g.setColor(DisplayWrapper.voidColorNormal);
				g.fillRect(0, y, DisplayWrapper.scrWidth, DisplayWrapper.lineHeight);
				DisplayWrapper.setBox(new Coord(0, y), new Coord(DisplayWrapper.scrWidth, DisplayWrapper.lineHeight));
				DisplayWrapper.setColor(DisplayWrapper.textColorNormal);
				DisplayWrapper.drawString(g, "加载中…");
			}
			if(MIDletNirvana.isDebugMode()) {
				DisplayWrapper.setBox(Coord.LEFT_TOP, Coord.RIGHT_BOTTOM);
				DisplayWrapper.setColor(DisplayWrapper.textColorAux);
				for(int i = 0; i < GameHandler.debugInfo.length; i++) {
					DisplayWrapper.drawInfo(g, 2, GameHandler.debugInfo[i]);
					DisplayWrapper.nextLine();
				}
				if(GameHandler.warningLife > 0)
					if(--GameHandler.warningLife == 0) GameHandler.debugInfo[2] = "";
			}
			updateStats();
		}
		
	};
	
	/* =====静态方法===== */
	
	public static void handle(MIDlet midlet) {
		if(GameHandler.midlet == null) GameHandler.midlet = midlet;
		else throw new IllegalStateException("Already handled a MIDlet.");
		MIDletNirvana.flushStats();
		try {
			ConfigManager.loadConfig();
			ResourceManager.init();
			Scene.init();
		} catch(ProcessBreakException ex) {
		} catch(Exception ex) {
			ErrorHandler.riseUncaught(ex);
		}
		Display.getDisplay(midlet).setCurrent(canvas);
		canvas.setFullScreenMode(true);
		if(canvas.getWidth() != DisplayWrapper.scrWidth || canvas.getHeight() != DisplayWrapper.scrHeight)
			setWarning("Resolution unmatched");
		shown = true;
		new Thread(thread).start();
		updateStats();
	}
	
	public static void log(Object obj) {
		MIDletNirvana.log(obj);
	}
	
	public static void updateStats() {
		MIDletNirvana.updateStats();
	}
	
	private static int warningLife = 0;
	public static void setWarning(String text) {
		debugInfo[2] = text;
		warningLife = 25;
	}
	
	public static void notifyLoadStart() {
		loading = true;
		canvas.serviceRepaints();
	}
	public static void notifyLoadEnd() {
		loading = false;
	}
	
	/* =====类实现===== */
	
	/** @deprecated */
	private GameHandler() {}
	
}
