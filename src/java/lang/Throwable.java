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
import  java.io.*;
import  java.util.*;

/**
 * The {@code Throwable} class is the superclass of all errors and
 * exceptions in the Java language. Only objects that are instances of this
 * class (or one of its subclasses) are thrown by the Java Virtual Machine or
 * can be thrown by the Java {@code throw} statement. Similarly, only
 * this class or one of its subclasses can be the argument type in a
 * {@code catch} clause.
 * 该类是java语言中所有错误和异常的父类。只有这个类或者其子类的实例对象
 * 被jvm抛出或者说能够通过throw声明抛出。相似的，只有这个类和其子类的实例
 * 能够作为catch的参数类型
 * For the purposes of compile-time checking of exceptions, {@code
 * Throwable} and any subclass of {@code Throwable} that is not also a
 * subclass of either {@link RuntimeException} or {@link Error} are
 * regarded as checked exceptions.
 *  在编译检查时，Throwable还有非RuntionException和
 *  非Error的但属于Throwable子类被认为是检查时异常
 *
 * <p>Instances of two subclasses, {@link java.lang.Error} and
 * {@link java.lang.Exception}, are conventionally used to indicate
 * that exceptional situations have occurred. Typically, these instances
 * are freshly created in the context of the exceptional situation so
 * as to include relevant information (such as stack trace data).
 * Error和Exception是Throwable的子类，一般被用于表明异常情况的发生。
 * 一般情况下，这些实例是在异常情况下创建的以便包含相关的信息（如堆栈信息）
 * <p>A throwable contains a snapshot of the execution stack of its
 * thread at the time it was created. It can also contain a message
 * string that gives more information about the error. Over time, a
 * throwable can {@linkplain Throwable#addSuppressed suppress} other
 * throwables from being propagated.  Finally, the throwable can also
 * contain a <i>cause</i>: another throwable that caused this
 * throwable to be constructed.  The recording of this causal information
 * is referred to as the <i>chained exception</i> facility, as the
 * cause can, itself, have a cause, and so on, leading to a "chain" of
 * exceptions, each caused by another.
 *  一次抛出的创建包含了当前线程此刻的栈执行快照信息。它包含了给出的相关错误信息。
 *  随着项目运行，一个throwable能够包含其他throwable的传播。最后，throwable能够
 *  得出一个原因：另外的异常抛出导致问题的发生。（其实就是说链式的异常结构，
 *  *当发生异常的时候，我们可以看到异常是一层一层套进去的。）这种因果关系的
 *  记录信息就是所谓的链式异常，因为原因本身可以具有原因，依此类推，导致异常的“链”，
 *  每个异常都是由另一个原因引起的。
 *
 * <p>One reason that a throwable may have a cause is that the class that
 * throws it is built atop a lower layered abstraction, and an operation on
 * the upper layer fails due to a failure in the lower layer.  It would be bad
 * design to let the throwable thrown by the lower layer propagate outward, as
 * it is generally unrelated to the abstraction provided by the upper layer.
 * Further, doing so would tie the API of the upper layer to the details of
 * its implementation, assuming the lower layer's exception was a checked
 * exception.  Throwing a "wrapped exception" (i.e., an exception containing a
 * cause) allows the upper layer to communicate the details of the failure to
 * its caller without incurring either of these shortcomings.  It preserves
 * the flexibility to change the implementation of the upper layer without
 * changing its API (in particular, the set of exceptions thrown by its
 * methods).
 *
 * 一个throwable可能有原因的一个原因是，将throwable的类构建在一个较低层的抽象之上，并且由于较低层的故障，
 * 导致较高层的操作失败。 让下层抛出的可抛物向外传播是不好的设计，因为它通常与上层提供的抽象无关。
 * 此外，假设下层的异常是已检查的异常，那么这样做会将上层的API与实现的细节联系起来。 引发“包装的异常”
 * （即包含原因的异常）使上层可以将失败的详细信息传达给其调用方，而不会引起这些缺点。 它保留了在不更改其
 * API（特别是其方法引发的异常集）的情况下更改上层实现的灵活性。
 *
 * <p>A second reason that a throwable may have a cause is that the method
 * that throws it must conform to a general-purpose interface that does not
 * permit the method to throw the cause directly.  For example, suppose
 * a persistent collection conforms to the {@link java.util.Collection
 * Collection} interface, and that its persistence is implemented atop
 * {@code java.io}.  Suppose the internals of the {@code add} method
 * can throw an {@link java.io.IOException IOException}.  The implementation
 * can communicate the details of the {@code IOException} to its caller
 * while conforming to the {@code Collection} interface by wrapping the
 * {@code IOException} in an appropriate unchecked exception.  (The
 * specification for the persistent collection should indicate that it is
 * capable of throwing such exceptions.)
 *
 * 一个throwable可能有原因的第二个原因是，引发一个throwable的方法必须符合
 * 一个通用接口，该接口不允许该方法直接引发原因。 例如，假设持久性集合
 * 符合java.util.Collection Collection接口，并且其持久性是在java.io之上实现的。
 * 假设add方法的内部可以引发java.io.IOException IOException。
 * 通过将IOException包装在适当的未经检查的异常中，实现可以在符合
 * Collection接口的同时将IOException的详细信息传达给其调用方。
 * （关于持久性集合的规范应表明它能够引发此类异常。）
 *(这段话说的我有点懵，但我的理解是我们自定义一个异常，该异常包装了
 * IOException，抛出该异常的时候也要将IOException的信息描述清楚)
 * <p>A cause can be associated with a throwable in two ways: via a
 * constructor that takes the cause as an argument, or via the
 * {@link #initCause(Throwable)} method.  New throwable classes that
 * wish to allow causes to be associated with them should provide constructors
 * that take a cause and delegate (perhaps indirectly) to one of the
 * {@code Throwable} constructors that takes a cause.
 *  原因与抛出事件相关联的方式一般有两个：通过将原因作为参数的构造器，或者通过
 *  initCause（Throwable）方法。建议采用构造器的方式建立关联。
 * Because the {@code initCause} method is public, it allows a cause to be
 * associated with any throwable, even a "legacy throwable" whose
 * implementation predates the addition of the exception chaining mechanism to
 * {@code Throwable}.
 * 因为initCause方法是公共方法，它允许原因与任何抛出相关，该throwable的实现
 * 早于将异常链接机制添加到Throwable代码中（不懂不懂。。。）
 *
 * <p>By convention, class {@code Throwable} and its subclasses have two
 * constructors, one that takes no arguments and one that takes a
 * {@code String} argument that can be used to produce a detail message.
 * Further, those subclasses that might likely have a cause associated with
 * them should have two more constructors, one that takes a
 * {@code Throwable} (the cause), and one that takes a
 * {@code String} (the detail message) and a {@code Throwable} (the
 * cause).
 * 一般而言，Throwable类和它的子类有两个构造器，一个无参，一个用string做参数
 * 能够产生细节信息。此外，Throwable的子类应该两个甚至更多的构造器，一个用
 * cause做参数，另外一个用cause和信息做参数
 * @author  unascribed
 * @author  Josh Bloch (Added exception chaining and programmatic access to
 *          stack trace in 1.4.)
 * @jls 11.2 Compile-Time Checking of Exceptions
 * @since JDK1.0
 */
