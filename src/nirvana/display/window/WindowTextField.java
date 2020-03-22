package nirvana.display.window;

import nirvana.control.KeyboardManager;
import nirvana.display.CoordSimple;
import nirvana.display.DisplayWrapper;
import nirvana.util.Anchor;
import nirvana.util.IResultable;
import nirvana.util.Position.Coord;

public final class WindowTextField extends Window implements IResultable {
	
	public final String field;
	
	protected int scrPos;
	
	private int result = IResultable.RESULT_NULL;
	
	public WindowTextField(Coord size, String field) {
		super(size, false);
		this.field = field;
		DisplayWrapper.pretranslate(field, this.inner.width, true);
		this.scrPos = 0;
	}
	public WindowTextField(Coord size) {
		this(size, "");
	}
	
	public boolean hasResult() {
		return this.result != IResultable.RESULT_NULL;
	}
	public int getResult() {
		return this.result;
	}
	
	protected boolean process() {
		boolean dirty = false;
		switch(KeyboardManager.trigger) {
		case KeyboardManager.KEY_UP:
			if(this.scrPos > 0) this.scrPos--;
			dirty = true;
			break;
		case KeyboardManager.KEY_DOWN:
			if(this.scrPos < DisplayWrapper.getLines(this.field, this.inner.width) - 1)
				this.scrPos++;
			dirty = true;
			break;
		case KeyboardManager.KEY_FIRE:
			this.result = IResultable.RESULT_YES;
			break;
		case KeyboardManager.KEY_CANCEL:
			this.result = IResultable.RESULT_EXIT;
			break;
		}
		return dirty;
	}
	
	protected void paint() {
		this.drawField(CoordSimple.coordHLVL, this.field, this.scrPos, new CoordSimple(this.inner.width, this.inner.height), Anchor.LEFT_TOP);
	}
	
}
