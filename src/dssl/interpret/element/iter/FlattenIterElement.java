package dssl.interpret.element.iter;

import org.eclipse.jdt.annotation.*;

import dssl.interpret.*;
import dssl.interpret.element.Element;

public class FlattenIterElement extends IterElement {
	
	protected final IterElement internal;
	
	protected @Nullable IterElement current = null;
	protected boolean end = false;
	
	public FlattenIterElement(Interpreter interpreter, IterElement internal) {
		super(interpreter);
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
				@Nullable IterElement iter = internal.next(exec).iterator(exec);
				if (iter == null) {
					throw new IllegalArgumentException(String.format("Built-in method \"flatten\" requires \"() -> %s\" %s element!", BuiltIn.ITERABLE, BuiltIn.ITER));
				}
				current = iter;
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
