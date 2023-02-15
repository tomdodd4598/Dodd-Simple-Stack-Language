package dssl.interpret;

import java.util.*;
import java.util.function.Function;

import org.eclipse.jdt.annotation.*;

import dssl.*;
import dssl.interpret.element.*;
import dssl.interpret.element.clazz.*;

public class Clazz implements HierarchicalScope {
	
	public final @NonNull String identifier;
	public final @NonNull String shallow;
	public final @NonNull Element elem;
	
	protected final Hierarchy<@NonNull String, Def> defHierarchy;
	protected final Hierarchy<@NonNull String, Macro> macroHierarchy;
	protected final Hierarchy<@NonNull String, Clazz> clazzHierarchy;
	protected final Hierarchy<@NonNull String, Magic> magicHierarchy;
	
	public Clazz(@NonNull String identifier, Clazz... supers) {
		this(null, identifier, null, new ArrayList<>(Arrays.asList(supers)));
	}
	
	public Clazz(String prev, @NonNull String extension, HierarchicalScope base, ArrayList<Clazz> supers) {
		this(prev, extension, base, supers, true);
	}
	
	protected Clazz(String prev, @NonNull String extension, HierarchicalScope base, ArrayList<Clazz> supers, boolean extendsObject) {
		identifier = prev == null ? extension : prev + "." + extension;
		shallow = extension;
		elem = new ClassElement(this);
		if (extendsObject) {
			supers.add(BuiltIn.OBJECT_CLAZZ);
		}
		defHierarchy = getHierarchy(base, supers, HierarchicalScope::getDefHierarchy);
		macroHierarchy = getHierarchy(base, supers, HierarchicalScope::getMacroHierarchy);
		clazzHierarchy = getHierarchy(base, supers, HierarchicalScope::getClazzHierarchy);
		magicHierarchy = getHierarchy(base, supers, HierarchicalScope::getMagicHierarchy);
	}
	
	protected static <K, V> Hierarchy<K, V> getHierarchy(HierarchicalScope base, List<Clazz> supers, Function<HierarchicalScope, Hierarchy<K, V>> function) {
		return (base == null ? new Hierarchy<K, V>() : function.apply(base)).branch(Helpers.map(supers, function));
	}
	
	public static @NonNull Clazz objectClazz() {
		return new Clazz(null, "__object__", null, new ArrayList<>(), false);
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
	
	public TokenResult instantiate(TokenExecutor exec) {
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
	
	protected RuntimeException castError(@NonNull Element elem) {
		return new IllegalArgumentException(String.format("Failed to cast %s \"%s\" to %s!", elem.typeName(), elem.toString(), identifier));
	}
	
	public @Nullable Element castImplicit(@NonNull Element elem) {
		if (elem instanceof ValueElement && ((ValueElement) elem).clazz.equals(this)) {
			return elem;
		}
		else {
			return null;
		}
	}
	
	public @NonNull Element castExplicit(@NonNull Element elem) {
		Element implicit = castImplicit(elem);
		if (implicit != null) {
			return implicit;
		}
		else {
			throw castError(elem);
		}
	}
}
