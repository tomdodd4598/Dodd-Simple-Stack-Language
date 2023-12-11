package dssl.interpret.element.primitive;

import java.math.BigInteger;
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import dssl.Helpers;
import dssl.interpret.*;
import dssl.interpret.element.Element;
import dssl.interpret.value.IntValue;

public class IntElement extends PrimitiveElement<@NonNull BigInteger, @NonNull IntValue> {
	
	public IntElement(Interpreter interpreter, BigInteger rawValue) {
		super(interpreter, interpreter.builtIn.intClazz, new IntValue(Helpers.checkNonNull(rawValue)));
	}
	
	public IntElement(Interpreter interpreter, long rawValue) {
		this(interpreter, BigInteger.valueOf(rawValue));
	}
	
	@Override
	public @NonNull IntElement intCast(TokenExecutor exec) {
		return this;
	}
	
	@Override
	public @NonNull TokenResult onNot(TokenExecutor exec) {
		exec.push(new IntElement(interpreter, value.raw.not()));
		return TokenResult.PASS;
	}
	
	public int primitiveInt() {
		return value.raw.intValueExact();
	}
	
	public long primitiveLong() {
		return value.raw.longValueExact();
	}
	
	@Override
	public @NonNull Element clone() {
		return new IntElement(interpreter, value.raw);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(BuiltIn.INT, value);
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
