/*
 * Copyright (c) 1994, 2013, Oracle and/or its affiliates. All rights reserved.
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

import java.util.Arrays;

/**
 * A thread-safe, mutable sequence of characters.
 * A string buffer is like a {@link String}, but can be modified. At any
 * point in time it contains some particular sequence of characters, but
 * the length and content of the sequence can be changed through certain
 * method calls.
 * 一个线程安全，可变的字符序列。一个像String一样的字符缓冲，能够被修改。
 * 任何时刻它包含一些主要的字符序列，但是序列的长度和内容能够通过特定方法调用
 * 改变。
 * <p>
 * String buffers are safe for use by multiple threads. The methods
 * are synchronized where necessary so that all the operations on any
 * particular instance behave as if they occur in some serial order
 * that is consistent with the order of the method calls made by each of
 * the individual threads involved.
 * 多线程使用该类是安全的。这些方法在必要时进行同步，以便在任何特定实例上进行的所有操作都表现为
 * 好像以某种串行顺序进行，该顺序与所涉及的每个单独线程进行的方法调用的顺序一致。
 * <p>
 * The principal operations on a {@code StringBuffer} are the
 * {@code append} and {@code insert} methods, which are
 * overloaded so as to accept data of any type. Each effectively
 * converts a given datum to a string and then appends or inserts the
 * characters of that string to the string buffer. The
 * {@code append} method always adds these characters at the end
 * of the buffer; the {@code insert} method adds the characters at
 * a specified point.
 * 该类主要的操作是append和insert方法，这些方法是覆盖齐全的因为所有的
 * 数据参数都能接收。能够将给定的数据转换为string类型并且添加或者插入这些字符
 * 到当前字符序列中。append方法主要用于在字符尾部添加，insert主要用于特定点插入。
 * <p>
 * For example, if {@code z} refers to a string buffer object
 * whose current contents are {@code "start"}, then
 * the method call {@code z.append("le")} would cause the string
 * buffer to contain {@code "startle"}, whereas
 * {@code z.insert(4, "le")} would alter the string buffer to
 * contain {@code "starlet"}.
 * <p>
 * In general, if sb refers to an instance of a {@code StringBuffer},
 * then {@code sb.append(x)} has the same effect as
 * {@code sb.insert(sb.length(), x)}.
 * <p>
 * Whenever an operation occurs involving a source sequence (such as
 * appending or inserting from a source sequence), this class synchronizes
 * only on the string buffer performing the operation, not on the source.
 * Note that while {@code StringBuffer} is designed to be safe to use
 * concurrently from multiple threads, if the constructor or the
 * {@code append} or {@code insert} operation is passed a source sequence
 * that is shared across threads, the calling code must ensure
 * that the operation has a consistent and unchanging view of the source
 * sequence for the duration of the operation.
 * This could be satisfied by the caller holding a lock during the
 * operation's call, by using an immutable source sequence, or by not
 * sharing the source sequence across threads.
 *  每当发生涉及原序列的操作（例如从原序列增加或插入），这个类同步值发生在
 *  操作上，而不是类上（也就是只对方法加锁，不对类加锁）
 *  注意，尽管stringbuffer被设计为多线程下并发安全的类，但是如果构造函数，或append或
 *  insert操作传递了被线程共享的原序列，这些调用必须确保操作过程中原序列一致不变。
 *  调用过程中对操作加锁，通过使用不变的字符序列或者不对所有线程共享原序列的方式是
 *  很不错的。
 * <p>
 * Every string buffer has a capacity. As long as the length of the
 * character sequence contained in the string buffer does not exceed
 * the capacity, it is not necessary to allocate a new internal
 * buffer array. If the internal buffer overflows, it is
 * automatically made larger.
 *  每一个stringbuffer都有一个容量。只要stringbuffer中字符序列长度没有超出容量，
 *  就没必要分配一个新的内部字符数组。如果内部字符溢出，自动会分配更大的空间。
 * <p>
 * Unless otherwise noted, passing a {@code null} argument to a constructor
 * or method in this class will cause a {@link NullPointerException} to be
 * thrown.
 * 除非另有说明，否则传递null参数给构造函数或者方法将会抛出空指针异常。
 * <p>
 * As of  release JDK 5, this class has been supplemented with an equivalent
 * class designed for use by a single thread, {@link StringBuilder}.  The
 * {@code StringBuilder} class should generally be used in preference to
 * this one, as it supports all of the same operations but it is faster, as
 * it performs no synchronization.
 *  在JDK5中，这个类增加了一个等效类StringBuilder，用于单个线程。
 *  一般优先使用StringBuilder类，因为支持所有相同操作且更快，不执行同步操作
 *
 * @author      Arthur van Hoff
 * @see     java.lang.StringBuilder
 * @see     java.lang.String
 * @since   JDK1.0
 */
