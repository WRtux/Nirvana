package nirvana.display.window;

import java.util.Vector;

import nirvana.control.KeyboardManager;
import nirvana.display.CoordSimple;
import nirvana.display.DisplayWrapper;
import nirvana.util.Anchor;
import nirvana.util.Position.Coord;

public abstract class WindowList extends Window implements IElementList {
	
	public static class WindowListVertical extends WindowList implements IScrollable {
		
		protected final int eleHeight;
		
		protected final boolean scrollable;
		
		public WindowListVertical(Coord size, int eleNum, int eleHeight, boolean scrollable) {
			super(size, eleNum);
			this.eleHeight = eleHeight;
			this.scrollable = scrollable;
		}
		public WindowListVertical(Coord size, int eleNum, boolean scrollable) {
			this(size, eleNum, DisplayWrapper.itemHeight, scrollable);
		}
		
		public WindowListVertical(Coord size, ListElement[] elements, int eleHeight, boolean scrollable) {
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
			for(int i = 0, size = this.elements.size(); this.scrPos + i < size; i++) {
				if(i * this.eleHeight >= this.inner.height) break;
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
				if(this.scrPos > 0) {
					this.scrPos--;
					dirty = true;
				}
				break;
			case KeyboardManager.KEY_DOWN:
				if(this.scrPos + this.inner.height / this.eleHeight < this.elements.size()) {
					this.scrPos++;
					dirty = true;
				}
				break;
			}
			return dirty;
		}
		
	}
	public class WindowListHorizontal extends WindowList {
		
		protected final int eleWidth;
		
		public WindowListHorizontal(Coord size, int eleNum, int eleWidth) {
			super(size, eleNum);
			this.eleWidth = eleWidth;
		}
		public WindowListHorizontal(Coord size, int eleNum) {
			this(size, eleNum, 2 * DisplayWrapper.itemHeight);
		}
		
		public WindowListHorizontal(Coord size, ListElement[] elements, int eleWidth) {
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
		
		protected void paint() {
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
				if(this.scrPos > 0) {
					this.scrPos--;
					dirty = true;
				}
				break;
			case KeyboardManager.KEY_RIGHT:
				if(this.scrPos + this.inner.width / this.eleWidth < this.elements.size()) {
					this.scrPos++;
					dirty = true;
				}
				break;
			}
			return dirty;
		}
		
	}
	
	protected final Vector elements;
	
	protected int scrPos;
	
	public WindowList(Coord size, int eleNum) {
		super(size, false);
		this.elements = new Vector(eleNum);
		this.scrPos = 0;
	}
	
	public WindowList(Coord size, ListElement[] elements) {
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
	
	protected abstract void paint();
	protected abstract boolean process();
	
}
