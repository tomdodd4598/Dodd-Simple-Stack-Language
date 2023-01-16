package dssl.interpret;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.element.Element;
import dssl.lexer.Lexer;
import dssl.node.Token;

public class Interpreter {
	
	protected final TokenExecutor root;
	protected boolean halt = false;
	
	protected final Deque<@NonNull Element> elemStack = new ArrayDeque<>();
	protected final List<String> printList = new ArrayList<>();
	
	protected final IO io;
	protected final boolean debug;
	
	public Interpreter(Lexer lexer, IO io, boolean debug) {
		this(new LexerIterator(lexer), io, debug);
	}
	
	public Interpreter(Iterator<@NonNull Token> iterator, IO io, boolean debug) {
		root = new TokenExecutor(this, iterator);
		this.io = io;
		this.debug = debug;
	}
	
	public TokenResult run() {
		return root.iterate();
	}
}
