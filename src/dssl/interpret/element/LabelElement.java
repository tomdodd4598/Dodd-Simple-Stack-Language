package dssl.interpret.element;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import dssl.Helpers;
import dssl.interpret.*;

public class LabelElement extends Element {
	
	protected final @NonNull Scope scope;
	
	public final @NonNull String fullIdentifier;
	protected final @NonNull String shallowIdentifier;
	
	public LabelElement(@NonNull Scope scope, @NonNull String identifier) {
		this(scope, Helpers.extendedIdentifier(scope.scopeIdentifier(), identifier), identifier);
	}
	
	protected LabelElement(@NonNull Scope scope, @NonNull String fullIdentifier, @NonNull String shallowIdentifier) {
		super(BuiltIn.LABEL_CLAZZ);
		this.scope = scope;
		this.fullIdentifier = fullIdentifier;
		this.shallowIdentifier = shallowIdentifier;
	}
	
	protected LabelElement(@NonNull LabelElement prev, @NonNull String extension) {
		super(BuiltIn.LABEL_CLAZZ);
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
		scope.setDef(shallowIdentifier, value, shadow);
	}
	
	public Macro getMacro() {
		return scope.getMacro(shallowIdentifier);
	}
	
	public void setMacro(@NonNull BlockElement block) {
		scope.setMacro(shallowIdentifier, block);
	}
	
	public Clazz getClazz() {
		return scope.getClazz(shallowIdentifier);
	}
	
	public void setClazz(@NonNull ClazzType type, HierarchicalScope base, ArrayList<Clazz> supers) {
		scope.setClazz(shallowIdentifier, type, base, supers);
	}
	
	public Magic getMagic() {
		return scope.getMagic(shallowIdentifier);
	}
	
	public void setMagic(@NonNull BlockElement block) {
		scope.setMagic(shallowIdentifier, block);
	}
	
	public @NonNull TokenResult delete() {
		if (scope.removeDef(shallowIdentifier) != null) {
			return TokenResult.PASS;
		}
		else if (scope.removeMacro(shallowIdentifier) != null) {
			return TokenResult.PASS;
		}
		else if (scope.removeClazz(shallowIdentifier) != null) {
			return TokenResult.PASS;
		}
		else if (scope.removeMagic(shallowIdentifier) != null) {
			return TokenResult.PASS;
		}
		throw Helpers.defError(fullIdentifier);
	}
	
	public @NonNull LabelElement extended(@NonNull String extension) {
		return new LabelElement(this, extension);
	}
	
	@Override
	public @NonNull Element scope(TokenExecutor exec) {
		return scope.scopeElement(exec);
	}
	
	@Override
	public @NonNull Element clone() {
		return new LabelElement(scope, fullIdentifier, shallowIdentifier);
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
	public @NonNull String toString() {
		return "/" + fullIdentifier;
	}
}
