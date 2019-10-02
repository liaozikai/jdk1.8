/*
 * Copyright (c) 1994, 2012, Oracle and/or its affiliates. All rights reserved.
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
 * Class {@code Object} is the root of the class hierarchy.
 * Every class has {@code Object} as a superclass. All objects,
 * including arrays, implement the methods of this class.
 * Object是类体系的根类，每个类都以它为父类。所有的对象包括数组，都实现了这个类的方法
 * @author  unascribed
 * @see     java.lang.Class
 * @since   JDK1.0
 */
public class Object {

    private static native void registerNatives();
    static {
        registerNatives(); // 静态代码块，类加载时，就调用注册本地方法
    }

    /**
     * Returns the runtime class of this {@code Object}. The returned
     * {@code Class} object is the object that is locked by {@code
     * static synchronized} methods of the represented class.
     *  返回当前对象运行时的类。返回的类对象被该类的静态同步方法锁住
     *  （我的理解是，该本地方法是一个线程安全的方法，返回的是运行时的类）
     * <p><b>The actual result type is {@code Class<? extends |X|>}
     * where {@code |X|} is the erasure of the static type of the
     * expression on which {@code getClass} is called.</b> For
     * example, no cast is required in this code fragment:</p>
     *  实际类型是继承于x的class类型。其中x是调用getClass的表达式的静态类型的擦除。
     *  （我的理解，java编译时，是会进行类型擦除的操作，也就是说，在编译时就已经知道该对象的实际类型了）
     *  例如，此代码片段不需要强制转换：
     * <p>
     * {@code Number n = 0;                             }<br>
     * {@code Class<? extends Number> c = n.getClass(); }
     * </p>
     *
     * @return The {@code Class} object that represents the runtime
     *         class of this object.
     * @jls 15.8.2 Class Literals
     */
    public final native Class<?> getClass();

    /**
     * Returns a hash code value for the object. This method is
     * supported for the benefit of hash tables such as those provided by
     * {@link java.util.HashMap}.
     * 返回对象的哈希值。该方法用于支持哈希表例如hashmap（好难翻译这句话，但
     * 大致意思应该是实现该方法有利于hash表和hashmap，这里利用hashcode来实现的对象和方法）
     * <p>
     * The general contract of {@code hashCode} is:
     * 哈希码的一般约定是：
     * <ul>
     * <li>Whenever it is invoked on the same object more than once during
     *     an execution of a Java application, the {@code hashCode} method
     *     must consistently return the same integer, provided no information
     *     used in {@code equals} comparisons on the object is modified.
     *     This integer need not remain consistent from one execution of an
     *     application to another execution of the same application.
     *      在java应用中对于同个对象无论是否该方法被调用超过一次，该方法必须一贯地返回同样的integer值，
     *      前提是没有修改对象上的{@code equals}比较中使用的信息。（也就是没用重写方法）
     *
     * <li>If two objects are equal according to the {@code equals(Object)}
     *     method, then calling the {@code hashCode} method on each of
     *     the two objects must produce the same integer result.
     *     如果两个对象通过equals方法比较结果是相同的，则调用每个对象的hashcode的方法需要返回
     *     相同的integer结果。
     * <li>It is <em>not</em> required that if two objects are unequal
     *     according to the {@link java.lang.Object#equals(java.lang.Object)}
     *     method, then calling the {@code hashCode} method on each of the
     *     two objects must produce distinct integer results.  However, the
     *     programmer should be aware that producing distinct integer results
     *     for unequal objects may improve the performance of hash tables.
     *     通过equals方法得到的结果是不相同的，并没有要求调用两个对象的hashcode一定要产生不同的integer值
     *      但是，编程人员应该注意对于不相同对象产生不同integer结果将会提高哈希表的性能。
     * </ul>
     * （我的理解是，这个方法表达的意思是，equals方法结果相同的对象哈希值必须相同，hash值相同的对象equasls一定相同）
     * <p>
     * As much as is reasonably practical, the hashCode method defined by
     * class {@code Object} does return distinct integers for distinct
     * objects. (This is typically implemented by converting the internal
     * address of the object into an integer, but this implementation
     * technique is not required by the
     * Java&trade; programming language.)
     *  对于不同的objects，Object的hashCode方法的确尽可能多地返回不同的integer值
     *  这时通过转换对象的内部地址到一个整型数值，但这种实现技巧不被java语言所支持。
     * @return  a hash code value for this object.
     * @see     java.lang.Object#equals(java.lang.Object)
     * @see     java.lang.System#identityHashCode
     */
    public native int hashCode();

