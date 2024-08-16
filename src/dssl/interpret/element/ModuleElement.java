package dssl.interpret.element;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;

public class ModuleElement extends Element {
	
	public final @NonNull Clazz internal;
	
	public ModuleElement(Interpreter interpreter, @NonNull String identifier) {
		super(interpreter, interpreter.builtIn.moduleClazz);
		Clazz internal = interpreter.builtIn.moduleMap.get(identifier);
		if (internal == null) {
			throw new IllegalArgumentException(String.format("Core module \"%s\" not found!", identifier));
		}
		this.internal = internal;
	}
	
	protected ModuleElement(Interpreter interpreter, @NonNull Clazz internal) {
		super(interpreter, interpreter.builtIn.moduleClazz);
		this.internal = internal;
	}
	
	@Override
	public @NonNull Element clone() {
		return new ModuleElement(interpreter, internal);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(BuiltIn.MODULE, internal);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ModuleElement other) {
			return internal.equals(other.internal);
		}
		return false;
	}
	
	@Override
	public @NonNull String toString(TokenExecutor exec) {
		return "$" + internal.fullIdentifier;
	}
}
