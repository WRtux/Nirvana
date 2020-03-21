package nirvana.display.scene;

import java.util.Stack;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

import nirvana.MIDletNirvana;
import nirvana.control.ErrorHandler;
import nirvana.control.GameHandler;
import nirvana.control.KeyboardManager;
import nirvana.display.DisplayWrapper;
import nirvana.display.window.Window;
import nirvana.game.map.MapScene;
import nirvana.util.Anchor;
import nirvana.util.Position.Coord;

public abstract class Scene {
	
	private static Stack sceStack;
	private static Scene sceNow;
	
	protected Graphics g = null;
	public boolean transparent = false;
	protected boolean ready = false;
	
	protected int ticks = 0;
	protected int frames = 0;
	
	public static final void init() {
		sceStack = new Stack();
		sceStack.push(sceNow = new SceneLoad());
		GameHandler.updateStats();
	}
	
	public static final void enter(Scene sce) {
		if(sce == null) ErrorHandler.riseError(Scene.class, new NullPointerException());
		KeyboardManager.clearKeys();
		sceStack.push(sceNow = sce);
		service();
	}
	public static final void gto(Scene sce) {
		if(sce == null) ErrorHandler.riseError(Scene.class, new NullPointerException());
		KeyboardManager.clearKeys();
		sceStack.setElementAt(sceNow = sce, sceStack.size() - 1);
		service();
	}
	
	//TODO
	public static final void pop() {
		KeyboardManager.clearKeys();
		sceStack.pop();
		if(sceStack.size() == 0) MIDletNirvana.exit();
		else sceNow = (Scene)sceStack.lastElement();
	}
	
	public static final boolean service() {
		if(sceStack.size() == 0) return false;
		sceNow.process();
		sceNow.ticks++;
		GameHandler.updateStats();
		return true;
	}
	
	public static final boolean refresh(Graphics g) {
		if(sceStack.size() == 0 || !sceNow.ready) return false;
		sceNow.g = g;
		sceNow.g.setClip(0, 0, DisplayWrapper.scrWidth, DisplayWrapper.scrHeight);
		sceNow.g.setColor(DisplayWrapper.voidColorNormal);
		sceNow.g.fillRect(0, 0, DisplayWrapper.scrWidth, DisplayWrapper.scrHeight);
		if(sceNow.transparent && sceStack.size() > 1) {
			Scene parent = (Scene)sceStack.elementAt(sceStack.size() - 2);
			if(parent.ready) {
				parent.g = sceNow.g;
				parent.paint();
				parent.frames++;
				parent.g = null;
			}
		}
		sceNow.paint();
		sceNow.frames++;
		sceNow.g = null;
		GameHandler.updateStats();
		return true;
	}
	
	public Scene() {
		this.ready = true;
	}
	
	protected final void clear() {
		this.g.setColor(DisplayWrapper.voidColorNormal);
		this.g.fillRect(0, 0, DisplayWrapper.scrWidth, DisplayWrapper.scrHeight);
	}
	
	protected final void drawImage(Coord pos, Image img, int anchor) {
		this.g.drawImage(img, pos.x, pos.y, anchor);
	}
	
	protected final void drawSprite(Sprite s) {
		s.paint(this.g);
		s.nextFrame();
	}
	
	protected final void drawMap(Coord pos, MapScene sce, Coord size, int anchor) {
		//TODO
		pos = nirvana.util.Anchor.translateOutOf(pos, size, anchor);
		MapScene.renderTileImage(this.g, pos, sce, size);
		MapScene.renderEntities(this.g, pos, sce, size);
	}
	
	protected final void drawWindow(Coord pos, Window window, int anchor) {
		Window.refresh(this.g, Anchor.translateOutOf(pos, window.size, anchor), window);
	}
	
	protected abstract void paint();
	protected abstract void process();
	
}
