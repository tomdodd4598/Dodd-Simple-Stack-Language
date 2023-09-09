package dssl.interpret.value;

import java.math.BigInteger;
import java.util.Objects;

import org.eclipse.jdt.annotation.*;

import dssl.Helpers;
import dssl.interpret.BuiltIn;

public class FloatValue extends PrimitiveValue<@NonNull Double> {
	
	public FloatValue(@NonNull Double value) {
		super(value);
	}
	
	@Override
	public @Nullable BigInteger intValue(boolean explicit) {
		return explicit ? Helpers.bigIntFromDouble(raw) : null;
	}
	
	@Override
	public @Nullable Boolean boolValue(boolean explicit) {
		return null;
	}
	
	@Override
	public @Nullable Double floatValue(boolean explicit) {
		return raw;
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
	public FloatValue clone() {
		return new FloatValue(raw.doubleValue());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(BuiltIn.FLOAT, raw);
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
