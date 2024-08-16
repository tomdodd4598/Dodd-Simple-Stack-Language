package dssl.interpret.element.primitive;

import java.util.*;
import java.util.stream.Stream;

import org.apache.commons.text.StringEscapeUtils;
import org.eclipse.jdt.annotation.*;

import dssl.Helpers;
import dssl.interpret.*;
import dssl.interpret.element.*;
import dssl.interpret.element.iter.IterElement;
import dssl.interpret.value.StringValue;

public class StringElement extends PrimitiveElement<@NonNull String, @NonNull StringValue> {
	
	public StringElement(Interpreter interpreter, @NonNull String rawValue) {
		super(interpreter, interpreter.builtIn.stringClazz, new StringValue(rawValue));
	}
	
	@Override
	public @NonNull StringElement stringCast(TokenExecutor exec) {
		return this;
	}
	
	@Override
	public @NonNull ListElement listCast(TokenExecutor exec) {
		return new ListElement(interpreter, internalIterable(exec));
	}
	
	@Override
	public @NonNull SetElement setCast(TokenExecutor exec) {
		return new SetElement(interpreter, internalStream(exec).map(x -> x.toKey(exec)));
	}
	
	@Override
	public @NonNull TokenResult onConcat(TokenExecutor exec, @NonNull Element other) {
		exec.push(new StringElement(interpreter, toString(exec) + other.stringCast(exec)));
		return TokenResult.PASS;
	}
	
	@Override
	public @NonNull TokenResult onNot(TokenExecutor exec) {
		throw unaryOpError("!");
	}
	
	@Override
	public @NonNull IterElement iterator(TokenExecutor exec) {
		return new IterElement(interpreter) {
			
			int index = 0;
			final int length = value.raw.length();
			
			@Override
			public boolean hasNext(TokenExecutor exec) {
				return index < length;
			}
			
			@Override
			public @NonNull Element next(TokenExecutor exec) {
				return new CharElement(interpreter, value.raw.charAt(index++));
			}
		};
	}
	
	@Override
	public @NonNull Element iter(TokenExecutor exec) {
		return iterator(exec);
	}
	
	@Override
	public void unpack(TokenExecutor exec) {
		int length = value.raw.length();
		for (int i = 0; i < length; ++i) {
			exec.push(new CharElement(interpreter, value.raw.charAt(i)));
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
	public boolean contains(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof CharElement) && !(elem instanceof StringElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"contains\" requires %s or %s element as argument!", BuiltIn.CHAR, BuiltIn.STRING));
		}
		return value.raw.contains(elem.toString(exec));
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
	public @NonNull Element get(TokenExecutor exec, @NonNull Element elem) {
		return new CharElement(interpreter, value.raw.charAt(methodIndex(exec, elem, "get")));
	}
	
	@SuppressWarnings("null")
	@Override
	public @NonNull Element slice(TokenExecutor exec, @NonNull Element elem0, @NonNull Element elem1) {
		return new StringElement(interpreter, value.raw.substring(methodIndex(exec, elem0, "slice", 1), methodIndex(exec, elem1, "slice", 2)));
	}
	
	@Override
	public @NonNull Element fst(TokenExecutor exec) {
		return new CharElement(interpreter, value.raw.charAt(0));
	}
	
	@Override
	public @NonNull Element snd(TokenExecutor exec) {
		return new CharElement(interpreter, value.raw.charAt(1));
	}
	
	@Override
	public @NonNull Element last(TokenExecutor exec) {
		return new CharElement(interpreter, value.raw.charAt(value.raw.length() - 1));
	}
	
