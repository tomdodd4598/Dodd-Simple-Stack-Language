package dssl.interpret.element.iter;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.*;

public class ZipIterElement extends IterElement {
	
	protected final IterElement first, second;
	
	public ZipIterElement(Interpreter interpreter, IterElement first, IterElement second) {
		super(interpreter);
		this.first = first;
		this.second = second;
	}
	
	@Override
	public boolean hasNext(TokenExecutor exec) {
		return first.hasNext(exec) && second.hasNext(exec);
	}
	
	@Override
	public @NonNull Element next(TokenExecutor exec) {
		return new ListElement(interpreter, first.next(exec), second.next(exec));
	}
}
