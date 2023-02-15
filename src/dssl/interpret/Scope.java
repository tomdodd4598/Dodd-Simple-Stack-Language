package dssl.interpret;

import java.util.ArrayList;

import org.eclipse.jdt.annotation.*;

import dssl.interpret.element.Element;

public interface Scope extends ScopeAccessor {
	
	@Override
	public default @Nullable TokenResult scopeAction(TokenExecutor exec, @NonNull String identifier) {
		return exec.scopeAction(() -> getDef(identifier), () -> getMacro(identifier), () -> getClazz(identifier));
	}
	
	public default void checkDef(@NonNull String identifier) {
		if (hasMacro(identifier)) {
			throw new IllegalArgumentException(String.format("Identifier \"%s\" already used for macro!", identifier));
		}
		else if (hasClazz(identifier)) {
			throw new IllegalArgumentException(String.format("Identifier \"%s\" already used for class!", identifier));
		}
	}
	
	public default void checkMacro(@NonNull String identifier) {
		if (hasDef(identifier)) {
			throw new IllegalArgumentException(String.format("Identifier \"%s\" already used for def!", identifier));
		}
		else if (hasClazz(identifier)) {
			throw new IllegalArgumentException(String.format("Identifier \"%s\" already used for class!", identifier));
		}
	}
	
	public default void checkClazz(@NonNull String shallow) {
		if (hasDef(shallow)) {
			throw new IllegalArgumentException(String.format("Identifier \"%s\" already used for def!", shallow));
		}
		else if (hasMacro(shallow)) {
			throw new IllegalArgumentException(String.format("Identifier \"%s\" already used for macro!", shallow));
		}
	}
	
	public boolean hasDef(@NonNull String identifier);
	
	public Def getDef(@NonNull String identifier);
	
	public void setDef(@NonNull String identifier, @NonNull Element value, boolean shadow);
	
	public boolean hasMacro(@NonNull String identifier);
	
	public Macro getMacro(@NonNull String identifier);
	
	public void setMacro(@NonNull String identifier, @NonNull Invokable invokable);
	
	public boolean hasClazz(@NonNull String shallow);
	
	public Clazz getClazz(@NonNull String shallow);
	
	public void setClazz(@NonNull String shallow, HierarchicalScope base, ArrayList<Clazz> supers);
	
	public boolean hasMagic(@NonNull String identifier);
	
	public Magic getMagic(@NonNull String identifier);
	
	public void setMagic(@NonNull String identifier, @NonNull Invokable invokable);
}
