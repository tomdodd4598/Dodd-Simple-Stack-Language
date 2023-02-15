package dssl.interpret.element;

import org.eclipse.jdt.annotation.*;

import dssl.interpret.*;
import dssl.interpret.element.primitive.BoolElement;

public abstract class ValueElement extends Element implements ScopeAccessor {
	
	public final @NonNull Clazz clazz;
	
	public ValueElement(@NonNull Clazz clazz) {
		super();
		this.clazz = clazz;
	}
	
	@Override
	public final @NonNull String typeName() {
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
	
	@Override
	public @Nullable TokenResult scopeAction(TokenExecutor exec, @NonNull String identifier) {
		exec.push(this);
		return clazz.scopeAction(exec, identifier);
	}
	
	public RuntimeException memberAccessError(@NonNull String member) {
		return new IllegalArgumentException(String.format("Value member \"%s.%s\" not defined!", clazz.identifier, member));
	}
	
	@Override
	public abstract @NonNull Element clone();
	
	@Override
	public int hash() {
		return hashCode();
	}
}
