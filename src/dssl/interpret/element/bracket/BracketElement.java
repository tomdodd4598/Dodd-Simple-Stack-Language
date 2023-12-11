package dssl.interpret.element.bracket;

import dssl.interpret.Interpreter;
import dssl.interpret.element.Element;

public abstract class BracketElement extends Element {
	
	protected BracketElement(Interpreter interpreter) {
		super(interpreter, interpreter.builtIn.bracketClazz);
	}
	
}
