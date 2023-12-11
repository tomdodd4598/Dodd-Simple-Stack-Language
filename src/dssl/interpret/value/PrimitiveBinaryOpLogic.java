package dssl.interpret.value;

import org.eclipse.jdt.annotation.NonNull;

import dssl.Helpers;
import dssl.interpret.Interpreter;
import dssl.interpret.element.Element;
import dssl.interpret.element.primitive.*;

public class PrimitiveBinaryOpLogic {
	
	public static Element onEqualTo(Interpreter interpreter, @NonNull PrimitiveValue<?> value0, @NonNull PrimitiveValue<?> value1) {
		return new BoolElement(interpreter, value0.equals(value1));
	}
	
	public static Element onNotEqualTo(Interpreter interpreter, @NonNull PrimitiveValue<?> value0, @NonNull PrimitiveValue<?> value1) {
		return new BoolElement(interpreter, !value0.equals(value1));
	}
	
	public static Element onLessThan(Interpreter interpreter, @NonNull PrimitiveValue<?> value0, @NonNull PrimitiveValue<?> value1) {
		if (value0 instanceof IntValue) {
			if (value1 instanceof IntValue) {
				return new BoolElement(interpreter, ((IntValue) value0).raw.compareTo(((IntValue) value1).raw) < 0);
			}
			else if (value1 instanceof FloatValue) {
				return new BoolElement(interpreter, ((IntValue) value0).floatValue(true).compareTo(((FloatValue) value1).raw) < 0);
			}
		}
		else if (value0 instanceof BoolValue && value1 instanceof BoolValue) {
			return new BoolElement(interpreter, ((BoolValue) value0).raw.compareTo(((BoolValue) value1).raw) < 0);
		}
		else if (value0 instanceof FloatValue) {
			if (value1 instanceof IntValue) {
				return new BoolElement(interpreter, ((FloatValue) value0).raw.compareTo(((IntValue) value1).floatValue(true)) < 0);
			}
			else if (value1 instanceof FloatValue) {
				return new BoolElement(interpreter, ((FloatValue) value0).raw.compareTo(((FloatValue) value1).raw) < 0);
			}
		}
		else if (value0 instanceof CharValue && value1 instanceof CharValue) {
			return new BoolElement(interpreter, ((CharValue) value0).raw.compareTo(((CharValue) value1).raw) < 0);
		}
		else if (value0 instanceof StringValue && value1 instanceof StringValue) {
			return new BoolElement(interpreter, ((StringValue) value0).raw.compareTo(((StringValue) value1).raw) < 0);
		}
		return null;
	}
	
	public static Element onLessOrEqual(Interpreter interpreter, @NonNull PrimitiveValue<?> value0, @NonNull PrimitiveValue<?> value1) {
		if (value0 instanceof IntValue) {
			if (value1 instanceof IntValue) {
				return new BoolElement(interpreter, ((IntValue) value0).raw.compareTo(((IntValue) value1).raw) <= 0);
			}
			else if (value1 instanceof FloatValue) {
				return new BoolElement(interpreter, ((IntValue) value0).floatValue(true).compareTo(((FloatValue) value1).raw) <= 0);
			}
		}
		else if (value0 instanceof BoolValue && value1 instanceof BoolValue) {
			return new BoolElement(interpreter, ((BoolValue) value0).raw.compareTo(((BoolValue) value1).raw) <= 0);
		}
		else if (value0 instanceof FloatValue) {
			if (value1 instanceof IntValue) {
				return new BoolElement(interpreter, ((FloatValue) value0).raw.compareTo(((IntValue) value1).floatValue(true)) <= 0);
			}
			else if (value1 instanceof FloatValue) {
				return new BoolElement(interpreter, ((FloatValue) value0).raw.compareTo(((FloatValue) value1).raw) <= 0);
			}
		}
		else if (value0 instanceof CharValue && value1 instanceof CharValue) {
			return new BoolElement(interpreter, ((CharValue) value0).raw.compareTo(((CharValue) value1).raw) <= 0);
		}
		else if (value0 instanceof StringValue && value1 instanceof StringValue) {
			return new BoolElement(interpreter, ((StringValue) value0).raw.compareTo(((StringValue) value1).raw) <= 0);
		}
		return null;
	}
	
