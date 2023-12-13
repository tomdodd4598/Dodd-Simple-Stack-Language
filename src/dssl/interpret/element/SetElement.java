package dssl.interpret.element;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.*;

import org.eclipse.jdt.annotation.*;

import dssl.interpret.*;
import dssl.interpret.element.iter.IterElement;
import dssl.interpret.element.primitive.StringElement;

public class SetElement extends Element {
	
	public final Set<@NonNull ElementKey> value;
	
	public <T extends Element> SetElement(TokenExecutor exec, Consumer<Consumer<@NonNull T>> forEach) {
		super(exec.interpreter, exec.interpreter.builtIn.setClazz);
		value = new HashSet<>();
		forEach.accept(x -> value.add(x.toKey(exec)));
	}
	
	public <T extends Element> SetElement(TokenExecutor exec, Iterable<@NonNull T> elems) {
		this(exec, elems::forEach);
	}
	
	public <T extends Element> SetElement(TokenExecutor exec, Iterator<@NonNull T> elems) {
		this(exec, elems::forEachRemaining);
	}
	
	public <T extends Element> SetElement(TokenExecutor exec, Stream<@NonNull T> elems) {
		this(exec, elems::forEach);
	}
	
	public SetElement(Interpreter interpreter, Set<@NonNull ElementKey> set) {
		super(interpreter, interpreter.builtIn.setClazz);
		value = set;
	}
	
	public SetElement(Interpreter interpreter, Stream<@NonNull ElementKey> keys) {
		this(interpreter, keys.collect(Collectors.toSet()));
	}
	
	@Override
	public @NonNull StringElement stringCast(TokenExecutor exec) {
		return new StringElement(interpreter, toString(exec));
	}
	
	@Override
	public @NonNull ListElement listCast(TokenExecutor exec) {
		return new ListElement(interpreter, value.stream().map(x -> x.elem));
	}
	
	@Override
	public @NonNull SetElement setCast(TokenExecutor exec) {
		return this;
	}
	
	@Override
	public @NonNull IterElement iterator(TokenExecutor exec) {
		return new IterElement(interpreter) {
			
			final Iterator<@NonNull ElementKey> internal = value.iterator();
			
			@Override
			public boolean hasNext(TokenExecutor exec) {
				return internal.hasNext();
			}
			
			@SuppressWarnings("null")
			@Override
			public @NonNull Element next(TokenExecutor exec) {
				return internal.next().elem;
			}
		};
	}
	
	@Override
	public @NonNull Element iter(TokenExecutor exec) {
		return iterator(exec);
	}
	
	@Override
	public void unpack(TokenExecutor exec) {
		for (@NonNull ElementKey key : value) {
			exec.push(key.elem);
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
		return value.contains(elem.toKey(exec));
	}
	
	@Override
	public void add(TokenExecutor exec, @NonNull Element elem) {
		value.add(elem.toKey(exec));
	}
	
	@Override
	public void remove(TokenExecutor exec, @NonNull Element elem) {
		value.remove(elem.toKey(exec));
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
	public void addAll(TokenExecutor exec, @NonNull Element elem) {
		@Nullable Iterable<@NonNull Element> iterable = elem.internalIterable(exec);
		if (iterable == null) {
			throw new IllegalArgumentException(String.format("Built-in method \"addAll\" requires %s element as argument!", BuiltIn.ITERABLE));
		}
		for (@NonNull Element e : iterable) {
			add(exec, e);
		}
	}
	
	@Override
	public void removeAll(TokenExecutor exec, @NonNull Element elem) {
		@Nullable Iterable<@NonNull Element> iterable = elem.internalIterable(exec);
		if (iterable == null) {
			throw new IllegalArgumentException(String.format("Built-in method \"removeAll\" requires %s element as argument!", BuiltIn.ITERABLE));
		}
		for (@NonNull Element e : iterable) {
			remove(exec, e);
		}
	}
	
	@Override
	public void clear(TokenExecutor exec) {
		value.clear();
	}
	
	@Override
	public @NonNull String debug(TokenExecutor exec) {
		return "(|...|)";
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
		return new SetElement(interpreter, value.stream().map(ElementKey::clone));
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(BuiltIn.SET, value);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SetElement) {
			return value.equals(((SetElement) obj).value);
		}
		return false;
	}
	
	@SuppressWarnings("null")
	@Override
	public @NonNull String toString(TokenExecutor exec) {
		return value.stream().map(x -> x.elem.innerString(exec, this)).collect(Collectors.joining(", ", "(|", "|)"));
	}
}
