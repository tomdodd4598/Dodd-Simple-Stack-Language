package dssl.interpret;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import dssl.HierarchyMap;
import dssl.interpret.clazz.ClassInfo;
import dssl.interpret.def.*;
import dssl.interpret.element.Element;
import dssl.node.Token;

public abstract class Interpreter {
	
	protected final Iterator<Token> iterator;
	
	protected final Interpreter previous;
	
	protected final HierarchyMap<String, Def<?>> defMap;
	protected final HierarchyMap<String, ClassInfo> classMap;
	
	protected Interpreter(Iterator<Token> iterator, Interpreter previous) {
		this.iterator = iterator;
		this.previous = previous;
		if (previous == null) {
			defMap = new HierarchyMap<>(null);
			classMap = new HierarchyMap<>(null);
		}
		else {
			defMap = new HierarchyMap<>(previous.defMap);
			classMap = new HierarchyMap<>(previous.classMap);
		}
	}
	
	public InterpretResult interpret() {
		loop: while (iterator.hasNext()) {
			InterpretResult readResult = read(iterator.next());
			switch (readResult) {
				case PASS:
					continue loop;
				default:
					return readResult;
			}
		}
		return InterpretResult.PASS;
	}
	
	protected abstract InterpretResult read(Token token);
	
	private <T extends @NonNull Element> void setDef(Def<T> def, boolean shadow) {
		defMap.put(def.identifier, def, shadow);
	}
	
	protected Def<?> getDef(String identifier) {
		return defMap.get(identifier);
	}
	
	protected void setVariable(@NonNull String identifier, @NonNull Element value, boolean shadow) {
		setDef(new VariableDef(identifier, value), shadow);
	}
	
	protected void setFunction(@NonNull String identifier, List<Token> tokens, boolean shadow) {
		setDef(new FunctionDef(identifier, tokens), shadow);
	}
}
