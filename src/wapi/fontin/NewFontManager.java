package wapi.fontin;

import java.lang.ref.WeakReference;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

/**
 * 字体管理器类。
 * @version stable
 * @author Wilderness Ranger
 */
public class NewFontManager {
	
	/* =====文本元素类===== */
	
	/** 文本段类。表示相同格式的一段文本。 */
	public static final class TextSegment {
		
		public final int offset;
		
		public final int width;
		
		public final FontIn font;
		
		public final int color;
		
		public final String text;
		
		TextSegment(String txt, int d, int w, FontIn fnt, int clr) {
			if(txt == null) throw new NullPointerException();
			this.offset = d;
			this.width = w;
			this.font = fnt;
			this.color = clr;
			this.text = txt;
		}
		protected TextSegment(String txt, int d, FontIn fnt, int clr) {
			this(txt, d, fnt.getWidth(txt), fnt, clr);
		}
		protected TextSegment(String txt, FontIn fnt, int clr) {
			this(txt, 0, fnt, clr);
		}
		
		TextSegment(TextSegment seg, int d) {
			this(seg.text, d, seg.width, seg.font, seg.color);
		}
		
	}
	
	/** 文本行类。表示由数个文本段组成的一行文本。 */
	public static final class TextLine {
		
		protected final TextSegment[] segs;
		
		protected TextLine(TextSegment[] segs) {
			this.segs = new TextSegment[segs.length];
			System.arraycopy(segs, 0, this.segs, 0, segs.length);
		}
		
		TextLine(TextLine ln, int d) {
			this.segs = new TextSegment[ln.segs.length];
			int l = this.getLeftEdge();
			for(int i = 0; i < ln.segs.length; i++)
				this.segs[i] = new TextSegment(ln.segs[i], ln.segs[i].offset - l + d);
		}
		
		public TextSegment[] getSegments() {
			TextSegment[] segs = new TextSegment[this.segs.length];
			System.arraycopy(this.segs, 0, segs, 0, segs.length);
			return segs;
		}
		
		public int getSegmentNum() {
			return this.segs.length;
		}
		
		public TextSegment getSegment(int i) {
			return this.segs[i];
		}
		
		public int getLeftEdge() {
			int l = this.segs[0].offset;
			for(int i = 1; i < this.segs.length; i++)
				if(this.segs[i].offset < l) l = this.segs[i].offset;
			return l;
		}
		
		public int getRightEdge() {
			int r = this.segs[0].offset + this.segs[0].width;
			for(int i = 1; i < this.segs.length; i++) {
				TextSegment seg = this.segs[i];
				if(seg.offset + seg.width > r) r = seg.offset + seg.width;
			}
			return r;
		}
		
	}
	
	/** 文本块类。表示由数个文本行组成的完整文本。 */
	public static final class TextBlock {
		
		protected final TextSegment[][] segs;
		
		protected TextBlock(TextSegment[][] segs) {
			this.segs = new TextSegment[segs.length][];
			for(int i = 0; i < segs.length; i++) {
				this.segs[i] = new TextSegment[segs.length];
				System.arraycopy(segs[i], 0, this.segs[i], 0, segs[i].length);
			}
		}
		protected TextBlock(TextLine[] lns) {
			this.segs = new TextSegment[lns.length][];
			for(int i = 0; i < lns.length; i++)
				this.segs[i] = lns[i].segs;
		}
		
		protected TextBlock(TextSegment[] segs, int[] seps) {
			if(seps == null || seps.length == 0) {
				this.segs = new TextSegment[1][segs.length];
				System.arraycopy(segs, 0, this.segs[0], 0, segs.length);
				return;
			}
			this.segs = new TextSegment[seps.length + 1][];
			this.segs[0] = new TextSegment[seps[0]];
			System.arraycopy(segs, 0, this.segs[0], 0, seps[0]);
			for(int i = 1; i < seps.length; i++) {
				int len = seps[i] - seps[i - 1];
				this.segs[i] = new TextSegment[len];
				System.arraycopy(segs, seps[i - 1], this.segs[i], 0, len);
			}
			int len = segs.length - seps[seps.length - 1];
			this.segs[seps.length] = new TextSegment[len];
			System.arraycopy(segs, seps[seps.length - 1], this.segs[seps.length], 0, len);
		}
		
