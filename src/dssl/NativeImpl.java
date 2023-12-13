package dssl;

import java.lang.reflect.*;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.stream.*;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.*;
import dssl.interpret.element.primitive.*;
import sun.reflect.generics.reflectiveObjects.*;

public class NativeImpl {
	
	public static final NativeImpl INSTANCE = new NativeImpl();
	
	private NativeImpl() {
		
	}
	
	public @NonNull TokenResult onNative(TokenExecutor exec) {
		@NonNull Element elem = exec.pop();
		StringElement stringElem = elem.asString(exec);
		if (stringElem == null) {
			throw new IllegalArgumentException(String.format("Keyword \"native\" requires %s element as last argument!", BuiltIn.STRING));
		}
		
		String str = stringElem.toString(exec);
		int index = str.lastIndexOf('.');
		List<Exception> exceptions = new ArrayList<>();
		try {
			TokenResult result;
			Class<?> clazz = Class.forName(str.substring(0, index));
			String member = str.substring(1 + index);
			List<Executable> executables = sortedExecutables(clazz.getMethods(), member);
			
			if ((result = tryExecutable(exec, clazz, executables, true, exceptions)) != null) {
				return result;
			}
			else if ((result = tryField(exec, clazz, member, true, exceptions)) != null) {
				return result;
			}
			else {
				if (member.equals("new")) {
					executables = sortedExecutables(clazz.getConstructors(), null);
				}
				
				if ((result = tryExecutable(exec, clazz, executables, false, exceptions)) != null) {
					return result;
				}
				else if ((result = tryField(exec, clazz, member, false, exceptions)) != null) {
					return result;
				}
			}
		}
		catch (Exception e) {
			exceptions.add(e);
		}
		
		for (Exception e : exceptions) {
			e.printStackTrace();
		}
		throw new IllegalArgumentException(String.format("Keyword \"native\" with last argument \"%s\" threw exceptions!", str));
	}
	
	static <T extends Executable> List<Executable> sortedExecutables(T[] array, String member) {
		Stream<T> stream = Arrays.stream(array);
		if (member != null) {
			stream = stream.filter(x -> x.getName().equals(member));
		}
		return stream.sorted(NativeImpl::compareExecutables).collect(Collectors.toList());
	}
	
	static TokenResult tryField(TokenExecutor exec, Class<?> clazz, String member, boolean isInst, List<Exception> exceptions) {
		try {
			Field field = clazz.getField(member);
			Object instance = isInst ? nativize(exec.peek(), clazz) : null;
			if (isInst && instance == null) {
				return null;
			}
			Object result = field.get(instance);
			if (isInst) {
				exec.pop();
			}
			exec.push(convert(exec, result));
			return TokenResult.PASS;
		}
		catch (Exception e) {
			exceptions.add(e);
		}
		return null;
	}
	
	static TokenResult tryExecutable(TokenExecutor exec, Class<?> clazz, List<Executable> executables, boolean isInst, List<Exception> exceptions) {
		outer: for (Executable executable : executables) {
			try {
				int params = executable.getParameterCount();
				int count = params + (isInst ? 1 : 0);
				if (count > exec.stackSize()) {
					continue;
				}
				Object instance = isInst ? nativize(exec.peek(), clazz) : null;
				if (isInst && instance == null) {
					continue;
				}
				Object[] args = new Object[params];
				@NonNull Element[] elems = exec.peek(count);
				Type[] types = executable.getGenericParameterTypes();
				
				for (int i = 0; i < params; ++i) {
					Object obj = nativize(elems[i], types[i]);
					if (obj == null && !exec.interpreter.builtIn.nullElement.equals(elems[i])) {
						continue outer;
					}
					else {
						args[i] = obj;
					}
				}
				
				if (args != null) {
					Object result;
					if (executable instanceof Method) {
						result = ((Method) executable).invoke(instance, args);
					}
					else {
						result = ((Constructor<?>) executable).newInstance(args);
					}
					exec.pop(count);
					if (result != null) {
						exec.push(convert(exec, result));
					}
					return TokenResult.PASS;
				}
			}
			catch (Exception e) {
				exceptions.add(e);
			}
		}
		return null;
	}
	
