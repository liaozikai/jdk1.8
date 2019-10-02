/*
 * Copyright (c) 1996, 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package java.lang;

/**
 *
 * The {@code Byte} class wraps a value of primitive type {@code byte}
 * in an object.  An object of type {@code Byte} contains a single
 * field whose type is {@code byte}.
 * Byte 类是原始类型byte的包装类，它包含了一个
 *    类型为byte的字段
 * <p>In addition, this class provides several methods for converting
 * a {@code byte} to a {@code String} and a {@code String} to a {@code
 * byte}, as well as other constants and methods useful when dealing
 * with a {@code byte}.
 *
 * @author  Nakul Saraiya
 * @author  Joseph D. Darcy
 * @see     java.lang.Number
 * @since   JDK1.1
 */
public final class Byte extends Number implements Comparable<Byte> {

    /**
     * A constant holding the minimum value a {@code byte} can
     * have, -2<sup>7</sup>.
     *  该常量表示byte能够持有的最小值
     */
    public static final byte   MIN_VALUE = -128;

    /**
     * A constant holding the maximum value a {@code byte} can
     * have, 2<sup>7</sup>-1.
     * 该常量表示byte能够持有的最大值
     */
    public static final byte   MAX_VALUE = 127;

    /**
     * The {@code Class} instance representing the primitive type
     * {@code byte}.
     */
    @SuppressWarnings("unchecked")
    public static final Class<Byte>  TYPE = (Class<Byte>) Class.getPrimitiveClass("byte");

    /**
     * Returns a new {@code String} object representing the
     * specified {@code byte}. The radix is assumed to be 10.
     * 返回一个表示byte类型的String对象，进制数默认为十进制
     * @param b the {@code byte} to be converted
     * @return the string representation of the specified {@code byte}
     * @see java.lang.Integer#toString(int)
     */
    public static String toString(byte b) {
        return Integer.toString((int)b, 10);
    }

    private static class ByteCache {
        // 不给实例化
        private ByteCache(){}

        static final Byte cache[] = new Byte[-(-128) + 127 + 1];

        // 初始化静态代码块，缓存-128到127，提高性能
        static {
            for(int i = 0; i < cache.length; i++)
                cache[i] = new Byte((byte)(i - 128));
        }
    }

    /**
     * Returns a {@code Byte} instance representing the specified
     * {@code byte} value.
     * 返回一个Byte实例代表特定的byte类型值
     * If a new {@code Byte} instance is not required, this method
     * should generally be used in preference to the constructor
     * {@link #Byte(byte)}, as this method is likely to yield
     * significantly better space and time performance since
     * all byte values are cached.
     *  如果不需要新的Byte实例，则一般使用该方法而不是构造方法，
     *  应为这个方法有更好的空间和时间性能，因为所有的byte值都被缓存了。
     * @param  b a byte value.
     * @return a {@code Byte} instance representing {@code b}.
     * @since  1.5
     */
    public static Byte valueOf(byte b) {
        final int offset = 128;
        return ByteCache.cache[(int)b + offset];
    }

    /**
     * Parses the string argument as a signed {@code byte} in the
     * radix specified by the second argument. The characters in the
     * string must all be digits, of the specified radix (as
     * determined by whether {@link java.lang.Character#digit(char,
     * int)} returns a nonnegative value) except that the first
     * character may be an ASCII minus sign {@code '-'}
     * ({@code '\u005Cu002D'}) to indicate a negative value or an
     * ASCII plus sign {@code '+'} ({@code '\u005Cu002B'}) to
     * indicate a positive value.  The resulting {@code byte} value is
     * returned.
     * 通过第二个特定的表示基数的参数来解析string参数成byte值。
     * string参数必须指定基数的数字（技术是由Character#digit(char,int)返回非负数值确定），
     *但第一个字符可以是ASCII减号 符号{@code'-'}（{@code'\ u005Cu002D'}）表示负值，
     *或ASCII加号{@code'+'}（{@code'\ u005Cu002B'}）表示正值。 返回结果{@code byte}。
     * <p>An exception of type {@code NumberFormatException} is
     * thrown if any of the following situations occurs:
     * <ul> 下面情况有任何一种发生就抛出NumberFormatException这个异常
     * <li> The first argument is {@code null} or is a string of
     * length zero.
     *  第一个参数是null或者字符长度为0
     * <li> The radix is either smaller than {@link
     * java.lang.Character#MIN_RADIX} or larger than {@link
     * java.lang.Character#MAX_RADIX}.
     * 基数小于最小基数值或者大于最大基数值
     * <li> Any character of the string is not a digit of the
     * specified radix, except that the first character may be a minus
     * sign {@code '-'} ({@code '\u005Cu002D'}) or plus sign
     * {@code '+'} ({@code '\u005Cu002B'}) provided that the
     * string is longer than length 1.
     *  string值不是特定基数下的数字，除了加减号代表的数字
     * <li> The value represented by the string is not a value of type
     * {@code byte}.
     * </ul>  该值不是byte类型的值
     *
     * @param s         the {@code String} containing the {@code byte}
     *                  representation to be parsed
     * @param radix     the radix to be used while parsing {@code s}
     * @return          the {@code byte} value represented by the string
     *                   argument in the specified radix
     * @throws          NumberFormatException If the string does
     *                  not contain a parsable {@code byte}.
     */
    public static byte parseByte(String s, int radix)
            throws NumberFormatException {
        // 借助Integer的parsetInt方法，因为int 的范围比byte范围大，
        // byte没必要多些一次解析的方法
        int i = Integer.parseInt(s, radix);
        if (i < MIN_VALUE || i > MAX_VALUE)
            throw new NumberFormatException(
                    "Value out of range. Value:\"" + s + "\" Radix:" + radix);
        return (byte)i;
    }

