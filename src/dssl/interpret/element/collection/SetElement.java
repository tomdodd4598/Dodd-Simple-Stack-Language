package dssl.interpret.element.collection;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.Element;
import dssl.interpret.element.dict.DictElement;
import dssl.interpret.element.range.RangeElement;
import dssl.interpret.element.value.IterableElement;
import dssl.interpret.element.value.primitive.BoolElement;

public class SetElement extends CollectionElement {
	
	public final Set<@NonNull Element> value;
	
	public SetElement(Collection<@NonNull Element> elems) {
		super();
		value = new HashSet<>(elems);
	}
	
	@Override
	public @NonNull String typeName() {
		return "set";
	}
	
	@Override
	public Element castInternal(@NonNull Element elem) {
		return elem.setCastExplicit();
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
		throw castError("dict");
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
		for (Element elem : value) {
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
		throw keywordError("get");
	}
	
	@Override
	public InterpretResult onPut(Executor exec, @NonNull Element elem0, @NonNull Element elem1) {
		throw keywordError("put");
	}
	
	@Override
	public InterpretResult onPutall(Executor exec, @NonNull Element elem) {
		throw keywordError("putall");
	}
	
	@Override
	public int size() {
		return value.size();
	}
	
	@Override
	public @NonNull Element clone() {
		Set<@NonNull Element> valueClone = new HashSet<>(value.size());
		for (@NonNull Element elem : value) {
			valueClone.add(elem.clone());
		}
		return new SetElement(valueClone);
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
		return "set:" + value.toString();
	}
	
	@Override
	public @NonNull String toBriefDebugString() {
		return "set:{|" + size() + "|}";
	}
}
