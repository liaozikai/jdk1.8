/*
 * Copyright (c) 1997, 2013, Oracle and/or its affiliates. All rights reserved.
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

import java.lang.reflect.AnnotatedElement;
import java.io.InputStream;
import java.util.Enumeration;

import java.util.StringTokenizer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import java.security.AccessController;
import java.security.PrivilegedAction;

import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.JarException;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import sun.net.www.ParseUtil;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;

import java.lang.annotation.Annotation;

/**
 * {@code Package} objects contain version information
 * about the implementation and specification of a Java package.
 * This versioning information is retrieved and made available
 * by the {@link ClassLoader} instance that
 * loaded the class(es).  Typically, it is stored in the manifest that is
 * distributed with the classes.
 * {@code Package}对象包含有关Java包的实现和规范的版本信息。
 * 加载该类的{@link ClassLoader}实例将检索此版本信息并使其可用。
 * 通常，它存储在与类一起分发的清单中。
 *
 * <p>The set of classes that make up the package may implement a
 * particular specification and if so the specification title, version number,
 * and vendor strings identify that specification.
 * An application can ask if the package is
 * compatible with a particular version, see the {@link
 * #isCompatibleWith isCompatibleWith}
 * method for details.
 *  包中的所有类可能实现了一个特殊的规格，该规格有标题，版本号和供应商字符串。
 *  应用能够查询包是否兼容特定的版本号，可通过isCompatibleWith方法查看更多细节。
 *
 * <p>Specification version numbers use a syntax that consists of nonnegative
 * decimal integers separated by periods ".", for example "2.0" or
 * "1.2.3.4.5.6.7".  This allows an extensible number to be used to represent
 * major, minor, micro, etc. versions.  The version specification is described
 * by the following formal grammar:
 * <blockquote>
 *     规格版本号使用了一种句法，该种句法有非负整型数组成，被“.”分隔。例如
 *     “2.0" or ”.2.3.4.5.6.7".它遵循一种可扩展的数字以便于代表主要的， 次要的，更次要的版本号。
 *     版本规格被下面的语法描述：
 * <dl>
 * <dt><i>SpecificationVersion:</i>
 * <dd><i>Digits RefinedVersion<sub>opt</sub></i>

 * <dt><i>RefinedVersion:</i>
 * <dd>{@code .} <i>Digits</i>
 * <dd>{@code .} <i>Digits RefinedVersion</i>
 *
 * <dt><i>Digits:</i>
 * <dd><i>Digit</i>
 * <dd><i>Digits</i>
 *
 * <dt><i>Digit:</i>
 * <dd>any character for which {@link Character#isDigit} returns {@code true},
 * e.g. 0, 1, 2, ...
 * </dl>
 * </blockquote>
 *
 * <p>The implementation title, version, and vendor strings identify an
 * implementation and are made available conveniently to enable accurate
 * reporting of the packages involved when a problem occurs. The contents
 * all three implementation strings are vendor specific. The
 * implementation version strings have no specified syntax and should
 * only be compared for equality with desired version identifiers.
 * 该实现标题，版本和供应商字符串标识一个实现，并且在发生问题时，能够方便准确报告包信息。
 *这三个实现字符串的内容都是特定于供应商的。实现版本字符串没有指定的语法，
 * 应仅与期望的版本标识符进行比较以进行相等性比较。
 * <p>Within each {@code ClassLoader} instance all classes from the same
 * java package have the same Package object.  The static methods allow a package
 * to be found by name or the set of all packages known to the current class
 * loader to be found.
 * 从相同包中由每一个类加载器实例加载的所有类都有相同的包对象。该静态方法
 * 允许包通过名字被找到，或者查找当前类加载器已知的所有软件包的集合
 *
 * @see ClassLoader#definePackage
 */
public class Package implements java.lang.reflect.AnnotatedElement {
    /**
     * Return the name of this package.
     * 返回报名
     *
     * @return  The fully-qualified name of this package as defined in section 6.5.3 of
     *          <cite>The Java&trade; Language Specification</cite>,
     *          for example, {@code java.lang}
     *          合格的名称在The Java&trade; Language Specification书中6.5.3节可以看到
     */
    public String getName() {
        return pkgName;
    }


    /**
     * Return the title of the specification that this package implements.
     * @return the specification title, null is returned if it is not known.
     * 返回该包实现的规格标题
     */
    public String getSpecificationTitle() {
        return specTitle;
    }

