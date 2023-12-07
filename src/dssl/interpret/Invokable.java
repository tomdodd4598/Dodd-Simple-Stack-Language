package dssl.interpret;

import org.eclipse.jdt.annotation.NonNull;

public interface Invokable {
	
	public @NonNull TokenResult invoke(TokenExecutor exec);
}
