package dssl.interpret.element.clazz;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import dssl.interpret.*;
import dssl.interpret.element.*;
import dssl.interpret.element.iter.IterElement;
import dssl.interpret.element.primitive.StringElement;

public class InstanceElement extends Element implements Scope {
	
	protected final Map<@NonNull String, Def> defMap;
	protected final Map<@NonNull String, Macro> macroMap;
	protected final Map<@NonNull String, Clazz> clazzMap;
	
	public final @NonNull String scopeIdentifier;
	
	public InstanceElement(Interpreter interpreter, @NonNull Clazz clazz) {
		this(interpreter, clazz, new HashMap<>(), new HashMap<>(), new HashMap<>());
	}
	
	protected InstanceElement(Interpreter interpreter, @NonNull Clazz clazz, Map<@NonNull String, Def> defMap, Map<@NonNull String, Macro> macroMap, Map<@NonNull String, Clazz> clazzMap) {
		super(interpreter, clazz);
		this.defMap = defMap;
		this.macroMap = macroMap;
		this.clazzMap = clazzMap;
		scopeIdentifier = toString();
	}
	
	@Override
	public @Nullable String scopeIdentifier() {
		return scopeIdentifier;
	}
	
	@Override
	public boolean canShadow() {
		return true;
	}
	
	@Override
	public boolean canDelete() {
		return true;
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
	public Def setDef(@NonNull String identifier, @NonNull Element value, boolean shadow) {
		if (shadow) {
			checkCollision(identifier);
		}
		return defMap.put(identifier, new Def(identifier, value));
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
	public Macro setMacro(@NonNull String identifier, @NonNull Invokable invokable) {
		checkCollision(identifier);
		return macroMap.put(identifier, new Macro(identifier, invokable));
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
	public Clazz setClazz(Interpreter interpreter, @NonNull String shallowIdentifier, @NonNull ClazzType type, @Nullable HierarchicalScope base, @NonNull ArrayList<Clazz> supers) {
		checkCollision(shallowIdentifier);
		return clazzMap.put(shallowIdentifier, new Clazz(interpreter, scopeIdentifier, shallowIdentifier, type, base, supers));
	}
	
	@Override
	public Clazz removeClazz(@NonNull String shallowIdentifier) {
		return clazzMap.remove(shallowIdentifier);
	}
	
	@Override
	public @Nullable IterElement iterator(TokenExecutor exec) {
		TokenResult result = memberAction(exec, "__iter__");
		return result == null ? null : (IterElement) exec.pop();
	}
	
	protected <T> void addToScopeMap(Map<@NonNull String, T> source, Map<@NonNull Element, @NonNull Element> target) {
		for (String key : source.keySet()) {
			target.put(new StringElement(interpreter, key), new LabelElement(interpreter, this, key));
		}
	}
	
	@Override
	public void addToScopeMap(TokenExecutor exec, @NonNull Map<@NonNull Element, @NonNull Element> map) {
		addToScopeMap(defMap, map);
		addToScopeMap(macroMap, map);
		addToScopeMap(clazzMap, map);
	}
	
	@Override
	public @Nullable Scope getMemberScope(@NonNull MemberAccessType access) {
		return access.equals(MemberAccessType.STATIC) ? this : clazz;
	}
	
	@Override
	public @NonNull Element clone() {
		return new InstanceElement(interpreter, clazz, new HashMap<>(defMap), new HashMap<>(macroMap), new HashMap<>(clazzMap));
	}
	
	@Override
	public int hashCode() {
		return Objects.hash("instance", clazz, defMap, clazzMap);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof InstanceElement) {
			InstanceElement other = (InstanceElement) obj;
			return clazz.equals(other.clazz) && defMap.equals(other.defMap) && clazzMap.equals(other.clazzMap);
		}
		return false;
	}
}
