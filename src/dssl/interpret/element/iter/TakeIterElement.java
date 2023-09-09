package dssl.interpret.element.iter;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.TokenExecutor;
import dssl.interpret.element.Element;

public class TakeIterElement extends IterElement {
	
	protected final IterElement internal;
	protected int take;
	
	public TakeIterElement(IterElement internal, int take) {
		super();
		this.internal = internal;
		this.take = take;
	}
	
	@Override
	public boolean hasNext(TokenExecutor exec) {
		return take > 0 ? internal.hasNext(exec) : false;
	}
	
	@Override
	public @NonNull Element next(TokenExecutor exec) {
		--take;
		return internal.next(exec);
	}
}
