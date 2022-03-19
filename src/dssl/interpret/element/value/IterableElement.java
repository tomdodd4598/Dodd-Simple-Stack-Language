package dssl.interpret.element.value;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.Element;
import dssl.interpret.element.value.primitive.IntElement;

public abstract class IterableElement extends Element implements Iterable<@NonNull Element> {
	
	protected IterableElement() {
		super();
	}
	
	@Override
	public InterpretResult onSize(Executor exec) {
		exec.push(new IntElement(size()));
		return InterpretResult.PASS;
	}
	
	public abstract int size();
	
	@Override
	public abstract Iterator<@NonNull Element> iterator();
	
	public abstract Collection<@NonNull Element> collection();
}
