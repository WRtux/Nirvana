package nirvana.display;

/** @deprecated */
public final class CoordSimple {
	
	public static final CoordSimple coordHLVL = new CoordSimple(0, 0);
	public static final CoordSimple coordHMVM = new CoordSimple(DisplayWrapper.scrWidth, DisplayWrapper.scrHeight);
	
	public static final CoordSimple coordHHVL = new CoordSimple(DisplayWrapper.scrWidth / 2, 0);
	public static final CoordSimple coordHHVH = new CoordSimple(DisplayWrapper.scrWidth / 2, DisplayWrapper.scrHeight / 2);
	public static final CoordSimple coordHHVM = new CoordSimple(DisplayWrapper.scrWidth / 2, DisplayWrapper.scrHeight);
	
	public int x;
	public int y;
	
	public CoordSimple(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public CoordSimple(CoordSimple coord) {
		this(coord.x, coord.y);
	}
	
	public CoordSimple adjust(int dx, int dy) {
		return new CoordSimple(this.x + dx, this.y + dy);
	}
	public CoordSimple adjust(CoordSimple d) {
		return new CoordSimple(this.x + d.x, this.y + d.y);
	}
	
	public boolean contain(CoordSimple coord) {
		return coord.x <= this.x && coord.y <= this.y;
	}
	
}
