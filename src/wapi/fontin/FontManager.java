package wapi.fontin;

import java.lang.ref.WeakReference;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

public final class FontManager {
	
	public static final class TextSegment {
		
		protected int ifont;
		protected int icolor;
		
		protected String text;
		
		protected int dx;
		protected int width;
		
		protected TextSegment(int ifont, int icolor, String text, int dx, int width) {
			this.ifont = ifont;
			this.icolor = icolor;
			this.text = text;
			this.dx = dx;
			this.width = width;
		}
		
		public int getIFont() {
			return this.ifont;
		}
		public int getIColor() {
			return this.icolor;
		}
		
		public String getText() {
			return this.text;
		}
		
		public int getWidth() {
			return this.width;
		}
		
	}
	
	public static final class TextBlock {
		
		protected int width;
		
		protected TextSegment[] segs;
		protected int[] lineIndex;
		
		protected TextBlock(int width, int lines, TextSegment[] segs) {
			this.width = width;
			this.segs = segs;
			this.lineIndex = new int[lines];
			int line = 1;
			for(int i = 0; i < segs.length; i++)
				if(segs[i].width == -1) this.lineIndex[line++] = i + 1;
			if(lines != line) throw new NullPointerException();
		}
		
		public int getWidth() {
			return this.width;
		}
		
		public int getHeight() {
			return this.lineIndex.length * lineHeight;
		}
		public int getLines() {
			return this.lineIndex.length;
		}
		
		public int getLeftEdge() {
			int min = this.width;
			for(int i = 0; i < this.segs.length; i++)
				if(this.segs[i].dx < min) min = this.segs[i].dx;
			return min;
		}
		public int getRightEdge() {
			int max = 0;
			for(int i = 0; i < this.segs.length; i++) {
				TextSegment seg = this.segs[i];
				if(seg.dx + seg.width > max) max = seg.dx + seg.width;
			}
			return max;
		}
		
	}
	
	protected static int boxX = 0;
	protected static int boxY = 0;
	protected static int boxWidth = 0;
	protected static int boxHeight = 0;
	
	protected static int offsetX = 0;
	protected static int offsetY = 0;
	protected static int lineHeight;
	
	protected static FontIn fontset[];
	protected static int colorset[];
	protected static int ebcolor;
	
	public static final Hashtable refFieldTable = new Hashtable(64);
	public static final Hashtable refLineTable = new Hashtable(64);
	
	static {
		fontset = new FontIn[] {new FontIn(Font.getDefaultFont())};
		lineHeight = fontset[0].getHeight();
		colorset = new int[] {0};
		ebcolor = 0;
	}
	
	public FontManager() {}
	
	public static int[] getBox() {
		return new int[] {boxX, boxY, boxWidth, boxHeight};
	}
	public static int[] getOffset() {
		return new int[] {offsetX, offsetY};
	}
	
	public static int getLineHeight() {
		return lineHeight;
	}
	
	public static FontIn[] getFontset() {
		FontIn[] fonts = new FontIn[fontset.length];
		System.arraycopy(fontset, 0, fonts, 0, fontset.length);
		return fonts;
	}
	public static FontIn getFont(int index) {
		return fontset[index];
	}
	
	public static int[] getColorset() {
		int[] colors = new int[colorset.length];
		System.arraycopy(colorset, 0, colors, 0, colorset.length);
		return colors;
	}
	public static int getColor(int index) {
		return colorset[index];
	}
	public static int getEColor() {
		return ebcolor;
	}
	
	public static void setBox(int x, int y, int width, int height) {
		if(width < 0 || height < 0) throw new IllegalArgumentException("Negtive box size.");
		boxX = x;
		boxY = y;
		boxWidth = width;
		boxHeight = height;
		offsetX = offsetY = 0;
	}
	public static void setOffset(int x, int y) {
		offsetX = x;
		offsetY = y;
	}
	public static void nextLine() {
		offsetY += lineHeight;
		offsetX = 0;
	}
	
	public static void setLineHeight(int height) {
		if(height < 0) throw new IllegalArgumentException("Negtive line height.");
		lineHeight = height;
	}
	
