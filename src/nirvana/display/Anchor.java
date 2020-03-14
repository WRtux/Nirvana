package nirvana.display;

import javax.microedition.lcdui.Graphics;

/** @deprecated */
public final class Anchor {
	
	public static final int HLVT = Graphics.LEFT | Graphics.TOP;
	public static final int HCVT = Graphics.HCENTER | Graphics.TOP;
	
	public static final int HLVC = Graphics.LEFT | Graphics.VCENTER;
	public static final int HCVC = Graphics.HCENTER | Graphics.VCENTER;
	public static final int HRVC = Graphics.RIGHT | Graphics.VCENTER;
	
	public static final int HLVB = Graphics.LEFT | Graphics.BOTTOM;
	public static final int HCVB = Graphics.HCENTER | Graphics.BOTTOM;
	public static final int HRVB = Graphics.RIGHT | Graphics.BOTTOM;
	
	public static CoordSimple translateIn(CoordSimple coord, int width, int height, int anchor) {
		switch(anchor) {
		case HLVT:
			return coord;
		case HCVT:
			return coord.adjust(width / 2, 0);
		case HLVC:
			return coord.adjust(0, height / 2);
		case HCVC:
			return coord.adjust(width / 2, height / 2);
		case HRVC:
			return coord.adjust(width, height / 2);
		case HLVB:
			return coord.adjust(0, height);
		case HCVB:
			return coord.adjust(width / 2, height);
		case HRVB:
			return coord.adjust(width, height);
		default:
			throw new IllegalArgumentException("Illegal anchor " + anchor + ".");
		}
	}
	public static CoordSimple translateIn(CoordSimple coord, CoordSimple size, int anchor) {
		return translateIn(coord, size.x, size.y, anchor);
	}
	
	public static CoordSimple translateOut(CoordSimple coord, int width, int height, int anchor) {
		switch(anchor) {
		case HLVT:
			return coord;
		case HCVT:
			return coord.adjust(-width / 2, 0);
		case HLVC:
			return coord.adjust(0, -height / 2);
		case HCVC:
			return coord.adjust(-width / 2, -height / 2);
		case HRVC:
			return coord.adjust(-width, -height / 2);
		case HLVB:
			return coord.adjust(0, -height);
		case HCVB:
			return coord.adjust(-width / 2, -height);
		case HRVB:
			return coord.adjust(-width, -height);
		default:
			throw new IllegalArgumentException("Illegal anchor " + anchor + ".");
		}
	}
	public static CoordSimple translateOut(CoordSimple coord, CoordSimple size, int anchor) {
		return translateOut(coord, size.x, size.y, anchor);
	}
	
	private Anchor() {}
	
}
