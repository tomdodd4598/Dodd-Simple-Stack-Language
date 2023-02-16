package dssl.interpret.element.primitive;

import java.util.*;

import org.apache.commons.text.StringEscapeUtils;
import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.*;
import dssl.interpret.element.container.*;
import dssl.interpret.value.StringValue;

public class StringElement extends PrimitiveElement<@NonNull String, @NonNull StringValue> implements IterableElement<@NonNull Element> {
	
	public StringElement(@NonNull String rawValue) {
		super(BuiltIn.STRING_CLAZZ, new StringValue(rawValue));
	}
	
	@Override
	public StringElement stringCast(boolean explicit) {
		return this;
	}
	
	@Override
	public ListElement listCast() {
		return new ListElement(this);
	}
	
	@Override
	public TupleElement tupleCast() {
		return new TupleElement(this);
	}
	
	@Override
	public SetElement setCast() {
		return new SetElement(this);
	}
	
	@Override
	public Iterator<@NonNull Element> iterator() {
		return new Iterator<@NonNull Element>() {
			
			int index = 0;
			
			@Override
			public boolean hasNext() {
				return index < value.raw.length();
			}
			
			@Override
			public @NonNull Element next() {
				return new CharElement(value.raw.charAt(index++));
			}
		};
	}
	
	@Override
	public void onEach(TokenExecutor exec, @NonNull Element item) {
		exec.push(item);
	}
	
	@Override
	public TokenResult onNot(TokenExecutor exec) {
		throw unaryOpError("!");
	}
	
	@Override
	public void unpack(TokenExecutor exec) {
		for (@NonNull Element elem : this) {
			exec.push(elem);
		}
	}
	
	@Override
	public int size() {
		return value.raw.length();
	}
	
	@Override
	public boolean isEmpty() {
		return value.raw.isEmpty();
	}
	
	@Override
	public boolean contains(@NonNull Element elem) {
		if (elem instanceof CharElement || elem instanceof StringElement) {
			return value.raw.contains(elem.toString());
		}
		else {
			return false;
		}
	}
	
	@Override
	public boolean containsAll(@NonNull Element elem) {
		if (!(elem instanceof CollectionElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"containsAll\" requires collection element as argument!"));
		}
		return ((CollectionElement) elem).collection().stream().allMatch(this::contains);
	}
	
	@Override
	public @NonNull Element get(@NonNull Element elem) {
		IntElement intElem = elem.intCast(false);
		if (intElem == null) {
			throw new IllegalArgumentException(String.format("Built-in method \"get\" requires non-negative int element as argument!"));
		}
		
		int primitiveInt = intElem.primitiveInt();
		if (primitiveInt < 0) {
			throw new IllegalArgumentException(String.format("Built-in method \"get\" requires non-negative int element as argument!"));
		}
		
		return new CharElement(value.raw.charAt(primitiveInt));
	}
	
	@Override
	public @NonNull Element clone() {
		return new StringElement(value.raw);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash("string", value);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StringElement) {
			StringElement other = (StringElement) obj;
			return value.equals(other.value);
		}
		return false;
	}
	
	@Override
	public @NonNull String debugString() {
		return "\"" + StringEscapeUtils.escapeJava(value.raw) + "\"";
	}
}
