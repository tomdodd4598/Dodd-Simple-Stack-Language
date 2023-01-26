package dssl.interpret.element.clazz;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.Element;
import dssl.interpret.element.primitive.StringElement;
import dssl.interpret.magic.Magic;

public class InstanceElement extends Element implements Scope {
	
	public final Clazz clazz;
	
	public final Map<@NonNull String, Def> defMap;
	public final Map<@NonNull String, Clazz> clazzMap;
	public final Map<@NonNull String, Magic> magicMap;
	
	public final @NonNull String scopeIdentifier;
	
	public InstanceElement(Clazz clazz) {
		this(clazz, ScopeMaps.empty());
	}
	
	protected InstanceElement(Clazz clazz, ScopeMaps maps) {
		super();
		this.clazz = clazz;
		defMap = maps.defMap;
		clazzMap = maps.clazzMap;
		magicMap = maps.magicMap;
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
	public Def getDef(@NonNull String identifier) {
		return defMap.get(identifier);
	}
	
	@Override
	public void setDef(@NonNull String identifier, @NonNull Element value, boolean shadow) {
		defMap.put(identifier, new Def(identifier, value));
	}
	
	@Override
	public Clazz getClazz(@NonNull String shallow) {
		return clazzMap.get(shallow);
	}
	
	@Override
	public void setClazz(@NonNull String shallow, ScopeMaps maps) {
		clazzMap.put(shallow, new Clazz(scopeIdentifier, shallow, maps));
	}
	
	@Override
	public ScopeMaps getMaps() {
		return new ScopeMaps(defMap, clazzMap, magicMap);
	}
	
	@Override
	public @NonNull Element clone() {
		return new InstanceElement(clazz, getMaps().clone());
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
