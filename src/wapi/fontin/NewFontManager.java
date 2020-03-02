package wapi.fontin;

import java.util.Hashtable;

import javax.microedition.lcdui.Font;

public final class NewFontManager {
	
	/* =====文本元素类===== */
	
	public static final class TextSegment {
		
		public final int offsetX;
		public final int width;
		
		public final FontIn font;
		public final int color;
		
		public final String text;
		
		protected TextSegment(int dx, int w, FontIn font, int color, String txt) {
			this.offsetX = dx;
			this.width = w;
			this.font = font;
			this.color = color;
			this.text = txt;
		}
		protected TextSegment(int w, FontIn font, int color, String txt) {
			this(0, w, font, color, txt);
		}
		
	}
	
	public static final class TextLine {
		
		public final int width;
		
		protected final TextSegment[] segs;
		
		protected TextLine(int w, TextSegment[] segs) {
			this.width = w;
			this.segs = new TextSegment[segs.length];
			System.arraycopy(segs, 0, this.segs, 0, segs.length);
		}
		
		public TextSegment[] getSegments() {
			TextSegment[] segs = new TextSegment[this.segs.length];
			System.arraycopy(this.segs, 0, segs, 0, segs.length);
			return segs;
		}
		
		public TextSegment getSegment(int i) {
			return this.segs[i];
		}
		
		public int getLeftEdge() {
			int min = this.width;
			for(int i = 0; i < this.segs.length; i++)
				if(this.segs[i].offsetX < min) min = this.segs[i].offsetX;
			return min;
		}
		
		public int getRightEdge() {
			int max = 0;
			for(int i = 0; i < this.segs.length; i++) {
				TextSegment seg = this.segs[i];
				if(seg.offsetX + seg.width > max) max = seg.offsetX + seg.width;
			}
			return max;
		}
		
	}
	
	public static final class TextBlock {
		
		public final int width;
		
		protected final TextSegment[][] segs;
		
		protected TextBlock(int w, TextSegment[][] segs) {
			this.width = w;
			this.segs = new TextSegment[segs.length][];
			for(int i = 0; i < segs.length; i++) {
				this.segs[i] = new TextSegment[segs.length];
				System.arraycopy(segs[i], 0, this.segs[i], 0, segs[i].length);
			}
		}
		protected TextBlock(int w, TextLine[] lines) {
			this.width = w;
			this.segs = new TextSegment[lines.length][];
			for(int i = 0; i < lines.length; i++) this.segs[i] = lines[i].segs;
		}
		
		protected TextBlock(int w, TextSegment[] segs, int[] seps) {
			this.width = w;
			this.segs = new TextSegment[seps.length + 1][];
			this.segs[0] = new TextSegment[seps[0]];
			System.arraycopy(segs, 0, this.segs[0], 0, seps[0]);
			for(int i = 1; i < seps.length; i++) {
				int len = seps[i] - seps[i - 1];
				this.segs[i] = new TextSegment[len];
				System.arraycopy(segs, seps[i], this.segs[i], 0, len);
			}
			int len = segs.length - seps[seps.length - 1];
			this.segs[seps.length] = new TextSegment[len];
			System.arraycopy(segs, seps[seps.length], this.segs[seps.length], 0, len);
		}
		
		public int getLines() {
			return this.segs.length;
		}
		
		public TextSegment[] getSegments(int line) {
			TextSegment[] segs = new TextSegment[this.segs[line].length];
			System.arraycopy(this.segs[line], 0, segs, 0, segs.length);
			return segs;
		}
		
		public TextSegment[][] getSegments() {
			TextSegment[][] segs = new TextSegment[this.segs.length][];
			for(int i = 0; i < this.segs.length; i++) {
				segs[i] = new TextSegment[this.segs[i].length];
				System.arraycopy(this.segs[i], 0, segs[i], 0, segs[i].length);
			}
			return segs;
		}
		
		public TextSegment getSegment(int line, int i) {
			return this.segs[line][i];
		}
		
		public int getLeftEdge() {
			int min = this.width;
			for(int i = 0; i < this.segs.length; i++)
				if(this.segs[i].offsetX < min) min = this.segs[i].offsetX;
			return min;
		}
		
		public int getRightEdge() {
			int max = 0;
			for(int i = 0; i < this.segs.length; i++) {
				TextSegment seg = this.segs[i];
				if(seg.offsetX + seg.width > max) max = seg.offsetX + seg.width;
			}
			return max;
		}
		
	}
	
