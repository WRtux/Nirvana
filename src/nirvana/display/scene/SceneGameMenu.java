package nirvana.display.scene;

import nirvana.display.DisplayWrapper;
import nirvana.display.window.Window;
import nirvana.display.window.Window.ListElement;
import nirvana.display.window.WindowChoice;
import nirvana.display.window.WindowChoice.WindowChoiceVertical;
import nirvana.util.Anchor;
import nirvana.util.Position.Coord;

public class SceneGameMenu extends Scene {
	
	private WindowChoice windowMenu;
	private WindowChoice windowCharas;
	
	protected SceneGameMenu() {
		super();
		this.transparent = true;
		this.windowMenu = new WindowChoiceVertical(
			new Coord(60, 6 * DisplayWrapper.lineHeight).add(Window.SHRINK_DEFAULT),
			ListElement.constrcutElementArray(new String[] {"状态", "物品", "退出"}),
			DisplayWrapper.itemHeight, false
		);
		this.windowMenu.setResultItem(WindowChoice.NOSELECT, this.windowMenu.getElementNum() - 1);
		this.windowMenu.visible = true;
		this.windowCharas = new WindowChoiceVertical(
			new Coord(120, 180).add(Window.SHRINK_DEFAULT),
			new ListElement[] {},
			48 + 4, false
		);
		this.windowCharas.visible = true;
	}
	
	protected void paint() {
		this.drawWindow(Coord.LEFT_TOP, this.windowMenu, Anchor.LEFT_TOP);
		this.drawWindow(new Coord(this.windowMenu.size.x, 0), this.windowCharas, Anchor.LEFT_TOP);
	}
	
	protected void process() {
		Window.service(this.windowMenu);
		if(this.windowMenu.hasResult()) switch(this.windowMenu.getResult()) {
		case WindowChoice.RESULT_NO:
			Scene.pop();
			Scene.pop();
			break;
		case WindowChoice.RESULT_EXIT:
			Scene.pop();
			break;
		}
	}

}
