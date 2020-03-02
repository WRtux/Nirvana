package wapi.betabase;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wapi.betabase.element.Element;
import wapi.betabase.element.ElementCompound;

public final class BetaBase {
	
	public String info;
	public ElementCompound container = new ElementCompound("root");
	
	public BetaBase(String info) {
		this.info = info;
	}
	public BetaBase() {
		this("");
	}
	
	public static BetaBase constructBase(DataInputStream stream) throws IOException {
		stream.mark(0);
		if(stream.readBoolean()) throw new IllegalStateException("Encrypted base not supported.");
		if(stream.readInt() != 0xDEAD1989) throw new IllegalArgumentException("Bad check code.");
		BetaBase base = new BetaBase(stream.readUTF());
		int eleNum = stream.readShort();
		base.container = new ElementCompound("root", eleNum);
		for(int i = 0; i < eleNum; i++)
			base.container.push(Element.constructElement(stream, Element.DATA_UNKOWN, Element.DATA_UNKOWN));
		return base;
	}
	
	public static void exportBase(DataOutputStream stream, BetaBase base) throws IOException {
		stream.writeBoolean(false);
		stream.writeInt(0xDEAD1989);
		stream.writeUTF(base.info);
		int eleNum = base.container.getElementNum();
		stream.writeShort(eleNum);
		for(int i = 0; i < eleNum; i++)
			Element.exportElement(stream, base.container.elementAt(i), true, true);
	}
	
}
