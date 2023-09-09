package dssl.interpret.element.iter;

import org.eclipse.jdt.annotation.*;

import dssl.interpret.*;
import dssl.interpret.element.Element;

public class FlatMapIterElement extends IterElement {
	
	protected final IterElement internal;
	protected final Invokable invokable;
	
	protected @Nullable IterElement current = null;
	protected boolean end = false;
	
	public FlatMapIterElement(IterElement internal, Invokable invokable) {
		super();
		this.internal = internal;
		this.invokable = invokable;
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
				exec.push(internal.next(exec));
				invokable.invoke(exec);
				
				@NonNull Element result = exec.pop();
				if (!(result instanceof IterElement)) {
					throw new IllegalArgumentException(String.format("Built-in method \"flatMap\" requires \"%s -> %s\" %s element as argument!", BuiltIn.OBJECT, BuiltIn.ITER, BuiltIn.BLOCK));
				}
				current = (IterElement) result;
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