    /**
     * Parses the string argument as a signed decimal {@code
     * byte}. The characters in the string must all be decimal digits,
     * except that the first character may be an ASCII minus sign
     * {@code '-'} ({@code '\u005Cu002D'}) to indicate a negative
     * value or an ASCII plus sign {@code '+'}
     * ({@code '\u005Cu002B'}) to indicate a positive value. The
     * resulting {@code byte} value is returned, exactly as if the
     * argument and the radix 10 were given as arguments to the {@link
     * #parseByte(java.lang.String, int)} method.
     *
     * @param s         a {@code String} containing the
     *                  {@code byte} representation to be parsed
     * @return          the {@code byte} value represented by the
     *                  argument in decimal
     * @throws          NumberFormatException if the string does not
     *                  contain a parsable {@code byte}.
     *
     *   默认10进制的解析
     */
    public static byte parseByte(String s) throws NumberFormatException {
        return parseByte(s, 10);
    }

    /**
     * Returns a {@code Byte} object holding the value
     * extracted from the specified {@code String} when parsed
     * with the radix given by the second argument. The first argument
     * is interpreted as representing a signed {@code byte} in
     * the radix specified by the second argument, exactly as if the
     * argument were given to the {@link #parseByte(java.lang.String,
     * int)} method. The result is a {@code Byte} object that
     * represents the {@code byte} value specified by the string.
     *
     * <p> In other words, this method returns a {@code Byte} object
     * equal to the value of:
     *
     * <blockquote>
     * {@code new Byte(Byte.parseByte(s, radix))}
     * </blockquote>
     *
     * @param s         the string to be parsed
     * @param radix     the radix to be used in interpreting {@code s}
     * @return          a {@code Byte} object holding the value
     *                  represented by the string argument in the
     *                  specified radix.
     * @throws          NumberFormatException If the {@code String} does
     *                  not contain a parsable {@code byte}.
     */
    public static Byte valueOf(String s, int radix)
            throws NumberFormatException {
        // 将parseByte(s, radix)封装成Byte类型
        return valueOf(parseByte(s, radix));
    }

    /**
     * Returns a {@code Byte} object holding the value
     * given by the specified {@code String}. The argument is
     * interpreted as representing a signed decimal {@code byte},
     * exactly as if the argument were given to the {@link
     * #parseByte(java.lang.String)} method. The result is a
     * {@code Byte} object that represents the {@code byte}
     * value specified by the string.
     *
     * <p> In other words, this method returns a {@code Byte} object
     * equal to the value of:
     *
     * <blockquote>
     * {@code new Byte(Byte.parseByte(s))}
     * </blockquote>
     *
     * @param s         the string to be parsed
     * @return          a {@code Byte} object holding the value
     *                  represented by the string argument
     * @throws          NumberFormatException If the {@code String} does
     *                  not contain a parsable {@code byte}.
     */
    public static Byte valueOf(String s) throws NumberFormatException {
        return valueOf(s, 10);
    }

