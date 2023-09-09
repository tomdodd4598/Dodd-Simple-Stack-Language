package dssl.interpret.element.iter;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.TokenExecutor;
import dssl.interpret.element.Element;

public class SkipIterElement extends IterElement {
	
	protected final IterElement internal;
	protected int skip;
	
	public SkipIterElement(IterElement internal, int skip) {
		super();
		this.internal = internal;
		this.skip = skip;
	}
	
	protected void prepare(TokenExecutor exec) {
		while (skip-- > 0 && internal.hasNext(exec)) {
			internal.next(exec);
		}
	}
	
	@Override
	public boolean hasNext(TokenExecutor exec) {
		prepare(exec);
		return internal.hasNext(exec);
	}
	
	@Override
	public @NonNull Element next(TokenExecutor exec) {
		prepare(exec);
		return internal.next(exec);
	}
}
