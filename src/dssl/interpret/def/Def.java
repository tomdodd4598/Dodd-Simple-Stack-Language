package dssl.interpret.def;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.element.Element;

public abstract class Def<T extends @NonNull Element> {
	
	public final @NonNull String identifier;
	
	protected Def(@NonNull String identifier) {
		this.identifier = identifier;
	}
	
	public abstract T getElement();
}
