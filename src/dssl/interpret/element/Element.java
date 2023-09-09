package dssl.interpret.element;

import org.eclipse.jdt.annotation.*;

import dssl.Helpers;
import dssl.interpret.*;
import dssl.interpret.element.primitive.*;

public abstract class Element {
	
	public final @NonNull Clazz clazz;
	
	protected Element(@NonNull Clazz clazz) {
		this.clazz = clazz;
	}
	
	public final @NonNull String typeName() {
		return clazz.fullIdentifier;
	}
	
	public @Nullable IntElement asInt(TokenExecutor exec) {
		return null;
	}
	
	public @Nullable BoolElement asBool(TokenExecutor exec) {
		return null;
	}
	
	public @Nullable FloatElement asFloat(TokenExecutor exec) {
		return null;
	}
	
	public @Nullable CharElement asChar(TokenExecutor exec) {
		return null;
	}
	
	public @Nullable StringElement asString(TokenExecutor exec) {
		return null;
	}
	
	protected RuntimeException castError(String type) {
		return new IllegalArgumentException(String.format("Failed to cast %s \"%s\" to %s!", typeName(), this, type));
	}
	
	public @NonNull IntElement intCast(TokenExecutor exec) {
		throw castError(BuiltIn.INT);
	}
	
	public @NonNull BoolElement boolCast(TokenExecutor exec) {
		throw castError(BuiltIn.BOOL);
	}
	
	public @NonNull FloatElement floatCast(TokenExecutor exec) {
		throw castError(BuiltIn.FLOAT);
	}
	
	public @NonNull CharElement charCast(TokenExecutor exec) {
		throw castError(BuiltIn.CHAR);
	}
	
	public @NonNull StringElement stringCast(TokenExecutor exec) {
		return new StringElement(toString());
	}
	
	public @NonNull RangeElement rangeCast(TokenExecutor exec) {
		throw castError(BuiltIn.RANGE);
	}
	
	public @NonNull ListElement listCast(TokenExecutor exec) {
		throw castError(BuiltIn.LIST);
	}
	
	public @NonNull SetElement setCast(TokenExecutor exec) {
		throw castError(BuiltIn.SET);
	}
	
	public @NonNull DictElement dictCast(TokenExecutor exec) {
		throw castError(BuiltIn.DICT);
	}
	
	protected RuntimeException binaryOpError(String operator, @NonNull Element other) {
		return new IllegalArgumentException(String.format("Binary operator \"%s\" is undefined for argument types \"%s\" and \"%s\"!", operator, typeName(), other.typeName()));
	}
	
	public TokenResult onEqualTo(TokenExecutor exec, @NonNull Element other) {
		exec.push(new BoolElement(NullElement.INSTANCE.equals(other) ? false : equals(other)));
		return TokenResult.PASS;
	}
	