public final class StringBuffer
        extends AbstractStringBuilder
        implements java.io.Serializable, CharSequence
{

    /**
     * A cache of the last value returned by toString. Cleared
     * whenever the StringBuffer is modified.
     *  只要StringBuffer有被修改，就将该值赋为null
     *
     *  （从头到位只在toString()方法看到有使用，而其他方法都是看到要清空它
     *  因为已经有value[]这个属性了，toStringCache只是用来简单的用在toString方法，
     *  在反序列化时不需要用到它，故而用transient关键字
     *  https://www.cnblogs.com/chenpi/p/6185773.html 这里描述地挺好）
     */
    private transient char[] toStringCache;

    /** use serialVersionUID from JDK 1.0.2 for interoperability */
    static final long serialVersionUID = 3388685877147921107L;

    /**
     * Constructs a string buffer with no characters in it and an
     * initial capacity of 16 characters.
     */
    public StringBuffer() {
        super(16);
    }

    /**
     * Constructs a string buffer with no characters in it and
     * the specified initial capacity.
     *
     * @param      capacity  the initial capacity.
     * @exception  NegativeArraySizeException  if the {@code capacity}
     *               argument is less than {@code 0}.
     */
    public StringBuffer(int capacity) {
        super(capacity);
    }

    /**
     * Constructs a string buffer initialized to the contents of the
     * specified string. The initial capacity of the string buffer is
     * {@code 16} plus the length of the string argument.
     *
     * @param   str   the initial contents of the buffer.
     */
    public StringBuffer(String str) {
        super(str.length() + 16);
        append(str);
    }

    /**
     * Constructs a string buffer that contains the same characters
     * as the specified {@code CharSequence}. The initial capacity of
     * the string buffer is {@code 16} plus the length of the
     * {@code CharSequence} argument.
     *  构造一个string buffer，包含了参数字符串。初始容量是16加上
     *  参数字符串的长度。
     * <p>
     * If the length of the specified {@code CharSequence} is
     * less than or equal to zero, then an empty buffer of capacity
     * {@code 16} is returned.
     *
     * @param      seq   the sequence to copy.
     * @since 1.5
     */
    public StringBuffer(CharSequence seq) {
        this(seq.length() + 16);
        append(seq);
    }

    @Override
    public synchronized int length() {
        return count;
    }

    @Override
    public synchronized int capacity() {
        return value.length;
    }


    @Override
    public synchronized void ensureCapacity(int minimumCapacity) {
        super.ensureCapacity(minimumCapacity);
    }

    /**
     * @since      1.5
     */
    @Override
    public synchronized void trimToSize() {
        super.trimToSize();
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @see        #length()
     */
    @Override
    public synchronized void setLength(int newLength) {
        toStringCache = null;
        super.setLength(newLength);
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @see        #length()
     */
    @Override
    public synchronized char charAt(int index) {
        if ((index < 0) || (index >= count))
            throw new StringIndexOutOfBoundsException(index);
        return value[index];
    }

    /**
     * @since      1.5
     */
    @Override
    public synchronized int codePointAt(int index) {
        return super.codePointAt(index);
    }

    /**
     * @since     1.5
     */
    @Override
    public synchronized int codePointBefore(int index) {
        return super.codePointBefore(index);
    }

    /**
     * @since     1.5
     */
    @Override
    public synchronized int codePointCount(int beginIndex, int endIndex) {
        return super.codePointCount(beginIndex, endIndex);
    }

    /**
     * @since     1.5
     */
    @Override
    public synchronized int offsetByCodePoints(int index, int codePointOffset) {
        return super.offsetByCodePoints(index, codePointOffset);
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public synchronized void getChars(int srcBegin, int srcEnd, char[] dst,
                                      int dstBegin)
    {
        super.getChars(srcBegin, srcEnd, dst, dstBegin);
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @see        #length()
     */
    @Override
    public synchronized void setCharAt(int index, char ch) {
        if ((index < 0) || (index >= count))
            throw new StringIndexOutOfBoundsException(index);
        toStringCache = null;
        value[index] = ch;
    }

    @Override
    public synchronized StringBuffer append(Object obj) {
        toStringCache = null;
        super.append(String.valueOf(obj));
        return this;
    }

    @Override
    public synchronized StringBuffer append(String str) {
        toStringCache = null;
        super.append(str);
        return this;
    }

    /**
     * Appends the specified {@code StringBuffer} to this sequence.
     * <p>
     * The characters of the {@code StringBuffer} argument are appended,
     * in order, to the contents of this {@code StringBuffer}, increasing the
     * length of this {@code StringBuffer} by the length of the argument.
     * If {@code sb} is {@code null}, then the four characters
     * {@code "null"} are appended to this {@code StringBuffer}.
     * <p>
     * Let <i>n</i> be the length of the old character sequence, the one
     * contained in the {@code StringBuffer} just prior to execution of the
     * {@code append} method. Then the character at index <i>k</i> in
     * the new character sequence is equal to the character at index <i>k</i>
     * in the old character sequence, if <i>k</i> is less than <i>n</i>;
     * otherwise, it is equal to the character at index <i>k-n</i> in the
     * argument {@code sb}.
     * <p>
     * This method synchronizes on {@code this}, the destination
     * object, but does not synchronize on the source ({@code sb}).
     *
     * @param   sb   the {@code StringBuffer} to append.
     * @return  a reference to this object.
     * @since 1.4
     */
    public synchronized StringBuffer append(StringBuffer sb) {
        toStringCache = null;
        super.append(sb);
        return this;
    }

    /**
     * @since 1.8
     */
    @Override
    synchronized StringBuffer append(AbstractStringBuilder asb) {
        toStringCache = null;
        super.append(asb);
        return this;
    }

    /**
     * Appends the specified {@code CharSequence} to this
     * sequence.
     * <p>
     * The characters of the {@code CharSequence} argument are appended,
     * in order, increasing the length of this sequence by the length of the
     * argument.
     *
     * <p>The result of this method is exactly the same as if it were an
     * invocation of this.append(s, 0, s.length());
     *
     * <p>This method synchronizes on {@code this}, the destination
     * object, but does not synchronize on the source ({@code s}).
     *
     * <p>If {@code s} is {@code null}, then the four characters
     * {@code "null"} are appended.
     *
     * @param   s the {@code CharSequence} to append.
     * @return  a reference to this object.
     * @since 1.5
     */
    @Override
    public synchronized StringBuffer append(CharSequence s) {
        toStringCache = null;
        super.append(s);
        return this;
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @since      1.5
     */
    @Override
    public synchronized StringBuffer append(CharSequence s, int start, int end)
    {
        toStringCache = null;
        super.append(s, start, end);
        return this;
    }

    @Override
    public synchronized StringBuffer append(char[] str) {
        toStringCache = null;
        super.append(str);
        return this;
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public synchronized StringBuffer append(char[] str, int offset, int len) {
        toStringCache = null;
        super.append(str, offset, len);
        return this;
    }

    @Override
    public synchronized StringBuffer append(boolean b) {
        toStringCache = null;
        super.append(b);
        return this;
    }

    @Override
    public synchronized StringBuffer append(char c) {
        toStringCache = null;
        super.append(c);
        return this;
    }

    @Override
    public synchronized StringBuffer append(int i) {
        toStringCache = null;
        super.append(i);
        return this;
    }

    /**
     * @since 1.5
     */
    @Override
    public synchronized StringBuffer appendCodePoint(int codePoint) {
        toStringCache = null;
        super.appendCodePoint(codePoint);
        return this;
    }

    @Override
    public synchronized StringBuffer append(long lng) {
        toStringCache = null;
        super.append(lng);
        return this;
    }

    @Override
    public synchronized StringBuffer append(float f) {
        toStringCache = null;
        super.append(f);
        return this;
    }

    @Override
    public synchronized StringBuffer append(double d) {
        toStringCache = null;
        super.append(d);
        return this;
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     * @since      1.2
     */
    @Override
    public synchronized StringBuffer delete(int start, int end) {
        toStringCache = null;
        super.delete(start, end);
        return this;
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     * @since      1.2
     */
    @Override
    public synchronized StringBuffer deleteCharAt(int index) {
        toStringCache = null;
        super.deleteCharAt(index);
        return this;
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     * @since      1.2
     */
    @Override
    public synchronized StringBuffer replace(int start, int end, String str) {
        toStringCache = null;
        super.replace(start, end, str);
        return this;
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     * @since      1.2
     */
    @Override
    public synchronized String substring(int start) {
        return substring(start, count);
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @since      1.4
     */
    @Override
    public synchronized CharSequence subSequence(int start, int end) {
        return super.substring(start, end);
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     * @since      1.2
     */
    @Override
    public synchronized String substring(int start, int end) {
        return super.substring(start, end);
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     * @since      1.2
     */
    @Override
    public synchronized StringBuffer insert(int index, char[] str, int offset,
                                            int len)
    {
        toStringCache = null;
        super.insert(index, str, offset, len);
        return this;
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public synchronized StringBuffer insert(int offset, Object obj) {
        toStringCache = null;
        super.insert(offset, String.valueOf(obj));
        return this;
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public synchronized StringBuffer insert(int offset, String str) {
        toStringCache = null;
        super.insert(offset, str);
        return this;
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public synchronized StringBuffer insert(int offset, char[] str) {
        toStringCache = null;
        super.insert(offset, str);
        return this;
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @since      1.5
     */
    @Override
    public StringBuffer insert(int dstOffset, CharSequence s) {
        // Note, synchronization achieved via invocations of other StringBuffer methods
        // after narrowing of s to specific type
        // Ditto for toStringCache clearing
        super.insert(dstOffset, s);
        return this;
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @since      1.5
     */
    @Override
    public synchronized StringBuffer insert(int dstOffset, CharSequence s,
                                            int start, int end)
    {
        toStringCache = null;
        super.insert(dstOffset, s, start, end);
        return this;
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public  StringBuffer insert(int offset, boolean b) {
        // Note, synchronization achieved via invocation of StringBuffer insert(int, String)
        // after conversion of b to String by super class method
        // Ditto for toStringCache clearing
        super.insert(offset, b);
        return this;
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public synchronized StringBuffer insert(int offset, char c) {
        toStringCache = null;
        super.insert(offset, c);
        return this;
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public StringBuffer insert(int offset, int i) {
        // Note, synchronization achieved via invocation of StringBuffer insert(int, String)
        // after conversion of i to String by super class method
        // Ditto for toStringCache clearing
        super.insert(offset, i);
        return this;
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public StringBuffer insert(int offset, long l) {
        // Note, synchronization achieved via invocation of StringBuffer insert(int, String)
        // after conversion of l to String by super class method
        // Ditto for toStringCache clearing
        super.insert(offset, l);
        return this;
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public StringBuffer insert(int offset, float f) {
        // Note, synchronization achieved via invocation of StringBuffer insert(int, String)
        // after conversion of f to String by super class method
        // Ditto for toStringCache clearing
        super.insert(offset, f);
        return this;
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public StringBuffer insert(int offset, double d) {
        // Note, synchronization achieved via invocation of StringBuffer insert(int, String)
        // after conversion of d to String by super class method
        // Ditto for toStringCache clearing
        super.insert(offset, d);
        return this;
    }

    /**
     * @since      1.4
     */
    @Override
    public int indexOf(String str) {
        // Note, synchronization achieved via invocations of other StringBuffer methods
        return super.indexOf(str);
    }

    /**
     * @since      1.4
     */
    @Override
    public synchronized int indexOf(String str, int fromIndex) {
        return super.indexOf(str, fromIndex);
    }

    /**
     * @since      1.4
     */
    @Override
    public int lastIndexOf(String str) {
        // Note, synchronization achieved via invocations of other StringBuffer methods
        return lastIndexOf(str, count);
    }

    /**
     * @since      1.4
     */
    @Override
    public synchronized int lastIndexOf(String str, int fromIndex) {
        return super.lastIndexOf(str, fromIndex);
    }

    /**
     * @since   JDK1.0.2
     */
    @Override
    public synchronized StringBuffer reverse() {
        toStringCache = null;
        super.reverse();
        return this;
    }

    @Override
    public synchronized String toString() {
        if (toStringCache == null) {
            toStringCache = Arrays.copyOfRange(value, 0, count);
        }
        return new String(toStringCache, true);
    }

    /**
     * Serializable fields for StringBuffer.
     * StringBuffer的可序列化字段
     * @serialField value  char[]
     *              The backing character array of this StringBuffer.
     * @serialField count int
     *              The number of characters in this StringBuffer.
     * @serialField shared  boolean
     *              A flag indicating whether the backing array is shared.
     *              The value is ignored upon deserialization.
     *              表明该字符数组是否共享的标志，反序列化被忽略
     */
    private static final java.io.ObjectStreamField[] serialPersistentFields =
            {
                    new java.io.ObjectStreamField("value", char[].class),
                    new java.io.ObjectStreamField("count", Integer.TYPE),
                    new java.io.ObjectStreamField("shared", Boolean.TYPE),
            };

    /**
     * readObject is called to restore the state of the StringBuffer from
     * a stream.
     * 调用readObject可以从流中恢复StringBuffer的状态。
     */
    private synchronized void writeObject(java.io.ObjectOutputStream s)
            throws java.io.IOException {
        java.io.ObjectOutputStream.PutField fields = s.putFields();
        fields.put("value", value);
        fields.put("count", count);
        fields.put("shared", false);
        s.writeFields();
    }

    /**
     * readObject is called to restore the state of the StringBuffer from
     * a stream.
     * 调用readObject可以从流中恢复StringBuffer的状态。
     */
    private void readObject(java.io.ObjectInputStream s)
            throws java.io.IOException, ClassNotFoundException {
        java.io.ObjectInputStream.GetField fields = s.readFields();
        value = (char[])fields.get("value", null);
        count = fields.get("count", 0);
    }
}
