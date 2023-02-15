package dssl.interpret.element.primitive;

import java.math.BigInteger;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.*;
import dssl.interpret.value.*;

public abstract class PrimitiveElement<@NonNull RAW, @NonNull VALUE extends @NonNull PrimitiveValue<@NonNull RAW>> extends ValueElement {
	
	public final @NonNull VALUE value;
	
	protected PrimitiveElement(@NonNull Clazz clazz, @NonNull VALUE value) {
		super(clazz);
		this.value = value;
	}
	
	@Override
	public IntElement intCast(boolean explicit) {
		BigInteger intValue = value.intValue(explicit);
		return intValue == null ? null : new IntElement(intValue);
	}
	
	@Override
	public BoolElement boolCast(boolean explicit) {
		Boolean boolValue = value.boolValue(explicit);
		return boolValue == null ? null : new BoolElement(boolValue);
	}
	
	@Override
	public FloatElement floatCast(boolean explicit) {
		Double floatValue = value.floatValue(explicit);
		return floatValue == null ? null : new FloatElement(floatValue);
	}
	
	@Override
	public CharElement charCast(boolean explicit) {
		Character charValue = value.charValue(explicit);
		return charValue == null ? null : new CharElement(charValue);
	}
	
	@Override
	public StringElement stringCast(boolean explicit) {
		String stringValue = value.stringValue(explicit);
		return stringValue == null ? null : new StringElement(stringValue);
	}
	
	@Override
	public TokenResult onEqualTo(TokenExecutor exec, @NonNull Element other) {
		if (other instanceof PrimitiveElement<?, ?>) {
			Element elem = PrimitiveBinaryOpLogic.onEqualTo(value, ((PrimitiveElement<?, ?>) other).value);
			if (elem != null) {
				exec.push(elem);
				return TokenResult.PASS;
			}
		}
		return super.onEqualTo(exec, other);
	}
	
	@Override
	public TokenResult onNotEqualTo(TokenExecutor exec, @NonNull Element other) {
		if (other instanceof PrimitiveElement<?, ?>) {
			Element elem = PrimitiveBinaryOpLogic.onNotEqualTo(value, ((PrimitiveElement<?, ?>) other).value);
			if (elem != null) {
				exec.push(elem);
				return TokenResult.PASS;
			}
		}
		return super.onNotEqualTo(exec, other);
	}
	
	@Override
	public TokenResult onLessThan(TokenExecutor exec, @NonNull Element other) {
		if (other instanceof PrimitiveElement<?, ?>) {
			Element elem = PrimitiveBinaryOpLogic.onLessThan(value, ((PrimitiveElement<?, ?>) other).value);
			if (elem != null) {
				exec.push(elem);
				return TokenResult.PASS;
			}
		}
		return super.onLessThan(exec, other);
	}
	
	@Override
	public TokenResult onLessOrEqual(TokenExecutor exec, @NonNull Element other) {
		if (other instanceof PrimitiveElement<?, ?>) {
			Element elem = PrimitiveBinaryOpLogic.onLessOrEqual(value, ((PrimitiveElement<?, ?>) other).value);
			if (elem != null) {
				exec.push(elem);
				return TokenResult.PASS;
			}
		}
		return super.onLessOrEqual(exec, other);
	}
	
	@Override
	public TokenResult onMoreThan(TokenExecutor exec, @NonNull Element other) {
		if (other instanceof PrimitiveElement<?, ?>) {
			Element elem = PrimitiveBinaryOpLogic.onMoreThan(value, ((PrimitiveElement<?, ?>) other).value);
			if (elem != null) {
				exec.push(elem);
				return TokenResult.PASS;
			}
		}
		return super.onMoreThan(exec, other);
	}
	
	@Override
	public TokenResult onMoreOrEqual(TokenExecutor exec, @NonNull Element other) {
		if (other instanceof PrimitiveElement<?, ?>) {
			Element elem = PrimitiveBinaryOpLogic.onMoreOrEqual(value, ((PrimitiveElement<?, ?>) other).value);
			if (elem != null) {
				exec.push(elem);
				return TokenResult.PASS;
			}
		}
		return super.onMoreOrEqual(exec, other);
	}
	
	@Override
	public TokenResult onPlus(TokenExecutor exec, @NonNull Element other) {
		if (other instanceof PrimitiveElement<?, ?>) {
			Element elem = PrimitiveBinaryOpLogic.onPlus(value, ((PrimitiveElement<?, ?>) other).value);
			if (elem != null) {
				exec.push(elem);
				return TokenResult.PASS;
			}
		}
		return super.onPlus(exec, other);
	}
	
	@Override
	public TokenResult onAnd(TokenExecutor exec, @NonNull Element other) {
		if (other instanceof PrimitiveElement<?, ?>) {
			Element elem = PrimitiveBinaryOpLogic.onAnd(value, ((PrimitiveElement<?, ?>) other).value);
			if (elem != null) {
				exec.push(elem);
				return TokenResult.PASS;
			}
		}
		return super.onAnd(exec, other);
	}
	
	@Override
	public TokenResult onOr(TokenExecutor exec, @NonNull Element other) {
		if (other instanceof PrimitiveElement<?, ?>) {
			Element elem = PrimitiveBinaryOpLogic.onOr(value, ((PrimitiveElement<?, ?>) other).value);
			if (elem != null) {
				exec.push(elem);
				return TokenResult.PASS;
			}
		}
		return super.onOr(exec, other);
	}
	