	public TokenResult onNotEqualTo(TokenExecutor exec, @NonNull Element other) {
		exec.push(new BoolElement(NullElement.INSTANCE.equals(other) ? true : !equals(other)));
		return TokenResult.PASS;
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
		if (other instanceof StringElement) {
			exec.push(new StringElement(stringCast(exec).toString() + other));
			return TokenResult.PASS;
		}
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
	
	public int size(TokenExecutor exec) {
		throw builtInMethodError("size");
	}
	
	public boolean isEmpty(TokenExecutor exec) {
		throw builtInMethodError("isEmpty");
	}
	
	public @NonNull Element iter(TokenExecutor exec) {
		throw builtInMethodError("iter");
	}
	
	public boolean contains(TokenExecutor exec, @NonNull Element elem) {
		throw builtInMethodError("contains");
	}
	
	public void add(TokenExecutor exec, @NonNull Element elem) {
		throw builtInMethodError("add");
	}
	
	public void remove(TokenExecutor exec, @NonNull Element elem) {
		throw builtInMethodError("remove");
	}
	
	public boolean containsAll(TokenExecutor exec, @NonNull Element elem) {
		throw builtInMethodError("containsAll");
	}
	
	public void addAll(TokenExecutor exec, @NonNull Element elem) {
		throw builtInMethodError("addAll");
	}
	
	public void removeAll(TokenExecutor exec, @NonNull Element elem) {
		throw builtInMethodError("removeAll");
	}
	
	public void clear(TokenExecutor exec) {
		throw builtInMethodError("clear");
	}
	
	public @NonNull Element get(TokenExecutor exec, @NonNull Element elem) {
		throw builtInMethodError("get");
	}
	
	public void put(TokenExecutor exec, @NonNull Element elem0, @NonNull Element elem1) {
		throw builtInMethodError("put");
	}
	
	public @NonNull Element slice(TokenExecutor exec, @NonNull Element elem0, @NonNull Element elem1) {
		throw builtInMethodError("slice");
	}
	
	public @NonNull Element startsWith(TokenExecutor exec, @NonNull Element elem) {
		throw builtInMethodError("startsWith");
	}
	
	public @NonNull Element endsWith(TokenExecutor exec, @NonNull Element elem) {
		throw builtInMethodError("endsWith");
	}
	
	public @NonNull Element matches(TokenExecutor exec, @NonNull Element elem) {
		throw builtInMethodError("matches");
	}
	
	public @NonNull Element replace(TokenExecutor exec, @NonNull Element elem0, @NonNull Element elem1) {
		throw builtInMethodError("replace");
	}
	
	public @NonNull Element split(TokenExecutor exec, @NonNull Element elem) {
		throw builtInMethodError("split");
	}
	
	public @NonNull Element lower(TokenExecutor exec) {
		throw builtInMethodError("lower");
	}
	
	public @NonNull Element upper(TokenExecutor exec) {
		throw builtInMethodError("upper");
	}
	
	public @NonNull Element trim(TokenExecutor exec) {
		throw builtInMethodError("trim");
	}
	
	public @NonNull Element format(TokenExecutor exec, @NonNull Element elem) {
		throw builtInMethodError("format");
	}
	
	public @NonNull Element fst(TokenExecutor exec) {
		throw builtInMethodError("fst");
	}
	
	public @NonNull Element snd(TokenExecutor exec) {
		throw builtInMethodError("snd");
	}
	
	public void reverse(TokenExecutor exec) {
		throw builtInMethodError("reverse");
	}
	
	public void sort(TokenExecutor exec) {
		throw builtInMethodError("sort");
	}
	
	public void sortBy(TokenExecutor exec, @NonNull Element elem) {
		throw builtInMethodError("sortBy");
	}
	
	public void shuffle(TokenExecutor exec) {
		throw builtInMethodError("shuffle");
	}
	
	public void putAll(TokenExecutor exec, @NonNull Element elem) {
		throw builtInMethodError("putAll");
	}
	
	public boolean containsKey(TokenExecutor exec, @NonNull Element elem) {
		throw builtInMethodError("containsKey");
	}
	
	public boolean containsValue(TokenExecutor exec, @NonNull Element elem) {
		throw builtInMethodError("containsValue");
	}
	
	public @NonNull Element keys(TokenExecutor exec) {
		throw builtInMethodError("keys");
	}
	
	public @NonNull Element values(TokenExecutor exec) {
		throw builtInMethodError("values");
	}
	
	public @NonNull Element collectString(TokenExecutor exec) {
		throw builtInMethodError("collectString");
	}
	
	public @NonNull Element collectList(TokenExecutor exec) {
		throw builtInMethodError("collectList");
	}
	
	public @NonNull Element collectSet(TokenExecutor exec) {
		throw builtInMethodError("collectSet");
	}
	
	public @NonNull Element collectDict(TokenExecutor exec) {
		throw builtInMethodError("collectDict");
	}
	
	public @NonNull Element stepBy(TokenExecutor exec, @NonNull Element elem) {
		throw builtInMethodError("stepBy");
	}
	
	public @NonNull Element chain(TokenExecutor exec, @NonNull Element elem) {
		throw builtInMethodError("chain");
	}
	
	public @NonNull Element zip(TokenExecutor exec, @NonNull Element elem) {
		throw builtInMethodError("zip");
	}
	
	public @NonNull Element map(TokenExecutor exec, @NonNull Element elem) {
		throw builtInMethodError("map");
	}
	
	public @NonNull Element filter(TokenExecutor exec, @NonNull Element elem) {
		throw builtInMethodError("filter");
	}
	
	public @NonNull Element filterMap(TokenExecutor exec, @NonNull Element elem) {
		throw builtInMethodError("filterMap");
	}
	
	public @NonNull Element enumerate(TokenExecutor exec) {
		throw builtInMethodError("enumerate");
	}
	
	public @NonNull Element takeWhile(TokenExecutor exec, @NonNull Element elem) {
		throw builtInMethodError("takeWhile");
	}
	
	public @NonNull Element mapWhile(TokenExecutor exec, @NonNull Element elem) {
		throw builtInMethodError("mapWhile");
	}
	
	public @NonNull Element skip(TokenExecutor exec, @NonNull Element elem) {
		throw builtInMethodError("skip");
	}
	
	public @NonNull Element take(TokenExecutor exec, @NonNull Element elem) {
		throw builtInMethodError("take");
	}
	
	public @NonNull Element flatMap(TokenExecutor exec, @NonNull Element elem) {
		throw builtInMethodError("flatMap");
	}
	
	public @NonNull Element flatten(TokenExecutor exec) {
		throw builtInMethodError("flatten");
	}
	
	public @NonNull Element chunks(TokenExecutor exec, @NonNull Element elem) {
		throw builtInMethodError("chunks");
	}
	
	public int count(TokenExecutor exec) {
		throw builtInMethodError("count");
	}
	
	public void forEach(TokenExecutor exec, @NonNull Element elem) {
		throw builtInMethodError("forEach");
	}
	
	public void into(TokenExecutor exec, @NonNull Element elem) {
		throw builtInMethodError("into");
	}
	
	public @NonNull Element fold(TokenExecutor exec, @NonNull Element elem0, @NonNull Element elem1) {
		throw builtInMethodError("fold");
	}
	
	public boolean all(TokenExecutor exec) {
		throw builtInMethodError("all");
	}
	
	public boolean any(TokenExecutor exec) {
		throw builtInMethodError("any");
	}
	
	public @NonNull Element min(TokenExecutor exec) {
		throw builtInMethodError("min");
	}
	
	public @NonNull Element max(TokenExecutor exec) {
		throw builtInMethodError("max");
	}
	
	public @NonNull Element sum(TokenExecutor exec) {
		throw builtInMethodError("sum");
	}
	
	public @NonNull Element product(TokenExecutor exec) {
		throw builtInMethodError("product");
	}
	
	public @NonNull Element scope(TokenExecutor exec) {
		throw builtInMethodError("scope");
	}
	
	public int compareTo(TokenExecutor exec, @NonNull Element elem) {
		onEqualTo(exec, elem);
		
		BoolElement result = exec.pop().asBool(exec);
		if (result == null) {
			throw new IllegalArgumentException(String.format("Element comparison requires binary operator \"==\" to return %s element!", BuiltIn.BOOL));
		}
		
		if (result.primitiveBool()) {
			return 0;
		}
		
		onLessThan(exec, elem);
		
		result = exec.pop().asBool(exec);
		if (result == null) {
			throw new IllegalArgumentException(String.format("Element comparison requires binary operator \"<\" to return %s element!", BuiltIn.BOOL));
		}
		
		return result.primitiveBool() ? -1 : 1;
	}
	
	public @NonNull String innerString(@Nullable TokenExecutor exec, @NonNull Element container) {
		return this == container ? "this" : (exec == null ? this : stringCast(exec)).toString();
	}
	
	protected RuntimeException builtInMethodArgumentError(String name, String type, int n) {
		String ordinal = n > 0 ? " " + Helpers.ordinal(n) : "";
		return new IllegalArgumentException(String.format("Built-in method \"%s\" requires %s element as%s argument!", name, type, ordinal));
	}
	
	public @NonNull IntElement methodInt(TokenExecutor exec, @NonNull Element elem, String name, String type, int n) {
		IntElement intElem = elem.asInt(exec);
		if (intElem == null) {
			throw builtInMethodArgumentError(name, type, n);
		}
		return intElem;
	}
	
	public @NonNull IntElement methodInt(TokenExecutor exec, @NonNull Element elem, String name, int n) {
		return methodInt(exec, elem, name, BuiltIn.INT, n);
	}
	
	public @NonNull IntElement methodInt(TokenExecutor exec, @NonNull Element elem, String name) {
		return methodInt(exec, elem, name, 0);
	}
	
	public int methodIndex(TokenExecutor exec, @NonNull Element elem, String name, String type, int n) {
		@NonNull IntElement intElem = methodInt(exec, elem, name, type, n);
		int primitiveInt = intElem.primitiveInt();
		if (primitiveInt < 0) {
			throw builtInMethodArgumentError(name, type, n);
		}
		return primitiveInt;
	}
	
	public int methodIndex(TokenExecutor exec, @NonNull Element elem, String name, int n) {
		return methodIndex(exec, elem, name, "non-negative " + BuiltIn.INT, n);
	}
	
	public int methodIndex(TokenExecutor exec, @NonNull Element elem, String name) {
		return methodIndex(exec, elem, name, 0);
	}
	
	public @NonNull Element clone(TokenExecutor exec) {
		return clone();
	}
	
	public int hash(TokenExecutor exec) {
		return hashCode();
	}
	
	protected RuntimeException magicMethodError(String name) {
		return new IllegalArgumentException(String.format("Magic method \"%s\" is undefined for argument type \"%s\"!", name, typeName()));
	}
	
	public @NonNull String debug(TokenExecutor exec) {
		return toString();
	}
	
	public @Nullable Scope getMemberLabelScope(@NonNull MemberAccessType access) {
		return access.equals(MemberAccessType.INSTANCE) ? clazz : null;
	}
	
	public @NonNull MemberAccessType getMemberLabelModifiedAccess(@NonNull MemberAccessType access) {
		return access;
	}
	
	public @Nullable TokenResult memberAccess(TokenExecutor exec, @NonNull String member, @NonNull MemberAccessType access) {
		if (access.equals(MemberAccessType.INSTANCE)) {
			exec.push(this);
			return clazz.scopeAction(exec, member);
		}
		else {
			return null;
		}
	}
	
	public RuntimeException memberAccessError(@NonNull String member, @NonNull MemberAccessType access) {
		return new IllegalArgumentException(String.format("%s member \"%s\" not defined!", access, access.nextIdentifier(this, member)));
	}
	
	public String scopeAccessIdentifier(@NonNull MemberAccessType access) {
		return access.equals(MemberAccessType.INSTANCE) ? clazz.fullIdentifier : toString();
	}
	
	public Object formatted(TokenExecutor exec) {
		return this;
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
	
	protected boolean objectEquals(Object obj) {
		return super.equals(obj);
	}
	
	@Override
	public @NonNull String toString() {
		return clazz.fullIdentifier + "@" + Integer.toString(objectHashCode(), 16);
	}
}