		public TextSegment[][] getSegments() {
			TextSegment[][] segs = new TextSegment[this.segs.length][];
			for(int i = 0; i < this.segs.length; i++) {
				segs[i] = new TextSegment[this.segs[i].length];
				System.arraycopy(this.segs[i], 0, segs[i], 0, segs[i].length);
			}
			return segs;
		}
		
		public int getLineNum() {
			return this.segs.length;
		}
		
		public int getSegmentNum() {
			int n = 0;
			for(int i = 0; i < this.segs.length; i++)
				n += this.segs[i].length;
			return n;
		}
		
		protected TextLine getLine(int i) {
			return new TextLine(this.segs[i]);
		}
		
		public TextSegment[] getSegments(int ln) {
			TextSegment[] segs = new TextSegment[this.segs[ln].length];
			System.arraycopy(this.segs[ln], 0, segs, 0, segs.length);
			return segs;
		}
		
		public TextSegment getSegment(int ln, int i) {
			return this.segs[ln][i];
		}
		
		public int getLeftEdge() {
			int l = this.segs[0][0].offset;
			for(int ln = 0; ln < this.segs.length; ln++)
				for(int i = 0; i < this.segs[ln].length; i++)
					if(this.segs[ln][i].offset < l) l = this.segs[ln][i].offset;
			return l;
		}
		
		public int getRightEdge() {
			int r = this.segs[0][0].offset + this.segs[0][0].width;
			for(int ln = 0; ln < this.segs.length; ln++)
				for(int i = 0; i < this.segs[ln].length; i++) {
					TextSegment seg = this.segs[ln][i];
					if(seg.offset + seg.width > r) r = seg.offset + seg.width;
				}
			return r;
		}
		
	}
	
	/* =====常量===== */
	
	/** 不在任何情况画出背景。 */
	public static final byte BGMODE_NONE = 0;
	/** 仅在显示系统字体时画出背景。 */
	public static final byte BGMODE_SYSTEM = 1;
	/** 仅在显示内嵌字体时画出背景。 */
	public static final byte BGMODE_EMBEDDED = 2;
	
	/** 在显示系统与内嵌字体时均画出背景。 */
	public static final byte BGMODE_ALL = BGMODE_SYSTEM | BGMODE_EMBEDDED;
	
	/* =====Buffer变量===== */
	
	public int boxXbuf;
	public int boxYbuf;
	
	public int boxWidthBuf;
	public int boxHeightBuf;
	
	public int offsetXbuf;
	public int offsetYbuf;
	
	public int lineHeightBuf;
	
	public FontIn[] fontsetBuf;
	
	public int[] colorsetBuf;
	
	public int bgColorBuf;
	
	/* =====内部变量===== */
	
	protected Graphics graphics;
	
	protected int boxX = 0;
	protected int boxY = 0;
	
	protected int boxWidth = 0;
	protected int boxHeight = 0;
	
	protected int offsetX = 0;
	protected int offsetY = 0;
	
	protected int lineHeight;
	
	protected FontIn[] fontset;
	
	protected int[] colorset;
	
	protected int bgColor = 0x66666666;
	
	protected byte bgMode = BGMODE_NONE;
	
	/* =====构造方法===== */
	
	public NewFontManager(Graphics g) {
		this.graphics = g;
		this.fontset = new FontIn[] {
			FontIn.defaultFont, FontIn.defaultFont, new FontIn(Font.getFont(1))};
		this.lineHeight = this.fontset[0].getHeight();
		this.colorset = new int[this.fontset.length];
		for(int i = 0; i < this.colorset.length; i++)
			this.colorset[i] = 0xFFFFFF;
		this.tableLineRef = new Hashtable(64);
		this.tableBlockRef = new Hashtable(64);
	}
	public NewFontManager() {
		this(null);
	}
	
