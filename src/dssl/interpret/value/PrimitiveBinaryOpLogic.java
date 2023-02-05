package dssl.interpret.value;

import org.eclipse.jdt.annotation.NonNull;

import dssl.Helpers;
import dssl.interpret.element.Element;
import dssl.interpret.element.primitive.*;

public class PrimitiveBinaryOpLogic {
	
	public static Element onEqualTo(@NonNull PrimitiveValue<?> value0, @NonNull PrimitiveValue<?> value1) {
		return new BoolElement(value0.equals(value1));
	}
	
	public static Element onNotEqualTo(@NonNull PrimitiveValue<?> value0, @NonNull PrimitiveValue<?> value1) {
		return new BoolElement(!value0.equals(value1));
	}
	
	public static Element onLessThan(@NonNull PrimitiveValue<?> value0, @NonNull PrimitiveValue<?> value1) {
		if (value0 instanceof IntValue) {
			if (value1 instanceof IntValue) {
				return new BoolElement(((IntValue) value0).raw.compareTo(((IntValue) value1).raw) < 0);
			}
			else if (value1 instanceof FloatValue) {
				return new BoolElement(((IntValue) value0).floatValue(true).compareTo(((FloatValue) value1).raw) < 0);
			}
		}
		else if (value0 instanceof BoolValue && value1 instanceof BoolValue) {
			return new BoolElement(((BoolValue) value0).raw.compareTo(((BoolValue) value1).raw) < 0);
		}
		else if (value0 instanceof FloatValue) {
			if (value1 instanceof IntValue) {
				return new BoolElement(((FloatValue) value0).raw.compareTo(((IntValue) value1).floatValue(true)) < 0);
			}
			else if (value1 instanceof FloatValue) {
				return new BoolElement(((FloatValue) value0).raw.compareTo(((FloatValue) value1).raw) < 0);
			}
		}
		else if (value0 instanceof CharValue && value1 instanceof CharValue) {
			return new BoolElement(((CharValue) value0).raw.compareTo(((CharValue) value1).raw) < 0);
		}
		else if (value0 instanceof StringValue && value1 instanceof StringValue) {
			return new BoolElement(((StringValue) value0).raw.compareTo(((StringValue) value1).raw) < 0);
		}
		return null;
	}
	
	public static Element onLessOrEqual(@NonNull PrimitiveValue<?> value0, @NonNull PrimitiveValue<?> value1) {
		if (value0 instanceof IntValue) {
			if (value1 instanceof IntValue) {
				return new BoolElement(((IntValue) value0).raw.compareTo(((IntValue) value1).raw) <= 0);
			}
			else if (value1 instanceof FloatValue) {
				return new BoolElement(((IntValue) value0).floatValue(true).compareTo(((FloatValue) value1).raw) <= 0);
			}
		}
		else if (value0 instanceof BoolValue && value1 instanceof BoolValue) {
			return new BoolElement(((BoolValue) value0).raw.compareTo(((BoolValue) value1).raw) <= 0);
		}
		else if (value0 instanceof FloatValue) {
			if (value1 instanceof IntValue) {
				return new BoolElement(((FloatValue) value0).raw.compareTo(((IntValue) value1).floatValue(true)) <= 0);
			}
			else if (value1 instanceof FloatValue) {
				return new BoolElement(((FloatValue) value0).raw.compareTo(((FloatValue) value1).raw) <= 0);
			}
		}
		else if (value0 instanceof CharValue && value1 instanceof CharValue) {
			return new BoolElement(((CharValue) value0).raw.compareTo(((CharValue) value1).raw) <= 0);
		}
		else if (value0 instanceof StringValue && value1 instanceof StringValue) {
			return new BoolElement(((StringValue) value0).raw.compareTo(((StringValue) value1).raw) <= 0);
		}
		return null;
	}
	
