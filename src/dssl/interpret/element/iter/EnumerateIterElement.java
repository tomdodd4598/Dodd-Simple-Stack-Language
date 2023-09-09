package dssl.interpret.element.iter;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.TokenExecutor;
import dssl.interpret.element.*;
import dssl.interpret.element.primitive.IntElement;

public class EnumerateIterElement extends IterElement {
	
	protected final IterElement internal;
	
	protected int index = 0;
	
	public EnumerateIterElement(IterElement internal) {
		super();
		this.internal = internal;
	}
	
	@Override
	public boolean hasNext(TokenExecutor exec) {
		return internal.hasNext(exec);
	}
	
	@Override
	public @NonNull Element next(TokenExecutor exec) {
		return new ListElement(new IntElement(index++), internal.next(exec));
	}
}
