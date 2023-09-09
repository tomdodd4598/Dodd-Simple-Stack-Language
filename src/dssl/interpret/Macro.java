package dssl.interpret;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.element.Element;
import dssl.interpret.element.ref.MacroRefElement;

public class Macro implements ScopeVariable {
	
	public final @NonNull String identifier;
	public final @NonNull Invokable invokable;
	
	public Macro(@NonNull String identifier, @NonNull Invokable invokable) {
		this.identifier = identifier;
		this.invokable = invokable;
	}
	
	@Override
	public @NonNull Element getRefElement() {
		return new MacroRefElement(this);
	}
}
