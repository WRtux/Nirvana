package wapi.betabase.element;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public final class ElementBin extends ElementOnymous {
	
	public static final byte TYPE_BINARY = 0x00;
	public static final byte TYPE_STRING = 0x01;
	
	public byte type;
	public byte[] data;
	
	public ElementBin(String name, byte type, int len) {
		super(Element.SYM_BIN, name);
		this.type = type;
		this.data = new byte[len];
	}
	public ElementBin(String name, byte type) {
		this(name, type, 0);
	}
	public ElementBin(String name) {
		this(name, TYPE_BINARY);
	}
	
	public ElementBin(String name, int[] data) {
		this(name, TYPE_BINARY, 4 * data.length);
		for(int i = 0; i < data.length; i++) {
			int offset = 4 * i;
			this.data[offset++] = (byte)(data[i] >> 24);
			this.data[offset++] = (byte)(data[i] >> 16);
			this.data[offset++] = (byte)(data[i] >> 8);
			this.data[offset++] = (byte)data[i];
		}
	}
	public ElementBin(String name, String text) {
		this(name, TYPE_STRING);
		try {
			this.data = text.getBytes("UTF-8");
		} catch(UnsupportedEncodingException ex) {
			throw new RuntimeException("UTF-8 not supported by system.");
		}
	}
	
	public void concretize(DataInputStream stream) throws IOException {
		this.type = stream.readByte();
		this.data = new byte[stream.readInt()];
		int len = stream.read(this.data);
		if(len != this.data.length) throw new IllegalArgumentException();
	}
	
	public void serialize(DataOutputStream stream) throws IOException {
		stream.writeByte(this.type);
		stream.writeInt(this.data.length);
		stream.write(this.data);
	}
	
	public int getSize() {
		return this.data.length;
	}
	
	public byte byteAt(int index) {
		return this.data[index];
	}
	public String getString() {
		try {
			return new String(data, "UTF-8");
		} catch(UnsupportedEncodingException ex) {
			throw new RuntimeException("UTF-8 not supported by system.");
		}
	}
	
}
