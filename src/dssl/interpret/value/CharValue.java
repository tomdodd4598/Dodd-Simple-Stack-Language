package dssl.interpret.value;

import java.math.BigInteger;
import java.util.Objects;

import org.eclipse.jdt.annotation.*;

import dssl.interpret.BuiltIn;

public class CharValue extends PrimitiveValue<@NonNull Character> {
	
	public CharValue(@NonNull Character value) {
		super(value);
	}
	
	@Override
	public @Nullable BigInteger intValue(boolean explicit) {
		return explicit ? BigInteger.valueOf(raw.charValue()) : null;
	}
	
	@Override
	public @Nullable Boolean boolValue(boolean explicit) {
		return null;
	}
	
	@Override
	public @Nullable Double floatValue(boolean explicit) {
		return null;
	}
	
	@Override
	public @Nullable Character charValue(boolean explicit) {
		return raw;
	}
	
	@Override
	public @Nullable String stringValue(boolean explicit) {
		return explicit ? raw.toString() : null;
	}
	
	@Override
	public CharValue clone() {
		return new CharValue(raw.charValue());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(BuiltIn.CHAR, raw);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CharValue other) {
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