	public static Element onMoreThan(@NonNull PrimitiveValue<?> value0, @NonNull PrimitiveValue<?> value1) {
		if (value0 instanceof IntValue) {
			if (value1 instanceof IntValue) {
				return new BoolElement(((IntValue) value0).raw.compareTo(((IntValue) value1).raw) > 0);
			}
			else if (value1 instanceof FloatValue) {
				return new BoolElement(((IntValue) value0).floatValue(true).compareTo(((FloatValue) value1).raw) > 0);
			}
		}
		else if (value0 instanceof BoolValue && value1 instanceof BoolValue) {
			return new BoolElement(((BoolValue) value0).raw.compareTo(((BoolValue) value1).raw) > 0);
		}
		else if (value0 instanceof FloatValue) {
			if (value1 instanceof IntValue) {
				return new BoolElement(((FloatValue) value0).raw.compareTo(((IntValue) value1).floatValue(true)) > 0);
			}
			else if (value1 instanceof FloatValue) {
				return new BoolElement(((FloatValue) value0).raw.compareTo(((FloatValue) value1).raw) > 0);
			}
		}
		else if (value0 instanceof CharValue && value1 instanceof CharValue) {
			return new BoolElement(((CharValue) value0).raw.compareTo(((CharValue) value1).raw) > 0);
		}
		else if (value0 instanceof StringValue && value1 instanceof StringValue) {
			return new BoolElement(((StringValue) value0).raw.compareTo(((StringValue) value1).raw) > 0);
		}
		return null;
	}
	
	public static Element onMoreOrEqual(@NonNull PrimitiveValue<?> value0, @NonNull PrimitiveValue<?> value1) {
		if (value0 instanceof IntValue) {
			if (value1 instanceof IntValue) {
				return new BoolElement(((IntValue) value0).raw.compareTo(((IntValue) value1).raw) >= 0);
			}
			else if (value1 instanceof FloatValue) {
				return new BoolElement(((IntValue) value0).floatValue(true).compareTo(((FloatValue) value1).raw) >= 0);
			}
		}
		else if (value0 instanceof BoolValue && value1 instanceof BoolValue) {
			return new BoolElement(((BoolValue) value0).raw.compareTo(((BoolValue) value1).raw) >= 0);
		}
		else if (value0 instanceof FloatValue) {
			if (value1 instanceof IntValue) {
				return new BoolElement(((FloatValue) value0).raw.compareTo(((IntValue) value1).floatValue(true)) >= 0);
			}
			else if (value1 instanceof FloatValue) {
				return new BoolElement(((FloatValue) value0).raw.compareTo(((FloatValue) value1).raw) >= 0);
			}
		}
		else if (value0 instanceof CharValue && value1 instanceof CharValue) {
			return new BoolElement(((CharValue) value0).raw.compareTo(((CharValue) value1).raw) >= 0);
		}
		else if (value0 instanceof StringValue && value1 instanceof StringValue) {
			return new BoolElement(((StringValue) value0).raw.compareTo(((StringValue) value1).raw) >= 0);
		}
		return null;
	}
	
	@SuppressWarnings("null")
	public static Element onPlus(@NonNull PrimitiveValue<?> value0, @NonNull PrimitiveValue<?> value1) {
		if (value0 instanceof IntValue) {
			if (value1 instanceof IntValue) {
				return new IntElement(((IntValue) value0).raw.add(((IntValue) value1).raw));
			}
			else if (value1 instanceof FloatValue) {
				return new FloatElement(((IntValue) value0).floatValue(true) + ((FloatValue) value1).raw);
			}
		}
		else if (value0 instanceof FloatValue) {
			if (value1 instanceof IntValue) {
				return new FloatElement(((FloatValue) value0).raw + ((IntValue) value1).floatValue(true));
			}
			else if (value1 instanceof FloatValue) {
				return new FloatElement(((FloatValue) value0).raw + ((FloatValue) value1).raw);
			}
		}
		else if (value0 instanceof StringValue && value1 instanceof StringValue) {
			return new StringElement(((StringValue) value0).raw + ((StringValue) value1).raw);
		}
		return null;
	}
	
	@SuppressWarnings("null")
	public static Element onAnd(@NonNull PrimitiveValue<?> value0, @NonNull PrimitiveValue<?> value1) {
		if (value0 instanceof IntValue && value1 instanceof IntValue) {
			return new IntElement(((IntValue) value0).raw.and(((IntValue) value1).raw));
		}
		else if (value0 instanceof BoolValue && value1 instanceof BoolValue) {
			return new BoolElement(((BoolValue) value0).raw && ((BoolValue) value1).raw);
		}
		return null;
	}
	
