package dssl.interpret;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import dssl.*;
import dssl.interpret.element.*;
import dssl.interpret.element.clazz.ClassElement;
import dssl.interpret.magic.Magic;

public class Clazz implements HierarchicalScope {
	
	public final @NonNull String identifier;
	public final @NonNull String shallow;
	public final @NonNull Element elem;
	
	protected final Hierarchy<@NonNull String, Def> defHierarchy;
	protected final Hierarchy<@NonNull String, Macro> macroHierarchy;
	protected final Hierarchy<@NonNull String, Clazz> clazzHierarchy;
	protected final Hierarchy<@NonNull String, Magic> magicHierarchy;
	
	public Clazz(String prev, @NonNull String extension, HierarchicalScope base, List<@NonNull Clazz> supers) {
		identifier = prev == null ? extension : prev + "." + extension;
		shallow = extension;
		elem = new ClassElement(this);
		defHierarchy = base.getDefHierarchy().branch(Helpers.map(supers, Clazz::getDefHierarchy));
		macroHierarchy = base.getMacroHierarchy().branch(Helpers.map(supers, Clazz::getMacroHierarchy));
		clazzHierarchy = base.getClazzHierarchy().branch(Helpers.map(supers, Clazz::getClazzHierarchy));
		magicHierarchy = base.getMagicHierarchy().branch(Helpers.map(supers, Clazz::getMagicHierarchy));
	}
	
	@Override
	public boolean hasDef(@NonNull String identifier) {
		return defHierarchy.containsKey(identifier);
	}
	
	@Override
	public Def getDef(@NonNull String identifier) {
		return defHierarchy.get(identifier);
	}
	
	@Override
	public void setDef(@NonNull String identifier, @NonNull Element value, boolean shadow) {
		checkDef(identifier);
		defHierarchy.put(identifier, new Def(identifier, value), shadow);
	}
	
	@Override
	public boolean hasMacro(@NonNull String identifier) {
		return macroHierarchy.containsKey(identifier);
	}
	
	@Override
	public Macro getMacro(@NonNull String identifier) {
		return macroHierarchy.get(identifier);
	}
	
	@Override
	public void setMacro(@NonNull String identifier, @NonNull BlockElement block) {
		checkMacro(identifier);
		macroHierarchy.put(identifier, new Macro(identifier, block), true);
	}
	
	@Override
	public boolean hasClazz(@NonNull String shallow) {
		return clazzHierarchy.containsKey(shallow);
	}
	
	@Override
	public Clazz getClazz(@NonNull String shallow) {
		return clazzHierarchy.get(shallow);
	}
	
	@Override
	public void setClazz(@NonNull String shallow, HierarchicalScope base, List<@NonNull Clazz> supers) {
		checkClazz(shallow);
		clazzHierarchy.put(shallow, new Clazz(identifier, shallow, base, supers), true);
	}
	
	@Override
	public boolean hasMagic(@NonNull String identifier) {
		return magicHierarchy.containsKey(identifier);
	}
	
	@Override
	public Magic getMagic(@NonNull String identifier) {
		return magicHierarchy.get(identifier);
	}
	
	@Override
	public Hierarchy<@NonNull String, Def> getDefHierarchy() {
		return defHierarchy;
	}
	
	@Override
	public Hierarchy<@NonNull String, Macro> getMacroHierarchy() {
		return macroHierarchy;
	}
	
	@Override
	public Hierarchy<@NonNull String, Clazz> getClazzHierarchy() {
		return clazzHierarchy;
	}
	
	@Override
	public Hierarchy<@NonNull String, Magic> getMagicHierarchy() {
		return magicHierarchy;
	}
}
