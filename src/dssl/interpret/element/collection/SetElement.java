package dssl.interpret.element.collection;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.*;
import dssl.interpret.element.primitive.BoolElement;

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
		throw castError("range");
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
	public TokenResult onUnpack(TokenExecutor exec) {
		for (Element elem : value) {
			exec.push(elem);
		}
		return TokenResult.PASS;
	}
	
	@Override
	public TokenResult onEmpty(TokenExecutor exec) {
		exec.push(new BoolElement(value.isEmpty()));
		return TokenResult.PASS;
	}
	
	@Override
	public TokenResult onHas(TokenExecutor exec, @NonNull Element elem) {
		exec.push(new BoolElement(value.contains(elem)));
		return TokenResult.PASS;
	}
	
	@Override
	public TokenResult onAdd(TokenExecutor exec, @NonNull Element elem) {
		exec.push(new BoolElement(value.add(elem)));
		return TokenResult.PASS;
	}
	
	@Override
	public TokenResult onRem(TokenExecutor exec, @NonNull Element elem) {
		exec.push(new BoolElement(value.remove(elem)));
		return TokenResult.PASS;
	}
	
	@Override
	public TokenResult onHasall(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof IterableElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"hasall\" requires iterable element as second argument!"));
		}
		exec.push(new BoolElement(value.containsAll(((IterableElement) elem).collection())));
		return TokenResult.PASS;
	}
	
	@Override
	public TokenResult onAddall(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof IterableElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"addall\" requires iterable element as second argument!"));
		}
		exec.push(new BoolElement(value.addAll(((IterableElement) elem).collection())));
		return TokenResult.PASS;
	}
	
	@Override
	public TokenResult onRemall(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof IterableElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"remall\" requires iterable element as second argument!"));
		}
		exec.push(new BoolElement(value.removeAll(((IterableElement) elem).collection())));
		return TokenResult.PASS;
	}
	
	@Override
	public TokenResult onClear(TokenExecutor exec) {
		value.clear();
		return TokenResult.PASS;
	}
	
	@Override
	public TokenResult onGet(TokenExecutor exec, @NonNull Element elem) {
		throw keywordError("get");
	}
	
	@Override
	public TokenResult onPut(TokenExecutor exec, @NonNull Element elem0, @NonNull Element elem1) {
		throw keywordError("put");
	}
	
	@Override
	public TokenResult onPutall(TokenExecutor exec, @NonNull Element elem) {
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
	public @NonNull String toDebugString() {
		return "set:{|" + size() + "|}";
	}
}