public class Throwable implements Serializable {
    /** use serialVersionUID from JDK 1.0.2 for interoperability */
    private static final long serialVersionUID = -3042686055658047285L;

    /**
     * Native code saves some indication of the stack backtrace in this slot.
     * 本机代码在该插槽中保存了一些有关堆栈回溯的指示。
     */
    private transient Object backtrace;

    /**
     * Specific details about the Throwable.  For example, for
     * {@code FileNotFoundException}, this contains the name of
     * the file that could not be found.
     * 抛出的特定细节信息。例如，文件找不到异常，该异常包含了找不到的文件名称
     * @serial
     */
    private String detailMessage;


    /**
     * Holder class to defer initializing sentinel objects only used
     * for serialization.
     * Holder类用于推迟仅用于序列化的哨兵对象的初始化。（这么说有点糊涂，看代码）
     */
    private static class SentinelHolder {
        /**
         * {@linkplain #setStackTrace(StackTraceElement[]) Setting the
         * stack trace} to a one-element array containing this sentinel
         * value indicates future attempts to set the stack trace will be
         * ignored.  The sentinal is equal to the result of calling:<br>
         * {@code new StackTraceElement("", "", null, Integer.MIN_VALUE)}
         * 将堆栈跟踪设置为包含此前哨值的单元素数组表示将来设置堆栈跟踪的尝试将被忽略。
         * 这个前哨等价于调用new StackTraceElement("", "", null, Integer.MIN_VALUE)（其实就是下面的实例）
         * （这段话是说定义了一个堆栈跟踪数组，而这个数组的初始化元素就是下面的这个STACK_TRACE_ELEMENT_SENTINEL）
         */
        public static final StackTraceElement STACK_TRACE_ELEMENT_SENTINEL =
            new StackTraceElement("", "", null, Integer.MIN_VALUE);

        /**
         * Sentinel value used in the serial form to indicate an immutable
         * stack trace.
         * 以串行形式使用的前哨值，表示不可变的堆栈跟踪。
         */
        public static final StackTraceElement[] STACK_TRACE_SENTINEL =
            new StackTraceElement[] {STACK_TRACE_ELEMENT_SENTINEL};
    }

    /**
     * A shared value for an empty stack.
     * 一个共享的空堆栈数组
     */
    private static final StackTraceElement[] UNASSIGNED_STACK = new StackTraceElement[0];

    /*
     * To allow Throwable objects to be made immutable and safely
     * reused by the JVM, such as OutOfMemoryErrors, fields of
     * Throwable that are writable in response to user actions, cause,
     * stackTrace, and suppressedExceptions obey the following
     * protocol:
     *  为了允许Throwable对象不可变并且能被jvm安全再使用，例如内存溢出错误，
     *  可响应用户而写入的Throwabel的字段，原因，堆栈跟踪，还有异常应遵循以下协议：
     * 1) The fields are initialized to a non-null sentinel value
     * which indicates the value has logically not been set.
     *  前哨值被初始化为非null表明这个值逻辑上未被设置
     * 2) Writing a null to the field indicates further writes
     * are forbidden
     *   前哨值初始为null表明不能再写入更多内容
     * 3) The sentinel value may be replaced with another non-null
     * value.
     *  前哨值可能被另外一个非空值所取代
     *
     * For example, implementations of the HotSpot JVM have
     * preallocated OutOfMemoryError objects to provide for better
     * diagnosability of that situation.  These objects are created
     * without calling the constructor for that class and the fields
     * in question are initialized to null.  To support this
     * capability, any new fields added to Throwable that require
     * being initialized to a non-null value require a coordinated JVM
     * change.
     *
     * 例如，HotSpot虚拟机的实现方式是预分配内存溢出对象来更好地诊断这种情况。
     * 这些对象的创建没有调用类构造方法并且字段被初始化为null。
     * 为了支持此功能，添加到Throwable中的任何新字段都需要初始化为非null值，需要协调的JVM更改。
     */

    /**
     * The throwable that caused this throwable to get thrown, or null if this
         * throwable was not caused by another throwable, or if the causative
     * throwable is unknown.  If this field is equal to this throwable itself,
     * it indicates that the cause of this throwable has not yet been
     * initialized.
     *   引发此一场抛出的throwable对象，如果不是被任何一个throwable对象抛出，
     *   则该值为null，或者导致可引发的异常是未知的。如果该字段等价于这个抛出本身，
     *   它表明抛出异常的原因没被初始化
     * @serial
     * @since 1.4
     */
    private Throwable cause = this;

    /**
     * The stack trace, as returned by {@link #getStackTrace()}.
     *  堆栈跟踪，被getStackTrace方法返回
     * The field is initialized to a zero-length array.  A {@code
     * null} value of this field indicates subsequent calls to {@link
     * #setStackTrace(StackTraceElement[])} and {@link
     * #fillInStackTrace()} will be be no-ops.
     * 该字段被初始化为长度为0的数字，该字段为null表明接下来调用
     * setStackTrace方法和fillInStackTrace（）将为空操作。
     * @serial
     * @since 1.4
     */
    private StackTraceElement[] stackTrace = UNASSIGNED_STACK;

