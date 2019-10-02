/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
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
 * The {@code Compiler} class is provided to support Java-to-native-code
 * compilers and related services. By design, the {@code Compiler} class does
 * nothing; it serves as a placeholder for a JIT compiler implementation.
 * 提供该类以支持java到本地代码的编译器和相关服务。设计上，Complier类并没有
 * 做什么事情。它充当JIT编译器实现的占位符。
 * <p> When the Java Virtual Machine first starts, it determines if the system
 * property {@code java.compiler} exists. (System properties are accessible
 * through {@link System#getProperty(String)} and {@link
 * System#getProperty(String, String)}.  If so, it is assumed to be the name of
 * a library (with a platform-dependent exact location and type); {@link
 * System#loadLibrary} is called to load that library. If this loading
 * succeeds, the function named {@code java_lang_Compiler_start()} in that
 * library is called.
 *  当jvm第一次开启时，它判断java.compiler属性是否存在。如果存在，
 *如果是这样，则假定它是一个库的名称（具有与平台有关的确切位置和类型）；
 * loadLibrary方法被调用去加载该库。如果加载成功，该库中名为{@code java_lang_Compiler_start（）}
 * 的函数将被调用。
 *
 * <p> If no compiler is available, these methods do nothing.
 *
 * @author  Frank Yellin
 * @since   JDK1.0
 */
public final class Compiler  {
    private Compiler() {}               // don't make instances 不给实例化

    private static native void initialize();

    private static native void registerNatives();

    static {
        // 注册可以执行本地方法
        registerNatives();
        java.security.AccessController.doPrivileged(
            new java.security.PrivilegedAction<Void>() {
                public Void run() {
                    boolean loaded = false;
                    //  获取java.compiler的属性内容
                    String jit = System.getProperty("java.compiler");
                    // jit有值
                    if ((jit != null) && (!jit.equals("NONE")) &&
                        (!jit.equals("")))
                    {
                        try {
                            // 加载类库
                            System.loadLibrary(jit);
                            initialize();
                            loaded = true;
                        } catch (UnsatisfiedLinkError e) {
                            System.err.println("Warning: JIT compiler \"" +
                              jit + "\" not found. Will use interpreter.");
                        }
                    }
                    // java虚拟机信息
                    String info = System.getProperty("java.vm.info");
                    if (loaded) {
                        // 加载成功则输入信息，不成功则输入没有jit
                        System.setProperty("java.vm.info", info + ", " + jit);
                    } else {
                        System.setProperty("java.vm.info", info + ", nojit");
                    }
                    return null;
                }
            });
    }

    /**
     * Compiles the specified class.
     *
     * @param  clazz
     *         A class
     *
     * @return  {@code true} if the compilation succeeded; {@code false} if the
     *          compilation failed or no compiler is available
     *
     * @throws  NullPointerException
     *          If {@code clazz} is {@code null}
     */
    public static native boolean compileClass(Class<?> clazz);

    /**
     * Compiles all classes whose name matches the specified string.
     * 编译所有与特定字符串名称相匹配的类
     * @param  string
     *         The name of the classes to compile
     *         编译的类名称
     *
     * @return  {@code true} if the compilation succeeded; {@code false} if the
     *          compilation failed or no compiler is available
     *          如果编译操作成功返回true；如果编译失败或者没有合适的编译器返回false
     * @throws  NullPointerException
     *          If {@code string} is {@code null}
     */
    public static native boolean compileClasses(String string);

    /**
     * Examines the argument type and its fields and perform some documented
     * operation.  No specific operations are required.
     * 检查参数类型及其字段，并执行一些记录的操作。 不需要特定的操作。
     *
     * @param  any
     *         An argument
     *
     * @return  A compiler-specific value, or {@code null} if no compiler is
     *          available
     *          一个特定编译器的值，如果没有合适的编译器将会返回null
     *
     * @throws  NullPointerException
     *          If {@code any} is {@code null}
     */
    public static native Object command(Object any);

    /**
     * Cause the Compiler to resume operation.
     * 使编译器可以运行
     */
    public static native void enable();

    /**
     * Cause the Compiler to cease operation.
     * 是编译器停止运行
     */
    public static native void disable();
}
