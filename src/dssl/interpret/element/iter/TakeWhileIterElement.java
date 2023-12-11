package dssl.interpret.element.iter;

import org.eclipse.jdt.annotation.*;

import dssl.interpret.*;
import dssl.interpret.element.Element;
import dssl.interpret.element.primitive.BoolElement;

public class TakeWhileIterElement extends IterElement {
	
	protected final IterElement internal;
	protected final Invokable invokable;
	
	protected @Nullable Element next = null;
	protected boolean end = false;
	
	public TakeWhileIterElement(Interpreter interpreter, IterElement internal, Invokable invokable) {
		super(interpreter);
		this.internal = internal;
		this.invokable = invokable;
	}
	
	protected void prepare(TokenExecutor exec) {
		if (next == null && !end) {
			if (internal.hasNext(exec)) {
				@NonNull Element elem = internal.next(exec);
				exec.push(elem);
				invokable.invoke(exec);
				
				BoolElement result = exec.pop().asBool(exec);
				if (result == null) {
					throw new IllegalArgumentException(String.format("Built-in method \"takeWhile\" requires \"%s -> %s\" %s element as argument!", BuiltIn.OBJECT, BuiltIn.BOOL, BuiltIn.BLOCK));
				}
				
				if (result.primitiveBool()) {
					next = elem;
					return;
				}
			}
			
			end = true;
		}
	}
	
	@Override
	public boolean hasNext(TokenExecutor exec) {
		prepare(exec);
		return !end;
	}
	
	@SuppressWarnings("null")
	@Override
	public @NonNull Element next(TokenExecutor exec) {
		prepare(exec);
		@NonNull Element elem = next;
		next = null;
		return elem;
	}
}
