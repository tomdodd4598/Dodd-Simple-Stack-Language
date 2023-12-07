package dssl.interpret.element;

import java.math.BigInteger;
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.iter.IterElement;
import dssl.interpret.element.primitive.IntElement;

public class RangeElement extends Element implements IterableElement {
	
	protected final @NonNull BigInteger start, stop, step;
	protected final long size;
	
	@SuppressWarnings("null")
	public <T extends Element> RangeElement(TokenExecutor exec, Reverse<@NonNull T> elems) {
		super(BuiltIn.RANGE_CLAZZ);
		int elemCount = elems.size();
		if (elemCount < 1 || elemCount > 3) {
			throw new IllegalArgumentException(String.format("Constructor for type \"%s\" requires one to three %s elements as arguments but received %s!", BuiltIn.RANGE, BuiltIn.INT, elemCount));
		}
		
		int index = 0;
		@NonNull BigInteger[] args = new @NonNull BigInteger[3];
		for (@NonNull Element elem : elems) {
			IntElement intElem = elem.asInt(exec);
			if (intElem == null) {
				throw new IllegalArgumentException(String.format("Constructor for type \"%s\" requires one to three %s elements as arguments!", BuiltIn.RANGE, BuiltIn.INT));
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
	
	public RangeElement(@NonNull BigInteger start, @NonNull BigInteger stop, @NonNull BigInteger step, long size) {
		super(BuiltIn.RANGE_CLAZZ);
		
		if (step.equals(BigInteger.ZERO)) {
			throw new IllegalArgumentException(String.format("Range element constructed with zero step size!"));
		}
		
		if (size < 0) {
			throw new IllegalArgumentException(String.format("Range element constructed with negative size!"));
		}
		
		this.start = start;
		this.stop = stop;
		this.step = step;
		this.size = size;
	}
	
	@Override
	public @NonNull RangeElement rangeCast(TokenExecutor exec) {
		return this;
	}
	
	@Override
	public @NonNull ListElement listCast(TokenExecutor exec) {
		return new ListElement(internalIterable(exec));
	}
	
	@Override
	public @NonNull SetElement setCast(TokenExecutor exec) {
		return new SetElement(internalIterable(exec));
	}
	
	@Override
	public @NonNull IterElement iterator(TokenExecutor exec) {
		return new IterElement() {
			
			long index = 0;
			
			@Override
			public boolean hasNext(TokenExecutor exec) {
				return index < size;
			}
			
			@Override
			public @NonNull Element next(TokenExecutor exec) {
				return new IntElement(at(index++));
			}
		};
	}
	
	@Override
	public void unpack(TokenExecutor exec) {
		long index = 0;
		while (index < size) {
			exec.push(new IntElement(at(index++)));
		}
	}
	
	@Override
	public int size(TokenExecutor exec) {
		if (size <= Integer.MAX_VALUE) {
			return (int) size;
		}
		else {
			throw new ArithmeticException(String.format("Range size %s larger than %s!", size, Integer.MAX_VALUE));
		}
	}
	
	@Override
	public boolean isEmpty(TokenExecutor exec) {
		return size == 0;
	}
	
	@Override
	public @NonNull Element iter(TokenExecutor exec) {
		return iterator(exec);
	}
	
	@Override
	public boolean contains(TokenExecutor exec, @NonNull Element elem) {
		IntElement intElem = elem.asInt(exec);
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
	public boolean containsAll(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof IterableElement)) {
			throw new IllegalArgumentException(String.format("Built-in method \"containsAll\" requires %s element as argument!", BuiltIn.ITERABLE));
		}
		for (@NonNull Element e : ((IterableElement) elem).internalIterable(exec)) {
			if (!contains(exec, e)) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public @NonNull Element get(TokenExecutor exec, @NonNull Element elem) {
		long primitiveLong = methodLongIndex(exec, elem, "get");
		methodIndexBoundsCheck(primitiveLong, "get");
		return new IntElement(at(primitiveLong));
	}
	
	@SuppressWarnings("null")
	@Override
	public @NonNull Element slice(TokenExecutor exec, @NonNull Element elem0, @NonNull Element elem1) {
		long begin = methodLongIndex(exec, elem0, "slice", 1);
		methodIndexBoundsCheck(begin, "slice");
		
		long end = methodLongIndex(exec, elem1, "slice", 2);
		methodIndexBoundsCheck(end, "slice");
		
		return new RangeElement(at(begin), at(end), step, end - begin);
	}
	
	@Override
	public @NonNull Element fst(TokenExecutor exec) {
		methodIndexBoundsCheck(0, "fst");
		return new IntElement(start);
	}
	
	@Override
	public @NonNull Element snd(TokenExecutor exec) {
		methodIndexBoundsCheck(1, "snd");
		return new IntElement(start.add(step));
	}
	
	@Override
	public @NonNull Element last(TokenExecutor exec) {
		methodIndexBoundsCheck(size - 1, "last");
		return new IntElement(at(size - 1));
	}
	
	protected BigInteger at(long index) {
		return start.add(step.multiply(BigInteger.valueOf(index)));
	}
	
	protected void methodIndexBoundsCheck(long index, String name) {
		if (index >= size) {
			throw new IndexOutOfBoundsException(String.format("Built-in method \"%s\" (index: %s, size: %s)", name, index, size));
		}
	}
	
	@Override
	public @NonNull String debug(TokenExecutor exec) {
		return "(...)";
	}
	
	@Override
	public @NonNull Element clone() {
		return new RangeElement(start, stop, step, size);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(BuiltIn.RANGE, start, stop, step);
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
		return String.format("(%s, %s, %s)", start, stop, step);
	}
}
