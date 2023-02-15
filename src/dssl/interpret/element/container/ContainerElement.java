package dssl.interpret.element.container;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.Clazz;
import dssl.interpret.element.ValueElement;
import dssl.interpret.element.primitive.StringElement;

public abstract class ContainerElement extends ValueElement {
	
	protected ContainerElement(@NonNull Clazz clazz) {
		super(clazz);
	}
	
	@Override
	public StringElement stringCast(boolean explicit) {
		return explicit ? new StringElement(toString()) : null;
	}
}
