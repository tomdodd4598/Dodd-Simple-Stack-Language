package dssl.interpret;

import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.element.Element;
import dssl.interpret.element.clazz.ClassElement;
import dssl.interpret.magic.Magic;

public class Clazz implements Scope {
	
	public final @NonNull String identifier;
	public final @NonNull String shallow;
	public final @NonNull Element elem;
	
	public final Map<@NonNull String, Def> defMap;
	public final Map<@NonNull String, Clazz> clazzMap;
	public final Map<@NonNull String, Magic> magicMap;
	
	public Clazz(String prev, @NonNull String extension, ScopeMaps maps) {
		identifier = prev == null ? extension : prev + "." + extension;
		shallow = extension;
		elem = new ClassElement(this);
		defMap = maps.defMap;
		clazzMap = maps.clazzMap;
		magicMap = maps.magicMap;
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
		clazzMap.put(shallow, new Clazz(identifier, shallow, maps));
	}
	
	@Override
	public ScopeMaps getMaps() {
		return new ScopeMaps(defMap, clazzMap, magicMap);
	}
}
