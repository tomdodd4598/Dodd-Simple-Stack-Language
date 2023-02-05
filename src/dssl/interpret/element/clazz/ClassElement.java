package dssl.interpret.element.clazz;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.Element;
import dssl.interpret.element.primitive.StringElement;

public class ClassElement extends Element {
	
	public final @NonNull Clazz clazz;
	
	public ClassElement(@NonNull Clazz clazz) {
		super();
		this.clazz = clazz;
	}
	
	@Override
	public @NonNull String typeName() {
		return "class";
	}
	
	@Override
	public @NonNull StringElement stringCastExplicit() {
		return new StringElement(toString());
	}
	
	public Def getDef(@NonNull String identifier) {
		return clazz.getDef(identifier);
	}
	
	public Macro getMacro(@NonNull String identifier) {
		return clazz.getMacro(identifier);
	}
	
	public Clazz getClazz(@NonNull String shallow) {
		return clazz.getClazz(shallow);
	}
	
	public Magic getMagic(@NonNull String identifier) {
		return clazz.getMagic(identifier);
	}
	
	public TokenResult instantiate(TokenExecutor exec) {
		InstanceElement instance = new InstanceElement(clazz);
		TokenResult init = instance.magicAction(exec, "init");
		if (init == null) {
			exec.push(instance);
			return TokenResult.PASS;
		}
		else {
			return init;
		}
	}
	
	@Override
	public @NonNull Element clone() {
		return new ClassElement(clazz);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash("class", clazz);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ClassElement) {
			ClassElement other = (ClassElement) obj;
			return clazz.equals(other.clazz);
		}
		return false;
	}
	
	@Override
	public @NonNull String toString() {
		return "class:" + clazz.identifier;
	}
	
	@Override
	public @NonNull String toDebugString() {
		return "class:" + clazz.identifier;
	}
}
