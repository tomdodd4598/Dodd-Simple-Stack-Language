package dssl.interpret.element;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.primitive.BoolElement;

public class NullElement extends Element {
	
	public static final @NonNull NullElement INSTANCE = new NullElement();
	
	private NullElement() {
		super(BuiltIn.NULL_CLAZZ);
	}
	
	@Override
	public @NonNull TokenResult onEqualTo(TokenExecutor exec, @NonNull Element other) {
		exec.push(new BoolElement(INSTANCE.equals(other)));
		return TokenResult.PASS;
	}
	
	@Override
	public @NonNull TokenResult onNotEqualTo(TokenExecutor exec, @NonNull Element other) {
		exec.push(new BoolElement(!INSTANCE.equals(other)));
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
		return INSTANCE;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(BuiltIn.NULL);
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj == INSTANCE;
	}
	
	@Override
	public @NonNull String toString() {
		return "null";
	}
}