	public static void setFontset(FontIn[] fonts) {
		if(fonts.length > 0) fontset = new FontIn[Math.min(fonts.length, 9)];
		else throw new NullPointerException();
		System.arraycopy(fonts, 0, fontset, 0, fontset.length);
		refFieldTable.clear();
		refLineTable.clear();
	}
	
	public static void setColorset(int[] colors) {
		if(colors.length > 0) colorset = new int[Math.min(colors.length, 9)];
		else throw new NullPointerException();
		for(int i = 0; i < colorset.length; i++) {
			int color = colors[i];
			if((color & 0xFF000000) == 0) color |= 0xFF000000;
			colorset[i] = color;
		}
	}
	public static void setEColor(int color) {
		ebcolor = color;
	}
	
	public static String delControls(String str) {
		StringBuffer buffer = new StringBuffer(str);
		int index = -1, sum = 0;
		while((index = str.indexOf("\\", index + 1)) != -1) {
			buffer.delete(index - sum * 2, index - sum * 2 + 2);
			sum++;
		}
		str = buffer.toString();
		return str;
	}
	
	protected static Vector addLineAlign(int align, Vector line, int width) {
		int dx = 0;
		switch(align) {
		case Graphics.LEFT:
			return line;
		case Graphics.HCENTER:
			dx = (boxWidth - width) / 2;
			break;
		case Graphics.RIGHT:
			dx = boxWidth - width;
			break;
		}
		for(int i = 0, size = line.size(); i < size; i++)
			((TextSegment)line.elementAt(i)).dx += dx;
		return line;
	}
	//这不是一个函数，这是地狱……
	public static TextBlock translatePreformattedString(String str, int boxWidth, boolean multiline) {
		Hashtable table = multiline ? refFieldTable : refLineTable;
		WeakReference ref = (WeakReference)table.get(str + (char)boxWidth);
		TextBlock block;
		if(ref != null) {
			block = (TextBlock)ref.get();
			if(block != null) return block;
		}
		int ialign = Graphics.LEFT, ifont = 0, icolor = 0;
		int lines = 1, start = 0;
		int dx = 0, width = 0;
		Vector segs = new Vector(), line = new Vector();
		for(int i = 0, l = str.length(); i < l; i++) {
			char ch = str.charAt(i);
			int cw = fontset[ifont].getWidth(ch);
			if(ch == '\\') {
				char control = str.charAt(i + 1);
				if(control >= '0' && control <= '9') {
					int n = control - '0';
					TextSegment seg = new TextSegment(ifont, icolor, str.substring(start, i), dx, width);
					line.addElement(seg);
					segs.addElement(seg);
					start = i + 2;
					dx += width;
					width = 0;
					if(n < fontset.length) ifont = n;
					else ifont = 0;
					if(n < colorset.length) icolor = n;
					else icolor = 0;
					i++;
					continue;
				}
				switch(control) {
				case 'l':
					if(dx == 0 && i == start) {
						ialign = Graphics.LEFT;
						start = i + 2;
						i++;
						continue;
					}
					break;
				case 'c':
					if(dx == 0 && i == start) {
						ialign = Graphics.HCENTER;
						start = i + 2;
						i++;
						continue;
					}
					break;
				case 'r':
					if(dx == 0 && i == start) {
						ialign = Graphics.RIGHT;
						start = i + 2;
						i++;
						continue;
					}
					break;
				}
			}
			if(ch == '\n' || dx + width + cw > boxWidth) {
				TextSegment seg = new TextSegment(ifont, icolor, str.substring(start, i), dx, -1);
				line.addElement(seg);
				segs.addElement(seg);
				addLineAlign(ialign, line, dx + width);
				if(multiline) lines++;
				else {
					start = str.length();
					if(ch == '\n') seg.width = width;
					else {
						seg.text += ch;
						seg.width = width + cw;
					}
					break;
				}
				start = i;
				if(ch == '\n') start++;
				dx = 0;
				if(ch == '\n') width = 0;
				else width = cw;
				line = new Vector();
			} else width += cw;
		}
		if(start != str.length()) {
			TextSegment seg = new TextSegment(ifont, icolor, str.substring(start, str.length()), dx, width);
			line.addElement(seg);
			segs.addElement(seg);
			addLineAlign(ialign, line, dx + width);
		}
		TextSegment[] arr = new TextSegment[segs.size()];
		segs.copyInto(arr);;
		table.put(str + (char)boxWidth, new WeakReference(block = new TextBlock(boxWidth, lines, arr)));
		return block;
	}
	