    /**
     * Decodes a {@code String} into a {@code Byte}.
     * Accepts decimal, hexadecimal, and octal numbers given by
     * the following grammar:
     * 将String字符串解码为Byte类型数据，该参数可以是10进制，
     * 16进制和8进制，语法可以参考下面
     * <blockquote>
     * <dl>
     * <dt><i>DecodableString:</i>
     * <dd><i>Sign<sub>opt</sub> DecimalNumeral</i>
     * <dd><i>Sign<sub>opt</sub></i> {@code 0x} <i>HexDigits</i>  以0x开头
     * <dd><i>Sign<sub>opt</sub></i> {@code 0X} <i>HexDigits</i>  以0X开头
     * <dd><i>Sign<sub>opt</sub></i> {@code #} <i>HexDigits</i>  以#开头
     * <dd><i>Sign<sub>opt</sub></i> {@code 0} <i>OctalDigits</i> 以0开头表示8进制
     *
     * <dt><i>Sign:</i>
     * <dd>{@code -}
     * <dd>{@code +}
     * </dl>
     * </blockquote>
     *
     * <i>DecimalNumeral</i>, <i>HexDigits</i>, and <i>OctalDigits</i>
     * are as defined in section 3.10.1 of
     * <cite>The Java&trade; Language Specification</cite>,
     * except that underscores are not accepted between digits.
     *
     * <p>The sequence of characters following an optional
     * sign and/or radix specifier ("{@code 0x}", "{@code 0X}",
     * "{@code #}", or leading zero) is parsed as by the {@code
     * Byte.parseByte} method with the indicated radix (10, 16, or 8).
     * This sequence of characters must represent a positive value or
     * a {@link NumberFormatException} will be thrown.  The result is
     * negated if first character of the specified {@code String} is
     * the minus sign.  No whitespace characters are permitted in the
     * {@code String}.
     * 可选的符号和/或基数说明符（“ 0x”，“ 0X”，“＃”或前导零）之后的字符序列由Byte.parseByte方法解析，
     * 并带有指定的基数（10、16或8））。 此字符序列必须表示一个正值，否则将引发NumberFormatException。
     * 如果指定字符串的第一个字符为减号，则结果为负。 字符串中不允许使用空格字符。
     * @param     nm the {@code String} to decode.
     * @return   a {@code Byte} object holding the {@code byte}
     *          value represented by {@code nm}
     * @throws  NumberFormatException  if the {@code String} does not
     *            contain a parsable {@code byte}.
     * @see java.lang.Byte#parseByte(java.lang.String, int)
     */
    public static Byte decode(String nm) throws NumberFormatException {
        int i = Integer.decode(nm);
        if (i < MIN_VALUE || i > MAX_VALUE)
            throw new NumberFormatException(
                    "Value " + i + " out of range from input " + nm);
        return valueOf((byte)i);
    }

    /**
     * The value of the {@code Byte}.
     *
     * @serial
     */
    private final byte value;

    /**
     * Constructs a newly allocated {@code Byte} object that
     * represents the specified {@code byte} value.
     *
     * @param value     the value to be represented by the
     *                  {@code Byte}.
     */
    public Byte(byte value) {
        this.value = value;
    }

    /**
     * Constructs a newly allocated {@code Byte} object that
     * represents the {@code byte} value indicated by the
     * {@code String} parameter. The string is converted to a
     * {@code byte} value in exactly the manner used by the
     * {@code parseByte} method for radix 10.
     *
     * @param s         the {@code String} to be converted to a
     *                  {@code Byte}
     * @throws           NumberFormatException If the {@code String}
     *                  does not contain a parsable {@code byte}.
     * @see        java.lang.Byte#parseByte(java.lang.String, int)
     */
    public Byte(String s) throws NumberFormatException {
        this.value = parseByte(s, 10);
    }

    /**
     * Returns the value of this {@code Byte} as a
     * {@code byte}.
     */
    public byte byteValue() {
        return value;
    }

    /**
     * Returns the value of this {@code Byte} as a {@code short} after
     * a widening primitive conversion.
     * @jls 5.1.2 Widening Primitive Conversions
     */
    public short shortValue() {
        return (short)value;
    }

    /**
     * Returns the value of this {@code Byte} as an {@code int} after
     * a widening primitive conversion.
     * @jls 5.1.2 Widening Primitive Conversions
     */
    public int intValue() {
        return (int)value;
    }

    /**
     * Returns the value of this {@code Byte} as a {@code long} after
     * a widening primitive conversion.
     * @jls 5.1.2 Widening Primitive Conversions
     */
    public long longValue() {
        return (long)value;
    }

    /**
     * Returns the value of this {@code Byte} as a {@code float} after
     * a widening primitive conversion.
     * @jls 5.1.2 Widening Primitive Conversions
     */
    public float floatValue() {
        return (float)value;
    }

    /**
     * Returns the value of this {@code Byte} as a {@code double}
     * after a widening primitive conversion.
     * @jls 5.1.2 Widening Primitive Conversions
     */
    public double doubleValue() {
        return (double)value;
    }

    /**
     * Returns a {@code String} object representing this
     * {@code Byte}'s value.  The value is converted to signed
     * decimal representation and returned as a string, exactly as if
     * the {@code byte} value were given as an argument to the
     * {@link java.lang.Byte#toString(byte)} method.
     *
     * @return  a string representation of the value of this object in
     *          base&nbsp;10.
     */
    public String toString() {
        return Integer.toString((int)value);
    }

