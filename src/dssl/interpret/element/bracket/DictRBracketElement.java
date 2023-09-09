package dssl.interpret.element.bracket;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.element.Element;

public class DictRBracketElement extends RBracketElement {
	
	public static final @NonNull DictRBracketElement INSTANCE = new DictRBracketElement();
	
	private DictRBracketElement() {
		super();
	}
	
	@Override
	public @NonNull Element clone() {
		return INSTANCE;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash("|]");
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj == INSTANCE;
	}
	
	@Override
	public @NonNull String toString() {
		return "|]";
	}
}
