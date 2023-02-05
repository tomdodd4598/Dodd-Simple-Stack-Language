package dssl.interpret;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.element.BlockElement;

public class Magic {
	
	public final @NonNull String identifier;
	public final @NonNull BlockElement block;
	
	public Magic(@NonNull String identifier, @NonNull BlockElement block) {
		this.identifier = identifier;
		this.block = block;
	}
}
