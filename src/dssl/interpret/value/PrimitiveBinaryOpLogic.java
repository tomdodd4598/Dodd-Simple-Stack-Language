package dssl.interpret.value;

import org.eclipse.jdt.annotation.NonNull;

import dssl.Helpers;
import dssl.interpret.element.Element;
import dssl.interpret.element.value.primitive.*;

public class PrimitiveBinaryOpLogic {
	
	public static Element onEqualTo(@NonNull PrimitiveValue<?> elem0, @NonNull PrimitiveValue<?> elem1) {
		return new BoolElement(elem0.equals(elem1));
	}
	
	public static Element onNotEqualTo(@NonNull PrimitiveValue<?> elem0, @NonNull PrimitiveValue<?> elem1) {
		return new BoolElement(!elem0.equals(elem1));
	}
	
	public static Element onLessThan(@NonNull PrimitiveValue<?> elem0, @NonNull PrimitiveValue<?> elem1) {
		if (elem0 instanceof IntValue) {
			if (elem1 instanceof IntValue) {
				return new BoolElement(((IntValue) elem0).raw.compareTo(((IntValue) elem1).raw) < 0);
			}
			else if (elem1 instanceof FloatValue) {
				return new BoolElement(((IntValue) elem0).floatValue(true).compareTo(((FloatValue) elem1).raw) < 0);
			}
		}
		else if (elem0 instanceof BoolValue && elem1 instanceof BoolValue) {
			return new BoolElement(((BoolValue) elem0).raw.compareTo(((BoolValue) elem1).raw) < 0);
		}
		else if (elem0 instanceof FloatValue) {
			if (elem1 instanceof IntValue) {
				return new BoolElement(((FloatValue) elem0).raw.compareTo(((IntValue) elem1).floatValue(true)) < 0);
			}
			else if (elem1 instanceof FloatValue) {
				return new BoolElement(((FloatValue) elem0).raw.compareTo(((FloatValue) elem1).raw) < 0);
			}
		}
		else if (elem0 instanceof CharValue && elem1 instanceof CharValue) {
			return new BoolElement(((CharValue) elem0).raw.compareTo(((CharValue) elem1).raw) < 0);
		}
		else if (elem0 instanceof StringValue && elem1 instanceof StringValue) {
			return new BoolElement(((StringValue) elem0).raw.compareTo(((StringValue) elem1).raw) < 0);
		}
		return null;
	}
	
	public static Element onLessOrEqual(@NonNull PrimitiveValue<?> elem0, @NonNull PrimitiveValue<?> elem1) {
		if (elem0 instanceof IntValue) {
			if (elem1 instanceof IntValue) {
				return new BoolElement(((IntValue) elem0).raw.compareTo(((IntValue) elem1).raw) <= 0);
			}
			else if (elem1 instanceof FloatValue) {
				return new BoolElement(((IntValue) elem0).floatValue(true).compareTo(((FloatValue) elem1).raw) <= 0);
			}
		}
		else if (elem0 instanceof BoolValue && elem1 instanceof BoolValue) {
			return new BoolElement(((BoolValue) elem0).raw.compareTo(((BoolValue) elem1).raw) <= 0);
		}
		else if (elem0 instanceof FloatValue) {
			if (elem1 instanceof IntValue) {
				return new BoolElement(((FloatValue) elem0).raw.compareTo(((IntValue) elem1).floatValue(true)) <= 0);
			}
			else if (elem1 instanceof FloatValue) {
				return new BoolElement(((FloatValue) elem0).raw.compareTo(((FloatValue) elem1).raw) <= 0);
			}
		}
		else if (elem0 instanceof CharValue && elem1 instanceof CharValue) {
			return new BoolElement(((CharValue) elem0).raw.compareTo(((CharValue) elem1).raw) <= 0);
		}
		else if (elem0 instanceof StringValue && elem1 instanceof StringValue) {
			return new BoolElement(((StringValue) elem0).raw.compareTo(((StringValue) elem1).raw) <= 0);
		}
		return null;
	}
	
