package dssl.interpret;

import java.nio.file.Path;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.element.BlockElement;

public interface Hooks {
	
	public void print(String str);
	
	public void debug(String str);
	
	public String read();
	
	public @NonNull TokenResult onInclude(TokenExecutor exec);
	
	public @NonNull TokenResult onImport(TokenExecutor exec);
	
	public @NonNull TokenResult onNative(TokenExecutor exec);
	
	public TokenIterator getBlockIterator(TokenExecutor exec, @NonNull BlockElement block);
	
	public Path getRootPath(TokenExecutor exec);
}
