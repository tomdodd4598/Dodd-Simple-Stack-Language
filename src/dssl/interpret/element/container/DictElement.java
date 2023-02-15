package dssl.interpret.element.container;

import java.util.*;
import java.util.Map.Entry;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.*;

public class DictElement extends ContainerElement implements IterableElement<Entry<@NonNull Element, @NonNull Element>> {
	
	public final Map<@NonNull Element, @NonNull Element> value;
	
	@SuppressWarnings("null")
	protected DictElement(DictElement other) {
		super(BuiltIn.DICT_CLAZZ);
		value = new HashMap<>();
		for (Entry<@NonNull Element, @NonNull Element> entry : other.value.entrySet()) {
			value.put(entry.getKey().clone(), entry.getValue().clone());
		}
	}
	
	public DictElement(Collection<@NonNull Element> elems) {
		super(BuiltIn.DICT_CLAZZ);
		int elemCount = elems.size();
		if ((elemCount & 1) == 1) {
			throw new IllegalArgumentException(String.format("Dict element construction requires even number of arguments but received %s!", elemCount));
		}
		
		value = new HashMap<>();
		Iterator<@NonNull Element> iter = elems.iterator();
		while (iter.hasNext()) {
			value.put(iter.next(), iter.next());
		}
	}
	
	public DictElement(Map<@NonNull Element, @NonNull Element> elems) {
		super(BuiltIn.DICT_CLAZZ);
		value = new HashMap<>(elems);
	}
	
	@Override
	public DictElement dictCast() {
		return this;
	}
	
	@Override
	public Iterator<Entry<@NonNull Element, @NonNull Element>> iterator() {
		return value.entrySet().iterator();
	}
	
	@Override
	public void onEach(TokenExecutor exec, Object item) {
		exec.push(((Entry<@NonNull Element, @NonNull Element>) item).getKey());
		exec.push(((Entry<@NonNull Element, @NonNull Element>) item).getValue());
	}
	
	@Override
	public void unpack(TokenExecutor exec) {
		for (Entry<@NonNull Element, @NonNull Element> entry : value.entrySet()) {
			exec.push(entry.getKey());
			exec.push(entry.getValue());
		}
	}
	
	@Override
	public int size() {
		return value.size();
	}
	
	@Override
	public boolean isEmpty() {
		return value.isEmpty();
	}
	
	@Override
	public void remove(@NonNull Element elem) {
		value.remove(elem);
	}
	
	@Override
	public void removeAll(@NonNull Element elem) {
		if (!(elem instanceof CollectionElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"removeAll\" requires collection element as argument!"));
		}
		for (@NonNull Element e : (CollectionElement) elem) {
			value.remove(e);
		}
	}
	
	@Override
	public void clear() {
		value.clear();
	}
	
	@Override
	public @NonNull Element get(@NonNull Element elem) {
		@SuppressWarnings("null") Element get = value.get(elem);
		return get == null ? NullElement.INSTANCE : get;
	}
	
	@Override
	public void put(@NonNull Element elem0, @NonNull Element elem1) {
		value.put(elem0, elem1);
	}
	
	@Override
	public void putAll(@NonNull Element elem) {
		if (!(elem instanceof DictElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"putAll\" requires dict element as argument!"));
		}
		value.putAll(((DictElement) elem).value);
	}
	
	@Override
	public boolean containsKey(@NonNull Element elem) {
		return value.containsKey(elem);
	}
	
	@Override
	public boolean containsValue(@NonNull Element elem) {
		return value.containsValue(elem);
	}
	
	@Override
	public @NonNull Element keys() {
		return new SetElement(value.keySet());
	}
	
	@Override
	public @NonNull Element values() {
		return new ListElement(value.values());
	}
	
	@Override
	public @NonNull Element clone() {
		return new DictElement(this);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash("dict", value);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DictElement) {
			DictElement other = (DictElement) obj;
			return value.equals(other.value);
		}
		return false;
	}
	
	@Override
	public @NonNull String toString() {
		return "dict:" + value.toString();
	}
	
	@Override
	public @NonNull String debugString() {
		return "dict:{|" + size() + "|}";
	}
}
