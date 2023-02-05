package dssl.interpret;

public interface Module {
	
	public TokenResult onInclude(TokenExecutor exec);
	
	public TokenResult onImport(TokenExecutor exec);
}
