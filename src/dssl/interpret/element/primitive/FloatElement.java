package dssl.interpret.element.primitive;

import java.math.BigDecimal;
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.Element;
import dssl.interpret.value.FloatValue;

public class FloatElement extends PrimitiveElement<@NonNull Double, @NonNull FloatValue> {
	
	public FloatElement(Interpreter interpreter, @NonNull Double rawValue) {
		super(interpreter, interpreter.builtIn.floatClazz, new FloatValue(rawValue));
	}
	
	@Override
	public @NonNull FloatElement floatCast(TokenExecutor exec) {
		return this;
	}
	
	@Override
	public @NonNull TokenResult onNot(TokenExecutor exec) {
		throw unaryOpError("!");
	}
	
	public double primitiveFloat() {
		return value.raw.doubleValue();
	}
	
	public @NonNull BigDecimal bigFloat() {
		return new BigDecimal(primitiveFloat());
	}
	
	@Override
	public @NonNull Element clone() {
		return new FloatElement(interpreter, value.raw);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(BuiltIn.FLOAT, value);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FloatElement other) {
			return value.equals(other.value);
		}
		return false;
	}
}
