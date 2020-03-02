package wapi.betabase.element;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public final class ElementVoid extends Element {
	
	public ElementVoid() {
		super(Element.SYM_VOID);
	}
	
	public void concretize(DataInputStream stream) {}
	public void serialize(DataOutputStream stream) {}
	
}