	/* =====更新方法===== */
	
	public void setGraphics(Graphics g) {
		this.graphics = g;
	}
	
	public void updateBox() {
		if(this.boxWidthBuf < 0 || this.boxHeightBuf < 0)
			throw new IllegalArgumentException("Negative box size.");
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
		if(this.lineHeightBuf >= 0)
			this.lineHeight = this.lineHeightBuf;
		else
			throw new IllegalArgumentException("Negative line height.");
	}
	
	/**
	 * 更新字体集。
	 * 注意：本方法会清除格式化缓存。
	 */
	public void updateFontset() {
		if(this.fontsetBuf.length == 0) throw new NullPointerException();
		this.fontset = new FontIn[Math.min(this.fontsetBuf.length, 10)];
		for(int i = 0; i < this.fontset.length; i++)
			if(this.fontsetBuf[i] == null) throw new NullPointerException();
		System.arraycopy(this.fontsetBuf, 0, this.fontset, 0, this.fontset.length);
		this.tableLineRef.clear();
		this.tableBlockRef.clear();
	}
	
	/**
	 * 更新字体集。
	 * 注意：本方法会清除格式化缓存。
	 */
	public void updateColorset() {
		if(this.colorsetBuf.length == 0) throw new NullPointerException();
		this.colorset = new int[Math.min(this.colorsetBuf.length, 10)];
		System.arraycopy(this.colorsetBuf, 0, this.colorset, 0, this.colorset.length);
		this.tableLineRef.clear();
		this.tableBlockRef.clear();
	}
	
	public void updateBackground() {
		this.bgColor = this.bgColorBuf;
	}
	
	public void setBackgroundMode(byte bgm) {
		if(bgm >= BGMODE_NONE && bgm <= BGMODE_ALL) 
			this.bgMode = bgm;
		else
			throw new IllegalArgumentException("Illegal background mode.");
	}
	
	/* =====弱引用哈希表===== */
	
	protected final Hashtable tableLineRef;
	
	protected final Hashtable tableBlockRef;
	
	/* =====格式化方法===== */
	
	public static String deleteControls(String str, boolean ctrl, boolean esc) {
		StringBuffer sbuf = new StringBuffer(str.length());
		if(ctrl)
			for(int i = 0, len = str.length(); i < len; i++) {
				char ch = str.charAt(i);
				switch(ch) {
				case '\n':
				case '\f':
				case '\r':
				case '\t':
				case '\b':
					break;
				default:
					sbuf.append(ch);
				}
			}
		if(esc && sbuf.length() >= 2)
			for(int i = 0; i < sbuf.length(); i++) {
				char ch = str.charAt(i);
				if(ch == '\u007F') {
					sbuf.delete(i, i + 2);
					i--;
				}
			}
		return sbuf.toString();
	}
	
	protected static TextLine addAlign(TextLine ln, int r, int w, int algn) {
		if(ln == null) throw new NullPointerException();
		switch(algn) {
		case Graphics.LEFT:
			return ln;
		case Graphics.HCENTER:
			w = (w - r) / 2;
			break;
		case Graphics.RIGHT:
			w -= r;
			break;
		default:
			throw new IllegalArgumentException("Illegal align.");
		}
		for(int i = 0; i < ln.segs.length; i++) {
			TextSegment seg = ln.segs[i];
			ln.segs[i] = new TextSegment(seg, seg.offset + w);
		}
		return ln;
	}
	
