package dssl.interpret.element.container;

import java.math.BigInteger;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.*;
import dssl.interpret.element.primitive.IntElement;

public class RangeElement extends ContainerElement implements IterableElement<@NonNull Element> {
	
	protected final @NonNull BigInteger start, stop, step;
	protected final int size;
	protected final Supplier<Stream<@NonNull Element>> supplier;
	protected List<@NonNull Element> list = null;
	
	protected RangeElement(RangeElement other) {
		super(BuiltIn.RANGE_CLAZZ);
		start = other.start;
		stop = other.stop;
		step = other.step;
		size = other.size;
		supplier = other.supplier;
	}
	
	@SuppressWarnings("null")
	public RangeElement(Collection<@NonNull Element> elems) {
		super(BuiltIn.RANGE_CLAZZ);
		int elemCount = elems.size();
		if (elemCount < 1 || elemCount > 3) {
			throw new IllegalArgumentException(String.format("Range element construction requires between one and three arguments but received %s!", elemCount));
		}
		
		int index = 0;
		@NonNull BigInteger[] args = new @NonNull BigInteger[3];
		for (@NonNull Element elem : elems) {
			IntElement intElem = elem.intCast(false);
			if (intElem == null) {
				throw new IllegalArgumentException(String.format("Range element construction requires int elements as arguments!"));
			}
			
			args[index] = intElem.value.raw;
			++index;
		}
		
		@NonNull BigInteger start, stop, step;
		if (elemCount == 1) {
			start = BigInteger.ZERO;
			stop = args[0];
			step = BigInteger.ONE;
		}
		else if (elemCount == 2) {
			start = args[0];
			stop = args[1];
			step = BigInteger.ONE;
		}
		else {
			start = args[0];
			stop = args[1];
			step = args[2];
		}
		
		if (step.equals(BigInteger.ZERO)) {
			throw new IllegalArgumentException(String.format("Range element constructed with zero step size!"));
		}
		
		BigInteger diff = stop.subtract(start);
		int diffComp = diff.compareTo(BigInteger.ZERO), stepComp = step.compareTo(BigInteger.ZERO);
		if ((diffComp > 0 && stepComp < 0) || (diffComp < 0 && stepComp > 0)) {
			throw new IllegalArgumentException(String.format("Range element constructed with invalid arguments start = %s, stop = %s, step = %s!", start, stop, step));
		}
		
		this.start = start;
		this.stop = stop;
		this.step = step;
		
		size = diff.divide(step).intValueExact();
		
		supplier = () -> StreamSupport.stream(new Spliterator<@NonNull Element>() {
			
			int index = 0;
			
			@Override
			public boolean tryAdvance(Consumer<? super @NonNull Element> action) {
				if (index < size) {
					action.accept(new IntElement(start.add(step.multiply(BigInteger.valueOf(index++)))));
					return true;
				}
				return false;
			}
			
			@Override
			public Spliterator<@NonNull Element> trySplit() {
				return null;
			}
			
			@Override
			public long estimateSize() {
				return size - index;
			}
			
			@Override
			public int characteristics() {
				return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.IMMUTABLE | Spliterator.NONNULL | Spliterator.DISTINCT;
			}
			
		}, false);
	}
	
	@Override
	public RangeElement rangeCast() {
		return this;
	}
	
	@Override
	public ListElement listCast() {
		return new ListElement(list());
	}
	
	@Override
	public TupleElement tupleCast() {
		return new TupleElement(list());
	}
	
	@Override
	public SetElement setCast() {
		return new SetElement(list());
	}
	
	@Override
	public Iterator<@NonNull Element> iterator() {
		return supplier.get().iterator();
	}
	
	@SuppressWarnings("null")
	@Override
	public void onEach(TokenExecutor exec, Object item) {
		exec.push((Element) item);
	}
	
	@Override
	public void unpack(TokenExecutor exec) {
		for (@NonNull Element elem : this) {
			exec.push(elem);
		}
	}
	
	@Override
	public int size() {
		return size;
	}
	
	@Override
	public boolean isEmpty() {
		return size == 0;
	}
	
	@Override
	public boolean contains(@NonNull Element elem) {
		return list().contains(elem);
	}
	
	@Override
	public boolean containsAll(@NonNull Element elem) {
		if (!(elem instanceof CollectionElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"containsAll\" requires collection element as argument!"));
		}
		return list().containsAll(((CollectionElement) elem).collection());
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
		
		return list().get(primitiveInt);
	}
	
	protected List<@NonNull Element> list() {
		if (list == null) {
			list = new ArrayList<>();
			supplier.get().collect(Collectors.toList());
		}
		return list;
	}
	
	@Override
	public @NonNull Element clone() {
		return new RangeElement(this);
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
	public @NonNull String debugString() {
		return String.format("range:(%s, %s, %s)", start, stop, step);
	}
}
