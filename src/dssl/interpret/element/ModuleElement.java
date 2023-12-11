package dssl.interpret.element;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;

public class ModuleElement extends Element {
	
	public final @NonNull String identifier;
	public final @NonNull Clazz internal;
	
	public ModuleElement(Interpreter interpreter, @NonNull String identifier) {
		super(interpreter, interpreter.builtIn.moduleClazz);
		this.identifier = identifier;
		Clazz internal = interpreter.builtIn.moduleMap.get(identifier);
		if (internal == null) {
			throw new IllegalArgumentException(String.format("Core module \"%s\" not found!", identifier));
		}
		this.internal = internal;
	}
	
	@Override
	public @NonNull Element clone() {
		return new ModuleElement(interpreter, identifier);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(BuiltIn.MODULE, identifier);
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
	public @NonNull String toString(TokenExecutor exec) {
		return "$" + identifier;
	}
}
