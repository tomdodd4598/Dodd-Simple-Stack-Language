package dssl.interpret.element.iter;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.Element;

public class MapIterElement extends IterElement {
	
	protected final IterElement internal;
	protected final Invokable invokable;
	
	public MapIterElement(Interpreter interpreter, IterElement internal, Invokable invokable) {
		super(interpreter);
		this.internal = internal;
		this.invokable = invokable;
	}
	
	@Override
	public boolean hasNext(TokenExecutor exec) {
		return internal.hasNext(exec);
	}
	
	@Override
	public @NonNull Element next(TokenExecutor exec) {
		exec.push(internal.next(exec));
		invokable.invoke(exec);
		return exec.pop();
	}
}