	@SuppressWarnings("null")
	public static Element onOr(@NonNull PrimitiveValue<?> value0, @NonNull PrimitiveValue<?> value1) {
		if (value0 instanceof IntValue && value1 instanceof IntValue) {
			return new IntElement(((IntValue) value0).raw.or(((IntValue) value1).raw));
		}
		else if (value0 instanceof BoolValue && value1 instanceof BoolValue) {
			return new BoolElement(((BoolValue) value0).raw || ((BoolValue) value1).raw);
		}
		return null;
	}
	
	@SuppressWarnings("null")
	public static Element onXor(@NonNull PrimitiveValue<?> value0, @NonNull PrimitiveValue<?> value1) {
		if (value0 instanceof IntValue && value1 instanceof IntValue) {
			return new IntElement(((IntValue) value0).raw.xor(((IntValue) value1).raw));
		}
		else if (value0 instanceof BoolValue && value1 instanceof BoolValue) {
			return new BoolElement(((BoolValue) value0).raw ^ ((BoolValue) value1).raw);
		}
		return null;
	}
	
	@SuppressWarnings("null")
	public static Element onMinus(@NonNull PrimitiveValue<?> value0, @NonNull PrimitiveValue<?> value1) {
		if (value0 instanceof IntValue) {
			if (value1 instanceof IntValue) {
				return new IntElement(((IntValue) value0).raw.subtract(((IntValue) value1).raw));
			}
			else if (value1 instanceof FloatValue) {
				return new FloatElement(((IntValue) value0).floatValue(true) - ((FloatValue) value1).raw);
			}
		}
		else if (value0 instanceof FloatValue) {
			if (value1 instanceof IntValue) {
				return new FloatElement(((FloatValue) value0).raw - ((IntValue) value1).floatValue(true));
			}
			else if (value1 instanceof FloatValue) {
				return new FloatElement(((FloatValue) value0).raw - ((FloatValue) value1).raw);
			}
		}
		return null;
	}
	
	public static Element onConcat(@NonNull PrimitiveValue<?> value0, @NonNull PrimitiveValue<?> value1) {
		return new StringElement(value0.toString() + value1.toString());
	}
	
	@SuppressWarnings("null")
	public static Element onLeftShift(@NonNull PrimitiveValue<?> value0, @NonNull PrimitiveValue<?> value1) {
		if (value0 instanceof IntValue && value1 instanceof IntValue) {
			return new IntElement(((IntValue) value0).raw.shiftLeft(((IntValue) value1).raw.intValue()));
		}
		return null;
	}
	
	@SuppressWarnings("null")
	public static Element onRightShift(@NonNull PrimitiveValue<?> value0, @NonNull PrimitiveValue<?> value1) {
		if (value0 instanceof IntValue && value1 instanceof IntValue) {
			return new IntElement(((IntValue) value0).raw.shiftRight(((IntValue) value1).raw.intValue()));
		}
		return null;
	}
	
	@SuppressWarnings("null")
	public static Element onMultiply(@NonNull PrimitiveValue<?> value0, @NonNull PrimitiveValue<?> value1) {
		if (value0 instanceof IntValue) {
			if (value1 instanceof IntValue) {
				return new IntElement(((IntValue) value0).raw.multiply(((IntValue) value1).raw));
			}
			else if (value1 instanceof FloatValue) {
				return new FloatElement(((IntValue) value0).floatValue(true) * ((FloatValue) value1).raw);
			}
		}
		else if (value0 instanceof FloatValue) {
			if (value1 instanceof IntValue) {
				return new FloatElement(((FloatValue) value0).raw * ((IntValue) value1).floatValue(true));
			}
			else if (value1 instanceof FloatValue) {
				return new FloatElement(((FloatValue) value0).raw * ((FloatValue) value1).raw);
			}
		}
		return null;
	}
	
	@SuppressWarnings("null")
	public static Element onDivide(@NonNull PrimitiveValue<?> value0, @NonNull PrimitiveValue<?> value1) {
		if (value0 instanceof IntValue) {
			if (value1 instanceof IntValue) {
				return new IntElement(((IntValue) value0).raw.divide(((IntValue) value1).raw));
			}
			else if (value1 instanceof FloatValue) {
				return new FloatElement(((IntValue) value0).floatValue(true) / ((FloatValue) value1).raw);
			}
		}
		else if (value0 instanceof FloatValue) {
			if (value1 instanceof IntValue) {
				return new FloatElement(((FloatValue) value0).raw / ((IntValue) value1).floatValue(true));
			}
			else if (value1 instanceof FloatValue) {
				return new FloatElement(((FloatValue) value0).raw / ((FloatValue) value1).raw);
			}
		}
		return null;
	}
	
