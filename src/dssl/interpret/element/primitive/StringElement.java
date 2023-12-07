package dssl.interpret.element.primitive;

import java.util.*;

import org.apache.commons.text.StringEscapeUtils;
import org.eclipse.jdt.annotation.NonNull;

import dssl.Helpers;
import dssl.interpret.*;
import dssl.interpret.element.*;
import dssl.interpret.element.iter.IterElement;
import dssl.interpret.value.StringValue;

public class StringElement extends PrimitiveElement<@NonNull String, @NonNull StringValue> implements IterableElement {
	
	public StringElement(@NonNull String rawValue) {
		super(BuiltIn.STRING_CLAZZ, new StringValue(rawValue));
	}
	
	@Override
	public @NonNull StringElement stringCast(TokenExecutor exec) {
		return this;
	}
	
	@Override
	public @NonNull ListElement listCast(TokenExecutor exec) {
		return new ListElement(internalIterable(exec));
	}
	
	@Override
	public @NonNull SetElement setCast(TokenExecutor exec) {
		return new SetElement(internalIterable(exec));
	}
	
	@Override
	public @NonNull IterElement iterator(TokenExecutor exec) {
		return new IterElement() {
			
			int index = 0;
			final int length = value.raw.length();
			
			@Override
			public boolean hasNext(TokenExecutor exec) {
				return index < length;
			}
			
			@Override
			public @NonNull Element next(TokenExecutor exec) {
				return new CharElement(value.raw.charAt(index++));
			}
		};
	}
	
	@Override
	public @NonNull TokenResult onConcat(TokenExecutor exec, @NonNull Element other) {
		exec.push(new StringElement(toString() + other.stringCast(exec)));
		return TokenResult.PASS;
	}
	
	@Override
	public @NonNull TokenResult onNot(TokenExecutor exec) {
		throw unaryOpError("!");
	}
	
	@Override
	public void unpack(TokenExecutor exec) {
		int length = value.raw.length();
		for (int i = 0; i < length; ++i) {
			exec.push(new CharElement(value.raw.charAt(i)));
		}
	}
	
	@Override
	public int size(TokenExecutor exec) {
		return value.raw.length();
	}
	
	@Override
	public boolean isEmpty(TokenExecutor exec) {
		return value.raw.isEmpty();
	}
	
	@Override
	public @NonNull Element iter(TokenExecutor exec) {
		return iterator(exec);
	}
	
	@Override
	public boolean contains(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof CharElement) && !(elem instanceof StringElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"contains\" requires %s or %s element as argument!", BuiltIn.CHAR, BuiltIn.STRING));
		}
		return value.raw.contains(elem.toString());
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
	public @NonNull Element get(TokenExecutor exec, @NonNull Element elem) {
		return new CharElement(value.raw.charAt(methodIndex(exec, elem, "get")));
	}
	
	@SuppressWarnings("null")
	@Override
	public @NonNull Element slice(TokenExecutor exec, @NonNull Element elem0, @NonNull Element elem1) {
		return new StringElement(value.raw.substring(methodIndex(exec, elem0, "slice", 1), methodIndex(exec, elem1, "slice", 2)));
	}
	
	@Override
	public @NonNull Element fst(TokenExecutor exec) {
		return new CharElement(value.raw.charAt(0));
	}
	
	@Override
	public @NonNull Element snd(TokenExecutor exec) {
		return new CharElement(value.raw.charAt(1));
	}
	
	@Override
	public @NonNull Element last(TokenExecutor exec) {
		return new CharElement(value.raw.charAt(value.raw.length() - 1));
	}
	
	@Override
	public @NonNull Element startsWith(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof StringElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"startsWith\" requires %s element as argument!", BuiltIn.STRING));
		}
		return new BoolElement(value.raw.startsWith(((StringElement) elem).value.raw));
	}
	
	@Override
	public @NonNull Element endsWith(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof StringElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"endsWith\" requires %s element as argument!", BuiltIn.STRING));
		}
		return new BoolElement(value.raw.endsWith(((StringElement) elem).value.raw));
	}
	
	@Override
	public @NonNull Element matches(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof StringElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"matches\" requires %s element as argument!", BuiltIn.STRING));
		}
		return new BoolElement(value.raw.matches(((StringElement) elem).value.raw));
	}
	
	@SuppressWarnings("null")
	@Override
	public @NonNull Element replace(TokenExecutor exec, @NonNull Element elem0, @NonNull Element elem1) {
		if (!(elem0 instanceof StringElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"replace\" requires %s element as first argument!", BuiltIn.STRING));
		}
		
		if (!(elem1 instanceof StringElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"replace\" requires %s element as second argument!", BuiltIn.STRING));
		}
		
		return new StringElement(value.raw.replaceAll(((StringElement) elem0).value.raw, ((StringElement) elem1).value.raw));
	}
	
	@SuppressWarnings("null")
	@Override
	public @NonNull Element split(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof StringElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"split\" requires %s element as argument!", BuiltIn.STRING));
		}
		return new ListElement(Arrays.stream(value.raw.split(((StringElement) elem).value.raw, -1)).map(StringElement::new));
	}
	
	@Override
	public @NonNull Element lower(TokenExecutor exec) {
		return new StringElement(Helpers.lowerCase(value.raw));
	}
	
	@Override
	public @NonNull Element upper(TokenExecutor exec) {
		return new StringElement(Helpers.upperCase(value.raw));
	}
	
	@SuppressWarnings("null")
	@Override
	public @NonNull Element trim(TokenExecutor exec) {
		return new StringElement(value.raw.trim());
	}
	
	@SuppressWarnings("null")
	@Override
	public @NonNull Element format(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof IterableElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"format\" requires %s element as argument!", BuiltIn.ITERABLE));
		}
		return new StringElement(String.format(value.raw, ((IterableElement) elem).stream(exec).map(x -> x.formatted(exec)).toArray()));
	}
	
	@Override
	public @NonNull String debug(TokenExecutor exec) {
		return "\"" + StringEscapeUtils.escapeJava(value.raw) + "\"";
	}
	
	@Override
	public @NonNull Element clone() {
		return new StringElement(value.raw);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(BuiltIn.STRING, value);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StringElement) {
			StringElement other = (StringElement) obj;
			return value.equals(other.value);
		}
		return false;
	}
}
