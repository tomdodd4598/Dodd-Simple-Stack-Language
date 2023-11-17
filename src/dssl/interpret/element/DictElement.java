package dssl.interpret.element;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.iter.IterElement;
import dssl.interpret.element.primitive.StringElement;

public class DictElement extends Element implements IterableElement {
	
	public final Map<@NonNull Element, @NonNull Element> value;
	
	public DictElement(TokenExecutor exec, Reverse<@NonNull Element> elems) {
		super(BuiltIn.DICT_CLAZZ);
		int elemCount = elems.size();
		if ((elemCount & 1) == 1) {
			throw new IllegalArgumentException(String.format("Constructor for type \"%s\" requires even number of arguments but received %s!", BuiltIn.DICT, elemCount));
		}
		
		value = new HashMap<>();
		Iterator<@NonNull Element> iter = elems.iterator();
		while (iter.hasNext()) {
			value.put(iter.next(), iter.next());
		}
	}
	
	public DictElement(Map<@NonNull Element, @NonNull Element> map, boolean copy) {
		super(BuiltIn.DICT_CLAZZ);
		value = copy ? new HashMap<>(map) : map;
	}
	
	@SuppressWarnings("null")
	@Override
	public @NonNull StringElement stringCast(TokenExecutor exec) {
		Function<@NonNull Element, @NonNull String> f = x -> x.innerString(exec, this);
		return new StringElement(value.entrySet().stream().map(x -> f.apply(x.getKey()) + ":" + f.apply(x.getValue())).collect(Collectors.joining(" ", "[| ", " |]")));
	}
	
	@Override
	public @NonNull DictElement dictCast(TokenExecutor exec) {
		return this;
	}
	
	@Override
	public @NonNull IterElement iterator(TokenExecutor exec) {
		return new IterElement() {
			
			final Iterator<Entry<@NonNull Element, @NonNull Element>> internal = value.entrySet().iterator();
			
			@Override
			public boolean hasNext(TokenExecutor exec) {
				return internal.hasNext();
			}
			
			@Override
			public @NonNull Element next(TokenExecutor exec) {
				Entry<@NonNull Element, @NonNull Element> next = internal.next();
				return new ListElement(next.getKey(), next.getValue());
			}
		};
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
	public void remove(TokenExecutor exec, @NonNull Element elem) {
		value.remove(elem);
	}
	
	@Override
	public void removeAll(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof IterableElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"removeAll\" requires %s element as argument!", BuiltIn.ITERABLE));
		}
		for (@NonNull Element e : ((IterableElement) elem).internal(exec)) {
			value.remove(e);
		}
	}
	
	@Override
	public void clear(TokenExecutor exec) {
		value.clear();
	}
	
	@Override
	public @NonNull Element get(TokenExecutor exec, @NonNull Element elem) {
		@SuppressWarnings("null") Element get = value.get(elem);
		return get == null ? NullElement.INSTANCE : get;
	}
	
	@Override
	public void put(TokenExecutor exec, @NonNull Element elem0, @NonNull Element elem1) {
		value.put(elem0, elem1);
	}
	
	@Override
	public void putAll(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof DictElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"putAll\" requires %s element as argument!", BuiltIn.DICT));
		}
		value.putAll(((DictElement) elem).value);
	}
	
	@Override
	public boolean containsKey(TokenExecutor exec, @NonNull Element elem) {
		return value.containsKey(elem);
	}
	
	@Override
	public boolean containsValue(TokenExecutor exec, @NonNull Element elem) {
		return value.containsValue(elem);
	}
	
	@Override
	public @NonNull Element keys(TokenExecutor exec) {
		return new SetElement(value.keySet());
	}
	
	@Override
	public @NonNull Element values(TokenExecutor exec) {
		return new ListElement(value.values());
	}
	
	@Override
	public @NonNull String debug(TokenExecutor exec) {
		return "[| ... |]";
	}
	
	@Override
	public @NonNull Element clone() {
		return new DictElement(value, true);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(BuiltIn.DICT, value);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DictElement) {
			return value.equals(((DictElement) obj).value);
		}
		return false;
	}
	
	@SuppressWarnings("null")
	@Override
	public @NonNull String toString() {
		Function<@NonNull Element, @NonNull String> f = x -> x.innerString(null, this);
		return value.entrySet().stream().map(x -> f.apply(x.getKey()) + ":" + f.apply(x.getValue())).collect(Collectors.joining(" ", "[| ", " |]"));
	}
}