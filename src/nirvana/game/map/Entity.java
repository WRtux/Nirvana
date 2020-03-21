package nirvana.game.map;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

import nirvana.util.Position.Coord;
import nirvana.util.TilePosition.TileCoordVarible;

public abstract class Entity {
	
	protected final MapScene mapIn;
	protected TileCoordVarible pos;
	
	protected Sprite sprite;
	
	public Entity(MapScene mapIn, TileCoordVarible pos) {
		this.mapIn = mapIn;
		this.pos = pos;
	}
	
	public final void setImage(Image image, Coord size) {
		sprite.setImage(image, size.x, size.y);
		sprite.defineReferencePixel(size.x / 2, size.y);
	}
	
	public final void paint(Graphics g, Coord ref) {
		sprite.setPosition(sprite.getX() - ref.x, sprite.getY() - ref.y);
		this.sprite.paint(g);
		this.sprite.nextFrame();
	}
	
}
