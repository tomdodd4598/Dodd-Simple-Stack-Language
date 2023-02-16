package dssl.interpret.element.container;

import java.util.*;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNull;

import dssl.Helpers;
import dssl.interpret.*;
import dssl.interpret.element.*;

public class SetElement extends ContainerElement implements CollectionElement {
	
	public final Set<@NonNull Element> value;
	
	protected SetElement(SetElement other) {
		super(BuiltIn.SET_CLAZZ);
		value = Helpers.map(other.value, Element::clone);
	}
	
	public SetElement(Collection<@NonNull Element> elems) {
		super(BuiltIn.SET_CLAZZ);
		value = new HashSet<>(elems);
	}
	
	public SetElement(Iterable<@NonNull Element> elems) {
		super(BuiltIn.SET_CLAZZ);
		value = new HashSet<>();
		elems.forEach(value::add);
	}
	
	@Override
	public ListElement listCast() {
		return new ListElement(value);
	}
	
	@Override
	public TupleElement tupleCast() {
		return new TupleElement(value);
	}
	
	@Override
	public SetElement setCast() {
		return this;
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
	
	@Override
	public @NonNull Element clone() {
		return new SetElement(this);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash("set", value);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SetElement) {
			SetElement other = (SetElement) obj;
			return value.equals(other.value);
		}
		return false;
	}
	
	@Override
	public @NonNull String toString() {
		return "set:" + Helpers.collectString(value, Collectors.joining(", ", "{", "}"));
	}
	
	@Override
	public @NonNull String debugString() {
		return "set:{|" + size() + "|}";
	}
}
