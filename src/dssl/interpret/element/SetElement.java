package dssl.interpret.element;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.*;

import org.eclipse.jdt.annotation.*;

import dssl.interpret.*;
import dssl.interpret.element.iter.IterElement;
import dssl.interpret.element.primitive.StringElement;

public class SetElement extends Element {
	
	public final Set<@NonNull Element> value;
	
	public <T extends Element> SetElement(Consumer<Consumer<@NonNull T>> forEach) {
		super(BuiltIn.SET_CLAZZ);
		value = new HashSet<>();
		forEach.accept(value::add);
	}
	
	public <T extends Element> SetElement(Iterable<@NonNull T> elems) {
		this(elems::forEach);
	}
	
	public <T extends Element> SetElement(Iterator<@NonNull T> elems) {
		this(elems::forEachRemaining);
	}
	
	public <T extends Element> SetElement(Stream<@NonNull T> elems) {
		this(elems::forEach);
	}
	
	@Override
	public @NonNull StringElement stringCast(TokenExecutor exec) {
		return new StringElement(toString(exec));
	}
	
	@Override
	public @NonNull ListElement listCast(TokenExecutor exec) {
		return new ListElement(value);
	}
	
	@Override
	public @NonNull SetElement setCast(TokenExecutor exec) {
		return this;
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
	public @NonNull Element __str__(TokenExecutor exec) {
		return stringCast(exec);
	}
	
	@Override
	public @NonNull Element __debug__(TokenExecutor exec) {
		return new StringElement(debug(exec));
	}
	
	@Override
	public @NonNull Element __iter__(TokenExecutor exec) {
		return iterator(exec);
	}
	
	@Override
	public @NonNull Element clone() {
		return new SetElement(value);
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
		return value.stream().map(x -> x.innerString(exec, this)).collect(Collectors.joining(", ", "(|", "|)"));
	}
}
