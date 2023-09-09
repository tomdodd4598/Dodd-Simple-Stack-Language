package dssl.interpret.element.iter;

import org.eclipse.jdt.annotation.*;

import dssl.interpret.*;
import dssl.interpret.element.*;

public class FlattenIterElement extends IterElement {
	
	protected final IterElement internal;
	
	protected @Nullable IterElement current = null;
	protected boolean end = false;
	
	public FlattenIterElement(IterElement internal) {
		super();
		this.internal = internal;
	}
	
	protected void prepare(TokenExecutor exec) {
		while (!end) {
			if (current != null) {
				if (current.hasNext(exec)) {
					return;
				}
				else {
					current = null;
				}
			}
			
			if (internal.hasNext(exec)) {
				@NonNull Element elem = internal.next(exec);
				if (!(elem instanceof IterableElement)) {
					throw new IllegalArgumentException(String.format("Built-in method \"flatten\" requires \"() -> %s\" %s element!", BuiltIn.ITERABLE, BuiltIn.ITER));
				}
				current = ((IterableElement) elem).iterator(exec);
			}
			else {
				end = true;
			}
		}
	}
	
	@Override
	public boolean hasNext(TokenExecutor exec) {
		prepare(exec);
		return !end;
	}
	
	@Override
	public @NonNull Element next(TokenExecutor exec) {
		prepare(exec);
		return current.next(exec);
	}
}