    // Setting this static field introduces an acceptable
    // initialization dependency on a few java.util classes.
   // 设置此静态字段会引入对几个java.util类的可接受的初始化依赖关系。
    private static final List<Throwable> SUPPRESSED_SENTINEL =
        Collections.unmodifiableList(new ArrayList<Throwable>(0));

    /**
     * The list of suppressed exceptions, as returned by {@link
     * #getSuppressed()}.  The list is initialized to a zero-element
     * unmodifiable sentinel list.  When a serialized Throwable is
     * read in, if the {@code suppressedExceptions} field points to a
     * zero-element list, the field is reset to the sentinel value.
     *  压入栈的异常，通过getSuppressed方法返回。该列表是初始化长度为-的不可修改
     *  的前哨列表。当一个连续的throw被读取，如果受压异常字段指向0元素列表，
     *  则这个字段被重新设置为前哨值
     * @serial
     * @since 1.7
     */
    private List<Throwable> suppressedExceptions = SUPPRESSED_SENTINEL;

    /** Message for trying to suppress a null exception. */
    // 尝试压入空异常的消息
    private static final String NULL_CAUSE_MESSAGE = "Cannot suppress a null exception.";

    /** Message for trying to suppress oneself. */
    //  尝试压入自身的信息
    private static final String SELF_SUPPRESSION_MESSAGE = "Self-suppression not permitted";

    /** Caption  for labeling causative exception stack traces */
    //标记原因异常堆栈跟踪的标题
    private static final String CAUSE_CAPTION = "Caused by: ";

    /** Caption for labeling suppressed exception stack traces */
    // 标记受压异常堆栈跟踪的标题
    private static final String SUPPRESSED_CAPTION = "Suppressed: ";

    /**
     * Constructs a new throwable with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     * 构造空的一个新的throwable对象作为细节信息。该原因未初始化，并且
     * 接下来可能调用initCause来初始化
     *
     * <p>The {@link #fillInStackTrace()} method is called to initialize
     * the stack trace data in the newly created throwable.
     *  fillInStackTrace方法被调用来初始化新创建堆栈数据的throwable对象。
     */
    public Throwable() {
        fillInStackTrace();
    }

    /**
     * Constructs a new throwable with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     * 构造参数为message的一个新的throwable对象作为细节信息。该原因未初始化，并且
     * 接下来可能调用initCause来初始化
     *
     * <p>The {@link #fillInStackTrace()} method is called to initialize
     * the stack trace data in the newly created throwable.
     *
     * @param   message   the detail message. The detail message is saved for
     *          later retrieval by the {@link #getMessage()} method.
     * 将保存详细消息，以供以后通过getMessage（）方法检索。
     */
    public Throwable(String message) {
        fillInStackTrace();
        detailMessage = message;
    }

    /**
     * Constructs a new throwable with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this throwable's detail message.
     *  以msg和cause作为参数构造新的throwable对象。注意msg
     *不会自动合并到此throwable的详细消息中。
     * <p>The {@link #fillInStackTrace()} method is called to initialize
     * the stack trace data in the newly created throwable.
     *
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A {@code null} value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     * @since  1.4
     */
    public Throwable(String message, Throwable cause) {
        fillInStackTrace();
        detailMessage = message;
        this.cause = cause;
    }

    /**
     * Constructs a new throwable with the specified cause and a detail
     * message of {@code (cause==null ? null : cause.toString())} (which
     * typically contains the class and detail message of {@code cause}).
     * This constructor is useful for throwables that are little more than
     * wrappers for other throwables (for example, {@link
     * java.security.PrivilegedActionException}).
     * 构造一个新的throwable对象，该对象包含cause和细节信息。
     * 此构造函数对仅用于其他throwable的包装器的throwable有用。
     * <p>The {@link #fillInStackTrace()} method is called to initialize
     * the stack trace data in the newly created throwable.
     *
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A {@code null} value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     * @since  1.4
     */
    public Throwable(Throwable cause) {
        fillInStackTrace();
        detailMessage = (cause==null ? null : cause.toString());
        this.cause = cause;
    }

    /**
     * Constructs a new throwable with the specified detail message,
     * cause, {@linkplain #addSuppressed suppression} enabled or
     * disabled, and writable stack trace enabled or disabled.  If
     * suppression is disabled, {@link #getSuppressed} for this object
     * will return a zero-length array and calls to {@link
     * #addSuppressed} that would otherwise append an exception to the
     * suppressed list will have no effect.  If the writable stack
     * trace is false, this constructor will not call {@link
     * #fillInStackTrace()}, a {@code null} will be written to the
     * {@code stackTrace} field, and subsequent calls to {@code
     * fillInStackTrace} and {@link
     * #setStackTrace(StackTraceElement[])} will not set the stack
     * trace.  If the writable stack trace is false, {@link
     * #getStackTrace} will return a zero length array.
     *  构造新throwable对象，带有msg，casue，suppersssion（能不能压入），
     *  wrirabel（能不能写入）为参数。如果suppression为false，则getSuppressed方法
     *  将会返回一个长度为0的数组并且调用addSuppressed方法用来添加异常到supperssed列表
     *  将不起作用。如果writable为false，这个构造器将不能调用fillInStackTrace方法，stackTrace将
     *  被赋值为null，并且调用fillInStackTrace和setStackTrace将不能设置堆栈信息。如果writable为false，
     *  将会返回一个长度为0的数组
     * <p>Note that the other constructors of {@code Throwable} treat
     * suppression as being enabled and the stack trace as being
     * writable.  Subclasses of {@code Throwable} should document any
     * conditions under which suppression is disabled and document
     * conditions under which the stack trace is not writable.
     * Disabling of suppression should only occur in exceptional
     * circumstances where special requirements exist, such as a
     * virtual machine reusing exception objects under low-memory
     * situations.  Circumstances where a given exception object is
     * repeatedly caught and rethrown, such as to implement control
     * flow between two sub-systems, is another situation where
     * immutable throwable objects would be appropriate.
     *  注意该类其他构造器默认能呀如并且堆栈跟踪可写。该类的子类应该什么条件下
     *  不能压入还有什么情况下不能写入。不能压入只应该发生在特殊要求情况下，
     *  例如虚拟机在第内存情况下再次使用异常对象。反复捕获并重新抛出给定异常对象
     *  （例如在两个子系统之间实现控制流）的情况是不可变的可抛出对象将是适当的另一种情况。
     * @param  message the detail message.
     * @param cause the cause.  (A {@code null} value is permitted,
     * and indicates that the cause is nonexistent or unknown.)
     * @param enableSuppression whether or not suppression is enabled or disabled
     * @param writableStackTrace whether or not the stack trace should be
     *                           writable
     *
     * @see OutOfMemoryError
     * @see NullPointerException
     * @see ArithmeticException
     * @since 1.7
     */
    protected Throwable(String message, Throwable cause,
                        boolean enableSuppression,
                        boolean writableStackTrace) {
        if (writableStackTrace) {
            fillInStackTrace();
        } else {
            stackTrace = null;
        }
        detailMessage = message;
        this.cause = cause;
        if (!enableSuppression)
            suppressedExceptions = null;
    }

