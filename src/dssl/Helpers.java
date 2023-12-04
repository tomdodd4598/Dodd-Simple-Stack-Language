package dssl;

import java.io.*;
import java.math.*;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.translate.*;
import org.eclipse.jdt.annotation.*;

import dssl.lexer.*;
import dssl.node.*;

public class Helpers {
	
	public static RuntimeException panic(Exception e) {
		e.printStackTrace();
		return new RuntimeException();
	}
	
	public static @NonNull String readFile(@NonNull String fileName) {
		return getThrowing(() -> new String(Files.readAllBytes(Paths.get(fileName)), Charset.defaultCharset()));
	}
	
	@SuppressWarnings("null")
	public static List<@NonNull String> readLines(@NonNull String fileName) {
		return getThrowing(() -> Files.readAllLines(Paths.get(fileName), Charset.defaultCharset()));
	}
	
	public static void writeFile(@NonNull String fileName, String contents) {
		try (PrintWriter out = new PrintWriter(fileName)) {
			out.print(contents);
		}
		catch (Exception e) {
			throw panic(e);
		}
	}
	
	public static <T> void writeLines(@NonNull String fileName, Consumer<Consumer<? super String>> forEach) {
		try (PrintWriter out = new PrintWriter(fileName)) {
			forEach.accept(x -> {
				out.print(x);
				out.println();
			});
		}
		catch (Exception e) {
			throw panic(e);
		}
	}
	
	public static void writeLines(@NonNull String fileName, Iterable<String> lines) {
		writeLines(fileName, lines::forEach);
	}
	
	public static void writeLines(@NonNull String fileName, Iterator<String> lines) {
		writeLines(fileName, lines::forEachRemaining);
	}
	
	public static void writeLines(@NonNull String fileName, Stream<String> lines) {
		writeLines(fileName, lines::forEachOrdered);
	}
	
	public static PushbackReader getPushbackReader(Reader reader) {
		return new PushbackReader(reader, 16384);
	}
	
	public static Lexer stringLexer(@NonNull String str) {
		return new Lexer(getPushbackReader(new StringReader(str)));
	}
	
	public static Token getLexerNext(Lexer lexer) {
		try {
			return lexer.next();
		}
		catch (LexerException e) {
			throw new IllegalArgumentException(String.format("Encountered invalid token \"%s\"!", e.getToken().getText()));
		}
		catch (IOException e) {
			throw panic(e);
		}
	}
	
	public static <T> @NonNull T checkNonNull(T value) {
		if (value == null) {
			throw new IllegalArgumentException(String.format("Encountered invalid null value!"));
		}
		else {
			return value;
		}
	}
	
	@SuppressWarnings("null")
	public static @NonNull String normalizedPathString(Path path) {
		return path.normalize().toString().replace('\\', '/');
	}
	
	@SuppressWarnings("null")
	public static @NonNull String lowerCase(@NonNull String str) {
		return str.toLowerCase(Locale.ROOT);
	}
	
	@SuppressWarnings("null")
	public static @NonNull String upperCase(@NonNull String str) {
		return str.toUpperCase(Locale.ROOT);
	}
	
	public static @NonNull String ordinal(int n) {
		switch (n) {
			case 1:
				return "first";
			case 2:
				return "second";
			case 3:
				return "third";
			case 4:
				return "fourth";
			case 5:
				return "fifth";
			default:
				throw new IllegalArgumentException(String.format("Could not convert %s to ordinal string!", n));
		}
	}
	
	public static int leadingWhitespaceCount(@NonNull String str) {
		int count = 0, len = str.length();
		for (int i = 0; i < len; ++i) {
			if (Character.isWhitespace(str.charAt(i))) {
				++count;
			}
			else {
				break;
			}
		}
		return count;
	}
	
	private static final CharSequenceTranslator UNESCAPE_TRANSLATOR;
	
