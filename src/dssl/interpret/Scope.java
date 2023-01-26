package dssl.interpret;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.element.Element;

public interface Scope {
	
	public Def getDef(@NonNull String identifier);
	
	public void setDef(@NonNull String identifier, @NonNull Element value, boolean shadow);
	
	public Clazz getClazz(@NonNull String shallow);
	
	public void setClazz(@NonNull String shallow, ScopeMaps maps);
	
	public ScopeMaps getMaps();
}
