package wapi.betabase.element;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

public class ElementCompound extends ElementOnymous {
	
	public Vector elements;
	
	protected ElementCompound(String name, byte symbol, int eleNum) {
		super(symbol, name);
		this.elements = new Vector(eleNum);
	}
	
	public ElementCompound(String name, int eleNum) {
		this(name, Element.SYM_COMPOUND, eleNum);
	}
	public ElementCompound(String name) {
		this(name, 0);
	}
	
	public ElementCompound(String name, Element[] elements) {
		this(name, elements.length);
		for(int i = 0; i < elements.length; i++) this.elements.addElement(elements[i]);
	}
	
	public void concretize(DataInputStream stream) throws IOException {
		int eleNum;
		this.elements = new Vector(eleNum = stream.readShort());
		for(int i = 0; i < eleNum; i++)
			this.elements.addElement(Element.constructElement(stream, Element.DATA_UNKOWN, Element.DATA_UNKOWN));
	}
	
	public void serialize(DataOutputStream stream) throws IOException {
		int eleNum = this.elements.size();
		stream.writeShort(eleNum);
		for(int i = 0; i < eleNum; i++)
			Element.exportElement(stream, (Element)this.elements.elementAt(i), true, true);
	}
	
	public void push(Element element) {
		if(element == null) throw new NullPointerException();
		this.elements.addElement(element);
	}
	
	public final int getElementNum() {
		return this.elements.size();
	}
	
	public final Element elementAt(int index) {
		return (Element)this.elements.elementAt(index);
	}
	public Element firstOf(String name) {
		if(this.elements.isEmpty()) return null;
		Element ele;
		for(int i = 0, size = elements.size(); i < size; i++) {
			ele = (Element)this.elements.elementAt(i);
			if(ele instanceof ElementOnymous && ((ElementOnymous)ele).name.equalsIgnoreCase(name)) return ele;
		}
		return null;
	}
	
}
