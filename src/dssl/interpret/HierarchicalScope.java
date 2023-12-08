package dssl.interpret;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import dssl.Hierarchy;
import dssl.interpret.element.*;
import dssl.interpret.element.primitive.StringElement;

public interface HierarchicalScope extends Scope {
	
	@Override
	public default boolean hasDef(@NonNull String identifier, boolean shallow) {
		return getDefHierarchy().containsKey(identifier, shallow);
	}
	
	@Override
	public default Def getDef(@NonNull String identifier) {
		return getDefHierarchy().get(identifier, false);
	}
	
	public default void setDef(@NonNull String identifier, @NonNull Def def, boolean shadow) {
		if (shadow) {
			checkCollision(identifier);
		}
		getDefHierarchy().put(identifier, def, shadow);
	}
	
	@Override
	public default void setDef(@NonNull String identifier, @NonNull Element value, boolean shadow) {
		setDef(identifier, new Def(identifier, value), shadow);
	}
	
	@Override
	public default Def removeDef(@NonNull String identifier) {
		return getDefHierarchy().remove(identifier, false);
	}
	
	@Override
	public default boolean hasMacro(@NonNull String identifier, boolean shallow) {
		return getMacroHierarchy().containsKey(identifier, shallow);
	}
	
	@Override
	public default Macro getMacro(@NonNull String identifier) {
		return getMacroHierarchy().get(identifier, false);
	}
	
	public default void setMacro(@NonNull String identifier, @NonNull Macro macro, boolean shadow) {
		checkCollision(identifier);
		getMacroHierarchy().put(identifier, macro, shadow);
	}
	
	@Override
	public default void setMacro(@NonNull String identifier, @NonNull Invokable invokable) {
		setMacro(identifier, new Macro(identifier, invokable), true);
	}
	
	@Override
	public default Macro removeMacro(@NonNull String identifier) {
		return getMacroHierarchy().remove(identifier, false);
	}
	
	@Override
	public default boolean hasClazz(@NonNull String shallowIdentifier, boolean shallow) {
		return getClazzHierarchy().containsKey(shallowIdentifier, shallow);
	}
	
	@Override
	public default Clazz getClazz(@NonNull String shallowIdentifier) {
		return getClazzHierarchy().get(shallowIdentifier, false);
	}
	
	public default void setClazz(@NonNull String shallowIdentifier, @NonNull Clazz clazz, boolean shadow) {
		checkCollision(shallowIdentifier);
		getClazzHierarchy().put(shallowIdentifier, clazz, shadow);
	}
	
	@Override
	public default void setClazz(@NonNull String shallowIdentifier, @NonNull ClazzType type, HierarchicalScope base, ArrayList<Clazz> supers) {
		setClazz(shallowIdentifier, new Clazz(scopeIdentifier(), shallowIdentifier, type, base, supers), true);
	}
	
	@Override
	public default Clazz removeClazz(@NonNull String shallowIdentifier) {
		return getClazzHierarchy().remove(shallowIdentifier, false);
	}
	
	@Override
	public default boolean hasMagic(@NonNull String identifier, boolean shallow) {
		return getMagicHierarchy().containsKey(identifier, shallow);
	}
	
	@Override
	public default Magic getMagic(@NonNull String identifier) {
		return getMagicHierarchy().get(identifier, false);
	}
	
	public default void setMagic(@NonNull String identifier, @NonNull Magic magic, boolean shadow) {
		getMagicHierarchy().put(identifier, magic, shadow);
	}
	
	@Override
	public default void setMagic(@NonNull String identifier, @NonNull Invokable invokable) {
		setMagic(identifier, new Magic(identifier, invokable), true);
	}
	
	@Override
	public default Magic removeMagic(@NonNull String identifier) {
		return getMagicHierarchy().remove(identifier, false);
	}
	
	public Hierarchy<@NonNull String, Def> getDefHierarchy();
	
	public Hierarchy<@NonNull String, Macro> getMacroHierarchy();
	
	public Hierarchy<@NonNull String, Clazz> getClazzHierarchy();
	
	public Hierarchy<@NonNull String, Magic> getMagicHierarchy();
	
	public default <T> void addToScopeMap(Hierarchy<@NonNull String, T> source, Map<@NonNull Element, @NonNull Element> target) {
		source.forEach((k, v) -> target.put(new StringElement(k), new LabelElement(this, k)), false);
	}
	
	@Override
	public default @NonNull Element scopeElement(TokenExecutor exec) {
		Map<@NonNull Element, @NonNull Element> map = new HashMap<>();
		addToScopeMap(getDefHierarchy(), map);
		addToScopeMap(getMacroHierarchy(), map);
		addToScopeMap(getClazzHierarchy(), map);
		return new DictElement(map, false);
	}
	
	@SuppressWarnings("null")
	public default void putAll(@NonNull HierarchicalScope from, boolean shadow, boolean shallow) {
		from.getDefHierarchy().forEach((k, v) -> setDef(k, v, shadow), shallow);
		from.getMacroHierarchy().forEach((k, v) -> setMacro(k, v, shadow), shallow);
		from.getClazzHierarchy().forEach((k, v) -> setClazz(k, v, shadow), shallow);
		from.getMagicHierarchy().forEach((k, v) -> setMagic(k, v, shadow), shallow);
	}
}
