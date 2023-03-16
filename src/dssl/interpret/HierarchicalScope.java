package dssl.interpret;

import java.util.ArrayList;

import org.eclipse.jdt.annotation.NonNull;

import dssl.Hierarchy;
import dssl.interpret.element.Element;

public interface HierarchicalScope extends Scope {
	
	@Override
	public default boolean hasDef(@NonNull String identifier) {
		return getDefHierarchy().containsKey(identifier);
	}
	
	@Override
	public default Def getDef(@NonNull String identifier) {
		return getDefHierarchy().get(identifier);
	}
	
	public default void setDef(@NonNull String identifier, @NonNull Def def, boolean shadow) {
		checkDef(identifier);
		getDefHierarchy().put(identifier, def, shadow);
	}
	
	@Override
	public default void setDef(@NonNull String identifier, @NonNull Element value, boolean shadow) {
		setDef(identifier, new Def(identifier, value), shadow);
	}
	
	@Override
	public default boolean hasMacro(@NonNull String identifier) {
		return getMacroHierarchy().containsKey(identifier);
	}
	
	@Override
	public default Macro getMacro(@NonNull String identifier) {
		return getMacroHierarchy().get(identifier);
	}
	
	public default void setMacro(@NonNull String identifier, @NonNull Macro macro, boolean shadow) {
		checkMacro(identifier);
		getMacroHierarchy().put(identifier, macro, shadow);
	}
	
	@Override
	public default void setMacro(@NonNull String identifier, @NonNull Invokable invokable) {
		setMacro(identifier, new Macro(identifier, invokable), true);
	}
	
	@Override
	public default boolean hasClazz(@NonNull String shallow) {
		return getClazzHierarchy().containsKey(shallow);
	}
	
	@Override
	public default Clazz getClazz(@NonNull String shallow) {
		return getClazzHierarchy().get(shallow);
	}
	
	public default void setClazz(@NonNull String shallow, @NonNull Clazz clazz, boolean shadow) {
		checkClazz(shallow);
		getClazzHierarchy().put(shallow, clazz, shadow);
	}
	
	@Override
	public default void setClazz(@NonNull String shallow, HierarchicalScope base, ArrayList<Clazz> supers) {
		setClazz(shallow, new Clazz(getIdentifier(), shallow, base, supers), true);
	}
	
	@Override
	public default boolean hasMagic(@NonNull String identifier) {
		return getMagicHierarchy().containsKey(identifier);
	}
	
	@Override
	public default Magic getMagic(@NonNull String identifier) {
		return getMagicHierarchy().get(identifier);
	}
	
	public default void setMagic(@NonNull String identifier, @NonNull Magic magic, boolean shadow) {
		getMagicHierarchy().put(identifier, magic, shadow);
	}
	
	@Override
	public default void setMagic(@NonNull String identifier, @NonNull Invokable invokable) {
		setMagic(identifier, new Magic(identifier, invokable), true);
	}
	
	public String getIdentifier();
	
	public Hierarchy<@NonNull String, Def> getDefHierarchy();
	
	public Hierarchy<@NonNull String, Macro> getMacroHierarchy();
	
	public Hierarchy<@NonNull String, Clazz> getClazzHierarchy();
	
	public Hierarchy<@NonNull String, Magic> getMagicHierarchy();
	
	@SuppressWarnings("null")
	public default void putAll(@NonNull HierarchicalScope from, boolean shadow, boolean shallow) {
		from.getDefHierarchy().forEach((k, v) -> setDef(k, v, shadow), shallow);
		from.getMacroHierarchy().forEach((k, v) -> setMacro(k, v, shadow), shallow);
		from.getClazzHierarchy().forEach((k, v) -> setClazz(k, v, shadow), shallow);
		from.getMagicHierarchy().forEach((k, v) -> setMagic(k, v, shadow), shallow);
	}
}
