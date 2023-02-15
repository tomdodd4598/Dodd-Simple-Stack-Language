package dssl.interpret.element;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

public class LBracketElement extends Element {
	
	public LBracketElement() {
		super();
	}
	
	@Override
	public @NonNull String typeName() {
		return "lbracket";
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
	public @NonNull String debugString() {
		return "[";
	}
}
