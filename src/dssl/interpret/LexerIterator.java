package dssl.interpret;

import java.io.StringReader;
import java.util.Iterator;

import org.eclipse.jdt.annotation.NonNull;

import dssl.Helpers;
import dssl.lexer.Lexer;
import dssl.node.*;

public class LexerIterator implements Iterator<@NonNull Token> {
	
	protected final Lexer lexer;
	protected Token token;
	
	public LexerIterator(String str) {
		this(new Lexer(Helpers.getPushbackReader(new StringReader(str))));
	}
	
	public LexerIterator(Lexer lexer) {
		this.lexer = lexer;
		getNext();
	}
	
	@Override
	public boolean hasNext() {
		return token != null && !(token instanceof EOF);
	}
	
	@SuppressWarnings("null")
	@Override
	public @NonNull Token next() {
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