	@Override
	public @NonNull Element indexOf(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof CharElement) && !(elem instanceof StringElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"indexOf\" requires %s or %s element as argument!", BuiltIn.CHAR, BuiltIn.STRING));
		}
		
		int index = value.raw.indexOf(elem.toString(exec));
		return index < 0 ? interpreter.builtIn.nullElement : new IntElement(interpreter, index);
	}
	
	@Override
	public @NonNull Element lastIndexOf(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof CharElement) && !(elem instanceof StringElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"lastIndexOf\" requires %s or %s element as argument!", BuiltIn.CHAR, BuiltIn.STRING));
		}
		
		int index = value.raw.lastIndexOf(elem.toString(exec));
		return index < 0 ? interpreter.builtIn.nullElement : new IntElement(interpreter, index);
	}
	
	@Override
	public @NonNull Element startsWith(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof CharElement) && !(elem instanceof StringElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"startsWith\" requires %s or %s element as argument!", BuiltIn.CHAR, BuiltIn.STRING));
		}
		return new BoolElement(interpreter, value.raw.startsWith(elem.toString(exec)));
	}
	
	@Override
	public @NonNull Element endsWith(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof CharElement) && !(elem instanceof StringElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"endsWith\" requires %s or %s element as argument!", BuiltIn.CHAR, BuiltIn.STRING));
		}
		return new BoolElement(interpreter, value.raw.endsWith(elem.toString(exec)));
	}
	
	@Override
	public @NonNull Element matches(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof StringElement stringElem)) {
			throw new IllegalArgumentException(String.format("Built-in method \"matches\" requires %s element as argument!", BuiltIn.STRING));
		}
		return new BoolElement(interpreter, value.raw.matches(stringElem.value.raw));
	}
	
	@SuppressWarnings("null")
	@Override
	public @NonNull Element replace(TokenExecutor exec, @NonNull Element elem0, @NonNull Element elem1) {
		if (!(elem0 instanceof StringElement stringElem0)) {
			throw new IllegalArgumentException(String.format("Built-in method \"replace\" requires %s element as first argument!", BuiltIn.STRING));
		}
		
		if (!(elem1 instanceof StringElement stringElem1)) {
			throw new IllegalArgumentException(String.format("Built-in method \"replace\" requires %s element as second argument!", BuiltIn.STRING));
		}
		
		return new StringElement(interpreter, value.raw.replaceAll(stringElem0.value.raw, stringElem1.value.raw));
	}
	
	@SuppressWarnings("null")
	@Override
	public @NonNull Element split(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof StringElement stringElem)) {
			throw new IllegalArgumentException(String.format("Built-in method \"split\" requires %s element as argument!", BuiltIn.STRING));
		}
		return new ListElement(interpreter, Arrays.stream(value.raw.split(stringElem.value.raw, -1)).map(x -> new StringElement(interpreter, x)));
	}
	
	@Override
	public @NonNull Element lower(TokenExecutor exec) {
		return new StringElement(interpreter, Helpers.lowerCase(value.raw));
	}
	
	@Override
	public @NonNull Element upper(TokenExecutor exec) {
		return new StringElement(interpreter, Helpers.upperCase(value.raw));
	}
	
	@SuppressWarnings("null")
	@Override
	public @NonNull Element trim(TokenExecutor exec) {
		return new StringElement(interpreter, value.raw.trim());
	}
	
	@SuppressWarnings("null")
	@Override
	public @NonNull Element format(TokenExecutor exec, @NonNull Element elem) {
		@Nullable Stream<@NonNull Element> stream = elem.internalStream(exec);
		if (stream == null) {
			throw new IllegalArgumentException(String.format("Built-in method \"format\" requires %s element as argument!", BuiltIn.ITERABLE));
		}
		return new StringElement(interpreter, String.format(value.raw, stream.map(x -> x.formatted(exec)).toArray()));
	}
	
	@Override
	public @NonNull String debug(TokenExecutor exec) {
		return "\"" + StringEscapeUtils.escapeJava(value.raw) + "\"";
	}
	
	@Override
	public @NonNull StringElement __str__(TokenExecutor exec) {
		return this;
	}
	
	@Override
	public @NonNull StringElement __debug__(TokenExecutor exec) {
		return new StringElement(interpreter, debug(exec));
	}
	
	@Override
	public @NonNull Element clone() {
		return new StringElement(interpreter, value.raw);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(BuiltIn.STRING, value);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StringElement other) {
			return value.equals(other.value);
		}
		return false;
	}
}
