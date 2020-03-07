package nirvana.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import nirvana.MIDletNirvana;

public final class Position {
	
	public static final class Coord {
		
		/** =====静态常量===== */
		
		/**
		 * 目标屏幕大小。
		 * 如果与实际的屏幕大小不匹配且处于调试模式{@link MIDletNirvana#debugMode}，将会在启动时发出警告。
		 */
		public static final Coord screenSize = new Coord(240, 320);
		
		public static final Coord LEFT_TOP = new Coord(0, 0);
		public static final Coord MIDDLE_TOP = new Coord(screenSize.x / 2, 0);
		public static final Coord RIGHT_TOP = new Coord(screenSize.x, 0);
		
		public static final Coord LEFT_MIDDLE = new Coord(0, screenSize.y / 2);
		public static final Coord MIDDLE_MIDDLE = new Coord(screenSize.x / 2, screenSize.y / 2);
		public static final Coord RIGHT_MIDDLE = new Coord(screenSize.x, screenSize.y / 2);
		
		public static final Coord LEFT_BOTTOM = new Coord(0, screenSize.y);
		public static final Coord MIDDLE_BOTTOM = new Coord(screenSize.x / 2, screenSize.y);
		public static final Coord RIGHT_BOTTOM = new Coord(screenSize.x, screenSize.y);
		
		public final int x;
		public final int y;
		
		public Coord(int x, int y) {
			this.x = x;
			this.y = y;
		}
		public Coord() {
			this(0, 0);
		}
		public Coord(CoordVarible origin) {
			this(origin.x, origin.y);
		}
		
		public Coord(DataInputStream stream) throws IOException {
			this(stream.readInt(), stream.readInt());
		}
		
		public void serialize(DataOutputStream stream) throws IOException {
			stream.writeInt(this.x);
			stream.writeInt(this.y);
		}
		
		public Coord add(int dx, int dy) {
			return new Coord(this.x + dx, this.y + dy);
		}
		public Coord add(Coord d) {
			return this.add(d.x, d.y);
		}
		
		public Coord subtract(int dx, int dy) {
			return new Coord(this.x - dx, this.y - dy);
		}
		public Coord subtract(Coord d) {
			return this.subtract(d.x, d.y);
		}
		
		public boolean isNatural() {
			return this.x >= 0 && this.y >= 0;
		}
		
		public boolean isPositive() {
			return this.x > 0 && this.y > 0;
		}
		
		//TODO correct
		public boolean contain(Coord coord) {
			return coord.x <= this.x && coord.y <= this.y;
		}
		
		public boolean contain(Coord pos, Coord size) {
			if(pos.x >= 0 && pos.y >= 0)
				return (pos.x + size.x <= this.x) && (pos.y + size.y <= this.y);
			else return false;
		}
		
	}
	
	public static final class CoordVarible {
		
		public int x;
		public int y;
		
		public CoordVarible(int x, int y) {
			this.x = x;
			this.y = y;
		}
		public CoordVarible() {
			this(0, 0);
		}
		public CoordVarible(CoordVarible origin) {
			this(origin.x, origin.y);
		}
		public CoordVarible(Coord origin) {
			this(origin.x, origin.y);
		}
		
		public CoordVarible(DataInputStream stream) throws IOException {
			this(stream.readInt(), stream.readInt());
		}
		
		public void serialize(DataOutputStream stream) throws IOException {
			stream.writeInt(this.x);
			stream.writeInt(this.y);
		}
		
		public CoordVarible add(int dx, int dy) {
			this.x += dx;
			this.y += dy;
			return this;
		}
		public CoordVarible add(CoordVarible d) {
			return this.add(d.x, d.y);
		}
		public CoordVarible add(Coord d) {
			return this.add(d.x, d.y);
		}
		
		public CoordVarible subtract(int dx, int dy) {
			this.x -= dx;
			this.y -= dy;
			return this;
		}
		public CoordVarible subtract(CoordVarible d) {
			return this.subtract(d.x, d.y);
		}
		public CoordVarible subtract(Coord d) {
			return this.subtract(d.x, d.y);
		}
		
		public boolean isNatural() {
			return this.x >= 0 && this.y >= 0;
		}
		
		public boolean isPositive() {
			return this.x > 0 && this.y > 0;
		}
		
		//TODO correct
		public boolean contain(CoordVarible coord) {
			return coord.x <= this.x && coord.y <= this.y;
		}
		
		public boolean contain(CoordVarible pos, CoordVarible size) {
			if(pos.x >= 0 && pos.y >= 0)
				return (pos.x + size.x <= this.x) && (pos.y + size.y <= this.y);
			else return false;
		}
		
	}
	
	public static final class Box {
		
		public final int x;
		public final int y;
		
		public final int width;
		public final int height;
		
		public Box(int x, int y, int width, int height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}
		public Box(int width, int height) {
			this(0, 0, width, height);
		}
		public Box(Coord pos, Coord size) {
			this(pos.x, pos.y, size.x, size.y);
		}
		
		public Box(DataInputStream stream) throws IOException {
			this(stream.readInt(), stream.readInt(), stream.readInt(), stream.readInt());
		}
		
		public void serialize(DataOutputStream stream) throws IOException {
			stream.writeInt(this.x);
			stream.writeInt(this.y);
			stream.writeInt(this.width);
			stream.writeInt(this.height);
		}
		
		public Coord getPos() {
			return new Coord(this.x, this.y);
		}
		
		public Coord getSize() {
			return new Coord(this.width, this.height);
		}
		
		public Box add(int dx, int dy) {
			return new Box(this.x + dx, this.y + dy, this.width, this.height);
		}
		public Box add(Coord d) {
			return this.add(d.x, d.y);
		}
		
		public Box subtract(int dx, int dy) {
			return new Box(this.x - dx, this.y - dy, this.width, this.height);
		}
		public Box subtract(Coord d) {
			return this.subtract(d.x, d.y);
		}
		
		public Box expand(int dx, int dy) {
			return new Box(this.x, this.y, this.width + dx, this.height + dy);
		}
		public Box expand(Coord d) {
			return this.expand(d.x, d.y);
		}
		
		public Box contract(int dx, int dy) {
			return new Box(this.x, this.y, this.width - dx, this.height - dy);
		}
		public Box contract(Coord d) {
			return this.contract(d.x, d.y);
		}
		
		public boolean isPosNatural() {
			return this.x >= 0 && this.y >= 0;
		}
		
		public boolean isPosPositive() {
			return this.x > 0 && this.y > 0;
		}
		
		public boolean isSizeNatural() {
			return this.width >= 0 && this.height >= 0;
		}
		
		public boolean isSizePositive() {
			return this.width > 0 && this.height > 0;
		}
		
		public boolean contain(Coord coord) {
			if(coord.x >= this.x && coord.y >= this.y)
				return (coord.x <= this.x + this.width) && (coord.y <= this.y + this.height);
			else return false;
		}
		
		public boolean contain(Coord pos, Coord size) {
			if(pos.x >= this.x && pos.y >= this.y)
				return (pos.x + size.x <= this.x + this.width) && (pos.y + size.y <= this.y + this.height);
			else return false;
		}
		public boolean contain(Box box) {
			return this.contain(box.getPos(), box.getSize());
		}
		
	}
	
	private Position() {}
	
}
