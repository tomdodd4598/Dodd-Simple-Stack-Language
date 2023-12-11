package dssl.interpret.element.primitive;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.Element;
import dssl.interpret.value.BoolValue;

public class BoolElement extends PrimitiveElement<@NonNull Boolean, @NonNull BoolValue> {
	
	public BoolElement(Interpreter interpreter, @NonNull Boolean rawValue) {
		super(interpreter, interpreter.builtIn.boolClazz, new BoolValue(rawValue));
	}
	
	@Override
	public @NonNull BoolElement boolCast(TokenExecutor exec) {
		return this;
	}
	
	@Override
	public @NonNull TokenResult onNot(TokenExecutor exec) {
		exec.push(new BoolElement(interpreter, !primitiveBool()));
		return TokenResult.PASS;
	}
	
	public boolean primitiveBool() {
		return value.raw.booleanValue();
	}
	
	@Override
	public @NonNull Element clone() {
		return new BoolElement(interpreter, primitiveBool());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(BuiltIn.BOOL, value);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BoolElement) {
			BoolElement other = (BoolElement) obj;
			return value.equals(other.value);
		}
		return false;
	}
}
