package nirvana.display.scene;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

import nirvana.MIDletNirvana;
import nirvana.data.ResourceManager;
import nirvana.display.DisplayWrapper;
import nirvana.display.window.Window;
import nirvana.display.window.Window.ListElement;
import nirvana.display.window.WindowChoice;
import nirvana.display.window.WindowChoice.WindowChoiceVertical;
import nirvana.display.window.WindowTextField;
import nirvana.game.GameVocab;
import nirvana.game.item.ItemStack;
import nirvana.util.Anchor;
import nirvana.util.IResultable;
import nirvana.util.Position.Coord;

public final class SceneTitle extends Scene {
	
	protected static final int RESULT_NGAME = 0;
	protected static final int RESULT_LOAD = 1;
	protected static final int RESULT_SOUND = 2;
	protected static final int RESULT_HELP = 3;
	protected static final int RESULT_ABOUT = 4;
	protected static final int RESULT_EXIT = 5;
	protected static final int RESULT_DEBUG = 6;
	
	private Image imgBg;
	private Image imgBgR;
	private static float bgRollSpeed = 0.4f;
	private float bgPos;
	private Image imgTitle;
	
	private WindowChoice windowMenu;
	private WindowChoice windowSaves;
	private WindowChoice windowSound;
	private WindowChoice windowDebug;
	private int state;
	
	protected SceneTitle() {
		
		super();
		
		this.imgBg = ResourceManager.getSymbolicImage(ResourceManager.IMG_TITLEBG);
		this.imgBgR = Image.createImage(
			this.imgBg,
			0, 0, this.imgBg.getWidth(), this.imgBg.getHeight(),
			Sprite.TRANS_ROT180
		);
		this.bgPos = 1;
		this.imgTitle = ResourceManager.getSymbolicImage(ResourceManager.IMG_GAMETITLE);
		
		this.windowMenu = new WindowChoiceVertical(
			new Coord(80, 4 * DisplayWrapper.itemHeight).add(Window.SHRINK_DEFAULT),
			ListElement.constrcutElementArray(GameVocab.titleNames),
			DisplayWrapper.itemHeight, true
		);
		if(MIDletNirvana.isDebugMode()) this.windowMenu.addElement(new ListElement("\\c\\1调试选项"));
		this.windowMenu.setResultItem(WindowChoice.NOSELECT, RESULT_EXIT);
		this.windowMenu.visible = true;
		
		this.windowSaves = new WindowChoiceVertical(
			Coord.MIDDLE_MIDDLE,
			new ListElement[] {
				new ListElement(new ItemStack((short)0)),
				new ListElement(new ItemStack((short)0, 10086, 0)),
				new ListElement("清除存档")
			}, DisplayWrapper.itemHeight, false
		);
		this.windowSaves.visible = true;
		
		this.windowSound = new WindowChoiceVertical(
			new Coord(60, 4 * DisplayWrapper.itemHeight).add(Window.SHRINK_DEFAULT),
			ListElement.constrcutElementArray(new String[] {
				"\\c关闭", "\\c较小", "\\c中等", "\\c较大"
			}), DisplayWrapper.itemHeight, false
		);
		this.windowSound.visible = true;
		
		this.windowDebug = new WindowChoiceVertical(
			new Coord(80, 4 * DisplayWrapper.itemHeight).add(Window.SHRINK_DEFAULT),
			ListElement.constrcutElementArray(new String[] {
				"调试信息", "性能信息", "Exception", "\\1关闭调试"
			}), DisplayWrapper.itemHeight, false
		);
		this.windowDebug.visible = true;
		
		this.state = 0;
		
	}
	
