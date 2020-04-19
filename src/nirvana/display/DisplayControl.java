package nirvana.display;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import wapi.fontin.FontIn;
import wapi.fontin.FontManager;

import nirvana.util.Anchor;
import nirvana.util.Position.Coord;

/**
 * 
 * @author Wilderness Ranger
 */
public class DisplayControl {
	
	//Default fonts
	public static final FontIn fontNormal;
	public static final FontIn fontEmp;
	public static FontIn fontInfo;
	
	public static final int lineHeight;
	public static final int itemHeight;
	
	static {
		
		Font font = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
		if(font.getHeight() >= 16) {
			fontNormal = new FontIn(font);
			fontEmp = new FontIn(
				Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM)
			);
		} else {
			fontNormal = new FontIn(
				Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_LARGE)
			);
			fontEmp = new FontIn(
				Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_LARGE)
			);
		}
		fontInfo = new FontIn(
			Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN, Font.SIZE_SMALL)
		);
		
		lineHeight = Math.min(fontNormal.getHeight() + 2, 20);
		itemHeight = 20;
		
	}
	
	/**
	 * {@link Graphics}的片段类。
	 * @author Wilderness Ranger
	 */
	public static class ClipGraphics {
		
		protected final Graphics g;
		
		protected final StyleSet ss;
		protected final FontManager fm;
		
		public ClipGraphics(Graphics g, StyleSet ss) {
			this.g = g;
			this.ss = ss;
			this.fm = new FontManager();
		}
		public ClipGraphics(Graphics g) {
			this(g, DisplayControl.defaultStyleSet);
		}
		
		public final void drawString(Coord pos, String str, int limit, int anchor) {
			Coord nlimit = new Coord(limit, DisplayWrapper.lineHeight);
			pos = Anchor.translateOutOf(pos, nlimit, anchor);
			FontManager.setBox(pos.x, pos.y, nlimit.x, nlimit.y);
			FontManager.setLineHeight(lineHeight);
			FontManager.drawLine(g, str);
		}
		public final void drawInfo(Coord pos, String str, int limit, int anchor) {
			Coord nlimit = new Coord(limit, DisplayWrapper.lineHeight);
			pos = Anchor.translateOutOf(pos, nlimit, anchor);
			FontManager.setBox(pos.x, pos.y, nlimit.x, nlimit.y);
			int box[] = FontManager.getBox(), offset[] = FontManager.getOffset();
			FontManager.setLineHeight(fontInfo.getHeight());
			offset[0] += FontManager.drawLinePlain(g, box[0] + offset[0], box[1] + offset[1], fontInfo, FontManager.getColor(icolor), str);
			FontManager.setOffset(offset[0], offset[1]);
		}
		
		protected final void drawImage(Coord pos, Image img, int anchor) {
			this.g.drawImage(img, pos.x, pos.y, anchor);
		}
		
		protected final void drawGauge(Coord pos, byte type, int value, int max, Coord size, int anchor) {
			pos = Anchor.translateOutOf(pos, size, anchor);
			this.g.setColor(this.ss.gaugeBgColor);
			this.g.fillRect(pos.x, pos.y, size.x, size.y);
			this.g.setColor(this.ss.gaugeFgXXColor);
			this.g.fillRect(pos.x, pos.y, size.x * value / max, size.y);
			if(this.ss.hasBorder) {
				this.g.setColor(this.ss.borderColor);
				this.g.drawRect(pos.x, pos.y, size.x - 1, size.y - 1);
			}
		}
		
	}
	
	protected static final class StyleSet {
		
		public int bgColor = DisplayWrapper.bgColorNormal;
		
		public boolean hasBorder = true;
		public int borderColor = DisplayWrapper.borderColorNormal;
		
		public int scrollBgColor = DisplayWrapper.borderColorAux;
		public int scrollFgColor = DisplayWrapper.borderColorNormal;
		
		public int gaugeBgColor = DisplayWrapper.gaugeColorBg;
		public int gaugeFgHPColor = DisplayWrapper.gaugeColorHP;
		public int gaugeFgMPColor = DisplayWrapper.gaugeColorMP;
		
		public StyleSet() {}
		
		public StyleSet(boolean hasBorder, int[] colors) {
			this();
			this.hasBorder = hasBorder;
			this.bgColor = colors[0];
			this.borderColor = colors[1];
			this.scrollBgColor = colors[2];
			this.scrollFgColor = colors[3];
			this.gaugeBgColor = colors[4];
			this.gaugeFgHPColor = colors[5];
			this.gaugeFgMPColor = colors[6];
		}
		
	}
	
	public static final StyleSet defaultStyleSet = new StyleSet();
	
}
