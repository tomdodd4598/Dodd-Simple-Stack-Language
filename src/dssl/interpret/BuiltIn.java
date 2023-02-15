package dssl.interpret;

import java.util.*;
import java.util.function.Function;

import org.eclipse.jdt.annotation.*;

import dssl.interpret.element.*;
import dssl.interpret.element.container.*;
import dssl.interpret.element.primitive.*;

public class BuiltIn {
	
	public static final Map<@NonNull String, Clazz> CLAZZ_MAP = new HashMap<>();
	public static final Map<@NonNull String, Clazz> MODULE_MAP = new HashMap<>();
	
	public static final @NonNull Clazz OBJECT_CLAZZ = clazz(Clazz.objectClazz());
	
	public static final @NonNull Clazz CLASS_CLAZZ = clazz(new BuiltInClazz("class"));
	public static final @NonNull Clazz BLOCK_CLAZZ = clazz(new BuiltInClazz("{...}"));
	public static final @NonNull Clazz NATIVE_CLAZZ = clazz(new BuiltInClazz("native"));
	public static final @NonNull Clazz NULL_CLAZZ = clazz(new BuiltInClazz("null"));
	
	public static final @NonNull Clazz INT_CLAZZ = primitive(new BuiltInClazz("int") {
		
		@Override
		public @Nullable Element castImplicit(@NonNull Element elem) {
			return elem.intCast(false);
		}
		
		@Override
		public @NonNull Element castExplicit(@NonNull Element elem) {
			Element casted = elem.intCast(true);
			if (casted == null) {
				throw castError(elem);
			}
			return casted;
		}
	});
	
	public static final @NonNull Clazz BOOL_CLAZZ = primitive(new BuiltInClazz("bool") {
		
		@Override
		public @Nullable Element castImplicit(@NonNull Element elem) {
			return elem.boolCast(false);
		}
		
		@Override
		public @NonNull Element castExplicit(@NonNull Element elem) {
			Element casted = elem.boolCast(true);
			if (casted == null) {
				throw castError(elem);
			}
			return casted;
		}
	});
	
	public static final @NonNull Clazz FLOAT_CLAZZ = primitive(new BuiltInClazz("float") {
		
		@Override
		public @Nullable Element castImplicit(@NonNull Element elem) {
			return elem.floatCast(false);
		}
		
		@Override
		public @NonNull Element castExplicit(@NonNull Element elem) {
			Element casted = elem.floatCast(true);
			if (casted == null) {
				throw castError(elem);
			}
			return casted;
		}
	});
	
	public static final @NonNull Clazz CHAR_CLAZZ = primitive(new BuiltInClazz("char") {
		
		@Override
		public @Nullable Element castImplicit(@NonNull Element elem) {
			return elem.charCast(false);
		}
		
		@Override
		public @NonNull Element castExplicit(@NonNull Element elem) {
			Element casted = elem.charCast(true);
			if (casted == null) {
				throw castError(elem);
			}
			return casted;
		}
	});
	
	public static final @NonNull Clazz STRING_CLAZZ = primitive(new BuiltInClazz("string") {
		
		@Override
		public @Nullable Element castImplicit(@NonNull Element elem) {
			return elem.stringCast(false);
		}
		
		@Override
		public @NonNull Element castExplicit(@NonNull Element elem) {
			Element casted = elem.stringCast(true);
			if (casted == null) {
				throw castError(elem);
			}
			return casted;
		}
	});
	
	public static final @NonNull Clazz RANGE_CLAZZ = container(new BuiltInClazz("range") {
		
		@Override
		public @NonNull Element castExplicit(@NonNull Element elem) {
			Element casted = elem.rangeCast();
			if (casted == null) {
				throw castError(elem);
			}
			return casted;
		}
	}, RangeElement::new);
	
	public static final @NonNull Clazz LIST_CLAZZ = container(new BuiltInClazz("list") {
		
		@Override
		public @NonNull Element castExplicit(@NonNull Element elem) {
			Element casted = elem.listCast();
			if (casted == null) {
				throw castError(elem);
			}
			return casted;
		}
	}, ListElement::new);
	
	public static final @NonNull Clazz TUPLE_CLAZZ = container(new BuiltInClazz("tuple") {
		
		@Override
		public @NonNull Element castExplicit(@NonNull Element elem) {
			Element casted = elem.tupleCast();
			if (casted == null) {
				throw castError(elem);
			}
			return casted;
		}
	}, TupleElement::new);
	
