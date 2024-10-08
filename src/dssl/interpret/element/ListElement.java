package dssl.interpret.element;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.*;

import org.eclipse.jdt.annotation.*;

import dssl.interpret.*;
import dssl.interpret.element.iter.IterElement;
import dssl.interpret.element.primitive.*;

public class ListElement extends Element {
	
	public final List<@NonNull Element> value;
	
	public <T extends Element> ListElement(Interpreter interpreter, Consumer<Consumer<@NonNull T>> forEach) {
		super(interpreter, interpreter.builtIn.listClazz);
		value = new ArrayList<>();
		forEach.accept(value::add);
	}
	
	public <T extends Element> ListElement(Interpreter interpreter, Iterable<@NonNull T> elems) {
		this(interpreter, elems::forEach);
	}
	
	public <T extends Element> ListElement(Interpreter interpreter, Iterator<@NonNull T> elems) {
		this(interpreter, elems::forEachRemaining);
	}
	
	public <T extends Element> ListElement(Interpreter interpreter, Stream<@NonNull T> elems) {
		this(interpreter, elems::forEachOrdered);
	}
	
	public <T extends Element> ListElement(Interpreter interpreter, @NonNull T... elems) {
		super(interpreter, interpreter.builtIn.listClazz);
		value = new ArrayList<>();
		for (@NonNull Element elem : elems) {
			value.add(elem);
		}
	}
	
	@Override
	public @NonNull StringElement stringCast(TokenExecutor exec) {
		return new StringElement(interpreter, toString(exec));
	}
	
	@Override
	public @NonNull ListElement listCast(TokenExecutor exec) {
		return this;
	}
	
	@Override
	public @NonNull SetElement setCast(TokenExecutor exec) {
		return new SetElement(interpreter, value.stream().map(x -> x.toKey(exec)));
	}
	
