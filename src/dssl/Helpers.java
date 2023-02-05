package dssl;

import java.io.*;
import java.math.*;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.*;
import java.util.stream.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.translate.*;
import org.eclipse.jdt.annotation.NonNull;

import dssl.lexer.*;
import dssl.node.*;

public class Helpers {
	
	public static final Collector<CharSequence, ?, String> SPACE_JOIN_COLLECTOR;
	
	static {
		SPACE_JOIN_COLLECTOR = Collectors.joining(" ");
	}
	
	public static String readFile(String fileName) {
		try {
			return new String(Files.readAllBytes(Paths.get(fileName)), Charset.defaultCharset());
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
	
	public static void writeFile(String fileName, String contents) {
		try (PrintWriter out = new PrintWriter(fileName)) {
			out.print(contents);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
	
	public static PushbackReader getPushbackReader(Reader reader) {
		return new PushbackReader(reader, 16384);
	}
	
	public static Lexer stringLexer(String str) {
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
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
	
	public static String lowerCase(String str) {
		return str.toLowerCase(Locale.ROOT);
	}
	
	public static String memberString(String identifier, String member) {
		return String.format("%s.%s", identifier, member).replaceAll(".", " .");
	}
	
	public static int leadingWhitespaceCount(String str) {
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
		unescapeMap.put("\\`", "`");
		unescapeMap.put("\\", "");
		
		UNESCAPE_TRANSLATOR = new LookupTranslator(unescapeMap);
	}
	
	public static @NonNull String parseString(String str) {
		boolean raw = str.charAt(0) == 'r';
		int start = raw ? 2 : 1, end = str.length() - 1;
		String parsed = raw ? str.substring(start, end) : UNESCAPE_TRANSLATOR.translate(CharBuffer.wrap(str, start, end));
		if (parsed == null) {
			throw new RuntimeException(String.format("Failed to parse token %s!", str));
		}
		return parsed;
	}
	
	public static @NonNull Character parseChar(String str) {
		String parsed = parseString(str);
		if (parsed.length() != 1) {
			throw new IllegalArgumentException(String.format("Character value %s is invalid!", str));
		}
		return parsed.charAt(0);
	}
	
	public static @NonNull String parseLineString(String str) {
		return parseString(str);
	}
	
	public static @NonNull String parseBlockString(String str) {
		String[] lines = parseString(str).split("\\r?\\n");
		boolean[] blanks = new boolean[lines.length];
		int commonWhitespace = -1;
		for (int i = 1; i < lines.length; ++i) {
			String line = lines[i];
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
			throw new RuntimeException(String.format("Failed to parse block string!"));
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
		return BigDecimal.valueOf(d).toBigInteger();
	}
	
	public static <A, B> List<B> map(List<A> list, Function<A, B> function) {
		return list.stream().map(function).collect(Collectors.toList());
	}
	
	public static <A, B> Set<B> map(Set<A> set, Function<A, B> function) {
		return set.stream().map(function).collect(Collectors.toSet());
	}
	
	public static <A, B, C, D> Collector<Entry<A, B>, ?, Map<C, D>> mapCollector(Function<A, C> keyFunction, Function<B, D> valueFunction, BinaryOperator<D> mergeFunction) {
		return Collectors.toMap(x -> keyFunction.apply(x.getKey()), x -> valueFunction.apply(x.getValue()), mergeFunction);
	}
	
	public static final Set<String> KEYWORDS;
	
	public static final String L_BRACE = "{";
	public static final String R_BRACE = "}";
	
	public static final String L_BRACKET = "[";
	public static final String R_BRACKET = "]";
	
	public static final String INCLUDE = "include";
	public static final String IMPORT = "import";
	
	public static final String NATIVE = "native";
	
	public static final String DEF = "def";
	public static final String MACRO = "macro";
	public static final String CLASS = "class";
	public static final String MAGIC = "magic";
	
	public static final String NEW = "new";
	
	public static final String DEREF = "deref";
	
	public static final String EXCH = "exch";
	public static final String POP = "pop";
	public static final String DUP = "dup";
	public static final String CLONE = "clone";
	
	public static final String ROLL = "roll";
	public static final String RID = "rid";
	public static final String COPY = "copy";
	
	public static final String INDEX = "index";
	public static final String COUNT = "count";
	public static final String COUNTTO = "countto";
	
	public static final String READ = "read";
	public static final String PRINT = "print";
	public static final String PRINTLN = "println";
	public static final String INTERPRET = "interpret";
	
	public static final String INT = "int";
	public static final String BOOL = "bool";
	public static final String FLOAT = "float";
	public static final String CHAR = "char";
	public static final String STRING = "string";
	
	public static final String RANGE = "range";
	public static final String LIST = "list";
	public static final String TUPLE = "tuple";
	public static final String SET = "set";
	public static final String DICT = "dict";
	
	public static final String NULL = "null";
	public static final String HASH = "hash";
	
	public static final String FOREACH = "foreach";
	public static final String UNPACK = "unpack";
	
	public static final String SIZE = "size";
	public static final String EMPTY = "empty";
	
	public static final String CONTAINS = "contains";
	public static final String ADD = "add";
	public static final String REMOVE = "remove";
	public static final String CONTAINSALL = "containsall";
	public static final String ADDALL = "addall";
	public static final String REMOVEALL = "removeall";
	public static final String CLEAR = "clear";
	
	public static final String GET = "get";
	public static final String PUT = "put";
	public static final String PUTALL = "putall";
	
	public static final String CONTAINSKEY = "containskey";
	public static final String CONTAINSVALUE = "containsvalue";
	public static final String CONTAINSENTRY = "containsentry";
	public static final String KEYS = "keys";
	public static final String VALUES = "values";
	public static final String ENTRIES = "entries";
	
	public static final String TYPE = "type";
	public static final String CAST = "cast";
	
	public static final String EXEC = "exec";
	public static final String IF = "if";
	public static final String IFELSE = "ifelse";
	public static final String REPEAT = "repeat";
	public static final String LOOP = "loop";
	
	public static final String QUIT = "quit";
	public static final String CONTINUE = "continue";
	public static final String BREAK = "break";
	
	public static final String EQUALS = "=";
	
	public static final String INCREMENT = "++";
	public static final String DECREMENT = "--";
	
	public static final String PLUS_EQUALS = "+=";
	public static final String AND_EQUALS = "&=";
	public static final String OR_EQUALS = "|=";
	public static final String XOR_EQUALS = "^=";
	public static final String MINUS_EQUALS = "-=";
	public static final String CONCAT_EQUALS = "~=";
	
	public static final String LEFT_SHIFT_EQUALS = "<<=";
	public static final String RIGHT_SHIFT_EQUALS = ">>=";
	
	public static final String MULTIPLY_EQUALS = "*=";
	public static final String DIVIDE_EQUALS = "/=";
	public static final String REMAINDER_EQUALS = "%=";
	public static final String POWER_EQUALS = "**=";
	public static final String IDIVIDE_EQUALS = "//=";
	public static final String MODULO_EQUALS = "%%=";
	
	public static final String EQUAL_TO = "==";
	public static final String NOT_EQUAL_TO = "!=";
	
	public static final String LESS_THAN = "<";
	public static final String LESS_OR_EQUAL = "<=";
	public static final String MORE_THAN = ">";
	public static final String MORE_OR_EQUAL = ">=";
	
	public static final String PLUS = "+";
	public static final String AND = "&";
	public static final String OR = "|";
	public static final String XOR = "^";
	public static final String MINUS = "-";
	public static final String CONCAT = "~";
	
	public static final String LEFT_SHIFT = "<<";
	public static final String RIGHT_SHIFT = ">>";
	
	public static final String MULTIPLY = "*";
	public static final String DIVIDE = "/";
	public static final String REMAINDER = "%";
	public static final String POWER = "**";
	public static final String IDIVIDE = "//";
	public static final String MODULO = "%%";
	
	public static final String NOT = "not";
	public static final String NEG = "neg";
	
	static {
		KEYWORDS = new HashSet<>();
		
		KEYWORDS.add(L_BRACE);
		KEYWORDS.add(R_BRACE);
		
		KEYWORDS.add(L_BRACKET);
		KEYWORDS.add(R_BRACKET);
		
		KEYWORDS.add(INCLUDE);
		KEYWORDS.add(IMPORT);
		
		KEYWORDS.add(NATIVE);
		
		KEYWORDS.add(DEF);
		KEYWORDS.add(MACRO);
		KEYWORDS.add(CLASS);
		KEYWORDS.add(MAGIC);
		
		KEYWORDS.add(NEW);
		
		KEYWORDS.add(DEREF);
		
		KEYWORDS.add(EXCH);
		KEYWORDS.add(POP);
		KEYWORDS.add(DUP);
		KEYWORDS.add(CLONE);
		
		KEYWORDS.add(ROLL);
		KEYWORDS.add(RID);
		KEYWORDS.add(COPY);
		
		KEYWORDS.add(INDEX);
		KEYWORDS.add(COUNT);
		KEYWORDS.add(COUNTTO);
		
		KEYWORDS.add(READ);
		KEYWORDS.add(PRINT);
		KEYWORDS.add(PRINTLN);
		KEYWORDS.add(INTERPRET);
		
		KEYWORDS.add(INT);
		KEYWORDS.add(BOOL);
		KEYWORDS.add(FLOAT);
		KEYWORDS.add(CHAR);
		KEYWORDS.add(STRING);
		
		KEYWORDS.add(RANGE);
		KEYWORDS.add(LIST);
		KEYWORDS.add(TUPLE);
		KEYWORDS.add(SET);
		KEYWORDS.add(DICT);
		
		KEYWORDS.add(NULL);
		KEYWORDS.add(HASH);
		
		KEYWORDS.add(FOREACH);
		KEYWORDS.add(UNPACK);
		
		KEYWORDS.add(SIZE);
		KEYWORDS.add(EMPTY);
		
		KEYWORDS.add(CONTAINS);
		KEYWORDS.add(ADD);
		KEYWORDS.add(REMOVE);
		KEYWORDS.add(CONTAINSALL);
		KEYWORDS.add(ADDALL);
		KEYWORDS.add(REMOVEALL);
		KEYWORDS.add(CLEAR);
		
		KEYWORDS.add(GET);
		KEYWORDS.add(PUT);
		KEYWORDS.add(PUTALL);
		
		KEYWORDS.add(CONTAINSKEY);
		KEYWORDS.add(CONTAINSVALUE);
		KEYWORDS.add(CONTAINSENTRY);
		KEYWORDS.add(KEYS);
		KEYWORDS.add(VALUES);
		KEYWORDS.add(ENTRIES);
		
		KEYWORDS.add(TYPE);
		KEYWORDS.add(CAST);
		
		KEYWORDS.add(EXEC);
		KEYWORDS.add(IF);
		KEYWORDS.add(IFELSE);
		KEYWORDS.add(REPEAT);
		KEYWORDS.add(LOOP);
		
		KEYWORDS.add(QUIT);
		KEYWORDS.add(CONTINUE);
		KEYWORDS.add(BREAK);
		
		KEYWORDS.add(EQUALS);
		
		KEYWORDS.add(INCREMENT);
		KEYWORDS.add(DECREMENT);
		
		KEYWORDS.add(PLUS_EQUALS);
		KEYWORDS.add(AND_EQUALS);
		KEYWORDS.add(OR_EQUALS);
		KEYWORDS.add(XOR_EQUALS);
		KEYWORDS.add(MINUS_EQUALS);
		KEYWORDS.add(CONCAT_EQUALS);
		
		KEYWORDS.add(LEFT_SHIFT_EQUALS);
		KEYWORDS.add(RIGHT_SHIFT_EQUALS);
		
		KEYWORDS.add(MULTIPLY_EQUALS);
		KEYWORDS.add(DIVIDE_EQUALS);
		KEYWORDS.add(REMAINDER_EQUALS);
		KEYWORDS.add(POWER_EQUALS);
		KEYWORDS.add(IDIVIDE_EQUALS);
		KEYWORDS.add(MODULO_EQUALS);
		
		KEYWORDS.add(EQUAL_TO);
		KEYWORDS.add(NOT_EQUAL_TO);
		
		KEYWORDS.add(LESS_THAN);
		KEYWORDS.add(LESS_OR_EQUAL);
		KEYWORDS.add(MORE_THAN);
		KEYWORDS.add(MORE_OR_EQUAL);
		
		KEYWORDS.add(PLUS);
		KEYWORDS.add(AND);
		KEYWORDS.add(OR);
		KEYWORDS.add(XOR);
		KEYWORDS.add(MINUS);
		KEYWORDS.add(CONCAT);
		
		KEYWORDS.add(LEFT_SHIFT);
		KEYWORDS.add(RIGHT_SHIFT);
		
		KEYWORDS.add(MULTIPLY);
		KEYWORDS.add(DIVIDE);
		KEYWORDS.add(REMAINDER);
		KEYWORDS.add(POWER);
		KEYWORDS.add(IDIVIDE);
		KEYWORDS.add(MODULO);
		
		KEYWORDS.add(NOT);
		KEYWORDS.add(NEG);
	}
	
	public static class Pair<L, R> {
		
		public final L left;
		public final R right;
		
		public Pair(L left, R right) {
			this.left = left;
			this.right = right;
		}
	}
	
	public static class Triple<F, S, T> {
		
		public final F first;
		public final S second;
		public final T third;
		
		public Triple(F first, S second, T third) {
			this.first = first;
			this.second = second;
			this.third = third;
		}
	}
}
