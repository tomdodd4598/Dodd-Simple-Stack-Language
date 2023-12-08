package dssl.interpret.element.clazz;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import dssl.interpret.*;
import dssl.interpret.element.*;
import dssl.interpret.element.primitive.StringElement;

public class InstanceElement extends Element implements Scope {
	
	protected final Map<@NonNull String, Def> defMap;
	protected final Map<@NonNull String, Macro> macroMap;
	protected final Map<@NonNull String, Clazz> clazzMap;
	protected final Map<@NonNull String, Magic> magicMap;
	
	public final @NonNull String scopeIdentifier;
	
	public InstanceElement(@NonNull Clazz clazz) {
		this(clazz, new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>());
	}
	
	protected InstanceElement(@NonNull Clazz clazz, Map<@NonNull String, Def> defMap, Map<@NonNull String, Macro> macroMap, Map<@NonNull String, Clazz> clazzMap, Map<@NonNull String, Magic> magicMap) {
		super(clazz);
		this.defMap = defMap;
		this.macroMap = macroMap;
		this.clazzMap = clazzMap;
		this.magicMap = magicMap;
		scopeIdentifier = toString();
	}
	
	@Override
	public @Nullable String scopeIdentifier() {
		return scopeIdentifier;
	}
	
	@Override
	public boolean hasDef(@NonNull String identifier, boolean shallow) {
		return defMap.containsKey(identifier);
	}
	
	@Override
	public Def getDef(@NonNull String identifier) {
		return defMap.get(identifier);
	}
	
	@Override
	public void setDef(@NonNull String identifier, @NonNull Element value, boolean shadow) {
		if (shadow) {
			checkCollision(identifier);
		}
		defMap.put(identifier, new Def(identifier, value));
	}
	
	@Override
	public Def removeDef(@NonNull String identifier) {
		return defMap.remove(identifier);
	}
	
	@Override
	public boolean hasMacro(@NonNull String identifier, boolean shallow) {
		return macroMap.containsKey(identifier);
	}
	
	@Override
	public Macro getMacro(@NonNull String identifier) {
		return macroMap.get(identifier);
	}
	
	@Override
	public void setMacro(@NonNull String identifier, @NonNull Invokable invokable) {
		checkCollision(identifier);
		macroMap.put(identifier, new Macro(identifier, invokable));
	}
	
	@Override
	public Macro removeMacro(@NonNull String identifier) {
		return macroMap.remove(identifier);
	}
	
	@Override
	public boolean hasClazz(@NonNull String shallowIdentifier, boolean shallow) {
		return clazzMap.containsKey(shallowIdentifier);
	}
	
	@Override
	public Clazz getClazz(@NonNull String shallowIdentifier) {
		return clazzMap.get(shallowIdentifier);
	}
	
	@Override
	public void setClazz(@NonNull String shallowIdentifier, @NonNull ClazzType type, HierarchicalScope base, ArrayList<Clazz> supers) {
		checkCollision(shallowIdentifier);
		clazzMap.put(shallowIdentifier, new Clazz(scopeIdentifier, shallowIdentifier, type, base, supers));
	}
	
	@Override
	public Clazz removeClazz(@NonNull String shallowIdentifier) {
		return clazzMap.remove(shallowIdentifier);
	}
	
	@Override
	public boolean hasMagic(@NonNull String identifier, boolean shallow) {
		return magicMap.containsKey(identifier);
	}
	
	@Override
	public Magic getMagic(@NonNull String identifier) {
		return magicMap.get(identifier);
	}
	
	@Override
	public void setMagic(@NonNull String identifier, @NonNull Invokable invokable) {
		magicMap.put(identifier, new Magic(identifier, invokable));
	}
	
	@Override
	public Magic removeMagic(@NonNull String identifier) {
		return magicMap.remove(identifier);
	}
	
	protected <T> void addToScopeMap(Map<@NonNull String, T> source, Map<@NonNull Element, @NonNull Element> target) {
		for (String key : source.keySet()) {
			target.put(new StringElement(key), new LabelElement(this, key));
		}
	}
	
	@Override
	public @NonNull Element scopeElement(TokenExecutor exec) {
		Map<@NonNull Element, @NonNull Element> map = new HashMap<>();
		addToScopeMap(defMap, map);
		addToScopeMap(macroMap, map);
		addToScopeMap(clazzMap, map);
		return new DictElement(map, false);
	}
	
	@Override
	public @Nullable Scope getMemberScope(@NonNull MemberAccessType access) {
		return access.equals(MemberAccessType.STATIC) ? this : clazz;
	}
	
	@Override
	public @NonNull Element scope(TokenExecutor exec) {
		return scopeElement(exec);
	}
	
	@Override
	public @NonNull Element clone() {
		return new InstanceElement(clazz, new HashMap<>(defMap), new HashMap<>(macroMap), new HashMap<>(clazzMap), new HashMap<>(magicMap));
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
}
