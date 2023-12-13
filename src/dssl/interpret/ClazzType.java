package dssl.interpret;

public enum ClazzType {
	
	STANDARD,
	INTERNAL,
	FINAL;
	
	public boolean canExtend() {
		return equals(STANDARD);
	}
	
	public boolean canModify() {
		return equals(STANDARD);
	}
	
	public boolean canInstantiate() {
		return !equals(INTERNAL);
	}
}
