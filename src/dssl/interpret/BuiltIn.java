package dssl.interpret;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import dssl.Constants;
import dssl.interpret.element.Element;
import dssl.interpret.element.primitive.*;

public class BuiltIn {
	
	public static final String OBJECT = "Object";
	
	public static final String SCOPE = "Scope";
	public static final String CLASS = "Class";
	
	public static final String LABEL = "Label";
	
	public static final String ITERABLE = "Iterable";
	
	public static final String BLOCK = "Block";
	public static final String BRACKET = "Bracket";
	public static final String NATIVE = "Native";
	public static final String NULL = "Null";
	public static final String MODULE = "Module";
	public static final String ITER = "Iter";
	
	public static final String INT = "Int";
	public static final String BOOL = "Bool";
	public static final String FLOAT = "Float";
	public static final String CHAR = "Char";
	public static final String STRING = "String";
	
	public static final String RANGE = "Range";
	public static final String LIST = "List";
	public static final String SET = "Set";
	public static final String DICT = "Dict";
	
	public static final Map<@NonNull String, Clazz> CLAZZ_MAP = new HashMap<>();
	public static final Map<@NonNull String, Clazz> MODULE_MAP = new HashMap<>();
	
	public static final @NonNull Clazz OBJECT_CLAZZ = clazz(Clazz.objectClazz());
	
	public static final @NonNull Clazz SCOPE_CLAZZ = clazz(new Clazz(SCOPE, ClazzType.INTERNAL));
	public static final @NonNull Clazz CLASS_CLAZZ = clazz(new Clazz(CLASS, ClazzType.INTERNAL, SCOPE_CLAZZ));
	
	public static final @NonNull Clazz LABEL_CLAZZ = clazz(new Clazz(LABEL, ClazzType.INTERNAL));
	
	public static final @NonNull Clazz ITERABLE_CLAZZ = clazz(new Clazz(ITERABLE, ClazzType.INTERNAL));
	
	public static final @NonNull Clazz BLOCK_CLAZZ = clazz(new Clazz(BLOCK, ClazzType.INTERNAL));
	public static final @NonNull Clazz BRACKET_CLAZZ = clazz(new Clazz(BRACKET, ClazzType.INTERNAL));
	public static final @NonNull Clazz NATIVE_CLAZZ = clazz(new Clazz(NATIVE, ClazzType.INTERNAL));
	public static final @NonNull Clazz NULL_CLAZZ = clazz(new Clazz(NULL, ClazzType.INTERNAL));
	public static final @NonNull Clazz MODULE_CLAZZ = clazz(new Clazz(MODULE, ClazzType.INTERNAL));
	public static final @NonNull Clazz ITER_CLAZZ = clazz(new Clazz(ITER, ClazzType.INTERNAL));
	
	public static final @NonNull Clazz INT_CLAZZ = primitive(new Clazz(INT, ClazzType.FINAL) {
		
		@Override
		public @Nullable Element as(TokenExecutor exec, @NonNull Element elem) {
			return elem.asInt(exec);
		}
		
		@Override
		public @NonNull Element cast(TokenExecutor exec, @NonNull Element elem) {
			return elem.intCast(exec);
		}
	});
	
	public static final @NonNull Clazz BOOL_CLAZZ = primitive(new Clazz(BOOL, ClazzType.FINAL) {
		
		@Override
		public @Nullable Element as(TokenExecutor exec, @NonNull Element elem) {
			return elem.asBool(exec);
		}
		
		@Override
		public @NonNull Element cast(TokenExecutor exec, @NonNull Element elem) {
			return elem.boolCast(exec);
		}
	});
	
	public static final @NonNull Clazz FLOAT_CLAZZ = primitive(new Clazz(FLOAT, ClazzType.FINAL) {
		
		@Override
		public @Nullable Element as(TokenExecutor exec, @NonNull Element elem) {
			return elem.asFloat(exec);
		}
		
		@Override
		public @NonNull Element cast(TokenExecutor exec, @NonNull Element elem) {
			return elem.floatCast(exec);
		}
	});
	
	public static final @NonNull Clazz CHAR_CLAZZ = primitive(new Clazz(CHAR, ClazzType.FINAL) {
		
		@Override
		public @Nullable Element as(TokenExecutor exec, @NonNull Element elem) {
			return elem.asChar(exec);
		}
		
		@Override
		public @NonNull Element cast(TokenExecutor exec, @NonNull Element elem) {
			return elem.charCast(exec);
		}
	});
	
