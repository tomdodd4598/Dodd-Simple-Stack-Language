package dssl.interpret;

import java.util.Iterator;

import org.eclipse.jdt.annotation.NonNull;

import dssl.node.Token;

public abstract class TokenReader {
	
	protected final Interpreter interpreter;
	protected final Iterator<@NonNull Token> iterator;
	protected final TokenReader prev;
	
	protected TokenReader(Interpreter interpreter, Iterator<@NonNull Token> iterator) {
		this.interpreter = interpreter;
		this.iterator = iterator;
		this.prev = null;
	}
	
	protected TokenReader(Iterator<@NonNull Token> iterator, TokenReader prev) {
		interpreter = prev.interpreter;
		this.iterator = iterator;
		this.prev = prev;
	}
	
	public TokenResult iterate() {
		loop: while (iterator.hasNext()) {
			TokenResult readResult = read(iterator.next());
			switch (readResult) {
				case PASS:
					continue loop;
				default:
					return readResult;
			}
		}
		return TokenResult.PASS;
	}
	
	protected abstract TokenResult read(@NonNull Token token);
}