	/**
	 * 
	 * @param str
	 * @param w
	 * @param mul 是否允许多行。
	 * @param dir
	 * @param sty
	 * @return 如果允许多行，则返回可能有多个文本行的数组，否则返回唯一的文本行。
	 */
	//地狱级方法……？
	protected TextLine[] format(String str, int w, boolean mul, int dir, int sty) {
		
		//检测参数
		if(str == null) throw new NullPointerException();
		if(w < 0) throw new IllegalArgumentException("Negative width.");
		if(dir != Graphics.LEFT && dir != Graphics.HCENTER && dir != Graphics.RIGHT)
			throw new IllegalArgumentException("Illegal align.");
		
		//初始化变量
		Vector lns = mul ? new Vector(4) : null, segs = new Vector(4);
		int algn = dir;
		int segi = 0, len = str.length();
		int segd = 0, segw = 0;
		FontIn fnt = this.fontset[Math.min(sty, this.fontset.length - 1)];
		int clr = this.colorset[Math.min(sty, this.colorset.length - 1)];
		
		//主循环
		for(int i = 0; i < len; i++) {
			char ch = str.charAt(i);
			TextSegment seg;
			switch(ch) {
			//换行符
			case '\n':
			//换页符（按2个换行处理）
			case '\f':
				if(i > segi) {
					seg = new TextSegment(str.substring(segi, i), segd, segw, fnt, clr);
					segs.addElement(seg);
				}
				if(!mul) {
					TextSegment[] arrseg = new TextSegment[segs.size()];
					segs.copyInto(arrseg);
					TextLine ln = new TextLine(arrseg);
					addAlign(ln, segd + segw, w, algn);
					return new TextLine[] {ln};
				}
				TextSegment[] arrseg = new TextSegment[segs.size()];
				segs.copyInto(arrseg);
				segs.removeAllElements();
				TextLine ln = new TextLine(arrseg);
				addAlign(ln, segd + segw, w, algn);
				lns.addElement(ln);
				if(ch == '\f')
					lns.addElement(new TextLine(new TextSegment[0]));
				algn = dir;
				segi = i + 1;
				segd = segw = 0;
				fnt = this.fontset[Math.min(sty, this.fontset.length - 1)];
				clr = this.colorset[Math.min(sty, this.colorset.length - 1)];
				break;
			//回车
			case '\r': //TODO 此算法的对齐处理存在一定问题，需调整。
				if(i > segi) {
					seg = new TextSegment(str.substring(segi, i), segd, segw, fnt, clr);
					segs.addElement(seg);
				}
				segi = i + 1;
				segd = segw = 0;
				break;
			//制表符（按2个全角空格处理）
			case '\t':
				if(i > segi) {
					seg = new TextSegment(str.substring(segi, i), segd, segw, fnt, clr);
					segs.addElement(seg);
				}
				segi = i + 1;
				segd += segw + fnt.getWidth(2);
				segw = 0;
				break;
			//退格字符
			case '\b':
				if(i > segi) {
					seg = new TextSegment(str.substring(segi, i), segd, segw, fnt, clr);
					segs.addElement(seg);
				}
				segd += segw - (i != segi ? fnt.getWidth(str.charAt(i - 1)) : 0);
				segi = i + 1;
				segw = 0;
				break;
			//转义字符
			case '\u007F':
				if(i > segi) {
					seg = new TextSegment(str.substring(segi, i), segd, segw, fnt, clr);
					segs.addElement(seg);
				}
				char ctrl = str.charAt(++i);
				if(ctrl >= '0' && ctrl <= '9') {
					fnt = this.fontset[Math.min(ctrl - '0', this.fontset.length - 1)];
					clr = this.colorset[Math.min(ctrl - '0', this.colorset.length - 1)];
				} else switch(ctrl) {
				case 'L':
					if(segs.size() == 0 && i == segi)
						algn = Graphics.LEFT;
					break;
				case 'C':
					if(segs.size() == 0 && i == segi)
						algn = Graphics.HCENTER;
					break;
				case 'R':
					if(segs.size() == 0 && i == segi)
						algn = Graphics.RIGHT;
					break;
				}
				segi = i + 2;
				segd += segw;
				segw = 0;
				break;
			//普通字符
			default:
				int chw = fnt.getWidth(ch);
				if(segi + segw + chw > w) {
					if(!mul) {
						seg = new TextSegment(str.substring(segi, ++i), segd, segw + chw, fnt, clr);
						segs.addElement(seg);
						arrseg = new TextSegment[segs.size()];
						segs.copyInto(arrseg);
						ln = new TextLine(arrseg);
						return new TextLine[] {ln};
					}
					if(i > segi) {
						seg = new TextSegment(str.substring(segi, i), segd, segw, fnt, clr);
						segs.addElement(seg);
					}
					arrseg = new TextSegment[segs.size()];
					segs.copyInto(arrseg);
					segs.removeAllElements();
					ln = new TextLine(arrseg);
					addAlign(ln, segd + segw, w, algn);
					lns.addElement(ln);
					algn = dir;
					segi = i;
					segd = segw = 0;
					fnt = this.fontset[Math.min(sty, this.fontset.length - 1)];
					clr = this.colorset[Math.min(sty, this.colorset.length - 1)];
				}
				segw += chw;
			}
		}
		
		//处理尾段
		if(segi < len) {
			TextSegment seg = new TextSegment(str.substring(segi, len), segd, segw, fnt, clr);
			segs.addElement(seg);
		}
		TextSegment[] arrseg = new TextSegment[segs.size()];
		segs.copyInto(arrseg);
		TextLine ln = new TextLine(arrseg);
		addAlign(ln, segd + segw, w, algn);
		if(!mul)
			return new TextLine[] {ln};
		lns.addElement(ln);
		TextLine[] arrln = new TextLine[lns.size()];
		lns.copyInto(arrln);
		return arrln;
		
	}
	protected TextLine[] format(String str, int w, boolean mul) {
		return this.format(str, w, mul, Graphics.LEFT, 0);
	}
	
