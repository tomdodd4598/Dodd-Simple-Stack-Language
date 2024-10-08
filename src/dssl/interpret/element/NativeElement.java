package dssl.interpret.element;

import java.io.Serializable;
import java.util.Objects;

import org.apache.commons.lang3.SerializationUtils;
import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;

public class NativeElement extends Element {
	
	public final @NonNull Object value;
	
	public NativeElement(Interpreter interpreter, @NonNull Object value) {
		super(interpreter, interpreter.builtIn.nativeClazz);
		this.value = value;
	}
	
	@Override
	public @NonNull Element clone() {
		if (value instanceof Serializable serializable) {
			return new NativeElement(interpreter, SerializationUtils.clone(serializable));
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
		if (obj instanceof NativeElement other) {
			return value.equals(other.value);
		}
		return false;
	}
	
	@SuppressWarnings("null")
	@Override
	public @NonNull String toString(TokenExecutor exec) {
		return String.valueOf(value);
	}
}
