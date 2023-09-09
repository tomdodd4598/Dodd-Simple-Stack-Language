package dssl.interpret.token;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import dssl.Helpers;
import dssl.node.*;

public class BlockToken extends Token {
	
	public final List<@NonNull Token> tokens;
	
	public BlockToken(List<@NonNull Token> tokens) {
		this.tokens = tokens;
		if (!tokens.isEmpty()) {
			@SuppressWarnings("null") Token first = tokens.get(0);
			setLine(first.getLine());
			setPos(first.getPos());
		}
	}
	
	@Override
	public void apply(Switch sw) {
		for (Token token : tokens) {
			token.apply(sw);
		}
	}
	
	@SuppressWarnings("null")
	@Override
	public Object clone() {
		List<@NonNull Token> tokensClone = new ArrayList<>();
		for (@NonNull Token token : tokens) {
			tokensClone.add((@NonNull Token) token.clone());
		}
		return new BlockToken(tokensClone);
	}
	
	@Override
	public @NonNull String toString() {
		return Helpers.tokenListToString(tokens);
	}
}
