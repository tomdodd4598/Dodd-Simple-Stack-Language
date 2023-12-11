package dssl.interpret;

import java.util.*;
import java.util.function.Supplier;

import org.eclipse.jdt.annotation.*;

import dssl.interpret.element.Element;

public interface Scope {
	
	public @Nullable String scopeIdentifier();
	
	public boolean canShadow();
	
	public boolean canDelete();
	
	public default @Nullable TokenResult scopeAction(TokenExecutor exec, @NonNull String identifier) {
		return exec.scopeAction(() -> getDef(identifier), () -> getMacro(identifier), () -> getClazz(identifier));
	}
	
	public default @Nullable Supplier<@NonNull TokenResult> scopeInvokable(TokenExecutor exec, @NonNull String identifier) {
		return exec.scopeInvokable(() -> getDef(identifier), () -> getMacro(identifier), () -> getClazz(identifier));
	}
	
	public default void checkCollision(@NonNull String identifier) {
		if (hasDef(identifier, true)) {
			throw new IllegalArgumentException(String.format("Identifier \"%s\" already used for def!", identifier));
		}
		else if (hasMacro(identifier, true)) {
			throw new IllegalArgumentException(String.format("Identifier \"%s\" already used for macro!", identifier));
		}
		else if (hasClazz(identifier, true)) {
			throw new IllegalArgumentException(String.format("Identifier \"%s\" already used for class!", identifier));
		}
	}
	
	public boolean hasDef(@NonNull String identifier, boolean shallow);
	
	public Def getDef(@NonNull String identifier);
	
	public Def setDef(@NonNull String identifier, @NonNull Element value, boolean shadow);
	
	public Def removeDef(@NonNull String identifier);
	
	public boolean hasMacro(@NonNull String identifier, boolean shallow);
	
	public Macro getMacro(@NonNull String identifier);
	
	public Macro setMacro(@NonNull String identifier, @NonNull Invokable invokable);
	
	public Macro removeMacro(@NonNull String identifier);
	
	public boolean hasClazz(@NonNull String shallowIdentifier, boolean shallow);
	
	public Clazz getClazz(@NonNull String shallowIdentifier);
	
	public Clazz setClazz(Interpreter interpreter, @NonNull String shallowIdentifier, @NonNull ClazzType type, @Nullable HierarchicalScope base, @NonNull ArrayList<Clazz> supers);
	
	public Clazz removeClazz(@NonNull String shallowIdentifier);
	
	public void addToScopeMap(TokenExecutor exec, @NonNull Map<@NonNull Element, @NonNull Element> map);
}
