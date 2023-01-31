package dssl.interpret;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.element.BlockElement;

public class Macro {
	
	public final @NonNull String identifier;
	public final @NonNull BlockElement block;
	
	public Macro(@NonNull String identifier, @NonNull BlockElement block) {
		this.identifier = identifier;
		this.block = block;
	}
}
