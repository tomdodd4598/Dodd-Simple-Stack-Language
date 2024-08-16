package dssl.interpret.value;

import java.math.BigInteger;
import java.util.Objects;

import org.eclipse.jdt.annotation.*;

import dssl.interpret.BuiltIn;

public class BoolValue extends PrimitiveValue<@NonNull Boolean> {
	
	public BoolValue(@NonNull Boolean value) {
		super(value);
	}
	
	@Override
	public @Nullable BigInteger intValue(boolean explicit) {
		return explicit ? (raw ? BigInteger.ONE : BigInteger.ZERO) : null;
	}
	
	@Override
	public @Nullable Boolean boolValue(boolean explicit) {
		return raw;
	}
	
	@Override
	public @Nullable Double floatValue(boolean explicit) {
		return null;
	}
	
	@Override
	public @Nullable Character charValue(boolean explicit) {
		return null;
	}
	
	@Override
	public @Nullable String stringValue(boolean explicit) {
		return explicit ? raw.toString() : null;
	}
	
	@Override
	public BoolValue clone() {
		return new BoolValue(raw.booleanValue());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(BuiltIn.BOOL, raw);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BoolValue other) {
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