	public static Element onMoreThan(Interpreter interpreter, @NonNull PrimitiveValue<?> value0, @NonNull PrimitiveValue<?> value1) {
		if (value0 instanceof IntValue) {
			if (value1 instanceof IntValue) {
				return new BoolElement(interpreter, ((IntValue) value0).raw.compareTo(((IntValue) value1).raw) > 0);
			}
			else if (value1 instanceof FloatValue) {
				return new BoolElement(interpreter, ((IntValue) value0).floatValue(true).compareTo(((FloatValue) value1).raw) > 0);
			}
		}
		else if (value0 instanceof BoolValue && value1 instanceof BoolValue) {
			return new BoolElement(interpreter, ((BoolValue) value0).raw.compareTo(((BoolValue) value1).raw) > 0);
		}
		else if (value0 instanceof FloatValue) {
			if (value1 instanceof IntValue) {
				return new BoolElement(interpreter, ((FloatValue) value0).raw.compareTo(((IntValue) value1).floatValue(true)) > 0);
			}
			else if (value1 instanceof FloatValue) {
				return new BoolElement(interpreter, ((FloatValue) value0).raw.compareTo(((FloatValue) value1).raw) > 0);
			}
		}
		else if (value0 instanceof CharValue && value1 instanceof CharValue) {
			return new BoolElement(interpreter, ((CharValue) value0).raw.compareTo(((CharValue) value1).raw) > 0);
		}
		else if (value0 instanceof StringValue && value1 instanceof StringValue) {
			return new BoolElement(interpreter, ((StringValue) value0).raw.compareTo(((StringValue) value1).raw) > 0);
		}
		return null;
	}
	
	public static Element onMoreOrEqual(Interpreter interpreter, @NonNull PrimitiveValue<?> value0, @NonNull PrimitiveValue<?> value1) {
		if (value0 instanceof IntValue) {
			if (value1 instanceof IntValue) {
				return new BoolElement(interpreter, ((IntValue) value0).raw.compareTo(((IntValue) value1).raw) >= 0);
			}
			else if (value1 instanceof FloatValue) {
				return new BoolElement(interpreter, ((IntValue) value0).floatValue(true).compareTo(((FloatValue) value1).raw) >= 0);
			}
		}
		else if (value0 instanceof BoolValue && value1 instanceof BoolValue) {
			return new BoolElement(interpreter, ((BoolValue) value0).raw.compareTo(((BoolValue) value1).raw) >= 0);
		}
		else if (value0 instanceof FloatValue) {
			if (value1 instanceof IntValue) {
				return new BoolElement(interpreter, ((FloatValue) value0).raw.compareTo(((IntValue) value1).floatValue(true)) >= 0);
			}
			else if (value1 instanceof FloatValue) {
				return new BoolElement(interpreter, ((FloatValue) value0).raw.compareTo(((FloatValue) value1).raw) >= 0);
			}
		}
		else if (value0 instanceof CharValue && value1 instanceof CharValue) {
			return new BoolElement(interpreter, ((CharValue) value0).raw.compareTo(((CharValue) value1).raw) >= 0);
		}
		else if (value0 instanceof StringValue && value1 instanceof StringValue) {
			return new BoolElement(interpreter, ((StringValue) value0).raw.compareTo(((StringValue) value1).raw) >= 0);
		}
		return null;
	}
	
	public static Element onPlus(Interpreter interpreter, @NonNull PrimitiveValue<?> value0, @NonNull PrimitiveValue<?> value1) {
		if (value0 instanceof IntValue) {
			if (value1 instanceof IntValue) {
				return new IntElement(interpreter, ((IntValue) value0).raw.add(((IntValue) value1).raw));
			}
			else if (value1 instanceof FloatValue) {
				return new FloatElement(interpreter, ((IntValue) value0).floatValue(true) + ((FloatValue) value1).raw);
			}
		}
		else if (value0 instanceof FloatValue) {
			if (value1 instanceof IntValue) {
				return new FloatElement(interpreter, ((FloatValue) value0).raw + ((IntValue) value1).floatValue(true));
			}
			else if (value1 instanceof FloatValue) {
				return new FloatElement(interpreter, ((FloatValue) value0).raw + ((FloatValue) value1).raw);
			}
		}
		else if (value0 instanceof StringValue && value1 instanceof StringValue) {
			return new StringElement(interpreter, ((StringValue) value0).raw + ((StringValue) value1).raw);
		}
		return null;
	}
	
	public static Element onAnd(Interpreter interpreter, @NonNull PrimitiveValue<?> value0, @NonNull PrimitiveValue<?> value1) {
		if (value0 instanceof IntValue && value1 instanceof IntValue) {
			return new IntElement(interpreter, ((IntValue) value0).raw.and(((IntValue) value1).raw));
		}
		else if (value0 instanceof BoolValue && value1 instanceof BoolValue) {
			return new BoolElement(interpreter, ((BoolValue) value0).raw && ((BoolValue) value1).raw);
		}
		return null;
	}
	
