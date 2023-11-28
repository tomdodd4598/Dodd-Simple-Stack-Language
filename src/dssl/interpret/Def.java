package dssl.interpret;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.element.Element;

public class Def {
	
	public final @NonNull String identifier;
	public final @NonNull Element elem;
	
	public Def(@NonNull String identifier, @NonNull Element elem) {
		this.identifier = identifier;
		this.elem = elem;
	}
}
