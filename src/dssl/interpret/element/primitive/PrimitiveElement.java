package dssl.interpret.element.primitive;

import java.math.BigInteger;

import org.eclipse.jdt.annotation.*;

import dssl.interpret.*;
import dssl.interpret.element.Element;
import dssl.interpret.value.*;

public abstract class PrimitiveElement<@NonNull RAW, @NonNull VALUE extends @NonNull PrimitiveValue<@NonNull RAW>> extends Element {
	
	public final @NonNull VALUE value;
	
	protected PrimitiveElement(Interpreter interpreter, @NonNull Clazz clazz, @NonNull VALUE value) {
		super(interpreter, clazz);
		this.value = value;
	}
	
	@Override
	public @Nullable IntElement asInt(TokenExecutor exec) {
		BigInteger intValue = value.intValue(false);
		return intValue == null ? null : new IntElement(interpreter, intValue);
	}
	
	@Override
	public @Nullable BoolElement asBool(TokenExecutor exec) {
		Boolean boolValue = value.boolValue(false);
		return boolValue == null ? null : new BoolElement(interpreter, boolValue);
	}
	
	@Override
	public @Nullable FloatElement asFloat(TokenExecutor exec) {
		Double floatValue = value.floatValue(false);
		return floatValue == null ? null : new FloatElement(interpreter, floatValue);
	}
	
	@Override
	public @Nullable CharElement asChar(TokenExecutor exec) {
		Character charValue = value.charValue(false);
		return charValue == null ? null : new CharElement(interpreter, charValue);
	}
	
	@Override
	public @Nullable StringElement asString(TokenExecutor exec) {
		String stringValue = value.stringValue(false);
		return stringValue == null ? null : new StringElement(interpreter, stringValue);
	}
	
	@Override
	public @NonNull IntElement intCast(TokenExecutor exec) {
		BigInteger intValue = value.intValue(true);
		return intValue == null ? super.intCast(exec) : new IntElement(interpreter, intValue);
	}
	
	@Override
	public @NonNull BoolElement boolCast(TokenExecutor exec) {
		Boolean boolValue = value.boolValue(true);
		return boolValue == null ? super.boolCast(exec) : new BoolElement(interpreter, boolValue);
	}
	
	@Override
	public @NonNull FloatElement floatCast(TokenExecutor exec) {
		Double floatValue = value.floatValue(true);
		return floatValue == null ? super.floatCast(exec) : new FloatElement(interpreter, floatValue);
	}
	
	@Override
	public @NonNull CharElement charCast(TokenExecutor exec) {
		Character charValue = value.charValue(true);
		return charValue == null ? super.charCast(exec) : new CharElement(interpreter, charValue);
	}
	
	@Override
	public @NonNull StringElement stringCast(TokenExecutor exec) {
		String stringValue = value.stringValue(true);
		return stringValue == null ? super.stringCastInternal(exec) : new StringElement(interpreter, stringValue);
	}
	
	@Override
	public @NonNull TokenResult onEqualTo(TokenExecutor exec, @NonNull Element other) {
		if (other instanceof PrimitiveElement<?, ?> primitive) {
			Element elem = PrimitiveBinaryOpLogic.onEqualTo(interpreter, value, primitive.value);
			if (elem != null) {
				exec.push(elem);
				return TokenResult.PASS;
			}
		}
		return super.onEqualToInternal(exec, other);
	}
	
	@Override
	public @NonNull TokenResult onNotEqualTo(TokenExecutor exec, @NonNull Element other) {
		if (other instanceof PrimitiveElement<?, ?> primitive) {
			Element elem = PrimitiveBinaryOpLogic.onNotEqualTo(interpreter, value, primitive.value);
			if (elem != null) {
				exec.push(elem);
				return TokenResult.PASS;
			}
		}
		return super.onNotEqualToInternal(exec, other);
	}
	
