package dssl.interpret;

import java.util.*;

import org.eclipse.jdt.annotation.*;

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
	
	public default Def setDef(@NonNull String identifier, @NonNull Def def, boolean shadow) {
		if (shadow) {
			checkCollision(identifier);
		}
		return getDefHierarchy().put(identifier, def, shadow);
	}
	
	@Override
	public default Def setDef(@NonNull String identifier, @NonNull Element value, boolean shadow) {
		return setDef(identifier, new Def(identifier, value), shadow);
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
	
	public default Macro setMacro(@NonNull String identifier, @NonNull Macro macro, boolean shadow) {
		checkCollision(identifier);
		return getMacroHierarchy().put(identifier, macro, shadow);
	}
	
	@Override
	public default Macro setMacro(@NonNull String identifier, @NonNull Invokable invokable) {
		return setMacro(identifier, new Macro(identifier, invokable), true);
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
	
	public default Clazz setClazz(@NonNull String shallowIdentifier, @NonNull Clazz clazz, boolean shadow) {
		checkCollision(shallowIdentifier);
		return getClazzHierarchy().put(shallowIdentifier, clazz, shadow);
	}
	
	@Override
	public default Clazz setClazz(Interpreter interpreter, @NonNull String shallowIdentifier, @NonNull ClazzType type, @Nullable HierarchicalScope base, @NonNull ArrayList<Clazz> supers) {
		return setClazz(shallowIdentifier, new Clazz(interpreter, scopeIdentifier(), shallowIdentifier, type, base, supers), true);
	}
	
	@Override
	public default Clazz removeClazz(@NonNull String shallowIdentifier) {
		return getClazzHierarchy().remove(shallowIdentifier, false);
	}
	
	public Hierarchy<@NonNull String, Def> getDefHierarchy();
	
	public Hierarchy<@NonNull String, Macro> getMacroHierarchy();
	
	public Hierarchy<@NonNull String, Clazz> getClazzHierarchy();
	
	public default <T> void addToScopeMap(Interpreter interpreter, Hierarchy<@NonNull String, T> source, Map<@NonNull Element, @NonNull Element> target) {
		source.forEach((k, v) -> target.put(new StringElement(interpreter, k), new LabelElement(interpreter, this, k)), false);
	}
	
	@Override
	public default void addToScopeMap(TokenExecutor exec, @NonNull Map<@NonNull Element, @NonNull Element> map) {
		addToScopeMap(exec.interpreter, getDefHierarchy(), map);
		addToScopeMap(exec.interpreter, getMacroHierarchy(), map);
		addToScopeMap(exec.interpreter, getClazzHierarchy(), map);
	}
	
	@SuppressWarnings("null")
	public default void putAll(@NonNull HierarchicalScope from, boolean shadow, boolean shallow) {
		from.getDefHierarchy().forEach((k, v) -> setDef(k, v, shadow), shallow);
		from.getMacroHierarchy().forEach((k, v) -> setMacro(k, v, shadow), shallow);
		from.getClazzHierarchy().forEach((k, v) -> setClazz(k, v, shadow), shallow);
	}
}