	public static final @NonNull Clazz STRING_CLAZZ = primitive(new Clazz(STRING, ClazzType.FINAL, ITERABLE_CLAZZ) {
		
		@Override
		public @Nullable Element as(TokenExecutor exec, @NonNull Element elem) {
			return elem.asString(exec);
		}
		
		@Override
		public @NonNull Element cast(TokenExecutor exec, @NonNull Element elem) {
			return elem.stringCast(exec);
		}
	});
	
	public static final @NonNull Clazz RANGE_CLAZZ = primitive(new Clazz(RANGE, ClazzType.FINAL, ITERABLE_CLAZZ) {
		
		@Override
		public @NonNull Element cast(TokenExecutor exec, @NonNull Element elem) {
			return elem.rangeCast(exec);
		}
	});
	
	public static final @NonNull Clazz LIST_CLAZZ = primitive(new Clazz(LIST, ClazzType.FINAL, ITERABLE_CLAZZ) {
		
		@Override
		public @NonNull Element cast(TokenExecutor exec, @NonNull Element elem) {
			return elem.listCast(exec);
		}
	});
	
	public static final @NonNull Clazz SET_CLAZZ = primitive(new Clazz(SET, ClazzType.FINAL, ITERABLE_CLAZZ) {
		
		@Override
		public @NonNull Element cast(TokenExecutor exec, @NonNull Element elem) {
			return elem.setCast(exec);
		}
	});
	
	public static final @NonNull Clazz DICT_CLAZZ = primitive(new Clazz(DICT, ClazzType.FINAL, ITERABLE_CLAZZ) {
		
		@Override
		public @NonNull Element cast(TokenExecutor exec, @NonNull Element elem) {
			return elem.dictCast(exec);
		}
	});
	
	static @NonNull Clazz clazz(@NonNull Clazz clazz) {
		CLAZZ_MAP.put(clazz.fullIdentifier, clazz);
		return clazz;
	}
	
	static @NonNull Clazz constructable(@NonNull Clazz clazz, @NonNull Invokable constructor) {
		clazz.setMagic("init", constructor);
		return clazz(clazz);
	}
	
	static @NonNull Clazz primitive(@NonNull Clazz clazz) {
		return constructable(clazz, x -> {
			Element casted = clazz.as(x, x.pop());
			if (casted == null) {
				throw new IllegalArgumentException(String.format("Constructor for type \"%1$s\" requires %1$s element as argument!", clazz.fullIdentifier));
			}
			x.push(casted);
			return TokenResult.PASS;
		});
	}
	
