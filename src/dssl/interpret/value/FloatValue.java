package dssl.interpret.value;

import java.math.BigInteger;
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import dssl.Helpers;

public class FloatValue extends PrimitiveValue<@NonNull Double> {
	
	public FloatValue(@NonNull Double value) {
		super(value);
	}
	
	@Override
	public BigInteger intValue(boolean explicit) {
		return explicit ? Helpers.bigIntFromDouble(raw) : null;
	}
	
	@Override
	public Boolean boolValue(boolean explicit) {
		return null;
	}
	
	@Override
	public Double floatValue(boolean explicit) {
		return raw;
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
	public FloatValue clone() {
		return new FloatValue(raw.doubleValue());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash("float", raw);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FloatValue) {
			FloatValue other = (FloatValue) obj;
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
