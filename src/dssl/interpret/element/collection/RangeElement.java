package dssl.interpret.element.collection;

import java.util.*;
import java.util.stream.IntStream;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.*;
import dssl.interpret.element.primitive.*;

public class RangeElement extends ValueElement implements IterableElement {
	
	protected final int start, stop, step;
	protected final IntStream value;
	protected List<@NonNull Element> list = null;
	
	public RangeElement(Collection<@NonNull Element> elems) {
		super(BuiltIn.RANGE_CLAZZ);
		int elemCount = elems.size();
		if (elemCount < 1 || elemCount > 3) {
			throw new IllegalArgumentException(String.format("Range element construction requires between one and three arguments but received %s!", elemCount));
		}
		
		int index = 0;
		int[] args = new int[3];
		for (@NonNull Element elem : elems) {
			IntElement intElem = elem.intCastImplicit();
			if (intElem == null) {
				throw new IllegalArgumentException(String.format("Range element construction requires int elements as arguments!"));
			}
			
			args[index] = intElem.primitiveInt();
			++index;
		}
		
		int start, stop, step;
		if (elemCount == 1) {
			start = 0;
			stop = args[0];
			step = 1;
		}
		else if (elemCount == 2) {
			start = args[0];
			stop = args[1];
			step = 1;
		}
		else {
			start = args[0];
			stop = args[1];
			step = args[2];
		}
		
		if (step == 0) {
			throw new IllegalArgumentException(String.format("Range element constructed with zero step size!"));
		}
		
		int diff = stop - start;
		if ((diff > 0 && step < 0) || (diff < 0 && step > 0)) {
			throw new IllegalArgumentException(String.format("Range element constructed with invalid arguments start = %s, stop = %s, step = %s!", start, stop, step));
		}
		
		this.start = start;
		this.stop = stop;
		this.step = step;
		
		value = IntStream.range(0, diff / step).map(x -> start + step * x);
	}
	
	@Override
	public @NonNull String typeName() {
		return "range";
	}
	
	@Override
	public Element castInternal(@NonNull Element elem) {
		return elem.rangeCastExplicit();
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
		return new RangeElement(list());
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
		throw castError("dict");
	}
	
	@Override
	public int size() {
		return list().size();
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
		exec.push(new BoolElement(list().isEmpty()));
		return TokenResult.PASS;
	}
	
	@Override
	public TokenResult onContains(TokenExecutor exec, @NonNull Element elem) {
		exec.push(new BoolElement(list().contains(elem)));
		return TokenResult.PASS;
	}
	
	@Override
	public TokenResult onAdd(TokenExecutor exec, @NonNull Element elem) {
		throw keywordError("add");
	}
	
	@Override
	public TokenResult onRemove(TokenExecutor exec, @NonNull Element elem) {
		throw keywordError("remove");
	}
	
	@Override
	public TokenResult onContainsall(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof IterableElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"containsall\" requires iterable element as second argument!"));
		}
		exec.push(new BoolElement(list().containsAll(((IterableElement) elem).collection())));
		return TokenResult.PASS;
	}
	
	@Override
	public TokenResult onAddall(TokenExecutor exec, @NonNull Element elem) {
		throw keywordError("addall");
	}
	
	@Override
	public TokenResult onRemoveall(TokenExecutor exec, @NonNull Element elem) {
		throw keywordError("removeall");
	}
	
	@Override
	public TokenResult onClear(TokenExecutor exec) {
		throw keywordError("clear");
	}
	
	@Override
	public TokenResult onGet(TokenExecutor exec, @NonNull Element elem) {
		IntElement intElem = elem.intCastImplicit();
		if (intElem == null) {
			throw new IllegalArgumentException(String.format("Keyword \"get\" requires non-negative int element as argument!"));
		}
		
		int primitiveInt = intElem.primitiveInt();
		if (primitiveInt < 0) {
			throw new IllegalArgumentException(String.format("Keyword \"get\" requires non-negative int element as argument!"));
		}
		
		exec.push(list().get(primitiveInt));
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
	
	protected List<@NonNull Element> list() {
		if (list == null) {
			list = new ArrayList<>();
			value.forEach(i -> list.add(new IntElement(i)));
		}
		return list;
	}
	
	@Override
	public @NonNull Element clone() {
		return new RangeElement(Arrays.asList(new IntElement(start), new IntElement(stop), new IntElement(step)));
	}
	
	@Override
	public int hashCode() {
		return Objects.hash("range", start, stop, step);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RangeElement) {
			RangeElement other = (RangeElement) obj;
			return start == other.start && stop == other.stop && step == other.step;
		}
		return false;
	}
	
	@SuppressWarnings("null")
	@Override
	public @NonNull String toString() {
		return String.format("range:(%s, %s, %s)", start, stop, step);
	}
	
	@SuppressWarnings("null")
	@Override
	public @NonNull String toDebugString() {
		return String.format("range:(%s, %s, %s)", start, stop, step);
	}
}
