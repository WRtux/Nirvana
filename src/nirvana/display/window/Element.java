package nirvana.display.window;

import javax.microedition.lcdui.Image;

import nirvana.util.Anchor;
import nirvana.util.Position.Coord;

public final class Element {
	
	public static final class Sider {
		
		public static final int WIDTH_AUTO = -1;
		
		public final Image icon;
		
		public final String left;
		public final String right;
		public final int rWidth;
		
		public Sider(Image icon, String left, String right, int rWidth) {
			this.icon = icon;
			this.left = left;
			this.right = right;
			this.rWidth = rWidth;
		}
		public Sider(Image icon, String left, String right) {
			this(icon, left, right, WIDTH_AUTO);
		}
		public Sider(String left, String right, int rWidth) {
			this(null, left, right, rWidth);
		}
		public Sider(String left, String right) {
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
	
	protected static void draw(Window window, Coord pos, Object ele, Coord size) {
		
	}
	
}