    /**
     * Returns the version number of the specification
     * that this package implements.
     * 返回这个包实现的规格版本号
     * This version string must be a sequence of nonnegative decimal
     * integers separated by "."'s and may have leading zeros.
     * When version strings are compared the most significant
     * numbers are compared.
     * 这个版本字符串鄙俗是非负数数字整数，并被“.”分隔，可能有前导零。
     * 比较版本字符串时，将比较最高有效数字。
     *
     * @return the specification version, null is returned if it is not known.
     */
    public String getSpecificationVersion() {
        return specVersion;
    }

    /**
     * Return the name of the organization, vendor,
     * or company that owns and maintains the specification
     * of the classes that implement this package.
     * @return the specification vendor, null is returned if it is not known.
     * 返回供应商字符串
     */
    public String getSpecificationVendor() {
        return specVendor;
    }

    /**
     * Return the title of this package.
     * @return the title of the implementation, null is returned if it is not known.
     * 返回包名标题
     */
    public String getImplementationTitle() {
        return implTitle;
    }

    /**
     * Return the version of this implementation. It consists of any string
     * assigned by the vendor of this implementation and does
     * not have any particular syntax specified or expected by the Java
     * runtime. It may be compared for equality with other
     * package version strings used for this implementation
     * by this vendor for this package.
     * @return the version of the implementation, null is returned if it is not known.
     * 返回当前实现的版本。
     */
    public String getImplementationVersion() {
        return implVersion;
    }

    /**
     * Returns the name of the organization,
     * vendor or company that provided this implementation.
     * @return the vendor that implemented this package..
     * 返回组织，供应商或公司提供当前实现的名称
     */
    public String getImplementationVendor() {
        return implVendor;
    }

    /**
     * Returns true if this package is sealed.
     * 如果该包是密封的返回true
     * @return true if the package is sealed, false otherwise
     */
    public boolean isSealed() {
        return sealBase != null;
    }

    /**
     * Returns true if this package is sealed with respect to the specified
     * code source url.
     * 如果此程序包相对于指定的代码源url是密封的，则返回true。
     * @param url the code source url
     * @return true if this package is sealed with respect to url
     */
    public boolean isSealed(URL url) {
        return url.equals(sealBase);
    }

    /**
     * Compare this package's specification version with a
     * desired version. It returns true if
     * this packages specification version number is greater than or equal
     * to the desired version number. <p>
     *     将包的规则版本号和给定的版本号进行比较。如果包规格版本大于或
     *     等于给定的版本号则返回true
     *
     * Version numbers are compared by sequentially comparing corresponding
     * components of the desired and specification strings.
     * Each component is converted as a decimal integer and the values
     * compared.
     * 对给定的版本号和规格字符串通过顺序比较对应的内容。每个组成被转换为数字
     * 整型并进行比较
     * If the specification value is greater than the desired
     * value true is returned. If the value is less false is returned.
     * If the values are equal the period is skipped and the next pair of
     * components is compared.
     * 如果规格版本号大于给定版本号就返回true。如果小于就返回false。
     * 如果某个组成相同就继续比较洗衣歌组成
     * @param desired the version string of the desired version.
     * @return true if this package's version number is greater
     *          than or equal to the desired version number
     *
     * @exception NumberFormatException if the desired or current version
     *          is not of the correct dotted form.
     */
    public boolean isCompatibleWith(String desired)
        throws NumberFormatException
    {
        if (specVersion == null || specVersion.length() < 1) {
            throw new NumberFormatException("Empty version string");
        }

        String [] sa = specVersion.split("\\.", -1);
        int [] si = new int[sa.length];
        for (int i = 0; i < sa.length; i++) {
            // 将“.”分隔成的每个部分进行整型转换
            si[i] = Integer.parseInt(sa[i]);
            if (si[i] < 0)
                throw NumberFormatException.forInputString("" + si[i]);
        }

        String [] da = desired.split("\\.", -1);
        int [] di = new int[da.length];
        for (int i = 0; i < da.length; i++) {
            di[i] = Integer.parseInt(da[i]);
            if (di[i] < 0)
                throw NumberFormatException.forInputString("" + di[i]);
        }

        // 一位一位地顺序比较
        int len = Math.max(di.length, si.length);
        for (int i = 0; i < len; i++) {
            int d = (i < di.length ? di[i] : 0);
            int s = (i < si.length ? si[i] : 0);
            if (s < d)
                return false;
            if (s > d)
                return true;
        }
        return true;
    }

