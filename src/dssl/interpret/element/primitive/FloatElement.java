package dssl.interpret.element.primitive;

import java.math.BigDecimal;
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.Element;
import dssl.interpret.value.FloatValue;

public class FloatElement extends PrimitiveElement<@NonNull Double, @NonNull FloatValue> {
	
	public FloatElement(@NonNull Double rawValue) {
		super(BuiltIn.FLOAT_CLAZZ, new FloatValue(rawValue));
	}
	
	@Override
	public FloatElement floatCast(boolean explicit) {
		return this;
	}
	
	@Override
	public TokenResult onNot(TokenExecutor exec) {
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
		return new FloatElement(primitiveFloat());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash("float", value);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FloatElement) {
			FloatElement other = (FloatElement) obj;
			return value.equals(other.value);
		}
		return false;
	}
}
