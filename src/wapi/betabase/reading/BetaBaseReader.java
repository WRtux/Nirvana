package wapi.betabase.reading;

import wapi.betabase.BetaBase;
import wapi.betabase.element.ElementCompound;

public final class BetaBaseReader {
	
	protected BetaBase base;
	
	public BetaBaseReader(BetaBase base) {
		if(base == null) throw new NullPointerException();
		this.base = base;
	}
	public BetaBaseReader() {
		this(new BetaBase());
	}
	
	public String readInfo() {
		return this.base.info;
	}
	public ElementCompound readContainer() {
		return this.base.container;
	}
	
	public CompoundReader with() {
		return new CompoundReader(this.base.container);
	}
	
}
