package dssl.interpret.element;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.TokenExecutor;
import dssl.interpret.element.primitive.StringElement;
import dssl.node.Token;

public class BlockElement extends Element {
	
	protected final List<@NonNull Token> tokens;
	
	public BlockElement(List<@NonNull Token> tokens) {
		super();
		this.tokens = tokens;
	}
	
	@Override
	public @NonNull String typeName() {
		return "block";
	}
	
	@Override
	public @NonNull StringElement stringCastExplicit() {
		throw castError("string");
	}
	
	public TokenExecutor executor(TokenExecutor exec) {
		return new TokenExecutor(tokens.iterator(), exec);
	}
	
	@SuppressWarnings("null")
	@Override
	public @NonNull Element clone() {
		List<@NonNull Token> tokensClone = new ArrayList<>(tokens.size());
		for (@NonNull Token token : tokens) {
			tokensClone.add((@NonNull Token) token.clone());
		}
		return new BlockElement(tokensClone);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash("block", tokens);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BlockElement) {
			BlockElement other = (BlockElement) obj;
			return tokens.equals(other.tokens);
		}
		return false;
	}
	
	@Override
	public @NonNull String toString() {
		return "block:" + tokens.toString();
	}
	
	@Override
	public @NonNull String toDebugString() {
		return "{...}";
	}
}
