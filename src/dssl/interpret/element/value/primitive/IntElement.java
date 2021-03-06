package dssl.interpret.element.value.primitive;

import java.math.BigInteger;
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.element.Element;
import dssl.interpret.value.IntValue;

public class IntElement extends PrimitiveElement<@NonNull BigInteger> {
	
	public IntElement(@NonNull BigInteger rawValue) {
		super(new IntValue(rawValue));
	}
	
	@SuppressWarnings("null")
	public IntElement(long rawValue) {
		this(BigInteger.valueOf(rawValue));
	}
	
	@Override
	public @NonNull String typeName() {
		return "int";
	}
	
	@Override
	public Element castInternal(@NonNull Element elem) {
		return elem.intCastExplicit();
	}
	
	public int primitiveInt() {
		return value.raw.intValueExact();
	}
	
	@Override
	public @NonNull Element onNot() {
		throw unaryOpError("not");
	}
	
	@SuppressWarnings("null")
	@Override
	public @NonNull Element onNeg() {
		return new IntElement(value.raw.negate());
	}
	
	@Override
	public @NonNull Element onInv() {
		// return new IntElement(value.raw.negate().subtract(BigInteger.ONE));
		throw unaryOpError("inv");
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