	public TextLine formatLine(String str, int w) {
		if(str == null) throw new NullPointerException();
		if(w < 0) throw new IllegalArgumentException("Negative width.");
		String key = Integer.toHexString(w | 0x80000000) + str;
		WeakReference ref = (WeakReference)this.tableLineRef.get(key);
		TextLine ln;
		if(ref != null) {
			ln = (TextLine)ref.get();
			if(ln != null) return ln;
		}
		ln = this.format(str, w, false)[0];
		this.tableLineRef.put(key, new WeakReference(ln));
		return ln;
	}
	
	public TextLine formatLine(String str, int w, int dir, int sty) {
		return this.format(str, w, false, dir, sty)[0];
	}
	
	public TextBlock formatBlock(String str, int w) {
		if(str == null) throw new NullPointerException();
		if(w < 0) throw new IllegalArgumentException("Negative width.");
		String key = Integer.toHexString(w | 0x80000000) + str;
		WeakReference ref = (WeakReference)this.tableBlockRef.get(key);
		TextBlock blk;
		if(ref != null) {
			blk = (TextBlock)ref.get();
			if(blk != null) return blk;
		}
		blk = new TextBlock(this.format(str, w, true));
		this.tableBlockRef.put(key, new WeakReference(blk));
		return blk;
	}
	
	public TextLine formatBlock(String str, int w, int dir, int sty) {
		return this.format(str, w, true, dir, sty)[0];
	}
	
	/* =====绘制方法===== */
	
	/** 裁剪区域副本。用作方法静态变量。 */
	private int cx, cy, cw, ch;
	/** 图形使用的字体的副本。用作方法静态变量。 */
	private Font gfnt;
	/** 图形使用的颜色的副本。用作方法静态变量。 */
	private int gclr;
	final void backupClip() {
		Graphics g = this.graphics;
		cx = g.getClipX();
		cy = g.getClipY();
		cw = g.getClipWidth();
		ch = g.getClipHeight();
		gfnt = g.getFont();
		gclr = g.getColor();
	}
	
	final void updateClip() {
		this.graphics.clipRect(this.boxX, this.boxY, this.boxWidth, this.boxHeight);
	}
	
	final void restoreClip() {
		Graphics g = this.graphics;
		g.setClip(cx, cy, cw, ch);
		g.setFont(gfnt);
		g.setColor(gclr);
	}
	
