package dssl.interpret.element.iter;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.TokenExecutor;
import dssl.interpret.element.Element;

public class ChainIterElement extends IterElement {
	
	protected final IterElement first, second;
	protected boolean start = true;
	
	public ChainIterElement(IterElement first, IterElement second) {
		super();
		this.first = first;
		this.second = second;
	}
	
	protected void prepare(TokenExecutor exec) {
		if (start && !first.hasNext(exec)) {
			start = false;
		}
	}
	
	@Override
	public boolean hasNext(TokenExecutor exec) {
		prepare(exec);
		return (start ? first : second).hasNext(exec);
	}
	
	@Override
	public @NonNull Element next(TokenExecutor exec) {
		prepare(exec);
		return (start ? first : second).next(exec);
	}
}
