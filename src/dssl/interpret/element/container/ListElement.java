package dssl.interpret.element.container;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import dssl.Helpers;
import dssl.interpret.*;
import dssl.interpret.element.*;
import dssl.interpret.element.primitive.IntElement;

public class ListElement extends ContainerElement implements CollectionElement {
	
	public final List<@NonNull Element> value;
	
	protected ListElement(ListElement other) {
		super(BuiltIn.LIST_CLAZZ);
		value = Helpers.map(other.value, Element::clone);
	}
	
	public ListElement(Collection<@NonNull Element> elems) {
		super(BuiltIn.LIST_CLAZZ);
		value = new ArrayList<>(elems);
	}
	
	public ListElement(Iterable<@NonNull Element> elems) {
		super(BuiltIn.LIST_CLAZZ);
		value = new ArrayList<>();
		elems.forEach(value::add);
	}
	
	@Override
	public ListElement listCast() {
		return this;
	}
	
	@Override
	public TupleElement tupleCast() {
		return new TupleElement(value);
	}
	
	@Override
	public SetElement setCast() {
		return new SetElement(value);
	}
	
	@Override
	public Iterator<@NonNull Element> iterator() {
		return value.iterator();
	}
	
	@Override
	public void onEach(TokenExecutor exec, @NonNull Element item) {
		exec.push(item);
	}
	
	@Override
	public Collection<@NonNull Element> collection() {
		return value;
	}
	
	@Override
	public void unpack(TokenExecutor exec) {
		for (@NonNull Element elem : value) {
			exec.push(elem);
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
	public boolean contains(@NonNull Element elem) {
		return value.contains(elem);
	}
	
	@Override
	public void add(@NonNull Element elem) {
		value.add(elem);
	}
	
	@Override
	public void remove(@NonNull Element elem) {
		value.remove(elem);
	}
	
	@Override
	public boolean containsAll(@NonNull Element elem) {
		if (!(elem instanceof CollectionElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"containsAll\" requires collection element as argument!"));
		}
		return value.containsAll(((CollectionElement) elem).collection());
	}
	
	@Override
	public void addAll(@NonNull Element elem) {
		if (!(elem instanceof CollectionElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"addAll\" requires collection element as argument!"));
		}
		value.addAll(((CollectionElement) elem).collection());
	}
	
	@Override
	public void removeAll(@NonNull Element elem) {
		if (!(elem instanceof CollectionElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"removeAll\" requires collection element as argument!"));
		}
		value.removeAll(((CollectionElement) elem).collection());
	}
	
	@Override
	public void clear() {
		value.clear();
	}
	
	@SuppressWarnings("null")
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
		
		return value.get(primitiveInt);
	}
	
	@Override
	public void put(@NonNull Element elem0, @NonNull Element elem1) {
		IntElement intElem = elem0.intCast(false);
		if (intElem == null) {
			throw new IllegalArgumentException(String.format("Built-in method \"put\" requires non-negative int element as argument!"));
		}
		
		int primitiveInt = intElem.primitiveInt();
		if (primitiveInt < 0) {
			throw new IllegalArgumentException(String.format("Built-in method \"put\" requires non-negative int element as argument!"));
		}
		
		value.set(primitiveInt, elem1);
	}
	
	@Override
	public @NonNull Element clone() {
		return new ListElement(this);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash("list", value);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ListElement) {
			ListElement other = (ListElement) obj;
			return value.equals(other.value);
		}
		return false;
	}
	
	@Override
	public @NonNull String toString() {
		return "list:" + value.toString();
	}
	
	@Override
	public @NonNull String debugString() {
		return "list:[|" + size() + "|]";
	}
}
