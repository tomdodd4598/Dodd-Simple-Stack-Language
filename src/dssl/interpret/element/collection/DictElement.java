package dssl.interpret.element.collection;

import java.util.*;
import java.util.Map.Entry;

import org.eclipse.jdt.annotation.NonNull;

import dssl.Helpers;
import dssl.interpret.*;
import dssl.interpret.element.*;
import dssl.interpret.element.primitive.*;

public class DictElement extends Element implements IterableElement {
	
	public final Map<@NonNull Element, @NonNull Element> value;
	protected List<@NonNull Element> list = null;
	protected Set<@NonNull Element> entries = null;
	
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
	public int size() {
		return value.size();
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
	public TokenResult onContains(TokenExecutor exec, @NonNull Element elem) {
		throw keywordError("contains");
	}
	
	@Override
	public TokenResult onAdd(TokenExecutor exec, @NonNull Element elem) {
		throw keywordError("add");
	}
	
	@Override
	public TokenResult onRemove(TokenExecutor exec, @NonNull Element elem) {
		if (value.remove(elem) != null) {
			list = null;
			entries = null;
		}
		return TokenResult.PASS;
	}
	
	@Override
	public TokenResult onContainsall(TokenExecutor exec, @NonNull Element elem) {
		throw keywordError("containsall");
	}
	
	@Override
	public TokenResult onAddall(TokenExecutor exec, @NonNull Element elem) {
		throw keywordError("addall");
	}
	
	@Override
	public TokenResult onRemoveall(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof IterableElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"removeall\" requires iterable element as second argument!"));
		}
		
		boolean modified = false;
		for (@NonNull Element e : (IterableElement) elem) {
			if (value.remove(e) != null) {
				modified = true;
			}
		}
		
		if (modified) {
			list = null;
			entries = null;
		}
		return TokenResult.PASS;
	}
	
	@Override
	public TokenResult onClear(TokenExecutor exec) {
		value.clear();
		list = null;
		entries = null;
		return TokenResult.PASS;
	}
	
	@Override
	public TokenResult onGet(TokenExecutor exec, @NonNull Element elem) {
		@SuppressWarnings("null") Element get = value.get(elem);
		exec.push(get == null ? NullElement.INSTANCE : get);
		return TokenResult.PASS;
	}
	
	@Override
	public TokenResult onPut(TokenExecutor exec, @NonNull Element elem0, @NonNull Element elem1) {
		if (value.put(elem0, elem1) != null) {
			list = null;
			entries = null;
		}
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
		
		boolean modified = false;
		Iterator<@NonNull Element> iter = iterableElem.iterator();
		while (iter.hasNext()) {
			@SuppressWarnings("null") Element k = iter.next(), v = iter.next();
			if (value.put(k, v) != null) {
				modified = true;
			}
		}
		
		if (modified) {
			list = null;
			entries = null;
		}
		return TokenResult.PASS;
	}
	
	public TokenResult onContainskey(TokenExecutor exec, @NonNull Element elem) {
		exec.push(new BoolElement(value.containsKey(elem)));
		return TokenResult.PASS;
	}
	
	public TokenResult onContainsvalue(TokenExecutor exec, @NonNull Element elem) {
		exec.push(new BoolElement(value.containsValue(elem)));
		return TokenResult.PASS;
	}
	
	public TokenResult onContainsentry(TokenExecutor exec, @NonNull Element elem0, @NonNull Element elem1) {
		exec.push(new BoolElement(containsEntry(elem0, elem1)));
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
		exec.push(new SetElement(entries()));
		return TokenResult.PASS;
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
	
	protected Set<@NonNull Element> entries() {
		if (entries == null) {
			entries = Helpers.map(value.entrySet(), x -> new TupleElement(Arrays.asList(x.getKey(), x.getValue())));
		}
		return entries;
	}
	
	protected boolean containsEntry(@NonNull Element k, @NonNull Element v) {
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