	static final Map<Class<?>, Integer> NATIVE_PARAMETER_PRIORITY_MAP = new HashMap<>();
	
	static {
		NATIVE_PARAMETER_PRIORITY_MAP.put(Byte.class, 1);
		NATIVE_PARAMETER_PRIORITY_MAP.put(Short.class, 2);
		NATIVE_PARAMETER_PRIORITY_MAP.put(Integer.class, 3);
		NATIVE_PARAMETER_PRIORITY_MAP.put(Long.class, 4);
		NATIVE_PARAMETER_PRIORITY_MAP.put(byte.class, 5);
		NATIVE_PARAMETER_PRIORITY_MAP.put(short.class, 6);
		NATIVE_PARAMETER_PRIORITY_MAP.put(int.class, 7);
		NATIVE_PARAMETER_PRIORITY_MAP.put(long.class, 8);
		NATIVE_PARAMETER_PRIORITY_MAP.put(BigInteger.class, 9);
		
		NATIVE_PARAMETER_PRIORITY_MAP.put(Boolean.class, 1);
		NATIVE_PARAMETER_PRIORITY_MAP.put(boolean.class, 2);
		
		NATIVE_PARAMETER_PRIORITY_MAP.put(Float.class, 1);
		NATIVE_PARAMETER_PRIORITY_MAP.put(Double.class, 2);
		NATIVE_PARAMETER_PRIORITY_MAP.put(float.class, 3);
		NATIVE_PARAMETER_PRIORITY_MAP.put(double.class, 4);
		
		NATIVE_PARAMETER_PRIORITY_MAP.put(Character.class, 1);
		NATIVE_PARAMETER_PRIORITY_MAP.put(char.class, 2);
	}
	
	static int compareExecutables(Executable a, Executable b) {
		int aCount = a.getParameterCount(), bCount = b.getParameterCount();
		int diff = bCount - aCount;
		if (diff != 0) {
			return diff;
		}
		
		Class<?>[] aTypes = a.getParameterTypes(), bTypes = b.getParameterTypes();
		for (int i = 0; i < aCount; ++i) {
			diff = NATIVE_PARAMETER_PRIORITY_MAP.getOrDefault(bTypes[i], 0) - NATIVE_PARAMETER_PRIORITY_MAP.getOrDefault(aTypes[i], 0);
			if (diff != 0) {
				return diff;
			}
		}
		
		return 0;
	}
	
