package wapi.betabase.reading;

import java.util.Vector;

import wapi.betabase.element.ElementArray;
import wapi.betabase.element.ElementBin;
import wapi.betabase.element.ElementCompound;
import wapi.betabase.element.ElementNumber;

public final class CompoundReader {
	
	protected CompoundReader upper;
	protected ElementCompound compound;
	
	protected int index;
	
	public CompoundReader(CompoundReader upper, ElementCompound compound) {
		if(compound == null) throw new NullPointerException();
		this.upper = upper;
		this.compound = compound;
	}
	public CompoundReader(ElementCompound compound) {
		this(null, compound);
	}
	
	public int getIndex() {
		return this.index;
	}
	public void mark(int index) {
		if(compound.elementAt(index) != null) this.index = index;
	}
	
	public int getUnreadNum() {
		return this.compound.getElementNum() - this.index;
	}
	
	public int readNumber() {
		return ((ElementNumber)this.compound.elementAt(index++)).value;
	}
	
	public byte[] readBin() {
		return ((ElementBin)this.compound.elementAt(index++)).data;
	}
	public String readString() {
		return ((ElementBin)this.compound.elementAt(index++)).getString();
	}
	
	public Vector readArray() {
		return ((ElementArray)this.compound.elementAt(index++)).elements;
	}
	
	public CompoundReader with() {
		return new CompoundReader(this, (ElementCompound)this.compound.elementAt(this.index++));
	}
	public CompoundReader upper() {
		return this.upper;
	}
	
}
