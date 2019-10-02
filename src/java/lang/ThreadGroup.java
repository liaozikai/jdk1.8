/*
 * Copyright (c) 1995, 2012, Oracle and/or its affiliates. All rights reserved.
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

import java.io.PrintStream;
import java.util.Arrays;
import sun.misc.VM;

/**
 * A thread group represents a set of threads. In addition, a thread
 * group can also include other thread groups. The thread groups form
 * a tree in which every thread group except the initial thread group
 * has a parent.
 *  线程组代表一组线程的集合。另外，线程组能够包含另外的线程组。
 *  除了最初的线程组，所有的线程组都有一个父亲，他们的就够就像一棵树
 * <p>
 * A thread is allowed to access information about its own thread
 * group, but not to access information about its thread group's
 * parent thread group or any other thread groups.
 * 线程能够通过他所在的线程组访问信息。但不能去访问它所在线程组的父类
 * 或其他线程组的信息。
 * @author  unascribed
 * @since   JDK1.0
 */
/* The locking strategy for this code is to try to lock only one level of the
 * tree wherever possible, but otherwise to lock from the bottom up.
 * That is, from child thread groups to parents.
 * This has the advantage of limiting the number of locks that need to be held
 * and in particular avoids having to grab the lock for the root thread group,
 * (or a global lock) which would be a source of contention on a
 * multi-processor system with many thread groups.
 * This policy often leads to taking a snapshot of the state of a thread group
 * and working off of that snapshot, rather than holding the thread group locked
 * while we work on the children.
 *
 * 此代码的锁定策略是尝试尽可能仅锁定树的一个级别，否则尝试从下至上锁定。 也就是说，从子线程组到父母。
 * 这样的优点是限制了需要保留的锁的数量，特别是避免了必须获取根线程组的锁（或全局锁），
 * 而该根线程组将成为具有以下特征的多处理器系统上的争用源： 许多线程组。
  * 此策略通常导致对线程组的状态进行快照并对其进行处理，而不是在处理子级时将线程组锁定。
 *（这段话说那么多，其实想表达的意思是，尽量将锁的锁定级别细化，降低对其他线程组的影响）
 */
public
class ThreadGroup implements Thread.UncaughtExceptionHandler {
    private final ThreadGroup parent;
    String name;
    int maxPriority;
    boolean destroyed;
    boolean daemon;
    boolean vmAllowSuspension;

    int nUnstartedThreads = 0;
    int nthreads;
    Thread threads[];

    int ngroups;
    ThreadGroup groups[];

    /**
     * Creates an empty Thread group that is not in any Thread group.
     * This method is used to create the system Thread group.
     *  创建一个不在任何线程组中的线程组，这个方法用于
     *  创建线程组
     */
    private ThreadGroup() {     // called from C code 由C代码调用
        this.name = "system";
        this.maxPriority = Thread.MAX_PRIORITY;
        this.parent = null;
    }

    /**
     * Constructs a new thread group. The parent of this new group is
     * the thread group of the currently running thread.
     * 构造一个新线程组。新线程组的父类是当前正在运行线程的线程组
     * <p>
     * The <code>checkAccess</code> method of the parent thread group is
     * called with no arguments; this may result in a security exception.
     *  父类线程组的checkAccess方法调用并且不传任何参数，可能会导致安全异常
     * @param   name   the name of the new thread group.
     * @exception  SecurityException  if the current thread cannot create a
     *               thread in the specified thread group.
     * @see     java.lang.ThreadGroup#checkAccess()
     * @since   JDK1.0
     */
    public ThreadGroup(String name) {
        this(Thread.currentThread().getThreadGroup(), name);
    }

    /**
     * Creates a new thread group. The parent of this new group is the
     * specified thread group.
     *  创建新的线程组，其父类是指定的线程组
     * <p>
     * The <code>checkAccess</code> method of the parent thread group is
     * called with no arguments; this may result in a security exception.
     *
     * @param     parent   the parent thread group.
     * @param     name     the name of the new thread group.
     * @exception  NullPointerException  if the thread group argument is
     *               <code>null</code>.
     * @exception  SecurityException  if the current thread cannot create a
     *               thread in the specified thread group.
     * @see     java.lang.SecurityException
     * @see     java.lang.ThreadGroup#checkAccess()
     * @since   JDK1.0
     */
    public ThreadGroup(ThreadGroup parent, String name) {
        this(checkParentAccess(parent), parent, name);
    }

    private ThreadGroup(Void unused, ThreadGroup parent, String name) {
        this.name = name;
        this.maxPriority = parent.maxPriority;
        this.daemon = parent.daemon; // 如果父类是守护线程组，子类也是
        this.vmAllowSuspension = parent.vmAllowSuspension;
        this.parent = parent;
        parent.add(this);
    }

