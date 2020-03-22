package nirvana.display.window;

import java.lang.ref.WeakReference;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

import nirvana.control.ErrorHandler;
import nirvana.display.CoordSimple;
import nirvana.display.DisplayWrapper;
import nirvana.game.item.Item;
import nirvana.game.item.ItemStack;
import nirvana.util.Anchor;
import nirvana.util.Position.Box;
import nirvana.util.Position.Coord;
import wapi.fontin.FontManager.TextBlock;

public abstract class Window {
	
	public static final Coord PADDING_DEFAULT = new Coord(2, 2);
	public static final Coord SHRINK_DEFAULT = new Coord(4, 4);
	
	protected static final class StyleSet {
		
		public int bgColor = DisplayWrapper.bgColorNormal;
		public boolean hasBorder = true;
		public int borderColorNormal = DisplayWrapper.borderColorNormal;
		public int borderColorAux = DisplayWrapper.borderColorAux;
		
		public int textColorNormal = DisplayWrapper.textColorNormal;
		public int textColorEmp = DisplayWrapper.textColorEmp;
		public int textColorAux = DisplayWrapper.textColorAux;
		
		//TODO standardize
		public int gaugeBgColor = DisplayWrapper.gaugeColorBg;
		public int gaugeFgColor = DisplayWrapper.gaugeColorMP;
		
		public StyleSet() {}
		
	}
	
	public static final class ListElement {
		
		public static final class SiderEx {
			
			public static final int WIDTH_AUTO = -1;
			
			public final Image icon;
			
			public final String left;
			public final String right;
			public final int rWidth;
			
			public SiderEx(Image icon, String left, String right, int rWidth) {
				this.icon = icon;
				this.left = left;
				this.right = right;
				this.rWidth = rWidth;
			}
			public SiderEx(Image icon, String left, String right) {
				this(icon, left, right, WIDTH_AUTO);
			}
			public SiderEx(String left, String right, int rWidth) {
				this(null, left, right, rWidth);
			}
			public SiderEx(String left, String right) {
				this(left, right, WIDTH_AUTO);
			}
			
			protected void draw(Window window, Coord pos, Coord size) {
				pos = Anchor.translateInto(pos, size, Anchor.LEFT_CENTER).add(4, 0);
				size = size.subtract(8, 0);
				if(this.icon != null) {
					window.drawImage(pos, this.icon, Anchor.LEFT_CENTER);
					pos = pos.add(this.icon.getWidth() + 2, 0);
					size = size.subtract(this.icon.getWidth() + 2, 0);
				}
				if(this.rWidth == WIDTH_AUTO)
					window.drawSider(pos, this.left, this.right, size.x, Anchor.LEFT_CENTER);
				else {
					window.drawString(pos, this.left, size.x - this.rWidth, Anchor.LEFT_CENTER);
					window.drawString(pos.add(size.x, 0), this.right, this.rWidth, Anchor.RIGHT_CENTER);
				}
			}
			
		}
		
		protected final Object element;
		
		public ListElement(String str) {
			if(str == null) throw new NullPointerException();
			this.element = str;
		}
		
		public ListElement(SiderEx siderEx) {
			if(siderEx == null) throw new NullPointerException();
			this.element = siderEx;
		}
		
		public ListElement(Item item) {
			if(item == null) throw new NullPointerException();
			this.element = item;
		}
		public ListElement(ItemStack stack) {
			if(stack == null) throw new NullPointerException();
			this.element = stack;
		}
		
		public static ListElement[] constrcutElementArray(String[] arr) {
			ListElement[] eleArr = new ListElement[arr.length];
			for(int i = 0; i < eleArr.length; i++) eleArr[i] = new ListElement(arr[i]);
			return eleArr;
		}
		
