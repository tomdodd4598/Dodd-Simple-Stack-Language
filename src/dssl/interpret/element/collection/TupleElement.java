package dssl.interpret.element.collection;

import java.util.*;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.*;
import dssl.interpret.element.primitive.*;

public class TupleElement extends CollectionElement {
	
	public final List<@NonNull Element> value;
	
	public TupleElement(Collection<@NonNull Element> elems) {
		super();
		value = Arrays.asList(elems.toArray(new @NonNull Element[elems.size()]));
	}
	
	@SuppressWarnings("null")
	@Override
	public @NonNull String typeName() {
		return new StringBuilder("tuple:").append(size()).toString();
	}
	
	@Override
	public Element castInternal(@NonNull Element elem) {
		return elem.tupleCastExplicit();
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
		throw new IllegalArgumentException(String.format("Failed to cast tuple \"%s\" to dict as construction requires even number of arguments but received %s!", toString(), elemCount));
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
		for (@NonNull Element elem : value) {
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
		throw keywordError("add");
	}
	
	@Override
	public TokenResult onRem(TokenExecutor exec, @NonNull Element elem) {
		throw keywordError("rem");
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
		throw keywordError("addall");
	}
	
	@Override
	public TokenResult onRemall(TokenExecutor exec, @NonNull Element elem) {
		throw keywordError("remall");
	}
	
	@Override
	public TokenResult onClear(TokenExecutor exec) {
		throw keywordError("clear");
	}
	
	@Override
	public TokenResult onGet(TokenExecutor exec, @NonNull Element elem) {
		IntElement intElem = elem.intCastImplicit();
		if (intElem == null) {
			throw new IllegalArgumentException(String.format("Keyword \"get\" requires non-negative int value element as argument!"));
		}
		
		int primitiveInt = intElem.primitiveInt();
		if (primitiveInt < 0) {
			throw new IllegalArgumentException(String.format("Keyword \"get\" requires non-negative int value element as argument!"));
		}
		
		exec.push(value.get(primitiveInt));
		return TokenResult.PASS;
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
		List<@NonNull Element> valueClone = new ArrayList<>(value.size());
		for (@NonNull Element elem : value) {
			valueClone.add(elem.clone());
		}
		return new TupleElement(valueClone);
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
		return "tuple:" + value.stream().map(Element::toString).collect(Collectors.joining(", ", "(", ")"));
	}
	
	@Override
	public @NonNull String toDebugString() {
		return "tuple:(|" + size() + "|)";
	}
}
