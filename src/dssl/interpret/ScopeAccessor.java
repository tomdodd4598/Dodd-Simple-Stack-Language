package dssl.interpret;

import org.eclipse.jdt.annotation.*;

public interface ScopeAccessor {
	
	public @Nullable TokenResult scopeAction(TokenExecutor exec, @NonNull String identifier);
}
