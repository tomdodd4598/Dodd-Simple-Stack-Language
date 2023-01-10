package dssl.interpret.def;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.element.Element;

public class VariableDef extends Def<@NonNull Element> {
	
	protected final @NonNull Element elem;
	
	public VariableDef(@NonNull String identifier, @NonNull Element elem) {
		super(identifier);
		this.elem = elem;
	}
	
	@Override
	public @NonNull Element getElement() {
		return elem;
	}
}