    /**
     * Returns the detail message string of this throwable.
     *
     * @return  the detail message string of this {@code Throwable} instance
     *          (which may be {@code null}).
     */
    public String getMessage() {
        return detailMessage;
    }

    /**
     * Creates a localized description of this throwable.
     * Subclasses may override this method in order to produce a
     * locale-specific message.  For subclasses that do not override this
     * method, the default implementation returns the same result as
     * {@code getMessage()}.
     * 创建当前对象的本地化描述。子类可能会重写该方法去产生一个
     * 特定于本地的信息。如果子类不重写，则返回getMessage；
     *
     * @return  The localized description of this throwable.
     * @since   JDK1.1
     */
    public String getLocalizedMessage() {
        return getMessage();
    }

    /**
     * Returns the cause of this throwable or {@code null} if the
     * cause is nonexistent or unknown.  (The cause is the throwable that
     * caused this throwable to get thrown.)
     * 如果原因为null或者不知道，则返回null。
     *
     * <p>This implementation returns the cause that was supplied via one of
     * the constructors requiring a {@code Throwable}, or that was set after
     * creation with the {@link #initCause(Throwable)} method.  While it is
     * typically unnecessary to override this method, a subclass can override
     * it to return a cause set by some other means.  This is appropriate for
     * a "legacy chained throwable" that predates the addition of chained
     * exceptions to {@code Throwable}.  Note that it is <i>not</i>
     * necessary to override any of the {@code PrintStackTrace} methods,
     * all of which invoke the {@code getCause} method to determine the
     * cause of a throwable.
     *  casue值的设置是通过当前对象的构造器或者initCause方法。一般不需要重写该方法，
     *  子类能够通过其他方式重写该方法去返回cause。注意没必要重写PrintStackTrace方法，
     *  该方法调用getCause方法去确定抛出的原因
     *
     * @return  the cause of this throwable or {@code null} if the
     *          cause is nonexistent or unknown.
     * @since 1.4
     */
    public synchronized Throwable getCause() {
        return (cause==this ? null : cause);
    }

    /**
     * Initializes the <i>cause</i> of this throwable to the specified value.
     * (The cause is the throwable that caused this throwable to get thrown.)
     *  初始化当前类 cause变量为特定值（该变量是造成当前对象抛出的原因）
     * <p>This method can be called at most once.  It is generally called from
     * within the constructor, or immediately after creating the
     * throwable.  If this throwable was created
     * with {@link #Throwable(Throwable)} or
     * {@link #Throwable(String,Throwable)}, this method cannot be called
     * even once.
     * 这个方法最多被调用一次。它一般在构造器中调用，或者在创建throwable对象后立即调用。
     * 如果当前对象是通过Throwable(Throwable)或Throwable(String,Throwable)调用，则
     * 该方法一次都不能被调用
     * <p>An example of using this method on a legacy throwable type
     * without other support for setting the cause is:
     *在没有其他支持原因设置的情况下，在旧式可抛出类型上使用此方法的示例是
     * <pre>
     * try {
     *     lowLevelOp();
     * } catch (LowLevelException le) {
     *     throw (HighLevelException)
     *           new HighLevelException().initCause(le); // Legacy constructor
     * }
     * </pre>
     *
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A {@code null} value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     *          造成该抛出的原因（保存cause以便getCause方法调用）。
     *          该值允许为null表明cause未存在或不可知
     * @return  a reference to this {@code Throwable} instance.
     * @throws IllegalArgumentException if {@code cause} is this
     *         throwable.  (A throwable cannot be its own cause.)
     * @throws IllegalStateException if this throwable was
     *         created with {@link #Throwable(Throwable)} or
     *         {@link #Throwable(String,Throwable)}, or this method has already
     *         been called on this throwable.
     * @since  1.4
     */
    public synchronized Throwable initCause(Throwable cause) {
        if (this.cause != this)
            throw new IllegalStateException("Can't overwrite cause with " +
                                            Objects.toString(cause, "a null"), this);
        if (cause == this)
            throw new IllegalArgumentException("Self-causation not permitted", this);
        this.cause = cause;
        return this;
    }

    /**
     * Returns a short description of this throwable.
     * The result is the concatenation of:
     * <ul>
     * <li> the {@linkplain Class#getName() name} of the class of this object
     * <li> ": " (a colon and a space)
     * <li> the result of invoking this object's {@link #getLocalizedMessage}
     *      method
     * </ul>
     * If {@code getLocalizedMessage} returns {@code null}, then just
     * the class name is returned.
     *
     * @return a string representation of this throwable.
     */
    public String toString() {
        // 获取当前类（子类）的名称
        String s = getClass().getName();
        String message = getLocalizedMessage();
        return (message != null) ? (s + ": " + message) : s;
    }

