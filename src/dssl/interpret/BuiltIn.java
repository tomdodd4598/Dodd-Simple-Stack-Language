package dssl.interpret;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

public class BuiltIn {
	
	public static final Map<@NonNull String, Clazz> CLAZZ_MAP = new HashMap<>();
	public static final Map<@NonNull String, Clazz> MODULE_MAP = new HashMap<>();
	
	public static final @NonNull Clazz INT_CLAZZ;
	public static final @NonNull Clazz BOOL_CLAZZ;
	public static final @NonNull Clazz FLOAT_CLAZZ;
	public static final @NonNull Clazz CHAR_CLAZZ;
	public static final @NonNull Clazz STRING_CLAZZ;
	
	public static final @NonNull Clazz RANGE_CLAZZ;
	public static final @NonNull Clazz LIST_CLAZZ;
	public static final @NonNull Clazz TUPLE_CLAZZ;
	public static final @NonNull Clazz SET_CLAZZ;
	public static final @NonNull Clazz DICT_CLAZZ;
	
	public static final @NonNull Clazz MATH_MODULE;
	
	static {
		INT_CLAZZ = clazz("int");
		BOOL_CLAZZ = clazz("bool");
		FLOAT_CLAZZ = clazz("float");
		CHAR_CLAZZ = clazz("char");
		STRING_CLAZZ = clazz("string");
		
		RANGE_CLAZZ = clazz("range");
		LIST_CLAZZ = clazz("list");
		TUPLE_CLAZZ = clazz("tuple");
		SET_CLAZZ = clazz("set");
		DICT_CLAZZ = clazz("dict");
		
		MATH_MODULE = module("math");
	}
	
	static @NonNull Clazz clazz(@NonNull String name) {
		Clazz clazz = new Clazz(name);
		CLAZZ_MAP.put(name, clazz);
		return clazz;
	}
	
	static @NonNull Clazz module(@NonNull String name) {
		Clazz module = new Clazz(name);
		MODULE_MAP.put(name, module);
		return module;
	}
	
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
