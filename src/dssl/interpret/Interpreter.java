package dssl.interpret;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.element.Element;
import dssl.lexer.Lexer;

public class Interpreter {
	
	protected final TokenExecutor root;
	protected boolean halt = false;
	
	protected final Deque<@NonNull Element> stack = new ArrayDeque<>();
	protected final List<String> printList = new ArrayList<>();
	
	protected final IO io;
	protected final Module moduleImpl;
	protected final Native nativeImpl;
	protected final boolean debug;
	
	public Interpreter(Lexer lexer, IO io, Module importImpl, Native nativeImpl, boolean debug) {
		this(new LexerIterator(lexer), io, importImpl, nativeImpl, debug);
	}
	
	public Interpreter(TokenIterator iterator, IO io, Module moduleImpl, Native nativeImpl, boolean debug) {
		root = newExecutor(iterator);
		this.io = io;
		this.moduleImpl = moduleImpl;
		this.nativeImpl = nativeImpl;
		this.debug = debug;
	}
	
	public TokenExecutor newExecutor(TokenIterator iterator) {
		return new TokenExecutor(this, iterator);
	}
	
	public TokenResult run() {
		return root.iterate();
	}
}
