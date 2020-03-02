package wapi.betabase.element;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class Element {
	
	public static final byte SYM_VOID = 0x00;
	
	public static final byte SYM_COMPOUND = 0x01;
	public static final byte SYM_ARRAY = 0x02;
	
	public static final byte SYM_BOOLEAN = 0x04;
	public static final byte SYM_BYTE = 0x09;
	public static final byte SYM_SHORT = 0x0B;
	public static final byte SYM_INT = 0x0D;
	
	public static final byte SYM_BIN = 0x10;
	
	public static final byte DATA_UNKOWN = -1;
	
	public final byte symbol;
	
	public static final Element constructElement(DataInputStream stream, byte symbol, int index) throws IOException {
		symbol = (symbol == DATA_UNKOWN) ? stream.readByte() : symbol;
		String name = null;
		if(symbol != SYM_VOID) name = (index == DATA_UNKOWN) ? stream.readUTF() : Integer.toString(index);
		Element ele;
		switch(symbol) {
		case Element.SYM_VOID:
			ele = new ElementVoid();
			break;
		case Element.SYM_COMPOUND:
			ele = new ElementCompound(name);
			break;
		case Element.SYM_ARRAY:
			ele = new ElementArray(name);
			break;
		case Element.SYM_BOOLEAN:
			ele = null;
			break;
		case Element.SYM_BYTE:
		case Element.SYM_SHORT:
		case Element.SYM_INT:
			ele = new ElementNumber(name, symbol);
			break;
		case Element.SYM_BIN:
			ele = new ElementBin(name);
			break;
		default:
			throw new IllegalArgumentException("Element type 0x" + Integer.toHexString(symbol) + " not supported.");
		}
		ele.concretize(stream);
		return ele;
	}
	
	public static final void exportElement(DataOutputStream stream, Element element, boolean symbolic, boolean onymous) throws IOException {
		if(symbolic) stream.writeByte(element.symbol);
		if(element instanceof ElementOnymous && onymous) stream.writeUTF(((ElementOnymous)element).name);
		element.serialize(stream);
	}
	
	public Element(byte symbol) {
		this.symbol = symbol;
	}
	
	public abstract void concretize(DataInputStream stream) throws IOException;
	public abstract void serialize(DataOutputStream stream) throws IOException;
	
}

abstract class ElementOnymous extends Element {
	
	public String name;
	
	public ElementOnymous(byte symbol, String name) {
		super(symbol);
		if(name == null) throw new NullPointerException("Null element name.");
		this.name = name;
	}
	
}