    /**
     * Prints this throwable and its backtrace to the
     * standard error stream. This method prints a stack trace for this
     * {@code Throwable} object on the error output stream that is
     * the value of the field {@code System.err}. The first line of
     * output contains the result of the {@link #toString()} method for
     * this object.  Remaining lines represent data previously recorded by
     * the method {@link #fillInStackTrace()}. The format of this
     * information depends on the implementation, but the following
     * example may be regarded as typical:
     * <blockquote><pre>
     * java.lang.NullPointerException
     *         at MyClass.mash(MyClass.java:9)
     *         at MyClass.crunch(MyClass.java:6)
     *         at MyClass.main(MyClass.java:3)
     * </pre></blockquote>
     * This example was produced by running the program:
     * <pre>
     * class MyClass {
     *     public static void main(String[] args) {
     *         crunch(null);
     *     }
     *     static void crunch(int[] a) {
     *         mash(a);
     *     }
     *     static void mash(int[] b) {
     *         System.out.println(b[0]);
     *     }
     * }
     * </pre>
     * The backtrace for a throwable with an initialized, non-null cause
     * should generally include the backtrace for the cause.  The format
     * of this information depends on the implementation, but the following
     * example may be regarded as typical:
     * <pre>
     * HighLevelException: MidLevelException: LowLevelException
     *         at Junk.a(Junk.java:13)
     *         at Junk.main(Junk.java:4)
     * Caused by: MidLevelException: LowLevelException
     *         at Junk.c(Junk.java:23)
     *         at Junk.b(Junk.java:17)
     *         at Junk.a(Junk.java:11)
     *         ... 1 more
     * Caused by: LowLevelException
     *         at Junk.e(Junk.java:30)
     *         at Junk.d(Junk.java:27)
     *         at Junk.c(Junk.java:21)
     *         ... 3 more
     * </pre>
     * Note the presence of lines containing the characters {@code "..."}.
     * These lines indicate that the remainder of the stack trace for this
     * exception matches the indicated number of frames from the bottom of the
     * stack trace of the exception that was caused by this exception (the
     * "enclosing" exception).  This shorthand can greatly reduce the length
     * of the output in the common case where a wrapped exception is thrown
     * from same method as the "causative exception" is caught.  The above
     * example was produced by running the program:
     * <pre>
     * public class Junk {
     *     public static void main(String args[]) {
     *         try {
     *             a();
     *         } catch(HighLevelException e) {
     *             e.printStackTrace();
     *         }
     *     }
     *     static void a() throws HighLevelException {
     *         try {
     *             b();
     *         } catch(MidLevelException e) {
     *             throw new HighLevelException(e);
     *         }
     *     }
     *     static void b() throws MidLevelException {
     *         c();
     *     }
     *     static void c() throws MidLevelException {
     *         try {
     *             d();
     *         } catch(LowLevelException e) {
     *             throw new MidLevelException(e);
     *         }
     *     }
     *     static void d() throws LowLevelException {
     *        e();
     *     }
     *     static void e() throws LowLevelException {
     *         throw new LowLevelException();
     *     }
     * }
     *
     * class HighLevelException extends Exception {
     *     HighLevelException(Throwable cause) { super(cause); }
     * }
     *
     * class MidLevelException extends Exception {
     *     MidLevelException(Throwable cause)  { super(cause); }
     * }
     *
     * class LowLevelException extends Exception {
     * }
     * </pre>
     * As of release 7, the platform supports the notion of
     * <i>suppressed exceptions</i> (in conjunction with the {@code
     * try}-with-resources statement). Any exceptions that were
     * suppressed in order to deliver an exception are printed out
     * beneath the stack trace.  The format of this information
     * depends on the implementation, but the following example may be
     * regarded as typical:
     *
     * <pre>
     * Exception in thread "main" java.lang.Exception: Something happened
     *  at Foo.bar(Foo.java:10)
     *  at Foo.main(Foo.java:5)
     *  Suppressed: Resource$CloseFailException: Resource ID = 0
     *          at Resource.close(Resource.java:26)
     *          at Foo.bar(Foo.java:9)
     *          ... 1 more
     * </pre>
     * Note that the "... n more" notation is used on suppressed exceptions
     * just at it is used on causes. Unlike causes, suppressed exceptions are
     * indented beyond their "containing exceptions."
     *
     * <p>An exception can have both a cause and one or more suppressed
     * exceptions:
     * <pre>
     * Exception in thread "main" java.lang.Exception: Main block
     *  at Foo3.main(Foo3.java:7)
     *  Suppressed: Resource$CloseFailException: Resource ID = 2
     *          at Resource.close(Resource.java:26)
     *          at Foo3.main(Foo3.java:5)
     *  Suppressed: Resource$CloseFailException: Resource ID = 1
     *          at Resource.close(Resource.java:26)
     *          at Foo3.main(Foo3.java:5)
     * Caused by: java.lang.Exception: I did it
     *  at Foo3.main(Foo3.java:8)
     * </pre>
     * Likewise, a suppressed exception can have a cause:
     * <pre>
     * Exception in thread "main" java.lang.Exception: Main block
     *  at Foo4.main(Foo4.java:6)
     *  Suppressed: Resource2$CloseFailException: Resource ID = 1
     *          at Resource2.close(Resource2.java:20)
     *          at Foo4.main(Foo4.java:5)
     *  Caused by: java.lang.Exception: Rats, you caught me
     *          at Resource2$CloseFailException.&lt;init&gt;(Resource2.java:45)
     *          ... 2 more
     * </pre>
     *
     * 该方法是打印异常的输出流信息。其他的内容很多都描述了异常打印的格式，
     * 平时异常报错我们也经常看到过。
     */
    public void printStackTrace() {
        printStackTrace(System.err);
    }

    /**
     * Prints this throwable and its backtrace to the specified print stream.
     * 输出抛出的异常和它的堆栈给这个特定的打印流
     * @param s {@code PrintStream} to use for output
     */
    public void printStackTrace(PrintStream s) {
        printStackTrace(new WrappedPrintStream(s));
    }

