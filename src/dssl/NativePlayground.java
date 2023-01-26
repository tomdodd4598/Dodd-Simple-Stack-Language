package dssl;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.*;

@SuppressWarnings("null")
public class NativePlayground {
	
	public static byte static_byte = 1;
	public static short static_short = 2;
	public static int static_int = 3;
	public static long static_long = 4;
	public static BigInteger static_big_int = BigInteger.valueOf(5);
	public static boolean static_boolean = true;
	public static float static_float = 45F;
	public static double static_double = 98;
	public static char static_char = 'x';
	public static String static_string = "tbe";
	
	public static Byte static_boxed_byte = 6;
	public static Short static_boxed_short = 7;
	public static Integer static_boxed_int = 8;
	public static Long static_boxed_long = 9L;
	public static Boolean static_boxed_boolean = false;
	public static Float static_boxed_float = 65F;
	public static Double static_boxed_double = 49D;
	public static Character static_boxed_char = 'y';
	
	public static byte[] static_byte_array = {static_byte, static_boxed_byte};
	public static short[] static_short_array = {static_short, static_boxed_short};
	public static int[] static_int_array = {static_int, static_boxed_int};
	public static long[] static_long_array = {static_long, static_boxed_long};
	public static boolean[] static_boolean_array = {static_boolean, static_boxed_boolean};
	public static float[] static_float_array = {static_float, static_boxed_float};
	public static double[] static_double_array = {static_double, static_boxed_double};
	public static char[] static_char_array = {static_char, static_boxed_char};
	public static String[] static_string_array = {"paul", "brian", static_string};
	
	public static Object[] static_array = {static_boolean, static_float_array, static_big_int, static_string_array};
	public static List<Object> static_list;
	public static Set<Object> static_set;
	public static Map<Object, Object> static_map;
	
	static {
		List<Object> x = Arrays.asList(static_byte, static_short, static_int, static_long, static_big_int);
		List<Object> y = Arrays.asList(static_boxed_boolean, static_boxed_float, static_boxed_double, static_boxed_char, static_string);
		static_list = new ArrayList<>(x);
		static_set = new HashSet<>(y);
		static_map = IntStream.range(0, x.size()).boxed().collect(Collectors.toMap(x::get, y::get));
	}
	
	public static void static_method(byte by, short sh, int in, long lo, BigInteger bi, boolean bo, float fl, double du, char ch, String st) {
		System.out.println(by + ", " + sh + ", " + in + ", " + lo + ", " + bi + ", " + bo + ", " + fl + ", " + du + ", " + ch + ", " + st);
	}
	
	public static boolean static_method(Byte by, Short sh, Integer in, Long lo, Boolean bo, Float fl, Double du, Character ch) {
		System.out.println(by + ", " + sh + ", " + in + ", " + lo + ", " + bo + ", " + fl + ", " + du + ", " + ch);
		return true;
	}
	
	public static Double static_method(byte[] by, short[] sh, int[] in, long[] lo, boolean[] bo, float[] fl, double[] du, char[] ch, String[] st) {
		System.out.println(Arrays.toString(by) + ", " + Arrays.toString(sh) + ", " + Arrays.toString(in) + ", " + Arrays.toString(lo) + ", " + Arrays.toString(bo) + ", " + Arrays.toString(fl) + ", " + Arrays.toString(du) + ", " + Arrays.toString(ch) + ", " + Arrays.toString(st));
		return 3.14;
	}
	
	public static byte[] static_method(byte[][] by, short[][] sh, int[][] in, long[][] lo, boolean[][] bo, float[][] fl, double[][] du, char[][] ch, String[][] st) {
		System.out.println(Arrays.deepToString(by) + ", " + Arrays.deepToString(sh) + ", " + Arrays.deepToString(in) + ", " + Arrays.deepToString(lo) + ", " + Arrays.deepToString(bo) + ", " + Arrays.deepToString(fl) + ", " + Arrays.deepToString(du) + ", " + Arrays.deepToString(ch) + ", " + Arrays.deepToString(st));
		return new byte[] {2, 4, 6, 8};
	}
	
	public static String[] static_method(List<Object> li, Set<Object> se, Map<Object, Object> ma) {
		System.out.println(li + ", " + se + ", " + ma);
		return new String[] {"bumble", "nass"};
	}
	