	@SuppressWarnings("null")
	public static Element onRemainder(@NonNull PrimitiveValue<?> value0, @NonNull PrimitiveValue<?> value1) {
		if (value0 instanceof IntValue) {
			if (value1 instanceof IntValue) {
				return new IntElement(((IntValue) value0).raw.remainder(((IntValue) value1).raw));
			}
			else if (value1 instanceof FloatValue) {
				return new FloatElement(((IntValue) value0).floatValue(true) % ((FloatValue) value1).raw);
			}
		}
		else if (value0 instanceof FloatValue) {
			if (value1 instanceof IntValue) {
				return new FloatElement(((FloatValue) value0).raw % ((IntValue) value1).floatValue(true));
			}
			else if (value1 instanceof FloatValue) {
				return new FloatElement(((FloatValue) value0).raw % ((FloatValue) value1).raw);
			}
		}
		return null;
	}
	
	@SuppressWarnings("null")
	public static Element onPower(@NonNull PrimitiveValue<?> value0, @NonNull PrimitiveValue<?> value1) {
		if (value0 instanceof IntValue) {
			if (value1 instanceof IntValue) {
				return new IntElement(((IntValue) value0).raw.pow(((IntValue) value1).raw.intValueExact()));
			}
			else if (value1 instanceof FloatValue) {
				return new FloatElement(Math.pow(((IntValue) value0).floatValue(true), ((FloatValue) value1).raw));
			}
		}
		else if (value0 instanceof FloatValue) {
			if (value1 instanceof IntValue) {
				return new FloatElement(Math.pow(((FloatValue) value0).raw, ((IntValue) value1).floatValue(true)));
			}
			else if (value1 instanceof FloatValue) {
				return new FloatElement(Math.pow(((FloatValue) value0).raw, ((FloatValue) value1).raw));
			}
		}
		return null;
	}
	
	@SuppressWarnings("null")
	public static Element onIdivide(@NonNull PrimitiveValue<?> value0, @NonNull PrimitiveValue<?> value1) {
		if (value0 instanceof IntValue) {
			if (value1 instanceof IntValue) {
				return new IntElement(((IntValue) value0).raw.divide(((IntValue) value1).raw));
			}
			else if (value1 instanceof FloatValue) {
				return new IntElement(Helpers.bigIntFromDouble(((IntValue) value0).floatValue(true) / ((FloatValue) value1).raw));
			}
		}
		else if (value0 instanceof FloatValue) {
			if (value1 instanceof IntValue) {
				return new IntElement(Helpers.bigIntFromDouble(((FloatValue) value0).raw / ((IntValue) value1).floatValue(true)));
			}
			else if (value1 instanceof FloatValue) {
				return new IntElement(Helpers.bigIntFromDouble(((FloatValue) value0).raw / ((FloatValue) value1).raw));
			}
		}
		return null;
	}
	
	@SuppressWarnings("null")
	public static Element onModulo(@NonNull PrimitiveValue<?> value0, @NonNull PrimitiveValue<?> value1) {
		if (value0 instanceof IntValue) {
			if (value1 instanceof IntValue) {
				return new IntElement(Helpers.mod(((IntValue) value0).raw, ((IntValue) value1).raw));
			}
			else if (value1 instanceof FloatValue) {
				return new FloatElement(Helpers.mod(((IntValue) value0).floatValue(true), ((FloatValue) value1).raw));
			}
		}
		else if (value0 instanceof FloatValue) {
			if (value1 instanceof IntValue) {
				return new FloatElement(Helpers.mod(((FloatValue) value0).raw, ((IntValue) value1).floatValue(true)));
			}
			else if (value1 instanceof FloatValue) {
				return new FloatElement(Helpers.mod(((FloatValue) value0).raw, ((FloatValue) value1).raw));
			}
		}
		return null;
	}
}
