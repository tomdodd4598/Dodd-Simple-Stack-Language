package dssl.interpret.element.clazz;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.Element;
import dssl.interpret.element.primitive.StringElement;
import dssl.interpret.magic.Magic;

public class InstanceElement extends Element implements Scope {
	
	public final Clazz clazz;
	
	public final Map<String, Def> defMap;
	public final Map<String, Clazz> clazzMap;
	public final Map<String, Magic> magicMap;
	
	public final @NonNull String scopeIdentifier;
	
	public InstanceElement(Clazz clazz) {
		this(clazz, new HashMap<>(), new HashMap<>(), new HashMap<>());
	}
	
	protected InstanceElement(Clazz clazz, Map<String, Def> defMap, Map<String, Clazz> clazzMap, Map<String, Magic> magicMap) {
		super();
		this.clazz = clazz;
		this.defMap = defMap;
		this.clazzMap = clazzMap;
		this.magicMap = magicMap;
		scopeIdentifier = clazz.identifier + "@" + Integer.toString(objectHashCode(), 16);
	}
	
	@Override
	public @NonNull String typeName() {
		return clazz.identifier;
	}
	
	@Override
	public @NonNull StringElement stringCastExplicit() {
		return new StringElement(toString());
	}
	
	@Override
	public Def getDef(String identifier) {
		return defMap.get(identifier);
	}
	
	@Override
	public void setDef(@NonNull String identifier, @NonNull Element value, boolean shadow) {
		defMap.put(identifier, new Def(identifier, value));
	}
	
	@Override
	public Clazz getClazz(String shallow) {
		return clazzMap.get(shallow);
	}
	
	@Override
	public void setClazz(@NonNull String shallow, Map<String, Def> defMap, Map<String, Clazz> clazzMap, Map<String, Magic> magicMap) {
		clazzMap.put(shallow, new Clazz(scopeIdentifier, shallow, defMap, clazzMap, magicMap));
	}
	
	@Override
	public @NonNull Element clone() {
		return new InstanceElement(clazz, new HashMap<>(defMap), new HashMap<>(clazzMap), new HashMap<>(magicMap));
	}
	
	@Override
	public int hashCode() {
		return Objects.hash("instance", clazz, defMap, clazzMap, magicMap);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof InstanceElement) {
			InstanceElement other = (InstanceElement) obj;
			return clazz.equals(other.clazz) && defMap.equals(other.defMap) && clazzMap.equals(other.clazzMap) && magicMap.equals(other.magicMap);
		}
		return false;
	}
	
	@Override
	public @NonNull String toString() {
		return clazz.identifier + "@" + Integer.toString(objectHashCode(), 16);
	}
	
	@Override
	public @NonNull String toDebugString() {
		return clazz.identifier + "@" + Integer.toString(objectHashCode(), 16);
	}
}
