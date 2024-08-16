package dssl.interpret.element.clazz;

import java.util.Objects;

import org.eclipse.jdt.annotation.*;

import dssl.interpret.*;
import dssl.interpret.element.*;

public class ClassElement extends Element {
	
	public final @NonNull Clazz internal;
	
	public ClassElement(Interpreter interpreter, @NonNull Clazz internal) {
		super(interpreter, interpreter.builtIn.classClazz);
		this.internal = internal;
	}
	
	@Override
	public @Nullable Scope getMemberScope(@NonNull MemberAccessType access) {
		return access.equals(MemberAccessType.STATIC) ? internal : clazz;
	}
	
	@Override
	public @NonNull Element supers(TokenExecutor exec) {
		return new ListElement(interpreter, internal.supers.stream().map(x -> x.clazzElement(interpreter)));
	}
	
	@Override
	public @NonNull Element clone() {
		return new ClassElement(interpreter, internal);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(BuiltIn.CLASS, internal);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ClassElement other) {
			return internal.equals(other.internal);
		}
		return false;
	}
	
	@Override
	public @NonNull String toString(TokenExecutor exec) {
		return internal.fullIdentifier;
	}
}
