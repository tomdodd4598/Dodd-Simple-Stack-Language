package dssl.interpret.element.ref;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.Element;

public class DefRefElement extends Element implements RefElement {
	
	protected final @NonNull Def def;
	
	public DefRefElement(@NonNull Def def) {
		super(BuiltIn.REF_CLAZZ);
		this.def = def;
	}
	
	@Override
	public Def getDef() {
		return def;
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
		return null;
	}
	
	@Override
	public @NonNull String refIdentifier() {
		return def.identifier;
	}
	
	@Override
	public @NonNull Element clone() {
		return this;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(BuiltIn.REF, def);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DefRefElement) {
			DefRefElement other = (DefRefElement) obj;
			return def.equals(other.def);
		}
		return false;
	}
	
	@Override
	public @NonNull String toString() {
		return "&" + refIdentifier();
	}
}
