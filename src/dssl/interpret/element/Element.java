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
		TokenResult magic = magicAction(exec, "str");
		if (magic != null) {
			return exec.pop().stringCast(exec);
		}
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
	
	public @NonNull TokenResult onEqualTo(TokenExecutor exec, @NonNull Element other) {
		TokenResult magic = magicAction(exec, "eq", other);
		if (magic != null) {
			return magic;
		}
		exec.push(new BoolElement(NullElement.INSTANCE.equals(other) ? false : equals(other)));
		return TokenResult.PASS;
	}
	
	public @NonNull TokenResult onNotEqualTo(TokenExecutor exec, @NonNull Element other) {
		TokenResult magic = magicAction(exec, "ne", other);
		if (magic != null) {
			return magic;
		}
		exec.push(new BoolElement(NullElement.INSTANCE.equals(other) ? true : !equals(other)));
		return TokenResult.PASS;
	}
	
	public @NonNull TokenResult onLessThan(TokenExecutor exec, @NonNull Element other) {
		TokenResult magic = magicAction(exec, "lt", other);
		if (magic != null) {
			return magic;
		}
		throw binaryOpError("<", other);
	}
	
	public @NonNull TokenResult onLessOrEqual(TokenExecutor exec, @NonNull Element other) {
		TokenResult magic = magicAction(exec, "le", other);
		if (magic != null) {
			return magic;
		}
		throw binaryOpError("<=", other);
	}
	
	public @NonNull TokenResult onMoreThan(TokenExecutor exec, @NonNull Element other) {
		TokenResult magic = magicAction(exec, "gt", other);
		if (magic != null) {
			return magic;
		}
		throw binaryOpError(">", other);
	}
	
	public @NonNull TokenResult onMoreOrEqual(TokenExecutor exec, @NonNull Element other) {
		TokenResult magic = magicAction(exec, "ge", other);
		if (magic != null) {
			return magic;
		}
		throw binaryOpError(">=", other);
	}
	
	public @NonNull TokenResult onPlus(TokenExecutor exec, @NonNull Element other) {
		TokenResult magic = magicAction(exec, "add", other);
		if (magic != null) {
			return magic;
		}
		throw binaryOpError("+", other);
	}
	
	public @NonNull TokenResult onAnd(TokenExecutor exec, @NonNull Element other) {
		TokenResult magic = magicAction(exec, "and", other);
		if (magic != null) {
			return magic;
		}
		throw binaryOpError("&", other);
	}
	
	public @NonNull TokenResult onOr(TokenExecutor exec, @NonNull Element other) {
		TokenResult magic = magicAction(exec, "or", other);
		if (magic != null) {
			return magic;
		}
		throw binaryOpError("|", other);
	}
	
	public @NonNull TokenResult onXor(TokenExecutor exec, @NonNull Element other) {
		TokenResult magic = magicAction(exec, "xor", other);
		if (magic != null) {
			return magic;
		}
		throw binaryOpError("^", other);
	}
	
	public @NonNull TokenResult onMinus(TokenExecutor exec, @NonNull Element other) {
		TokenResult magic = magicAction(exec, "sub", other);
		if (magic != null) {
			return magic;
		}
		throw binaryOpError("-", other);
	}
	
	public @NonNull TokenResult onConcat(TokenExecutor exec, @NonNull Element other) {
		TokenResult magic = magicAction(exec, "concat", other);
		if (magic != null) {
			return magic;
		}
		if (other instanceof StringElement) {
			exec.push(new StringElement(stringCast(exec).toString() + other));
			return TokenResult.PASS;
		}
		throw binaryOpError("~", other);
	}
	
	public @NonNull TokenResult onLeftShift(TokenExecutor exec, @NonNull Element other) {
		TokenResult magic = magicAction(exec, "lshift", other);
		if (magic != null) {
			return magic;
		}
		throw binaryOpError("<<", other);
	}
	
	public @NonNull TokenResult onRightShift(TokenExecutor exec, @NonNull Element other) {
		TokenResult magic = magicAction(exec, "rshift", other);
		if (magic != null) {
			return magic;
		}
		throw binaryOpError(">>", other);
	}
	
	public @NonNull TokenResult onMultiply(TokenExecutor exec, @NonNull Element other) {
		TokenResult magic = magicAction(exec, "mul", other);
		if (magic != null) {
			return magic;
		}
		throw binaryOpError("*", other);
	}
	
	public @NonNull TokenResult onDivide(TokenExecutor exec, @NonNull Element other) {
		TokenResult magic = magicAction(exec, "div", other);
		if (magic != null) {
			return magic;
		}
		throw binaryOpError("/", other);
	}
	
	public @NonNull TokenResult onRemainder(TokenExecutor exec, @NonNull Element other) {
		TokenResult magic = magicAction(exec, "rem", other);
		if (magic != null) {
			return magic;
		}
		throw binaryOpError("%", other);
	}
	
	public @NonNull TokenResult onPower(TokenExecutor exec, @NonNull Element other) {
		TokenResult magic = magicAction(exec, "pow", other);
		if (magic != null) {
			return magic;
		}
		throw binaryOpError("**", other);
	}
	
	public @NonNull TokenResult onIdivide(TokenExecutor exec, @NonNull Element other) {
		TokenResult magic = magicAction(exec, "floordiv", other);
		if (magic != null) {
			return magic;
		}
		throw binaryOpError("//", other);
	}
	
	public @NonNull TokenResult onModulo(TokenExecutor exec, @NonNull Element other) {
		TokenResult magic = magicAction(exec, "mod", other);
		if (magic != null) {
			return magic;
		}
		throw binaryOpError("%%", other);
	}
	
	protected RuntimeException unaryOpError(String operator) {
		return new IllegalArgumentException(String.format("Unary operator \"%s\" is undefined for argument type \"%s\"!", operator, typeName()));
	}
	
	public @NonNull TokenResult onNot(TokenExecutor exec) {
		TokenResult magic = magicAction(exec, "not");
		if (magic != null) {
			return magic;
		}
		throw unaryOpError("!");
	}
	
	protected RuntimeException builtInMethodError(String name) {
		return new IllegalArgumentException(String.format("Built-in method \"%s\" is undefined for argument type \"%s\"!", name, typeName()));
	}
	
	public void unpack(TokenExecutor exec) {
		throw builtInMethodError("unpack");
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
	
	public void push(TokenExecutor exec, @NonNull Element elem) {
		throw builtInMethodError("push");
	}
	
	public void insert(TokenExecutor exec, @NonNull Element elem0, @NonNull Element elem1) {
		throw builtInMethodError("insert");
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
	
	public void pushAll(TokenExecutor exec, @NonNull Element elem) {
		throw builtInMethodError("pushAll");
	}
	
	public void insertAll(TokenExecutor exec, @NonNull Element elem0, @NonNull Element elem1) {
		throw builtInMethodError("insertAll");
	}
	
	public void addAll(TokenExecutor exec, @NonNull Element elem) {
		throw builtInMethodError("addAll");
	}
	
	public void removeAll(TokenExecutor exec, @NonNull Element elem) {
		throw builtInMethodError("removeAll");
	}
	
	public @NonNull Element pop(TokenExecutor exec) {
		throw builtInMethodError("pop");
	}
	
	public void clear(TokenExecutor exec) {
		throw builtInMethodError("clear");
	}
	
	public @NonNull Element get(TokenExecutor exec, @NonNull Element elem) {
		throw builtInMethodError("get");
	}
	
	public void set(TokenExecutor exec, @NonNull Element elem0, @NonNull Element elem1) {
		throw builtInMethodError("set");
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
	
	public void removeValue(TokenExecutor exec, @NonNull Element elem) {
		throw builtInMethodError("removeValue");
	}
	
	public @NonNull Element fst(TokenExecutor exec) {
		throw builtInMethodError("fst");
	}
	
	public @NonNull Element snd(TokenExecutor exec) {
		throw builtInMethodError("snd");
	}
	
	public @NonNull Element last(TokenExecutor exec) {
		throw builtInMethodError("last");
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
	
	public void put(TokenExecutor exec, @NonNull Element elem0, @NonNull Element elem1) {
		throw builtInMethodError("put");
	}
	
	public void putAll(TokenExecutor exec, @NonNull Element elem) {
		throw builtInMethodError("putAll");
	}
	
	public void removeEntry(TokenExecutor exec, @NonNull Element elem0, @NonNull Element elem1) {
		throw builtInMethodError("removeEntry");
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
	
	public @NonNull Element entries(TokenExecutor exec) {
		throw builtInMethodError("entries");
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
	
	protected static final String NON_NEGATIVE_INT = "non-negative " + BuiltIn.INT;
	
	public int methodIndex(TokenExecutor exec, @NonNull Element elem, String name, int n) {
		@NonNull IntElement intElem = methodInt(exec, elem, name, NON_NEGATIVE_INT, n);
		int primitiveInt = intElem.primitiveInt();
		if (primitiveInt < 0) {
			throw builtInMethodArgumentError(name, NON_NEGATIVE_INT, n);
		}
		return primitiveInt;
	}
	
	public int methodIndex(TokenExecutor exec, @NonNull Element elem, String name) {
		return methodIndex(exec, elem, name, 0);
	}
	
	public long methodLongIndex(TokenExecutor exec, @NonNull Element elem, String name, int n) {
		@NonNull IntElement intElem = methodInt(exec, elem, name, NON_NEGATIVE_INT, n);
		long primitiveLong = intElem.primitiveLong();
		if (primitiveLong < 0) {
			throw builtInMethodArgumentError(name, NON_NEGATIVE_INT, n);
		}
		return primitiveLong;
	}
	
	public long methodLongIndex(TokenExecutor exec, @NonNull Element elem, String name) {
		return methodLongIndex(exec, elem, name, 0);
	}
	
	public @NonNull Element clone(TokenExecutor exec) {
		return clone();
	}
	
	public int hash(TokenExecutor exec) {
		return hashCode();
	}
	
	public @NonNull String debug(TokenExecutor exec) {
		TokenResult magic = magicAction(exec, "debug");
		if (magic != null) {
			return exec.pop().debug(exec);
		}
		return toString();
	}
	
	protected RuntimeException magicMethodError(String name) {
		return new IllegalArgumentException(String.format("Magic method \"%s\" is undefined for argument type \"%s\"!", name, typeName()));
	}
	
	public @Nullable Scope getMemberScope(@NonNull MemberAccessType access) {
		return access.equals(MemberAccessType.STATIC) ? null : clazz;
	}
	
	public @Nullable TokenResult memberAccess(TokenExecutor exec, @NonNull String member) {
		@Nullable Scope memberScope = getMemberScope(MemberAccessType.STATIC);
		if (memberScope != null) {
			TokenResult result = memberScope.scopeAction(exec, member);
			if (result != null) {
				return result;
			}
		}
		
		memberScope = getMemberScope(MemberAccessType.INSTANCE);
		if (memberScope != null) {
			exec.push(this);
			return memberScope.scopeAction(exec, member);
		}
		
		return null;
	}
	
	public @Nullable TokenResult magicAction(TokenExecutor exec, @NonNull String identifier, @NonNull Element... args) {
		@Nullable Scope memberScope = getMemberScope(MemberAccessType.STATIC);
		if (memberScope != null) {
			Magic magic = memberScope.getMagic(identifier);
			if (magic != null) {
				for (@NonNull Element arg : args) {
					exec.push(arg);
				}
				return magic.invokable.invoke(exec);
			}
		}
		
		memberScope = getMemberScope(MemberAccessType.INSTANCE);
		if (memberScope != null) {
			Magic magic = memberScope.getMagic(identifier);
			if (magic != null) {
				exec.push(this);
				for (@NonNull Element arg : args) {
					exec.push(arg);
				}
				return magic.invokable.invoke(exec);
			}
		}
		
		return null;
	}
	
	public @Nullable String memberAccessIdentifier(@NonNull MemberAccessType access) {
		@Nullable Scope memberScope = getMemberScope(access);
		if (memberScope != null) {
			return memberScope.getIdentifier();
		}
		else {
			return toString();
		}
	}
	
	public @NonNull String extendedIdentifier(@NonNull String extension, @NonNull MemberAccessType access) {
		return Helpers.extendedIdentifier(memberAccessIdentifier(access), extension);
	}
	
	public RuntimeException memberAccessError(@NonNull String member) {
		@NonNull String staticIdentifier = extendedIdentifier(member, MemberAccessType.STATIC);
		@NonNull String instanceIdentifier = extendedIdentifier(member, MemberAccessType.INSTANCE);
		
		String desc;
		if (staticIdentifier.equals(instanceIdentifier)) {
			desc = String.format("\"%s\"", staticIdentifier);
		}
		else {
			desc = String.format("\"%s\" or \"%s\"", staticIdentifier, instanceIdentifier);
		}
		
		return new IllegalArgumentException(String.format("Member %s not defined!", desc));
	}
	
	public Object formatted(TokenExecutor exec) {
		TokenResult magic = magicAction(exec, "str");
		if (magic != null) {
			return exec.pop().stringCast(exec);
		}
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