    /**
     * Indicates whether some other object is "equal to" this one.
     * 表明是否一些其他对象等于这一个对象
     * <p>
     * The {@code equals} method implements an equivalence relation
     * on non-null object references:
     *  这个方法在非空的对象引用上实现一个等价的比较关系
     * <ul>
     * <li>It is <i>reflexive</i>: for any non-null reference value
     *     {@code x}, {@code x.equals(x)} should return
     *     {@code true}.
     *     该方法具有反身性，即x必须equals x
     * <li>It is <i>symmetric</i>: for any non-null reference values
     *     {@code x} and {@code y}, {@code x.equals(y)}
     *     should return {@code true} if and only if
     *     {@code y.equals(x)} returns {@code true}.
     *     x.equals(y)与y.equals(x)必须结果相同
     * <li>It is <i>transitive</i>: for any non-null reference values
     *     {@code x}, {@code y}, and {@code z}, if
     *     {@code x.equals(y)} returns {@code true} and
     *     {@code y.equals(z)} returns {@code true}, then
     *     {@code x.equals(z)} should return {@code true}.
     *     必须传递性
     * <li>It is <i>consistent</i>: for any non-null reference values
     *     {@code x} and {@code y}, multiple invocations of
     *     {@code x.equals(y)} consistently return {@code true}
     *     or consistently return {@code false}, provided no
     *     information used in {@code equals} comparisons on the
     *     objects is modified.
     *     x.equals(y)的值多次调用应该一直返回true或false，
     *     前提是没有修改对象上的{@code equals}比较中使用的信息。
     * <li>For any non-null reference value {@code x},
     *     {@code x.equals(null)} should return {@code false}.
     *     对于非空引用x，x.equals(null)应该返回null
     * </ul>
     * <p>
     * The {@code equals} method for class {@code Object} implements
     * the most discriminating possible equivalence relation on objects;
     * that is, for any non-null reference values {@code x} and
     * {@code y}, this method returns {@code true} if and only
     * if {@code x} and {@code y} refer to the same object
     * ({@code x == y} has the value {@code true}).
     * 对于类Object的equals方法实现了最具有鉴别的等价关系，
     * 那就是对于任何非空额引用值x和y，只有当x和y指向相同对象才会返回true
     * <p>
     * Note that it is generally necessary to override the {@code hashCode}
     * method whenever this method is overridden, so as to maintain the
     * general contract for the {@code hashCode} method, which states
     * that equal objects must have equal hash codes.
     * 注意无论何时该方法被重写一般而言都有必要重写hashCode方法，
     * 只要保证equals结果为true时hashcode结果相同这个规则。
     * @param   obj   the reference object with which to compare.
     * @return  {@code true} if this object is the same as the obj
     *          argument; {@code false} otherwise.
     * @see     #hashCode()
     * @see     java.util.HashMap
     */
    public boolean equals(Object obj) {
        return (this == obj);
    }

