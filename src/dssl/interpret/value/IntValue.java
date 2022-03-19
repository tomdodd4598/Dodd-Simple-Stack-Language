package dssl.interpret.value;

import java.math.BigInteger;
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

public class IntValue extends PrimitiveValue<@NonNull BigInteger> {
	
	public IntValue(@NonNull BigInteger value) {
		super(value);
	}
	
	@Override
	public BigInteger intValue(boolean explicit) {
		return raw;
	}
	
	@Override
	public Boolean boolValue(boolean explicit) {
		return explicit ? !raw.equals(BigInteger.ZERO) : null;
	}
	
	@Override
	public Double floatValue(boolean explicit) {
		return raw.doubleValue();
	}
	
	@Override
	public Character charValue(boolean explicit) {
		return explicit ? (char) raw.intValue() : null;
	}
	
	@Override
	public String stringValue(boolean explicit) {
		return explicit ? raw.toString() : null;
	}
	
	@Override
	public IntValue clone() {
		return new IntValue(raw);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash("int", raw);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IntValue) {
			IntValue other = (IntValue) obj;
			return raw.equals(other.raw);
		}
		return false;
	}
	
	@SuppressWarnings("null")
	@Override
	public @NonNull String toString() {
		return raw.toString();
	}
}