	public static Element onMoreThan(@NonNull PrimitiveValue<?> elem0, @NonNull PrimitiveValue<?> elem1) {
		if (elem0 instanceof IntValue) {
			if (elem1 instanceof IntValue) {
				return new BoolElement(((IntValue) elem0).raw.compareTo(((IntValue) elem1).raw) > 0);
			}
			else if (elem1 instanceof FloatValue) {
				return new BoolElement(((IntValue) elem0).floatValue(true).compareTo(((FloatValue) elem1).raw) > 0);
			}
		}
		else if (elem0 instanceof BoolValue && elem1 instanceof BoolValue) {
			return new BoolElement(((BoolValue) elem0).raw.compareTo(((BoolValue) elem1).raw) > 0);
		}
		else if (elem0 instanceof FloatValue) {
			if (elem1 instanceof IntValue) {
				return new BoolElement(((FloatValue) elem0).raw.compareTo(((IntValue) elem1).floatValue(true)) > 0);
			}
			else if (elem1 instanceof FloatValue) {
				return new BoolElement(((FloatValue) elem0).raw.compareTo(((FloatValue) elem1).raw) > 0);
			}
		}
		else if (elem0 instanceof CharValue && elem1 instanceof CharValue) {
			return new BoolElement(((CharValue) elem0).raw.compareTo(((CharValue) elem1).raw) > 0);
		}
		else if (elem0 instanceof StringValue && elem1 instanceof StringValue) {
			return new BoolElement(((StringValue) elem0).raw.compareTo(((StringValue) elem1).raw) > 0);
		}
		return null;
	}
	
	public static Element onMoreOrEqual(@NonNull PrimitiveValue<?> elem0, @NonNull PrimitiveValue<?> elem1) {
		if (elem0 instanceof IntValue) {
			if (elem1 instanceof IntValue) {
				return new BoolElement(((IntValue) elem0).raw.compareTo(((IntValue) elem1).raw) >= 0);
			}
			else if (elem1 instanceof FloatValue) {
				return new BoolElement(((IntValue) elem0).floatValue(true).compareTo(((FloatValue) elem1).raw) >= 0);
			}
		}
		else if (elem0 instanceof BoolValue && elem1 instanceof BoolValue) {
			return new BoolElement(((BoolValue) elem0).raw.compareTo(((BoolValue) elem1).raw) >= 0);
		}
		else if (elem0 instanceof FloatValue) {
			if (elem1 instanceof IntValue) {
				return new BoolElement(((FloatValue) elem0).raw.compareTo(((IntValue) elem1).floatValue(true)) >= 0);
			}
			else if (elem1 instanceof FloatValue) {
				return new BoolElement(((FloatValue) elem0).raw.compareTo(((FloatValue) elem1).raw) >= 0);
			}
		}
		else if (elem0 instanceof CharValue && elem1 instanceof CharValue) {
			return new BoolElement(((CharValue) elem0).raw.compareTo(((CharValue) elem1).raw) >= 0);
		}
		else if (elem0 instanceof StringValue && elem1 instanceof StringValue) {
			return new BoolElement(((StringValue) elem0).raw.compareTo(((StringValue) elem1).raw) >= 0);
		}
		return null;
	}
	
	@SuppressWarnings("null")
	public static Element onPlus(@NonNull PrimitiveValue<?> elem0, @NonNull PrimitiveValue<?> elem1) {
		if (elem0 instanceof IntValue) {
			if (elem1 instanceof IntValue) {
				return new IntElement(((IntValue) elem0).raw.add(((IntValue) elem1).raw));
			}
			else if (elem1 instanceof FloatValue) {
				return new FloatElement(((IntValue) elem0).floatValue(true) + ((FloatValue) elem1).raw);
			}
		}
		else if (elem0 instanceof FloatValue) {
			if (elem1 instanceof IntValue) {
				return new FloatElement(((FloatValue) elem0).raw + ((IntValue) elem1).floatValue(true));
			}
			else if (elem1 instanceof FloatValue) {
				return new FloatElement(((FloatValue) elem0).raw + ((FloatValue) elem1).raw);
			}
		}
		else if (elem0 instanceof StringValue && elem1 instanceof StringValue) {
			return new StringElement(((StringValue) elem0).raw + ((StringValue) elem1).raw);
		}
		return null;
	}
	
	@SuppressWarnings("null")
	public static Element onAnd(@NonNull PrimitiveValue<?> elem0, @NonNull PrimitiveValue<?> elem1) {
		if (elem0 instanceof IntValue && elem1 instanceof IntValue) {
			return new IntElement(((IntValue) elem0).raw.and(((IntValue) elem1).raw));
		}
		else if (elem0 instanceof BoolValue && elem1 instanceof BoolValue) {
			return new BoolElement(((BoolValue) elem0).raw && ((BoolValue) elem1).raw);
		}
		return null;
	}
	
