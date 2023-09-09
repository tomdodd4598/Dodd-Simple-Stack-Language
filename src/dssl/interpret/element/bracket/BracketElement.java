package dssl.interpret.element.bracket;

import dssl.interpret.BuiltIn;
import dssl.interpret.element.Element;

public abstract class BracketElement extends Element {
	
	protected BracketElement() {
		super(BuiltIn.BRACKET_CLAZZ);
	}
	
}
