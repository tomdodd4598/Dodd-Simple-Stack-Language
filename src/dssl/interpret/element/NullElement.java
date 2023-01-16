package dssl.interpret.element;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.element.primitive.StringElement;

public class NullElement extends Element {
	
	public static final @NonNull NullElement INSTANCE = new NullElement();
	
	private NullElement() {
		super();
	}
	
	@Override
	public @NonNull String typeName() {
		return "null";
	}
	
	@Override
	public @NonNull StringElement stringCastExplicit() {
		throw castError("string");
	}
	
	@Override
	public @NonNull Element clone() {
		return INSTANCE;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash("null");
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj == INSTANCE;
	}
	
	@Override
	public @NonNull String toString() {
		return "null";
	}
	
	@Override
	public @NonNull String toDebugString() {
		return "null";
	}
}
