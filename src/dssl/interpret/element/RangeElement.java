package dssl.interpret.element;

import java.math.BigInteger;
import java.util.Objects;

import org.eclipse.jdt.annotation.*;

import dssl.interpret.*;
import dssl.interpret.element.iter.IterElement;
import dssl.interpret.element.primitive.IntElement;

public class RangeElement extends Element {
	
	protected final @NonNull BigInteger start, stop, step;
	protected final long size;
	
	@SuppressWarnings("null")
	public <T extends Element> RangeElement(TokenExecutor exec, Reverse<@NonNull T> elems) {
		super(exec.interpreter, exec.interpreter.builtIn.rangeClazz);
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
	
	public RangeElement(Interpreter interpreter, @NonNull BigInteger start, @NonNull BigInteger stop, @NonNull BigInteger step, long size) {
		super(interpreter, interpreter.builtIn.rangeClazz);
		
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
		return new ListElement(interpreter, internalIterable(exec));
	}
	
	@Override
	public @NonNull SetElement setCast(TokenExecutor exec) {
		return new SetElement(interpreter, internalStream(exec).map(x -> x.toKey(exec)));
	}
	
	@Override
	public @NonNull IterElement iterator(TokenExecutor exec) {
		return new IterElement(interpreter) {
			
			long index = 0;
			
			@Override
			public boolean hasNext(TokenExecutor exec) {
				return index < size;
			}
			
			@Override
			public @NonNull Element next(TokenExecutor exec) {
				return new IntElement(interpreter, at(index++));
			}
		};
	}
	
	@Override
	public @NonNull Element iter(TokenExecutor exec) {
		return iterator(exec);
	}
	
	@Override
	public void unpack(TokenExecutor exec) {
		long index = 0;
		while (index < size) {
			exec.push(new IntElement(interpreter, at(index++)));
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
	public boolean contains(TokenExecutor exec, @NonNull Element elem) {
		IntElement intElem = elem.asInt(exec);
		if (intElem == null) {
			throw new IllegalArgumentException(String.format("Built-in method \"contains\" requires %s element as argument!", BuiltIn.INT));
		}
		
		@NonNull BigInteger intValue = intElem.value.raw;
		if (intValue.compareTo(start) < 0 || intValue.compareTo(stop) >= 0) {
			return false;
		}
		return intValue.subtract(start).mod(step).equals(BigInteger.ZERO);
	}
	
	@Override
	public boolean containsAll(TokenExecutor exec, @NonNull Element elem) {
		@Nullable Iterable<@NonNull Element> iterable = elem.internalIterable(exec);
		if (iterable == null) {
			throw new IllegalArgumentException(String.format("Built-in method \"containsAll\" requires %s element as argument!", BuiltIn.ITERABLE));
		}
		for (@NonNull Element e : iterable) {
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
		return new IntElement(interpreter, at(primitiveLong));
	}
	
	@SuppressWarnings("null")
	@Override
	public @NonNull Element slice(TokenExecutor exec, @NonNull Element elem0, @NonNull Element elem1) {
		long begin = methodLongIndex(exec, elem0, "slice", 1);
		methodIndexBoundsCheck(begin, "slice");
		
		long end = methodLongIndex(exec, elem1, "slice", 2);
		methodIndexBoundsCheck(end, "slice");
		
		return new RangeElement(interpreter, at(begin), at(end), step, end - begin);
	}
	
	@Override
	public @NonNull Element fst(TokenExecutor exec) {
		methodIndexBoundsCheck(0, "fst");
		return new IntElement(interpreter, start);
	}
	
	@Override
	public @NonNull Element snd(TokenExecutor exec) {
		methodIndexBoundsCheck(1, "snd");
		return new IntElement(interpreter, start.add(step));
	}
	
	@Override
	public @NonNull Element last(TokenExecutor exec) {
		methodIndexBoundsCheck(size - 1, "last");
		return new IntElement(interpreter, at(size - 1));
	}
	
	@Override
	public @NonNull Element indexOf(TokenExecutor exec, @NonNull Element elem) {
		IntElement intElem = elem.asInt(exec);
		if (intElem == null) {
			throw new IllegalArgumentException(String.format("Built-in method \"indexOf\" requires %s element as argument!", BuiltIn.INT));
		}
		
		@NonNull BigInteger intValue = intElem.value.raw;
		if (intValue.compareTo(start) < 0 || intValue.compareTo(stop) >= 0) {
			return interpreter.builtIn.nullElement;
		}
		
		BigInteger[] divRem = intValue.subtract(start).divideAndRemainder(step);
		return divRem[1].equals(BigInteger.ZERO) ? new IntElement(interpreter, divRem[0]) : interpreter.builtIn.nullElement;
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
		return new RangeElement(interpreter, start, stop, step, size);
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
	public @NonNull String toString(TokenExecutor exec) {
		return String.format("(%s, %s, %s)", start, stop, step);
	}
}
