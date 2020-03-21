package nirvana.game.map;

import javax.microedition.lcdui.Image;

import nirvana.util.Position.Coord;
import nirvana.util.ImageChunkSet;

public final class Mappic extends ImageChunkSet {
	
	public static final Coord TILE_SIZE = new Coord(16, 16);
	
	public static final byte TYPE_TERRAIN = 0x00;
	public static final byte TYPE_GROUND = 0x01;
	public static final byte TYPE_ATTACHER = 0x04;
	public static final byte TYPE_OBSTACLE = 0x05;
	public static final byte TYPE_MASK = 0x10;
	
	public static Mappic constructMappic(Image image) {
		return new Mappic(image);
	}
	
	public final byte[] tileTypes;
	
	protected Mappic(Image image) {
		super(image, TILE_SIZE);
		this.tileTypes = new byte[this.cols * this.lines];
		for(int i = 0; i < this.tileTypes.length; i++)
			this.tileTypes[i] = TYPE_GROUND;
	}
	
}
