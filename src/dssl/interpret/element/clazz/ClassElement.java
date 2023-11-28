package dssl.interpret.element.clazz;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import dssl.Hierarchy;
import dssl.interpret.*;
import dssl.interpret.element.*;
import dssl.interpret.element.primitive.StringElement;

public class ClassElement extends Element {
	
	public final @NonNull Clazz internal;
	
	public ClassElement(@NonNull Clazz internal) {
		super(BuiltIn.CLASS_CLAZZ);
		this.internal = internal;
	}
	
	@Override
	public @Nullable Scope getMemberLabelScope(@NonNull MemberAccessType access) {
		return access.equals(MemberAccessType.INSTANCE) ? clazz : internal;
	}
	
	@Override
	public @Nullable TokenResult memberAccess(TokenExecutor exec, @NonNull String member, @NonNull MemberAccessType access) {
		if (access.equals(MemberAccessType.INSTANCE)) {
			exec.push(this);
			return clazz.scopeAction(exec, member);
		}
		else {
			return internal.scopeAction(exec, member);
		}
	}
	
	@Override
	public String scopeAccessIdentifier(@NonNull MemberAccessType access) {
		return access.equals(MemberAccessType.INSTANCE) ? clazz.fullIdentifier : internal.fullIdentifier;
	}
	
	protected <T> void addToScopeMap(Hierarchy<@NonNull String, T> source, Map<@NonNull Element, @NonNull Element> target) {
		source.forEach((k, v) -> target.put(new StringElement(k), new LabelElement(internal, k)), false);
	}
	
	@Override
	public @NonNull Element scope(TokenExecutor exec) {
		Map<@NonNull Element, @NonNull Element> map = new HashMap<>();
		addToScopeMap(internal.getDefHierarchy(), map);
		addToScopeMap(internal.getConstHierarchy(), map);
		addToScopeMap(internal.getMacroHierarchy(), map);
		addToScopeMap(internal.getClazzHierarchy(), map);
		return new DictElement(map, false);
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