	@SuppressWarnings("null")
	public static Element onOr(@NonNull PrimitiveValue<?> elem0, @NonNull PrimitiveValue<?> elem1) {
		if (elem0 instanceof IntValue && elem1 instanceof IntValue) {
			return new IntElement(((IntValue) elem0).raw.or(((IntValue) elem1).raw));
		}
		else if (elem0 instanceof BoolValue && elem1 instanceof BoolValue) {
			return new BoolElement(((BoolValue) elem0).raw || ((BoolValue) elem1).raw);
		}
		return null;
	}
	
	@SuppressWarnings("null")
	public static Element onXor(@NonNull PrimitiveValue<?> elem0, @NonNull PrimitiveValue<?> elem1) {
		if (elem0 instanceof IntValue && elem1 instanceof IntValue) {
			return new IntElement(((IntValue) elem0).raw.xor(((IntValue) elem1).raw));
		}
		else if (elem0 instanceof BoolValue && elem1 instanceof BoolValue) {
			return new BoolElement(((BoolValue) elem0).raw ^ ((BoolValue) elem1).raw);
		}
		return null;
	}
	
	@SuppressWarnings("null")
	public static Element onMinus(@NonNull PrimitiveValue<?> elem0, @NonNull PrimitiveValue<?> elem1) {
		if (elem0 instanceof IntValue) {
			if (elem1 instanceof IntValue) {
				return new IntElement(((IntValue) elem0).raw.subtract(((IntValue) elem1).raw));
			}
			else if (elem1 instanceof FloatValue) {
				return new FloatElement(((IntValue) elem0).floatValue(true) - ((FloatValue) elem1).raw);
			}
		}
		else if (elem0 instanceof FloatValue) {
			if (elem1 instanceof IntValue) {
				return new FloatElement(((FloatValue) elem0).raw - ((IntValue) elem1).floatValue(true));
			}
			else if (elem1 instanceof FloatValue) {
				return new FloatElement(((FloatValue) elem0).raw - ((FloatValue) elem1).raw);
			}
		}
		return null;
	}
	
	public static Element onConcat(@NonNull PrimitiveValue<?> elem0, @NonNull PrimitiveValue<?> elem1) {
		return new StringElement(elem0.toString() + elem1.toString());
	}
	
	@SuppressWarnings("null")
	public static Element onArithmeticLeftShift(@NonNull PrimitiveValue<?> elem0, @NonNull PrimitiveValue<?> elem1) {
		if (elem0 instanceof IntValue && elem1 instanceof IntValue) {
			return new IntElement(((IntValue) elem0).raw.shiftLeft(((IntValue) elem1).raw.intValue()));
		}
		return null;
	}
	
	@SuppressWarnings("null")
	public static Element onArithmeticRightShift(@NonNull PrimitiveValue<?> elem0, @NonNull PrimitiveValue<?> elem1) {
		if (elem0 instanceof IntValue && elem1 instanceof IntValue) {
			return new IntElement(((IntValue) elem0).raw.shiftRight(((IntValue) elem1).raw.intValue()));
		}
		return null;
	}
	
	@SuppressWarnings("null")
	public static Element onMultiply(@NonNull PrimitiveValue<?> elem0, @NonNull PrimitiveValue<?> elem1) {
		if (elem0 instanceof IntValue) {
			if (elem1 instanceof IntValue) {
				return new IntElement(((IntValue) elem0).raw.multiply(((IntValue) elem1).raw));
			}
			else if (elem1 instanceof FloatValue) {
				return new FloatElement(((IntValue) elem0).floatValue(true) * ((FloatValue) elem1).raw);
			}
		}
		else if (elem0 instanceof FloatValue) {
			if (elem1 instanceof IntValue) {
				return new FloatElement(((FloatValue) elem0).raw * ((IntValue) elem1).floatValue(true));
			}
			else if (elem1 instanceof FloatValue) {
				return new FloatElement(((FloatValue) elem0).raw * ((FloatValue) elem1).raw);
			}
		}
		return null;
	}
	