	public static final @NonNull Clazz SET_CLAZZ = container(new BuiltInClazz("set") {
		
		@Override
		public @NonNull Element castExplicit(@NonNull Element elem) {
			Element casted = elem.setCast();
			if (casted == null) {
				throw castError(elem);
			}
			return casted;
		}
	}, SetElement::new);
	
	public static final @NonNull Clazz DICT_CLAZZ = container(new BuiltInClazz("dict") {
		
		@Override
		public @NonNull Element castExplicit(@NonNull Element elem) {
			Element casted = elem.dictCast();
			if (casted == null) {
				throw castError(elem);
			}
			return casted;
		}
	}, DictElement::new);
	
	static @NonNull Clazz clazz(@NonNull Clazz clazz) {
		CLAZZ_MAP.put(clazz.identifier, clazz);
		return clazz;
	}
	
	static @NonNull Clazz primitive(@NonNull Clazz clazz) {
		clazz.setMagic("init", x -> {
			Element casted = clazz.castImplicit(x.pop());
			if (casted == null) {
				throw new IllegalArgumentException(String.format("Initializer for type \"%1$s\" requires %1$s element as argument!", clazz.identifier));
			}
			x.push(casted);
			return TokenResult.PASS;
		});
		return clazz(clazz);
	}
	
	static @NonNull Clazz container(@NonNull Clazz clazz, Function<Collection<@NonNull Element>, @NonNull Element> constructor) {
		clazz.setMagic("init", x -> {
			Element elem = x.pop();
			if (!(elem instanceof RBracketElement)) {
				throw new IllegalArgumentException(String.format("Initializer for type \"%s\" requires RBracket element as last argument!", clazz.identifier));
			}
			x.push(constructor.apply(x.getElemsToLBracket()));
			return TokenResult.PASS;
		});
		return clazz(clazz);
	}
	
	static class BuiltInClazz extends Clazz {
		
		public BuiltInClazz(@NonNull String identifier) {
			super(identifier);
		}
		
		@Override
		public TokenResult instantiate(TokenExecutor exec) {
			getMagic("init").invokable.invoke(exec);
			return TokenResult.PASS;
		}
	}
	
