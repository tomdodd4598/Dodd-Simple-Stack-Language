package dssl.interpret.element.iter;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import dssl.interpret.*;
import dssl.interpret.element.*;
import dssl.interpret.element.primitive.*;

public abstract class IterElement extends Element {
	
	public IterElement(Interpreter interpreter) {
		super(interpreter, interpreter.builtIn.iterClazz);
	}
	
	public abstract boolean hasNext(TokenExecutor exec);
	
	public abstract @NonNull Element next(TokenExecutor exec);
	
	public Iterator<@NonNull Element> internalIterator(TokenExecutor exec) {
		return new Iterator<@NonNull Element>() {
			
			@Override
			public boolean hasNext() {
				return IterElement.this.hasNext(exec);
			}
			
			@Override
			public @NonNull Element next() {
				return IterElement.this.next(exec);
			}
		};
	}
	
	@SuppressWarnings("null")
	@Override
	public @NonNull Element collectString(TokenExecutor exec) {
		Iterator<@NonNull Element> iter = internalIterator(exec);
		StringBuilder sb = new StringBuilder();
		while (iter.hasNext()) {
			@NonNull Element elem = iter.next();
			if (!(elem instanceof CharElement)) {
				throw new IllegalArgumentException(String.format("Built-in method \"collectString\" requires \"() -> %s\" %s element as argument!", BuiltIn.CHAR, BuiltIn.ITER));
			}
			sb.append(((CharElement) elem).primitiveChar());
		}
		return new StringElement(interpreter, sb.toString());
	}
	
	@Override
	public @NonNull Element collectList(TokenExecutor exec) {
		return new ListElement(interpreter, internalIterator(exec));
	}
	
	@Override
	public @NonNull Element collectSet(TokenExecutor exec) {
		return new SetElement(interpreter, internalIterator(exec));
	}
	
	@Override
	public @NonNull Element collectDict(TokenExecutor exec) {
		Iterator<@NonNull Element> iter = internalIterator(exec);
		Map<@NonNull Element, @NonNull Element> map = new HashMap<>();
		while (iter.hasNext()) {
			@SuppressWarnings("null") @Nullable IterElement iterElem = iter.next().iterator(exec);
			if (iterElem == null) {
				throw new IllegalArgumentException(String.format("Built-in method \"collectDict\" requires \"() -> %s\" %s element as argument!", BuiltIn.ITERABLE, BuiltIn.ITER));
			}
			map.put(iterElem.next(exec), iterElem.next(exec));
		}
		
		return new DictElement(interpreter, map, false);
	}
	
	@Override
	public @NonNull Element stepBy(TokenExecutor exec, @NonNull Element elem) {
		return new StepByIterElement(interpreter, this, methodLongIndex(exec, elem, "stepBy"));
	}
	
