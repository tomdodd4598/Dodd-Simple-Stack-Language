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
	
	public final BlockIterator blockIteratorImpl;
	
	protected final IO ioImpl;
	protected final Module moduleImpl;
	protected final Native nativeImpl;
	protected final boolean debug;
	
	public Interpreter(Lexer lexer, BlockIterator blockIteratorImpl, IO ioImpl, Module importImpl, Native nativeImpl, boolean debug) {
		this(new LexerIterator(lexer), blockIteratorImpl, ioImpl, importImpl, nativeImpl, debug);
	}
	
	public Interpreter(TokenIterator iterator, BlockIterator blockIteratorImpl, IO ioImpl, Module moduleImpl, Native nativeImpl, boolean debug) {
		root = newExecutor(iterator);
		this.blockIteratorImpl = blockIteratorImpl;
		this.ioImpl = ioImpl;
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