	protected void paint() {
		//TODO
		this.g.setColor(0xFFFFFF);
		this.g.fillRect(0, 0, DisplayWrapper.scrWidth, DisplayWrapper.scrHeight);
		int w = this.imgBg.getWidth();
		for(float x = this.bgPos - w; x < DisplayWrapper.scrWidth; x += w) {
			this.drawImage(new Coord((int)x, 0), this.imgBg, Anchor.LEFT_TOP);
			this.drawImage(new Coord((int)x, DisplayWrapper.scrHeight), this.imgBgR, Anchor.LEFT_BOTTOM);
		}
		if((this.bgPos += bgRollSpeed) > this.imgBg.getWidth()) this.bgPos = 1;
		this.drawImage(Coord.MIDDLE_TOP.add(0, 40), this.imgTitle, Anchor.CENTER_TOP);
		switch(this.state) {
		case 0:
			this.drawWindow(Coord.MIDDLE_BOTTOM.subtract(0, 50), this.windowMenu, Anchor.CENTER_BOTTOM);
			break;
		case RESULT_LOAD:
			this.drawWindow(Coord.MIDDLE_BOTTOM.subtract(0, 50), this.windowMenu, Anchor.CENTER_BOTTOM);
			this.drawWindow(Coord.MIDDLE_MIDDLE, this.windowSaves, Anchor.CENTER_CENTER);
			break;
		case RESULT_SOUND:
			this.drawWindow(Coord.MIDDLE_BOTTOM.subtract(0, 50), this.windowSound, Anchor.CENTER_BOTTOM);
			break;
		case RESULT_DEBUG:
			this.drawWindow(Coord.MIDDLE_BOTTOM.subtract(0, 50), this.windowDebug, Anchor.CENTER_BOTTOM);
			break;
		}
	}
	
	protected void process() {
		switch(this.state) {
		case 0:
			processMenu();
			return;
		case RESULT_LOAD:
			processSaves();
			return;
		case RESULT_SOUND:
			processSound();
			return;
		case RESULT_DEBUG:
			processDebug();
			return;
		}
	}
	
	private void processMenu() {
		Window.service(this.windowMenu);
		if(this.windowMenu.hasResult()) switch(this.windowMenu.getResult()) {
		case RESULT_NGAME:
			Scene.enter(new SceneGame());
			break;
		case RESULT_LOAD:
			this.state = RESULT_LOAD;
			break;
		case RESULT_SOUND:
			this.state = RESULT_SOUND;
			break;
		case RESULT_HELP:
			Scene.enter(new SceneSingleWindow(new WindowTextField(Coord.RIGHT_BOTTOM, "\\c帮助\n\\l")));
			break;
		case RESULT_ABOUT:
			Scene.enter(new SceneSingleWindow(new WindowTextField(
				Coord.RIGHT_BOTTOM, "\\c\\1关于\\0\n\\lNirvana Game Engine\nBy Wilderness Ranger"
			)));
			break;
		case IResultable.RESULT_NO:
			Scene.pop();
			break;
		case RESULT_DEBUG:
			this.state = RESULT_DEBUG;
			break;
		}
	}
	
	private void processSaves() {
		Window.service(this.windowSaves);
		if(this.windowSaves.hasResult()) switch(this.windowSaves.getResult()) {
		case 0:
			//TODO ...
			this.state = 0;
			break;
		case IResultable.RESULT_EXIT:
			this.state = 0;
			break;
		}
	}
	
	private void processSound() {
		Window.service(this.windowSound);
		if(this.windowSound.hasResult()) switch(this.windowSound.getResult()) {
		case 0:
		case 1:
		case 2:
		case 3:
			//TODO ...
			this.state = 0;
			break;
		case IResultable.RESULT_EXIT:
			this.state = 0;
			break;
		}
	}
	
	private void processDebug() {
		Window.service(this.windowDebug);
		if(this.windowDebug.hasResult()) switch(this.windowDebug.getResult()) {
		case 0:
		case 1:
		case 2:
		case 3:
			//TODO ...
			break;
		case IResultable.RESULT_EXIT:
			this.state = 0;
			break;
		}
	}
	
}
