package dssl.interpret.element.collection;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.Element;
import dssl.interpret.element.dict.DictElement;
import dssl.interpret.element.range.RangeElement;
import dssl.interpret.element.value.IterableElement;
import dssl.interpret.element.value.primitive.*;

public class ListElement extends CollectionElement {
	
	public final List<@NonNull Element> value;
	
	public ListElement(Collection<@NonNull Element> elems) {
		super();
		value = new ArrayList<>(elems);
	}
	
	@Override
	public @NonNull String typeName() {
		return "list";
	}
	
	@Override
	public Element castInternal(@NonNull Element elem) {
		return elem.listCastExplicit();
	}
	
	@Override
	public @NonNull RangeElement rangeCastExplicit() {
		return new RangeElement(value);
	}
	
	@Override
	public @NonNull ListElement listCastExplicit() {
		return new ListElement(value);
	}
	
	@Override
	public @NonNull TupleElement tupleCastExplicit() {
		return new TupleElement(value);
	}
	
	@Override
	public @NonNull SetElement setCastExplicit() {
		return new SetElement(value);
	}
	
	@Override
	public @NonNull DictElement dictCastExplicit() {
		int elemCount = value.size();
		if ((elemCount & 1) == 0) {
			return new DictElement(value);
		}
		throw new IllegalArgumentException(String.format("Failed to cast list \"%s\" to dict as construction requires even number of arguments but received %s!", toString(), elemCount));
	}
	
	@Override
	public Iterator<@NonNull Element> iterator() {
		return value.iterator();
	}
	
	@Override
	public Collection<@NonNull Element> collection() {
		return value;
	}
	
	@Override
	public InterpretResult onUnpack(Executor exec) {
		for (@NonNull Element elem : value) {
			exec.push(elem);
		}
		return InterpretResult.PASS;
	}
	
	@Override
	public InterpretResult onEmpty(Executor exec) {
		exec.push(new BoolElement(value.isEmpty()));
		return InterpretResult.PASS;
	}
	
	@Override
	public InterpretResult onHas(Executor exec, @NonNull Element elem) {
		exec.push(new BoolElement(value.contains(elem)));
		return InterpretResult.PASS;
	}
	
	@Override
	public InterpretResult onAdd(Executor exec, @NonNull Element elem) {
		exec.push(new BoolElement(value.add(elem)));
		return InterpretResult.PASS;
	}
	
	@Override
	public InterpretResult onRem(Executor exec, @NonNull Element elem) {
		exec.push(new BoolElement(value.remove(elem)));
		return InterpretResult.PASS;
	}
	
	@Override
	public InterpretResult onHasall(Executor exec, @NonNull Element elem) {
		if (!(elem instanceof IterableElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"hasall\" requires iterable element as second argument!"));
		}
		exec.push(new BoolElement(value.containsAll(((IterableElement) elem).collection())));
		return InterpretResult.PASS;
	}
	
	@Override
	public InterpretResult onAddall(Executor exec, @NonNull Element elem) {
		if (!(elem instanceof IterableElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"addall\" requires iterable element as second argument!"));
		}
		exec.push(new BoolElement(value.addAll(((IterableElement) elem).collection())));
		return InterpretResult.PASS;
	}
	
	@Override
	public InterpretResult onRemall(Executor exec, @NonNull Element elem) {
		if (!(elem instanceof IterableElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"remall\" requires iterable element as second argument!"));
		}
		exec.push(new BoolElement(value.removeAll(((IterableElement) elem).collection())));
		return InterpretResult.PASS;
	}
	
	@Override
	public InterpretResult onClear(Executor exec) {
		value.clear();
		return InterpretResult.PASS;
	}
	
	@Override
	public InterpretResult onGet(Executor exec, @NonNull Element elem) {
		IntElement intElem = elem.intCastImplicit();
		if (intElem == null) {
			throw new IllegalArgumentException(String.format("Keyword \"get\" requires non-negative int value element as argument!"));
		}
		
		int primitiveInt = intElem.primitiveInt();
		if (primitiveInt < 0) {
			throw new IllegalArgumentException(String.format("Keyword \"get\" requires non-negative int value element as argument!"));
		}
		
		exec.push(value.get(primitiveInt));
		return InterpretResult.PASS;
	}
	
	@Override
	public InterpretResult onPut(Executor exec, @NonNull Element elem0, @NonNull Element elem1) {
		IntElement intElem = elem0.intCastImplicit();
		if (intElem == null) {
			throw new IllegalArgumentException(String.format("Keyword \"get\" requires non-negative int value element as argument!"));
		}
		
		int primitiveInt = intElem.primitiveInt();
		if (primitiveInt < 0) {
			throw new IllegalArgumentException(String.format("Keyword \"get\" requires non-negative int value element as argument!"));
		}
		
		exec.push(value.set(primitiveInt, elem1));
		return InterpretResult.PASS;
	}
	
	@Override
	public InterpretResult onPutall(Executor exec, @NonNull Element elem) {
		if (!(elem instanceof IterableElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"putall\" requires iterable element as second argument!"));
		}
		
		IterableElement iterableElem = (IterableElement) elem;
		int elemCount = iterableElem.size();
		if ((elemCount & 1) == 1) {
			throw new IllegalArgumentException(String.format("The second argument of keyword \"putall\" requires value with an iterator over an even number of elements, but instead it iterates over %s elements!", elemCount));
		}
		
		final Set<@NonNull Element> set = new HashSet<>();
		Iterator<@NonNull Element> iter = iterableElem.iterator();
		while (iter.hasNext()) {
			@SuppressWarnings("null") @NonNull Element index = iter.next();
			IntElement intElem = index.intCastImplicit();
			if (intElem == null) {
				throw new IllegalArgumentException(String.format("The second argument of keyword \"putall\", when the first argument is a list element requires value with an iterator over an even number of elements, where each even-indexed element iterated over must be a non-negative int value!"));
			}
			
			int primitiveInt = intElem.primitiveInt();
			if (primitiveInt < 0) {
				throw new IllegalArgumentException(String.format("The second argument of keyword \"putall\", when the first argument is a list element requires value with an iterator over an even number of elements, where each even-indexed element iterated over must be a non-negative int value!"));
			}
			
			@SuppressWarnings("null") @NonNull Element setElem = iter.next();
			@SuppressWarnings("null") Element put = value.set(primitiveInt, setElem);
			if (put != null) {
				set.add(put);
			}
			set.add(put);
		}
		
		return InterpretResult.PASS;
	}
	
	@Override
	public int size() {
		return value.size();
	}
	
	@Override
	public @NonNull Element clone() {
		List<@NonNull Element> valueClone = new ArrayList<>(value.size());
		for (@NonNull Element elem : value) {
			valueClone.add(elem.clone());
		}
		return new ListElement(valueClone);
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
	public @NonNull String toBriefDebugString() {
		return "list:[|" + size() + "|]";
	}
}
