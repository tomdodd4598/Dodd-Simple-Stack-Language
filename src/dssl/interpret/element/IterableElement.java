package dssl.interpret.element;

import dssl.interpret.TokenExecutor;

public interface IterableElement<T> extends Iterable<T> {
	
	public void onEach(TokenExecutor exec, Object item);
}