	static {
		Map<CharSequence, CharSequence> unescapeMap = new HashMap<>();
		unescapeMap.put("\\t", "\t");
		unescapeMap.put("\\b", "\b");
		unescapeMap.put("\\n", "\n");
		unescapeMap.put("\\r", "\r");
		unescapeMap.put("\\f", "\f");
		unescapeMap.put("\\\\", "\\");
		unescapeMap.put("\\'", "'");
		unescapeMap.put("\\\"", "\"");
		unescapeMap.put("\\", "");
		
		UNESCAPE_TRANSLATOR = new LookupTranslator(unescapeMap);
	}
	
	public static @NonNull String parseString(@NonNull String str, int prefixLength, int suffixLength) {
		boolean raw = str.charAt(0) == 'r';
		int start = (raw ? 1 : 0) + prefixLength, end = str.length() - suffixLength;
		String parsed = raw ? str.substring(start, end) : UNESCAPE_TRANSLATOR.translate(CharBuffer.wrap(str, start, end));
		if (parsed == null) {
			throw new RuntimeException(String.format("Failed to parse token %s!", str));
		}
		return parsed;
	}
	
	public static @NonNull Character parseChar(@NonNull String str) {
		String parsed = parseString(str, 1, 1);
		if (parsed.length() != 1) {
			throw new IllegalArgumentException(String.format("Character value %s is invalid!", str));
		}
		return parsed.charAt(0);
	}
	
	public static @NonNull String parseLineString(@NonNull String str) {
		return parseString(str, 1, 1);
	}
	
	@SuppressWarnings("null")
	public static @NonNull String parseBlockString(@NonNull String str) {
		@NonNull String[] lines = parseString(str, 3, 3).split("\\R", -1);
		boolean[] blanks = new boolean[lines.length];
		int commonWhitespace = -1;
		for (int i = 1; i < lines.length; ++i) {
			@NonNull String line = lines[i];
			boolean blank = blanks[i] = StringUtils.isBlank(line);
			if (i == lines.length - 1 || !blank) {
				int whitespaceCount = leadingWhitespaceCount(line);
				if (commonWhitespace == -1 || commonWhitespace > whitespaceCount) {
					commonWhitespace = whitespaceCount;
				}
			}
		}
		
		if (commonWhitespace < 0) {
			commonWhitespace = 0;
		}
		
		for (int i = 1; i < lines.length; ++i) {
			lines[i] = blanks[i] ? "" : lines[i].substring(commonWhitespace);
		}
		
		String joined = StringUtils.join(Arrays.asList(lines).subList(1, lines.length), '\n');
		if (joined == null) {
			throw new RuntimeException(String.format("Failed to parse multi-line string!"));
		}
		return joined;
	}
	
	public static boolean isSeparator(Token token) {
		return token instanceof TBlank || token instanceof TComment;
	}
	
	public static int mod(int a, int b) {
		return (a % b + b) % b;
	}
	
	public static double mod(double a, double b) {
		return (a % b + b) % b;
	}
	
	public static BigInteger mod(BigInteger a, BigInteger b) {
		return a.remainder(b).add(b).remainder(b);
	}
	
	public static BigInteger bigIntFromDouble(double d) {
		return new BigDecimal(d).toBigInteger();
	}
	
	public static BigInteger iroot(BigInteger a, int b) {
		if (b == 0) {
			if (a.equals(BigInteger.ZERO)) {
				return BigInteger.ZERO;
			}
			else {
				throw new ArithmeticException("zeroth root of non-zero integer");
			}
		}
		else if (a.signum() < 0) {
			if ((b & 1) == 0) {
				throw new ArithmeticException("even root of negative integer");
			}
			else if (b < -1 || b > 1) {
				throw new ArithmeticException("fractional power of negative integer");
			}
			else {
				return iroot(a.negate(), b).negate();
			}
		}
		else if (b < 0) {
			if (a.equals(BigInteger.ZERO)) {
				throw new ArithmeticException("negative root of zero");
			}
			else {
				return a.equals(BigInteger.ONE) ? BigInteger.ONE : BigInteger.ZERO;
			}
		}
		else if (a.equals(BigInteger.ZERO) || a.equals(BigInteger.ONE)) {
			return a;
		}
		else {
			BigInteger root = BigInteger.ONE.shiftLeft(a.bitLength() / b), mul = BigInteger.valueOf(b - 1), div = BigInteger.valueOf(b);
			while (root.pow(b).compareTo(a) > 0 || a.compareTo(root.add(BigInteger.ONE).pow(b)) >= 0) {
				root = root.multiply(mul).add(a.divide(root.pow(b - 1))).divide(div);
			}
			return root;
		}
	}
	