    private void printStackTrace(PrintStreamOrWriter s) {
        // Guard against malicious overrides of Throwable.equals by
        // using a Set with identity equality semantics.
        // 防止有人而已重写Throwable，故用了set来去重
        Set<Throwable> dejaVu =
            Collections.newSetFromMap(new IdentityHashMap<Throwable, Boolean>());
        dejaVu.add(this);

        synchronized (s.lock()) {

            // 以下面的异常信息为例
            /*Exception in thread "main" java.lang.Exception: Something happened
             at Foo.bar(Foo.java:10)
                at Foo.main(Foo.java:5)*/

            // Print our stack trace
            // 打印我们的堆栈跟踪。这里第一个println打印的是最底层调用的方法信息
            // 打印的是这一句Exception in thread "main" java.lang.Exception: Something happened
            s.println(this);

            // 获取堆栈信息后打印出来
            StackTraceElement[] trace = getOurStackTrace();
            for (StackTraceElement traceElement : trace)
                //  打印的是这两句
                //   at Foo.bar(Foo.java:10)
                //    at Foo.main(Foo.java:5)
                s.println("\tat " + traceElement);


           /* 打印的下面的信息
                Suppressed: Resource2$CloseFailException: Resource ID = 1
             at Resource2.close(Resource2.java:20)
             at Foo4.main(Foo4.java:5)*/
            // Print suppressed exceptions, if any
            for (Throwable se : getSuppressed())
                se.printEnclosedStackTrace(s, trace, SUPPRESSED_CAPTION, "\t", dejaVu);

            /*
            打印的是下面的信息
                Caused by: java.lang.Exception: Rats, you caught me
             at Resource2$CloseFailException.&lt;init&gt;(Resource2.java:45)
          ... 2 more*/
            // Print cause, if any
            Throwable ourCause = getCause();
            if (ourCause != null)
                ourCause.printEnclosedStackTrace(s, trace, CAUSE_CAPTION, "", dejaVu);
        }
    }

    /**
     * Print our stack trace as an enclosed exception for the specified
     * stack trace.
     */
    private void printEnclosedStackTrace(PrintStreamOrWriter s,
                                         StackTraceElement[] enclosingTrace,
                                         String caption,
                                         String prefix,
                                         Set<Throwable> dejaVu) {
        assert Thread.holdsLock(s.lock());
        if (dejaVu.contains(this)) {
            s.println("\t[CIRCULAR REFERENCE:" + this + "]");
        } else {
            dejaVu.add(this);
            // Compute number of frames in common between this and enclosing trace
            StackTraceElement[] trace = getOurStackTrace();
            int m = trace.length - 1;
            int n = enclosingTrace.length - 1;
            while (m >= 0 && n >=0 && trace[m].equals(enclosingTrace[n])) {
                m--; n--;
            }
            int framesInCommon = trace.length - 1 - m;

            // Print our stack trace
            s.println(prefix + caption + this);
            for (int i = 0; i <= m; i++)
                s.println(prefix + "\tat " + trace[i]);
            if (framesInCommon != 0)
                s.println(prefix + "\t... " + framesInCommon + " more");

            // Print suppressed exceptions, if any
            for (Throwable se : getSuppressed())
                se.printEnclosedStackTrace(s, trace, SUPPRESSED_CAPTION,
                                           prefix +"\t", dejaVu);

            // Print cause, if any
            Throwable ourCause = getCause();
            if (ourCause != null)
                ourCause.printEnclosedStackTrace(s, trace, CAUSE_CAPTION, prefix, dejaVu);
        }
    }

    /**
     * Prints this throwable and its backtrace to the specified
     * print writer.
     * 打印当前对象和它的堆栈信息到特定的输出流
     * @param s {@code PrintWriter} to use for output
     * @since   JDK1.1
     */
    public void printStackTrace(PrintWriter s) {
        printStackTrace(new WrappedPrintWriter(s));
    }

    /**
     * Wrapper class for PrintStream and PrintWriter to enable a single
     * implementation of printStackTrace.
     *  包装PrintStream和PrintWriter，使他们能够作为参数直接用printStackTrace方法
     */
    private abstract static class PrintStreamOrWriter {
        /** Returns the object to be locked when using this StreamOrWriter */
        // 返回使用该对象的锁
        abstract Object lock();

        /** Prints the specified string as a line on this StreamOrWriter */
        // 打印特定信息
        abstract void println(Object o);
    }

    private static class WrappedPrintStream extends PrintStreamOrWriter {
        private final PrintStream printStream;

        WrappedPrintStream(PrintStream printStream) {
            this.printStream = printStream;
        }

        Object lock() {
            return printStream;
        }

        void println(Object o) {
            printStream.println(o);
        }
    }

    private static class WrappedPrintWriter extends PrintStreamOrWriter {
        private final PrintWriter printWriter;

        WrappedPrintWriter(PrintWriter printWriter) {
            this.printWriter = printWriter;
        }

        Object lock() {
            return printWriter;
        }

        void println(Object o) {
            printWriter.println(o);
        }
    }

    /**
     * Fills in the execution stack trace. This method records within this
     * {@code Throwable} object information about the current state of
     * the stack frames for the current thread.
     *  填写执行堆栈跟踪。该方法记录了当前对象有关当前线程的堆栈帧的状态
     * <p>If the stack trace of this {@code Throwable} {@linkplain
     * Throwable#Throwable(String, Throwable, boolean, boolean) is not
     * writable}, calling this method has no effect.
     *  如果Throwable(String, Throwable, boolean, boolean) 这个构造方法不可写，
     *  则这个方法不起作用
     * @return  a reference to this {@code Throwable} instance.
     * @see     java.lang.Throwable#printStackTrace()
     */
    public synchronized Throwable fillInStackTrace() {
        //  stackTrace为null的情况是调用了Throwable(String, Throwable, boolean, boolean)
        // 构造方法，而backtrace在当前类却没见过有赋值。
        if (stackTrace != null ||
            backtrace != null /* Out of protocol state */ ) {
            fillInStackTrace(0); // 调用本地方法，开辟内存空间，设置堆栈跟踪
            stackTrace = UNASSIGNED_STACK;
        }
        return this;
    }

    private native Throwable fillInStackTrace(int dummy);

