package dssl.interpret.element;

import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNull;

import dssl.Helpers;
import dssl.interpret.TokenExecutor;
import dssl.interpret.element.iter.IterElement;

public interface IterableElement {
	
	public @NonNull IterElement iterator(TokenExecutor exec);
	
	public default Iterable<@NonNull Element> internal(TokenExecutor exec) {
		return () -> iterator(exec).internal(exec);
	}
	
	public default Stream<@NonNull Element> stream(TokenExecutor exec) {
		return Helpers.stream(internal(exec));
	}
}
