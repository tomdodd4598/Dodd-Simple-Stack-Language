package dssl.interpret.element;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import dssl.interpret.*;

public class LabelElement extends Element {
	
	protected final @NonNull Scope scope;
	
	public final @NonNull String fullIdentifier;
	protected final @NonNull String shallowIdentifier;
	
	public LabelElement(@NonNull Scope scope, @NonNull String identifier) {
		this(scope, MemberAccessType.STATIC.nextIdentifier(scope.getIdentifier(), identifier), identifier);
	}
	
	protected LabelElement(@NonNull Scope scope, @NonNull String fullIdentifier, @NonNull String shallowIdentifier) {
		super(BuiltIn.LABEL_CLAZZ);
		this.scope = scope;
		this.fullIdentifier = fullIdentifier;
		this.shallowIdentifier = shallowIdentifier;
	}
	
	protected LabelElement(@NonNull LabelElement prev, @NonNull String extension, @NonNull MemberAccessType access) {
		super(BuiltIn.LABEL_CLAZZ);
		Def def = prev.getDef();
		@NonNull Element elem;
		@NonNull MemberAccessType modifiedAccess;
		@Nullable Scope nextScope;
		if (def != null && (nextScope = (elem = def.elem).getMemberLabelScope(modifiedAccess = elem.getMemberLabelModifiedAccess(access))) != null) {
			scope = nextScope;
			fullIdentifier = modifiedAccess.nextIdentifier(elem, extension);
		}
		else if ((nextScope = prev.getClazz()) != null) {
			scope = nextScope;
			fullIdentifier = MemberAccessType.STATIC.nextIdentifier(prev.fullIdentifier, extension);
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
	
	public Const getConst() {
		return scope.getConst(shallowIdentifier);
	}
	
	public void setConst(@NonNull Element value) {
		scope.setConst(shallowIdentifier, value);
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
	
	public @NonNull LabelElement extended(@NonNull String extension, @NonNull MemberAccessType access) {
		return new LabelElement(this, extension, access);
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