    /**
     * Creates and returns a copy of this object.  The precise meaning
     * of "copy" may depend on the class of the object. The general
     * intent is that, for any object {@code x}, the expression:
     * <blockquote>
     *
     * <pre>
     * x.clone() != x</pre></blockquote>
     * will be true, and that the expression:
     * <blockquote>
     *
     * <pre>
     * x.clone().getClass() == x.getClass()</pre></blockquote>
     * will be {@code true}, but these are not absolute requirements.
     * While it is typically the case that:
     * <blockquote>
     *
     * <pre>
     * x.clone().equals(x)</pre></blockquote>
     * will be {@code true}, this is not an absolute requirement.
     * <p>
     * By convention, the returned object should be obtained by calling
     * {@code super.clone}.  If a class and all of its superclasses (except
     * {@code Object}) obey this convention, it will be the case that
     * {@code x.clone().getClass() == x.getClass()}.
     * <p>
     *
     * By convention, the object returned by this method should be independent
     * of this object (which is being cloned).  To achieve this independence,
     * it may be necessary to modify one or more fields of the object returned
     * by {@code super.clone} before returning it.  Typically, this means
     * copying any mutable objects that comprise the internal "deep structure"
     * of the object being cloned and replacing the references to these
     * objects with references to the copies.  If a class contains only
     * primitive fields or references to immutable objects, then it is usually
     * the case that no fields in the object returned by {@code super.clone}
     * need to be modified.
     *   创建和返回一个对象的复制对象。这个精确的复制可能依赖于对象的类。总的意图是：
     *   对于任何对象 x，表达式：x.clone()!=x 应该返回true。并且下面的表达式：
     *   x.clone().getClass() == x.getClass()应该返回true；但这些不是绝对的要求。
     *    然而典型的案例是：x.clone().equals(x)应返回true，这个也不是绝对的要求
     *    按照惯例，返回对象应该工作super.clone来获取，如果一个类和它的父类（除了object）遵循这个惯例，
     *    则会有x.clone().getClass() == x.getClass()
     *    按照惯例，通过这个方法返回的对象应该是独立于这个对象，为了去实现这种独立性，有必要去修改
     *    clone返回对象的一个或多个字段。通常，这意味着复制任何具有深层结构的可变对象的复制，需要替换
     *    这些复制的引用。如果一个类只包含基本字段或对不可变对象的引用，那么通常情况下克隆的对象就不需要修改。
     *    （其实也就是说对象中有引用类型的字段，需要进行深克隆）
     * <p>
     * The method {@code clone} for class {@code Object} performs a
     * specific cloning operation. First, if the class of this object does
     * not implement the interface {@code Cloneable}, then a
     * {@code CloneNotSupportedException} is thrown. Note that all arrays
     * are considered to implement the interface {@code Cloneable} and that
     * the return type of the {@code clone} method of an array type {@code T[]}
     * is {@code T[]} where T is any reference or primitive type.
     * Otherwise, this method creates a new instance of the class of this
     * object and initializes all its fields with exactly the contents of
     * the corresponding fields of this object, as if by assignment; the
     * contents of the fields are not themselves cloned. Thus, this method
     * performs a "shallow copy" of this object, not a "deep copy" operation.
     * <p>
     *     clone方法需要Object对象执行一些特定操作。首先，使用该方法的对象如果没有实现Cloneable接口，
     *     将会抛出一个CloneNotSupportedException异常。注意，所有数组都被认为实现接口{@code Cloneable}，
     *     数组类型{@code T[]}的{@code clone}方法的返回类型为{@code T[]}，其中T是任何引用或基本类型。
     *     否则，该方法创建该对象类的一个新实例，并使用该对象相应字段的内容精确地初始化其所有字段，就像通过赋值一样;
     *     字段的内容本身不是克隆的。因此，此方法执行此对象的“浅复制”操作，而不是“深复制”操作。
     *     （有道翻译很好用。其实就是该方法要实现 Cloneable接口并且该方法是浅克隆不是深克隆）
     * The class {@code Object} does not itself implement the interface
     * {@code Cloneable}, so calling the {@code clone} method on an object
     * whose class is {@code Object} will result in throwing an
     * exception at run time.
     *  由于Object本身并没有实现Cloneable接口，所以它调用会抛出运行时异常
     *
     * @return     a clone of this instance.
     * @throws  CloneNotSupportedException  if the object's class does not
     *               support the {@code Cloneable} interface. Subclasses
     *               that override the {@code clone} method can also
     *               throw this exception to indicate that an instance cannot
     *               be cloned.
     * @see java.lang.Cloneable
     */

    protected native Object clone() throws CloneNotSupportedException;

    /**
     * Returns a string representation of the object. In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     *
     *  返回一个对象的字符串表示。一般而言，该方法返回的是这个对象代表性的文本描述。
     *  这个结果应该是一个简短的且容易让人读懂的信息表示。
     * The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     * 该方法返回由类实例名，@符号和该对象无符号16进制哈希码组成的字符串。
     * 具体而言，就是返回getClass().getName() + '@' + Integer.toHexString(hashCode())的字符串
     *
     * @return  a string representation of the object.
     */
    public String toString() {
        return getClass().getName() + "@" + Integer.toHexString(hashCode());
    }

