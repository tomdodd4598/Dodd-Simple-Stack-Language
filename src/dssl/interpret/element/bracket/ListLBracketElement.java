package dssl.interpret.element.bracket;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.element.Element;

public class ListLBracketElement extends LBracketElement {
	
	public static final @NonNull ListLBracketElement INSTANCE = new ListLBracketElement();
	
	private ListLBracketElement() {
		super();
	}
	
	@Override
	public @NonNull Element clone() {
		return INSTANCE;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash("[");
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj == INSTANCE;
	}
	
	@Override
	public @NonNull String toString() {
		return "[";
	}
}
