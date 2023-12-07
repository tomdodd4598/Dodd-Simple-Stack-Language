package dssl.interpret;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.*;

import dssl.*;
import dssl.interpret.element.Element;
import dssl.interpret.element.clazz.*;

public class Clazz implements HierarchicalScope {
	
	public final @NonNull String fullIdentifier;
	public final @NonNull String shallowIdentifier;
	
	public final @NonNull ClazzType type;
	
	protected Element elem;
	
	protected final List<Clazz> supers;
	
	protected final Hierarchy<@NonNull String, Def> defHierarchy;
	protected final Hierarchy<@NonNull String, Macro> macroHierarchy;
	protected final Hierarchy<@NonNull String, Clazz> clazzHierarchy;
	protected final Hierarchy<@NonNull String, Magic> magicHierarchy;
	
	public Clazz(@NonNull String identifier, @NonNull ClazzType type, Clazz... supers) {
		this(null, identifier, type, null, new ArrayList<>(Arrays.asList(supers)));
	}
	
	public Clazz(String prev, @NonNull String extension, @NonNull ClazzType type, HierarchicalScope base, ArrayList<Clazz> supers) {
		fullIdentifier = Helpers.extendedIdentifier(prev, extension);
		shallowIdentifier = extension;
		this.type = type;
		
		if (!objectClazzInit) {
			supers.add(BuiltIn.OBJECT_CLAZZ);
		}
		this.supers = supers.stream().distinct().collect(Collectors.toList());
		
		defHierarchy = getHierarchy(base, HierarchicalScope::getDefHierarchy);
		macroHierarchy = getHierarchy(base, HierarchicalScope::getMacroHierarchy);
		clazzHierarchy = getHierarchy(base, HierarchicalScope::getClazzHierarchy);
		magicHierarchy = getHierarchy(base, HierarchicalScope::getMagicHierarchy);
	}
	
	protected <K, V> Hierarchy<K, V> getHierarchy(HierarchicalScope base, Function<HierarchicalScope, Hierarchy<K, V>> function) {
		return (base == null ? new Hierarchy<K, V>() : function.apply(base)).branch(Helpers.map(supers, function));
	}
	
	private static boolean objectClazzInit = false;
	
	public static @NonNull Clazz objectClazz() {
		objectClazzInit = true;
		@NonNull Clazz clazz = new Clazz(BuiltIn.OBJECT, ClazzType.INTERNAL);
		objectClazzInit = false;
		return clazz;
	}
	
	@SuppressWarnings("null")
	public @NonNull Element getElem() {
		if (elem == null) {
			elem = new ClassElement(this);
		}
		return elem;
	}
	
	@Override
	public @Nullable String getIdentifier() {
		return fullIdentifier;
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
	
	public @NonNull TokenResult instantiate(TokenExecutor exec) {
		if (!type.canInstantiate()) {
			throw new IllegalArgumentException(String.format("Can not instantiate instance of class \"%s\"!", fullIdentifier));
		}
		
		InstanceElement instance = new InstanceElement(this);
		TokenResult init = instance.magicAction(exec, "init");
		if (init == null) {
			exec.push(instance);
			return TokenResult.PASS;
		}
		else {
			return init;
		}
	}
	
	public boolean is(@NonNull Clazz clazz) {
		return equals(clazz) || supers.stream().anyMatch(x -> x.is(clazz));
	}
	
	protected RuntimeException castError(@NonNull Element elem) {
		return new IllegalArgumentException(String.format("Failed to cast %s \"%s\" to %s!", elem.typeName(), elem, fullIdentifier));
	}
	
	public @Nullable Element as(TokenExecutor exec, @NonNull Element elem) {
		if (elem.clazz.is(this)) {
			return elem;
		}
		else {
			return null;
		}
	}
	
	public @NonNull Element cast(TokenExecutor exec, @NonNull Element elem) {
		Element implicit = as(exec, elem);
		if (implicit != null) {
			return implicit;
		}
		else {
			throw castError(elem);
		}
	}
}