		protected void draw(Window window, Coord pos, Coord size) {
			if(this.element instanceof SiderEx) {
				((SiderEx)this.element).draw(window, pos, size);
				return;
			}
			pos = Anchor.translateInto(pos, size, Anchor.LEFT_CENTER).add(4, 0);
			size = size.subtract(8, 0);
			if(this.element instanceof String)
				window.drawString(pos, (String)this.element, size.x, Anchor.LEFT_CENTER);
			else if(this.element instanceof Item) {
				Item item = (Item)this.element;
				Image icon = item.getIcon();
				int w = icon.getWidth() + 2;
				window.drawImage(pos, icon, Anchor.LEFT_CENTER);
				window.drawString(pos.add(w, 0), item.getName(), size.x - w, Anchor.LEFT_CENTER);
			} else if(this.element instanceof ItemStack) {
				ItemStack stack = (ItemStack)this.element;
				Item item = stack.getItem();
				Image icon = item.getIcon();
				int w = icon.getWidth() + 2;
				window.drawImage(pos, icon, Anchor.LEFT_CENTER);
				window.drawSider(pos.add(w, 0), item.getName(), "x" + stack.getNum(), size.x - w, Anchor.LEFT_CENTER);
			}
		}
		
	}
	
	protected Graphics g = null;
	public final Coord size;
	public final Box inner;
	
	public boolean visible = false;
	protected final boolean dynamic;
	public boolean dirty = false;
	
	protected int ticks = 0;
	protected int frames = 0;
	
	protected StyleSet style;
	
	private WeakReference refWindowImg;
	
	public static final boolean service(Window window) {
		if(!window.visible) return false;
		window.dirty |= window.process();
		window.ticks++;
		return true;
	}
	
	public static final boolean refresh(Graphics g, Coord pos, Window window) {
		if(!window.visible) return false;
		Image img = null;
		Graphics buf = g;
		Coord gcPos, gcSize;
		if(window.dynamic) {
			gcPos = new Coord(g.getClipX(), g.getClipY());
			gcSize = new Coord(g.getClipWidth(), g.getClipHeight());
			g.translate(pos.x, pos.y);
		} else {
			img = (Image)window.refWindowImg.get();
			if(img == null)
				img = Image.createImage(window.size.x, window.size.y);
			else if(!window.dirty) {
				g.drawImage(img, pos.x, pos.y, Anchor.LEFT_TOP);
				return true;
			}
			buf = img.getGraphics();
			gcPos = Coord.LEFT_TOP;
			gcSize = window.size;
		}
		buf.setColor(window.style.bgColor);
		buf.fillRect(0, 0, window.size.x, window.size.y);
		Coord padding = window.inner.getPos();
		buf.translate(padding.x, padding.y);
		buf.clipRect(-padding.x, -padding.y, window.size.x, window.size.y);
		window.g = buf;
		DisplayWrapper.setColor(
			window.style.textColorNormal, window.style.textColorEmp, window.style.textColorAux
		);
		window.paint();
		window.g = null;
		buf.translate(-padding.x, -padding.y);
		if(window.dynamic)
			buf.setClip(gcPos.x - pos.x, gcPos.y - pos.y, gcSize.x, gcSize.y);
		else buf.setClip(0, 0, window.size.x, window.size.y);
		if(window.style.hasBorder) {
			buf.setColor(window.style.borderColorNormal);
			buf.drawRect(0, 0, window.size.x - 1, window.size.y - 1);
			Image border = DisplayWrapper.imgBorder;
			int w = border.getWidth(), h = border.getHeight();
			int r = window.size.x - w, b = window.size.y - h;
			buf.drawImage(border, 0, 0, 0);
			buf.drawImage(Image.createImage(border, 0, 0, w, h, Sprite.TRANS_MIRROR), r, 0, 0);
			buf.drawImage(Image.createImage(border, 0, 0, w, h, Sprite.TRANS_MIRROR_ROT180), 0, b, 0);
			buf.drawImage(Image.createImage(border, 0, 0, w, h, Sprite.TRANS_ROT180), r, b, 0);
		}
		if(window instanceof IScrollable) {
			IScrollable scr = (IScrollable)window;
			int sMax = scr.getScrollMax(), sPos = scr.getScrollPos(), sSize = scr.getScrollSize();
			if(scr.hasScroll() && sMax > 0) {
				int l = window.size.x - 2, h = window.size.y - 2;
				int sLen = Math.max(h * sSize / sMax, 10);
				int sOff = (h - sLen) * sPos / Math.max(sMax - sSize, 1);
				buf.setColor(window.style.borderColorAux);
				buf.fillRect(l, 1, 2, h);
				buf.setColor(window.style.borderColorNormal);
				buf.fillRect(l, sOff + 1, 2, sLen);
			}
		}
		window.dirty = false;
		window.frames++;
		if(window.dynamic)
			g.translate(-pos.x, -pos.y);
		else {
			window.refWindowImg = new WeakReference(img);
			g.drawImage(img, pos.x, pos.y, Anchor.LEFT_TOP);
		}
		return true;
	}
	