	static Object nativize(@NonNull Element elem, Type type) {
		if (elem.interpreter.builtIn.nullElement.equals(elem)) {
			return null;
		}
		else if (type instanceof Class) {
			Class<?> clazz = (Class<?>) type;
			if (elem instanceof IntElement) {
				BigInteger val = ((IntElement) elem).value.raw;
				long longVal;
				int intVal;
				short shortVal;
				byte byteVal;
				if (clazz.isInstance(val)) {
					return val;
				}
				else if (clazz.isInstance(longVal = val.longValue()) || clazz.equals(long.class)) {
					return longVal;
				}
				else if (clazz.isInstance(intVal = val.intValue()) || clazz.equals(int.class)) {
					return intVal;
				}
				else if (clazz.isInstance(shortVal = val.shortValue()) || clazz.equals(short.class)) {
					return shortVal;
				}
				else if (clazz.isInstance(byteVal = val.byteValue()) || clazz.equals(byte.class)) {
					return byteVal;
				}
			}
			else if (elem instanceof BoolElement) {
				boolean val = ((BoolElement) elem).value.raw;
				if (clazz.isInstance(val) || clazz.equals(boolean.class)) {
					return val;
				}
			}
			else if (elem instanceof FloatElement) {
				double val = ((FloatElement) elem).value.raw;
				float floatVal;
				if (clazz.isInstance(val) || clazz.equals(double.class)) {
					return val;
				}
				else if (clazz.isInstance(floatVal = (float) val) || clazz.equals(float.class)) {
					return floatVal;
				}
			}
			else if (elem instanceof CharElement) {
				char val = ((CharElement) elem).value.raw;
				if (clazz.isInstance(val) || clazz.equals(char.class)) {
					return val;
				}
			}
			else if (elem instanceof StringElement) {
				String val = ((StringElement) elem).value.raw;
				if (clazz.isInstance(val)) {
					return val;
				}
			}
			else if (elem instanceof ListElement) {
				if (List.class.isAssignableFrom(clazz)) {
					return listNativize(elem, Object.class);
				}
				else if (clazz.isArray()) {
					return arrayNativize(elem, clazz.getComponentType());
				}
			}
			else if (elem instanceof SetElement && Set.class.isAssignableFrom(clazz)) {
				return setNativize(elem, Object.class);
			}
			else if (elem instanceof DictElement && Map.class.isAssignableFrom(clazz)) {
				return mapNativize(elem, Object.class, Object.class);
			}
		}
		else if (type instanceof GenericArrayTypeImpl) {
			if (elem instanceof ListElement) {
				Type t = ((GenericArrayTypeImpl) type).getGenericComponentType();
				return arrayNativize(elem, t instanceof ParameterizedTypeImpl ? ((ParameterizedTypeImpl) t).getRawType() : Object.class);
			}
		}
		else if (type instanceof ParameterizedTypeImpl) {
			return nativize(elem, ((ParameterizedTypeImpl) type).getRawType());
		}
		else if (type instanceof WildcardType) {
			return nativize(elem, Object.class);
		}
		
		return elem instanceof NativeElement ? ((NativeElement) elem).value : null;
	}
	
	static Class<?> rawType(Type type) {
		if (type instanceof Class) {
			return (Class<?>) type;
		}
		else if (type instanceof GenericArrayTypeImpl) {
			return newArray(((GenericArrayTypeImpl) type).getGenericComponentType()).getClass();
		}
		else if (type instanceof ParameterizedTypeImpl) {
			return ((ParameterizedTypeImpl) type).getRawType();
		}
		else {
			return Object.class;
		}
	}
	
	static <T> T[] newArray(Type componentType) {
		return (T[]) Array.newInstance(rawType(componentType), 0);
	}
	
	static <T extends Collection<Object>> T collection_nativize(Stream<@NonNull Element> stream, Type type, Tracker tracker, Collector<Object, ?, T> collector) {
		return stream.map(x -> trackedNativize(x, type, tracker)).collect(collector);
	}
	
	static Object listNativize(@NonNull Element elem, Type type) {
		Tracker tracker = new Tracker();
		List<?> obj = collection_nativize(((ListElement) elem).value.stream(), type, tracker, Collectors.toList());
		return tracker.flag ? null : obj;
	}
	
	static Object arrayNativize(@NonNull Element elem, Type type) {
		Tracker tracker = new Tracker();
		List<?> obj = collection_nativize(((ListElement) elem).value.stream(), type, tracker, Collectors.toList());
		if (tracker.flag) {
			return null;
		}
		else {
			int size = obj.size();
			if (type.equals(byte.class)) {
				byte[] arr = new byte[size];
				for (int i = 0; i < size; ++i) {
					arr[i] = (byte) obj.get(i);
				}
				return arr;
			}
			else if (type.equals(short.class)) {
				short[] arr = new short[size];
				for (int i = 0; i < size; ++i) {
					arr[i] = (short) obj.get(i);
				}
				return arr;
			}
			else if (type.equals(int.class)) {
				int[] arr = new int[size];
				for (int i = 0; i < size; ++i) {
					arr[i] = (int) obj.get(i);
				}
				return arr;
			}
			else if (type.equals(long.class)) {
				long[] arr = new long[size];
				for (int i = 0; i < size; ++i) {
					arr[i] = (long) obj.get(i);
				}
				return arr;
			}
			else if (type.equals(boolean.class)) {
				boolean[] arr = new boolean[size];
				for (int i = 0; i < size; ++i) {
					arr[i] = (boolean) obj.get(i);
				}
				return arr;
			}
			else if (type.equals(float.class)) {
				float[] arr = new float[size];
				for (int i = 0; i < size; ++i) {
					arr[i] = (float) obj.get(i);
				}
				return arr;
			}
			else if (type.equals(double.class)) {
				double[] arr = new double[size];
				for (int i = 0; i < size; ++i) {
					arr[i] = (double) obj.get(i);
				}
				return arr;
			}
			else if (type.equals(char.class)) {
				char[] arr = new char[size];
				for (int i = 0; i < size; ++i) {
					arr[i] = (char) obj.get(i);
				}
				return arr;
			}
			else {
				return obj.toArray(newArray(type));
			}
		}
	}
	
