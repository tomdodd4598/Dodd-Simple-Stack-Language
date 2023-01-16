package dssl.interpret.magic;

import org.eclipse.jdt.annotation.NonNull;

public abstract class Magic {
	
	public final @NonNull String identifier;
	
	protected Magic(@NonNull String identifier) {
		this.identifier = identifier;
	}
}
