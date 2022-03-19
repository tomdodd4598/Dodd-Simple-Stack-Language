package dssl.interpret.value;

import java.math.BigInteger;
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

public class BoolValue extends PrimitiveValue<@NonNull Boolean> {
	
	public BoolValue(@NonNull Boolean value) {
		super(value);
	}
	
	@Override
	public BigInteger intValue(boolean explicit) {
		return explicit ? (raw ? BigInteger.ONE : BigInteger.ZERO) : null;
	}
	
	@Override
	public Boolean boolValue(boolean explicit) {
		return raw;
	}
	
	@Override
	public Double floatValue(boolean explicit) {
		return null;
	}
	
	@Override
	public Character charValue(boolean explicit) {
		return null;
	}
	
	@Override
	public String stringValue(boolean explicit) {
		return explicit ? raw.toString() : null;
	}
	
	@Override
	public BoolValue clone() {
		return new BoolValue(raw.booleanValue());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash("bool", raw);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BoolValue) {
			BoolValue other = (BoolValue) obj;
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
