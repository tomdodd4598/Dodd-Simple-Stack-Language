package dssl.interpret.element;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.*;

import dssl.interpret.*;
import dssl.interpret.element.iter.IterElement;
import dssl.interpret.element.primitive.StringElement;

public class DictElement extends Element {
	
	public final Map<@NonNull ElementKey, @NonNull Element> value;
	
	@SuppressWarnings("null")
	public <T extends Element> DictElement(TokenExecutor exec, Reverse<@NonNull T> elems) {
		super(exec.interpreter, exec.interpreter.builtIn.dictClazz);
		int elemCount = elems.size();
		if ((elemCount & 1) == 1) {
			throw new IllegalArgumentException(String.format("Constructor for type \"%s\" requires even number of arguments but received %s!", BuiltIn.DICT, elemCount));
		}
		
		value = new HashMap<>();
		Iterator<@NonNull T> iter = elems.iterator();
		while (iter.hasNext()) {
			value.put(iter.next().toKey(exec), iter.next());
		}
	}
	
	public DictElement(Interpreter interpreter, Map<@NonNull ElementKey, @NonNull Element> map) {
		super(interpreter, interpreter.builtIn.dictClazz);
		value = map;
	}
	
	@Override
	public @NonNull StringElement stringCast(TokenExecutor exec) {
		return new StringElement(interpreter, toString(exec));
	}
	
	@Override
	public @NonNull DictElement dictCast(TokenExecutor exec) {
		return this;
	}
	
	@Override
	public @NonNull IterElement iterator(TokenExecutor exec) {
		return new IterElement(interpreter) {
			
			final Iterator<Entry<@NonNull ElementKey, @NonNull Element>> internal = value.entrySet().iterator();
			
			@Override
			public boolean hasNext(TokenExecutor exec) {
				return internal.hasNext();
			}
			
			@SuppressWarnings("null")
			@Override
			public @NonNull Element next(TokenExecutor exec) {
				Entry<@NonNull ElementKey, @NonNull Element> next = internal.next();
				return new ListElement(interpreter, next.getKey().elem, next.getValue());
			}
		};
	}
	
	@Override
	public @NonNull Element iter(TokenExecutor exec) {
		return iterator(exec);
	}
	
	@SuppressWarnings("null")
	@Override
	public void unpack(TokenExecutor exec) {
		for (Entry<@NonNull ElementKey, @NonNull Element> entry : value.entrySet()) {
			exec.push(new ListElement(interpreter, entry.getKey().elem, entry.getValue()));
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
	public void remove(TokenExecutor exec, @NonNull Element elem) {
		value.remove(elem.toKey(exec));
	}
	
	@Override
	public void removeAll(TokenExecutor exec, @NonNull Element elem) {
		@Nullable Iterable<@NonNull Element> iterable = elem.internalIterable(exec);
		if (iterable == null) {
			throw new IllegalArgumentException(String.format("Built-in method \"removeAll\" requires %s element as argument!", BuiltIn.ITERABLE));
		}
		for (@NonNull Element e : iterable) {
			value.remove(e.toKey(exec));
		}
	}
	
	@Override
	public void clear(TokenExecutor exec) {
		value.clear();
	}
	
	@Override
	public @NonNull Element get(TokenExecutor exec, @NonNull Element elem) {
		@SuppressWarnings("null") Element get = value.get(elem.toKey(exec));
		return get == null ? interpreter.builtIn.nullElement : get;
	}
	
	@Override
	public void put(TokenExecutor exec, @NonNull Element elem0, @NonNull Element elem1) {
		value.put(elem0.toKey(exec), elem1);
	}
	
	@Override
	public void putAll(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof DictElement dict)) {
			throw new IllegalArgumentException(String.format("Built-in method \"putAll\" requires %s element as argument!", BuiltIn.DICT));
		}
		value.putAll(dict.value);
	}
	
	@Override
	public void removeEntry(TokenExecutor exec, @NonNull Element elem0, @NonNull Element elem1) {
		value.remove(elem0, elem1);
	}
	
	@Override
	public boolean containsKey(TokenExecutor exec, @NonNull Element elem) {
		return value.containsKey(elem.toKey(exec));
	}
	
	@Override
	public boolean containsValue(TokenExecutor exec, @NonNull Element elem) {
		return value.containsValue(elem);
	}
	
	@Override
	public boolean containsEntry(TokenExecutor exec, @NonNull Element elem0, @NonNull Element elem1) {
		@SuppressWarnings("null") Element get = value.get(elem0.toKey(exec));
		return get == null ? false : get.equals(elem1);
	}
	
	@Override
	public @NonNull Element keys(TokenExecutor exec) {
		return new SetElement(exec, value.keySet().stream().map(x -> x.elem));
	}
	
	@Override
	public @NonNull Element values(TokenExecutor exec) {
		return new ListElement(interpreter, value.values());
	}
	
	@Override
	public @NonNull Element entries(TokenExecutor exec) {
		return new SetElement(exec, internalIterable(exec));
	}
	
	@SuppressWarnings("null")
	@Override
	public @NonNull Element clone(TokenExecutor exec) {
		Map<@NonNull ElementKey, @NonNull Element> map = new HashMap<>();
		for (Entry<@NonNull ElementKey, @NonNull Element> entry : value.entrySet()) {
			map.put(entry.getKey().clone(), entry.getValue().dynClone(exec));
		}
		return new DictElement(interpreter, map);
	}
	
	@SuppressWarnings("null")
	@Override
	public int hash(TokenExecutor exec) {
		int hash = BuiltIn.LIST.hashCode();
		for (Entry<@NonNull ElementKey, @NonNull Element> entry : value.entrySet()) {
			hash = 31 * hash + (entry.getKey().hashCode() ^ entry.getValue().dynHash(exec));
		}
		return hash;
	}
	
	@Override
	public @NonNull String debug(TokenExecutor exec) {
		return "[|...|]";
	}
	
	@Override
	public @NonNull StringElement __str__(TokenExecutor exec) {
		return stringCast(exec);
	}
	
	@Override
	public @NonNull StringElement __debug__(TokenExecutor exec) {
		return new StringElement(interpreter, debug(exec));
	}
	
	@SuppressWarnings("null")
	@Override
	public @NonNull Element clone() {
		Map<@NonNull ElementKey, @NonNull Element> map = new HashMap<>();
		for (Entry<@NonNull ElementKey, @NonNull Element> entry : value.entrySet()) {
			map.put(entry.getKey().clone(), entry.getValue().clone());
		}
		return new DictElement(interpreter, map);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(BuiltIn.DICT, value);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DictElement other) {
			return value.equals(other.value);
		}
		return false;
	}
	
	@SuppressWarnings("null")
	@Override
	public @NonNull String toString(TokenExecutor exec) {
		Function<@NonNull Element, @NonNull String> f = x -> x.innerString(exec, this);
		return value.entrySet().stream().map(x -> f.apply(x.getKey().elem) + ":" + f.apply(x.getValue())).collect(Collectors.joining(", ", "[|", "|]"));
	}
}