    /**
     * Find a package by name in the callers {@code ClassLoader} instance.
     * The callers {@code ClassLoader} instance is used to find the package
     * instance corresponding to the named class. If the callers
     * {@code ClassLoader} instance is null then the set of packages loaded
     * by the system {@code ClassLoader} instance is searched to find the
     * named package. <p>
     *    在调用方类加载器实例中按名称查找包。调用方类加载器被用于找到相对应
     *    类名称的包实例。如果类加载器为null，则包集被系统类加载器实例加载，
     *    用于查找该包
     *
     * Packages have attributes for versions and specifications only if the class
     * loader created the package instance with the appropriate attributes. Typically,
     * those attributes are defined in the manifests that accompany the classes.
     *  包拥有版本的属性和规格只要类加载器创造合适属性的包实例。
     *  通常，这些属性是在与类一起出现的清单中定义的。
     *
     * @param name a package name, for example, java.lang. 参数名称 例如：java.lang
     * @return the package of the requested name. It may be null if no package
     *          information is available from the archive or codebase.
     *          返回要求参数名称所在包。如果在档案或代码库中没有合适包信息的话
     *          会返回null。
     */
    @CallerSensitive
    public static Package getPackage(String name) {
        // 获取类加载器
        ClassLoader l = ClassLoader.getClassLoader(Reflection.getCallerClass());
        if (l != null) {
            // 通过类加载器getPackage方法获取包
            return l.getPackage(name);
        } else {
            // 通过系统类加载器加载
            return getSystemPackage(name);
        }
    }

    /**
     * Get all the packages currently known for the caller's {@code ClassLoader}
     * instance.  Those packages correspond to classes loaded via or accessible by
     * name to that {@code ClassLoader} instance.  If the caller's
     * {@code ClassLoader} instance is the bootstrap {@code ClassLoader}
     * instance, which may be represented by {@code null} in some implementations,
     * only packages corresponding to classes loaded by the bootstrap
     * {@code ClassLoader} instance will be returned.
     *  获得调用方类加载器已知的所有包。这些包与当前类加载器相对应或者可以通过名称
     *  访问。如果调用房类加载器是bootstrap实例，在一些实现上可能会返回null。
     *  仅返回与bootstrap {@code ClassLoader}实例加载的类相对应的软件包。
     *
     * @return a new array of packages known to the callers {@code ClassLoader}
     * instance.  An zero length array is returned if none are known.
     */
    @CallerSensitive
    public static Package[] getPackages() {
        ClassLoader l = ClassLoader.getClassLoader(Reflection.getCallerClass());
        if (l != null) {
            return l.getPackages();
        } else {
            return getSystemPackages();
        }
    }

    /**
     * Get the package for the specified class.
     * The class's class loader is used to find the package instance
     * corresponding to the specified class. If the class loader
     * is the bootstrap class loader, which may be represented by
     * {@code null} in some implementations, then the set of packages
     * loaded by the bootstrap class loader is searched to find the package.
     * <p>
     * Packages have attributes for versions and specifications only
     * if the class loader created the package
     * instance with the appropriate attributes. Typically those
     * attributes are defined in the manifests that accompany
     * the classes.
     *
     * @param c the class to get the package of.
     * @return the package of the class. It may be null if no package
     *          information is available from the archive or codebase.  */
    static Package getPackage(Class<?> c) {
        String name = c.getName();
        int i = name.lastIndexOf('.');
        if (i != -1) {
            // 获取包名路径名称
            name = name.substring(0, i);
            // 获取当前类的类加载器
            ClassLoader cl = c.getClassLoader();
            if (cl != null) {
                return cl.getPackage(name);
            } else {
                return getSystemPackage(name);
            }
        } else {
            return null;
        }
    }

    /**
     * Return the hash code computed from the package name.
     * @return the hash code computed from the package name.
     */
    public int hashCode(){
        return pkgName.hashCode();
    }

