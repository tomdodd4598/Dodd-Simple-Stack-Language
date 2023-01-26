package dssl.interpret.element;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.element.primitive.StringElement;

public class NativeElement extends Element {
	
	public final Object value;
	
	public NativeElement(Object value) {
		this.value = value;
	}
	
	@Override
	public @NonNull String typeName() {
		return "native";
	}
	
	@Override
	public @NonNull StringElement stringCastExplicit() {
		return new StringElement(toString());
	}
	
	@Override
	public @NonNull Element clone() {
		throw keywordError("clone");
	}
	
	@Override
	public int hashCode() {
		return Objects.hash("native", value);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NativeElement) {
			NativeElement other = (NativeElement) obj;
			return value.equals(other.value);
		}
		return false;
	}
	
	@SuppressWarnings("null")
	@Override
	public @NonNull String toString() {
		return value.toString();
	}
	
	@SuppressWarnings("null")
	@Override
	public @NonNull String toDebugString() {
		return value.toString();
	}
}
