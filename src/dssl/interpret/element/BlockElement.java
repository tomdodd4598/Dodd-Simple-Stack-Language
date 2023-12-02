package dssl.interpret.element;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import dssl.Helpers;
import dssl.interpret.*;
import dssl.node.Token;

public class BlockElement extends Element implements Invokable {
	
	public final List<@NonNull Token> tokens;
	
	public BlockElement(List<@NonNull Token> tokens) {
		super(BuiltIn.BLOCK_CLAZZ);
		this.tokens = tokens;
	}
	
	public TokenExecutor executor(TokenExecutor exec) {
		return new TokenExecutor(exec.interpreter.blockIteratorImpl.get(this), exec, true);
	}
	
	@Override
	public TokenResult invoke(TokenExecutor exec) {
		return executor(exec).iterate();
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
		return Objects.hash(BuiltIn.BLOCK, tokens);
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
		return Helpers.tokenListToString(tokens);
	}
}
