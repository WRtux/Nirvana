package nirvana.display.window;

import java.util.Vector;

import nirvana.control.KeyboardManager;
import nirvana.display.CoordSimple;
import nirvana.display.DisplayWrapper;
import nirvana.util.Anchor;
import nirvana.util.IResultable;
import nirvana.util.Position.Coord;

public abstract class WindowChoice extends Window implements IElementList, IResultable {
	
	public static class WindowChoiceVertical extends WindowChoice implements IScrollable {
		
		protected final int eleHeight;
		
		protected final boolean scrollable;
		
		public WindowChoiceVertical(Coord size, int eleNum, int eleHeight, boolean scrollable) {
			super(size, eleNum);
			this.eleHeight = eleHeight;
			this.scrollable = scrollable;
		}
		public WindowChoiceVertical(Coord size, int eleNum, boolean scrollable) {
			this(size, eleNum, DisplayWrapper.itemHeight, scrollable);
		}
		
		public WindowChoiceVertical(Coord size, ListElement[] elements, int eleHeight, boolean scrollable) {
			super(size, elements);
			this.eleHeight = eleHeight;
			this.scrollable = scrollable;
		}
		
		public final ListElement[] getElementsShown() {
			int num = (this.inner.height - 1) / this.eleHeight + 1;
			ListElement[] arr = new ListElement[num];
			for(int i = 0; i < num; i++)
				arr[i] = (ListElement)this.elements.elementAt(this.scrPos + i);
			return arr;
		}
		
		public void updateScreenPos() {
			if(this.selectPos != NOSELECT) {
				if(this.selectPos < this.scrPos) this.scrPos = this.selectPos;
				int fulls = this.inner.height / this.eleHeight;
				if(this.selectPos >= this.scrPos + fulls)
					this.scrPos = this.selectPos - fulls + 1;
			}
		}
		
		public boolean hasScroll() {
			return this.scrollable;
		}
		
		public int getScrollMax() {
			return this.elements.size();
		}
		public int getScrollPos() {
			return this.scrPos;
		}
		public int getScrollSize() {
			return this.inner.height / this.eleHeight;
		}
		
		protected void paint() {
			if(this.selectPos != NOSELECT) {
				this.g.setColor(DisplayWrapper.bgColorEmp);
				this.g.fillRect(
					0, (this.selectPos - this.scrPos) * this.eleHeight,
					this.inner.width, this.eleHeight
				);
			}
			for(int i = 0, size = this.elements.size(); this.scrPos + i < size; i++) {
				if(i * this.eleHeight >= this.inner.width) break;
				this.drawElement(
					new CoordSimple(0, i * this.eleHeight),
					(ListElement)this.elements.elementAt(this.scrPos + i),
					new CoordSimple(this.inner.width, this.eleHeight), Anchor.LEFT_TOP
				);
			}
		}
		
		protected boolean process() {
			boolean dirty = false;
			switch(KeyboardManager.trigger) {
			case KeyboardManager.KEY_UP:
				this.selectPos--;
				if(this.selectPos < 0) this.selectPos = this.elements.size() - 1;
				this.updateScreenPos();
				dirty = true;
				break;
			case KeyboardManager.KEY_DOWN:
				this.selectPos++;
				if(this.selectPos >= this.elements.size()) this.selectPos = 0;
				this.updateScreenPos();
				dirty = true;
				break;
			case KeyboardManager.KEY_FIRE:
				if(this.selectPos == this.iyes) this.result = IResultable.RESULT_YES;
				else if(this.selectPos == this.ino) this.result = IResultable.RESULT_NO;
				else this.result = this.selectPos;
				break;
			case KeyboardManager.KEY_CANCEL:
				this.result = IResultable.RESULT_EXIT;
				break;
			}
			return dirty;
		}
		
	}
	
	public static class WindowChoiceHorizontal extends WindowChoice {
		
		protected final int eleWidth;
		
		public WindowChoiceHorizontal(Coord size, int eleNum, int eleWidth) {
			super(size, eleNum);
			this.eleWidth = eleWidth;
		}
		public WindowChoiceHorizontal(Coord size, int eleNum) {
			this(size, eleNum, 2 * DisplayWrapper.itemHeight);
		}
		
		public WindowChoiceHorizontal(Coord size, ListElement[] elements, int eleWidth) {
			super(size, elements);
			this.eleWidth = eleWidth;
		}
		
		public final ListElement[] getElementsShown() {
			int num = (this.inner.width - 1) / this.eleWidth + 1;
			ListElement[] arr = new ListElement[num];
			for(int i = 0; i < num; i++)
				arr[i] = (ListElement)this.elements.elementAt(this.scrPos + i);
			return arr;
		}
		
		public void updateScreenPos() {
			if(this.selectPos != NOSELECT) {
				if(this.selectPos < this.scrPos) this.scrPos = this.selectPos;
				int fulls = this.inner.width / this.eleWidth;
				if(this.selectPos >= this.scrPos + fulls)
					this.scrPos = this.selectPos - fulls + 1;
			}
		}
		