	@Override
	public TokenResult onXor(TokenExecutor exec, @NonNull Element other) {
		if (other instanceof PrimitiveElement<?, ?>) {
			Element elem = PrimitiveBinaryOpLogic.onXor(value, ((PrimitiveElement<?, ?>) other).value);
			if (elem != null) {
				exec.push(elem);
				return TokenResult.PASS;
			}
		}
		return super.onXor(exec, other);
	}
	
	@Override
	public TokenResult onMinus(TokenExecutor exec, @NonNull Element other) {
		if (other instanceof PrimitiveElement<?, ?>) {
			Element elem = PrimitiveBinaryOpLogic.onMinus(value, ((PrimitiveElement<?, ?>) other).value);
			if (elem != null) {
				exec.push(elem);
				return TokenResult.PASS;
			}
		}
		return super.onMinus(exec, other);
	}
	
	@Override
	public TokenResult onConcat(TokenExecutor exec, @NonNull Element other) {
		if (other instanceof PrimitiveElement<?, ?>) {
			Element elem = PrimitiveBinaryOpLogic.onConcat(value, ((PrimitiveElement<?, ?>) other).value);
			if (elem != null) {
				exec.push(elem);
				return TokenResult.PASS;
			}
		}
		return super.onConcat(exec, other);
	}
	
	@Override
	public TokenResult onLeftShift(TokenExecutor exec, @NonNull Element other) {
		if (other instanceof PrimitiveElement<?, ?>) {
			Element elem = PrimitiveBinaryOpLogic.onLeftShift(value, ((PrimitiveElement<?, ?>) other).value);
			if (elem != null) {
				exec.push(elem);
				return TokenResult.PASS;
			}
		}
		return super.onLeftShift(exec, other);
	}
	
	@Override
	public TokenResult onRightShift(TokenExecutor exec, @NonNull Element other) {
		if (other instanceof PrimitiveElement<?, ?>) {
			Element elem = PrimitiveBinaryOpLogic.onRightShift(value, ((PrimitiveElement<?, ?>) other).value);
			if (elem != null) {
				exec.push(elem);
				return TokenResult.PASS;
			}
		}
		return super.onRightShift(exec, other);
	}
	
	@Override
	public TokenResult onMultiply(TokenExecutor exec, @NonNull Element other) {
		if (other instanceof PrimitiveElement<?, ?>) {
			Element elem = PrimitiveBinaryOpLogic.onMultiply(value, ((PrimitiveElement<?, ?>) other).value);
			if (elem != null) {
				exec.push(elem);
				return TokenResult.PASS;
			}
		}
		return super.onMultiply(exec, other);
	}
	
	@Override
	public TokenResult onDivide(TokenExecutor exec, @NonNull Element other) {
		if (other instanceof PrimitiveElement<?, ?>) {
			Element elem = PrimitiveBinaryOpLogic.onDivide(value, ((PrimitiveElement<?, ?>) other).value);
			if (elem != null) {
				exec.push(elem);
				return TokenResult.PASS;
			}
		}
		return super.onDivide(exec, other);
	}
	
	@Override
	public TokenResult onRemainder(TokenExecutor exec, @NonNull Element other) {
		if (other instanceof PrimitiveElement<?, ?>) {
			Element elem = PrimitiveBinaryOpLogic.onRemainder(value, ((PrimitiveElement<?, ?>) other).value);
			if (elem != null) {
				exec.push(elem);
				return TokenResult.PASS;
			}
		}
		return super.onRemainder(exec, other);
	}
	
	@Override
	public TokenResult onPower(TokenExecutor exec, @NonNull Element other) {
		if (other instanceof PrimitiveElement<?, ?>) {
			Element elem = PrimitiveBinaryOpLogic.onPower(value, ((PrimitiveElement<?, ?>) other).value);
			if (elem != null) {
				exec.push(elem);
				return TokenResult.PASS;
			}
		}
		return super.onPower(exec, other);
	}
	
	@Override
	public TokenResult onIdivide(TokenExecutor exec, @NonNull Element other) {
		if (other instanceof PrimitiveElement<?, ?>) {
			Element elem = PrimitiveBinaryOpLogic.onIdivide(value, ((PrimitiveElement<?, ?>) other).value);
			if (elem != null) {
				exec.push(elem);
				return TokenResult.PASS;
			}
		}
		return super.onIdivide(exec, other);
	}
	
	@Override
	public TokenResult onModulo(TokenExecutor exec, @NonNull Element other) {
		if (other instanceof PrimitiveElement<?, ?>) {
			Element elem = PrimitiveBinaryOpLogic.onModulo(value, ((PrimitiveElement<?, ?>) other).value);
			if (elem != null) {
				exec.push(elem);
				return TokenResult.PASS;
			}
		}
		return super.onModulo(exec, other);
	}
	
	@Override
	public abstract TokenResult onNot(TokenExecutor exec);
	
	@Override
	public @NonNull String toString() {
		return value.toString();
	}
	
	@Override
	public @NonNull String debugString() {
		return value.toString();
	}
}
