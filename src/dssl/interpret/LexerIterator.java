package dssl.interpret;

import java.util.Iterator;

import dssl.lexer.Lexer;
import dssl.node.*;

public class LexerIterator implements Iterator<Token> {
	
	protected final Lexer lexer;
	protected Token token;
	
	public LexerIterator(Lexer lexer) {
		this.lexer = lexer;
		getNext();
	}
	
	@Override
	public boolean hasNext() {
		return token != null && !(token instanceof EOF);
	}
	
	@Override
	public Token next() {
		Token next = token;
		getNext();
		return next;
	}
	
	protected void getNext() {
		try {
			token = lexer.next();
		}
		catch (Exception e) {
			token = new EOF();
			e.printStackTrace();
		}
	}
}
