package wapi.betabase.element;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

public final class ElementArray extends ElementCompound {
	
	public byte subtype;
	
	public ElementArray(String name, byte subtype, int eleNum) {
		super(name, Element.SYM_ARRAY, eleNum);
		this.subtype = subtype;
	}
	public ElementArray(String name, byte subtype) {
		this(name, subtype, 0);
	}
	public ElementArray(String name) {
		this(name, Element.SYM_VOID);
	}
	
	public ElementArray(String name, Element[] elements) {
		this(name, elements[0].symbol, elements.length);
		Element ele;
		for(int i = 0; i < elements.length; i++)
			if((ele = elements[i]).symbol == this.subtype) this.elements.addElement(ele);
			else throw new IllegalArgumentException("Type 0x" + Integer.toHexString(ele.symbol) + " mismatch.");
	}
	
	public void concretize(DataInputStream stream) throws IOException {
		this.subtype = stream.readByte();
		int eleNum;
		this.elements = new Vector(eleNum = stream.readShort());
		for(int i = 0; i < eleNum; i++)
			this.elements.addElement(Element.constructElement(stream, this.subtype, i));
	}
	
	public void serialize(DataOutputStream stream) throws IOException {
		stream.writeByte(this.subtype);
		int eleNum = this.elements.size();
		stream.writeShort(eleNum);
		Element ele;
		for(int i = 0; i < eleNum; i++)
			if((ele = (Element)this.elements.elementAt(i)).symbol == this.subtype)
				Element.exportElement(stream, ele, false, false);
			else throw new IllegalArgumentException("Type 0x" + Integer.toHexString(ele.symbol) + " mismatch.");
	}
	
	public void push(Element element) {
		if(element.symbol == this.symbol) this.elements.addElement(element);
		else throw new IllegalArgumentException("Type 0x" + Integer.toHexString(element.symbol) + " mismatch.");
	}
	
	public Element firstOf(String name) {
		if(this.elements.isEmpty()) return null;
		Element ele = (Element)this.elements.firstElement();
		if(!(ele instanceof ElementOnymous)) return null;
		for(int i = 0, size = elements.size(); i < size; i++) {
			ele = (Element)this.elements.elementAt(i);
			if(((ElementOnymous)ele).name.equalsIgnoreCase(name)) return ele;
		}
		return null;
	}
	
}