    /*
     * @throws  NullPointerException  if the parent argument is {@code null}
     * @throws  SecurityException     if the current thread cannot create a
     *                                thread in the specified thread group.
            */
    private static Void checkParentAccess(ThreadGroup parent) {
        parent.checkAccess();
        return null;
    }

    /**
     * Returns the name of this thread group.
     *
     * @return  the name of this thread group.
     * @since   JDK1.0
     */
    public final String getName() {
        return name;
    }

    /**
     * Returns the parent of this thread group.
     * <p>
     * First, if the parent is not <code>null</code>, the
     * <code>checkAccess</code> method of the parent thread group is
     * called with no arguments; this may result in a security exception.
     *
     * @return  the parent of this thread group. The top-level thread group
     *          is the only thread group whose parent is <code>null</code>.
     * @exception  SecurityException  if the current thread cannot modify
     *               this thread group.
     * @see        java.lang.ThreadGroup#checkAccess()
     * @see        java.lang.SecurityException
     * @see        java.lang.RuntimePermission
     * @since   JDK1.0
     */
    public final ThreadGroup getParent() {
        if (parent != null)
            parent.checkAccess();
        return parent;
    }

    /**
     * Returns the maximum priority of this thread group. Threads that are
     * part of this group cannot have a higher priority than the maximum
     * priority.
     *
     * @return  the maximum priority that a thread in this thread group
     *          can have.
     * @see     #setMaxPriority
     * @since   JDK1.0
     */
    public final int getMaxPriority() {
        return maxPriority;
    }

    /**
     * Tests if this thread group is a daemon thread group. A
     * daemon thread group is automatically destroyed when its last
     * thread is stopped or its last thread group is destroyed.
     *  测试这个线程组是否守护线程组。当线程组的最后一个线程被终止或者
     *  它的最后一个线程组被销毁，则它也被自动销毁
     * @return  <code>true</code> if this thread group is a daemon thread group;
     *          <code>false</code> otherwise.
     * @since   JDK1.0
     */
    public final boolean isDaemon() {
        return daemon;
    }

    /**
     * Tests if this thread group has been destroyed.
     *
     * @return  true if this object is destroyed
     * @since   JDK1.1
     */
    public synchronized boolean isDestroyed() {
        return destroyed;
    }

    /**
     * Changes the daemon status of this thread group.
     * <p>
     * First, the <code>checkAccess</code> method of this thread group is
     * called with no arguments; this may result in a security exception.
     * <p>
     * A daemon thread group is automatically destroyed when its last
     * thread is stopped or its last thread group is destroyed.
     *
     * @param      daemon   if <code>true</code>, marks this thread group as
     *                      a daemon thread group; otherwise, marks this
     *                      thread group as normal.
     * @exception  SecurityException  if the current thread cannot modify
     *               this thread group.
     * @see        java.lang.SecurityException
     * @see        java.lang.ThreadGroup#checkAccess()
     * @since      JDK1.0
     */
    public final void setDaemon(boolean daemon) {
        checkAccess();
        this.daemon = daemon;
    }

    /**
     * Sets the maximum priority of the group. Threads in the thread
     * group that already have a higher priority are not affected.
     * 设置这个组的最大优先级。线程组中已经具有更高优先级的线程不受影响。
     * <p>
     * First, the <code>checkAccess</code> method of this thread group is
     * called with no arguments; this may result in a security exception.
     * <p>
     * If the <code>pri</code> argument is less than
     * {@link Thread#MIN_PRIORITY} or greater than
     * {@link Thread#MAX_PRIORITY}, the maximum priority of the group
     * remains unchanged.
     * 如果参数小于最小优先级或者大于最大优先级，则该组的最大优先级
     * 仍然保持不变
     * <p>
     * Otherwise, the priority of this ThreadGroup object is set to the
     * smaller of the specified <code>pri</code> and the maximum permitted
     * priority of the parent of this thread group. (If this thread group
     * is the system thread group, which has no parent, then its maximum
     * priority is simply set to <code>pri</code>.) Then this method is
     * called recursively, with <code>pri</code> as its argument, for
     * every thread group that belongs to this thread group.
     *
     * 否则，将此ThreadGroup对象的优先级设置为指定的<code> pri </ code>
     * 和此线程组的父级的最大允许优先级中的较小者。 （如果此线程组是没有父级的
     * 系统线程组，则其最大优先级仅设置为<code> pri </ code>。）
     * 然后使用<code> pri </ code>递归调用此方法。 作为其参数，针对属于
     * 该线程组的每个线程组。
     *
     * @param      pri   the new priority of the thread group.
     * @exception  SecurityException  if the current thread cannot modify
     *               this thread group.
     * @see        #getMaxPriority
     * @see        java.lang.SecurityException
     * @see        java.lang.ThreadGroup#checkAccess()
     * @since      JDK1.0
     */
    public final void setMaxPriority(int pri) {
        int ngroupsSnapshot;
        ThreadGroup[] groupsSnapshot;
        synchronized (this) {
            checkAccess();
            if (pri < Thread.MIN_PRIORITY || pri > Thread.MAX_PRIORITY) {
                return;
            }
            maxPriority = (parent != null) ? Math.min(pri, parent.maxPriority) : pri;
            ngroupsSnapshot = ngroups;
            if (groups != null) {
                // 快照的意思就是副本，是复制出来的内容
                groupsSnapshot = Arrays.copyOf(groups, ngroupsSnapshot);
            } else {
                groupsSnapshot = null;
            }
        }
        // 线程组中每个快照都是这个优先级
        for (int i = 0 ; i < ngroupsSnapshot ; i++) {
            groupsSnapshot[i].setMaxPriority(pri);
        }
    }

