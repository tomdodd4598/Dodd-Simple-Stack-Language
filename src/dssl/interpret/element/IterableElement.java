package dssl.interpret.element;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jdt.annotation.NonNull;

public interface IterableElement extends Iterable<@NonNull Element> {
	
	public int size();
	
	@Override
	public Iterator<@NonNull Element> iterator();
	
	public Collection<@NonNull Element> collection();
}
