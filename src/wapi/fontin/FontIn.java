package wapi.fontin;

import javax.microedition.lcdui.Font;

public final class FontIn {
	
	public static final boolean TYPE_SYSTEM = false;
	public static final boolean TYPE_EMBEDED = true;
	
	protected final boolean type;
	
	protected final Font sysFont;
	protected final byte[][] embedData;
	
	public FontIn(Font sysFont) {
		this.type = TYPE_SYSTEM;
		this.sysFont = sysFont;
		this.embedData = null;
		this.cacheWidth = new int[128];
		for(char ch = 0; ch < 128; ch++)
			this.cacheWidth[ch] = this.sysFont.charWidth(ch);
	}
	
	public FontIn(byte[][] embedData) {
		this.type = TYPE_EMBEDED;
		this.sysFont = null;
		if(embedData.length == 128) {
			this.embedData = new byte[128][];
			this.cacheWidth = new int[128];
			for(char ch = 0; ch < 128; ch++) {
				this.embedData[ch] = new byte[embedData[ch].length];
				System.arraycopy(embedData[ch], 0, this.embedData[ch], 0, embedData[ch].length);
				this.cacheWidth[ch] = embedData[ch].length;
			}
		} else throw new IllegalArgumentException("Invalid embeded font data.");
	}
	
	public boolean isSystem() {
		return this.type == TYPE_SYSTEM;
	}
	
	public int getHeight() {
		if(this.type == TYPE_SYSTEM) return this.sysFont.getHeight();
		else return 8;
	}
	public int getHeight(int lines) {
		if(lines < 0) throw new IllegalArgumentException("Negtive line num.");
		return lines * this.getHeight();
	}
	
	public int getWidth(int tiles) {
		if(tiles < 0) throw new IllegalArgumentException("Negtive tile num.");
		return tiles * this.getHeight();
	}
	
	private final int[] cacheWidth;
	public int getWidth(char ch) {
		if(this.type == TYPE_SYSTEM) {
			if(ch < this.cacheWidth.length) return this.cacheWidth[ch];
			else return this.sysFont.charWidth(ch);
		} else return this.cacheWidth[ch < this.cacheWidth.length ? ch : 127];
	
	}
	public int getWidth(String str) {
		if(this.type == TYPE_SYSTEM) return this.sysFont.stringWidth(str);
		else {
			int len = 0;
			for(int i = 0, l = str.length(); i < l; i++) {
				char ch = str.charAt(i);
				len += this.cacheWidth[ch < this.cacheWidth.length ? ch : 127];
			}
			return len;
		}
	}
	
	public Font sysFont() {
		if(this.type == TYPE_SYSTEM) return this.sysFont;
		else throw new IllegalStateException("Illegal font type.");
	}
	
	public byte[] getCharData(char ch) {
		if(this.type == TYPE_SYSTEM) throw new IllegalStateException("Illegal font type.");
		else {
			char nch = ch < 128 ? ch : 127;
			byte[] data = new byte[this.embedData[nch].length];
			System.arraycopy(this.embedData[nch], 0, data, 0, this.embedData[nch].length);
			return data;
		}
	}
	
}