    /**
     * Provides programmatic access to the stack trace information printed by
     * {@link #printStackTrace()}.  Returns an array of stack trace elements,
     * each representing one stack frame.  The zeroth element of the array
     * (assuming the array's length is non-zero) represents the top of the
     * stack, which is the last method invocation in the sequence.  Typically,
     * this is the point at which this throwable was created and thrown.
     * The last element of the array (assuming the array's length is non-zero)
     * represents the bottom of the stack, which is the first method invocation
     * in the sequence.
     * 通过该方法提供以编程方式访问的堆栈跟踪信息并打印出来。返回堆栈元素数组，
     * 每一个代表一个栈帧。数组的第一个（假设数组长度非0）代表栈帧的最顶部，
     * 它是队列中最后一个调用的方法。一般而言，这是当前对象创建和抛出的点。
     * 数组最后一个元素代表栈的底部，是第一个调用的方法。
     *
     * <p>Some virtual machines may, under some circumstances, omit one
     * or more stack frames from the stack trace.  In the extreme case,
     * a virtual machine that has no stack trace information concerning
     * this throwable is permitted to return a zero-length array from this
     * method.  Generally speaking, the array returned by this method will
     * contain one element for every frame that would be printed by
     * {@code printStackTrace}.  Writes to the returned array do not
     * affect future calls to this method.
     *  一些虚拟机可能在特定环境下，从堆栈跟踪中发送一个或多个栈帧。在极端情况下，
     *  没有当前对象栈帧信息的虚拟机允许返回一个长度为0的数组。一般而言，通过这个
     *  方法返回的数组将会包含每个打印信息的帧元素。写入返回的数组不会影响
     *  以后对该方法的调用。
     *
     * @return an array of stack trace elements representing the stack trace
     *         pertaining to this throwable.
     * @since  1.4
     */
    public StackTraceElement[] getStackTrace() {
        return getOurStackTrace().clone();
    }

    private synchronized StackTraceElement[] getOurStackTrace() {
        // Initialize stack trace field with information from
        // backtrace if this is the first call to this method
        // 首次调用该方法初始化由backtrace字段所定义的堆栈信息
        if (stackTrace == UNASSIGNED_STACK ||
            (stackTrace == null && backtrace != null) /* Out of protocol state */) {
            // 第一次调用，则获取栈深度
            int depth = getStackTraceDepth();
            // 初始化堆栈数组（StackTraceElement包含哪个文件，类名，方法名，第几行的信息）
            stackTrace = new StackTraceElement[depth];
            for (int i=0; i < depth; i++)
                // 调用本地方法，将所有的入栈异常信息都找出来，并且赋值给stackTrace
                stackTrace[i] = getStackTraceElement(i);
        } else if (stackTrace == null) {
            return UNASSIGNED_STACK;
        }
        return stackTrace;
    }

    /**
     * Sets the stack trace elements that will be returned by
     * {@link #getStackTrace()} and printed by {@link #printStackTrace()}
     * and related methods.
     *  设置栈帧元素，这些元素将会通过getStackTrace返回和打印
     *
     * This method, which is designed for use by RPC frameworks and other
     * advanced systems, allows the client to override the default
     * stack trace that is either generated by {@link #fillInStackTrace()}
     * when a throwable is constructed or deserialized when a throwable is
     * read from a serialization stream.
     * 这个方法，被设计用于RPC框架和其他先进系统，当构造当前对象或者从序列化流中反序列化
     * 异常信息时，允许客户端重写由fillInStackTrace方法生成的默认堆栈信息
     *
     * <p>If the stack trace of this {@code Throwable} {@linkplain
     * Throwable#Throwable(String, Throwable, boolean, boolean) is not
     * writable}, calling this method has no effect other than
     * validating its argument.
     *
     * @param   stackTrace the stack trace elements to be associated with
     * this {@code Throwable}.  The specified array is copied by this
     * call; changes in the specified array after the method invocation
     * returns will have no affect on this {@code Throwable}'s stack
     * trace.
     *
     * @throws NullPointerException if {@code stackTrace} is
     *         {@code null} or if any of the elements of
     *         {@code stackTrace} are {@code null}
     *
     * @since  1.4
     */
    public void setStackTrace(StackTraceElement[] stackTrace) {
        // Validate argument
        StackTraceElement[] defensiveCopy = stackTrace.clone();
        for (int i = 0; i < defensiveCopy.length; i++) {
            if (defensiveCopy[i] == null)
                throw new NullPointerException("stackTrace[" + i + "]");
        }

        synchronized (this) {
            if (this.stackTrace == null && // Immutable stack
                backtrace == null) // Test for out of protocol state
                return;
            this.stackTrace = defensiveCopy;
        }
    }

    /**
     * Returns the number of elements in the stack trace (or 0 if the stack
     * trace is unavailable).
     * 返回堆栈跟踪的元素个数
     * package-protection for use by SharedSecrets.
     */
    native int getStackTraceDepth();

    /**
     * Returns the specified element of the stack trace.
     * 返回特定的堆栈元素
     * package-protection for use by SharedSecrets.
     *
     * @param index index of the element to return.
     * @throws IndexOutOfBoundsException if {@code index < 0 ||
     *         index >= getStackTraceDepth() }
     */
    native StackTraceElement getStackTraceElement(int index);

    /**
     * Reads a {@code Throwable} from a stream, enforcing
     * well-formedness constraints on fields.  Null entries and
     * self-pointers are not allowed in the list of {@code
     * suppressedExceptions}.  Null entries are not allowed for stack
     * trace elements.  A null stack trace in the serial form results
     * in a zero-length stack element array. A single-element stack
     * trace whose entry is equal to {@code new StackTraceElement("",
     * "", null, Integer.MIN_VALUE)} results in a {@code null} {@code
     * stackTrace} field.
     * 从流中读取throwable对象，对字段实施格式正确的约束。null实体和自我
     * 自己指向自己的指针在suppressedExceptions中是不允许存在的。
     * 堆栈元素不允许存在null实体。长度为0的堆栈元素数组会返回一个空的堆栈跟踪信息。
     * 如果当前堆栈为StackTraceElement("","", null, Integer.MIN_VALUE），也会返回null结果。
     *
     * Note that there are no constraints on the value the {@code
     * cause} field can hold; both {@code null} and {@code this} are
     * valid values for the field.
     * cause值并没有约束，null和this也可以为cause的值
     */
    private void readObject(ObjectInputStream s)
        throws IOException, ClassNotFoundException {
        // 从输入流中读取信息
        s.defaultReadObject();     // read in all fields
        if (suppressedExceptions != null) {
            List<Throwable> suppressed = null;
            if (suppressedExceptions.isEmpty()) {
                // Use the sentinel for a zero-length list
                suppressed = SUPPRESSED_SENTINEL;
            } else { // Copy Throwables to new list
                suppressed = new ArrayList<>(1);
                for (Throwable t : suppressedExceptions) {
                    // Enforce constraints on suppressed exceptions in
                    // case of corrupt or malicious stream.
                    if (t == null)
                        throw new NullPointerException(NULL_CAUSE_MESSAGE);
                    if (t == this)
                        throw new IllegalArgumentException(SELF_SUPPRESSION_MESSAGE);
                    suppressed.add(t);
                }
            }
            suppressedExceptions = suppressed;
        } // else a null suppressedExceptions field remains null

        /*
         * For zero-length stack traces, use a clone of
         * UNASSIGNED_STACK rather than UNASSIGNED_STACK itself to
         * allow identity comparison against UNASSIGNED_STACK in
         * getOurStackTrace.  The identity of UNASSIGNED_STACK in
         * stackTrace indicates to the getOurStackTrace method that
         * the stackTrace needs to be constructed from the information
         * in backtrace.
         */
        if (stackTrace != null) {
            if (stackTrace.length == 0) {
                stackTrace = UNASSIGNED_STACK.clone();
            }  else if (stackTrace.length == 1 &&
                        // Check for the marker of an immutable stack trace
                        SentinelHolder.STACK_TRACE_ELEMENT_SENTINEL.equals(stackTrace[0])) {
                stackTrace = null;
            } else { // Verify stack trace elements are non-null.
                for(StackTraceElement ste : stackTrace) {
                    if (ste == null)
                        throw new NullPointerException("null StackTraceElement in serial stream. ");
                }
            }
        } else {
            // A null stackTrace field in the serial form can result
            // from an exception serialized without that field in
            // older JDK releases; treat such exceptions as having
            // empty stack traces.
            stackTrace = UNASSIGNED_STACK.clone();
        }
    }