	public static <A, B> List<B> static_method(List<A> li, Set<B> se, Map<A, B> ma, A[] ar1, List<B>[] ar2) {
		System.out.println(li + ", " + se + ", " + ma + ", " + Arrays.toString(ar1) + ", " + Arrays.toString(ar2));
		return se.stream().collect(Collectors.toList());
	}
	
	public static ABC static_abc() {
		return new ABC();
	}
	
	public static class ABC {
		
		public byte instance_byte = static_byte;
		public short instance_short = static_short;
		public int instance_int = static_int;
		public long instance_long = static_long;
		public BigInteger instance_big_int = static_big_int;
		public boolean instance_boolean = static_boolean;
		public float instance_float = static_float;
		public double instance_double = static_double;
		public char instance_char = static_char;
		public String instance_string = static_string;
		
		public Byte instance_boxed_byte = static_boxed_byte;
		public Short instance_boxed_short = static_boxed_short;
		public Integer instance_boxed_int = static_boxed_int;
		public Long instance_boxed_long = static_boxed_long;
		public Boolean instance_boxed_boolean = static_boxed_boolean;
		public Float instance_boxed_float = static_boxed_float;
		public Double instance_boxed_double = static_boxed_double;
		public Character instance_boxed_char = static_boxed_char;
		
		public byte[] instance_byte_array = static_byte_array;
		public short[] instance_short_array = static_short_array;
		public int[] instance_int_array = static_int_array;
		public long[] instance_long_array = static_long_array;
		public boolean[] instance_boolean_array = static_boolean_array;
		public float[] instance_float_array = static_float_array;
		public double[] instance_double_array = static_double_array;
		public char[] instance_char_array = static_char_array;
		public String[] instance_string_array = static_string_array;
		
		public Object[] instance_array = static_array;
		public List<Object> instance_list = static_list;
		public Set<Object> instance_set = static_set;
		public Map<Object, Object> instance_map = static_map;
		
		public void instance_method(byte by, short sh, int in, long lo, BigInteger bi, boolean bo, float fl, double du, char ch, String st) {
			System.out.println(by + ", " + sh + ", " + in + ", " + lo + ", " + bi + ", " + bo + ", " + fl + ", " + du + ", " + ch + ", " + st);
		}
		
		public boolean instance_method(Byte by, Short sh, Integer in, Long lo, Boolean bo, Float fl, Double du, Character ch) {
			System.out.println(by + ", " + sh + ", " + in + ", " + lo + ", " + bo + ", " + fl + ", " + du + ", " + ch);
			return true;
		}
		
		public Double instance_method(byte[] by, short[] sh, int[] in, long[] lo, boolean[] bo, float[] fl, double[] du, char[] ch, String[] st) {
			System.out.println(Arrays.toString(by) + ", " + Arrays.toString(sh) + ", " + Arrays.toString(in) + ", " + Arrays.toString(lo) + ", " + Arrays.toString(bo) + ", " + Arrays.toString(fl) + ", " + Arrays.toString(du) + ", " + Arrays.toString(ch) + ", " + Arrays.toString(st));
			return 3.14;
		}
		
		public byte[] instance_method(byte[][] by, short[][] sh, int[][] in, long[][] lo, boolean[][] bo, float[][] fl, double[][] du, char[][] ch, String[][] st) {
			System.out.println(Arrays.deepToString(by) + ", " + Arrays.deepToString(sh) + ", " + Arrays.deepToString(in) + ", " + Arrays.deepToString(lo) + ", " + Arrays.deepToString(bo) + ", " + Arrays.deepToString(fl) + ", " + Arrays.deepToString(du) + ", " + Arrays.deepToString(ch) + ", " + Arrays.deepToString(st));
			return new byte[] {2, 4, 6, 8};
		}
		
		public String[] instance_method(List<Object> li, Set<Object> se, Map<Object, Object> ma) {
			System.out.println(li + ", " + se + ", " + ma);
			return new String[] {"bumble", "nass"};
		}
		
		public <A, B> List<B> instance_method(List<A> li, Set<B> se, Map<A, B> ma, A[] ar1, List<B>[] ar2) {
			System.out.println(li + ", " + se + ", " + ma + ", " + Arrays.toString(ar1) + ", " + Arrays.toString(ar2));
			return se.stream().collect(Collectors.toList());
		}
	}
}