    /**
     * Wakes up a single thread that is waiting on this object's
     * monitor. If any threads are waiting on this object, one of them
     * is chosen to be awakened. The choice is arbitrary and occurs at
     * the discretion of the implementation. A thread waits on an object's
     * monitor by calling one of the {@code wait} methods.
     *  唤醒正在等待这个对象监视器的一个单线程。如果有任何一个线程正在等待
     *  使用这个对象，其中的一个将被选择唤醒。这个选择是任意的且自由的。
     *  一个线程通过wait方法来等待该对象的监视器。
     *  （我的理解，每个对象都有一个监视者对象，多个线程竞争同一个对象资源的时候，
     *      需要获得监视者对象的同意，也就是监视器锁。只有获得监视器锁的线程才能
     *      进行资源的操作，其他获取不到锁的线程继续阻塞）
     * <p>
     * The awakened thread will not be able to proceed until the current
     * thread relinquishes the lock on this object. The awakened thread will
     * compete in the usual manner with any other threads that might be
     * actively competing to synchronize on this object; for example, the
     * awakened thread enjoys no reliable privilege or disadvantage in being
     * the next thread to lock this object.
     *  在当前线程放弃这个对象的锁之前，唤醒的线程不能继续操作。唤醒的线程将会
     *  将会与其他任何一个线程以常见的方式对该对象进行同步竞争。例如，唤醒的线程
     *  在称为锁定该对象的下一个线程之前没有任何特权或优势
     *  （我的理解，其实这里想表明的就是任何一个被唤醒的线程都有公平的机会获得锁）
     * <p>
     * This method should only be called by a thread that is the owner
     * of this object's monitor. A thread becomes the owner of the
     * object's monitor in one of three ways:
     *  这个方法只能被该对象监视器的拥有者所调用。成为该对象监视器的拥有者有
     *  以下三种方式：
     * <ul>
     * <li>By executing a synchronized instance method of that object.
     * <li>By executing the body of a {@code synchronized} statement
     *     that synchronizes on the object.
     * <li>For objects of type {@code Class,} by executing a
     *     synchronized static method of that class.
     * </ul>
     *      通过执行该对象的同步实例方法（普通方法前加上synchronized关键字）
     *      通过执行在对象上同步的同步语句的主体。
     *      (方法内部代码块加锁，如：public void fun3() {synchronized (this) {}})
     *      对于类类型的对象，通过执行该类的同步静态方法。（类方法前加上synchronized关键字）
     * <p>
     * Only one thread at a time can own an object's monitor.
     *  每次只能有一个线程获得对象的监视器
     *
     * @throws  IllegalMonitorStateException  if the current thread is not
     *               the owner of this object's monitor.
     * @see        java.lang.Object#notifyAll()
     * @see        java.lang.Object#wait()
     */
    public final native void notify();

    /**
     * Wakes up all threads that are waiting on this object's monitor. A
     * thread waits on an object's monitor by calling one of the
     * {@code wait} methods.
     * <p>
     * The awakened threads will not be able to proceed until the current
     * thread relinquishes the lock on this object. The awakened threads
     * will compete in the usual manner with any other threads that might
     * be actively competing to synchronize on this object; for example,
     * the awakened threads enjoy no reliable privilege or disadvantage in
     * being the next thread to lock this object.
     * <p>
     * This method should only be called by a thread that is the owner
     * of this object's monitor. See the {@code notify} method for a
     * description of the ways in which a thread can become the owner of
     * a monitor.
     *
     * @throws  IllegalMonitorStateException  if the current thread is not
     *               the owner of this object's monitor.
     * @see        java.lang.Object#notify()
     * @see        java.lang.Object#wait()
     *
     * notify和notifyAll区别是前者只会唤醒一个线程，后者会唤醒所有线程去竞争，
     * 最终会有一个线程获得监视器
     */
    public final native void notifyAll();

