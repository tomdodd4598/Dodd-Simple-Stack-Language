package dssl.interpret;

import org.eclipse.jdt.annotation.NonNull;

public class Magic {
	
	public final @NonNull String identifier;
	public final @NonNull Invokable invokable;
	
	public Magic(@NonNull String identifier, @NonNull Invokable invokable) {
		this.identifier = identifier;
		this.invokable = invokable;
	}
}
