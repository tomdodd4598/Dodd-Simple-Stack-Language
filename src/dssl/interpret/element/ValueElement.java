package dssl.interpret.element;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.primitive.BoolElement;

public abstract class ValueElement extends Element {
	
	public final @NonNull Clazz clazz;
	
	public ValueElement(@NonNull Clazz clazz) {
		super();
		this.clazz = clazz;
	}
	
	@Override
	public @NonNull String typeName() {
		return clazz.identifier;
	}
	
	@Override
	public TokenResult onEqualTo(TokenExecutor exec, @NonNull Element other) {
		if (NullElement.INSTANCE.equals(other)) {
			exec.push(new BoolElement(false));
			return TokenResult.PASS;
		}
		return super.onEqualTo(exec, other);
	}
	
	@Override
	public TokenResult onNotEqualTo(TokenExecutor exec, @NonNull Element other) {
		if (NullElement.INSTANCE.equals(other)) {
			exec.push(new BoolElement(true));
			return TokenResult.PASS;
		}
		return super.onNotEqualTo(exec, other);
	}
}