    /**
     * Tests if this thread group is either the thread group
     * argument or one of its ancestor thread groups.
     * 测试此线程组是线程组参数还是其祖先线程组之一。
     * @param   g   a thread group.
     * @return  <code>true</code> if this thread group is the thread group
     *          argument or one of its ancestor thread groups;
     *          <code>false</code> otherwise.
     * @since   JDK1.0
     */
    public final boolean parentOf(ThreadGroup g) {
        for (; g != null ; g = g.parent) {
            if (g == this) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the currently running thread has permission to
     * modify this thread group.
     *  判断当前线程是否有权限去修改这个线程组
     * <p>
     * If there is a security manager, its <code>checkAccess</code> method
     * is called with this thread group as its argument. This may result
     * in throwing a <code>SecurityException</code>.
     *
     * @exception  SecurityException  if the current thread is not allowed to
     *               access this thread group.
     * @see        java.lang.SecurityManager#checkAccess(java.lang.ThreadGroup)
     * @since      JDK1.0
     */
    public final void checkAccess() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkAccess(this);
        }
    }

    /**
     * Returns an estimate of the number of active threads in this thread
     * group and its subgroups. Recursively iterates over all subgroups in
     * this thread group.
     * 返回此线程组及其子组中活动线程的数量的估计值。
     * 递归地迭代此线程组中的所有子组。
     * <p> The value returned is only an estimate because the number of
     * threads may change dynamically while this method traverses internal
     * data structures, and might be affected by the presence of certain
     * system threads. This method is intended primarily for debugging
     * and monitoring purposes.
     *
     * @return  an estimate of the number of active threads in this thread
     *          group and in any other thread group that has this thread
     *          group as an ancestor
     *
     * @since   JDK1.0
     */
    public int activeCount() {
        int result;
        // Snapshot sub-group data so we don't hold this lock
        // while our children are computing.
        // 快照子组数据，因此我们不持有此锁定
        // 当我们的孩子正在计算时。
        int ngroupsSnapshot;
        ThreadGroup[] groupsSnapshot;
        synchronized (this) {
            if (destroyed) {
                return 0;
            }
            result = nthreads;
            ngroupsSnapshot = ngroups;
            if (groups != null) {
                groupsSnapshot = Arrays.copyOf(groups, ngroupsSnapshot);
            } else {
                groupsSnapshot = null;
            }
        }
        for (int i = 0 ; i < ngroupsSnapshot ; i++) {
            result += groupsSnapshot[i].activeCount();
        }
        return result;
    }

    /**
     * Copies into the specified array every active thread in this
     * thread group and its subgroups.
     * 将此线程组及其子组中的每个活动线程复制到指定的数组中。
     * <p> An invocation of this method behaves in exactly the same
     * way as the invocation
     *
     * <blockquote>
     * {@linkplain #enumerate(Thread[], boolean) enumerate}{@code (list, true)}
     * </blockquote>
     *
     * @param  list
     *         an array into which to put the list of threads
     *
     * @return  the number of threads put into the array
     *
     * @throws  SecurityException
     *          if {@linkplain #checkAccess checkAccess} determines that
     *          the current thread cannot access this thread group
     *
     * @since   JDK1.0
     */
    public int enumerate(Thread list[]) {
        checkAccess();
        return enumerate(list, 0, true);
    }

