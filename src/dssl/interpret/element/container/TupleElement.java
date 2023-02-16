package dssl.interpret.element.container;

import java.util.*;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNull;

import dssl.Helpers;
import dssl.interpret.*;
import dssl.interpret.element.*;
import dssl.interpret.element.primitive.IntElement;

public class TupleElement extends ContainerElement implements CollectionElement {
	
	public final List<@NonNull Element> value;
	
	protected TupleElement(TupleElement other) {
		super(BuiltIn.TUPLE_CLAZZ);
		value = Helpers.map(other.value, Element::clone);
	}
	
	public TupleElement(Collection<@NonNull Element> elems) {
		super(BuiltIn.TUPLE_CLAZZ);
		value = new ArrayList<>(elems);
	}
	
	public TupleElement(Iterable<@NonNull Element> elems) {
		super(BuiltIn.TUPLE_CLAZZ);
		value = new ArrayList<>();
		elems.forEach(value::add);
	}
	
	@Override
	public ListElement listCast() {
		return new ListElement(value);
	}
	
	@Override
	public TupleElement tupleCast() {
		return this;
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
	public boolean containsAll(@NonNull Element elem) {
		if (!(elem instanceof CollectionElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"containsAll\" requires collection element as argument!"));
		}
		return value.containsAll(((CollectionElement) elem).collection());
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
	public @NonNull Element clone() {
		return new TupleElement(this);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash("tuple", value);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TupleElement) {
			TupleElement other = (TupleElement) obj;
			return value.equals(other.value);
		}
		return false;
	}
	
	@Override
	public @NonNull String toString() {
		return "tuple:" + Helpers.collectString(value, Collectors.joining(", ", "(", ")"));
	}
	
	@Override
	public @NonNull String debugString() {
		return "tuple:(|" + size() + "|)";
	}
}
