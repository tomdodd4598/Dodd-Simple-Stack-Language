package dssl.interpret.element;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.value.primitive.StringElement;
import dssl.node.Token;

public class FunctionElement extends Element {
	
	public final List<Token> tokens;
	
	public FunctionElement(List<Token> tokens) {
		super();
		this.tokens = tokens;
	}
	
	@Override
	public @NonNull String typeName() {
		return "function";
	}
	
	@Override
	public @NonNull StringElement stringCastExplicit() {
		throw castError("string");
	}
	
	public InterpretResult invoke(Executor executor) {
		return new Executor(tokens.iterator(), executor).interpret();
	}
	
	@Override
	public @NonNull Element clone() {
		List<Token> tokensClone = new ArrayList<>(tokens.size());
		for (Token token : tokens) {
			tokensClone.add((Token) token.clone());
		}
		return new FunctionElement(tokensClone);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash("function", tokens);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FunctionElement) {
			FunctionElement other = (FunctionElement) obj;
			return tokens.equals(other.tokens);
		}
		return false;
	}
	
	@Override
	public @NonNull String toString() {
		return "function:" + tokens.toString();
	}
	
	@Override
	public @NonNull String toBriefDebugString() {
		return "{...}";
	}
}
