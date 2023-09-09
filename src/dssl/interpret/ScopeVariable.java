package dssl.interpret;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.element.Element;

public interface ScopeVariable {
	
	public @NonNull Element getRefElement();
}
