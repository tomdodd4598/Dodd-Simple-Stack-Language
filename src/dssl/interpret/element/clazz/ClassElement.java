package dssl.interpret.element.clazz;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.Element;
import dssl.interpret.element.primitive.StringElement;
import dssl.interpret.magic.*;

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
	
	public Def getDef(String identifier) {
		return clazz.defMap.get(identifier);
	}
	
	public Clazz getClazz(String identifier) {
		return clazz.clazzMap.get(identifier);
	}
	
	public TokenResult instantiate(TokenExecutor exec) {
		exec.push(new InstanceElement(clazz));
		Magic init = clazz.magicMap.get("init");
		if (init == null) {
			return TokenResult.PASS;
		}
		else {
			return ((InitMagic) init).block.executor(exec).iterate();
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
