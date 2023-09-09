package dssl.interpret.element.ref;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;

public interface RefElement {
	
	public Def getDef();
	
	public Const getConst();
	
	public Macro getMacro();
	
	public Clazz getClazz();
	
	public @NonNull String refIdentifier();
}
