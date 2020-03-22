package nirvana.display.window;

import nirvana.display.window.Window.ListElement;

public interface IElementList {
	
	public abstract int getElementNum();
	
	public abstract ListElement elementAt(int index);
	public abstract ListElement[] getElementsShown();
	
	public abstract void removeAllElements();
	public abstract void addElement(ListElement element);
	
}