    /**
     * Copies into the specified array every active thread in this
     * thread group. If {@code recurse} is {@code true},
     * this method recursively enumerates all subgroups of this
     * thread group and references to every active thread in these
     * subgroups are also included. If the array is too short to
     * hold all the threads, the extra threads are silently ignored.
     *将此线程组中的每个活动线程复制到指定的数组中。 如果{@code recurse}为{@code true}，
     * 则此方法递归枚举该线程组的所有子组，
     * 并且还包括对这些子组中每个活动线程的引用。 如果数组太短而无法容纳
     * 所有线程，则多余的线程将被静默忽略。
     * <p> An application might use the {@linkplain #activeCount activeCount}
     * method to get an estimate of how big the array should be, however
     * <i>if the array is too short to hold all the threads, the extra threads
     * are silently ignored.</i>  If it is critical to obtain every active
     * thread in this thread group, the caller should verify that the returned
     * int value is strictly less than the length of {@code list}.
     * 应用程序可以使用{@linkplain #activeCount activeCount}方法来估计数组的大小，
     * 但是如果数组太短而无法容纳所有线程，则多余的线程将被忽略。
     * 如果获取该线程组中的每个活动线程至关重要，则调用者应验证返回的int值
     * 严格小于{@code list}的长度。
     *
     * <p> Due to the inherent race condition in this method, it is recommended
     * that the method only be used for debugging and monitoring purposes.
     *
     * @param  list
     *         an array into which to put the list of threads
     *
     * @param  recurse
     *         if {@code true}, recursively enumerate all subgroups of this
     *         thread group
     *
     * @return  the number of threads put into the array
     *
     * @throws  SecurityException
     *          if {@linkplain #checkAccess checkAccess} determines that
     *          the current thread cannot access this thread group
     *
     * @since   JDK1.0
     */
    public int enumerate(Thread list[], boolean recurse) {
        checkAccess();
        return enumerate(list, 0, recurse);
    }

    private int enumerate(Thread list[], int n, boolean recurse) {
        int ngroupsSnapshot = 0;
        ThreadGroup[] groupsSnapshot = null;
        synchronized (this) {
            if (destroyed) {
                return 0;
            }
            int nt = nthreads;
            if (nt > list.length - n) {
                nt = list.length - n;
            }
            for (int i = 0; i < nt; i++) {
                if (threads[i].isAlive()) {
                    list[n++] = threads[i];
                }
            }
            if (recurse) {
                ngroupsSnapshot = ngroups;
                if (groups != null) {
                    groupsSnapshot = Arrays.copyOf(groups, ngroupsSnapshot);
                } else {
                    groupsSnapshot = null;
                }
            }
        }
        if (recurse) {
            for (int i = 0 ; i < ngroupsSnapshot ; i++) {
                n = groupsSnapshot[i].enumerate(list, n, true);
            }
        }
        return n;
    }

    /**
     * Returns an estimate of the number of active groups in this
     * thread group and its subgroups. Recursively iterates over
     * all subgroups in this thread group.
     *
     * <p> The value returned is only an estimate because the number of
     * thread groups may change dynamically while this method traverses
     * internal data structures. This method is intended primarily for
     * debugging and monitoring purposes.
     *
     * @return  the number of active thread groups with this thread group as
     *          an ancestor
     *
     * @since   JDK1.0
     */
    public int activeGroupCount() {
        int ngroupsSnapshot;
        ThreadGroup[] groupsSnapshot;
        synchronized (this) {
            if (destroyed) {
                return 0;
            }
            ngroupsSnapshot = ngroups;
            if (groups != null) {
                groupsSnapshot = Arrays.copyOf(groups, ngroupsSnapshot);
            } else {
                groupsSnapshot = null;
            }
        }
        int n = ngroupsSnapshot;
        for (int i = 0 ; i < ngroupsSnapshot ; i++) {
            n += groupsSnapshot[i].activeGroupCount();
        }
        return n;
    }

    /**
     * Copies into the specified array references to every active
     * subgroup in this thread group and its subgroups.
     *
     * <p> An invocation of this method behaves in exactly the same
     * way as the invocation
     *
     * <blockquote>
     * {@linkplain #enumerate(ThreadGroup[], boolean) enumerate}{@code (list, true)}
     * </blockquote>
     *
     * @param  list
     *         an array into which to put the list of thread groups
     *
     * @return  the number of thread groups put into the array
     *
     * @throws  SecurityException
     *          if {@linkplain #checkAccess checkAccess} determines that
     *          the current thread cannot access this thread group
     *
     * @since   JDK1.0
     */
    public int enumerate(ThreadGroup list[]) {
        checkAccess();
        return enumerate(list, 0, true);
    }