	@Override
	public @NonNull TokenResult onLessThan(TokenExecutor exec, @NonNull Element other) {
		if (other instanceof PrimitiveElement<?, ?> primitive) {
			Element elem = PrimitiveBinaryOpLogic.onLessThan(interpreter, value, primitive.value);
			if (elem != null) {
				exec.push(elem);
				return TokenResult.PASS;
			}
		}
		return super.onLessThanInternal(exec, other);
	}
	
	@Override
	public @NonNull TokenResult onLessOrEqual(TokenExecutor exec, @NonNull Element other) {
		if (other instanceof PrimitiveElement<?, ?> primitive) {
			Element elem = PrimitiveBinaryOpLogic.onLessOrEqual(interpreter, value, primitive.value);
			if (elem != null) {
				exec.push(elem);
				return TokenResult.PASS;
			}
		}
		return super.onLessOrEqualInternal(exec, other);
	}
	
	@Override
	public @NonNull TokenResult onMoreThan(TokenExecutor exec, @NonNull Element other) {
		if (other instanceof PrimitiveElement<?, ?> primitive) {
			Element elem = PrimitiveBinaryOpLogic.onMoreThan(interpreter, value, primitive.value);
			if (elem != null) {
				exec.push(elem);
				return TokenResult.PASS;
			}
		}
		return super.onMoreThanInternal(exec, other);
	}
	
	@Override
	public @NonNull TokenResult onMoreOrEqual(TokenExecutor exec, @NonNull Element other) {
		if (other instanceof PrimitiveElement<?, ?> primitive) {
			Element elem = PrimitiveBinaryOpLogic.onMoreOrEqual(interpreter, value, primitive.value);
			if (elem != null) {
				exec.push(elem);
				return TokenResult.PASS;
			}
		}
		return super.onMoreOrEqualInternal(exec, other);
	}
	
	@Override
	public @NonNull TokenResult onPlus(TokenExecutor exec, @NonNull Element other) {
		if (other instanceof PrimitiveElement<?, ?> primitive) {
			Element elem = PrimitiveBinaryOpLogic.onPlus(interpreter, value, primitive.value);
			if (elem != null) {
				exec.push(elem);
				return TokenResult.PASS;
			}
		}
		return super.onPlusInternal(exec, other);
	}
	
	@Override
	public @NonNull TokenResult onAnd(TokenExecutor exec, @NonNull Element other) {
		if (other instanceof PrimitiveElement<?, ?> primitive) {
			Element elem = PrimitiveBinaryOpLogic.onAnd(interpreter, value, primitive.value);
			if (elem != null) {
				exec.push(elem);
				return TokenResult.PASS;
			}
		}
		return super.onAndInternal(exec, other);
	}
	
	@Override
	public @NonNull TokenResult onOr(TokenExecutor exec, @NonNull Element other) {
		if (other instanceof PrimitiveElement<?, ?> primitive) {
			Element elem = PrimitiveBinaryOpLogic.onOr(interpreter, value, primitive.value);
			if (elem != null) {
				exec.push(elem);
				return TokenResult.PASS;
			}
		}
		return super.onOrInternal(exec, other);
	}
	
	@Override
	public @NonNull TokenResult onXor(TokenExecutor exec, @NonNull Element other) {
		if (other instanceof PrimitiveElement<?, ?> primitive) {
			Element elem = PrimitiveBinaryOpLogic.onXor(interpreter, value, primitive.value);
			if (elem != null) {
				exec.push(elem);
				return TokenResult.PASS;
			}
		}
		return super.onXorInternal(exec, other);
	}
	
	@Override
	public @NonNull TokenResult onMinus(TokenExecutor exec, @NonNull Element other) {
		if (other instanceof PrimitiveElement<?, ?> primitive) {
			Element elem = PrimitiveBinaryOpLogic.onMinus(interpreter, value, primitive.value);
			if (elem != null) {
				exec.push(elem);
				return TokenResult.PASS;
			}
		}
		return super.onMinusInternal(exec, other);
	}
	