	@Override
	public @NonNull Element chain(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof IterElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"chain\" requires %s element as argument!", BuiltIn.ITER));
		}
		return new ChainIterElement(interpreter, this, (IterElement) elem);
	}
	
	@Override
	public @NonNull Element zip(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof IterElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"zip\" requires %s element as argument!", BuiltIn.ITER));
		}
		return new ZipIterElement(interpreter, this, (IterElement) elem);
	}
	
	@Override
	public @NonNull Element map(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof BlockElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"map\" requires %s element as argument!", BuiltIn.BLOCK));
		}
		return new MapIterElement(interpreter, this, (BlockElement) elem);
	}
	
	@Override
	public @NonNull Element filter(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof BlockElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"filter\" requires %s element as argument!", BuiltIn.BLOCK));
		}
		return new FilterIterElement(interpreter, this, (BlockElement) elem);
	}
	
	@Override
	public @NonNull Element filterMap(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof BlockElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"filterMap\" requires %s element as argument!", BuiltIn.BLOCK));
		}
		return new FilterMapIterElement(interpreter, this, (BlockElement) elem);
	}
	
	@Override
	public @NonNull Element enumerate(TokenExecutor exec) {
		return new EnumerateIterElement(interpreter, this);
	}
	
	@Override
	public @NonNull Element takeWhile(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof BlockElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"takeWhile\" requires %s element as argument!", BuiltIn.BLOCK));
		}
		return new TakeWhileIterElement(interpreter, this, (BlockElement) elem);
	}
	
	@Override
	public @NonNull Element mapWhile(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof BlockElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"mapWhile\" requires %s element as argument!", BuiltIn.BLOCK));
		}
		return new MapWhileIterElement(interpreter, this, (BlockElement) elem);
	}
	
	@Override
	public @NonNull Element skip(TokenExecutor exec, @NonNull Element elem) {
		return new SkipIterElement(interpreter, this, methodLongIndex(exec, elem, "skip"));
	}
	
	@Override
	public @NonNull Element take(TokenExecutor exec, @NonNull Element elem) {
		return new TakeIterElement(interpreter, this, methodLongIndex(exec, elem, "take"));
	}
	
	@Override
	public @NonNull Element flatten(TokenExecutor exec) {
		return new FlattenIterElement(interpreter, this);
	}
	
	@Override
	public @NonNull Element flatMap(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof BlockElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"flatMap\" requires %s element as argument!", BuiltIn.BLOCK));
		}
		return new FlatMapIterElement(interpreter, this, (BlockElement) elem);
	}
	
	@Override
	public @NonNull Element chunks(TokenExecutor exec, @NonNull Element elem) {
		return new ChunksIterElement(interpreter, this, methodLongIndex(exec, elem, "chunks"));
	}
	
	@Override
	public int count(TokenExecutor exec) {
		Iterator<@NonNull Element> iter = internalIterator(exec);
		int count = 0;
		while (iter.hasNext()) {
			iter.next();
			++count;
		}
		return count;
	}
	
	@Override
	public void forEach(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof BlockElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"forEach\" requires %s element as argument!", BuiltIn.BLOCK));
		}
		
		BlockElement block = (BlockElement) elem;
		internalIterator(exec).forEachRemaining(x -> {
			exec.push(x);
			block.invoke(exec);
		});
	}
	
	@Override
	public boolean all(TokenExecutor exec) {
		Iterator<@NonNull Element> iter = internalIterator(exec);
		while (iter.hasNext()) {
			@SuppressWarnings("null") BoolElement elem = iter.next().asBool(exec);
			if (elem == null) {
				throw new IllegalArgumentException(String.format("Built-in method \"all\" requires \"() -> %s\" %s element as argument!", BuiltIn.BOOL, BuiltIn.ITER));
			}
			if (!elem.primitiveBool()) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean any(TokenExecutor exec) {
		Iterator<@NonNull Element> iter = internalIterator(exec);
		while (iter.hasNext()) {
			@SuppressWarnings("null") BoolElement elem = iter.next().asBool(exec);
			if (elem == null) {
				throw new IllegalArgumentException(String.format("Built-in method \"any\" requires \"() -> %s\" %s element as argument!", BuiltIn.BOOL, BuiltIn.ITER));
			}
			if (elem.primitiveBool()) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public @NonNull Element min(TokenExecutor exec) {
		@NonNull Element curr = interpreter.builtIn.nullElement;
		Iterator<@NonNull Element> iter = internalIterator(exec);
		while (iter.hasNext()) {
			@SuppressWarnings("null") @NonNull Element elem = iter.next();
			if (curr.equals(interpreter.builtIn.nullElement) || elem.compareTo(exec, curr) < 0) {
				curr = elem;
			}
		}
		return curr;
	}
	
	@Override
	public @NonNull Element max(TokenExecutor exec) {
		@NonNull Element curr = interpreter.builtIn.nullElement;
		Iterator<@NonNull Element> iter = internalIterator(exec);
		while (iter.hasNext()) {
			@SuppressWarnings("null") @NonNull Element elem = iter.next();
			if (curr.equals(interpreter.builtIn.nullElement) || elem.compareTo(exec, curr) > 0) {
				curr = elem;
			}
		}
		return curr;
	}
	
	@Override
	public @NonNull Element sum(TokenExecutor exec) {
		@NonNull Element curr = new IntElement(interpreter, 0);
		Iterator<@NonNull Element> iter = internalIterator(exec);
		while (iter.hasNext()) {
			curr.onPlus(exec, iter.next());
			curr = exec.pop();
		}
		return curr;
	}
	
	@Override
	public @NonNull Element product(TokenExecutor exec) {
		@NonNull Element curr = new IntElement(interpreter, 1);
		Iterator<@NonNull Element> iter = internalIterator(exec);
		while (iter.hasNext()) {
			curr.onMultiply(exec, iter.next());
			curr = exec.pop();
		}
		return curr;
	}
	
	@Override
	public @NonNull Element clone() {
		throw builtInMethodError("clone");
	}
	
	@Override
	public int hashCode() {
		return objectHashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return objectEquals(obj);
	}
}
