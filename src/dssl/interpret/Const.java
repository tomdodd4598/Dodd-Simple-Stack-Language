package dssl.interpret;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.element.Element;

public class Const {
	
	public final @NonNull String identifier;
	public final @NonNull Element elem;
	
	public Const(@NonNull String identifier, @NonNull Element elem) {
		this.identifier = identifier;
		this.elem = elem;
	}
}
