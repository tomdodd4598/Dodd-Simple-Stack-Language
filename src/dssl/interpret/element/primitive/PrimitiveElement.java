package dssl.interpret.element.primitive;

import java.math.BigInteger;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.element.Element;
import dssl.interpret.value.*;

public abstract class PrimitiveElement<@NonNull T> extends Element {
	
	public final @NonNull PrimitiveValue<@NonNull T> value;
	
	protected PrimitiveElement(@NonNull PrimitiveValue<@NonNull T> value) {
		super();
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
	public @NonNull Element onEqualTo(@NonNull Element other) {
		if (other instanceof PrimitiveElement<?>) {
			Element elem = PrimitiveBinaryOpLogic.onEqualTo(value, ((PrimitiveElement<?>) other).value);
			if (elem != null) {
				return elem;
			}
		}
		throw binaryOpError("==", other);
	}
	
	@Override
	public @NonNull Element onNotEqualTo(@NonNull Element other) {
		if (other instanceof PrimitiveElement<?>) {
			Element elem = PrimitiveBinaryOpLogic.onNotEqualTo(value, ((PrimitiveElement<?>) other).value);
			if (elem != null) {
				return elem;
			}
		}
		throw binaryOpError("!=", other);
	}
	
	@Override
	public @NonNull Element onLessThan(@NonNull Element other) {
		if (other instanceof PrimitiveElement<?>) {
			Element elem = PrimitiveBinaryOpLogic.onLessThan(value, ((PrimitiveElement<?>) other).value);
			if (elem != null) {
				return elem;
			}
		}
		throw binaryOpError("<", other);
	}
	
	@Override
	public @NonNull Element onLessOrEqual(@NonNull Element other) {
		if (other instanceof PrimitiveElement<?>) {
			Element elem = PrimitiveBinaryOpLogic.onLessOrEqual(value, ((PrimitiveElement<?>) other).value);
			if (elem != null) {
				return elem;
			}
		}
		throw binaryOpError("<=", other);
	}
	
	@Override
	public @NonNull Element onMoreThan(@NonNull Element other) {
		if (other instanceof PrimitiveElement<?>) {
			Element elem = PrimitiveBinaryOpLogic.onMoreThan(value, ((PrimitiveElement<?>) other).value);
			if (elem != null) {
				return elem;
			}
		}
		throw binaryOpError(">", other);
	}
	
	@Override
	public @NonNull Element onMoreOrEqual(@NonNull Element other) {
		if (other instanceof PrimitiveElement<?>) {
			Element elem = PrimitiveBinaryOpLogic.onMoreOrEqual(value, ((PrimitiveElement<?>) other).value);
			if (elem != null) {
				return elem;
			}
		}
		throw binaryOpError(">=", other);
	}
	
	@Override
	public @NonNull Element onPlus(@NonNull Element other) {
		if (other instanceof PrimitiveElement<?>) {
			Element elem = PrimitiveBinaryOpLogic.onPlus(value, ((PrimitiveElement<?>) other).value);
			if (elem != null) {
				return elem;
			}
		}
		throw binaryOpError("+", other);
	}
	
	@Override
	public @NonNull Element onAnd(@NonNull Element other) {
		if (other instanceof PrimitiveElement<?>) {
			Element elem = PrimitiveBinaryOpLogic.onAnd(value, ((PrimitiveElement<?>) other).value);
			if (elem != null) {
				return elem;
			}
		}
		throw binaryOpError("&", other);
	}
	
	@Override
	public @NonNull Element onOr(@NonNull Element other) {
		if (other instanceof PrimitiveElement<?>) {
			Element elem = PrimitiveBinaryOpLogic.onOr(value, ((PrimitiveElement<?>) other).value);
			if (elem != null) {
				return elem;
			}
		}
		throw binaryOpError("|", other);
	}
	
	@Override
	public @NonNull Element onXor(@NonNull Element other) {
		if (other instanceof PrimitiveElement<?>) {
			Element elem = PrimitiveBinaryOpLogic.onXor(value, ((PrimitiveElement<?>) other).value);
			if (elem != null) {
				return elem;
			}
		}
		throw binaryOpError("^", other);
	}
	
	@Override
	public @NonNull Element onMinus(@NonNull Element other) {
		if (other instanceof PrimitiveElement<?>) {
			Element elem = PrimitiveBinaryOpLogic.onMinus(value, ((PrimitiveElement<?>) other).value);
			if (elem != null) {
				return elem;
			}
		}
		throw binaryOpError("-", other);
	}
	
	@Override
	public @NonNull Element onConcat(@NonNull Element other) {
		if (other instanceof PrimitiveElement<?>) {
			Element elem = PrimitiveBinaryOpLogic.onConcat(value, ((PrimitiveElement<?>) other).value);
			if (elem != null) {
				return elem;
			}
		}
		throw binaryOpError("~", other);
	}
	
	@Override
	public @NonNull Element onArithmeticLeftShift(@NonNull Element other) {
		if (other instanceof PrimitiveElement<?>) {
			Element elem = PrimitiveBinaryOpLogic.onArithmeticLeftShift(value, ((PrimitiveElement<?>) other).value);
			if (elem != null) {
				return elem;
			}
		}
		throw binaryOpError("<<", other);
	}
	
	@Override
	public @NonNull Element onArithmeticRightShift(@NonNull Element other) {
		if (other instanceof PrimitiveElement<?>) {
			Element elem = PrimitiveBinaryOpLogic.onArithmeticRightShift(value, ((PrimitiveElement<?>) other).value);
			if (elem != null) {
				return elem;
			}
		}
		throw binaryOpError(">>", other);
	}
	
	@Override
	public @NonNull Element onMultiply(@NonNull Element other) {
		if (other instanceof PrimitiveElement<?>) {
			Element elem = PrimitiveBinaryOpLogic.onMultiply(value, ((PrimitiveElement<?>) other).value);
			if (elem != null) {
				return elem;
			}
		}
		throw binaryOpError("*", other);
	}
	
	@Override
	public @NonNull Element onDivide(@NonNull Element other) {
		if (other instanceof PrimitiveElement<?>) {
			Element elem = PrimitiveBinaryOpLogic.onDivide(value, ((PrimitiveElement<?>) other).value);
			if (elem != null) {
				return elem;
			}
		}
		throw binaryOpError("/", other);
	}
	
	@Override
	public @NonNull Element onRemainder(@NonNull Element other) {
		if (other instanceof PrimitiveElement<?>) {
			Element elem = PrimitiveBinaryOpLogic.onRemainder(value, ((PrimitiveElement<?>) other).value);
			if (elem != null) {
				return elem;
			}
		}
		throw binaryOpError("%", other);
	}
	
	@Override
	public @NonNull Element onPower(@NonNull Element other) {
		if (other instanceof PrimitiveElement<?>) {
			Element elem = PrimitiveBinaryOpLogic.onPower(value, ((PrimitiveElement<?>) other).value);
			if (elem != null) {
				return elem;
			}
		}
		throw binaryOpError("**", other);
	}
	
	@Override
	public @NonNull Element onIdivide(@NonNull Element other) {
		if (other instanceof PrimitiveElement<?>) {
			Element elem = PrimitiveBinaryOpLogic.onIdivide(value, ((PrimitiveElement<?>) other).value);
			if (elem != null) {
				return elem;
			}
		}
		throw binaryOpError("//", other);
	}
	
	@Override
	public @NonNull Element onModulo(@NonNull Element other) {
		if (other instanceof PrimitiveElement<?>) {
			Element elem = PrimitiveBinaryOpLogic.onModulo(value, ((PrimitiveElement<?>) other).value);
			if (elem != null) {
				return elem;
			}
		}
		throw binaryOpError("%%", other);
	}
	
	@Override
	public abstract @NonNull Element onNot();
	
	@Override
	public abstract @NonNull Element onNeg();
	
	@Override
	public @NonNull String toString() {
		return value.toString();
	}
	
	@Override
	public @NonNull String toDebugString() {
		return value.toString();
	}
}