	public Window(Coord size, Box inner, boolean dynamic) {
		if(size.x < DisplayWrapper.itemHeight + 4 || size.y < DisplayWrapper.itemHeight + 4)
			ErrorHandler.riseError(Window.class, new IllegalArgumentException("Window size too small."));
		this.size = size;
		this.inner = inner;
		this.dynamic = dynamic;
		if(!dynamic) this.refWindowImg = new WeakReference(null);
		this.dirty = true;
		this.style = new StyleSet();
	}
	public Window(Coord size, Coord padding, Coord innerSize, boolean dynamic) {
		this(size, new Box(padding, innerSize), dynamic);
	}
	public Window(Coord size, Coord innerSize, boolean dynamic) {
		this(size, new Coord((size.x - innerSize.x) / 2, (size.y - innerSize.y) / 2), innerSize, dynamic);
	}
	public Window(Coord size, boolean dynamic) {
		this(size, PADDING_DEFAULT, size.subtract(SHRINK_DEFAULT), dynamic);
	}
	
	protected final void clear() {
		this.g.setColor(this.style.bgColor);
		this.g.fillRect(0, 0, this.inner.width, this.inner.height);
	}
	
	protected final void drawString(CoordSimple pos, String str, int limit, int anchor) {
		CoordSimple nlimit = new CoordSimple(limit, DisplayWrapper.lineHeight);
		pos = Anchor.translateOut(pos, nlimit, anchor);
		DisplayWrapper.setBox(pos, nlimit);
		DisplayWrapper.drawString(this.g, str);
	}
	protected final void drawInfo(Coord pos, String str, int limit, int anchor) {
		Coord nlimit = new Coord(limit, DisplayWrapper.lineHeight);
		pos = Anchor.translateOutOf(pos, nlimit, anchor);
		DisplayWrapper.setBox(pos, nlimit);
		DisplayWrapper.drawInfo(this.g, 0, str);
	}
	
	protected final void drawSider(Coord pos, String left, String right, int limit, int anchor) {
		Coord nlimit = new Coord(limit, DisplayWrapper.lineHeight);
		pos = Anchor.translateOutOf(pos, nlimit, anchor);
		TextBlock block = DisplayWrapper.pretranslate(right, limit, false);
		int nw = Math.min(block.getRightEdge(), limit);
		DisplayWrapper.setBox(pos.add(limit - nw, 0), new Coord(nw, DisplayWrapper.lineHeight));
		DisplayWrapper.drawString(g, block);
		DisplayWrapper.setBox(pos, nlimit.subtract(nw, 0));
		DisplayWrapper.drawString(g, left);
	}
	
	protected final void drawField(CoordSimple pos, String str, int loffset, CoordSimple limit, int anchor) {
		pos = Anchor.translateOut(pos, limit, anchor);
		DisplayWrapper.setBox(pos, limit);
		DisplayWrapper.drawField(this.g, str, loffset);
	}
	protected final void drawField(CoordSimple pos, String str, CoordSimple limit, int anchor) {
		this.drawField(pos, str, 0, limit, anchor);
	}
	
	protected final void drawImage(CoordSimple pos, Image img, int anchor) {
		this.g.drawImage(img, pos.x, pos.y, anchor);
	}
	
	protected final void drawGauge(Coord pos, int value, int max, Coord size, int anchor) {
		pos = Anchor.translateOutOf(pos, size, anchor);
		this.g.setColor(this.style.gaugeBgColor);
		this.g.fillRect(pos.x, pos.y, size.x, size.y);
		this.g.setColor(this.style.gaugeFgColor);
		this.g.fillRect(pos.x, pos.y, size.x * value / max, size.y);
		if(this.style.hasBorder) {
			this.g.setColor(this.style.borderColorNormal);
			this.g.drawRect(pos.x, pos.y, size.x - 1, size.y - 1);
		}
	}
	
	protected final void drawElement(CoordSimple pos, ListElement ele, CoordSimple size, int anchor) {
		ele.draw(this, Anchor.translateOut(pos, size, anchor), size);
	}
	
	protected void paint() {}
	protected boolean process() {
		return false;
	}
	
}
