package dssl.interpret.element;

import java.io.Serializable;
import java.util.Objects;

import org.apache.commons.lang3.SerializationUtils;
import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.BuiltIn;

public class NativeElement extends Element {
	
	public final Object value;
	
	public NativeElement(Object value) {
		super(BuiltIn.NATIVE_CLAZZ);
		this.value = value;
	}
	
	@Override
	public @NonNull Element clone() {
		if (value instanceof Serializable) {
			return new NativeElement(SerializationUtils.clone((Serializable) value));
		}
		else {
			throw new IllegalArgumentException(String.format("Non-serializable %s element can not be cloned!", BuiltIn.NATIVE));
		}
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(BuiltIn.NATIVE, value);
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
		return String.valueOf(value);
	}
}