	public static Element onOr(Interpreter interpreter, @NonNull PrimitiveValue<?> value0, @NonNull PrimitiveValue<?> value1) {
		if (value0 instanceof IntValue && value1 instanceof IntValue) {
			return new IntElement(interpreter, ((IntValue) value0).raw.or(((IntValue) value1).raw));
		}
		else if (value0 instanceof BoolValue && value1 instanceof BoolValue) {
			return new BoolElement(interpreter, ((BoolValue) value0).raw || ((BoolValue) value1).raw);
		}
		return null;
	}
	
	public static Element onXor(Interpreter interpreter, @NonNull PrimitiveValue<?> value0, @NonNull PrimitiveValue<?> value1) {
		if (value0 instanceof IntValue && value1 instanceof IntValue) {
			return new IntElement(interpreter, ((IntValue) value0).raw.xor(((IntValue) value1).raw));
		}
		else if (value0 instanceof BoolValue && value1 instanceof BoolValue) {
			return new BoolElement(interpreter, ((BoolValue) value0).raw ^ ((BoolValue) value1).raw);
		}
		return null;
	}
	
	public static Element onMinus(Interpreter interpreter, @NonNull PrimitiveValue<?> value0, @NonNull PrimitiveValue<?> value1) {
		if (value0 instanceof IntValue) {
			if (value1 instanceof IntValue) {
				return new IntElement(interpreter, ((IntValue) value0).raw.subtract(((IntValue) value1).raw));
			}
			else if (value1 instanceof FloatValue) {
				return new FloatElement(interpreter, ((IntValue) value0).floatValue(true) - ((FloatValue) value1).raw);
			}
		}
		else if (value0 instanceof FloatValue) {
			if (value1 instanceof IntValue) {
				return new FloatElement(interpreter, ((FloatValue) value0).raw - ((IntValue) value1).floatValue(true));
			}
			else if (value1 instanceof FloatValue) {
				return new FloatElement(interpreter, ((FloatValue) value0).raw - ((FloatValue) value1).raw);
			}
		}
		return null;
	}
	
	public static Element onConcat(Interpreter interpreter, @NonNull PrimitiveValue<?> value0, @NonNull PrimitiveValue<?> value1) {
		return new StringElement(interpreter, value0.stringValue(true) + value1.stringValue(true));
	}
	
	public static Element onLeftShift(Interpreter interpreter, @NonNull PrimitiveValue<?> value0, @NonNull PrimitiveValue<?> value1) {
		if (value0 instanceof IntValue && value1 instanceof IntValue) {
			return new IntElement(interpreter, ((IntValue) value0).raw.shiftLeft(((IntValue) value1).raw.intValue()));
		}
		return null;
	}
	
	public static Element onRightShift(Interpreter interpreter, @NonNull PrimitiveValue<?> value0, @NonNull PrimitiveValue<?> value1) {
		if (value0 instanceof IntValue && value1 instanceof IntValue) {
			return new IntElement(interpreter, ((IntValue) value0).raw.shiftRight(((IntValue) value1).raw.intValue()));
		}
		return null;
	}
	
	public static Element onMultiply(Interpreter interpreter, @NonNull PrimitiveValue<?> value0, @NonNull PrimitiveValue<?> value1) {
		if (value0 instanceof IntValue) {
			if (value1 instanceof IntValue) {
				return new IntElement(interpreter, ((IntValue) value0).raw.multiply(((IntValue) value1).raw));
			}
			else if (value1 instanceof FloatValue) {
				return new FloatElement(interpreter, ((IntValue) value0).floatValue(true) * ((FloatValue) value1).raw);
			}
		}
		else if (value0 instanceof FloatValue) {
			if (value1 instanceof IntValue) {
				return new FloatElement(interpreter, ((FloatValue) value0).raw * ((IntValue) value1).floatValue(true));
			}
			else if (value1 instanceof FloatValue) {
				return new FloatElement(interpreter, ((FloatValue) value0).raw * ((FloatValue) value1).raw);
			}
		}
		return null;
	}
	
