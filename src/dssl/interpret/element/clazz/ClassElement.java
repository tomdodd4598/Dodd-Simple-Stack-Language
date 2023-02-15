package dssl.interpret.element.clazz;

import java.util.Objects;

import org.eclipse.jdt.annotation.*;

import dssl.interpret.*;
import dssl.interpret.element.*;
import dssl.interpret.element.primitive.StringElement;

public class ClassElement extends ValueElement {
	
	public final @NonNull Clazz internal;
	
	public ClassElement(@NonNull Clazz internal) {
		super(BuiltIn.CLASS_CLAZZ);
		this.internal = internal;
	}
	
	@Override
	public StringElement stringCast(boolean explicit) {
		return explicit ? new StringElement(toString()) : null;
	}
	
	public Def getDef(@NonNull String identifier) {
		return internal.getDef(identifier);
	}
	
	public Macro getMacro(@NonNull String identifier) {
		return internal.getMacro(identifier);
	}
	
	public Clazz getClazz(@NonNull String shallow) {
		return internal.getClazz(shallow);
	}
	
	public Magic getMagic(@NonNull String identifier) {
		return internal.getMagic(identifier);
	}
	
	@Override
	public @Nullable TokenResult scopeAction(TokenExecutor exec, @NonNull String identifier) {
		TokenResult result = internal.scopeAction(exec, identifier);
		return result == null ? super.scopeAction(exec, identifier) : result;
	}
	
	@Override
	public RuntimeException memberAccessError(@NonNull String member) {
		return new IllegalArgumentException(String.format("Class member \"%s.%s\" not defined!", internal.identifier, member));
	}
	
	@Override
	public @NonNull Element clone() {
		return new ClassElement(internal);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash("class", internal);
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
		return "class:" + internal.identifier;
	}
	
	@Override
	public @NonNull String debugString() {
		return "class:" + internal.identifier;
	}
}
