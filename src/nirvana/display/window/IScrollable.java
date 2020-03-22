package nirvana.display.window;

public interface IScrollable {
	
	public abstract boolean hasScroll();
	
	public abstract int getScrollMax();
	public abstract int getScrollPos();
	public abstract int getScrollSize();
	
}