    /**
     * Causes the current thread to wait until either another thread invokes the
     * {@link java.lang.Object#notify()} method or the
     * {@link java.lang.Object#notifyAll()} method for this object, or a
     * specified amount of time has elapsed.
     *  使得当前线程等待，直到有其他线程调用notify或notifyAll方法，或者特定时间过去
     * <p>
     * The current thread must own this object's monitor.
     *  当前线程必须拥有该对象的监视器
     * <p>
     * This method causes the current thread (call it <var>T</var>) to
     * place itself in the wait set for this object and then to relinquish
     * any and all synchronization claims on this object. Thread <var>T</var>
     * becomes disabled for thread scheduling purposes and lies dormant
     * until one of four things happens:
     *        此方法导致当前线程（称为<var> T </ var>）将自己置于该对象的等待集中，然后放弃
     *       关于此对象的所有同步声明。 线程<var> T </ var>出于线程调度目的而被禁用，
     *       并且处于休眠状态直到发生四件事之一：
     * <ul>
     * <li>Some other thread invokes the {@code notify} method for this
     * object and thread <var>T</var> happens to be arbitrarily chosen as
     * the thread to be awakened.
     * <li>Some other thread invokes the {@code notifyAll} method for this
     * object.
     * <li>Some other thread {@linkplain Thread#interrupt() interrupts}
     * thread <var>T</var>.
     * <li>The specified amount of real time has elapsed, more or less.  If
     * {@code timeout} is zero, however, then real time is not taken into
     * consideration and the thread simply waits until notified.
     *    一些其他线程调用notify方法并且该线程刚好选中被唤醒
     *     一些其他线程调用notifyall方法
     *     一些其他线程中断
     *      指定的时间过去。如果指定的时间为0，则该线程只能等待被唤醒。
     * </ul>
     * The thread <var>T</var> is then removed from the wait set for this
     * object and re-enabled for thread scheduling. It then competes in the
     * usual manner with other threads for the right to synchronize on the
     * object; once it has gained control of the object, all its
     * synchronization claims on the object are restored to the status quo
     * ante - that is, to the situation as of the time that the {@code wait}
     * method was invoked. Thread <var>T</var> then returns from the
     * invocation of the {@code wait} method. Thus, on return from the
     * {@code wait} method, the synchronization state of the object and of
     * thread {@code T} is exactly as it was when the {@code wait} method
     * was invoked.
     *  这个线程T之后离开这个对象的等待集并且能够再次被线程调度。之后能够与其他线程
     *  竞争该对象的同步权利。如果它获得这个对象的控制，所有在这个对象的同步声明
     *  都会恢复为现状，该现状指的是调用wait方法时候的状态。线程T之后从调用wait的方法返回。
     *  因此，线程被唤醒之后的状态数据与被调用wait方法时的状态数据完全一致。
     * <p>
     * A thread can also wake up without being notified, interrupted, or
     * timing out, a so-called <i>spurious wakeup</i>.  While this will rarely
     * occur in practice, applications must guard against it by testing for
     * the condition that should have caused the thread to be awakened, and
     * continuing to wait if the condition is not satisfied.  In other words,
     * waits should always occur in loops, like this one:
     *  线程还能够不是以notifty方法，中断和时间等待的方式被唤醒，这种方式叫做伪唤醒。
     *  实际情况很少发生，应用程序必须通过测试应该导致线程唤醒的条件来防范它，并在条件不满足时继续等待。
     *  换句话说，等待应该总是像这样循环执行，如：
     * <pre>
     *     synchronized (obj) {
     *         while (&lt;condition does not hold&gt;)
     *             obj.wait(timeout);
     *         ... // Perform action appropriate to condition
     *     }
     * </pre>
     * (For more information on this topic, see Section 3.2.3 in Doug Lea's
     * "Concurrent Programming in Java (Second Edition)" (Addison-Wesley,
     * 2000), or Item 50 in Joshua Bloch's "Effective Java Programming
     * Language Guide" (Addison-Wesley, 2001).
     *  更多信息看 Effective Java Programming Language Guide
     *
     * <p>If the current thread is {@linkplain java.lang.Thread#interrupt()
     * interrupted} by any thread before or while it is waiting, then an
     * {@code InterruptedException} is thrown.  This exception is not
     * thrown until the lock status of this object has been restored as
     * described above.
     *如果当前线程在等待之前或等待时被任何线程interrupt（）中断，
     * 则将引发InterruptedException。
     *  如上所述，直到该对象的锁定状态恢复之前，不会引发此异常。
     * <p>
     * Note that the {@code wait} method, as it places the current thread
     * into the wait set for this object, unlocks only this object; any
     * other objects on which the current thread may be synchronized remain
     * locked while the thread waits.
     * 注意wait方法，因为它将该线程放到了对象的等待集，只能通过这个对象解锁，
     * 当这个对象等待的时候，当前线程的其他对象可能同步保持被锁状态。
     * <p>
     * This method should only be called by a thread that is the owner
     * of this object's monitor. See the {@code notify} method for a
     * description of the ways in which a thread can become the owner of
     * a monitor.
     *  这个方法只能被拥有该对象监视器的线程锁调用，看notify方法的描述
     *  可以看到怎么成为监视器的拥有者
     * @param      timeout   the maximum time to wait in milliseconds.
     *                       等待的最大毫秒数
     * @throws  IllegalArgumentException      if the value of timeout is
     *               negative.
     * @throws  IllegalMonitorStateException  if the current thread is not
     *               the owner of the object's monitor.
     * @throws  InterruptedException if any thread interrupted the
     *             current thread before or while the current thread
     *             was waiting for a notification.  The <i>interrupted
     *             status</i> of the current thread is cleared when
     *             this exception is thrown.
     * @see        java.lang.Object#notify()
     * @see        java.lang.Object#notifyAll()
     */
    public final native void wait(long timeout) throws InterruptedException;

