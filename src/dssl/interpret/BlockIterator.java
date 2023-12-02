package dssl.interpret;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.element.BlockElement;

@FunctionalInterface
public interface BlockIterator {
	
	TokenIterator get(@NonNull BlockElement block);
}
