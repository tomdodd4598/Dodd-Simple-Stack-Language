package dssl.interpret.element;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import dssl.Helpers;
import dssl.interpret.*;
import dssl.node.Token;

public class BlockElement extends Element implements Invokable {
	
	public final List<@NonNull Token> tokens;
	
	public BlockElement(Interpreter interpreter, List<@NonNull Token> tokens) {
		super(interpreter, interpreter.builtIn.blockClazz);
		this.tokens = tokens;
	}
	
	public TokenExecutor executor(TokenExecutor exec) {
		return new TokenExecutor(exec.interpreter.hooks.getBlockIterator(exec, this), exec, true);
	}
	
	@Override
	public @NonNull TokenResult invoke(TokenExecutor exec) {
		return executor(exec).iterate();
	}
	
	@SuppressWarnings("null")
	@Override
	public @NonNull Element clone() {
		List<@NonNull Token> tokensClone = new ArrayList<>(tokens.size());
		for (@NonNull Token token : tokens) {
			tokensClone.add((@NonNull Token) token.clone());
		}
		return new BlockElement(interpreter, tokensClone);
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
	public @NonNull String toString(TokenExecutor exec) {
		return Helpers.tokenListToString(tokens);
	}
}