		protected void paint() {
			if(this.scrPos != NOSELECT) {
				this.g.setColor(DisplayWrapper.bgColorEmp);
				this.g.fillRect(
					(this.selectPos - this.scrPos) * this.eleWidth, 0,
					this.eleWidth, this.inner.height
				);
			}
			for(int i = 0, size = this.elements.size(); this.scrPos + i < size; i++) {
				if(i * this.eleWidth >= this.inner.width) break;
				this.drawElement(
					new CoordSimple(i * this.eleWidth, 0),
					(ListElement)this.elements.elementAt(this.scrPos + i),
					new CoordSimple(this.eleWidth, this.inner.height), Anchor.LEFT_TOP
				);
			}
		}
		
		protected boolean process() {
			boolean dirty = false;
			switch(KeyboardManager.trigger) {
			case KeyboardManager.KEY_LEFT:
				this.selectPos--;
				if(this.selectPos < 0) this.selectPos = this.elements.size() - 1;
				this.updateScreenPos();
				dirty = true;
				break;
			case KeyboardManager.KEY_RIGHT:
				this.selectPos++;
				if(this.selectPos >= this.elements.size()) this.selectPos = 0;
				this.updateScreenPos();
				dirty = true;
				break;
			case KeyboardManager.KEY_FIRE:
				if(this.selectPos == this.iyes) this.result = IResultable.RESULT_YES;
				else if(this.selectPos == this.ino) this.result = IResultable.RESULT_NO;
				else this.result = this.selectPos;
				break;
			case KeyboardManager.KEY_CANCEL:
				this.result = IResultable.RESULT_EXIT;
				break;
			}
			return dirty;
		}
		
	}
	
	//TODO complete
	/* public static class WindowChoiceComplex extends WindowChoice implements IScrollable {
		
		protected final int cols;
		protected final int eleHeight;
		
		protected final boolean scrollable;
		
		public WindowChoiceComplex(Coord size, int cols, int eleNum, int eleHeight, boolean scrollable) {
			super(size, eleNum);
			this.cols = cols;
			this.eleHeight = eleHeight;
			this.scrollable = scrollable;
		}
		public WindowChoiceComplex(Coord size, int cols, int eleNum, boolean scrollable) {
			this(size, cols, eleNum, DisplayWrapper.itemHeight, scrollable);
		}
		
		public WindowChoiceComplex(Coord size, int cols, ListElement[] elements, int eleHeight, boolean scrollable) {
			super(size, elements);
			this.cols = cols;
			this.eleHeight = eleHeight;
			this.scrollable = scrollable;
		}
		
		public final ListElement[] getElementsShown() {
			int start = this.scrPos * this.cols,
				num = ((this.innerSize.y - 1) / this.eleHeight + 1) * this.cols;
			ListElement[] arr = new ListElement[num];
			for(int i = 0; i < num; i++)
				arr[i] = (ListElement)this.elements.elementAt(start + i);
			return arr;
		}
		
		public void updateScreenPos() {
			if(this.selectPos != NOSELECT) {
				int start = this.selectPos  * this.cols;
				if(this.selectPos < start) this.scrPos = this.selectPos / this.cols;
				int fulls = this.innerSize.y / this.eleHeight * this.cols;
				if(this.selectPos >= start + fulls)
					this.scrPos = (this.selectPos - fulls) / this.cols + 1;
			}
		}
		
		public boolean hasScroll() {
			return this.scrollable;
		}
		
		public int getScrollMax() {
			return (this.elements.size() - 1) / this.cols + 1;
		}
		public int getScrollPos() {
			return this.scrPos / this.cols;
		}
		public int getScrollSize() {
			return this.innerSize.y / this.eleHeight;
		}
		
	} */
	
	public static final int NOSELECT = -1;
	
	protected final Vector elements;
	
	protected int scrPos;
	
	protected int iyes = NOSELECT;
	protected int ino = NOSELECT;
	
	protected int selectPos = NOSELECT;
	protected int result = NOSELECT;
	
	public WindowChoice(Coord size, int eleNum) {
		super(size, false);
		this.elements = new Vector(eleNum);
		this.scrPos = 0;
		this.selectPos = 0;
	}
	
	public WindowChoice(Coord size, ListElement[] elements) {
		this(size, elements.length);
		for(int i = 0; i < elements.length; i++)
			if(elements[i] != null) this.elements.addElement(elements[i]);
			else throw new NullPointerException();
	}
	
	public final int getElementNum() {
		return this.elements.size();
	}
	
	public final ListElement elementAt(int index) {
		return (ListElement)this.elements.elementAt(index);
	}
	public abstract ListElement[] getElementsShown();
	
	public final void removeAllElements() {
		this.elements.removeAllElements();
	}
	
	public final void addElement(ListElement element) {
		this.elements.addElement(element);
	}
	
	public final void setResultItem(int iyes, int ino) {
		this.iyes = iyes;
		this.ino = ino;
	}
	
	public abstract void updateScreenPos();
	
	public final int getSelectPos() {
		return this.selectPos;
	}
	public final void setSelectPos(int pos) {
		if(pos < 0) this.selectPos = 0;
		else if(pos >= this.elements.size()) this.selectPos = this.elements.size() - 1;
		else this.selectPos = pos;
	}
	
	public final boolean hasResult() {
		return this.result != NOSELECT;
	}
	public final int getResult() {
		int result = this.result;
		if(result == NOSELECT) result = IResultable.RESULT_NULL;
		this.result = NOSELECT;
		return result;
	}
	
	protected abstract void paint();
	protected abstract boolean process();
	
}
