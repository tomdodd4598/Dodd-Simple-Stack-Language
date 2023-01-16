package dssl.interpret.element;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.collection.*;
import dssl.interpret.element.primitive.*;

public abstract class Element {
	
	protected Element() {}
	
	public abstract @NonNull String typeName();
	
	/** Cast element to this element's type. */
	public @NonNull Element cast(@NonNull Element elem) {
		Element cast = castInternal(elem);
		if (cast == null) {
			throw new IllegalArgumentException(String.format("Failed to cast %s \"%s\" to %s!", elem.getClass().getSimpleName(), elem.toString(), typeName()));
		}
		return cast;
	}
	
	public Element castInternal(@NonNull Element elem) {
		return null;
	}
	
	protected RuntimeException castError(String type) {
		return new IllegalArgumentException(String.format("Failed to cast %s \"%s\" to %s!", typeName(), toString(), type));
	}
	
	public IntElement intCastImplicit() {
		return null;
	}
	
	public @NonNull IntElement intCastExplicit() {
		throw castError("int");
	}
	
	public BoolElement boolCastImplicit() {
		return null;
	}
	
	public @NonNull BoolElement boolCastExplicit() {
		throw castError("bool");
	}
	
	public FloatElement floatCastImplicit() {
		return null;
	}
	
	public @NonNull FloatElement floatCastExplicit() {
		throw castError("float");
	}
	
	public CharElement charCastImplicit() {
		return null;
	}
	
	public @NonNull CharElement charCastExplicit() {
		throw castError("char");
	}
	
	public StringElement stringCastImplicit() {
		return null;
	}
	
	public abstract @NonNull StringElement stringCastExplicit();
	
	public @NonNull RangeElement rangeCastExplicit() {
		throw castError("range");
	}
	
	public @NonNull ListElement listCastExplicit() {
		throw castError("list");
	}
	
	public @NonNull TupleElement tupleCastExplicit() {
		throw castError("tuple");
	}
	
	public @NonNull SetElement setCastExplicit() {
		throw castError("set");
	}
	
	public @NonNull DictElement dictCastExplicit() {
		throw castError("dict");
	}
	
	protected RuntimeException binaryOpError(String operator, @NonNull Element other) {
		return new IllegalArgumentException(String.format("Binary operator \"%s\" is undefined for argument types \"%s\" and \"%s\"!", operator, typeName(), other.typeName()));
	}
	
	public @NonNull Element onEqualTo(@NonNull Element other) {
		throw binaryOpError("==", other);
	}
	
	public @NonNull Element onNotEqualTo(@NonNull Element other) {
		throw binaryOpError("!=", other);
	}
	
	public @NonNull Element onLessThan(@NonNull Element other) {
		throw binaryOpError("<", other);
	}
	
	public @NonNull Element onLessOrEqual(@NonNull Element other) {
		throw binaryOpError("<=", other);
	}
	
	public @NonNull Element onMoreThan(@NonNull Element other) {
		throw binaryOpError(">", other);
	}
	
	public @NonNull Element onMoreOrEqual(@NonNull Element other) {
		throw binaryOpError(">=", other);
	}
	
	public @NonNull Element onPlus(@NonNull Element other) {
		throw binaryOpError("+", other);
	}
	
	public @NonNull Element onAnd(@NonNull Element other) {
		throw binaryOpError("&", other);
	}
	
	public @NonNull Element onOr(@NonNull Element other) {
		throw binaryOpError("|", other);
	}
	
	public @NonNull Element onXor(@NonNull Element other) {
		throw binaryOpError("^", other);
	}
	
	public @NonNull Element onMinus(@NonNull Element other) {
		throw binaryOpError("-", other);
	}
	
	public @NonNull Element onConcat(@NonNull Element other) {
		throw binaryOpError("~", other);
	}
	
	public @NonNull Element onArithmeticLeftShift(@NonNull Element other) {
		throw binaryOpError("<<", other);
	}
	
	public @NonNull Element onArithmeticRightShift(@NonNull Element other) {
		throw binaryOpError(">>", other);
	}
	
	public @NonNull Element onMultiply(@NonNull Element other) {
		throw binaryOpError("*", other);
	}
	
	public @NonNull Element onDivide(@NonNull Element other) {
		throw binaryOpError("/", other);
	}
	
	public @NonNull Element onRemainder(@NonNull Element other) {
		throw binaryOpError("%", other);
	}
	
	public @NonNull Element onPower(@NonNull Element other) {
		throw binaryOpError("**", other);
	}
	
	public @NonNull Element onIdivide(@NonNull Element other) {
		throw binaryOpError("//", other);
	}
	
	public @NonNull Element onModulo(@NonNull Element other) {
		throw binaryOpError("%%", other);
	}
	
	protected RuntimeException unaryOpError(String operator) {
		return new IllegalArgumentException(String.format("Unary operator \"%s\" is undefined for argument type \"%s\"!", operator, typeName()));
	}
	
	public @NonNull Element onNot() {
		throw unaryOpError("not");
	}
	
	public @NonNull Element onNeg() {
		throw unaryOpError("neg");
	}
	
	protected RuntimeException keywordError(String keyword) {
		return new IllegalArgumentException(String.format("Action of keyword \"%s\" is undefined for argument type \"%s\"!", keyword, typeName()));
	}
	
	public TokenResult onUnpack(TokenExecutor exec) {
		throw keywordError("unpack");
	}
	
	public TokenResult onSize(TokenExecutor exec) {
		throw keywordError("size");
	}
	
	public TokenResult onEmpty(TokenExecutor exec) {
		throw keywordError("empty");
	}
	
	public TokenResult onHas(TokenExecutor exec, @NonNull Element elem) {
		throw keywordError("has");
	}
	
	public TokenResult onAdd(TokenExecutor exec, @NonNull Element elem) {
		throw keywordError("add");
	}
	
	public TokenResult onRem(TokenExecutor exec, @NonNull Element elem) {
		throw keywordError("rem");
	}
	
	public TokenResult onHasall(TokenExecutor exec, @NonNull Element elem) {
		throw keywordError("hasall");
	}
	
	public TokenResult onAddall(TokenExecutor exec, @NonNull Element elem) {
		throw keywordError("addall");
	}
	
	public TokenResult onRemall(TokenExecutor exec, @NonNull Element elem) {
		throw keywordError("remall");
	}
	
	public TokenResult onClear(TokenExecutor exec) {
		throw keywordError("clear");
	}
	
	public TokenResult onGet(TokenExecutor exec, @NonNull Element elem) {
		throw keywordError("get");
	}
	
	public TokenResult onPut(TokenExecutor exec, @NonNull Element elem0, @NonNull Element elem1) {
		throw keywordError("put");
	}
	
	public TokenResult onPutall(TokenExecutor exec, @NonNull Element elem) {
		throw keywordError("putall");
	}
	
	@Override
	public abstract @NonNull Element clone();
	
	@Override
	public abstract int hashCode();
	
	protected int objectHashCode() {
		return super.hashCode();
	}
	
	@Override
	public abstract boolean equals(Object obj);
	
	@Override
	public abstract @NonNull String toString();
	
	public abstract @NonNull String toDebugString();
}
