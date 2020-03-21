package nirvana.display.scene;

import javax.microedition.lcdui.Image;

import nirvana.data.ResourceManager;
import nirvana.display.DisplayWrapper;
import nirvana.display.window.Window;
import nirvana.display.window.Window.ListElement;
import nirvana.display.window.WindowChoice;
import nirvana.display.window.WindowChoice.WindowChoiceVertical;
import nirvana.util.Anchor;
import nirvana.util.IResultable;
import nirvana.util.Position.Coord;

public final class SceneLoad extends Scene {
	
	private boolean state;
	
	private WindowChoice windowSound;
	private Image imgTmp;
	
	protected SceneLoad() {
		super();
		this.state = false;
		this.imgTmp = ResourceManager.getSymbolicImage(ResourceManager.IMG_NIRVANA);
		this.windowSound = new WindowChoiceVertical(
			new Coord(80, 2 * DisplayWrapper.itemHeight).add(Window.SHRINK_DEFAULT),
			ListElement.constrcutElementArray(new String[] {
				"打开声音", "关闭声音"
			}), DisplayWrapper.itemHeight, false
		);
		this.windowSound.setResultItem(0, 1);
		this.windowSound.visible = true;
	}
	
	protected void paint() {
		if(!this.state) {
			this.g.setColor(0xFFFFFF);
			this.g.fillRect(0, 0, DisplayWrapper.scrWidth, DisplayWrapper.scrHeight);
			this.drawImage(Coord.MIDDLE_MIDDLE, this.imgTmp, Anchor.CENTER_CENTER);
		} else this.drawWindow(Coord.MIDDLE_MIDDLE, this.windowSound, Anchor.CENTER_CENTER);
	}
	
	protected void process() {
		if(this.ticks == 50)
			this.imgTmp = ResourceManager.getSymbolicImage(ResourceManager.IMG_GAMELOGO);
		if(this.ticks >= 100) {
			if(this.ticks == 100) this.state = true;
			Window.service(this.windowSound);
			if(this.windowSound.hasResult()) switch(this.windowSound.getResult()) {
			case IResultable.RESULT_YES:
			case IResultable.RESULT_NO:
				//TODO ...
				Scene.gto(new SceneTitle());
				break;
			}
		}
	}
	
}
