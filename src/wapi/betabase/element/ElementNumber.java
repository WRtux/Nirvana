package wapi.betabase.element;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class ElementNumber extends ElementOnymous {
	
	public int value;
	
	public ElementNumber(String name, byte type) {
		super(type, name);
		if(type != Element.SYM_BYTE && type != Element.SYM_SHORT && type != Element.SYM_INT)
			throw new IllegalArgumentException("Element type 0x" + Integer.toHexString(type) + " not numberic.");
	}
	
	public ElementNumber(String name, byte type, int value) {
		this(name, type);
		this.value = value;
	}
	
	public void concretize(DataInputStream stream) throws IOException {
		switch(this.symbol) {
		case Element.SYM_BYTE:
			this.value = stream.readByte();
			break;
		case Element.SYM_SHORT:
			this.value = stream.readShort();
			break;
		case Element.SYM_INT:
			this.value = stream.readInt();
			break;
		default:
			throw new IllegalArgumentException("Illegal numberic symbol 0x" + Integer.toHexString(this.symbol) + ".");
		}
	}
	
	public void serialize(DataOutputStream stream) throws IOException {
		switch(this.symbol) {
		case Element.SYM_BYTE:
			stream.writeByte(this.value);
			break;
		case Element.SYM_SHORT:
			stream.writeShort(this.value);
			break;
		case Element.SYM_INT:
			stream.writeInt(this.value);
			break;
		default:
			throw new IllegalArgumentException("Illegal numberic symbol 0x" + Integer.toHexString(this.symbol) + ".");
		}
	}
	
	public byte getByte() {
		return (byte)this.value;
	}
	public short getShort() {
		return (short)this.value;
	}
	public int getInt() {
		return (int)this.value;
	}
	
}
