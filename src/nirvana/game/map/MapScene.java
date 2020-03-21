package nirvana.game.map;

import java.io.DataInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Vector;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import nirvana.data.ResourceManager;
import nirvana.display.DisplayWrapper;
import nirvana.util.Anchor;
import nirvana.util.Position.Coord;
import nirvana.util.Position.CoordVarible;
import nirvana.util.TilePosition.TileCoord;

final class Map {
	
	public final short id;
	
	protected final Mappic[] mappicSet;
	
	public final TileCoord size;
	public final int layers;
	protected final byte[][][] tileData;
	
	public static Map constructMap(short id, DataInputStream stream) throws IOException {
		if(stream.readShort() != 0x5464) throw new IllegalArgumentException("Bad map data.");
		if(stream.readShort() != id) throw new IllegalArgumentException("Map ID mismatch.");
		Map map = new Map(id, new TileCoord(stream.readShort(), stream.readShort()), stream.readByte());
		for(int i = 0; i < map.layers; i++)
			map.mappicSet[i] = ResourceManager.getMappic(stream.readByte());
		for(int y = 0; y < map.size.y; y++) for(int x = 0; x < map.size.x; x++)
				if(stream.read(map.tileData[y][x]) != map.layers)
					throw new IllegalArgumentException("Unexpected map end.");
		return map;
	}
	
	protected Map(short id, TileCoord size, int layers) {
		this.id = id;
		this.size = size;
		this.layers = layers;
		this.mappicSet = new Mappic[layers];
		this.tileData = new byte[size.y][size.x][layers];
	}
	
	public byte[][][] getTileData() {
		return this.tileData;
	}
	
}

public final class MapScene {
	
	public final short id;
	
	protected String name;
	
	protected final Map map;
	protected final Vector entities;
	
	protected CoordVarible camera = new CoordVarible();
	
	public static Map constructMap(short id, DataInputStream stream) throws IOException {
		return Map.constructMap(id, stream);
	}
	
	public static MapScene constructScene(short id, DataInputStream stream) throws IOException {
		if(stream.readShort() != 0x5480) throw new IllegalArgumentException("Bad scene data.");
		if(stream.readShort() != id) throw new IllegalArgumentException("Scene ID mismatch.");
		MapScene sce = new MapScene(id, (Map)ResourceManager.getMap(stream.readShort()));
		//TODO load entities
		return sce;
	}
	
	protected MapScene(short id, Map map) {
		this.id = id;
		this.map = map;
		this.entities = new Vector();
		this.dirty = true;
		this.refTileImage = new WeakReference(null);
	}
	
	private boolean dirty = false;
	private WeakReference refTileImage;
	public static void renderTileImage(Graphics g, Coord pos, MapScene sce, Coord size) {
		
		//Preparing
		Image img = (Image)sce.refTileImage.get();
		if(img == null || img.getWidth() != size.x || img.getHeight() != size.y) {
			sce.dirty = true;
			sce.refTileImage = new WeakReference(img = Image.createImage(size.x, size.y));
		}
		Map map = sce.map;
		CoordVarible camera = sce.camera;
		TileCoord offset = new TileCoord(
				(camera.x - size.x / 2) / Mappic.TILE_SIZE.x,
				(camera.y - size.y / 2) / Mappic.TILE_SIZE.y
			),
			n = new TileCoord(
				(camera.x + (size.x - 1) / 2) / Mappic.TILE_SIZE.x - offset.x + 1,
				(camera.y + (size.y - 1) / 2) / Mappic.TILE_SIZE.y - offset.y + 1
			);
		Coord z = new Coord(
			offset.x * Mappic.TILE_SIZE.x - camera.x + size.x / 2,
			offset.y * Mappic.TILE_SIZE.y - camera.y + size.y / 2
		);
		
		//Rendering tiles
		if(sce.dirty) {
			Graphics buf = img.getGraphics();
			buf.setColor(DisplayWrapper.voidColorNormal);
			buf.fillRect(0, 0, size.x, size.y);
			for(int ty = 0; ty < n.y; ty++) for(int tx = 0; tx < n.x; tx++) {
				int x = offset.x + tx, y = offset.y + ty;
				if(x < 0 || x >= map.size.x || y < 0 || y >= map.size.y) continue;
				for(int l = 0; l < map.layers; l++)
					buf.drawImage(
						map.mappicSet[l].getChunk(map.tileData[y][x][l]),
						z.x + tx * Mappic.TILE_SIZE.x, z.y + ty * Mappic.TILE_SIZE.y, 0
					);
			}
			sce.dirty = false;
		}
		
		//Flushing
		g.drawImage(img, pos.x, pos.y, Anchor.LEFT_TOP);
		
	}
	
	public static void renderEntities(Graphics g, Coord pos, MapScene sce, Coord size) {
		
		//Preparing
		Coord gcPos = new Coord(g.getClipX(), g.getClipY()),
			gcSize = new Coord(g.getClipWidth(), g.getClipHeight());
		g.translate(pos.x, pos.y);
		g.setClip(0, 0, size.x, size.y);
		CoordVarible camera = sce.camera;
		
		//Rendering entities
		for(int i = 0, j = sce.entities.size(); i < j; i++)
			((Entity)sce.entities.elementAt(i)).paint(g, new Coord(Anchor.translateOutOf(camera, size, Anchor.CENTER_CENTER)));
		
		//Post
		g.translate(-pos.x, -pos.y);
		g.setClip(gcPos.x - pos.x, gcPos.y - pos.y, gcSize.x, gcSize.y);
		
	}
	
	public String getName() {
		return this.name;
	}
	
	public CoordVarible getCamera() {
		return this.camera;
	}
	public void setCamera(CoordVarible camera) {
		if(camera == null) throw new NullPointerException();
		this.camera = camera;
		this.dirty = true;
	}
	
	public static CoordVarible cameraAt(TileCoord pos) {
		return new CoordVarible(
			pos.x * Mappic.TILE_SIZE.x + Mappic.TILE_SIZE.y / 2,
			pos.y * Mappic.TILE_SIZE.x + Mappic.TILE_SIZE.y / 2
		);
	}
	
	public void optimizeCamera(Coord size) {
		TileCoord msize = this.map.size;
		if(msize.x * Mappic.TILE_SIZE.x < size.x)
			this.camera.x = msize.x * Mappic.TILE_SIZE.x / 2;
		else if(camera.x < size.x / 2)
			this.camera.x = size.x / 2;
		else if(msize.x * Mappic.TILE_SIZE.x - 1 - camera.x < (size.x - 1) / 2)
			this.camera.x = msize.x * Mappic.TILE_SIZE.x - 1 - (size.x - 1) / 2;
		if(msize.y * Mappic.TILE_SIZE.y < size.y)
			this.camera.y = msize.y * Mappic.TILE_SIZE.y / 2;
		else if(camera.y < size.y / 2)
			this.camera.y = size.y / 2;
		else if(msize.y * Mappic.TILE_SIZE.y - 1 - camera.y < (size.y - 1) / 2)
			this.camera.y = msize.y * Mappic.TILE_SIZE.y - 1 - (size.y - 1) / 2;
		this.dirty = true;
	}
	
}