	public static double clamp(double value, double min, double max) {
		return value < min ? min : (value > max ? max : value);
	}
	
	public static BigInteger clamp(BigInteger value, BigInteger min, BigInteger max) {
		return value.compareTo(min) < 0 ? min : (value.compareTo(max) > 0 ? max : value);
	}
	
	@SuppressWarnings("null")
	public static @NonNull String tokenListToString(List<@NonNull Token> tokens) {
		return tokens.stream().filter(x -> !isSeparator(x)).map(x -> x.toString().trim()).collect(Collectors.joining(" ", "{ ", " }"));
	}
	
	public static <T> Stream<T> stream(Iterable<T> iterable) {
		return StreamSupport.stream(iterable.spliterator(), false);
	}
	
	public static <A, B> List<B> map(List<A> list, Function<? super A, B> function) {
		return list.stream().map(function).collect(Collectors.toList());
	}
	
	public static <A, B> Set<B> map(Set<A> set, Function<? super A, B> function) {
		return set.stream().map(function).collect(Collectors.toSet());
	}
	
	public static <A, B, C, D> Map<C, D> map(Map<A, B> map, Function<? super A, C> keyFun, Function<? super B, D> valueFun) {
		return map.entrySet().stream().collect(Collectors.toMap(x -> keyFun.apply(x.getKey()), x -> valueFun.apply(x.getValue()), (x, y) -> y));
	}
	
	public static RuntimeException variableError(@NonNull String identifier) {
		return new IllegalArgumentException(String.format("Variable \"%s\" not defined!", identifier));
	}
	
	public static RuntimeException defError(@NonNull String identifier) {
		return new IllegalArgumentException(String.format("Variable, macro or class \"%s\" not defined!", identifier));
	}
	
	public static @NonNull String extendedIdentifier(@Nullable String prevIdentifier, @NonNull String extension) {
		return prevIdentifier == null ? extension : prevIdentifier + "." + extension;
	}
	
	public static class Pair<A, B> {
		
		public final A first;
		public final B second;
		
		public Pair(A first, B second) {
			this.first = first;
			this.second = second;
		}
	}
	
	public static class Triple<A, B, C> {
		
		public final A first;
		public final B second;
		public final C third;
		
		public Triple(A first, B second, C third) {
			this.first = first;
			this.second = second;
			this.third = third;
		}
	}
	
	@FunctionalInterface
	public static interface ThrowingRunnable {
		
		public void run() throws Exception;
	}
	
	public static void runThrowing(ThrowingRunnable runnable) {
		try {
			runnable.run();
		}
		catch (Exception e) {
			throw panic(e);
		}
	}
	
	@FunctionalInterface
	public static interface ThrowingConsumer<T> {
		
		public void accept(T x) throws Exception;
	}
	
	public static <T> void acceptThrowing(ThrowingConsumer<T> consumer, T x) {
		try {
			consumer.accept(x);
		}
		catch (Exception e) {
			throw panic(e);
		}
	}
	
	@FunctionalInterface
	public static interface ThrowingSupplier<T> {
		
		public T get() throws Exception;
	}
	
	public static <T> T getThrowing(ThrowingSupplier<T> supplier) {
		try {
			return supplier.get();
		}
		catch (Exception e) {
			throw panic(e);
		}
	}
	
	@FunctionalInterface
	public static interface ThrowingFunction<A, B> {
		
		public B apply(A x) throws Exception;
	}
	
	public static <A, B> B applyThrowing(ThrowingFunction<A, B> function, A x) {
		try {
			return function.apply(x);
		}
		catch (Exception e) {
			throw panic(e);
		}
	}
}
