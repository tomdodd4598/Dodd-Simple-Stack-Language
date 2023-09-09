package dssl.interpret;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.element.Element;
import dssl.interpret.element.ref.ConstRefElement;

public class Const implements ScopeVariable {
	
	public final @NonNull String identifier;
	public final @NonNull Element elem;
	
	public Const(@NonNull String identifier, @NonNull Element elem) {
		this.identifier = identifier;
		this.elem = elem;
	}
	
	@Override
	public @NonNull Element getRefElement() {
		return new ConstRefElement(this);
	}
}
