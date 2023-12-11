package dssl.interpret.element;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.primitive.BoolElement;

public class NullElement extends Element {
	
	public NullElement(Interpreter interpreter) {
		super(interpreter, interpreter.builtIn.nullClazz);
	}
	
	@Override
	public @NonNull TokenResult onEqualTo(TokenExecutor exec, @NonNull Element other) {
		exec.push(new BoolElement(interpreter, interpreter.builtIn.nullElement.equals(other)));
		return TokenResult.PASS;
	}
	
	@Override
	public @NonNull TokenResult onNotEqualTo(TokenExecutor exec, @NonNull Element other) {
		exec.push(new BoolElement(interpreter, !interpreter.builtIn.nullElement.equals(other)));
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
		return interpreter.builtIn.nullElement;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(BuiltIn.NULL);
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj == interpreter.builtIn.nullElement;
	}
	
	@Override
	public @NonNull String toString(TokenExecutor exec) {
		return "null";
	}
}
