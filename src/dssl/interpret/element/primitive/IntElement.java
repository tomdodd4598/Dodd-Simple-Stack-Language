package dssl.interpret.element.primitive;

import java.math.BigInteger;
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import dssl.Helpers;
import dssl.interpret.*;
import dssl.interpret.element.Element;
import dssl.interpret.value.IntValue;

public class IntElement extends PrimitiveElement<@NonNull BigInteger, @NonNull IntValue> {
	
	public IntElement(BigInteger rawValue) {
		super(BuiltIn.INT_CLAZZ, new IntValue(Helpers.checkNonNull(rawValue)));
	}
	
	@Override
	public IntElement intCast(boolean explicit) {
		return this;
	}
	
	public IntElement(long rawValue) {
		this(BigInteger.valueOf(rawValue));
	}
	
	@Override
	public TokenResult onNot(TokenExecutor exec) {
		exec.push(new IntElement(value.raw.not()));
		return TokenResult.PASS;
	}
	
	public int primitiveInt() {
		return value.raw.intValueExact();
	}
	
	@Override
	public @NonNull Element clone() {
		return new IntElement(value.raw);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash("int", value);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IntElement) {
			IntElement other = (IntElement) obj;
			return value.equals(other.value);
		}
		return false;
	}
}
