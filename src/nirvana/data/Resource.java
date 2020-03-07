package nirvana.data;

import java.io.DataInputStream;
import java.io.IOException;

import javax.microedition.lcdui.Image;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;

public final class Resource {
	
	public static final String SFX_VOID = "";
	
	public static final String SFX_BIN = ".bin";
	public static final String SFX_TEXT = ".txt";
	public static final String SFX_IMAGE = ".png";
	public static final String SFX_MIDI = ".mid";
	
	public static final String SFX_DATABASE = ".db";
	public static final String SFX_MAP = ".map";
	
	public static final int NOINDEX = -1;
	
	public final String category;
	public final String name;
	public final String suffix;
	public final int index;
	
	public Resource(String category, String name, String suffix, int index) {
		this.category = category;
		this.name = name;
		this.suffix = suffix;
		this.index = index;
	}
	public Resource(String category, String name, String suffix) {
		this(category, name, suffix, NOINDEX);
	}
	
	public Resource(String serial) {
		int index = serial.indexOf('/');
		if(index != 0) this.category = serial.substring(0, index);
		else this.category = null;
		index++;
		this.name = serial.substring(index, index = serial.indexOf('/', index));
		index++;
		int end = serial.indexOf('/', index);
		if(end == -1) end = serial.length();
		this.suffix = serial.substring(index, end);
		this.index = NOINDEX;
	}
	
	public String getFileName() {
		return "/" + (this.category == null ? "" : this.category + "/")
			+ this.name + (this.index == NOINDEX ? "" : "_" + this.index) + this.suffix;
	}
	
	public boolean exist() {
		DataInputStream stream = this.getStream();
		boolean exist = stream != null;
		try {
			stream.close();
		} catch(IOException ex) {}
		return exist;
	}
	
	public DataInputStream getStream() {
		return new DataInputStream(this.getClass().getResourceAsStream(this.getFileName()));
	}
	
	public byte[] getByteArray(int offset, int len) throws IOException {
		DataInputStream stream = this.getStream();
		byte[] arr = new byte[len];
		stream.read(arr, offset, len);
		stream.close();
		return arr;
	}
	
	public Image getImage() throws IOException {
		return Image.createImage(this.getFileName());
	}
	public Image getImage(int offset) throws IOException {
		DataInputStream stream = this.getStream();
		stream.mark(offset);
		Image img = Image.createImage(stream);
		stream.close();
		return img;
	}
	
	public Player getMidiPlayer() throws IOException, MediaException {
		DataInputStream stream = this.getStream();
		Player player = Manager.createPlayer(stream, "audio/midi");
		stream.close();
		player.prefetch();
		return player;
	}
	public Player getMidiPlayer(int offset) throws IOException, MediaException {
		DataInputStream stream = this.getStream();
		stream.mark(offset);
		Player player = Manager.createPlayer(stream, "audio/midi");
		stream.close();
		player.prefetch();
		return player;
	}
	
}
