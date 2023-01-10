package dssl.interpret.element.value;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.clazz.ClassInfo;
import dssl.interpret.def.Def;
import dssl.interpret.element.Element;
import dssl.interpret.element.value.primitive.StringElement;

public class InstanceElement extends Element {
	
	public final ClassInfo classInfo;
	public final Map<String, Def<?>> memberMap = new HashMap<>();
	
	protected InstanceElement(ClassInfo classInfo) {
		super();
		this.classInfo = classInfo;
		classInfo.initInstance(this);
	}
	
	@Override
	public @NonNull String typeName() {
		return classInfo.identifier;
	}
	
	@Override
	public @NonNull StringElement stringCastExplicit() {
		return new StringElement(toString());
	}
	
	@Override
	public @NonNull Element clone() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public @NonNull String toString() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public @NonNull String toBriefDebugString() {
		return classInfo.identifier;
	}
}
