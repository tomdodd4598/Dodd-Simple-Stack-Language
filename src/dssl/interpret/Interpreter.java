package dssl.interpret;

import java.util.*;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.element.Element;
import dssl.interpret.element.primitive.StringElement;
import dssl.lexer.Lexer;

public class Interpreter {
	
	public final List<@NonNull StringElement> args;
	
	protected final TokenExecutor root;
	protected boolean halt = false;
	
	protected final Deque<@NonNull Element> stack = new ArrayDeque<>();
	protected final List<String> printList = new ArrayList<>();
	
	public final BlockIterator blockIteratorImpl;
	
	protected final IO ioImpl;
	protected final Module moduleImpl;
	protected final Native nativeImpl;
	protected final boolean debug;
	
	public Interpreter(List<@NonNull String> args, Lexer lexer, BlockIterator blockIteratorImpl, IO ioImpl, Module importImpl, Native nativeImpl, boolean debug) {
		this(args, new LexerIterator(lexer), blockIteratorImpl, ioImpl, importImpl, nativeImpl, debug);
	}
	
	public Interpreter(List<@NonNull String> args, TokenIterator iterator, BlockIterator blockIteratorImpl, IO ioImpl, Module moduleImpl, Native nativeImpl, boolean debug) {
		this.args = args.stream().map(StringElement::new).collect(Collectors.toList());
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