	@SuppressWarnings("null")
	public static Element onDivide(@NonNull PrimitiveValue<?> elem0, @NonNull PrimitiveValue<?> elem1) {
		if (elem0 instanceof IntValue) {
			if (elem1 instanceof IntValue) {
				return new IntElement(((IntValue) elem0).raw.divide(((IntValue) elem1).raw));
			}
			else if (elem1 instanceof FloatValue) {
				return new FloatElement(((IntValue) elem0).floatValue(true) / ((FloatValue) elem1).raw);
			}
		}
		else if (elem0 instanceof FloatValue) {
			if (elem1 instanceof IntValue) {
				return new FloatElement(((FloatValue) elem0).raw / ((IntValue) elem1).floatValue(true));
			}
			else if (elem1 instanceof FloatValue) {
				return new FloatElement(((FloatValue) elem0).raw / ((FloatValue) elem1).raw);
			}
		}
		return null;
	}
	
	@SuppressWarnings("null")
	public static Element onRemainder(@NonNull PrimitiveValue<?> elem0, @NonNull PrimitiveValue<?> elem1) {
		if (elem0 instanceof IntValue) {
			if (elem1 instanceof IntValue) {
				return new IntElement(((IntValue) elem0).raw.remainder(((IntValue) elem1).raw));
			}
			else if (elem1 instanceof FloatValue) {
				return new FloatElement(((IntValue) elem0).floatValue(true) % ((FloatValue) elem1).raw);
			}
		}
		else if (elem0 instanceof FloatValue) {
			if (elem1 instanceof IntValue) {
				return new FloatElement(((FloatValue) elem0).raw % ((IntValue) elem1).floatValue(true));
			}
			else if (elem1 instanceof FloatValue) {
				return new FloatElement(((FloatValue) elem0).raw % ((FloatValue) elem1).raw);
			}
		}
		return null;
	}
	
	@SuppressWarnings("null")
	public static Element onPower(@NonNull PrimitiveValue<?> elem0, @NonNull PrimitiveValue<?> elem1) {
		if (elem0 instanceof IntValue) {
			if (elem1 instanceof IntValue) {
				return new IntElement(((IntValue) elem0).raw.pow(((IntValue) elem1).raw.intValueExact()));
			}
			else if (elem1 instanceof FloatValue) {
				return new FloatElement(Math.pow(((IntValue) elem0).floatValue(true), ((FloatValue) elem1).raw));
			}
		}
		else if (elem0 instanceof FloatValue) {
			if (elem1 instanceof IntValue) {
				return new FloatElement(Math.pow(((FloatValue) elem0).raw, ((IntValue) elem1).floatValue(true)));
			}
			else if (elem1 instanceof FloatValue) {
				return new FloatElement(Math.pow(((FloatValue) elem0).raw, ((FloatValue) elem1).raw));
			}
		}
		return null;
	}
	
	@SuppressWarnings("null")
	public static Element onIdivide(@NonNull PrimitiveValue<?> elem0, @NonNull PrimitiveValue<?> elem1) {
		if (elem0 instanceof IntValue) {
			if (elem1 instanceof IntValue) {
				return new IntElement(((IntValue) elem0).raw.divide(((IntValue) elem1).raw));
			}
			else if (elem1 instanceof FloatValue) {
				return new IntElement(Helpers.bigIntFromDouble(((IntValue) elem0).floatValue(true) / ((FloatValue) elem1).raw));
			}
		}
		else if (elem0 instanceof FloatValue) {
			if (elem1 instanceof IntValue) {
				return new IntElement(Helpers.bigIntFromDouble(((FloatValue) elem0).raw / ((IntValue) elem1).floatValue(true)));
			}
			else if (elem1 instanceof FloatValue) {
				return new IntElement(Helpers.bigIntFromDouble(((FloatValue) elem0).raw / ((FloatValue) elem1).raw));
			}
		}
		return null;
	}
	
	@SuppressWarnings("null")
	public static Element onModulo(@NonNull PrimitiveValue<?> elem0, @NonNull PrimitiveValue<?> elem1) {
		if (elem0 instanceof IntValue) {
			if (elem1 instanceof IntValue) {
				return new IntElement(Helpers.mod(((IntValue) elem0).raw, ((IntValue) elem1).raw));
			}
			else if (elem1 instanceof FloatValue) {
				return new FloatElement(Helpers.mod(((IntValue) elem0).floatValue(true), ((FloatValue) elem1).raw));
			}
		}
		else if (elem0 instanceof FloatValue) {
			if (elem1 instanceof IntValue) {
				return new FloatElement(Helpers.mod(((FloatValue) elem0).raw, ((IntValue) elem1).floatValue(true)));
			}
			else if (elem1 instanceof FloatValue) {
				return new FloatElement(Helpers.mod(((FloatValue) elem0).raw, ((FloatValue) elem1).raw));
			}
		}
		return null;
	}
}
