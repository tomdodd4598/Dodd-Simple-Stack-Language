package dssl;

import java.math.BigInteger;

public class Constants {
	
	// Ints
	
	public static final BigInteger MIN_INT_8 = BigInteger.valueOf(Byte.MIN_VALUE);
	public static final BigInteger MAX_INT_8 = BigInteger.valueOf(Byte.MAX_VALUE);
	
	public static final BigInteger MIN_INT_16 = BigInteger.valueOf(Short.MIN_VALUE);
	public static final BigInteger MAX_INT_16 = BigInteger.valueOf(Short.MAX_VALUE);
	
	public static final BigInteger MIN_INT_32 = BigInteger.valueOf(Integer.MIN_VALUE);
	public static final BigInteger MAX_INT_32 = BigInteger.valueOf(Integer.MAX_VALUE);
	
	public static final BigInteger MIN_INT_64 = BigInteger.valueOf(Long.MIN_VALUE);
	public static final BigInteger MAX_INT_64 = BigInteger.valueOf(Long.MAX_VALUE);
	
	// Floats
	
	public static final double INF = Double.POSITIVE_INFINITY;
	public static final double NAN = Double.NaN;
	public static final double MIN_F = Double.MIN_VALUE;
	public static final double MAX_F = Double.MAX_VALUE;
	public static final double EPSILON = 2.2204460492503131E-16;
	
	public static final double E = Math.E;
	
	public static final double PI = Math.PI;
	public static final double TAU = 6.28318530717958647692528676655900577;
	
	public static final double INV_PI = 0.318309886183790671537767526745028724;
	public static final double INV_TAU = 0.159154943091895335768883763372514362;
	
	public static final double SQRT_PI = 1.77245385090551602729816748334114518;
	public static final double SQRT_TAU = 2.50662827463100050241576528481104525;
	
	public static final double INV_SQRT_PI = 0.564189583547756286948079451560772586;
	public static final double INV_SQRT_TAU = 0.398942280401432677939946059934381868;
	
	public static final double INV_3 = 0.333333333333333333333333333333333333;
	public static final double INV_6 = 0.166666666666666666666666666666666667;
	
	public static final double SQRT_2 = 1.41421356237309504880168872420969808;
	public static final double SQRT_3 = 1.73205080756887729352744634150587237;
	public static final double SQRT_6 = 2.44948974278317809819728407470589139;
	public static final double SQRT_8 = 2.82842712474619009760337744841939616;
	
	public static final double INV_SQRT_2 = 0.707106781186547524400844362104849039;
	public static final double INV_SQRT_3 = 0.577350269189625764509148780501957456;
	public static final double INV_SQRT_6 = 0.408248290463863016366214012450981899;
	public static final double INV_SQRT_8 = 0.353553390593273762200422181052424520;
	
	public static final double FRAC_PI_2 = 1.57079632679489661923132169163975144;
	public static final double FRAC_PI_3 = 1.04719755119659774615421446109316763;
	public static final double FRAC_PI_4 = 0.785398163397448309615660845819875721;
	public static final double FRAC_PI_6 = 0.52359877559829887307710723054658381;
	public static final double FRAC_PI_8 = 0.39269908169872415480783042290993786;
	
	public static final double FRAC_2_PI = 0.636619772367581343075535053490057448;
	public static final double FRAC_2_SQRT_PI = 1.12837916709551257389615890312154517;
	
	public static final double LN_2 = 0.693147180559945309417232121458176568;
	public static final double LN_10 = 2.30258509299404568401799145468436421;
	public static final double LOG2_E = 1.44269504088896340735992468100189214;
	public static final double LOG2_10 = 3.32192809488736234787031942948939018;
	public static final double LOG10_E = 0.434294481903251827651128918916605082;
	public static final double LOG10_2 = 0.301029995663981195213738894724493027;
}