	/**
	 * 绘制系统字体的文本（不能包含控制字符）。
	 * 为防止频繁调整裁剪区域影响性能，本方法不会调用{@link Graphics#setClip(int, int, int, int)}。
	 * @param g 用于绘制的图形。
	 * @param txt 将被绘制的文本。
	 * @param x 绘制的坐标。以左上角为参考。
	 * @param y
	 * @param h 行高。
	 * @param fnt 文本使用的字体。必须为{@link FontIn#TYPE_SYSTEM}字体。
	 * @param clr RGB格式的文本颜色。
	 * @param bgclr ARGB格式的背景颜色。
	 */
	protected static void drawSysText(Graphics g, String txt, int x, int y, int h, FontIn fnt, int clr, int bgclr) {
		if(g == null) throw new NullPointerException();
		if(fnt.type != FontIn.TYPE_SYSTEM)
			throw new IllegalArgumentException("Illegal font type.");
		if(txt.length() == 0) return;
		int dy = fnt.getHeight();
		if(dy <= h)
			dy = h - dy;
		else {
			y += h;
			h = (h + dy + 1) / 2;
			y -= h;
			dy = 0;
		}
		if((bgclr & 0xFF000000) == 0xFF000000) {
			g.setColor(bgclr);
			g.drawRect(x, y, fnt.getWidth(txt), h);
		} else if((bgclr & 0xFF000000) != 0) {
			int w = fnt.getWidth(txt);
			int[] dat = new int[w * h];
			for(int i = 0; i < dat.length; i++)
				dat[i] = bgclr;
			g.drawRGB(dat, 0, w, x, y, w, h, true);
		}
		g.setFont(fnt.sysFont);
		g.setColor(clr);
		g.drawString(txt, x, y + dy, 0);
	}
	
	/**
	 * 绘制内嵌字体的文本。
	 * @param g 用于绘制的图形。
	 * @param txt 将被绘制的文本。
	 * @param x 绘制的坐标。以左上角为参考。
	 * @param y
	 * @param h 行高。
	 * @param fnt 文本使用的字体。必须为{@link FontIn#TYPE_EMBEDDED}字体。
	 * @param clr RGB格式的文本颜色。
	 * @param bgclr ARGB格式的背景颜色。
	 */
	protected static void drawEmbedText(Graphics g, String txt, int x, int y, int h, FontIn fnt, int clr, int bgclr) {
		if(g == null) throw new NullPointerException();
		if(fnt.type != FontIn.TYPE_EMBEDDED)
			throw new IllegalArgumentException("Illegal font type.");
		if(txt.length() == 0) return;
		int w = fnt.getWidth(txt), dy = fnt.getHeight();
		if(dy <= h)
			dy = h - dy;
		else {
			y += h;
			h = (h + dy + 1) / 2;
			y -= h;
			dy = 0;
		}
		int[] dat = new int[w * h];
		if((bgclr & 0xFF000000) != 0)
			for(int i = 0; i < dat.length; i++) dat[i] = bgclr;
		clr &= 0xFFFFFF;
		int dx = 0;
		for(int i = 0, len = txt.length(); i < len; i++) {
			int[] chdat = fnt.getCharData(txt.charAt(i));
			for(int j = 0; j < chdat.length; j++, dx++)
				for(int b = 0; dy + b < h; b++)
					if((chdat[j] >>> b & 1) == 1) dat[dx + w * (dy + b)] = clr;
		}
		g.drawRGB(dat, 0, w, x, y, w, h, true);
	}
	