    /**
     * Copies into the specified array references to every active
     * subgroup in this thread group. If {@code recurse} is
     * {@code true}, this method recursively enumerates all subgroups of this
     * thread group and references to every active thread group in these
     * subgroups are also included.
     *
     * <p> An application might use the
     * {@linkplain #activeGroupCount activeGroupCount} method to
     * get an estimate of how big the array should be, however <i>if the
     * array is too short to hold all the thread groups, the extra thread
     * groups are silently ignored.</i>  If it is critical to obtain every
     * active subgroup in this thread group, the caller should verify that
     * the returned int value is strictly less than the length of
     * {@code list}.
     *
     * <p> Due to the inherent race condition in this method, it is recommended
     * that the method only be used for debugging and monitoring purposes.
     *
     * @param  list
     *         an array into which to put the list of thread groups
     *
     * @param  recurse
     *         if {@code true}, recursively enumerate all subgroups
     *
     * @return  the number of thread groups put into the array
     *
     * @throws  SecurityException
     *          if {@linkplain #checkAccess checkAccess} determines that
     *          the current thread cannot access this thread group
     *
     * @since   JDK1.0
     */
    public int enumerate(ThreadGroup list[], boolean recurse) {
        checkAccess();
        return enumerate(list, 0, recurse);
    }

    private int enumerate(ThreadGroup list[], int n, boolean recurse) {
        int ngroupsSnapshot = 0;
        ThreadGroup[] groupsSnapshot = null;
        synchronized (this) {
            if (destroyed) {
                return 0;
            }
            int ng = ngroups;
            if (ng > list.length - n) {
                ng = list.length - n;
            }
            if (ng > 0) {
                System.arraycopy(groups, 0, list, n, ng);
                n += ng;
            }
            if (recurse) {
                ngroupsSnapshot = ngroups;
                if (groups != null) {
                    groupsSnapshot = Arrays.copyOf(groups, ngroupsSnapshot);
                } else {
                    groupsSnapshot = null;
                }
            }
        }
        if (recurse) {
            for (int i = 0 ; i < ngroupsSnapshot ; i++) {
                n = groupsSnapshot[i].enumerate(list, n, true);
            }
        }
        return n;
    }

    /**
     * Stops all threads in this thread group.
     * 停止该线程组的所有线程
     * <p>
     * First, the <code>checkAccess</code> method of this thread group is
     * called with no arguments; this may result in a security exception.
     * <p>
     * This method then calls the <code>stop</code> method on all the
     * threads in this thread group and in all of its subgroups.
     *  这个方法会调用该线程组和其所有子线程组的stop方法
     * @exception  SecurityException  if the current thread is not allowed
     *               to access this thread group or any of the threads in
     *               the thread group.
     * @see        java.lang.SecurityException
     * @see        java.lang.Thread#stop()
     * @see        java.lang.ThreadGroup#checkAccess()
     * @since      JDK1.0
     * @deprecated    This method is inherently unsafe.  See
     *     {@link Thread#stop} for details.
     */
    @Deprecated
    public final void stop() {
        if (stopOrSuspend(false))
            Thread.currentThread().stop();
    }

    /**
     * Interrupts all threads in this thread group.
     *  中断这个线程组的所有线程
     * <p>
     * First, the <code>checkAccess</code> method of this thread group is
     * called with no arguments; this may result in a security exception.
     * <p>
     * This method then calls the <code>interrupt</code> method on all the
     * threads in this thread group and in all of its subgroups.
     * 该方法会调用这个线程组或者其子线程组的interrupt方法
     * @exception  SecurityException  if the current thread is not allowed
     *               to access this thread group or any of the threads in
     *               the thread group.
     * @see        java.lang.Thread#interrupt()
     * @see        java.lang.SecurityException
     * @see        java.lang.ThreadGroup#checkAccess()
     * @since      1.2
     */
    public final void interrupt() {
        int ngroupsSnapshot;
        ThreadGroup[] groupsSnapshot;
        synchronized (this) {
            checkAccess();
            for (int i = 0 ; i < nthreads ; i++) {
                threads[i].interrupt();
            }
            ngroupsSnapshot = ngroups;
            if (groups != null) {
                groupsSnapshot = Arrays.copyOf(groups, ngroupsSnapshot);
            } else {
                groupsSnapshot = null;
            }
        }
        for (int i = 0 ; i < ngroupsSnapshot ; i++) {
            groupsSnapshot[i].interrupt();
        }
    }

