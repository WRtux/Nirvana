package nirvana.util;

import javax.microedition.lcdui.Graphics;

import nirvana.util.Position.Coord;
import nirvana.util.Position.CoordVarible;

public final class Anchor {
	
	public static final int LEFT_TOP = Graphics.LEFT | Graphics.TOP;
	public static final int CENTER_TOP = Graphics.HCENTER | Graphics.TOP;
	public static final int RIGHT_TOP = Graphics.RIGHT | Graphics.TOP;
	
	public static final int LEFT_CENTER = Graphics.LEFT | Graphics.VCENTER;
	public static final int CENTER_CENTER = Graphics.HCENTER | Graphics.VCENTER;
	public static final int RIGHT_CENTER = Graphics.RIGHT | Graphics.VCENTER;
	
	public static final int LEFT_BOTTOM = Graphics.LEFT | Graphics.BOTTOM;
	public static final int CENTER_BOTTOM = Graphics.HCENTER | Graphics.BOTTOM;
	public static final int RIGHT_BOTTOM = Graphics.RIGHT | Graphics.BOTTOM;
	
	public static Coord getAdjustment(Coord size, int anchor) {
		if(!size.isNatural()) throw new IllegalArgumentException("Negtive coord size.");
		switch(anchor) {
		case LEFT_TOP:
			return new Coord(0, 0);
		case CENTER_TOP:
			return new Coord(size.x / 2, 0);
		case RIGHT_TOP:
			return new Coord(size.x, 0);
		case LEFT_CENTER:
			return new Coord(0, size.y / 2);
		case CENTER_CENTER:
			return new Coord(size.x / 2, size.y / 2);
		case RIGHT_CENTER:
			return new Coord(size.x, size.y / 2);
		case LEFT_BOTTOM:
			return new Coord(0, size.y);
		case CENTER_BOTTOM:
			return new Coord(size.x / 2, size.y);
		case RIGHT_BOTTOM:
			return new Coord(size.x, size.y);
		default:
			throw new IllegalArgumentException("Illegal anchor " + anchor + ".");
		}
	}
	
	public static Coord translateInto(Coord coord, Coord size, int anchor) {
		return coord.add(getAdjustment(size, anchor));
	}
	public static CoordVarible translateInto(CoordVarible coord, Coord size, int anchor) {
		return coord.add(getAdjustment(size, anchor));
	}
	
	public static Coord translateOutOf(Coord coord, Coord size, int anchor) {
		return coord.subtract(getAdjustment(size, anchor));
	}
	public static CoordVarible translateOutOf(CoordVarible coord, Coord size, int anchor) {
		return coord.subtract(getAdjustment(size, anchor));
	}
	
}
