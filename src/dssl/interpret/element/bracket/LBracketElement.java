package dssl.interpret.element.bracket;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.element.Element;
import dssl.interpret.element.primitive.StringElement;

public class LBracketElement extends BracketElement {
	
	public LBracketElement() {
		super();
	}
	
	@Override
	public @NonNull String typeName() {
		return "lbracket";
	}
	
	@Override
	public @NonNull StringElement stringCastExplicit() {
		return new StringElement("[");
	}
	
	@Override
	public @NonNull Element clone() {
		return new LBracketElement();
	}
	
	@Override
	public int hashCode() {
		return Objects.hash("[lbracket");
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof LBracketElement;
	}
	
	@Override
	public @NonNull String toString() {
		return "[lbracket";
	}
	
	@Override
	public @NonNull String toDebugString() {
		return "[";
	}
}
