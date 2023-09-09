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
	public TokenResult onEqualTo(TokenExecutor exec, @NonNull Element other) {
		exec.push(new BoolElement(INSTANCE.equals(other)));
		return TokenResult.PASS;
	}
	
	@Override
	public TokenResult onNotEqualTo(TokenExecutor exec, @NonNull Element other) {
		exec.push(new BoolElement(!INSTANCE.equals(other)));
		return TokenResult.PASS;
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
