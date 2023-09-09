package dssl.interpret.element.ref;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.Element;

public class ConstRefElement extends Element implements RefElement {
	
	protected final @NonNull Const cons;
	
	public ConstRefElement(@NonNull Const cons) {
		super(BuiltIn.REF_CLAZZ);
		this.cons = cons;
	}
	
	@Override
	public Def getDef() {
		return null;
	}
	
	@Override
	public Const getConst() {
		return cons;
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
		return cons.identifier;
	}
	
	@Override
	public @NonNull Element clone() {
		return this;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(BuiltIn.REF, cons);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ConstRefElement) {
			ConstRefElement other = (ConstRefElement) obj;
			return cons.equals(other.cons);
		}
		return false;
	}
	
	@Override
	public @NonNull String toString() {
		return "&" + refIdentifier();
	}
}
