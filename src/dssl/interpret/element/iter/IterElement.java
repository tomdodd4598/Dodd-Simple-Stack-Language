package dssl.interpret.element.iter;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.*;
import dssl.interpret.element.primitive.*;

public abstract class IterElement extends Element {
	
	public IterElement() {
		super(BuiltIn.ITER_CLAZZ);
	}
	
	public abstract boolean hasNext(TokenExecutor exec);
	
	public abstract @NonNull Element next(TokenExecutor exec);
	
	public Iterator<@NonNull Element> internal(TokenExecutor exec) {
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
		Iterator<@NonNull Element> iter = internal(exec);
		StringBuilder sb = new StringBuilder();
		while (iter.hasNext()) {
			@NonNull Element elem = iter.next();
			if (!(elem instanceof CharElement)) {
				throw new IllegalArgumentException(String.format("Built-in method \"collectString\" requires \"() -> %s\" %s element as argument!", BuiltIn.CHAR, BuiltIn.ITER));
			}
			sb.append(((CharElement) elem).primitiveChar());
		}
		return new StringElement(sb.toString());
	}
	
	@Override
	public @NonNull Element collectList(TokenExecutor exec) {
		return new ListElement(() -> internal(exec));
	}
	
	@Override
	public @NonNull Element collectSet(TokenExecutor exec) {
		return new SetElement(() -> internal(exec));
	}
	
	@Override
	public @NonNull Element collectDict(TokenExecutor exec) {
		Iterator<@NonNull Element> iter = internal(exec);
		Map<@NonNull Element, @NonNull Element> map = new HashMap<>();
		while (iter.hasNext()) {
			@SuppressWarnings("null") @NonNull Element elem = iter.next();
			if (!(elem instanceof IterableElement)) {
				throw new IllegalArgumentException(String.format("Built-in method \"collectDict\" requires \"() -> %s\" %s element as argument!", BuiltIn.ITERABLE, BuiltIn.ITER));
			}
			
			IterElement iterElem = ((IterableElement) elem).iterator(exec);
			map.put(iterElem.next(exec), iterElem.next(exec));
		}
		
		return new DictElement(map, false);
	}
	
	@Override
	public @NonNull Element stepBy(TokenExecutor exec, @NonNull Element elem) {
		return new StepByIterElement(this, methodIndex(exec, elem, "stepBy"));
	}
	
	@Override
	public @NonNull Element chain(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof IterElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"chain\" requires %s element as argument!", BuiltIn.ITER));
		}
		return new ChainIterElement(this, (IterElement) elem);
	}
	
	@Override
	public @NonNull Element zip(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof IterElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"zip\" requires %s element as argument!", BuiltIn.ITER));
		}
		return new ZipIterElement(this, (IterElement) elem);
	}
	
	@Override
	public @NonNull Element map(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof BlockElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"map\" requires %s element as argument!", BuiltIn.BLOCK));
		}
		return new MapIterElement(this, (BlockElement) elem);
	}
	
	@Override
	public @NonNull Element filter(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof BlockElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"filter\" requires %s element as argument!", BuiltIn.BLOCK));
		}
		return new FilterIterElement(this, (BlockElement) elem);
	}
	
	@Override
	public @NonNull Element filterMap(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof BlockElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"filterMap\" requires %s element as argument!", BuiltIn.BLOCK));
		}
		return new FilterMapIterElement(this, (BlockElement) elem);
	}
	
	@Override
	public @NonNull Element enumerate(TokenExecutor exec) {
		return new EnumerateIterElement(this);
	}
	
	@Override
	public @NonNull Element takeWhile(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof BlockElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"takeWhile\" requires %s element as argument!", BuiltIn.BLOCK));
		}
		return new TakeWhileIterElement(this, (BlockElement) elem);
	}
	
	@Override
	public @NonNull Element mapWhile(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof BlockElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"mapWhile\" requires %s element as argument!", BuiltIn.BLOCK));
		}
		return new MapWhileIterElement(this, (BlockElement) elem);
	}
	
	@Override
	public @NonNull Element skip(TokenExecutor exec, @NonNull Element elem) {
		return new SkipIterElement(this, methodIndex(exec, elem, "skip"));
	}
	
	@Override
	public @NonNull Element take(TokenExecutor exec, @NonNull Element elem) {
		return new TakeIterElement(this, methodIndex(exec, elem, "take"));
	}
	
	@Override
	public @NonNull Element flatMap(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof BlockElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"flatMap\" requires %s element as argument!", BuiltIn.BLOCK));
		}
		return new FlatMapIterElement(this, (BlockElement) elem);
	}
	
	@Override
	public @NonNull Element flatten(TokenExecutor exec) {
		return new FlattenIterElement(this);
	}
	
	@Override
	public @NonNull Element chunks(TokenExecutor exec, @NonNull Element elem) {
		return new ChunksIterElement(this, methodIndex(exec, elem, "chunks"));
	}
	
	@Override
	public int count(TokenExecutor exec) {
		Iterator<@NonNull Element> iter = internal(exec);
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
		internal(exec).forEachRemaining(x -> {
			exec.push(x);
			block.invoke(exec);
		});
	}
	
	@Override
	public void into(TokenExecutor exec, @NonNull Element elem) {
		internal(exec).forEachRemaining(x -> elem.add(exec, x));
	}
	
	@Override
	public @NonNull Element fold(TokenExecutor exec, @NonNull Element elem0, @NonNull Element elem1) {
		if (!(elem1 instanceof BlockElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"fold\" requires %s element as second argument!", BuiltIn.BLOCK));
		}
		
		Iterator<@NonNull Element> iter = internal(exec);
		BlockElement block = (BlockElement) elem1;
		while (iter.hasNext()) {
			exec.push(elem0);
			exec.push(iter.next());
			block.invoke(exec);
			elem0 = exec.pop();
		}
		return elem0;
	}
	
	@Override
	public boolean all(TokenExecutor exec) {
		Iterator<@NonNull Element> iter = internal(exec);
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
		Iterator<@NonNull Element> iter = internal(exec);
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
		@NonNull Element curr = NullElement.INSTANCE;
		Iterator<@NonNull Element> iter = internal(exec);
		while (iter.hasNext()) {
			@SuppressWarnings("null") @NonNull Element elem = iter.next();
			if (curr.equals(NullElement.INSTANCE) || elem.compareTo(exec, curr) < 0) {
				curr = elem;
			}
		}
		return curr;
	}
	
	@Override
	public @NonNull Element max(TokenExecutor exec) {
		@NonNull Element curr = NullElement.INSTANCE;
		Iterator<@NonNull Element> iter = internal(exec);
		while (iter.hasNext()) {
			@SuppressWarnings("null") @NonNull Element elem = iter.next();
			if (curr.equals(NullElement.INSTANCE) || elem.compareTo(exec, curr) > 0) {
				curr = elem;
			}
		}
		return curr;
	}
	
	@Override
	public @NonNull Element sum(TokenExecutor exec) {
		@NonNull Element curr = new IntElement(0);
		Iterator<@NonNull Element> iter = internal(exec);
		while (iter.hasNext()) {
			curr.onPlus(exec, iter.next());
			curr = exec.pop();
		}
		return curr;
	}
	
	@Override
	public @NonNull Element product(TokenExecutor exec) {
		@NonNull Element curr = new IntElement(1);
		Iterator<@NonNull Element> iter = internal(exec);
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
