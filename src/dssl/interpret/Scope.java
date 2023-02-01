package dssl.interpret;

import java.util.List;
import java.util.function.BiPredicate;

import org.eclipse.jdt.annotation.NonNull;

import dssl.Helpers.Pair;
import dssl.interpret.element.*;
import dssl.interpret.magic.Magic;

public interface Scope {
	
	static class CheckPair extends Pair<BiPredicate<Scope, @NonNull String>, String> {
		
		CheckPair(BiPredicate<Scope, @NonNull String> left, String right) {
			super(left, right);
		}
	}
	
	static CheckPair CHECK_PAIR_DEF = new CheckPair(Scope::hasDef, "def");
	static CheckPair CHECK_PAIR_MACRO = new CheckPair(Scope::hasMacro, "macro");
	static CheckPair CHECK_PAIR_CLAZZ = new CheckPair(Scope::hasClazz, "class");
	
	public default void check(@NonNull String identifier, CheckPair... pairs) {
		for (CheckPair pair : pairs) {
			if (pair.left.test(this, identifier)) {
				throw new IllegalArgumentException(String.format("Identifier \"%s\" already used for %s!", identifier, pair.right));
			}
		}
	}
	
	public default void checkDef(@NonNull String identifier) {
		check(identifier, CHECK_PAIR_MACRO, CHECK_PAIR_CLAZZ);
	}
	
	public default void checkMacro(@NonNull String identifier) {
		check(identifier, CHECK_PAIR_DEF, CHECK_PAIR_CLAZZ);
	}
	
	public default void checkClazz(@NonNull String shallow) {
		check(shallow, CHECK_PAIR_DEF, CHECK_PAIR_MACRO);
	}
	
	public boolean hasDef(@NonNull String identifier);
	
	public Def getDef(@NonNull String identifier);
	
	public void setDef(@NonNull String identifier, @NonNull Element value, boolean shadow);
	
	public boolean hasMacro(@NonNull String identifier);
	
	public Macro getMacro(@NonNull String identifier);
	
	public void setMacro(@NonNull String identifier, @NonNull BlockElement block);
	
	public boolean hasClazz(@NonNull String shallow);
	
	public Clazz getClazz(@NonNull String shallow);
	
	public void setClazz(@NonNull String shallow, HierarchicalScope base, List<@NonNull Clazz> supers);
	
	public boolean hasMagic(@NonNull String identifier);
	
	public Magic getMagic(@NonNull String identifier);
}
