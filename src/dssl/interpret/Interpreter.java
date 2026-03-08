package dssl.interpret;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.element.Element;
import dssl.lexer.Lexer;

public class Interpreter {
	
	public final List<@NonNull String> args;
	
	public final BuiltIn builtIn;
	public final TokenExecutor root;
	
	public final Hooks hooks;
	public final boolean debug;
	
	protected boolean halt = false;
	
	public final Deque<@NonNull Element> stack = new ArrayDeque<>();
	public final List<String> printList = new ArrayList<>();
	
	public Interpreter(List<@NonNull String> args, Lexer lexer, Hooks hooks, boolean debug) {
		this(args, new LexerIterator(lexer), hooks, debug);
	}
	
	public Interpreter(List<@NonNull String> args, TokenIterator iterator, Hooks hooks, boolean debug) {
		this.args = args;
		builtIn = new BuiltIn(this);
		root = newExecutor(iterator);
		this.hooks = hooks;
		this.debug = debug;
	}
	
	public TokenExecutor newExecutor(TokenIterator iterator) {
		return new TokenExecutor(this, iterator);
	}
	
	public TokenResult run() {
		return root.iterate();
	}
}
