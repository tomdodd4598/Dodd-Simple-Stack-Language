package dssl.interpret.element;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

public class RBracketElement extends Element {
	
	public RBracketElement() {
		super();
	}
	
	@Override
	public @NonNull String typeName() {
		return "rbracket";
	}
	
	@Override
	public @NonNull Element clone() {
		return new RBracketElement();
	}
	
	@Override
	public int hashCode() {
		return Objects.hash("]rbracket");
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof RBracketElement;
	}
	
	@Override
	public @NonNull String toString() {
		return "]rbracket";
	}
	
	@Override
	public @NonNull String debugString() {
		return "]";
	}
}