    /**
     * Suspends all threads in this thread group.
     *  挂起线程组所有的线程
     * <p>
     * First, the <code>checkAccess</code> method of this thread group is
     * called with no arguments; this may result in a security exception.
     * <p>
     * This method then calls the <code>suspend</code> method on all the
     * threads in this thread group and in all of its subgroups.
     *
     * @exception  SecurityException  if the current thread is not allowed
     *               to access this thread group or any of the threads in
     *               the thread group.
     * @see        java.lang.Thread#suspend()
     * @see        java.lang.SecurityException
     * @see        java.lang.ThreadGroup#checkAccess()
     * @since      JDK1.0
     * @deprecated    This method is inherently deadlock-prone.  See
     *     {@link Thread#suspend} for details.
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public final void suspend() {
        if (stopOrSuspend(true))
            Thread.currentThread().suspend();
    }

    /**
     * Helper method: recursively stops or suspends (as directed by the
     * boolean argument) all of the threads in this thread group and its
     * subgroups, except the current thread.  This method returns true
     * if (and only if) the current thread is found to be in this thread
     * group or one of its subgroups.
     * Helper方法：以递归方式停止或挂起（由boolean参数指示）此线程组及其
     * 子组中的所有线程（当前线程除外）。
     * 当（且仅当）当前线程位于此线程组或其子组之一中时，此方法返回true。
     */
    @SuppressWarnings("deprecation")
    private boolean stopOrSuspend(boolean suspend) {
        boolean suicide = false;
        Thread us = Thread.currentThread();
        int ngroupsSnapshot;
        ThreadGroup[] groupsSnapshot = null;
        synchronized (this) {
            checkAccess();
            for (int i = 0 ; i < nthreads ; i++) {
                if (threads[i]==us)
                    suicide = true;
                else if (suspend)
                    threads[i].suspend();
                else
                    threads[i].stop();
            }

            ngroupsSnapshot = ngroups;
            if (groups != null) {
                groupsSnapshot = Arrays.copyOf(groups, ngroupsSnapshot);
            }
        }
        for (int i = 0 ; i < ngroupsSnapshot ; i++)
            suicide = groupsSnapshot[i].stopOrSuspend(suspend) || suicide;

        return suicide;
    }

    /**
     * Resumes all threads in this thread group.
     * <p>
     * First, the <code>checkAccess</code> method of this thread group is
     * called with no arguments; this may result in a security exception.
     * <p>
     * This method then calls the <code>resume</code> method on all the
     * threads in this thread group and in all of its sub groups.
     *
     * @exception  SecurityException  if the current thread is not allowed to
     *               access this thread group or any of the threads in the
     *               thread group.
     * @see        java.lang.SecurityException
     * @see        java.lang.Thread#resume()
     * @see        java.lang.ThreadGroup#checkAccess()
     * @since      JDK1.0
     * @deprecated    This method is used solely in conjunction with
     *      <tt>Thread.suspend</tt> and <tt>ThreadGroup.suspend</tt>,
     *       both of which have been deprecated, as they are inherently
     *       deadlock-prone.  See {@link Thread#suspend} for details.
     *
     *      过时不看
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public final void resume() {
        int ngroupsSnapshot;
        ThreadGroup[] groupsSnapshot;
        synchronized (this) {
            checkAccess();
            for (int i = 0 ; i < nthreads ; i++) {
                threads[i].resume();
            }
            ngroupsSnapshot = ngroups;
            if (groups != null) {
                groupsSnapshot = Arrays.copyOf(groups, ngroupsSnapshot);
            } else {
                groupsSnapshot = null;
            }
        }
        for (int i = 0 ; i < ngroupsSnapshot ; i++) {
            groupsSnapshot[i].resume();
        }
    }

    /**
     * Destroys this thread group and all of its subgroups. This thread
     * group must be empty, indicating that all threads that had been in
     * this thread group have since stopped.
     *  销毁该线程组及其所有子组。 该线程组必须为空，
     *  表示此线程组中的所有线程此后都已停止。
     * <p>
     * First, the <code>checkAccess</code> method of this thread group is
     * called with no arguments; this may result in a security exception.
     *
     * @exception  IllegalThreadStateException  if the thread group is not
     *               empty or if the thread group has already been destroyed.
     * @exception  SecurityException  if the current thread cannot modify this
     *               thread group.
     * @see        java.lang.ThreadGroup#checkAccess()
     * @since      JDK1.0
     */
    public final void destroy() {
        int ngroupsSnapshot;
        ThreadGroup[] groupsSnapshot;
        synchronized (this) {
            checkAccess();
            if (destroyed || (nthreads > 0)) {
                throw new IllegalThreadStateException();
            }
            ngroupsSnapshot = ngroups;
            if (groups != null) {
                groupsSnapshot = Arrays.copyOf(groups, ngroupsSnapshot);
            } else {
                groupsSnapshot = null;
            }
            if (parent != null) {
                destroyed = true;
                ngroups = 0;
                groups = null;
                nthreads = 0;
                threads = null;
            }
        }
        for (int i = 0 ; i < ngroupsSnapshot ; i += 1) {
            groupsSnapshot[i].destroy();
        }
        if (parent != null) {
            parent.remove(this);
        }
    }