	static {
		for (Clazz clazz : Arrays.asList(STRING_CLAZZ, RANGE_CLAZZ, LIST_CLAZZ, SET_CLAZZ, DICT_CLAZZ)) {
			clazz.setMacro("unpack", x -> {
				x.pop().unpack(x);
				return TokenResult.PASS;
			});
			
			clazz.setMacro("size", x -> {
				x.push(new IntElement(x.pop().size(x)));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("isEmpty", x -> {
				x.push(new BoolElement(x.pop().isEmpty(x)));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("iter", x -> {
				x.push(x.pop().iter(x));
				return TokenResult.PASS;
			});
		}
		
		for (Clazz clazz : Arrays.asList(STRING_CLAZZ, RANGE_CLAZZ, LIST_CLAZZ, SET_CLAZZ)) {
			clazz.setMacro("contains", x -> {
				x.push(new BoolElement(x.pop().contains(x, x.pop())));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("containsAll", x -> {
				x.push(new BoolElement(x.pop().containsAll(x, x.pop())));
				return TokenResult.PASS;
			});
		}
		
		for (Clazz clazz : Arrays.asList(STRING_CLAZZ, RANGE_CLAZZ, LIST_CLAZZ, DICT_CLAZZ)) {
			clazz.setMacro("get", x -> {
				x.push(x.pop().get(x, x.pop()));
				return TokenResult.PASS;
			});
		}
		
		for (Clazz clazz : Arrays.asList(STRING_CLAZZ, RANGE_CLAZZ, LIST_CLAZZ)) {
			clazz.setMacro("slice", x -> {
				@NonNull Element elem2 = x.pop(), elem1 = x.pop(), elem0 = x.pop();
				x.push(elem2.slice(x, elem0, elem1));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("fst", x -> {
				x.push(x.pop().fst(x));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("snd", x -> {
				x.push(x.pop().snd(x));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("last", x -> {
				x.push(x.pop().last(x));
				return TokenResult.PASS;
			});
		}
		
		for (Clazz clazz : Arrays.asList(STRING_CLAZZ)) {
			clazz.setMacro("startsWith", x -> {
				x.push(x.pop().startsWith(x, x.pop()));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("endsWith", x -> {
				x.push(x.pop().endsWith(x, x.pop()));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("matches", x -> {
				x.push(x.pop().matches(x, x.pop()));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("replace", x -> {
				@NonNull Element elem2 = x.pop(), elem1 = x.pop(), elem0 = x.pop();
				x.push(elem2.replace(x, elem0, elem1));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("split", x -> {
				x.push(x.pop().split(x, x.pop()));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("lower", x -> {
				x.push(x.pop().lower(x));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("upper", x -> {
				x.push(x.pop().upper(x));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("trim", x -> {
				x.push(x.pop().trim(x));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("format", x -> {
				x.push(x.pop().format(x, x.pop()));
				return TokenResult.PASS;
			});
		}
		
		for (Clazz clazz : Arrays.asList(LIST_CLAZZ, SET_CLAZZ, DICT_CLAZZ)) {
			clazz.setMacro("remove", x -> {
				x.pop().remove(x, x.pop());
				return TokenResult.PASS;
			});
			
			clazz.setMacro("removeAll", x -> {
				x.pop().removeAll(x, x.pop());
				return TokenResult.PASS;
			});
			
			clazz.setMacro("clear", x -> {
				x.pop().clear(x);
				return TokenResult.PASS;
			});
		}
		
		for (Clazz clazz : Arrays.asList(LIST_CLAZZ)) {
			clazz.setMacro("push", x -> {
				x.pop().push(x, x.pop());
				return TokenResult.PASS;
			});
			
			clazz.setMacro("insert", x -> {
				@NonNull Element elem2 = x.pop(), elem1 = x.pop(), elem0 = x.pop();
				elem2.insert(x, elem0, elem1);
				return TokenResult.PASS;
			});
			
			clazz.setMacro("pushAll", x -> {
				x.pop().pushAll(x, x.pop());
				return TokenResult.PASS;
			});
			
			clazz.setMacro("insertAll", x -> {
				@NonNull Element elem2 = x.pop(), elem1 = x.pop(), elem0 = x.pop();
				elem2.insertAll(x, elem0, elem1);
				return TokenResult.PASS;
			});
			
			clazz.setMacro("pop", x -> {
				x.push(x.pop().pop(x));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("set", x -> {
				@NonNull Element elem2 = x.pop(), elem1 = x.pop(), elem0 = x.pop();
				elem2.set(x, elem0, elem1);
				return TokenResult.PASS;
			});
			
			clazz.setMacro("removeValue", x -> {
				x.pop().removeValue(x, x.pop());
				return TokenResult.PASS;
			});
			
			clazz.setMacro("reverse", x -> {
				x.pop().reverse(x);
				return TokenResult.PASS;
			});
			
			clazz.setMacro("sort", x -> {
				x.pop().sort(x);
				return TokenResult.PASS;
			});
			
			clazz.setMacro("sortBy", x -> {
				x.pop().sortBy(x, x.pop());
				return TokenResult.PASS;
			});
			
			clazz.setMacro("shuffle", x -> {
				x.pop().shuffle(x);
				return TokenResult.PASS;
			});
		}
		
		for (Clazz clazz : Arrays.asList(SET_CLAZZ)) {
			clazz.setMacro("add", x -> {
				x.pop().add(x, x.pop());
				return TokenResult.PASS;
			});
			
			clazz.setMacro("addAll", x -> {
				x.pop().addAll(x, x.pop());
				return TokenResult.PASS;
			});
		}
		
		for (Clazz clazz : Arrays.asList(DICT_CLAZZ)) {
			clazz.setMacro("put", x -> {
				@NonNull Element elem2 = x.pop(), elem1 = x.pop(), elem0 = x.pop();
				elem2.put(x, elem0, elem1);
				return TokenResult.PASS;
			});
			
			clazz.setMacro("putAll", x -> {
				x.pop().putAll(x, x.pop());
				return TokenResult.PASS;
			});
			
			clazz.setMacro("removeEntry", x -> {
				@NonNull Element elem2 = x.pop(), elem1 = x.pop(), elem0 = x.pop();
				elem2.removeEntry(x, elem0, elem1);
				return TokenResult.PASS;
			});
			
			clazz.setMacro("containsKey", x -> {
				x.push(new BoolElement(x.pop().containsKey(x, x.pop())));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("containsValue", x -> {
				x.push(new BoolElement(x.pop().containsValue(x, x.pop())));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("keys", x -> {
				x.push(x.pop().keys(x));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("values", x -> {
				x.push(x.pop().values(x));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("entries", x -> {
				x.push(x.pop().entries(x));
				return TokenResult.PASS;
			});
		}
		
		for (Clazz clazz : Arrays.asList(ITER_CLAZZ)) {
			clazz.setMacro("collectString", x -> {
				x.push(x.pop().collectString(x));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("collectList", x -> {
				x.push(x.pop().collectList(x));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("collectSet", x -> {
				x.push(x.pop().collectSet(x));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("collectDict", x -> {
				x.push(x.pop().collectDict(x));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("stepBy", x -> {
				x.push(x.pop().stepBy(x, x.pop()));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("chain", x -> {
				x.push(x.pop().chain(x, x.pop()));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("zip", x -> {
				x.push(x.pop().zip(x, x.pop()));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("map", x -> {
				x.push(x.pop().map(x, x.pop()));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("filter", x -> {
				x.push(x.pop().filter(x, x.pop()));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("filterMap", x -> {
				x.push(x.pop().filterMap(x, x.pop()));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("enumerate", x -> {
				x.push(x.pop().enumerate(x));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("takeWhile", x -> {
				x.push(x.pop().takeWhile(x, x.pop()));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("mapWhile", x -> {
				x.push(x.pop().mapWhile(x, x.pop()));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("skip", x -> {
				x.push(x.pop().skip(x, x.pop()));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("take", x -> {
				x.push(x.pop().take(x, x.pop()));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("flatMap", x -> {
				x.push(x.pop().flatMap(x, x.pop()));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("flatten", x -> {
				x.push(x.pop().flatten(x));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("chunks", x -> {
				x.push(x.pop().chunks(x, x.pop()));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("count", x -> {
				x.push(new IntElement(x.pop().count(x)));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("forEach", x -> {
				x.pop().forEach(x, x.pop());
				return TokenResult.PASS;
			});
			
			clazz.setMacro("into", x -> {
				x.pop().into(x, x.pop());
				return TokenResult.PASS;
			});
			
			clazz.setMacro("fold", x -> {
				@NonNull Element elem2 = x.pop(), elem1 = x.pop(), elem0 = x.pop();
				x.push(elem2.fold(x, elem0, elem1));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("all", x -> {
				x.push(new BoolElement(x.pop().all(x)));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("any", x -> {
				x.push(new BoolElement(x.pop().any(x)));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("min", x -> {
				x.push(x.pop().min(x));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("max", x -> {
				x.push(x.pop().max(x));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("sum", x -> {
				x.push(x.pop().sum(x));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("product", x -> {
				x.push(x.pop().product(x));
				return TokenResult.PASS;
			});
		}
		
		for (Clazz clazz : Arrays.asList(OBJECT_CLAZZ)) {
			clazz.setMacro("clone", x -> {
				x.push(x.pop().clone(x));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("hash", x -> {
				x.push(new IntElement(x.pop().hash(x)));
				return TokenResult.PASS;
			});
		}
		
		for (Clazz clazz : Arrays.asList(SCOPE_CLAZZ)) {
			clazz.setMacro("scope", x -> {
				x.push(x.pop().scope(x));
				return TokenResult.PASS;
			});
		}
	}
	
	static @NonNull Clazz module(@NonNull String identifier) {
		Clazz module = new Clazz(identifier, ClazzType.INTERNAL, SCOPE_CLAZZ);
		MODULE_MAP.put(identifier, module);
		return module;
	}
	
	public static final @NonNull Clazz MATH_MODULE = module("math");
	
	static {
		MATH_MODULE.setMacro("finite", TokenExecutor::finite);
		MATH_MODULE.setMacro("infinite", TokenExecutor::infinite);
		
		MATH_MODULE.setMacro("inv", TokenExecutor::inv);
		MATH_MODULE.setMacro("neg", TokenExecutor::neg);
		MATH_MODULE.setMacro("abs", TokenExecutor::abs);
		MATH_MODULE.setMacro("sgn", TokenExecutor::sgn);
		
		MATH_MODULE.setMacro("floor", TokenExecutor::floor);
		MATH_MODULE.setMacro("ceil", TokenExecutor::ceil);
		MATH_MODULE.setMacro("trunc", TokenExecutor::trunc);
		MATH_MODULE.setMacro("fract", TokenExecutor::fract);
		
		MATH_MODULE.setMacro("round", TokenExecutor::round);
		MATH_MODULE.setMacro("places", TokenExecutor::places);
		
		MATH_MODULE.setMacro("sin", TokenExecutor::sin);
		MATH_MODULE.setMacro("cos", TokenExecutor::cos);
		MATH_MODULE.setMacro("tan", TokenExecutor::tan);
		MATH_MODULE.setMacro("asin", TokenExecutor::asin);
		MATH_MODULE.setMacro("acos", TokenExecutor::acos);
		MATH_MODULE.setMacro("atan", TokenExecutor::atan);
		
		MATH_MODULE.setMacro("sinc", TokenExecutor::sinc);
		MATH_MODULE.setMacro("atan2", TokenExecutor::atan2);
		
		MATH_MODULE.setMacro("hypot", TokenExecutor::hypot);
		
		MATH_MODULE.setMacro("rads", TokenExecutor::rads);
		MATH_MODULE.setMacro("degs", TokenExecutor::degs);
		
		MATH_MODULE.setMacro("exp", TokenExecutor::exp);
		MATH_MODULE.setMacro("ln", TokenExecutor::ln);
		MATH_MODULE.setMacro("log2", TokenExecutor::log2);
		MATH_MODULE.setMacro("log10", TokenExecutor::log10);
		MATH_MODULE.setMacro("log", TokenExecutor::log);
		
		MATH_MODULE.setMacro("expm1", TokenExecutor::expm1);
		MATH_MODULE.setMacro("ln1p", TokenExecutor::ln1p);
		
		MATH_MODULE.setMacro("sqrt", TokenExecutor::sqrt);
		MATH_MODULE.setMacro("cbrt", TokenExecutor::cbrt);
		MATH_MODULE.setMacro("root", TokenExecutor::root);
		
		MATH_MODULE.setMacro("isqrt", TokenExecutor::isqrt);
		MATH_MODULE.setMacro("icbrt", TokenExecutor::icbrt);
		MATH_MODULE.setMacro("iroot", TokenExecutor::iroot);
		
		MATH_MODULE.setMacro("sinh", TokenExecutor::sinh);
		MATH_MODULE.setMacro("cosh", TokenExecutor::cosh);
		MATH_MODULE.setMacro("tanh", TokenExecutor::tanh);
		MATH_MODULE.setMacro("asinh", TokenExecutor::asinh);
		MATH_MODULE.setMacro("acosh", TokenExecutor::acosh);
		MATH_MODULE.setMacro("atanh", TokenExecutor::atanh);
		
		MATH_MODULE.setMacro("min", TokenExecutor::min);
		MATH_MODULE.setMacro("max", TokenExecutor::max);
		MATH_MODULE.setMacro("clamp", TokenExecutor::clamp);
		
		MATH_MODULE.setMacro("clamp8", TokenExecutor::clamp8);
		MATH_MODULE.setMacro("clamp16", TokenExecutor::clamp16);
		MATH_MODULE.setMacro("clamp32", TokenExecutor::clamp32);
		MATH_MODULE.setMacro("clamp64", TokenExecutor::clamp64);
	}
	
	public static final @NonNull Clazz CONSTS_MODULE = module("consts");
	
	static {
		CONSTS_MODULE.setDef("MIN_INT_8", new IntElement(Constants.MIN_INT_8), true);
		CONSTS_MODULE.setDef("MAX_INT_8", new IntElement(Constants.MAX_INT_8), true);
		
		CONSTS_MODULE.setDef("MIN_INT_16", new IntElement(Constants.MIN_INT_16), true);
		CONSTS_MODULE.setDef("MAX_INT_16", new IntElement(Constants.MAX_INT_16), true);
		
		CONSTS_MODULE.setDef("MIN_INT_32", new IntElement(Constants.MIN_INT_32), true);
		CONSTS_MODULE.setDef("MAX_INT_32", new IntElement(Constants.MAX_INT_32), true);
		
		CONSTS_MODULE.setDef("MIN_INT_64", new IntElement(Constants.MIN_INT_64), true);
		CONSTS_MODULE.setDef("MAX_INT_64", new IntElement(Constants.MAX_INT_64), true);
		
		CONSTS_MODULE.setDef("INF", new FloatElement(Constants.INF), true);
		CONSTS_MODULE.setDef("NAN", new FloatElement(Constants.NAN), true);
		CONSTS_MODULE.setDef("MIN_F", new FloatElement(Constants.MIN_F), true);
		CONSTS_MODULE.setDef("MAX_F", new FloatElement(Constants.MAX_F), true);
		CONSTS_MODULE.setDef("EPSILON", new FloatElement(Constants.EPSILON), true);
		
		CONSTS_MODULE.setDef("E", new FloatElement(Constants.E), true);
		
		CONSTS_MODULE.setDef("PI", new FloatElement(Constants.PI), true);
		CONSTS_MODULE.setDef("TAU", new FloatElement(Constants.TAU), true);
		
		CONSTS_MODULE.setDef("INV_PI", new FloatElement(Constants.INV_PI), true);
		CONSTS_MODULE.setDef("INV_TAU", new FloatElement(Constants.INV_TAU), true);
		
		CONSTS_MODULE.setDef("SQRT_PI", new FloatElement(Constants.SQRT_PI), true);
		CONSTS_MODULE.setDef("SQRT_TAU", new FloatElement(Constants.SQRT_TAU), true);
		
		CONSTS_MODULE.setDef("INV_SQRT_PI", new FloatElement(Constants.INV_SQRT_PI), true);
		CONSTS_MODULE.setDef("INV_SQRT_TAU", new FloatElement(Constants.INV_SQRT_TAU), true);
		
		CONSTS_MODULE.setDef("INV_3", new FloatElement(Constants.INV_3), true);
		CONSTS_MODULE.setDef("INV_6", new FloatElement(Constants.INV_6), true);
		
		CONSTS_MODULE.setDef("SQRT_2", new FloatElement(Constants.SQRT_2), true);
		CONSTS_MODULE.setDef("SQRT_3", new FloatElement(Constants.SQRT_3), true);
		CONSTS_MODULE.setDef("SQRT_6", new FloatElement(Constants.SQRT_6), true);
		CONSTS_MODULE.setDef("SQRT_8", new FloatElement(Constants.SQRT_8), true);
		
		CONSTS_MODULE.setDef("INV_SQRT_2", new FloatElement(Constants.INV_SQRT_2), true);
		CONSTS_MODULE.setDef("INV_SQRT_3", new FloatElement(Constants.INV_SQRT_3), true);
		CONSTS_MODULE.setDef("INV_SQRT_6", new FloatElement(Constants.INV_SQRT_6), true);
		CONSTS_MODULE.setDef("INV_SQRT_8", new FloatElement(Constants.INV_SQRT_8), true);
		
		CONSTS_MODULE.setDef("FRAC_PI_2", new FloatElement(Constants.FRAC_PI_2), true);
		CONSTS_MODULE.setDef("FRAC_PI_3", new FloatElement(Constants.FRAC_PI_3), true);
		CONSTS_MODULE.setDef("FRAC_PI_4", new FloatElement(Constants.FRAC_PI_4), true);
		CONSTS_MODULE.setDef("FRAC_PI_6", new FloatElement(Constants.FRAC_PI_6), true);
		CONSTS_MODULE.setDef("FRAC_PI_8", new FloatElement(Constants.FRAC_PI_8), true);
		
		CONSTS_MODULE.setDef("FRAC_2_PI", new FloatElement(Constants.FRAC_2_PI), true);
		CONSTS_MODULE.setDef("FRAC_2_SQRT_PI", new FloatElement(Constants.FRAC_2_SQRT_PI), true);
		
		CONSTS_MODULE.setDef("LN_2", new FloatElement(Constants.LN_2), true);
		CONSTS_MODULE.setDef("LN_10", new FloatElement(Constants.LN_10), true);
		CONSTS_MODULE.setDef("LOG2_E", new FloatElement(Constants.LOG2_E), true);
		CONSTS_MODULE.setDef("LOG2_10", new FloatElement(Constants.LOG2_10), true);
		CONSTS_MODULE.setDef("LOG10_E", new FloatElement(Constants.LOG10_E), true);
		CONSTS_MODULE.setDef("LOG10_2", new FloatElement(Constants.LOG10_2), true);
	}
	
	public static final @NonNull Clazz ENV_MODULE = module("env");
	
	static {
		ENV_MODULE.setMacro("args", TokenExecutor::args);
		
		ENV_MODULE.setMacro("rootPath", TokenExecutor::rootPath);
		ENV_MODULE.setMacro("rootDir", TokenExecutor::rootDir);
		
		ENV_MODULE.setMacro("fromRoot", TokenExecutor::fromRoot);
	}
	
	public static final @NonNull Clazz FS_MODULE = module("fs");
	
	static {
		FS_MODULE.setMacro("readFile", TokenExecutor::readFile);
		FS_MODULE.setMacro("writeFile", TokenExecutor::writeFile);
		
		FS_MODULE.setMacro("readLines", TokenExecutor::readLines);
		FS_MODULE.setMacro("writeLines", TokenExecutor::writeLines);
	}
	
	public static final Set<String> KEYWORDS = new HashSet<>();
	
	static {
		KEYWORDS.add("{");
		KEYWORDS.add("}");
		
		KEYWORDS.add("[|");
		KEYWORDS.add("|]");
		
		KEYWORDS.add("(|");
		KEYWORDS.add("|)");
		
		KEYWORDS.add("[");
		KEYWORDS.add("]");
		
		KEYWORDS.add("(");
		KEYWORDS.add(")");
		
		KEYWORDS.add("include");
		KEYWORDS.add("import");
		
		KEYWORDS.add("native");
		
		KEYWORDS.add("def");
		KEYWORDS.add("macro");
		KEYWORDS.add("class");
		KEYWORDS.add("magic");
		
		KEYWORDS.add("new");
		
		KEYWORDS.add("deref");
		
		KEYWORDS.add("null");
		KEYWORDS.add("type");
		KEYWORDS.add("cast");
		KEYWORDS.add("is");
		
		KEYWORDS.add("exch");
		KEYWORDS.add("roll");
		KEYWORDS.add("pop");
		KEYWORDS.add("dup");
		
		KEYWORDS.add("stacksize");
		
		KEYWORDS.add("read");
		KEYWORDS.add("print");
		KEYWORDS.add("println");
		KEYWORDS.add("interpret");
		
		KEYWORDS.add("exec");
		KEYWORDS.add("if");
		KEYWORDS.add("ifelse");
		KEYWORDS.add("loop");
		KEYWORDS.add("repeat");
		KEYWORDS.add("foreach");
		
		KEYWORDS.add("continue");
		KEYWORDS.add("break");
		KEYWORDS.add("quit");
		
		KEYWORDS.add("=");
		
		KEYWORDS.add("++");
		KEYWORDS.add("--");
		
		KEYWORDS.add("+=");
		KEYWORDS.add("&=");
		KEYWORDS.add("|=");
		KEYWORDS.add("^=");
		KEYWORDS.add("-=");
		KEYWORDS.add("~=");
		
		KEYWORDS.add("<<=");
		KEYWORDS.add(">>=");
		
		KEYWORDS.add("*=");
		KEYWORDS.add("/=");
		KEYWORDS.add("%=");
		KEYWORDS.add("**=");
		KEYWORDS.add("//=");
		KEYWORDS.add("%%=");
		
		KEYWORDS.add("==");
		KEYWORDS.add("!=");
		
		KEYWORDS.add("<");
		KEYWORDS.add("<=");
		KEYWORDS.add(">");
		KEYWORDS.add(">=");
		
		KEYWORDS.add("+");
		KEYWORDS.add("&");
		KEYWORDS.add("|");
		KEYWORDS.add("^");
		KEYWORDS.add("-");
		KEYWORDS.add("~");
		
		KEYWORDS.add("<<");
		KEYWORDS.add(">>");
		
		KEYWORDS.add("*");
		KEYWORDS.add("/");
		KEYWORDS.add("%");
		KEYWORDS.add("**");
		KEYWORDS.add("//");
		KEYWORDS.add("%%");
		
		KEYWORDS.add("!");
	}
}