    /**
     * Causes the current thread to wait until another thread invokes the
     * {@link java.lang.Object#notify()} method or the
     * {@link java.lang.Object#notifyAll()} method for this object, or
     * some other thread interrupts the current thread, or a certain
     * amount of real time has elapsed.
     * <p>
     * This method is similar to the {@code wait} method of one
     * argument, but it allows finer control over the amount of time to
     * wait for a notification before giving up. The amount of real time,
     * measured in nanoseconds, is given by:
     * <blockquote>
     * <pre>
     * 1000000*timeout+nanos</pre></blockquote>
     * <p>
     * In all other respects, this method does the same thing as the
     * method {@link #wait(long)} of one argument. In particular,
     * {@code wait(0, 0)} means the same thing as {@code wait(0)}.
     * <p>
     * The current thread must own this object's monitor. The thread
     * releases ownership of this monitor and waits until either of the
     * following two conditions has occurred:
     * <ul>
     * <li>Another thread notifies threads waiting on this object's monitor
     *     to wake up either through a call to the {@code notify} method
     *     or the {@code notifyAll} method.
     * <li>The timeout period, specified by {@code timeout}
     *     milliseconds plus {@code nanos} nanoseconds arguments, has
     *     elapsed.
     * </ul>
     * <p>
     * The thread then waits until it can re-obtain ownership of the
     * monitor and resumes execution.
     * <p>
     * As in the one argument version, interrupts and spurious wakeups are
     * possible, and this method should always be used in a loop:
     * <pre>
     *     synchronized (obj) {
     *         while (&lt;condition does not hold&gt;)
     *             obj.wait(timeout, nanos);
     *         ... // Perform action appropriate to condition
     *     }
     * </pre>
     * This method should only be called by a thread that is the owner
     * of this object's monitor. See the {@code notify} method for a
     * description of the ways in which a thread can become the owner of
     * a monitor.
     *
     * @param      timeout   the maximum time to wait in milliseconds.
     * @param      nanos      additional time, in nanoseconds range
     *                       0-999999.
     * @throws  IllegalArgumentException      if the value of timeout is
     *                      negative or the value of nanos is
     *                      not in the range 0-999999.
     * @throws  IllegalMonitorStateException  if the current thread is not
     *               the owner of this object's monitor.
     * @throws  InterruptedException if any thread interrupted the
     *             current thread before or while the current thread
     *             was waiting for a notification.  The <i>interrupted
     *             status</i> of the current thread is cleared when
     *             this exception is thrown.
     */
    public final void wait(long timeout, int nanos) throws InterruptedException {
        if (timeout < 0) {
            throw new IllegalArgumentException("timeout value is negative");
        }

        if (nanos < 0 || nanos > 999999) {
            throw new IllegalArgumentException(
                    "nanosecond timeout value out of range");
        }

        // 只要nanos大于0，就加1毫秒
        if (nanos > 0) {
            timeout++;
        }

        wait(timeout);
    }

