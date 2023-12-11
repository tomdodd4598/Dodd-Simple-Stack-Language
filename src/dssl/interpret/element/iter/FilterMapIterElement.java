package dssl.interpret.element.iter;

import org.eclipse.jdt.annotation.*;

import dssl.interpret.*;
import dssl.interpret.element.Element;

public class FilterMapIterElement extends IterElement {
	
	protected final IterElement internal;
	protected final Invokable invokable;
	
	protected @Nullable Element next = null;
	protected boolean end = false;
	
	public FilterMapIterElement(Interpreter interpreter, IterElement internal, Invokable invokable) {
		super(interpreter);
		this.internal = internal;
		this.invokable = invokable;
	}
	
	protected void prepare(TokenExecutor exec) {
		if (next == null && !end) {
			while (internal.hasNext(exec)) {
				exec.push(internal.next(exec));
				invokable.invoke(exec);
				
				@NonNull Element result = exec.pop();
				if (!result.equals(interpreter.builtIn.nullElement)) {
					next = result;
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
