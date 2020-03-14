package nirvana.display;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import nirvana.util.Position.Coord;

import wapi.fontin.FontIn;
import wapi.fontin.FontManager;
import wapi.fontin.FontManager.TextBlock;

public final class DisplayWrapper {
	
	//Application screen size
	public static final int scrWidth = 240;
	public static final int scrHeight = 320;
	
	//Default fonts
	public static final FontIn fontNormal;
	public static final FontIn fontEmp;
	public static FontIn fontInfo;
	public static final int lineHeight;
	public static final int itemHeight;
	
	//Default colors
	public static int voidColorNormal = 0x001122;
	public static int bgColorNormal = 0x440022;
	public static int bgColorEmp = 0x885522;
	public static int borderColorNormal = 0xFF9911;
	public static int borderColorAux = 0xAA7722;
	public static int textColorNormal = 0xFFFFFF;
	public static int textColorEmp = 0xFFCC55;
	public static int textColorAux = 0x88FF88;
	
	//Extend colors
	public static int gaugeColorBg = 0x220022;
	public static int gaugeColorHP = 0xEE3311;
	public static int gaugeColorMP = 0x1122EE;
	
	public static Image imgBorder;
	
	static {
		Font font = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
		if(font.getHeight() < 16) {
			fontNormal = new FontIn(
				Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_LARGE)
			);
			fontEmp = new FontIn(
				Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_LARGE)
			);
		} else {
			fontNormal = new FontIn(font);
			fontEmp = new FontIn(
				Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM)
			);
		}
		fontInfo = new FontIn(
			Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL)
		);
		lineHeight = Math.min(fontNormal.getHeight(), 20);
		itemHeight = 20;
		FontManager.setFontset(new FontIn[] {fontNormal, fontEmp, fontEmp});
		FontManager.setColorset(new int[] {textColorNormal, textColorEmp, textColorAux});
		FontManager.setEColor(0x66666666);
	}
	
	public static void setBox(Coord pos, Coord size) {
		FontManager.setBox(pos.x, pos.y, size.x, size.y);
	}
	/** @deprecated */
	public static void setBox(CoordSimple pos, CoordSimple size) {
		setBox(new Coord(pos.x, pos.y), new Coord(size.x, size.y));
	}
	
	public static void nextLine() {
		FontManager.nextLine();
	}
	
	public static void setColorset(int[] colors) {
		FontManager.setColorset(colors);
	}
	public static void setColor(int colorNormal, int colorEmp, int colorAux) {
		setColorset(new int[] {colorNormal, colorEmp, colorAux});
	}
	public static void setColor(int colorNormal, int colorEmp) {
		setColorset(new int[] {colorNormal, colorEmp, textColorAux});
	}
	public static void setColor(int color) {
		setColorset(new int[] {color, color, color});
	}
	
	public static TextBlock pretranslate(String str, int boxWidth, boolean multiline) {
		return FontManager.translatePreformattedString(str, boxWidth, multiline);
	}
	public static int getLines(String str, int boxWidth) {
		return FontManager.translatePreformattedString(str, boxWidth, true).getLines();
	}
	
	public static String delControls(String str) {
		return FontManager.delControls(str);
	}
	
	public static void drawString(Graphics g, TextBlock block) {
		FontManager.setLineHeight(lineHeight);
		FontManager.drawLine(g, block);
	}
	public static void drawString(Graphics g, String str) {
		FontManager.setLineHeight(lineHeight);
		FontManager.drawLine(g, str);
	}
	
	public static void drawInfo(Graphics g, int icolor, String str) {
		int box[] = FontManager.getBox(), offset[] = FontManager.getOffset();
		FontManager.setLineHeight(fontInfo.getHeight());
		offset[0] += FontManager.drawLinePlain(g, box[0] + offset[0], box[1] + offset[1], fontInfo, FontManager.getColor(icolor), str);
		FontManager.setOffset(offset[0], offset[1]);
	}
	
	public static void drawField(Graphics g, TextBlock block, int loffset) {
		FontManager.setLineHeight(lineHeight);
		FontManager.drawField(g, block, loffset);
	}
	public static void drawField(Graphics g, String str, int loffset) {
		FontManager.setLineHeight(lineHeight);
		FontManager.drawField(g, str, loffset);
	}
	
	private DisplayWrapper() {}
	
}