	static {
		for (Clazz clazz : Arrays.asList(STRING_CLAZZ, RANGE_CLAZZ, LIST_CLAZZ, TUPLE_CLAZZ, SET_CLAZZ, DICT_CLAZZ)) {
			clazz.setMacro("unpack", x -> {
				x.pop().unpack(x);
				return TokenResult.PASS;
			});
			
			clazz.setMacro("size", x -> {
				x.push(new IntElement(x.pop().size()));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("isEmpty", x -> {
				x.push(new BoolElement(x.pop().isEmpty()));
				return TokenResult.PASS;
			});
		}
		
		for (Clazz clazz : Arrays.asList(STRING_CLAZZ, RANGE_CLAZZ, LIST_CLAZZ, TUPLE_CLAZZ, SET_CLAZZ)) {
			clazz.setMacro("contains", x -> {
				x.push(new BoolElement(x.pop().contains(x.pop())));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("containsAll", x -> {
				x.push(new BoolElement(x.pop().containsAll(x.pop())));
				return TokenResult.PASS;
			});
		}
		
		for (Clazz clazz : Arrays.asList(STRING_CLAZZ, RANGE_CLAZZ, LIST_CLAZZ, TUPLE_CLAZZ, DICT_CLAZZ)) {
			clazz.setMacro("get", x -> {
				x.push(x.pop().get(x.pop()));
				return TokenResult.PASS;
			});
		}
		
		for (Clazz clazz : Arrays.asList(LIST_CLAZZ, SET_CLAZZ, DICT_CLAZZ)) {
			clazz.setMacro("remove", x -> {
				x.pop().remove(x.pop());
				return TokenResult.PASS;
			});
			
			clazz.setMacro("removeAll", x -> {
				x.pop().removeAll(x.pop());
				return TokenResult.PASS;
			});
			
			clazz.setMacro("clear", x -> {
				x.pop().clear();
				return TokenResult.PASS;
			});
		}
		
		for (Clazz clazz : Arrays.asList(LIST_CLAZZ, SET_CLAZZ)) {
			clazz.setMacro("add", x -> {
				x.pop().add(x.pop());
				return TokenResult.PASS;
			});
			
			clazz.setMacro("addAll", x -> {
				x.pop().addAll(x.pop());
				return TokenResult.PASS;
			});
		}
		
		for (Clazz clazz : Arrays.asList(LIST_CLAZZ, DICT_CLAZZ)) {
			clazz.setMacro("put", x -> {
				@NonNull Element elem2 = x.pop(), elem1 = x.pop(), elem0 = x.pop();
				elem2.put(elem0, elem1);
				return TokenResult.PASS;
			});
		}
		
		for (Clazz clazz : Arrays.asList(DICT_CLAZZ)) {
			clazz.setMacro("putAll", x -> {
				x.pop().putAll(x.pop());
				return TokenResult.PASS;
			});
			
			clazz.setMacro("containsKey", x -> {
				x.push(new BoolElement(x.pop().containsKey(x.pop())));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("containsValue", x -> {
				x.push(new BoolElement(x.pop().containsValue(x.pop())));
				return TokenResult.PASS;
			});
			
			clazz.setMacro("keys", x -> {
				x.push(x.pop().keys());
				return TokenResult.PASS;
			});
			
			clazz.setMacro("values", x -> {
				x.push(x.pop().values());
				return TokenResult.PASS;
			});
		}
		
		for (Clazz clazz : Arrays.asList(OBJECT_CLAZZ)) {
			clazz.setMacro("clone", x -> {
				x.push(x.pop().clone());
				return TokenResult.PASS;
			});
			
			clazz.setMacro("hash", x -> {
				x.push(new IntElement(x.pop().hash()));
				return TokenResult.PASS;
			});
		}
	}
	
	static @NonNull Clazz module(@NonNull String identifier) {
		Clazz module = new Clazz(identifier);
		MODULE_MAP.put(identifier, module);
		return module;
	}
	
	public static final @NonNull Clazz MATH_MODULE = module("math");
	
	static {
		MATH_MODULE.setMacro("neg", TokenExecutor::neg);
		MATH_MODULE.setMacro("abs", TokenExecutor::abs);
		MATH_MODULE.setMacro("sgn", TokenExecutor::sgn);
		MATH_MODULE.setMacro("floor", TokenExecutor::floor);
		MATH_MODULE.setMacro("ceil", TokenExecutor::ceil);
		MATH_MODULE.setMacro("round", TokenExecutor::round);
		
		MATH_MODULE.setMacro("sin", TokenExecutor::sin);
		MATH_MODULE.setMacro("cos", TokenExecutor::cos);
		MATH_MODULE.setMacro("tan", TokenExecutor::tan);
		MATH_MODULE.setMacro("asin", TokenExecutor::asin);
		MATH_MODULE.setMacro("acos", TokenExecutor::acos);
		MATH_MODULE.setMacro("atan", TokenExecutor::atan);
		
		MATH_MODULE.setMacro("sinc", TokenExecutor::sinc);
		MATH_MODULE.setMacro("atan2", TokenExecutor::atan2);
		
		MATH_MODULE.setMacro("rads", TokenExecutor::rads);
		MATH_MODULE.setMacro("degs", TokenExecutor::degs);
		
		MATH_MODULE.setMacro("exp", TokenExecutor::exp);
		MATH_MODULE.setMacro("log", TokenExecutor::log);
		MATH_MODULE.setMacro("log10", TokenExecutor::log10);
		MATH_MODULE.setMacro("sqrt", TokenExecutor::sqrt);
		MATH_MODULE.setMacro("cbrt", TokenExecutor::cbrt);
		
		MATH_MODULE.setMacro("expm1", TokenExecutor::expm1);
		MATH_MODULE.setMacro("log1p", TokenExecutor::log1p);
		
		MATH_MODULE.setMacro("sinh", TokenExecutor::sinh);
		MATH_MODULE.setMacro("cosh", TokenExecutor::cosh);
		MATH_MODULE.setMacro("tanh", TokenExecutor::tanh);
		MATH_MODULE.setMacro("asinh", TokenExecutor::asinh);
		MATH_MODULE.setMacro("acosh", TokenExecutor::acosh);
		MATH_MODULE.setMacro("atanh", TokenExecutor::atanh);
		
		MATH_MODULE.setMacro("min", TokenExecutor::min);
		MATH_MODULE.setMacro("max", TokenExecutor::max);
	}
}
