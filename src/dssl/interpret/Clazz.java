package dssl.interpret;

import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.element.*;
import dssl.interpret.element.clazz.ClassElement;
import dssl.interpret.magic.Magic;

public class Clazz implements Scope {
	
	public final @NonNull String identifier;
	public final @NonNull String shallow;
	public final @NonNull Element elem;
	
	protected final Map<@NonNull String, Def> defMap;
	protected final Map<@NonNull String, Macro> macroMap;
	protected final Map<@NonNull String, Clazz> clazzMap;
	protected final Map<@NonNull String, Magic> magicMap;
	
	public Clazz(String prev, @NonNull String extension, ScopeMaps maps) {
		identifier = prev == null ? extension : prev + "." + extension;
		shallow = extension;
		elem = new ClassElement(this);
		defMap = maps.defMap;
		macroMap = maps.macroMap;
		clazzMap = maps.clazzMap;
		magicMap = maps.magicMap;
	}
	
	@Override
	public boolean hasDef(@NonNull String identifier) {
		return defMap.containsKey(identifier);
	}
	
	@Override
	public Def getDef(@NonNull String identifier) {
		return defMap.get(identifier);
	}
	
	@Override
	public void setDef(@NonNull String identifier, @NonNull Element value, boolean shadow) {
		checkDef(identifier);
		defMap.put(identifier, new Def(identifier, value));
	}
	
	@Override
	public boolean hasMacro(@NonNull String identifier) {
		return macroMap.containsKey(identifier);
	}
	
	@Override
	public Macro getMacro(@NonNull String identifier) {
		return macroMap.get(identifier);
	}
	
	@Override
	public void setMacro(@NonNull String identifier, @NonNull BlockElement block) {
		checkMacro(identifier);
		macroMap.put(identifier, new Macro(identifier, block));
	}
	
	@Override
	public boolean hasClazz(@NonNull String shallow) {
		return clazzMap.containsKey(shallow);
	}
	
	@Override
	public Clazz getClazz(@NonNull String shallow) {
		return clazzMap.get(shallow);
	}
	
	@Override
	public void setClazz(@NonNull String shallow, ScopeMaps maps) {
		checkClazz(shallow);
		clazzMap.put(shallow, new Clazz(identifier, shallow, maps));
	}
	
	@Override
	public boolean hasMagic(@NonNull String identifier) {
		return magicMap.containsKey(identifier);
	}
	
	@Override
	public Magic getMagic(@NonNull String identifier) {
		return magicMap.get(identifier);
	}
	
	@Override
	public ScopeMaps getMaps() {
		return new ScopeMaps(defMap, macroMap, clazzMap, magicMap);
	}
}
