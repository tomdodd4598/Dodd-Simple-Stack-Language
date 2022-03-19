package dssl.interpret.element.bracket;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.element.Element;
import dssl.interpret.element.value.primitive.StringElement;

public class RBracketElement extends BracketElement {
	
	public RBracketElement() {
		super();
	}
	
	@Override
	public @NonNull String typeName() {
		return "rbracket";
	}
	
	@Override
	public @NonNull StringElement stringCastExplicit() {
		return new StringElement("]");
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
	public @NonNull String toBriefDebugString() {
		return "]";
	}
}
