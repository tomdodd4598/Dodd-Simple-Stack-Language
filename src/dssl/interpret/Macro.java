package dssl.interpret;

import org.eclipse.jdt.annotation.NonNull;

public class Macro {
	
	public final @NonNull String identifier;
	public final @NonNull Invokable invokable;
	
	public Macro(@NonNull String identifier, @NonNull Invokable invokable) {
		this.identifier = identifier;
		this.invokable = invokable;
	}
}
