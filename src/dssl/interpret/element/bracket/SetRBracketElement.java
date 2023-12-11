package dssl.interpret.element.bracket;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.Element;

public class SetRBracketElement extends RBracketElement {
	
	public SetRBracketElement(Interpreter interpreter) {
		super(interpreter);
	}
	
	@Override
	public @NonNull Element clone() {
		return interpreter.builtIn.setRBracketElement;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash("|)");
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj == interpreter.builtIn.setRBracketElement;
	}
	
	@Override
	public @NonNull String toString(TokenExecutor exec) {
		return "|)";
	}
}
