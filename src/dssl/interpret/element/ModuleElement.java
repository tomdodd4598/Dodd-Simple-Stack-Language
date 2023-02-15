package dssl.interpret.element;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;

public class ModuleElement extends Element {
	
	public final @NonNull String identifier;
	public final @NonNull Clazz clazz;
	
	public ModuleElement(@NonNull String identifier) {
		this.identifier = identifier;
		Clazz clazz = BuiltIn.MODULE_MAP.get(identifier);
		if (clazz == null) {
			throw new IllegalArgumentException(String.format("Core module \"%s\" not found!", identifier));
		}
		this.clazz = clazz;
	}
	
	@Override
	public @NonNull String typeName() {
		return "module";
	}
	
	@Override
	public @NonNull Element clone() {
		return new ModuleElement(identifier);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash("module", identifier);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ModuleElement) {
			ModuleElement other = (ModuleElement) obj;
			return identifier.equals(other.identifier);
		}
		return false;
	}
	
	@Override
	public @NonNull String toString() {
		return "module:" + identifier;
	}
	
	@Override
	public @NonNull String debugString() {
		return "$" + identifier;
	}
}
