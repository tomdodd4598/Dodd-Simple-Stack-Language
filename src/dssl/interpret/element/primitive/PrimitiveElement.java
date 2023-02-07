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
	public IntElement intCastImplicit() {
		BigInteger intValue = value.intValue(false);
		return intValue == null ? null : new IntElement(intValue);
	}
	
	@Override
	public @NonNull IntElement intCastExplicit() {
		BigInteger intValue = value.intValue(true);
		if (intValue == null) {
			throw castError("int");
		}
		return new IntElement(intValue);
	}
	
	@Override
	public BoolElement boolCastImplicit() {
		Boolean boolValue = value.boolValue(false);
		return boolValue == null ? null : new BoolElement(boolValue);
	}
	
	@Override
	public @NonNull BoolElement boolCastExplicit() {
		Boolean boolValue = value.boolValue(true);
		if (boolValue == null) {
			throw castError("bool");
		}
		return new BoolElement(boolValue);
	}
	
	@Override
	public FloatElement floatCastImplicit() {
		Double floatValue = value.floatValue(false);
		return floatValue == null ? null : new FloatElement(floatValue);
	}
	
	@Override
	public @NonNull FloatElement floatCastExplicit() {
		Double floatValue = value.floatValue(true);
		if (floatValue == null) {
			throw castError("float");
		}
		return new FloatElement(floatValue);
	}
	
	@Override
	public CharElement charCastImplicit() {
		Character charValue = value.charValue(false);
		return charValue == null ? null : new CharElement(charValue);
	}
	
	@Override
	public @NonNull CharElement charCastExplicit() {
		Character charValue = value.charValue(true);
		if (charValue == null) {
			throw castError("char");
		}
		return new CharElement(charValue);
	}
	
	@Override
	public StringElement stringCastImplicit() {
		String stringValue = value.stringValue(false);
		return stringValue == null ? null : new StringElement(stringValue);
	}
	
	@Override
	public @NonNull StringElement stringCastExplicit() {
		String stringValue = value.stringValue(true);
		if (stringValue == null) {
			throw castError("string");
		}
		return new StringElement(stringValue);
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
	public @NonNull String toDebugString() {
		return value.toString();
	}
}
