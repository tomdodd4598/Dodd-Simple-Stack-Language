package dssl;

import java.io.*;
import java.math.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;

import org.apache.commons.text.translate.*;
import org.eclipse.jdt.annotation.NonNull;

public class Helpers {
	
	private static final BufferedReader READER = new BufferedReader(new InputStreamReader(System.in));
	
	public static @NonNull String readLine() {
		String str = null;
		try {
			str = READER.readLine();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		if (str == null) {
			throw new RuntimeException("Failed to read input!");
		}
		return str;
	}
	
	public static PushbackReader getPushbackReader(Reader reader) {
		return new PushbackReader(reader, 8192);
	}
	
	public static String readFile(String fileName) {
		try {
			return new String(Files.readAllBytes(Paths.get(fileName)), Charset.defaultCharset());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void writeFile(String fileName, String contents) {
		try {
			PrintWriter out = new PrintWriter(fileName);
			out.print(contents);
			out.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String lowerCase(String str) {
		return str.toLowerCase(Locale.ROOT);
	}
	
	public static final CharSequenceTranslator UNESCAPE_TRANSLATOR;
	
	static {
		Map<CharSequence, CharSequence> unescapeMap = new HashMap<>();
		unescapeMap.put("\\t", "\t");
		unescapeMap.put("\\b", "\b");
		unescapeMap.put("\\n", "\n");
		unescapeMap.put("\\r", "\r");
		unescapeMap.put("\\f", "\f");
		Map<CharSequence, CharSequence> unescapeCtrlCharsMap = Collections.unmodifiableMap(unescapeMap);
		
		unescapeMap = new HashMap<>();
		unescapeMap.put("\\\\", "\\");
		unescapeMap.put("\\'", "'");
		unescapeMap.put("\\\"", "\"");
		unescapeMap.put("\\", "");
		Map<CharSequence, CharSequence> unescapeExtrasMap = Collections.unmodifiableMap(unescapeMap);
		
		UNESCAPE_TRANSLATOR = new AggregateTranslator(new LookupTranslator(unescapeCtrlCharsMap), new LookupTranslator(unescapeExtrasMap));
	}
	
	public static @NonNull Character parseChar(String str) {
		String unescape = parseString(str);
		if (unescape.length() != 1) {
			throw new IllegalArgumentException(String.format("Character value %s in invalid!", str));
		}
		return unescape.charAt(0);
	}
	
	public static @NonNull String parseString(String str) {
		String parsed = UNESCAPE_TRANSLATOR.translate(str.substring(1, str.length() - 1));
		if (parsed == null) {
			throw new RuntimeException(String.format("Failed to parse string \"%s\"!", str));
		}
		return parsed;
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
	
	public static <T> T nullable(@NonNull T obj) {
		return obj;
	}
	
	public static class Pair<L, R> {
		
		public L left;
		public R right;
		
		public Pair(L left, R right) {
			this.left = left;
			this.right = right;
		}
	}
}