	@Override
	public @NonNull IterElement iterator(TokenExecutor exec) {
		return new IterElement(interpreter) {
			
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
	public @NonNull Element iter(TokenExecutor exec) {
		return iterator(exec);
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
	public boolean contains(TokenExecutor exec, @NonNull Element elem) {
		for (@NonNull Element e : value) {
			if (elem.dynEqualTo(exec, e)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void push(TokenExecutor exec, @NonNull Element elem) {
		value.add(elem);
	}
	
	@Override
	public void insert(TokenExecutor exec, @NonNull Element elem0, @NonNull Element elem1) {
		value.add(methodIndex(exec, elem0, "insert", 1), elem1);
	}
	
	@Override
	public void remove(TokenExecutor exec, @NonNull Element elem) {
		value.remove(methodIndex(exec, elem, "remove"));
	}
	
	@Override
	public boolean containsAll(TokenExecutor exec, @NonNull Element elem) {
		@Nullable Iterable<@NonNull Element> iterable = elem.internalIterable(exec);
		if (iterable == null) {
			throw new IllegalArgumentException(String.format("Built-in method \"containsAll\" requires %s element as argument!", BuiltIn.ITERABLE));
		}
		for (@NonNull Element e : iterable) {
			if (!contains(exec, e)) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public void pushAll(TokenExecutor exec, @NonNull Element elem) {
		@Nullable Iterable<@NonNull Element> iterable = elem.internalIterable(exec);
		if (iterable == null) {
			throw new IllegalArgumentException(String.format("Built-in method \"pushAll\" requires %s element as argument!", BuiltIn.ITERABLE));
		}
		for (@NonNull Element e : iterable) {
			value.add(e);
		}
	}
	
	@Override
	public void insertAll(TokenExecutor exec, @NonNull Element elem0, @NonNull Element elem1) {
		@Nullable Iterable<@NonNull Element> iterable = elem1.internalIterable(exec);
		if (iterable == null) {
			throw new IllegalArgumentException(String.format("Built-in method \"insertAll\" requires %s element as second argument!", BuiltIn.ITERABLE));
		}
		
		int index = methodIndex(exec, elem0, "insertAll", 1);
		for (@NonNull Element e : iterable) {
			value.add(index++, e);
		}
	}
	
	@SuppressWarnings("null")
	@Override
	public void removeAll(TokenExecutor exec, @NonNull Element elem) {
		@Nullable Iterable<@NonNull Element> iterable = elem.internalIterable(exec);
		if (iterable == null) {
			throw new IllegalArgumentException(String.format("Built-in method \"removeAll\" requires %s element of non-negative integers as argument!", BuiltIn.ITERABLE));
		}
		for (@NonNull Element e : iterable) {
			IntElement intElem = e.asInt(exec);
			if (intElem == null) {
				throw new IllegalArgumentException(String.format("Built-in method \"removeAll\" requires %s element of non-negative integers as argument!", BuiltIn.ITERABLE));
			}
			value.set(intElem.primitiveInt(), null);
		}
		value.removeIf(x -> x == null);
	}
	
	@SuppressWarnings("null")
	@Override
	public @NonNull Element pop(TokenExecutor exec) {
		return value.remove(value.size() - 1);
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
	public void set(TokenExecutor exec, @NonNull Element elem0, @NonNull Element elem1) {
		value.set(methodIndex(exec, elem0, "set", 1), elem1);
	}
	
	@Override
	public void removeValue(TokenExecutor exec, @NonNull Element elem) {
		int size = value.size();
		for (int i = 0; i < size; ++i) {
			if (elem.dynEqualTo(exec, value.get(i))) {
				value.remove(i);
				return;
			}
		}
	}
	
	@Override
	public @NonNull Element slice(TokenExecutor exec, @NonNull Element elem0, @NonNull Element elem1) {
		return new ListElement(interpreter, value.subList(methodIndex(exec, elem0, "slice", 1), methodIndex(exec, elem1, "slice", 2)));
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
	
	@SuppressWarnings("null")
	@Override
	public @NonNull Element last(TokenExecutor exec) {
		return value.get(value.size() - 1);
	}
	
	@Override
	public @NonNull Element indexOf(TokenExecutor exec, @NonNull Element elem) {
		int size = value.size();
		for (int i = 0; i < size; ++i) {
			if (elem.dynEqualTo(exec, value.get(i))) {
				return new IntElement(interpreter, i);
			}
		}
		return interpreter.builtIn.nullElement;
	}
	
	@Override
	public @NonNull Element lastIndexOf(TokenExecutor exec, @NonNull Element elem) {
		for (int i = value.size() - 1; i >= 0; --i) {
			if (elem.dynEqualTo(exec, value.get(i))) {
				return new IntElement(interpreter, i);
			}
		}
		return interpreter.builtIn.nullElement;
	}
	
	@Override
	public void reverse(TokenExecutor exec) {
		Collections.reverse(value);
	}
	
	@Override
	public void sort(TokenExecutor exec) {
		Collections.sort(value, (x, y) -> x.dynCompareTo(exec, y));
	}
	
	@Override
	public void sortBy(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof BlockElement block)) {
			throw new IllegalArgumentException(String.format("Built-in method \"sortBy\" requires \"%s %s -> %s\" %s element as argument!", BuiltIn.OBJECT, BuiltIn.OBJECT, BuiltIn.INT, BuiltIn.BLOCK));
		}
		
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
	public @NonNull Element clone(TokenExecutor exec) {
		return new ListElement(interpreter, value.stream().map(x -> x.dynClone(exec)));
	}
	
	@Override
	public int hash(TokenExecutor exec) {
		int hash = BuiltIn.LIST.hashCode();
		for (@NonNull Element elem : value) {
			hash = 31 * hash + elem.dynHash(exec);
		}
		return hash;
	}
	
	@Override
	public @NonNull String debug(TokenExecutor exec) {
		return "[...]";
	}
	
	@Override
	public @NonNull StringElement __str__(TokenExecutor exec) {
		return stringCast(exec);
	}
	
	@Override
	public @NonNull StringElement __debug__(TokenExecutor exec) {
		return new StringElement(interpreter, debug(exec));
	}
	
	@Override
	public @NonNull Element clone() {
		return new ListElement(interpreter, value.stream().map(Element::clone));
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(BuiltIn.LIST, value);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ListElement other) {
			return value.equals(other.value);
		}
		return false;
	}
	
	@SuppressWarnings("null")
	@Override
	public @NonNull String toString(TokenExecutor exec) {
		return value.stream().map(x -> x.innerString(exec, this)).collect(Collectors.joining(", ", "[", "]"));
	}
}