    /**
     * Returns the string representation of this Package. 返回包的字符串表示
     * Its value is the string "package " and the package name. 值为package 加上报名
     * If the package title is defined it is appended. 如果标题存在就加上
     * If the package version is defined it is appended. 如果版本存在则加上
     * @return the string representation of the package.
     */
    public String toString() {
        String spec = specTitle;
        String ver =  specVersion;
        if (spec != null && spec.length() > 0)
            spec = ", " + spec;
        else
            spec = "";
        if (ver != null && ver.length() > 0)
            ver = ", version " + ver;
        else
            ver = "";
        return "package " + pkgName + spec + ver;
    }

    private Class<?> getPackageInfo() {
        if (packageInfo == null) {
            try {
                //  获取类
                packageInfo = Class.forName(pkgName + ".package-info", false, loader);
            } catch (ClassNotFoundException ex) {
                // store a proxy for the package info that has no annotations
                class PackageInfoProxy {}
                packageInfo = PackageInfoProxy.class;
            }
        }
        return packageInfo;
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     * @since 1.5
     */
    public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
        // 获取类的注解
        return getPackageInfo().getAnnotation(annotationClass);
    }

    /**
     * {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     * @since 1.5
     */
    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return AnnotatedElement.super.isAnnotationPresent(annotationClass);
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     * @since 1.8
     */
    @Override
    public  <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationClass) {
        return getPackageInfo().getAnnotationsByType(annotationClass);
    }

    /**
     * @since 1.5
     */
    public Annotation[] getAnnotations() {
        return getPackageInfo().getAnnotations();
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     * @since 1.8
     */
    @Override
    public <A extends Annotation> A getDeclaredAnnotation(Class<A> annotationClass) {
        return getPackageInfo().getDeclaredAnnotation(annotationClass);
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     * @since 1.8
     */
    @Override
    public <A extends Annotation> A[] getDeclaredAnnotationsByType(Class<A> annotationClass) {
        return getPackageInfo().getDeclaredAnnotationsByType(annotationClass);
    }

    /**
     * @since 1.5
     */
    public Annotation[] getDeclaredAnnotations()  {
        return getPackageInfo().getDeclaredAnnotations();
    }

    /**
     * Construct a package instance with the specified version
     * information. 通过给定的版本信息构造包实例
     * @param name the name of the package 包名称
     * @param spectitle the title of the specification 规格标题
     * @param specversion the version of the specification 规格版本
     * @param specvendor the organization that maintains the specification 包含规则的组织
     * @param impltitle the title of the implementation 当前实现的标题
     * @param implversion the version of the implementation 当前实现的版本
     * @param implvendor the organization that maintains the implementation 当前实现的供应向信息
     */
    Package(String name,
            String spectitle, String specversion, String specvendor,
            String impltitle, String implversion, String implvendor,
            URL sealbase, ClassLoader loader)
    {
        pkgName = name;
        implTitle = impltitle;
        implVersion = implversion;
        implVendor = implvendor;
        specTitle = spectitle;
        specVersion = specversion;
        specVendor = specvendor;
        sealBase = sealbase;
        this.loader = loader;
    }

    /*
     * Construct a package using the attributes from the specified manifest.
     *  使用特定清单的属性来构造包
     * @param name the package name
     * @param man the optional manifest for the package
     * @param url the optional code source url for the package
     */
    private Package(String name, Manifest man, URL url, ClassLoader loader) {
        String path = name.replace('.', '/').concat("/");
        String sealed = null;
        String specTitle= null;
        String specVersion= null;
        String specVendor= null;
        String implTitle= null;
        String implVersion= null;
        String implVendor= null;
        URL sealBase= null;
        Attributes attr = man.getAttributes(path);
        if (attr != null) {
            specTitle   = attr.getValue(Name.SPECIFICATION_TITLE);
            specVersion = attr.getValue(Name.SPECIFICATION_VERSION);
            specVendor  = attr.getValue(Name.SPECIFICATION_VENDOR);
            implTitle   = attr.getValue(Name.IMPLEMENTATION_TITLE);
            implVersion = attr.getValue(Name.IMPLEMENTATION_VERSION);
            implVendor  = attr.getValue(Name.IMPLEMENTATION_VENDOR);
            sealed      = attr.getValue(Name.SEALED);
        }
        attr = man.getMainAttributes();
        if (attr != null) {
            if (specTitle == null) {
                specTitle = attr.getValue(Name.SPECIFICATION_TITLE);
            }
            if (specVersion == null) {
                specVersion = attr.getValue(Name.SPECIFICATION_VERSION);
            }
            if (specVendor == null) {
                specVendor = attr.getValue(Name.SPECIFICATION_VENDOR);
            }
            if (implTitle == null) {
                implTitle = attr.getValue(Name.IMPLEMENTATION_TITLE);
            }
            if (implVersion == null) {
                implVersion = attr.getValue(Name.IMPLEMENTATION_VERSION);
            }
            if (implVendor == null) {
                implVendor = attr.getValue(Name.IMPLEMENTATION_VENDOR);
            }
            if (sealed == null) {
                sealed = attr.getValue(Name.SEALED);
            }
        }
        if ("true".equalsIgnoreCase(sealed)) {
            sealBase = url;
        }
        pkgName = name;
        this.specTitle = specTitle;
        this.specVersion = specVersion;
        this.specVendor = specVendor;
        this.implTitle = implTitle;
        this.implVersion = implVersion;
        this.implVendor = implVendor;
        this.sealBase = sealBase;
        this.loader = loader;
    }

    /*
     * Returns the loaded system package for the specified name.
     */
    static Package getSystemPackage(String name) {
        synchronized (pkgs) {
            Package pkg = pkgs.get(name);
            if (pkg == null) {
                // 将“.”换成“/”
                name = name.replace('.', '/').concat("/");
                String fn = getSystemPackage0(name);
                if (fn != null) {
                    pkg = defineSystemPackage(name, fn);
                }
            }
            return pkg;
        }
    }

    /*
     * Return an array of loaded system packages.
     * 返回加载的系统包数组
     */
    static Package[] getSystemPackages() {
        // First, update the system package map with new package names
        // 获取所有系统包名
        String[] names = getSystemPackages0();
        synchronized (pkgs) {
            for (int i = 0; i < names.length; i++) {
                // 通过之前获取的报名，重新更新了包信息
                defineSystemPackage(names[i], getSystemPackage0(names[i]));
            }
            // 返回包数组
            return pkgs.values().toArray(new Package[pkgs.size()]);
        }
    }

    // 通过包名称和文件名来构造包
    private static Package defineSystemPackage(final String iname,
                                               final String fn)
    {
        return AccessController.doPrivileged(new PrivilegedAction<Package>() {
            public Package run() {
                String name = iname;
                // Get the cached code source url for the file name
                // 通过文件名称获取url路径
                URL url = urls.get(fn);
                if (url == null) {
                    // URL not found, so create one
                    File file = new File(fn);
                    try {
                        url = ParseUtil.fileToEncodedURL(file);
                    } catch (MalformedURLException e) {
                    }
                    if (url != null) {
                        urls.put(fn, url);
                        // If loading a JAR file, then also cache the manifest
                        // 如果加载了一个jar文件，就缓存它的清单
                        if (file.isFile()) {
                            mans.put(fn, loadManifest(fn));
                        }
                    }
                }
                // Convert to "."-separated package name
                name = name.substring(0, name.length() - 1).replace('/', '.');
                Package pkg;
                Manifest man = mans.get(fn);
                if (man != null) {
                    pkg = new Package(name, man, url, null);
                } else {
                    pkg = new Package(name, null, null, null,
                                      null, null, null, null, null);
                }
                pkgs.put(name, pkg);
                return pkg;
            }
        });
    }

    /*
     * Returns the Manifest for the specified JAR file name.
     * 返回指定JAR文件名的清单。
     */
    private static Manifest loadManifest(String fn) {
        try (FileInputStream fis = new FileInputStream(fn);
             JarInputStream jis = new JarInputStream(fis, false))
        {
            // 通过文件输入流返回加载的jar包清单
            return jis.getManifest();
        } catch (IOException e) {
            return null;
        }
    }

    // The map of loaded system packages
    private static Map<String, Package> pkgs = new HashMap<>(31);

    // Maps each directory or zip file name to its corresponding url
    private static Map<String, URL> urls = new HashMap<>(10);

    // Maps each code source url for a jar file to its manifest
    private static Map<String, Manifest> mans = new HashMap<>(10);

    private static native String getSystemPackage0(String name);
    private static native String[] getSystemPackages0();

    /*
     * Private storage for the package name and attributes.
     */
    private final String pkgName;
    private final String specTitle;
    private final String specVersion;
    private final String specVendor;
    private final String implTitle;
    private final String implVersion;
    private final String implVendor;
    private final URL sealBase;
    private transient final ClassLoader loader;
    private transient Class<?> packageInfo;
}