	static Object setNativize(@NonNull Element elem, Type type) {
		Tracker tracker = new Tracker();
		Set<?> obj = collection_nativize(((SetElement) elem).value.stream().map(x -> x.elem), type, tracker, Collectors.toSet());
		return tracker.flag ? null : obj;
	}
	
	static Object mapNativize(@NonNull Element elem, Type keyType, Type valueType) {
		Tracker tracker = new Tracker();
		Map<?, ?> obj = Helpers.map(((DictElement) elem).value, x -> trackedNativize(x.elem, keyType, tracker), x -> trackedNativize(x, valueType, tracker));
		return tracker.flag ? null : obj;
	}
	
	static class Tracker {
		
		boolean flag = false;
	}
	
	static <T> T tracked(@NonNull Element elem, Function<@NonNull Element, T> mapper, Tracker tracker) {
		T result = mapper.apply(elem);
		if (result == null && !elem.interpreter.builtIn.nullElement.equals(elem)) {
			tracker.flag = true;
		}
		return result;
	}
	
	static Object trackedNativize(@NonNull Element elem, Type type, Tracker tracker) {
		return tracked(elem, y -> nativize(y, type), tracker);
	}
	
	static @NonNull Element convert(TokenExecutor exec, Object obj) {
		if (obj == null) {
			return exec.interpreter.builtIn.nullElement;
		}
		else if (obj instanceof Byte) {
			return new IntElement(exec.interpreter, (Byte) obj);
		}
		else if (obj instanceof Short) {
			return new IntElement(exec.interpreter, (Short) obj);
		}
		else if (obj instanceof Integer) {
			return new IntElement(exec.interpreter, (Integer) obj);
		}
		else if (obj instanceof Long) {
			return new IntElement(exec.interpreter, (Long) obj);
		}
		else if (obj instanceof BigInteger) {
			return new IntElement(exec.interpreter, (BigInteger) obj);
		}
		else if (obj instanceof Boolean) {
			return new BoolElement(exec.interpreter, (Boolean) obj);
		}
		else if (obj instanceof Float) {
			return new FloatElement(exec.interpreter, ((Float) obj).doubleValue());
		}
		else if (obj instanceof Double) {
			return new FloatElement(exec.interpreter, (Double) obj);
		}
		else if (obj instanceof Character) {
			return new CharElement(exec.interpreter, (Character) obj);
		}
		else if (obj instanceof String) {
			return new StringElement(exec.interpreter, (String) obj);
		}
		else if (obj instanceof List) {
			return new ListElement(exec.interpreter, ((List<?>) obj).stream().map(x -> convert(exec, x)));
		}
		else if (obj.getClass().isArray()) {
			return new ListElement(exec.interpreter, IntStream.range(0, Array.getLength(obj)).mapToObj(x -> NativeImpl.convert(exec, Array.get(obj, x))));
		}
		else if (obj instanceof Set) {
			return new SetElement(exec.interpreter, Helpers.map((Set<?>) obj, x -> convert(exec, x).toKey(exec)));
		}
		else if (obj instanceof Map) {
			return new DictElement(exec.interpreter, Helpers.map((Map<?, ?>) obj, x -> convert(exec, x).toKey(exec), x -> convert(exec, x)));
		}
		else {
			return new NativeElement(exec.interpreter, obj);
		}
	}
}
