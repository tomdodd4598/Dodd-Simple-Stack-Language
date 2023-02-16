package dssl.interpret.element.container;

import java.math.BigInteger;
import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.*;
import dssl.interpret.element.primitive.IntElement;

public class RangeElement extends ContainerElement implements IterableElement<@NonNull Element> {
	
	protected final @NonNull BigInteger start, stop, step;
	protected final long size;
	
	protected RangeElement(RangeElement other) {
		super(BuiltIn.RANGE_CLAZZ);
		start = other.start;
		stop = other.stop;
		step = other.step;
		size = other.size;
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
		
		size = diff.divide(step).longValueExact();
	}
	
	@Override
	public RangeElement rangeCast() {
		return this;
	}
	
	@Override
	public ListElement listCast() {
		return new ListElement(this);
	}
	
	@Override
	public TupleElement tupleCast() {
		return new TupleElement(this);
	}
	
	@Override
	public SetElement setCast() {
		return new SetElement(this);
	}
	
	@Override
	public Iterator<@NonNull Element> iterator() {
		return new Iterator<@NonNull Element>() {
			
			long index = 0;
			
			@Override
			public boolean hasNext() {
				return index < size;
			}
			
			@Override
			public @NonNull Element next() {
				return new IntElement(start.add(step.multiply(BigInteger.valueOf(index++))));
			}
		};
	}
	
	@Override
	public void onEach(TokenExecutor exec, @NonNull Element item) {
		exec.push(item);
	}
	
	@Override
	public void unpack(TokenExecutor exec) {
		for (@NonNull Element elem : this) {
			exec.push(elem);
		}
	}
	
	@Override
	public int size() {
		if (size >= Integer.MIN_VALUE && size <= Integer.MAX_VALUE) {
			return (int) size;
		}
		else {
			throw new ArithmeticException(String.format("Range size %s out of int range!", size));
		}
	}
	
	@Override
	public boolean isEmpty() {
		return size == 0;
	}
	
	@Override
	public boolean contains(@NonNull Element elem) {
		IntElement intElem = elem.intCast(false);
		if (intElem == null) {
			return false;
		}
		
		@NonNull BigInteger intValue = intElem.value.raw;
		if (intValue.compareTo(start) < 0 || intValue.compareTo(stop) >= 0) {
			return false;
		}
		return intValue.subtract(start).mod(step).equals(BigInteger.ZERO);
	}
	
	@Override
	public boolean containsAll(@NonNull Element elem) {
		if (!(elem instanceof CollectionElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"containsAll\" requires collection element as argument!"));
		}
		return ((CollectionElement) elem).collection().stream().allMatch(this::contains);
	}
	
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
		
		if (primitiveInt >= size) {
			throw new IndexOutOfBoundsException(String.format("Built-in method \"get\" (index: %s, size: %s)", primitiveInt, size));
		}
		
		return new IntElement(start.add(step.multiply(BigInteger.valueOf(primitiveInt))));
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
