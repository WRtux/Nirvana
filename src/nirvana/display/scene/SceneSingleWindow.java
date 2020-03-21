package nirvana.display.scene;

import nirvana.control.ErrorHandler;
import nirvana.display.window.Window;
import nirvana.util.Anchor;
import nirvana.util.IResultable;
import nirvana.util.Position.Coord;

public final class SceneSingleWindow extends Scene implements IResultable {
	
	private final Window window;
	
	private int result = IResultable.RESULT_NULL;
	
	public SceneSingleWindow(Window window) {
		super();
		if(!(window instanceof IResultable))
			ErrorHandler.riseError(getClass(), new IllegalArgumentException("Window without result."));
		this.window = window;
		window.visible = true;
	}
	
	public boolean hasResult() {
		return this.result !=IResultable.RESULT_NULL;
	}
	public int getResult() {
		return this.result;
	}
	
	protected void paint() {
		this.drawWindow(Coord.MIDDLE_MIDDLE, this.window, Anchor.CENTER_CENTER);
	}
	
	protected void process() {
		Window.service(this.window);
		IResultable w = (IResultable)this.window;
		if(w.hasResult()) {
			this.result = w.getResult();
			Scene.pop();
		}
	}
	
}
