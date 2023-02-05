package dssl.interpret;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import dssl.*;
import dssl.interpret.element.Element;
import dssl.interpret.element.clazz.ClassElement;

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
	public String getIdentifier() {
		return identifier;
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
