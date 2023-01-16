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
	
	public final Map<String, Def> defMap;
	public final Map<String, Clazz> clazzMap;
	public final Map<String, Magic> magicMap;
	
	public Clazz(String prev, @NonNull String extension, Map<String, Def> defMap, Map<String, Clazz> clazzMap, Map<String, Magic> magicMap) {
		identifier = prev == null ? extension : prev + "." + extension;
		shallow = extension;
		elem = new ClassElement(this);
		this.defMap = defMap;
		this.clazzMap = clazzMap;
		this.magicMap = magicMap;
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
		clazzMap.put(shallow, new Clazz(identifier, shallow, defMap, clazzMap, magicMap));
	}
}