	/** （不能包含控制字符） */
	public void drawText(String txt, int h, int sty, boolean bg) {
		if(txt == null) throw new NullPointerException();
		if(this.graphics == null) return;
		if(this.offsetY + h <= 0 || this.offsetY >= this.boxHeight) return;
		if(this.offsetX >= this.boxWidth) return;
		this.backupClip();
		this.updateClip();
		Graphics g = this.graphics;
		int x = this.boxX + this.offsetX, y = this.boxY + this.offsetY;
		FontIn fnt = this.fontset[Math.min(sty, this.fontset.length - 1)];
		int clr = this.colorset[Math.min(sty, this.colorset.length - 1)];
		if(fnt.type == FontIn.TYPE_SYSTEM)
			drawSysText(g, txt, x, y, h, fnt, clr, bg ? this.bgColor : 0);
		else
			drawEmbedText(g, txt, x, y, h, fnt, clr, bg ? this.bgColor : 0);
		this.restoreClip();
		this.offsetX += fnt.getWidth(txt);
	}
	
	public void drawTextSegment(TextSegment seg, boolean off) { //ADV 越界优化
		if(seg == null) throw new NullPointerException();
		if(this.graphics == null) return;
		this.backupClip();
		this.updateClip();
		Graphics g = this.graphics;
		int x = this.boxX + this.offsetX, y = this.boxY + this.offsetY;
		if(off) x += seg.offset;
		if(seg.font.type == FontIn.TYPE_SYSTEM) {
			boolean f = (this.bgMode & BGMODE_SYSTEM) != 0;
			drawSysText(
				g, seg.text, x, y, this.lineHeight, seg.font, seg.color, f ? this.bgColor : 0);
		} else {
			boolean f = (this.bgMode & BGMODE_EMBEDDED) != 0;
			drawEmbedText(
				g, seg.text, x, y, this.lineHeight, seg.font, seg.color, f ? this.bgColor : 0);
		}
		this.restoreClip();
		this.offsetX += seg.width;
	}
	public void drawTextSegment(TextSegment seg) {
		this.drawTextSegment(seg, false);
	}
	
	public void drawTextLine(TextLine ln) { //ADV 越界优化
		if(ln == null) throw new NullPointerException();
		if(this.graphics == null) return;
		this.backupClip();
		this.updateClip();
		Graphics g = this.graphics;
		int x = this.boxX + this.offsetX, y = this.boxY + this.offsetY;
		for(int i = 0; i < ln.segs.length; i++) {
			TextSegment seg = ln.segs[i];
			if(seg.font.type == FontIn.TYPE_SYSTEM) {
				int bgclr = (this.bgMode & BGMODE_SYSTEM) != 0 ? this.bgColor : 0;
				drawSysText(
					g, seg.text, x + seg.offset, y, this.lineHeight, seg.font, seg.color, bgclr);
			} else {
				int bgclr = (this.bgMode & BGMODE_EMBEDDED) != 0 ? this.bgColor : 0;
				drawEmbedText(
					g, seg.text, x + seg.offset, y, this.lineHeight, seg.font, seg.color, bgclr);
			}
		}
		this.restoreClip();
		this.offsetY += this.lineHeight;
	}
	
	public void drawTextBlock(TextBlock blk) { //ADV 越界优化
		if(blk == null) throw new NullPointerException();
		if(this.graphics == null) return;
		this.backupClip();
		this.updateClip();
		Graphics g = this.graphics;
		int x = this.boxX + this.offsetX, y = this.boxY + this.offsetY;
		for(int i = 0; i < blk.segs.length; i++) {
			TextSegment[] ln = blk.segs[i];
			for(int j = 0; j < ln.length; j++) {
				TextSegment seg = ln[i];
				if(seg.font.type == FontIn.TYPE_SYSTEM) {
					int bgclr = (this.bgMode & BGMODE_SYSTEM) != 0 ? this.bgColor : 0;
					drawSysText(
						g, seg.text, x + seg.offset, y, this.lineHeight, seg.font, seg.color, bgclr);
				} else {
					int bgclr = (this.bgMode & BGMODE_EMBEDDED) != 0 ? this.bgColor : 0;
					drawEmbedText(
						g, seg.text, x + seg.offset, y, this.lineHeight, seg.font, seg.color, bgclr);
				}
			}
			y += this.lineHeight;
			this.offsetY += this.lineHeight;
		}
		this.restoreClip();
	}
	
}
