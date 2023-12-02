package dssl.interpret;

import java.util.ArrayList;

import org.eclipse.jdt.annotation.*;

import dssl.interpret.element.Element;

public interface Scope {
	
	public @Nullable String getIdentifier();
	
	public default @Nullable TokenResult scopeAction(TokenExecutor exec, @NonNull String identifier) {
		return exec.scopeAction(() -> getDef(identifier), () -> getMacro(identifier), () -> getClazz(identifier));
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
	
	public void setDef(@NonNull String identifier, @NonNull Element value, boolean shadow);
	
	public boolean hasMacro(@NonNull String identifier, boolean shallow);
	
	public Macro getMacro(@NonNull String identifier);
	
	public void setMacro(@NonNull String identifier, @NonNull Invokable invokable);
	
	public boolean hasClazz(@NonNull String shallowIdentifier, boolean shallow);
	
	public Clazz getClazz(@NonNull String shallowIdentifier);
	
	public void setClazz(@NonNull String shallowIdentifier, @NonNull ClazzType type, HierarchicalScope base, ArrayList<Clazz> supers);
	
	public boolean hasMagic(@NonNull String identifier, boolean shallow);
	
	public Magic getMagic(@NonNull String identifier);
	
	public void setMagic(@NonNull String identifier, @NonNull Invokable invokable);
}
