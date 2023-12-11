package dssl.interpret.element;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import dssl.Helpers;
import dssl.interpret.*;

public class LabelElement extends Element {
	
	protected final @NonNull Scope scope;
	
	public final @NonNull String fullIdentifier;
	protected final @NonNull String shallowIdentifier;
	
	public LabelElement(Interpreter interpreter, @NonNull Scope scope, @NonNull String identifier) {
		this(interpreter, scope, Helpers.extendedIdentifier(scope.scopeIdentifier(), identifier), identifier);
	}
	
	protected LabelElement(Interpreter interpreter, @NonNull Scope scope, @NonNull String fullIdentifier, @NonNull String shallowIdentifier) {
		super(interpreter, interpreter.builtIn.labelClazz);
		this.scope = scope;
		this.fullIdentifier = fullIdentifier;
		this.shallowIdentifier = shallowIdentifier;
	}
	
	protected LabelElement(Interpreter interpreter, @NonNull LabelElement prev, @NonNull String extension) {
		super(interpreter, interpreter.builtIn.labelClazz);
		Def def;
		@NonNull Element elem;
		@Nullable Scope nextScope;
		if ((def = prev.getDef()) != null && (nextScope = (elem = def.elem).getMemberScope(MemberAccessType.STATIC)) != null) {
			scope = nextScope;
			fullIdentifier = elem.extendedIdentifier(extension, MemberAccessType.STATIC);
		}
		else if ((nextScope = prev.getClazz()) != null) {
			scope = nextScope;
			fullIdentifier = Helpers.extendedIdentifier(prev.fullIdentifier, extension);
		}
		else {
			throw new IllegalArgumentException(String.format("Scope \"%s\" not accessible for member \"%s\"!", prev.fullIdentifier, extension));
		}
		shallowIdentifier = extension;
	}
	
	public Def getDef() {
		return scope.getDef(shallowIdentifier);
	}
	
	public void setDef(@NonNull Element value, boolean shadow) {
		Def prev = scope.setDef(shallowIdentifier, value, shadow);
		if (prev != null && !scope.canShadow()) {
			throw shadowError("variable");
		}
	}
	
	public Macro getMacro() {
		return scope.getMacro(shallowIdentifier);
	}
	
	public void setMacro(@NonNull BlockElement block) {
		Macro prev = scope.setMacro(shallowIdentifier, block);
		if (prev != null && !scope.canShadow()) {
			throw shadowError("macro");
		}
	}
	
	public Clazz getClazz() {
		return scope.getClazz(shallowIdentifier);
	}
	
	public void setClazz(@NonNull ClazzType type, @Nullable HierarchicalScope base, @NonNull ArrayList<Clazz> supers) {
		Clazz prev = scope.setClazz(interpreter, shallowIdentifier, type, base, supers);
		if (prev != null && !scope.canShadow()) {
			throw shadowError("class");
		}
	}
	
	protected RuntimeException shadowError(@NonNull String type) {
		return new IllegalArgumentException(String.format("Can not shadow %s \"%s\" in %s!", type, shallowIdentifier, scope.scopeIdentifier()));
	}
	
	public @NonNull TokenResult delete() {
		if (scope.removeDef(shallowIdentifier) != null) {
			if (!scope.canDelete()) {
				throw deleteError("variable");
			}
			return TokenResult.PASS;
		}
		else if (scope.removeMacro(shallowIdentifier) != null) {
			if (!scope.canDelete()) {
				throw deleteError("macro");
			}
			return TokenResult.PASS;
		}
		else if (scope.removeClazz(shallowIdentifier) != null) {
			if (!scope.canDelete()) {
				throw deleteError("class");
			}
			return TokenResult.PASS;
		}
		throw Helpers.defError(fullIdentifier);
	}
	
	protected RuntimeException deleteError(@NonNull String type) {
		return new IllegalArgumentException(String.format("Can not delete %s \"%s\" in %s!", type, shallowIdentifier, scope.scopeIdentifier()));
	}
	
	public @NonNull LabelElement extended(@NonNull String extension) {
		return new LabelElement(interpreter, this, extension);
	}
	
	@Override
	public @NonNull Element clone() {
		return new LabelElement(interpreter, scope, fullIdentifier, shallowIdentifier);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(BuiltIn.LABEL, scope, fullIdentifier, shallowIdentifier);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LabelElement) {
			LabelElement other = (LabelElement) obj;
			return scope.equals(other.scope) && fullIdentifier.equals(other.fullIdentifier) && shallowIdentifier.equals(other.shallowIdentifier);
		}
		return false;
	}
	
	@Override
	public @NonNull String toString(TokenExecutor exec) {
		return "/" + fullIdentifier;
	}
}
