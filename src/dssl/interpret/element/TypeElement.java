package dssl.interpret.element;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.element.primitive.StringElement;

public class TypeElement extends Element {
	
	public final @NonNull Element internal;
	
	public TypeElement(@NonNull Element internal) {
		super();
		this.internal = internal;
	}
	
	@Override
	public @NonNull String typeName() {
		return "type";
	}
	
	@Override
	public @NonNull StringElement stringCastExplicit() {
		return new StringElement(internal.typeName());
	}
	
	@Override
	public @NonNull Element clone() {
		return new TypeElement(internal.clone());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash("type", internal.typeName());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TypeElement) {
			TypeElement other = (TypeElement) obj;
			return internal.typeName().equals(other.internal.typeName());
		}
		return false;
	}
	
	@Override
	public @NonNull String toString() {
		return "type:" + internal.typeName();
	}
	
	@Override
	public @NonNull String toDebugString() {
		return "type:" + internal.typeName();
	}
}
