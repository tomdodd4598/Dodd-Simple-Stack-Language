package dssl.interpret;

public enum TokenResult {
	PASS("pass"),
	CONTINUE("continue"),
	BREAK("break"),
	QUIT("quit");
	
	private final String string;
	
	private TokenResult(String string) {
		this.string = string;
	}
	
	@Override
	public String toString() {
		return string;
	}
}