	/* =====常量===== */
	
	/** 不在任何情况画出背景。 */
	public static final byte BGMODE_NONE = 0;
	
	/** 仅在显示系统字体时画出背景。 */
	public static final byte BGMODE_SYSTEM = 1;
	/** 仅在显示内嵌字体时画出背景。 */
	public static final byte BGMODE_EMBEDED = 2;
	
	/** 在显示系统与内嵌字体时均画出背景。 */
	public static final byte BGMODE_ALL = BGMODE_SYSTEM | BGMODE_EMBEDED;
	
	/* =====Buffer变量===== */
	
	public int boxXbuf;
	public int boxYbuf;
	
	public int boxWidthBuf;
	public int boxHeightBuf;
	
	public int offsetXbuf;
	public int offsetYbuf;
	
	public int lineHeightBuf;
	
	public FontIn fontsetBuf[];
	
	public int colorsetBuf[];
	
	public int bgColorBuf;
	public byte bgModeBuf;
	
	/* =====内部protected变量===== */
	
	protected int boxX = 0;
	protected int boxY = 0;
	
	protected int boxWidth = 0;
	protected int boxHeight = 0;
	
	protected int offsetX = 0;
	protected int offsetY = 0;
	
	protected int lineHeight;
	
	protected FontIn fontset[];
	
	protected int colorset[];
	
	protected int bgColor = 0x66666666;
	protected byte bgMode = BGMODE_NONE;
	
	/* =====弱引用哈希表===== */
	
	protected final Hashtable refFieldTable = new Hashtable(64);
	protected final Hashtable refLineTable = new Hashtable(64);
	
	/* =====构造函数===== */
	
	public NewFontManager() {
		//TODO complete
		this.fontset = new FontIn[] {new FontIn(Font.getDefaultFont())};
		this.lineHeight = Font.getDefaultFont().getHeight();
		this.colorset = new int[] {0xFFFFFF};
	}
	
	/* =====更新函数===== */
	
	public void updateBox() {
		if(this.boxWidthBuf < 0 || this.boxHeightBuf < 0)
			throw new IllegalArgumentException("Negtive box size.");
		this.boxX = this.boxXbuf;
		this.boxY = this.boxYbuf;
		this.boxWidth = this.boxWidthBuf;
		this.boxHeight = this.boxHeightBuf;
		this.offsetX = this.offsetY = 0;
	}
	
	public void updateOffset() {
		this.offsetX = this.offsetXbuf;
		this.offsetY = this.offsetYbuf;
	}
	
	public void updateLineHeight() {
		if(this.lineHeightBuf < 0) throw new IllegalArgumentException("Negtive line height.");
		this.lineHeight = this.lineHeightBuf;
	}
	
	public void updateFontset() {
		if(this.fontsetBuf.length > 0)
			this.fontset = new FontIn[Math.min(this.fontsetBuf.length, 9)];
		else throw new NullPointerException();
		System.arraycopy(this.fontsetBuf, 0, this.fontset, 0, this.fontset.length);
		this.offsetX = this.offsetY = 0;
		this.refFieldTable.clear();
		this.refLineTable.clear();
	}
	
	public void updateColorset() {
		if(this.colorsetBuf.length > 0)
			this.colorset = new int[Math.min(this.colorsetBuf.length, 9)];
		else throw new NullPointerException();
		System.arraycopy(this.colorsetBuf, 0, this.colorset, 0, this.colorset.length);
		this.offsetX = this.offsetY = 0;
	}
	
	public void updateBackground() {
		switch(this.bgModeBuf) {
		case BGMODE_NONE:
		case BGMODE_SYSTEM:
		case BGMODE_EMBEDED:
		case BGMODE_ALL:
			this.bgColor = this.bgColorBuf;
			this.bgMode = this.bgModeBuf;
			break;
		default:
			throw new IllegalArgumentException("Illegal background mode.");
		}
	}
	
	/* ===== */
	
	public TextLine formatLine(String str, int w) {
		
	}
	
	public TextBlock formatBlock(String str, int w) {
		
	}
	
	/* ===== */
	
	public void drawTextSegment(TextSegment seg, boolean offsetX) {
		
	}
	public void drawTextSegment(TextSegment seg) {
		drawTextSegment(seg, false);
	}
	
	public void drawTextLine(TextLine line) {
		
	}
	
	public void drawTextBlock(TextBlock block) {
		
	}
	
}
