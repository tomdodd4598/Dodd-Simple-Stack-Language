package dssl.interpret.element.clazz;

import java.util.Objects;

import org.eclipse.jdt.annotation.*;

import dssl.interpret.*;
import dssl.interpret.element.*;

public class ClassElement extends Element {
	
	public final @NonNull Clazz internal;
	
	public ClassElement(@NonNull Clazz internal) {
		super(BuiltIn.CLASS_CLAZZ);
		this.internal = internal;
	}
	
	@Override
	public @Nullable Scope getMemberScope(@NonNull MemberAccessType access) {
		return access.equals(MemberAccessType.STATIC) ? internal : clazz;
	}
	
	@Override
	public @NonNull Element supers(TokenExecutor exec) {
		return new ListElement(internal.supers.stream().map(Clazz::clazzElement));
	}
	
	@Override
	public @NonNull Element clone() {
		return new ClassElement(internal);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(BuiltIn.CLASS, internal);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ClassElement) {
			ClassElement other = (ClassElement) obj;
			return internal.equals(other.internal);
		}
		return false;
	}
	
	@Override
	public @NonNull String toString() {
		return internal.fullIdentifier;
	}
}