    /**
     * Returns a hash code for this {@code Byte}; equal to the result
     * of invoking {@code intValue()}.
     *
     * @return a hash code value for this {@code Byte}
     */
    @Override
    public int hashCode() {
        return Byte.hashCode(value);
    }

    /**
     * Returns a hash code for a {@code byte} value; compatible with
     * {@code Byte.hashCode()}.
     *
     * @param value the value to hash
     * @return a hash code value for a {@code byte} value.
     * @since 1.8
     * // byte型数据的hashcode就是其本身的值
     */
    public static int hashCode(byte value) {
        return (int)value;
    }

    /**
     * Compares this object to the specified object.  The result is
     * {@code true} if and only if the argument is not
     * {@code null} and is a {@code Byte} object that
     * contains the same {@code byte} value as this object.
     *
     * @param obj       the object to compare with
     * @return          {@code true} if the objects are the same;
     *                  {@code false} otherwise.
     */
    public boolean equals(Object obj) {
        if (obj instanceof Byte) {
            return value == ((Byte)obj).byteValue();
        }
        return false;
    }

    /**
     * Compares two {@code Byte} objects numerically.
     *
     * @param   anotherByte   the {@code Byte} to be compared.
     * @return  the value {@code 0} if this {@code Byte} is
     *          equal to the argument {@code Byte}; a value less than
     *          {@code 0} if this {@code Byte} is numerically less
     *          than the argument {@code Byte}; and a value greater than
     *           {@code 0} if this {@code Byte} is numerically
     *           greater than the argument {@code Byte} (signed
     *           comparison).
     * @since   1.2
     */
    public int compareTo(Byte anotherByte) {
        return compare(this.value, anotherByte.value);
    }

    /**
     * Compares two {@code byte} values numerically.
     * The value returned is identical to what would be returned by:
     * <pre>
     *    Byte.valueOf(x).compareTo(Byte.valueOf(y))
     * </pre>
     *
     * @param  x the first {@code byte} to compare
     * @param  y the second {@code byte} to compare
     * @return the value {@code 0} if {@code x == y};
     *         a value less than {@code 0} if {@code x < y}; and
     *         a value greater than {@code 0} if {@code x > y}
     * @since 1.7
     */
    public static int compare(byte x, byte y) {
        return x - y;
    }

    /**
     * Converts the argument to an {@code int} by an unsigned
     * conversion.  In an unsigned conversion to an {@code int}, the
     * high-order 24 bits of the {@code int} are zero and the
     * low-order 8 bits are equal to the bits of the {@code byte} argument.
     *  通过无符号的转换将参数转为int型数据。在转换过程中，高24位被赋值为0
     *  并且低8位值和原来的byte参数是一样的。
     * Consequently, zero and positive {@code byte} values are mapped
     * to a numerically equal {@code int} value and negative {@code
     * byte} values are mapped to an {@code int} value equal to the
     * input plus 2<sup>8</sup>.
     * 所以，0和整数值被映射到一个int型数据并且负数被映射到等价于input
     * @param  x the value to convert to an unsigned {@code int}
     * @return the argument converted to {@code int} by an unsigned
     *         conversion
     * @since 1.8
     */
    public static int toUnsignedInt(byte x) {
        // 先转换为int型，那么高位会补位，如果是正数则补零，负数则补1，
        // 跟0xff相与操作后，高位会被清空为0
        return ((int) x) & 0xff;
    }

    /**
     * Converts the argument to a {@code long} by an unsigned
     * conversion.  In an unsigned conversion to a {@code long}, the
     * high-order 56 bits of the {@code long} are zero and the
     * low-order 8 bits are equal to the bits of the {@code byte} argument.
     *
     * Consequently, zero and positive {@code byte} values are mapped
     * to a numerically equal {@code long} value and negative {@code
     * byte} values are mapped to a {@code long} value equal to the
     * input plus 2<sup>8</sup>.
     *
     * @param  x the value to convert to an unsigned {@code long}
     * @return the argument converted to {@code long} by an unsigned
     *         conversion
     * @since 1.8
     */
    public static long toUnsignedLong(byte x) {
        return ((long) x) & 0xffL;
    }


    /**
     * The number of bits used to represent a {@code byte} value in two's
     * complement binary form.
     *
     * @since 1.5
     */
    public static final int SIZE = 8;

    /**
     * The number of bytes used to represent a {@code byte} value in two's
     * complement binary form.
     *
     * @since 1.8
     */
    public static final int BYTES = SIZE / Byte.SIZE;

    /** use serialVersionUID from JDK 1.1. for interoperability */
    private static final long serialVersionUID = -7183698231559129828L;
}
