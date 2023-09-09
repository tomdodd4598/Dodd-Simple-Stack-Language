package dssl.interpret.value;

import java.math.BigInteger;

import org.eclipse.jdt.annotation.*;

public abstract class PrimitiveValue<@NonNull T> {
	
	public final @NonNull T raw;
	
	protected PrimitiveValue(@NonNull T raw) {
		this.raw = raw;
	}
	
	public abstract @Nullable BigInteger intValue(boolean explicit);
	
	public abstract @Nullable Boolean boolValue(boolean explicit);
	
	public abstract @Nullable Double floatValue(boolean explicit);
	
	public abstract @Nullable Character charValue(boolean explicit);
	
	public abstract @Nullable String stringValue(boolean explicit);
	
	@Override
	public abstract PrimitiveValue<@NonNull T> clone();
	
	@Override
	public abstract int hashCode();
	
	@Override
	public abstract boolean equals(Object obj);
	
	@Override
	public abstract @NonNull String toString();
}