	protected static int drawSysCharPlain(Graphics g, int x, int y, FontIn font, int color, char ch) {
		g.setFont(font.sysFont);
		g.setColor(color);
		g.drawChar(ch, x, y + (lineHeight - font.sysFont.getHeight()) / 2, 0);
		return font.getWidth(ch);
	}
	protected static int drawEmbedCharPlain(Graphics g, int x, int y, FontIn font, int color, char ch) {
		byte[] d = font.getCharData(ch);
		int[] ARGB = new int[Math.max(d.length * 8, 8)];
		for(int l = 0; l < d.length; l++) for(int b = 0; b < 8; b++)
			ARGB[d.length * b + l] = ((d[l] << b & 0x80) == 0x80) ? color : ebcolor;
		g.drawRGB(ARGB, 0, d.length, x, y + (lineHeight - 8) / 2, d.length, 8, true);
		return d.length;
	}
	
	public static int drawLinePlain(Graphics g, int x, int y, FontIn font, int color, String str) {
		int width = 0;
		if(font.isSystem()) {
			g.setFont(font.sysFont);
			g.setColor(color);
			g.drawString(str, x, y + (lineHeight - font.sysFont.getHeight()) / 2, 0);
			width = font.getWidth(str);
		} else for(int i = 0, l = str.length(); i < l; i++)
			width += drawEmbedCharPlain(g, x + width, y, font, color, str.charAt(i));
		return width;
	}
	
	public static void drawField(Graphics g, TextBlock block, int loffset) {
		if(offsetY >= boxHeight) return;
		int gcx = g.getClipX(), gcy = g.getClipY(),
			gcwidth = g.getClipWidth(), gcheight = g.getClipHeight();
		Font gfont = g.getFont();
		int gcolor = g.getColor();
		g.clipRect(boxX, boxY, boxWidth, boxHeight);
		if(offsetX != 0) nextLine();
		TextSegment[] segs = block.segs;
		for(int i = block.lineIndex[loffset]; i < segs.length; i++) {
			TextSegment seg = segs[i];
			drawLinePlain(g, boxX + seg.dx, boxY + offsetY, fontset[seg.ifont], colorset[seg.icolor], seg.text);
			if(seg.width == -1) {
				nextLine();
				if(offsetY >= boxHeight) break;
			} else offsetX = seg.dx + seg.width;
		}
		g.setFont(gfont);
		g.setColor(gcolor);
		g.setClip(gcx, gcy, gcwidth, gcheight);
	}
	public static void drawField(Graphics g, String str, int loffset) {
		drawField(g, translatePreformattedString(str, boxWidth, true), loffset);
	}
	
	public static void drawLine(Graphics g, TextBlock block) {
		if(offsetY >= boxHeight) return;
		int gcx = g.getClipX(), gcy = g.getClipY(),
			gcwidth = g.getClipWidth(), gcheight = g.getClipHeight();
		Font gfont = g.getFont();
		int gcolor = g.getColor();
		g.clipRect(boxX, boxY, boxWidth, boxHeight);
		TextSegment[] segs = block.segs;
		for(int i = 0; i < segs.length; i++) {
			TextSegment seg = segs[i];
			drawLinePlain(g, boxX + seg.dx, boxY + offsetY, fontset[seg.ifont], colorset[seg.icolor], seg.text);
			if(seg.width == -1) {
				nextLine();
				break;
			} else offsetX = seg.dx + seg.width;
		}
		g.setFont(gfont);
		g.setColor(gcolor);
		g.setClip(gcx, gcy, gcwidth, gcheight);
	}
	public static void drawLine(Graphics g, String str) {
		drawLine(g, translatePreformattedString(str, boxWidth, false));
	}
	
}