    /**
     * Causes the current thread to wait until another thread invokes the
     * {@link java.lang.Object#notify()} method or the
     * {@link java.lang.Object#notifyAll()} method for this object.
     * In other words, this method behaves exactly as if it simply
     * performs the call {@code wait(0)}.
     * <p>
     * The current thread must own this object's monitor. The thread
     * releases ownership of this monitor and waits until another thread
     * notifies threads waiting on this object's monitor to wake up
     * either through a call to the {@code notify} method or the
     * {@code notifyAll} method. The thread then waits until it can
     * re-obtain ownership of the monitor and resumes execution.
     * <p>
     * As in the one argument version, interrupts and spurious wakeups are
     * possible, and this method should always be used in a loop:
     * <pre>
     *     synchronized (obj) {
     *         while (&lt;condition does not hold&gt;)
     *             obj.wait();
     *         ... // Perform action appropriate to condition
     *     }
     * </pre>
     * This method should only be called by a thread that is the owner
     * of this object's monitor. See the {@code notify} method for a
     * description of the ways in which a thread can become the owner of
     * a monitor.
     *
     * @throws  IllegalMonitorStateException  if the current thread is not
     *               the owner of the object's monitor.
     * @throws  InterruptedException if any thread interrupted the
     *             current thread before or while the current thread
     *             was waiting for a notification.  The <i>interrupted
     *             status</i> of the current thread is cleared when
     *             this exception is thrown.
     * @see        java.lang.Object#notify()
     * @see        java.lang.Object#notifyAll()
     */
    public final void wait() throws InterruptedException {
        wait(0);
    }

    /**
     * Called by the garbage collector on an object when garbage collection
     * determines that there are no more references to the object.
     * A subclass overrides the {@code finalize} method to dispose of
     * system resources or to perform other cleanup.
     *  当垃圾回收器确定该对象没有其他对象引用的时候就会被调用。
     *  子类会覆盖该方法去处理系统资源或执行其他清理。
     *
     * <p>
     * The general contract of {@code finalize} is that it is invoked
     * if and when the Java&trade; virtual
     * machine has determined that there is no longer any
     * means by which this object can be accessed by any thread that has
     * not yet died, except as a result of an action taken by the
     * finalization of some other object or class which is ready to be
     * finalized. The {@code finalize} method may take any action, including
     * making this object available again to other threads; the usual purpose
     * of {@code finalize}, however, is to perform cleanup actions before
     * the object is irrevocably discarded. For example, the finalize method
     * for an object that represents an input/output connection might perform
     * explicit I/O transactions to break the connection before the object is
     * permanently discarded.
     *  （好难理解这段话，但是我看明白的一点就是，这个方法存在的可能意义是
     *      在允许对象被终止前执行一些操作）
     * <p>
     * The {@code finalize} method of class {@code Object} performs no
     * special action; it simply returns normally. Subclasses of
     * {@code Object} may override this definition.
     *  object的finalize并没有执行什么特殊操作，可能由子类去重写它
     * <p>
     * The Java programming language does not guarantee which thread will
     * invoke the {@code finalize} method for any given object. It is
     * guaranteed, however, that the thread that invokes finalize will not
     * be holding any user-visible synchronization locks when finalize is
     * invoked. If an uncaught exception is thrown by the finalize method,
     * the exception is ignored and finalization of that object terminates.
     *   java语言不保证哪一个对象一定会调用finalize方法。但是它保证
     *   当finalize方法调用的时候，没有任何一个线程会持有用户看得见的同步锁。
     *   如果finalize方法抛出异常，则该异常会被忽略并且该对象的finalize过程会被终止。
     * <p>
     * After the {@code finalize} method has been invoked for an object, no
     * further action is taken until the Java virtual machine has again
     * determined that there is no longer any means by which this object can
     * be accessed by any thread that has not yet died, including possible
     * actions by other objects or classes which are ready to be finalized,
     * at which point the object may be discarded.
     *  当调用了finalize方法之后，jvm不会采取其他的操作，直到jvm再次确定
     *  其他存在的线程不会调用到这个对象，则这个对象将会被回收。
     * <p>
     * The {@code finalize} method is never invoked more than once by a Java
     * virtual machine for any given object.
     *   jvm对于给定的任何对象，finalize方法都不会调用超过一次。
     * <p>
     * Any exception thrown by the {@code finalize} method causes
     * the finalization of this object to be halted, but is otherwise
     * ignored.
     *  由finalize方法引发的任何异常都将导致此对象的finalize过程终止，否则将被忽略。
     *
     * @throws Throwable the {@code Exception} raised by this method
     * @see java.lang.ref.WeakReference
     * @see java.lang.ref.PhantomReference
     * @jls 12.6 Finalization of Class Instances
     */
    protected void finalize() throws Throwable { }
}
