package dssl.interpret.element.bracket;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.Element;
import dssl.interpret.element.primitive.BoolElement;

public class DictRBracketElement extends RBracketElement {
	
	public DictRBracketElement(Interpreter interpreter) {
		super(interpreter);
	}
	
	@Override
	public @NonNull TokenResult onEqualTo(TokenExecutor exec, @NonNull Element other) {
		exec.push(new BoolElement(interpreter, interpreter.builtIn.dictRBracketElement.equals(other)));
		return TokenResult.PASS;
	}
	
	@Override
	public @NonNull TokenResult onNotEqualTo(TokenExecutor exec, @NonNull Element other) {
		exec.push(new BoolElement(interpreter, !interpreter.builtIn.dictRBracketElement.equals(other)));
		return TokenResult.PASS;
	}
	
	@Override
	public @NonNull TokenResult __eq__(TokenExecutor exec, @NonNull Element other) {
		return onEqualTo(exec, other);
	}
	
	@Override
	public @NonNull TokenResult __ne__(TokenExecutor exec, @NonNull Element other) {
		return onNotEqualTo(exec, other);
	}
	
	@Override
	public @NonNull Element clone() {
		return interpreter.builtIn.dictRBracketElement;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash("|]");
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj == interpreter.builtIn.dictRBracketElement;
	}
	
	@Override
	public @NonNull String toString(TokenExecutor exec) {
		return "|]";
	}
}
