package dssl.interpret.element;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.primitive.BoolElement;

public class NullElement extends ValueElement {
	
	public static final @NonNull NullElement INSTANCE = new NullElement();
	
	private NullElement() {
		super(BuiltIn.NULL_CLAZZ);
	}
	
	@Override
	public TokenResult onEqualTo(TokenExecutor exec, @NonNull Element other) {
		if (other instanceof ValueElement) {
			exec.push(new BoolElement(INSTANCE.equals(other)));
			return TokenResult.PASS;
		}
		throw binaryOpError("==", other);
	}
	
	@Override
	public TokenResult onNotEqualTo(TokenExecutor exec, @NonNull Element other) {
		if (other instanceof ValueElement) {
			exec.push(new BoolElement(!INSTANCE.equals(other)));
			return TokenResult.PASS;
		}
		throw binaryOpError("!=", other);
	}
	
	@Override
	public @NonNull Element clone() {
		return INSTANCE;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash("null");
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj == INSTANCE;
	}
	
	@Override
	public @NonNull String toString() {
		return "null";
	}
	
	@Override
	public @NonNull String debugString() {
		return "null";
	}
}
