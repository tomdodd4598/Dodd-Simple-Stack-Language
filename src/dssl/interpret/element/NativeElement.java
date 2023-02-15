package dssl.interpret.element;

import java.io.Serializable;
import java.util.Objects;

import org.apache.commons.lang3.SerializationUtils;
import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.BuiltIn;
import dssl.interpret.element.primitive.StringElement;

public class NativeElement extends ValueElement {
	
	public final Object value;
	
	public NativeElement(Object value) {
		super(BuiltIn.NATIVE_CLAZZ);
		this.value = value;
	}
	
	@Override
	public StringElement stringCast(boolean explicit) {
		return explicit ? new StringElement(toString()) : null;
	}
	
	@Override
	public @NonNull Element clone() {
		if (value instanceof Serializable) {
			return new NativeElement(SerializationUtils.clone((Serializable) value));
		}
		else {
			throw new IllegalArgumentException("Non-serializable native element can not be cloned!");
		}
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
	public @NonNull String debugString() {
		return value.toString();
	}
}
