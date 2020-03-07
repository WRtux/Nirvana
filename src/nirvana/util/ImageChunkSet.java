package nirvana.util;

import java.lang.ref.WeakReference;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

import nirvana.util.Position.Coord;

public class ImageChunkSet {
	
	protected final Image image;
	private final WeakReference refChunks[];
	
	public final Coord size;
	
	public final int cols;
	public final int lines;
	
	public ImageChunkSet(Image image, Coord size) {
		if(!size.isPositive())
			throw new IllegalArgumentException("Bad chunk size.");
		if(image.getWidth() % size.x != 0 || image.getHeight() % size.y != 0)
			throw new IllegalArgumentException("Bad chunkset image.");
		this.image = image;
		this.size = size;
		this.cols = image.getWidth() / size.x;
		this.lines = image.getHeight() / size.y;
		this.refChunks = new WeakReference[this.cols * this.lines];
		for(int i = 0; i < this.refChunks.length; i++)
			this.refChunks[i] = new WeakReference(null);
	}
	
	public Image[] getChunkArray() {
		Image[] arrChunk = new Image[this.cols * this.lines];
		Image chunk;
		for(int i = 0; i < arrChunk.length; i++) {
			chunk = (Image)this.refChunks[i].get();
			if(chunk == null) {
				chunk = Image.createImage(
					this.image,
					(i % this.cols) * this.size.x, (i / this.cols) * this.size.y,
					this.size.x, this.size.y, Sprite.TRANS_NONE
				);
				this.refChunks[i] = new WeakReference(chunk);
			}
			arrChunk[i] = chunk;
		}
		return arrChunk;
	}
	
	public Image getChunk(int index) {
		Image chunk = (Image)this.refChunks[index].get();
		if(chunk == null) {
			chunk = Image.createImage(
				this.image,
				(index % this.cols) * this.size.x, (index / this.cols) * this.size.y,
				this.size.x, this.size.y, Sprite.TRANS_NONE
			);
			this.refChunks[index] = new WeakReference(chunk);
		}
		return chunk;
	}
	
	public Sprite getSprite() {
		return new Sprite(this.image, this.size.x, this.size.y);
	}
	
}
