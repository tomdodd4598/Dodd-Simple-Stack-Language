package dssl.interpret.element.collection;

import java.util.*;
import java.util.Map.Entry;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.*;
import dssl.interpret.element.primitive.*;

public class DictElement extends Element implements IterableElement {
	
	public final Map<@NonNull Element, @NonNull Element> value;
	protected List<@NonNull Element> list = null;
	
	public DictElement(Collection<@NonNull Element> elems) {
		super();
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
		super();
		value = new HashMap<>(elems);
	}
	
	@Override
	public @NonNull String typeName() {
		return "dict";
	}
	
	@Override
	public Element castInternal(@NonNull Element elem) {
		return elem.dictCastExplicit();
	}
	
	@Override
	public IntElement intCastImplicit() {
		return null;
	}
	
	@Override
	public @NonNull IntElement intCastExplicit() {
		throw castError("int");
	}
	
	@Override
	public BoolElement boolCastImplicit() {
		return null;
	}
	
	@Override
	public @NonNull BoolElement boolCastExplicit() {
		throw castError("bool");
	}
	
	@Override
	public FloatElement floatCastImplicit() {
		return null;
	}
	
	@Override
	public @NonNull FloatElement floatCastExplicit() {
		throw castError("float");
	}
	
	@Override
	public CharElement charCastImplicit() {
		return null;
	}
	
	@Override
	public @NonNull CharElement charCastExplicit() {
		throw castError("char");
	}
	
	@Override
	public StringElement stringCastImplicit() {
		return null;
	}
	
	@Override
	public @NonNull StringElement stringCastExplicit() {
		return new StringElement(toString());
	}
	
	@Override
	public @NonNull RangeElement rangeCastExplicit() {
		throw castError("range");
	}
	
	@Override
	public @NonNull ListElement listCastExplicit() {
		return new ListElement(list());
	}
	
	@Override
	public @NonNull TupleElement tupleCastExplicit() {
		return new TupleElement(list());
	}
	
	@Override
	public @NonNull SetElement setCastExplicit() {
		return new SetElement(list());
	}
	
	@Override
	public @NonNull DictElement dictCastExplicit() {
		return new DictElement(value);
	}
	
	@Override
	public Iterator<@NonNull Element> iterator() {
		return list().iterator();
	}
	
	@Override
	public Collection<@NonNull Element> collection() {
		return list();
	}
	
	@Override
	public TokenResult onUnpack(TokenExecutor exec) {
		for (@NonNull Element elem : list()) {
			exec.push(elem);
		}
		return TokenResult.PASS;
	}
	
	@Override
	public TokenResult onSize(TokenExecutor exec) {
		exec.push(new IntElement(size()));
		return TokenResult.PASS;
	}
	
	@Override
	public TokenResult onEmpty(TokenExecutor exec) {
		exec.push(new BoolElement(value.isEmpty()));
		return TokenResult.PASS;
	}
	
	@Override
	public TokenResult onHas(TokenExecutor exec, @NonNull Element elem) {
		throw keywordError("has");
	}
	
	@Override
	public TokenResult onAdd(TokenExecutor exec, @NonNull Element elem) {
		throw keywordError("add");
	}
	
	@Override
	public TokenResult onRem(TokenExecutor exec, @NonNull Element elem) {
		@SuppressWarnings("null") Element rem = value.remove(elem);
		if (rem != null) {
			list = null;
		}
		exec.push(rem == null ? NullElement.INSTANCE : rem);
		return TokenResult.PASS;
	}
	
	@Override
	public TokenResult onHasall(TokenExecutor exec, @NonNull Element elem) {
		throw keywordError("hasall");
	}
	
	@Override
	public TokenResult onAddall(TokenExecutor exec, @NonNull Element elem) {
		throw keywordError("addall");
	}
	
	@Override
	public TokenResult onRemall(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof IterableElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"remall\" requires iterable element as second argument!"));
		}
		final Set<@NonNull Element> set = new HashSet<>();
		for (@NonNull Element e : (IterableElement) elem) {
			@SuppressWarnings("null") Element rem = value.remove(e);
			if (rem != null) {
				set.add(rem);
			}
		}
		if (!set.isEmpty()) {
			list = null;
		}
		exec.push(new SetElement(set));
		return TokenResult.PASS;
	}
	
	@Override
	public TokenResult onClear(TokenExecutor exec) {
		value.clear();
		list = null;
		return TokenResult.PASS;
	}
	
	@Override
	public TokenResult onGet(TokenExecutor exec, @NonNull Element elem) {
		exec.push(value.get(elem));
		return TokenResult.PASS;
	}
	
	@Override
	public TokenResult onPut(TokenExecutor exec, @NonNull Element elem0, @NonNull Element elem1) {
		exec.push(value.put(elem0, elem1));
		return TokenResult.PASS;
	}
	
	@Override
	public TokenResult onPutall(TokenExecutor exec, @NonNull Element elem) {
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
			@SuppressWarnings("null") @NonNull Element k = iter.next(), v = iter.next();
			@SuppressWarnings("null") Element put = value.put(k, v);
			if (put != null) {
				set.add(put);
			}
			set.add(put);
		}
		
		if (!set.isEmpty()) {
			list = null;
		}
		return TokenResult.PASS;
	}
	
	public TokenResult onHaskey(TokenExecutor exec, @NonNull Element elem) {
		exec.push(new BoolElement(value.containsKey(elem)));
		return TokenResult.PASS;
	}
	
	public TokenResult onHasvalue(TokenExecutor exec, @NonNull Element elem) {
		exec.push(new BoolElement(value.containsValue(elem)));
		return TokenResult.PASS;
	}
	
	public TokenResult onHasentry(TokenExecutor exec, @NonNull Element elem0, @NonNull Element elem1) {
		exec.push(new BoolElement(hasEntry(elem0, elem1)));
		return TokenResult.PASS;
	}
	
	public TokenResult onKeys(TokenExecutor exec) {
		exec.push(new SetElement(value.keySet()));
		return TokenResult.PASS;
	}
	
	public TokenResult onValues(TokenExecutor exec) {
		exec.push(new SetElement(value.values()));
		return TokenResult.PASS;
	}
	
	public TokenResult onEntries(TokenExecutor exec) {
		exec.push(new ListElement(list()));
		return TokenResult.PASS;
	}
	
	@Override
	public int size() {
		return value.size();
	}
	
	protected List<@NonNull Element> list() {
		if (list == null) {
			list = new ArrayList<>();
			for (Entry<@NonNull Element, @NonNull Element> entry : value.entrySet()) {
				list.add(entry.getKey());
				list.add(entry.getValue());
			}
		}
		return list;
	}
	
	protected boolean hasEntry(@NonNull Element k, @NonNull Element v) {
		@SuppressWarnings("null") Element val = value.get(k);
		return val != null && val.equals(v);
	}
	
	@SuppressWarnings("null")
	@Override
	public @NonNull Element clone() {
		List<@NonNull Element> valueClone = new ArrayList<>(value.size() * 2);
		for (Entry<@NonNull Element, @NonNull Element> entry : value.entrySet()) {
			valueClone.add(entry.getKey().clone());
			valueClone.add(entry.getValue().clone());
		}
		return new DictElement(valueClone);
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
	public @NonNull String toDebugString() {
		return "dict:{|" + size() + "|}";
	}
}
