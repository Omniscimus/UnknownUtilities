package net.omniscimus.unknownutilities;

public abstract class UnknownFeature {
	
	public abstract boolean enable();
	public abstract boolean disable();
	
	public void reload() {
		disable();
		enable();
	}
	
}
