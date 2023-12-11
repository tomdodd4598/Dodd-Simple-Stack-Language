package dssl.interpret;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.element.Element;
import dssl.lexer.Lexer;

public class Interpreter {
	
	public final List<@NonNull String> args;
	
	public final BuiltIn builtIn;
	
	protected final TokenExecutor root;
	protected boolean halt = false;
	
	protected final Deque<@NonNull Element> stack = new ArrayDeque<>();
	protected final List<String> printList = new ArrayList<>();
	
	public final Hooks hooks;
	public final boolean debug;
	
	public Interpreter(List<@NonNull String> args, Hooks hooks, Lexer lexer, boolean debug) {
		this(args, hooks, new LexerIterator(lexer), debug);
	}
	
	public Interpreter(List<@NonNull String> args, Hooks hooks, TokenIterator iterator, boolean debug) {
		this.args = args;
		builtIn = new BuiltIn(this);
		this.hooks = hooks;
		root = newExecutor(iterator);
		this.debug = debug;
	}
	
	public TokenExecutor newExecutor(TokenIterator iterator) {
		return new TokenExecutor(this, iterator);
	}
	
	public TokenResult run() {
		return root.iterate();
	}
}