	@Override
	public @NonNull TokenResult onConcat(TokenExecutor exec, @NonNull Element other) {
		if (other instanceof PrimitiveElement<?, ?> primitive) {
			Element elem = PrimitiveBinaryOpLogic.onConcat(interpreter, value, primitive.value);
			if (elem != null) {
				exec.push(elem);
				return TokenResult.PASS;
			}
		}
		return super.onConcatInternal(exec, other);
	}
	
	@Override
	public @NonNull TokenResult onLeftShift(TokenExecutor exec, @NonNull Element other) {
		if (other instanceof PrimitiveElement<?, ?> primitive) {
			Element elem = PrimitiveBinaryOpLogic.onLeftShift(interpreter, value, primitive.value);
			if (elem != null) {
				exec.push(elem);
				return TokenResult.PASS;
			}
		}
		return super.onLeftShiftInternal(exec, other);
	}
	
	@Override
	public @NonNull TokenResult onRightShift(TokenExecutor exec, @NonNull Element other) {
		if (other instanceof PrimitiveElement<?, ?> primitive) {
			Element elem = PrimitiveBinaryOpLogic.onRightShift(interpreter, value, primitive.value);
			if (elem != null) {
				exec.push(elem);
				return TokenResult.PASS;
			}
		}
		return super.onRightShiftInternal(exec, other);
	}
	
	@Override
	public @NonNull TokenResult onMultiply(TokenExecutor exec, @NonNull Element other) {
		if (other instanceof PrimitiveElement<?, ?> primitive) {
			Element elem = PrimitiveBinaryOpLogic.onMultiply(interpreter, value, primitive.value);
			if (elem != null) {
				exec.push(elem);
				return TokenResult.PASS;
			}
		}
		return super.onMultiplyInternal(exec, other);
	}
	
	@Override
	public @NonNull TokenResult onDivide(TokenExecutor exec, @NonNull Element other) {
		if (other instanceof PrimitiveElement<?, ?> primitive) {
			Element elem = PrimitiveBinaryOpLogic.onDivide(interpreter, value, primitive.value);
			if (elem != null) {
				exec.push(elem);
				return TokenResult.PASS;
			}
		}
		return super.onDivideInternal(exec, other);
	}
	
	@Override
	public @NonNull TokenResult onRemainder(TokenExecutor exec, @NonNull Element other) {
		if (other instanceof PrimitiveElement<?, ?> primitive) {
			Element elem = PrimitiveBinaryOpLogic.onRemainder(interpreter, value, primitive.value);
			if (elem != null) {
				exec.push(elem);
				return TokenResult.PASS;
			}
		}
		return super.onRemainderInternal(exec, other);
	}
	
	@Override
	public @NonNull TokenResult onPower(TokenExecutor exec, @NonNull Element other) {
		if (other instanceof PrimitiveElement<?, ?> primitive) {
			Element elem = PrimitiveBinaryOpLogic.onPower(interpreter, value, primitive.value);
			if (elem != null) {
				exec.push(elem);
				return TokenResult.PASS;
			}
		}
		return super.onPowerInternal(exec, other);
	}
	
	@Override
	public @NonNull TokenResult onIdivide(TokenExecutor exec, @NonNull Element other) {
		if (other instanceof PrimitiveElement<?, ?> primitive) {
			Element elem = PrimitiveBinaryOpLogic.onIdivide(interpreter, value, primitive.value);
			if (elem != null) {
				exec.push(elem);
				return TokenResult.PASS;
			}
		}
		return super.onIdivideInternal(exec, other);
	}
	
	@Override
	public @NonNull TokenResult onModulo(TokenExecutor exec, @NonNull Element other) {
		if (other instanceof PrimitiveElement<?, ?> primitive) {
			Element elem = PrimitiveBinaryOpLogic.onModulo(interpreter, value, primitive.value);
			if (elem != null) {
				exec.push(elem);
				return TokenResult.PASS;
			}
		}
		return super.onModuloInternal(exec, other);
	}
	