    /**
     * Adds the specified Thread group to this group.
     * 将指定的线程组添加到该线程组中
     * @param g the specified Thread group to be added
     * @exception IllegalThreadStateException If the Thread group has been destroyed.
     */
    private final void add(ThreadGroup g){
        synchronized (this) {
            if (destroyed) {
                throw new IllegalThreadStateException();
            }
            if (groups == null) {
                groups = new ThreadGroup[4];
            } else if (ngroups == groups.length) {
                groups = Arrays.copyOf(groups, ngroups * 2);
            }
            groups[ngroups] = g;

            // This is done last so it doesn't matter in case the
            // thread is killed
            ngroups++;
        }
    }

    /**
     * Removes the specified Thread group from this group.
     * 从该线程组中移除指定的线程组
     * @param g the Thread group to be removed
     * @return if this Thread has already been destroyed.
     */
    private void remove(ThreadGroup g) {
        synchronized (this) {
            if (destroyed) {
                return;
            }
            for (int i = 0 ; i < ngroups ; i++) {
                if (groups[i] == g) {
                    ngroups -= 1;
                    System.arraycopy(groups, i + 1, groups, i, ngroups - i);
                    // Zap dangling reference to the dead group so that
                    // the garbage collector will collect it.
                    // 设置为null以便垃圾回收器收集
                    groups[ngroups] = null;
                    break;
                }
            }
            if (nthreads == 0) {
                notifyAll();
            }
            if (daemon && (nthreads == 0) &&
                (nUnstartedThreads == 0) && (ngroups == 0))
            {
                destroy();
            }
        }
    }


    /**
     * Increments the count of unstarted threads in the thread group.
     * Unstarted threads are not added to the thread group so that they
     * can be collected if they are never started, but they must be
     * counted so that daemon thread groups with unstarted threads in
     * them are not destroyed.
     * 增加线程组中未启动线程的数量。 未启动的线程不会添加到线程组中，
     * 这样即使从未启动过的线程也不会被收集，但是必须对它们进行计数，
     * 以便不会破坏其中未启动的线程的守护程序线程组。
     */
    void addUnstarted() {
        synchronized(this) {
            if (destroyed) {
                throw new IllegalThreadStateException();
            }
            nUnstartedThreads++;
        }
    }

    /**
     * Adds the specified thread to this thread group.
     * 添加指定的线程到线程组中
     * <p> Note: This method is called from both library code
     * and the Virtual Machine. It is called from VM to add
     * certain system threads to the system thread group.
     *  注意：这个方法被库代码和jvm所调用。
     *  jvm调用该方法添加指定的系统线程到系统线程组中
     * @param  t
     *         the Thread to be added
     *
     * @throws  IllegalThreadStateException
     *          if the Thread group has been destroyed
     */
    void add(Thread t) {
        synchronized (this) {
            if (destroyed) {
                throw new IllegalThreadStateException();
            }
            if (threads == null) {
                threads = new Thread[4]; // 一初始化就是4个线程的空间
            } else if (nthreads == threads.length) {
                threads = Arrays.copyOf(threads, nthreads * 2);
            }
            threads[nthreads] = t;

            // This is done last so it doesn't matter in case the
            // thread is killed
            nthreads++;

            // The thread is now a fully fledged member of the group, even
            // though it may, or may not, have been started yet. It will prevent
            // the group from being destroyed so the unstarted Threads count is
            // decremented.
            nUnstartedThreads--;
        }
    }

    /**
     * Notifies the group that the thread {@code t} has failed
     * an attempt to start.
     * 通知组线程{@code t}尝试启动失败。
     * <p> The state of this thread group is rolled back as if the
     * attempt to start the thread has never occurred. The thread is again
     * considered an unstarted member of the thread group, and a subsequent
     * attempt to start the thread is permitted.
     * 该线程组的状态会回滚，就好像从未尝试启动该线程一样。
     * 再次将该线程视为线程组的未启动成员，并且允许随后尝试启动该线程。
     * @param  t
     *         the Thread whose start method was invoked
     */
    void threadStartFailed(Thread t) {
        synchronized(this) {
            remove(t);
            nUnstartedThreads++;
        }
    }

    /**
     * Notifies the group that the thread {@code t} has terminated.
     * 通知组线程{@code t}已终止。
     * <p> Destroy the group if all of the following conditions are
     * true: this is a daemon thread group; there are no more alive
     * or unstarted threads in the group; there are no subgroups in
     * this thread group.
     * 如果满足以下所有条件，则销毁该组：这是一个守护线程组；
     * 该组中没有其他活动线程或未启动线程； 该线程组中没有子组。
     *
     * @param  t
     *         the Thread that has terminated
     */
    void threadTerminated(Thread t) {
        synchronized (this) {
            remove(t);

            if (nthreads == 0) {
                notifyAll();
            }
            if (daemon && (nthreads == 0) &&
                (nUnstartedThreads == 0) && (ngroups == 0))
            {
                destroy();
            }
        }
    }

