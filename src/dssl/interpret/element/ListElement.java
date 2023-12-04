package dssl.interpret.element;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.*;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.iter.IterElement;
import dssl.interpret.element.primitive.*;

public class ListElement extends Element implements IterableElement {
	
	public final List<@NonNull Element> value;
	
	public <T extends Element> ListElement(Consumer<Consumer<@NonNull T>> forEach) {
		super(BuiltIn.LIST_CLAZZ);
		value = new ArrayList<>();
		forEach.accept(value::add);
	}
	
	public <T extends Element> ListElement(Iterable<@NonNull T> elems) {
		this(elems::forEach);
	}
	
	public <T extends Element> ListElement(Iterator<@NonNull T> elems) {
		this(elems::forEachRemaining);
	}
	
	public <T extends Element> ListElement(Stream<@NonNull T> elems) {
		this(elems::forEachOrdered);
	}
	
	public <T extends Element> ListElement(@NonNull T... elems) {
		super(BuiltIn.LIST_CLAZZ);
		value = new ArrayList<>();
		for (@NonNull Element elem : elems) {
			value.add(elem);
		}
	}
	
	@Override
	public @NonNull StringElement stringCast(TokenExecutor exec) {
		return new StringElement(toString(exec));
	}
	
	@Override
	public @NonNull ListElement listCast(TokenExecutor exec) {
		return this;
	}
	
	@Override
	public @NonNull SetElement setCast(TokenExecutor exec) {
		return new SetElement(value);
	}
	
	@Override
	public @NonNull IterElement iterator(TokenExecutor exec) {
		return new IterElement() {
			
			final Iterator<@NonNull Element> internal = value.iterator();
			
			@Override
			public boolean hasNext(TokenExecutor exec) {
				return internal.hasNext();
			}
			
			@SuppressWarnings("null")
			@Override
			public @NonNull Element next(TokenExecutor exec) {
				return internal.next();
			}
		};
	}
	
	@Override
	public void unpack(TokenExecutor exec) {
		for (@NonNull Element elem : value) {
			exec.push(elem);
		}
	}
	
	@Override
	public int size(TokenExecutor exec) {
		return value.size();
	}
	
	@Override
	public boolean isEmpty(TokenExecutor exec) {
		return value.isEmpty();
	}
	
	@Override
	public @NonNull Element iter(TokenExecutor exec) {
		return iterator(exec);
	}
	
	@Override
	public boolean contains(TokenExecutor exec, @NonNull Element elem) {
		return value.contains(elem);
	}
	
	@Override
	public void add(TokenExecutor exec, @NonNull Element elem) {
		value.add(elem);
	}
	
	@Override
	public void remove(TokenExecutor exec, @NonNull Element elem) {
		value.remove(elem);
	}
	
	@Override
	public boolean containsAll(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof IterableElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"containsAll\" requires %s element as argument!", BuiltIn.ITERABLE));
		}
		for (@NonNull Element e : ((IterableElement) elem).internalIterable(exec)) {
			if (!contains(exec, e)) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public void addAll(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof IterableElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"addAll\" requires %s element as argument!", BuiltIn.ITERABLE));
		}
		for (@NonNull Element e : ((IterableElement) elem).internalIterable(exec)) {
			value.add(e);
		}
	}
	
	@Override
	public void removeAll(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof IterableElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"removeAll\" requires %s element as argument!", BuiltIn.ITERABLE));
		}
		for (@NonNull Element e : ((IterableElement) elem).internalIterable(exec)) {
			value.remove(e);
		}
	}
	
	@Override
	public void clear(TokenExecutor exec) {
		value.clear();
	}
	
	@SuppressWarnings("null")
	@Override
	public @NonNull Element get(TokenExecutor exec, @NonNull Element elem) {
		return value.get(methodIndex(exec, elem, "get"));
	}
	
	@Override
	public void put(TokenExecutor exec, @NonNull Element elem0, @NonNull Element elem1) {
		value.set(methodIndex(exec, elem0, "put"), elem1);
	}
	
	@Override
	public @NonNull Element slice(TokenExecutor exec, @NonNull Element elem0, @NonNull Element elem1) {
		return new ListElement(value.subList(methodIndex(exec, elem0, "slice"), methodIndex(exec, elem1, "slice")));
	}
	
	@SuppressWarnings("null")
	@Override
	public @NonNull Element fst(TokenExecutor exec) {
		return value.get(0);
	}
	
	@SuppressWarnings("null")
	@Override
	public @NonNull Element snd(TokenExecutor exec) {
		return value.get(1);
	}
	
	@Override
	public void reverse(TokenExecutor exec) {
		Collections.reverse(value);
	}
	
	@Override
	public void sort(TokenExecutor exec) {
		Collections.sort(value, (x, y) -> x.compareTo(exec, y));
	}
	
	@Override
	public void sortBy(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof BlockElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"sortBy\" requires \"%s %s -> %s\" %s element as argument!", BuiltIn.OBJECT, BuiltIn.OBJECT, BuiltIn.INT, BuiltIn.BLOCK));
		}
		
		BlockElement block = (BlockElement) elem;
		Collections.sort(value, (x, y) -> {
			exec.push(x);
			exec.push(y);
			block.invoke(exec);
			
			IntElement result = exec.pop().asInt(exec);
			if (result == null) {
				throw new IllegalArgumentException(String.format("Built-in method \"sortBy\" requires \"%s %s -> %s\" %s element as argument!", BuiltIn.OBJECT, BuiltIn.OBJECT, BuiltIn.INT, BuiltIn.BLOCK));
			}
			
			return result.primitiveInt();
		});
	}
	
	@Override
	public void shuffle(TokenExecutor exec) {
		Collections.shuffle(value);
	}
	
	@Override
	public @NonNull String debug(TokenExecutor exec) {
		return "[...]";
	}
	
	@Override
	public @NonNull Element clone() {
		return new ListElement(value);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(BuiltIn.LIST, value);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ListElement) {
			return value.equals(((ListElement) obj).value);
		}
		return false;
	}
	
	@Override
	public @NonNull String toString() {
		return toString(null);
	}
	
	@SuppressWarnings("null")
	public @NonNull String toString(TokenExecutor exec) {
		return value.stream().map(x -> x.innerString(exec, this)).collect(Collectors.joining(", ", "[", "]"));
	}
}
