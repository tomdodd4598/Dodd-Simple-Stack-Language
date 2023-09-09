package dssl.interpret;

import java.io.PushbackReader;

import org.eclipse.jdt.annotation.NonNull;

import dssl.Helpers;
import dssl.lexer.Lexer;
import dssl.node.*;

public class LexerIterator extends TokenIterator {
	
	protected final Lexer lexer;
	
	public LexerIterator(@NonNull String str) {
		this(Helpers.stringLexer(str));
	}
	
	public LexerIterator(PushbackReader reader) {
		this(new Lexer(reader));
	}
	
	public LexerIterator(Lexer lexer) {
		super();
		this.lexer = lexer;
	}
	
	@Override
	public void onStart() {
		curr = getNextChecked();
	}
	
	@Override
	public boolean validNext() {
		return !(curr instanceof EOF);
	}
	
	@Override
	protected Token getNext() {
		return Helpers.getLexerNext(lexer);
	}
}