    /**
     * Removes the specified Thread from this group. Invoking this method
     * on a thread group that has been destroyed has no effect.
     * 从该组中删除指定的线程。 在已销毁的线程组上调用此方法无效。
     * @param  t
     *         the Thread to be removed
     */
    private void remove(Thread t) {
        synchronized (this) {
            if (destroyed) {
                return;
            }
            for (int i = 0 ; i < nthreads ; i++) {
                if (threads[i] == t) {
                    System.arraycopy(threads, i + 1, threads, i, --nthreads - i);
                    // Zap dangling reference to the dead thread so that
                    // the garbage collector will collect it.
                    threads[nthreads] = null;
                    break;
                }
            }
        }
    }

    /**
     * Prints information about this thread group to the standard
     * output. This method is useful only for debugging.
     *  打印这个线程组的标准输出信息，这个方法只在调试使用
     * @since   JDK1.0
     */
    public void list() {
        list(System.out, 0);
    }
    void list(PrintStream out, int indent) {
        int ngroupsSnapshot;
        ThreadGroup[] groupsSnapshot;
        synchronized (this) {
            for (int j = 0 ; j < indent ; j++) {
                out.print(" ");
            }
            out.println(this);
            indent += 4;
            for (int i = 0 ; i < nthreads ; i++) {
                for (int j = 0 ; j < indent ; j++) {
                    out.print(" ");
                }
                out.println(threads[i]);
            }
            ngroupsSnapshot = ngroups;
            if (groups != null) {
                groupsSnapshot = Arrays.copyOf(groups, ngroupsSnapshot);
            } else {
                groupsSnapshot = null;
            }
        }
        for (int i = 0 ; i < ngroupsSnapshot ; i++) {
            groupsSnapshot[i].list(out, indent);
        }
    }

    /**
     * Called by the Java Virtual Machine when a thread in this
     * thread group stops because of an uncaught exception, and the thread
     * does not have a specific {@link Thread.UncaughtExceptionHandler}
     * installed.
     * 当一个线程在线程组中因为未捕获异常而停止时，jvm调用该方法。
     * 并且线程没有安装指定的未捕获异常控制器
     * <p>
     *     下面描述的几点直接看代码
     * The <code>uncaughtException</code> method of
     * <code>ThreadGroup</code> does the following:
     * <ul>
     * <li>If this thread group has a parent thread group, the
     *     <code>uncaughtException</code> method of that parent is called
     *     with the same two arguments.
     * <li>Otherwise, this method checks to see if there is a
     *     {@linkplain Thread#getDefaultUncaughtExceptionHandler default
     *     uncaught exception handler} installed, and if so, its
     *     <code>uncaughtException</code> method is called with the same
     *     two arguments.
     * <li>Otherwise, this method determines if the <code>Throwable</code>
     *     argument is an instance of {@link ThreadDeath}. If so, nothing
     *     special is done. Otherwise, a message containing the
     *     thread's name, as returned from the thread's {@link
     *     Thread#getName getName} method, and a stack backtrace,
     *     using the <code>Throwable</code>'s {@link
     *     Throwable#printStackTrace printStackTrace} method, is
     *     printed to the {@linkplain System#err standard error stream}.
     * </ul>
     * <p>
     * Applications can override this method in subclasses of
     * <code>ThreadGroup</code> to provide alternative handling of
     * uncaught exceptions.
     *
     * @param   t   the thread that is about to exit.
     * @param   e   the uncaught exception.
     * @since   JDK1.0
     */
    public void uncaughtException(Thread t, Throwable e) {
        if (parent != null) {
            parent.uncaughtException(t, e);
        } else {
            Thread.UncaughtExceptionHandler ueh =
                Thread.getDefaultUncaughtExceptionHandler();
            if (ueh != null) {
                ueh.uncaughtException(t, e);
            } else if (!(e instanceof ThreadDeath)) {
                System.err.print("Exception in thread \""
                                 + t.getName() + "\" ");
                e.printStackTrace(System.err);
            }
        }
    }

    /**
     * Used by VM to control lowmem implicit suspension.
     *
     * @param b boolean to allow or disallow suspension
     * @return true on success
     * @since   JDK1.1
     * @deprecated The definition of this call depends on {@link #suspend},
     *             which is deprecated.  Further, the behavior of this call
     *             was never specified.
     *   过时不看
     */
    @Deprecated
    public boolean allowThreadSuspension(boolean b) {
        this.vmAllowSuspension = b;
        if (!b) {
            VM.unsuspendSomeThreads();
        }
        return true;
    }

    /**
     * Returns a string representation of this Thread group.
     * 返回这个线程组的字符串表示
     * @return  a string representation of this thread group.
     * @since   JDK1.0
     */
    public String toString() {
        return getClass().getName() + "[name=" + getName() + ",maxpri=" + maxPriority + "]";
    }
}
