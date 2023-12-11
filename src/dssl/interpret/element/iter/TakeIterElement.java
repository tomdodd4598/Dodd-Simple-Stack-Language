package dssl.interpret.element.iter;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.Element;

public class TakeIterElement extends IterElement {
	
	protected final IterElement internal;
	protected long take;
	
	public TakeIterElement(Interpreter interpreter, IterElement internal, long take) {
		super(interpreter);
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
