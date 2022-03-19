package dssl.interpret.value;

import java.math.BigInteger;
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

public class StringValue extends PrimitiveValue<@NonNull String> {
	
	public StringValue(@NonNull String value) {
		super(value);
	}
	
	@Override
	public BigInteger intValue(boolean explicit) {
		if (explicit) {
			BigInteger intValue = null;
			try {
				intValue = new BigInteger(raw);
			}
			catch (Exception e) {}
			return intValue;
		}
		return null;
	}
	
	@Override
	public Boolean boolValue(boolean explicit) {
		return explicit ? (raw.equals("true") ? Boolean.TRUE : (raw.equals("false") ? Boolean.FALSE : null)) : null;
	}
	
	@Override
	public Double floatValue(boolean explicit) {
		if (explicit) {
			Double floatValue = null;
			try {
				floatValue = Double.parseDouble(raw);
			}
			catch (Exception e) {}
			return floatValue;
		}
		return null;
	}
	
	@Override
	public Character charValue(boolean explicit) {
		return explicit ? (raw.length() == 1 ? raw.charAt(0) : null) : null;
	}
	
	@Override
	public String stringValue(boolean explicit) {
		return raw;
	}
	
	@Override
	public StringValue clone() {
		return new StringValue(raw);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash("string", raw);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StringValue) {
			StringValue other = (StringValue) obj;
			return raw.equals(other.raw);
		}
		return false;
	}
	
	@Override
	public @NonNull String toString() {
		return raw;
	}
}
