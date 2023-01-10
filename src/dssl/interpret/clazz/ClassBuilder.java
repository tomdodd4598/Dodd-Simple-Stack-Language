package dssl.interpret.clazz;

import java.util.Iterator;

import dssl.interpret.Executor;
import dssl.node.Token;

public class ClassBuilder extends Executor {
	
	public ClassBuilder(Iterator<Token> iterator, Executor previous) {
		super(iterator, previous);
	}
}
