package dssl.interpret;

public enum TokenResult {
	
	PASS("pass"),
	CONTINUE("continue"),
	BREAK("break"),
	QUIT("quit");
	
	private final String str;
	
	private TokenResult(String str) {
		this.str = str;
	}
	
	@Override
	public String toString() {
		return str;
	}
}
