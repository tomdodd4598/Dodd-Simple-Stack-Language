package dssl.interpret;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.token.BlockToken;
import dssl.node.*;

public class TokenCollector extends TokenReader {
	
	protected final Deque<List<@NonNull Token>> listStack = new ArrayDeque<>();
	
	protected TokenCollector(Interpreter interpreter, TokenIterator iterator) {
		super(interpreter, iterator);
		listStack.push(new ArrayList<>());
	}
	
	@Override
	protected TokenResult read(@NonNull Token token) {
		if (token instanceof TLBrace) {
			listStack.push(new ArrayList<>());
		}
		else if (token instanceof TRBrace) {
			int stackSize = listStack.size();
			if (stackSize < 1) {
				throw new IllegalArgumentException(String.format("Encountered unexpected \"}\" token!"));
			}
			else if (stackSize == 1) {
				return TokenResult.BREAK;
			}
			else {
				List<@NonNull Token> list = listStack.pop();
				listStack.peek().add(new BlockToken(list));
			}
		}
		else {
			listStack.peek().add(token);
		}
		return TokenResult.PASS;
	}
}
