package nirvana.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class TilePosition {
	
	public static final class TileCoord {
		
		public final int x;
		public final int y;
		
		public final byte facing;
		
		public TileCoord(int x, int y, byte facing) {
			this.x = x;
			this.y = y;
			this.facing = facing;
		}
		public TileCoord(int x, int y) {
			this(x, y, Facing.NULL);
		}
		public TileCoord() {
			this(0, 0);
		}
		public TileCoord(TileCoordVarible origin) {
			this(origin.x, origin.y, origin.facing);
		}
		
		public TileCoord(DataInputStream stream) throws IOException {
			this(stream.readInt(), stream.readInt(), stream.readByte());
		}
		
		public void serialize(DataOutputStream stream) throws IOException {
			stream.writeInt(this.x);
			stream.writeInt(this.x);
			stream.writeByte(this.facing);
		}
		
		public TileCoord add(int dx, int dy) {
			return new TileCoord(this.x + dx, this.y + dy, this.facing);
		}
		
		public TileCoord add(TileCoord d) {
			if(d.facing == Facing.NULL) return this.add(d.x, d.y);
			return new TileCoord(this.x + d.x, this.y + d.y, d.facing);
		}
		
		public TileCoord subtract(int dx, int dy) {
			return new TileCoord(this.x - dx, this.y - dy, this.facing);
		}
		
		public TileCoord subtract(TileCoord d) {
			if(d.facing == Facing.NULL) return this.subtract(d.x, d.y);
			return new TileCoord(this.x - d.x, this.y - d.y, d.facing);
		}
		
		public TileCoord adjust(byte facing) {
			return new TileCoord(this.x, this.y, facing);
		}
		
		public TileCoord forward() {
			switch(this.facing) {
			case Facing.ANY:
				return this;
			case Facing.UP:
				return this.subtract(0, 1);
			case Facing.DOWN:
				return this.add(0, 1);
			case Facing.LEFT:
				return this.subtract(1, 0);
			case Facing.RIGHT:
				return this.add(1, 0);
			default:
				throw new IllegalStateException("Unknown facing " + this.facing + ".");
			}
		}
		
		public TileCoord backward() {
			switch(this.facing) {
			case Facing.ANY:
				return this;
			case Facing.UP:
				return this.subtract(0, -1);
			case Facing.DOWN:
				return this.add(0, -1);
			case Facing.LEFT:
				return this.subtract(-1, 0);
			case Facing.RIGHT:
				return this.add(-1, 0);
			default:
				throw new IllegalStateException("Unknown facing " + this.facing + ".");
			}
		}
		
		public boolean isNatural() {
			return this.x >= 0 && this.y >= 0;
		}
		
		public boolean isPositive() {
			return this.x > 0 && this.y > 0;
		}
		
		//TODO correct
		public boolean contain(TileCoord coord) {
			return coord.x <= this.x && coord.y <= this.y;
		}
		
		public boolean contain(TileCoord pos, TileCoord size) {
			if(pos.x >= 0 && pos.y >= 0)
				return (pos.x + size.x <= this.x) && (pos.y + size.y <= this.y);
			else return false;
		}
		
	}
	
	public static final class TileCoordVarible {
		
		public int x;
		public int y;
		
		public byte facing;
		
		public TileCoordVarible(int x, int y, byte facing) {
			this.x = x;
			this.y = y;
			this.facing = facing;
		}
		public TileCoordVarible(int x, int y) {
			this(x, y, Facing.NULL);
		}
		public TileCoordVarible() {
			this(0, 0);
		}
		public TileCoordVarible(TileCoordVarible origin) {
			this(origin.x, origin.y, origin.facing);
		}
		public TileCoordVarible(TileCoord origin) {
			this(origin.x, origin.y, origin.facing);
		}
		
		public TileCoordVarible(DataInputStream stream) throws IOException {
			this(stream.readInt(), stream.readInt(), stream.readByte());
		}
		
		public void serialize(DataOutputStream stream) throws IOException {
			stream.writeInt(this.x);
			stream.writeInt(this.x);
			stream.writeByte(this.facing);
		}
		
		public TileCoordVarible add(int dx, int dy) {
			this.x += dx;
			this.y += dy;
			return this;
		}
		
		public TileCoordVarible add(TileCoordVarible d) {
			this.add(d.x, d.y);
			if(d.facing != Facing.NULL) this.adjust(d.facing);
			return this;
		}
		public TileCoordVarible add(TileCoord d) {
			this.add(d.x, d.y);
			if(d.facing != Facing.NULL) this.adjust(d.facing);
			return this;
		}
		
		public TileCoordVarible subtract(int dx, int dy) {
			this.x -= dx;
			this.y -= dy;
			return this;
		}
		
		public TileCoordVarible subtract(TileCoordVarible d) {
			this.subtract(d.x, d.y);
			if(d.facing != Facing.NULL) this.adjust(d.facing);
			return this;
		}
		public TileCoordVarible subtract(TileCoord d) {
			this.subtract(d.x, d.y);
			if(d.facing != Facing.NULL) this.adjust(d.facing);
			return this;
		}
		
		public TileCoordVarible adjust(byte facing) {
			this.facing = facing;
			return this;
		}
		
		public TileCoordVarible forward() {
			switch(this.facing) {
			case Facing.ANY:
				return this;
			case Facing.UP:
				return this.subtract(0, 1);
			case Facing.DOWN:
				return this.add(0, 1);
			case Facing.LEFT:
				return this.subtract(1, 0);
			case Facing.RIGHT:
				return this.add(1, 0);
			default:
				throw new IllegalStateException("Unknown facing " + this.facing + ".");
			}
		}
		
		public TileCoordVarible backward() {
			switch(this.facing) {
			case Facing.ANY:
				return this;
			case Facing.UP:
				return this.subtract(0, -1);
			case Facing.DOWN:
				return this.add(0, -1);
			case Facing.LEFT:
				return this.subtract(-1, 0);
			case Facing.RIGHT:
				return this.add(-1, 0);
			default:
				throw new IllegalStateException("Unknown facing " + this.facing + ".");
			}
		}
		
		public boolean isNatural() {
			return this.x >= 0 && this.y >= 0;
		}
		
		public boolean isPositive() {
			return this.x > 0 && this.y > 0;
		}
		
		public boolean contain(TileCoordVarible coord) {
			return coord.x <= this.x && coord.y <= this.y;
		}
		
		public boolean contain(TileCoordVarible pos, TileCoordVarible size) {
			if(pos.x >= 0 && pos.y >= 0)
				return (pos.x + size.x <= this.x) && (pos.y + size.y <= this.y);
			else return false;
		}
		
	}
	
	private TilePosition() {}
	
}
