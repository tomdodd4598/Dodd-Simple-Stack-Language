package dssl.interpret;

import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.element.Element;
import dssl.interpret.magic.Magic;

public interface Scope {
	
	public Def getDef(String identifier);
	
	public void setDef(@NonNull String identifier, @NonNull Element value, boolean shadow);
	
	public Clazz getClazz(String shallow);
	
	public void setClazz(@NonNull String shallow, Map<String, Def> defMap, Map<String, Clazz> clazzMap, Map<String, Magic> magicMap);
}
