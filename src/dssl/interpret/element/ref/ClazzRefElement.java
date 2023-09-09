package dssl.interpret.element.ref;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.Element;

public class ClazzRefElement extends Element implements RefElement {
	
	protected final @NonNull Clazz internal;
	
	public ClazzRefElement(@NonNull Clazz internal) {
		super(BuiltIn.REF_CLAZZ);
		this.internal = internal;
	}
	
	@Override
	public Def getDef() {
		return null;
	}
	
	@Override
	public Const getConst() {
		return null;
	}
	
	@Override
	public Macro getMacro() {
		return null;
	}
	
	@Override
	public Clazz getClazz() {
		return internal;
	}
	
	@Override
	public @NonNull String refIdentifier() {
		return internal.fullIdentifier;
	}
	
	@Override
	public @NonNull Element clone() {
		return this;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(BuiltIn.REF, internal);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ClazzRefElement) {
			ClazzRefElement other = (ClazzRefElement) obj;
			return internal.equals(other.internal);
		}
		return false;
	}
	
	@Override
	public @NonNull String toString() {
		return "&" + refIdentifier();
	}
}