	public static Element onDivide(Interpreter interpreter, @NonNull PrimitiveValue<?> value0, @NonNull PrimitiveValue<?> value1) {
		if (value0 instanceof IntValue) {
			if (value1 instanceof IntValue) {
				return new IntElement(interpreter, ((IntValue) value0).raw.divide(((IntValue) value1).raw));
			}
			else if (value1 instanceof FloatValue) {
				return new FloatElement(interpreter, ((IntValue) value0).floatValue(true) / ((FloatValue) value1).raw);
			}
		}
		else if (value0 instanceof FloatValue) {
			if (value1 instanceof IntValue) {
				return new FloatElement(interpreter, ((FloatValue) value0).raw / ((IntValue) value1).floatValue(true));
			}
			else if (value1 instanceof FloatValue) {
				return new FloatElement(interpreter, ((FloatValue) value0).raw / ((FloatValue) value1).raw);
			}
		}
		return null;
	}
	
	public static Element onRemainder(Interpreter interpreter, @NonNull PrimitiveValue<?> value0, @NonNull PrimitiveValue<?> value1) {
		if (value0 instanceof IntValue) {
			if (value1 instanceof IntValue) {
				return new IntElement(interpreter, ((IntValue) value0).raw.remainder(((IntValue) value1).raw));
			}
			else if (value1 instanceof FloatValue) {
				return new FloatElement(interpreter, ((IntValue) value0).floatValue(true) % ((FloatValue) value1).raw);
			}
		}
		else if (value0 instanceof FloatValue) {
			if (value1 instanceof IntValue) {
				return new FloatElement(interpreter, ((FloatValue) value0).raw % ((IntValue) value1).floatValue(true));
			}
			else if (value1 instanceof FloatValue) {
				return new FloatElement(interpreter, ((FloatValue) value0).raw % ((FloatValue) value1).raw);
			}
		}
		return null;
	}
	
	public static Element onPower(Interpreter interpreter, @NonNull PrimitiveValue<?> value0, @NonNull PrimitiveValue<?> value1) {
		if (value0 instanceof IntValue) {
			if (value1 instanceof IntValue) {
				return new IntElement(interpreter, ((IntValue) value0).raw.pow(((IntValue) value1).raw.intValueExact()));
			}
			else if (value1 instanceof FloatValue) {
				return new FloatElement(interpreter, Math.pow(((IntValue) value0).floatValue(true), ((FloatValue) value1).raw));
			}
		}
		else if (value0 instanceof FloatValue) {
			if (value1 instanceof IntValue) {
				return new FloatElement(interpreter, Math.pow(((FloatValue) value0).raw, ((IntValue) value1).floatValue(true)));
			}
			else if (value1 instanceof FloatValue) {
				return new FloatElement(interpreter, Math.pow(((FloatValue) value0).raw, ((FloatValue) value1).raw));
			}
		}
		return null;
	}
	
	public static Element onIdivide(Interpreter interpreter, @NonNull PrimitiveValue<?> value0, @NonNull PrimitiveValue<?> value1) {
		if (value0 instanceof IntValue) {
			if (value1 instanceof IntValue) {
				return new IntElement(interpreter, ((IntValue) value0).raw.divide(((IntValue) value1).raw));
			}
			else if (value1 instanceof FloatValue) {
				return new IntElement(interpreter, Helpers.bigIntFromDouble(((IntValue) value0).floatValue(true) / ((FloatValue) value1).raw));
			}
		}
		else if (value0 instanceof FloatValue) {
			if (value1 instanceof IntValue) {
				return new IntElement(interpreter, Helpers.bigIntFromDouble(((FloatValue) value0).raw / ((IntValue) value1).floatValue(true)));
			}
			else if (value1 instanceof FloatValue) {
				return new IntElement(interpreter, Helpers.bigIntFromDouble(((FloatValue) value0).raw / ((FloatValue) value1).raw));
			}
		}
		return null;
	}
	
	public static Element onModulo(Interpreter interpreter, @NonNull PrimitiveValue<?> value0, @NonNull PrimitiveValue<?> value1) {
		if (value0 instanceof IntValue) {
			if (value1 instanceof IntValue) {
				return new IntElement(interpreter, Helpers.mod(((IntValue) value0).raw, ((IntValue) value1).raw));
			}
			else if (value1 instanceof FloatValue) {
				return new FloatElement(interpreter, Helpers.mod(((IntValue) value0).floatValue(true), ((FloatValue) value1).raw));
			}
		}
		else if (value0 instanceof FloatValue) {
			if (value1 instanceof IntValue) {
				return new FloatElement(interpreter, Helpers.mod(((FloatValue) value0).raw, ((IntValue) value1).floatValue(true)));
			}
			else if (value1 instanceof FloatValue) {
				return new FloatElement(interpreter, Helpers.mod(((FloatValue) value0).raw, ((FloatValue) value1).raw));
			}
		}
		return null;
	}
}
