package dssl.interpret;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import dssl.Hierarchy;
import dssl.interpret.element.*;

public interface HierarchicalScope extends Scope {
	
	@Override
	public default boolean hasDef(@NonNull String identifier) {
		return getDefHierarchy().containsKey(identifier);
	}
	
	@Override
	public default Def getDef(@NonNull String identifier) {
		return getDefHierarchy().get(identifier);
	}
	
	@Override
	public default void setDef(@NonNull String identifier, @NonNull Element value, boolean shadow) {
		checkDef(identifier);
		getDefHierarchy().put(identifier, new Def(identifier, value), shadow);
	}
	
	@Override
	public default boolean hasMacro(@NonNull String identifier) {
		return getMacroHierarchy().containsKey(identifier);
	}
	
	@Override
	public default Macro getMacro(@NonNull String identifier) {
		return getMacroHierarchy().get(identifier);
	}
	
	@Override
	public default void setMacro(@NonNull String identifier, @NonNull BlockElement block) {
		checkMacro(identifier);
		getMacroHierarchy().put(identifier, new Macro(identifier, block), true);
	}
	
	@Override
	public default boolean hasClazz(@NonNull String shallow) {
		return getClazzHierarchy().containsKey(shallow);
	}
	
	@Override
	public default Clazz getClazz(@NonNull String shallow) {
		return getClazzHierarchy().get(shallow);
	}
	
	@Override
	public default void setClazz(@NonNull String shallow, HierarchicalScope base, List<@NonNull Clazz> supers) {
		checkClazz(shallow);
		getClazzHierarchy().put(shallow, new Clazz(getIdentifier(), shallow, base, supers), true);
	}
	
	@Override
	public default boolean hasMagic(@NonNull String identifier) {
		return getMagicHierarchy().containsKey(identifier);
	}
	
	@Override
	public default Magic getMagic(@NonNull String identifier) {
		return getMagicHierarchy().get(identifier);
	}
	
	@Override
	public default void setMagic(@NonNull String identifier, @NonNull BlockElement block) {
		getMagicHierarchy().put(identifier, new Magic(identifier, block), true);
	}
	
	public String getIdentifier();
	
	public Hierarchy<@NonNull String, Def> getDefHierarchy();
	
	public Hierarchy<@NonNull String, Macro> getMacroHierarchy();
	
	public Hierarchy<@NonNull String, Clazz> getClazzHierarchy();
	
	public Hierarchy<@NonNull String, Magic> getMagicHierarchy();
}
