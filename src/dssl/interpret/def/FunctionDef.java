package dssl.interpret.def;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.element.FunctionElement;
import dssl.node.Token;

public class FunctionDef extends Def<@NonNull FunctionElement> {
	
	protected final List<Token> tokens;
	
	public FunctionDef(@NonNull String identifier, List<Token> tokens) {
		super(identifier);
		this.tokens = tokens;
	}
	
	@Override
	public @NonNull FunctionElement getElement() {
		return new FunctionElement(tokens);
	}
}