    /**
     * Write a {@code Throwable} object to a stream.
     * 将对象写到流里面
     * A {@code null} stack trace field is represented in the serial
     * form as a one-element array whose element is equal to {@code
     * new StackTraceElement("", "", null, Integer.MIN_VALUE)}.
     */
    private synchronized void writeObject(ObjectOutputStream s)
        throws IOException {
        // Ensure that the stackTrace field is initialized to a
        // non-null value, if appropriate.  As of JDK 7, a null stack
        // trace field is a valid value indicating the stack trace
        // should not be set.
        getOurStackTrace();

        StackTraceElement[] oldStackTrace = stackTrace;
        try {
            if (stackTrace == null)
                stackTrace = SentinelHolder.STACK_TRACE_SENTINEL;
            s.defaultWriteObject();
        } finally {
            stackTrace = oldStackTrace;
        }
    }

    /**
     * Appends the specified exception to the exceptions that were
     * suppressed in order to deliver this exception. This method is
     * thread-safe and typically called (automatically and implicitly)
     * by the {@code try}-with-resources statement.
     *  添加特定的异常信息到异常中，目的是传递这个异常。这个方法是线程安全的，
     *  一般是通过try生命调用
     *
     * <p>The suppression behavior is enabled <em>unless</em> disabled
     * {@linkplain #Throwable(String, Throwable, boolean, boolean) via
     * a constructor}.  When suppression is disabled, this method does
     * nothing other than to validate its argument.
     *  Throwable(String, Throwable, boolean, boolean)  这个构造方法
     *  中，suppression为false，则该方法无效
     *
     * <p>Note that when one exception {@linkplain
     * #initCause(Throwable) causes} another exception, the first
     * exception is usually caught and then the second exception is
     * thrown in response.  In other words, there is a causal
     * connection between the two exceptions.
     *  两个异常之间是有联系的
     *
     * In contrast, there are situations where two independent
     * exceptions can be thrown in sibling code blocks, in particular
     * in the {@code try} block of a {@code try}-with-resources
     * statement and the compiler-generated {@code finally} block
     * which closes the resource.
     *
     * In these situations, only one of the thrown exceptions can be
     * propagated.  In the {@code try}-with-resources statement, when
     * there are two such exceptions, the exception originating from
     * the {@code try} block is propagated and the exception from the
     * {@code finally} block is added to the list of exceptions
     * suppressed by the exception from the {@code try} block.  As an
     * exception unwinds the stack, it can accumulate multiple
     * suppressed exceptions.
     *
     * <p>An exception may have suppressed exceptions while also being
     * caused by another exception.  Whether or not an exception has a
     * cause is semantically known at the time of its creation, unlike
     * whether or not an exception will suppress other exceptions
     * which is typically only determined after an exception is
     * thrown.
     *
     * <p>Note that programmer written code is also able to take
     * advantage of calling this method in situations where there are
     * multiple sibling exceptions and only one can be propagated.
     *
     * @param exception the exception to be added to the list of
     *        suppressed exceptions
     * @throws IllegalArgumentException if {@code exception} is this
     *         throwable; a throwable cannot suppress itself.
     * @throws NullPointerException if {@code exception} is {@code null}
     * @since 1.7
     *  该方法是把异常加到list中
     */
    public final synchronized void addSuppressed(Throwable exception) {
        if (exception == this)
            throw new IllegalArgumentException(SELF_SUPPRESSION_MESSAGE, exception);

        if (exception == null)
            throw new NullPointerException(NULL_CAUSE_MESSAGE);

        if (suppressedExceptions == null) // Suppressed exceptions not recorded
            return;

        if (suppressedExceptions == SUPPRESSED_SENTINEL)
            suppressedExceptions = new ArrayList<>(1);

        suppressedExceptions.add(exception);
    }

    private static final Throwable[] EMPTY_THROWABLE_ARRAY = new Throwable[0];

    /**
     * Returns an array containing all of the exceptions that were
     * suppressed, typically by the {@code try}-with-resources
     * statement, in order to deliver this exception.
     *
     * If no exceptions were suppressed or {@linkplain
     * #Throwable(String, Throwable, boolean, boolean) suppression is
     * disabled}, an empty array is returned.  This method is
     * thread-safe.  Writes to the returned array do not affect future
     * calls to this method.
     *
     * @return an array containing all of the exceptions that were
     *         suppressed to deliver this exception.
     * @since 1.7
     *
     *  该方法返回一个数组，该数组包含所有的异常，这些异常被
     *  压到栈中用于传递异常
     */
    public final synchronized Throwable[] getSuppressed() {
        if (suppressedExceptions == SUPPRESSED_SENTINEL ||
            suppressedExceptions == null)
            return EMPTY_THROWABLE_ARRAY;
        else
            return suppressedExceptions.toArray(EMPTY_THROWABLE_ARRAY);
    }
}