	@Override
	public abstract @NonNull TokenResult onNot(TokenExecutor exec);
	
	@Override
	protected Object formattedInternal(TokenExecutor exec) {
		return value.raw;
	}
	
	@Override
	public Object formatted(TokenExecutor exec) {
		return formattedInternal(exec);
	}
	
	@Override
	public @NonNull StringElement __str__(TokenExecutor exec) {
		String stringValue = value.stringValue(true);
		return stringValue == null ? super.__str__(exec) : new StringElement(interpreter, stringValue);
	}
	
	@Override
	public @NonNull TokenResult __eq__(TokenExecutor exec, @NonNull Element other) {
		return onEqualTo(exec, other);
	}
	
	@Override
	public @NonNull TokenResult __ne__(TokenExecutor exec, @NonNull Element other) {
		return onNotEqualTo(exec, other);
	}
	
	@Override
	public @NonNull TokenResult __lt__(TokenExecutor exec, @NonNull Element other) {
		return onLessThan(exec, other);
	}
	
	@Override
	public @NonNull TokenResult __le__(TokenExecutor exec, @NonNull Element other) {
		return onLessOrEqual(exec, other);
	}
	
	@Override
	public @NonNull TokenResult __gt__(TokenExecutor exec, @NonNull Element other) {
		return onMoreThan(exec, other);
	}
	
	@Override
	public @NonNull TokenResult __ge__(TokenExecutor exec, @NonNull Element other) {
		return onMoreOrEqual(exec, other);
	}
	
	@Override
	public @NonNull TokenResult __add__(TokenExecutor exec, @NonNull Element other) {
		return onPlus(exec, other);
	}
	
	@Override
	public @NonNull TokenResult __and__(TokenExecutor exec, @NonNull Element other) {
		return onAnd(exec, other);
	}
	
	@Override
	public @NonNull TokenResult __or__(TokenExecutor exec, @NonNull Element other) {
		return onOr(exec, other);
	}
	
	@Override
	public @NonNull TokenResult __xor__(TokenExecutor exec, @NonNull Element other) {
		return onXor(exec, other);
	}
	
	@Override
	public @NonNull TokenResult __sub__(TokenExecutor exec, @NonNull Element other) {
		return onMinus(exec, other);
	}
	
	@Override
	public @NonNull TokenResult __concat__(TokenExecutor exec, @NonNull Element other) {
		return onConcat(exec, other);
	}
	
	@Override
	public @NonNull TokenResult __lshift__(TokenExecutor exec, @NonNull Element other) {
		return onLeftShift(exec, other);
	}
	
	@Override
	public @NonNull TokenResult __rshift__(TokenExecutor exec, @NonNull Element other) {
		return onRightShift(exec, other);
	}
	
	@Override
	public @NonNull TokenResult __mul__(TokenExecutor exec, @NonNull Element other) {
		return onMultiply(exec, other);
	}
	
	@Override
	public @NonNull TokenResult __div__(TokenExecutor exec, @NonNull Element other) {
		return onDivide(exec, other);
	}
	
	@Override
	public @NonNull TokenResult __rem__(TokenExecutor exec, @NonNull Element other) {
		return onRemainder(exec, other);
	}
	
	@Override
	public @NonNull TokenResult __pow__(TokenExecutor exec, @NonNull Element other) {
		return onPower(exec, other);
	}
	
	@Override
	public @NonNull TokenResult __floordiv__(TokenExecutor exec, @NonNull Element other) {
		return onIdivide(exec, other);
	}
	
	@Override
	public @NonNull TokenResult __mod__(TokenExecutor exec, @NonNull Element other) {
		return onModulo(exec, other);
	}
	
	@Override
	public @NonNull TokenResult __not__(TokenExecutor exec) {
		return onNot(exec);
	}
	
	@Override
	public @NonNull String toString(TokenExecutor exec) {
		return value.toString();
	}
}
