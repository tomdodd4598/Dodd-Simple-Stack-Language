package dssl.interpret;

public enum InterpretResult {
	PASS("pass"),
	CONTINUE("continue"),
	BREAK("break"),
	QUIT("quit");
	
	private final String string;
	
	private InterpretResult(String string) {
		this.string = string;
	}
	
	@Override
	public String toString() {
		return string;
	}
}
