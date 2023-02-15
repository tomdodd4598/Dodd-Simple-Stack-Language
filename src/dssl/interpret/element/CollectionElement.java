package dssl.interpret.element;

import java.util.Collection;

import org.eclipse.jdt.annotation.NonNull;

public interface CollectionElement extends IterableElement<@NonNull Element> {
	
	public Collection<@NonNull Element> collection();
}
