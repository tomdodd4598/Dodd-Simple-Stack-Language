package dssl.interpret.element;

import java.util.*;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.iter.IterElement;
import dssl.interpret.element.primitive.StringElement;

public class SetElement extends Element implements IterableElement {
	
	public final Set<@NonNull Element> value;
	
	public SetElement(Iterable<@NonNull Element> elems) {
		super(BuiltIn.SET_CLAZZ);
		value = new HashSet<>();
		elems.forEach(value::add);
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
	
	@Override
	public @NonNull String debug(TokenExecutor exec) {
		return "(|...|)";
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
	
	@Override
	public @NonNull String toString() {
		return toString(null);
	}
	
	@SuppressWarnings("null")
	public @NonNull String toString(TokenExecutor exec) {
		return value.stream().map(x -> x.innerString(exec, this)).collect(Collectors.joining(", ", "(|", "|)"));
	}
}
