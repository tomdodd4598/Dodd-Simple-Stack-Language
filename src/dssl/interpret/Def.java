package dssl.interpret;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.element.Element;
import dssl.interpret.element.ref.DefRefElement;

public class Def implements ScopeVariable {
	
	public final @NonNull String identifier;
	public final @NonNull Element elem;
	
	public Def(@NonNull String identifier, @NonNull Element elem) {
		this.identifier = identifier;
		this.elem = elem;
	}
	
	@Override
	public @NonNull Element getRefElement() {
		return new DefRefElement(this);
	}
}
