package dssl.interpret.element;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.container.*;
import dssl.interpret.element.primitive.*;

public abstract class Element {
	
	protected Element() {}
	
	public abstract @NonNull String typeName();
	
	public IntElement intCast(boolean explicit) {
		return null;
	}
	
	public BoolElement boolCast(boolean explicit) {
		return null;
	}
	
	public FloatElement floatCast(boolean explicit) {
		return null;
	}
	
	public CharElement charCast(boolean explicit) {
		return null;
	}
	
	public StringElement stringCast(boolean explicit) {
		return null;
	}
	
	public RangeElement rangeCast() {
		return null;
	}
	
	public ListElement listCast() {
		return null;
	}
	
	public TupleElement tupleCast() {
		return null;
	}
	
	public SetElement setCast() {
		return null;
	}
	
	public DictElement dictCast() {
		return null;
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
		throw unaryOpError("!");
	}
	
	protected RuntimeException builtInMethodError(String name) {
		return new IllegalArgumentException(String.format("Built-in method \"%s\" is undefined for argument type \"%s\"!", name, typeName()));
	}
	
	public void unpack(TokenExecutor exec) {
		throw builtInMethodError("unpack");
	}
	
	public int size() {
		throw builtInMethodError("size");
	}
	
	public boolean isEmpty() {
		throw builtInMethodError("isEmpty");
	}
	
	public boolean contains(@NonNull Element elem) {
		throw builtInMethodError("contains");
	}
	
	public void add(@NonNull Element elem) {
		throw builtInMethodError("add");
	}
	
	public void remove(@NonNull Element elem) {
		throw builtInMethodError("remove");
	}
	
	public boolean containsAll(@NonNull Element elem) {
		throw builtInMethodError("containsAll");
	}
	
	public void addAll(@NonNull Element elem) {
		throw builtInMethodError("addAll");
	}
	
	public void removeAll(@NonNull Element elem) {
		throw builtInMethodError("removeAll");
	}
	
	public void clear() {
		throw builtInMethodError("clear");
	}
	
	public @NonNull Element get(@NonNull Element elem) {
		throw builtInMethodError("get");
	}
	
	public void put(@NonNull Element elem0, @NonNull Element elem1) {
		throw builtInMethodError("put");
	}
	
	public void putAll(@NonNull Element elem) {
		throw builtInMethodError("putAll");
	}
	
	public boolean containsKey(@NonNull Element elem) {
		throw builtInMethodError("containsKey");
	}
	
	public boolean containsValue(@NonNull Element elem) {
		throw builtInMethodError("containsValue");
	}
	
	public @NonNull Element keys() {
		throw builtInMethodError("keys");
	}
	
	public @NonNull Element values() {
		throw builtInMethodError("values");
	}
	
	@Override
	public @NonNull Element clone() {
		throw builtInMethodError("clone");
	}
	
	public int hash() {
		throw builtInMethodError("hash");
	}
	
	@Override
	public abstract int hashCode();
	
	protected int objectHashCode() {
		return super.hashCode();
	}
	
	@Override
	public abstract boolean equals(Object obj);
	
	@Override
	public abstract @NonNull String toString();
	
	public abstract @NonNull String debugString();
}
