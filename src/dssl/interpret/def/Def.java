package dssl.interpret.def;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.element.Element;

public abstract class Def<T extends @NonNull Element> {
	
	public final String identifier;
	
	protected Def(String identifier) {
		this.identifier = identifier;
	}
	
	public abstract T getElement();
}
