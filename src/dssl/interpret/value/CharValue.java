package dssl.interpret.value;

import java.math.BigInteger;
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

public class CharValue extends PrimitiveValue<@NonNull Character> {
	
	public CharValue(@NonNull Character value) {
		super(value);
	}
	
	@Override
	public BigInteger intValue(boolean explicit) {
		return explicit ? BigInteger.valueOf(raw.charValue()) : null;
	}
	
	@Override
	public Boolean boolValue(boolean explicit) {
		return null;
	}
	
	@Override
	public Double floatValue(boolean explicit) {
		return null;
	}
	
	@Override
	public Character charValue(boolean explicit) {
		return raw;
	}
	
	@Override
	public String stringValue(boolean explicit) {
		return explicit ? raw.toString() : null;
	}
	
	@Override
	public CharValue clone() {
		return new CharValue(raw.charValue());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash("char", raw);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CharValue) {
			CharValue other = (CharValue) obj;
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
