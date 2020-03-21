package nirvana.display.scene;

import nirvana.control.KeyboardManager;
import nirvana.data.ResourceManager;
import nirvana.game.map.MapScene;
import nirvana.util.Anchor;
import nirvana.util.Position.Coord;
import nirvana.util.Position.CoordVarible;

//TODO
public final class SceneGame extends Scene {
	
	private MapScene sce;
	
	protected SceneGame() {
		super();
		this.sce = ResourceManager.getMapScene((short)0);
		this.sce.setCamera(new CoordVarible(Coord.MIDDLE_MIDDLE));
	}
	
	protected void paint() {
		this.drawMap(Coord.LEFT_TOP, this.sce, Coord.RIGHT_BOTTOM, Anchor.LEFT_TOP);
	}
	
	protected void process() {
		CoordVarible camera = this.sce.getCamera();
		switch(KeyboardManager.current) {
		case KeyboardManager.KEY_UP:
			this.sce.setCamera(camera.subtract(0, 2));
			this.sce.optimizeCamera(Coord.RIGHT_BOTTOM);
			break;
		case KeyboardManager.KEY_DOWN:
			this.sce.setCamera(camera.add(0, 2));
			this.sce.optimizeCamera(Coord.RIGHT_BOTTOM);
			break;
		case KeyboardManager.KEY_LEFT:
			this.sce.setCamera(camera.subtract(2, 0));
			this.sce.optimizeCamera(Coord.RIGHT_BOTTOM);
			break;
		case KeyboardManager.KEY_RIGHT:
			this.sce.setCamera(camera.add(2, 0));
			this.sce.optimizeCamera(Coord.RIGHT_BOTTOM);
			break;
		case KeyboardManager.KEY_CANCEL:
			Scene.enter(new SceneGameMenu());
			break;
		}
	}
	
}
