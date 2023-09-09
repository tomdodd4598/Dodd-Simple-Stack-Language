package dssl.interpret.element.ref;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.Element;

public class MacroRefElement extends Element implements RefElement {
	
	protected final @NonNull Macro macro;
	
	public MacroRefElement(@NonNull Macro macro) {
		super(BuiltIn.REF_CLAZZ);
		this.macro = macro;
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
		return macro;
	}
	
	@Override
	public Clazz getClazz() {
		return null;
	}
	
	@Override
	public @NonNull String refIdentifier() {
		return macro.identifier;
	}
	
	@Override
	public @NonNull Element clone() {
		return this;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(BuiltIn.REF, macro);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MacroRefElement) {
			MacroRefElement other = (MacroRefElement) obj;
			return macro.equals(other.macro);
		}
		return false;
	}
	
	@Override
	public @NonNull String toString() {
		return "&" + refIdentifier();
	}
}
