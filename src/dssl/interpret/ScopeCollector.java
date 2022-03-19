package dssl.interpret;

import java.util.*;

import dssl.node.*;

public class ScopeCollector extends Interpreter {
	
	public final List<Token> tokens = new ArrayList<>();
	
	protected int depth = 1;
	
	public ScopeCollector(Iterator<Token> iterator) {
		super(iterator, null);
	}
	
	@Override
	protected InterpretResult read(Token token) {
		if (token instanceof TLBrace) {
			++depth;
		}
		else if (token instanceof TRBrace) {
			if (--depth == 0) {
				return InterpretResult.BREAK;
			}
		}
		tokens.add(token);
		return InterpretResult.PASS;
	}
}
