package dssl.interpret.element;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.element.value.primitive.StringElement;

public class LabelElement extends Element {
	
	public final @NonNull String identifier;
	
	public LabelElement(@NonNull String identifier) {
		super();
		this.identifier = identifier;
	}
	
	@Override
	public @NonNull String typeName() {
		return "label";
	}
	
	@Override
	public @NonNull StringElement stringCastExplicit() {
		return new StringElement(identifier);
	}
	
	@Override
	public @NonNull Element clone() {
		return new LabelElement(identifier);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash("label", identifier);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LabelElement) {
			LabelElement other = (LabelElement) obj;
			return identifier.equals(other.identifier);
		}
		return false;
	}
	
	@Override
	public @NonNull String toString() {
		return "label:" + identifier;
	}
	
	@Override
	public @NonNull String toBriefDebugString() {
		return "/" + identifier;
	}
}
