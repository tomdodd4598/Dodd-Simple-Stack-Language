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
	
	public TokenResult onEqualTo(TokenExecutor exec, @NonNull Element other) {
		throw binaryOpError("==", other);
	}
	
	public TokenResult onNotEqualTo(TokenExecutor exec, @NonNull Element other) {
		throw binaryOpError("!=", other);
	}
	
	public TokenResult onLessThan(TokenExecutor exec, @NonNull Element other) {
		throw binaryOpError("<", other);
	}
	
	public TokenResult onLessOrEqual(TokenExecutor exec, @NonNull Element other) {
		throw binaryOpError("<=", other);
	}
	
	public TokenResult onMoreThan(TokenExecutor exec, @NonNull Element other) {
		throw binaryOpError(">", other);
	}
	
	public TokenResult onMoreOrEqual(TokenExecutor exec, @NonNull Element other) {
		throw binaryOpError(">=", other);
	}
	
	public TokenResult onPlus(TokenExecutor exec, @NonNull Element other) {
		throw binaryOpError("+", other);
	}
	
	public TokenResult onAnd(TokenExecutor exec, @NonNull Element other) {
		throw binaryOpError("&", other);
	}
	
	public TokenResult onOr(TokenExecutor exec, @NonNull Element other) {
		throw binaryOpError("|", other);
	}
	
	public TokenResult onXor(TokenExecutor exec, @NonNull Element other) {
		throw binaryOpError("^", other);
	}
	
	public TokenResult onMinus(TokenExecutor exec, @NonNull Element other) {
		throw binaryOpError("-", other);
	}
	
	public TokenResult onConcat(TokenExecutor exec, @NonNull Element other) {
		throw binaryOpError("~", other);
	}
	
	public TokenResult onLeftShift(TokenExecutor exec, @NonNull Element other) {
		throw binaryOpError("<<", other);
	}
	
	public TokenResult onRightShift(TokenExecutor exec, @NonNull Element other) {
		throw binaryOpError(">>", other);
	}
	
	public TokenResult onMultiply(TokenExecutor exec, @NonNull Element other) {
		throw binaryOpError("*", other);
	}
	
	public TokenResult onDivide(TokenExecutor exec, @NonNull Element other) {
		throw binaryOpError("/", other);
	}
	
	public TokenResult onRemainder(TokenExecutor exec, @NonNull Element other) {
		throw binaryOpError("%", other);
	}
	
	public TokenResult onPower(TokenExecutor exec, @NonNull Element other) {
		throw binaryOpError("**", other);
	}
	
	public TokenResult onIdivide(TokenExecutor exec, @NonNull Element other) {
		throw binaryOpError("//", other);
	}
	
	public TokenResult onModulo(TokenExecutor exec, @NonNull Element other) {
		throw binaryOpError("%%", other);
	}
	
	protected RuntimeException unaryOpError(String operator) {
		return new IllegalArgumentException(String.format("Unary operator \"%s\" is undefined for argument type \"%s\"!", operator, typeName()));
	}
	
	public TokenResult onNot(TokenExecutor exec) {
		throw unaryOpError("not");
	}
	
	public TokenResult onNeg(TokenExecutor exec) {
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
	
	public TokenResult onContains(TokenExecutor exec, @NonNull Element elem) {
		throw keywordError("contains");
	}
	
	public TokenResult onAdd(TokenExecutor exec, @NonNull Element elem) {
		throw keywordError("add");
	}
	
	public TokenResult onRemove(TokenExecutor exec, @NonNull Element elem) {
		throw keywordError("remove");
	}
	
	public TokenResult onContainsall(TokenExecutor exec, @NonNull Element elem) {
		throw keywordError("containsall");
	}
	
	public TokenResult onAddall(TokenExecutor exec, @NonNull Element elem) {
		throw keywordError("addall");
	}
	
	public TokenResult onRemoveall(TokenExecutor exec, @NonNull Element elem) {
		throw keywordError("removeall");
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
