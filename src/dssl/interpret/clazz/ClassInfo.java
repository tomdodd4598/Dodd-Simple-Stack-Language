package dssl.interpret.clazz;

import org.eclipse.jdt.annotation.NonNull;

import dssl.HierarchyMap;
import dssl.interpret.def.Def;
import dssl.interpret.element.value.InstanceElement;

public class ClassInfo {
	
	public final @NonNull String identifier;
	protected final HierarchyMap<String, Def<?>> defMap;
	protected final HierarchyMap<String, ClassInfo> classMap;
	
	protected ClassInfo(@NonNull String identifier, HierarchyMap<String, Def<?>> defMap, HierarchyMap<String, ClassInfo> classMap) {
		this.identifier = identifier;
		this.defMap = defMap;
		this.classMap = classMap;
	}
	
	public void initInstance(InstanceElement element) {
		
	}
}
