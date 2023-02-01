package dssl.interpret;

import org.eclipse.jdt.annotation.NonNull;

import dssl.Hierarchy;
import dssl.interpret.magic.Magic;

public interface HierarchicalScope extends Scope {
	
	public Hierarchy<@NonNull String, Def> getDefHierarchy();
	
	public Hierarchy<@NonNull String, Macro> getMacroHierarchy();
	
	public Hierarchy<@NonNull String, Clazz> getClazzHierarchy();
	
	public Hierarchy<@NonNull String, Magic> getMagicHierarchy();
}
