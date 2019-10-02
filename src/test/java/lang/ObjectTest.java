package test.java.lang;

import test.java.entity.Parent;
import test.java.entity.Son;

public class ObjectTest {

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("调用finalize方法");
    }

    public static void main(String[] args) throws Exception{
        Parent firstSon = new Son();
        // 返回结果 class test.java.entity.Son
        // getClass()方法返回的是运行类的类型，而不是声明类的类型
        System.out.println(firstSon.getClass());

        Son son1 = new Son();
        Son son2 = new Son();
        Son son3 = son1;
        // equals结果不同，hashcode不同
        System.out.println(son1.equals(son2));
        System.out.println("son1的hashcode：" + son1.hashCode() + " son2的hashCode：" + son2.hashCode());
        // equals结果相同，hashCode相同,hashCode的算法与对象地址引用有关，相同对象地址引用，hashCode一定相同
        // 但是由于不同地址可能算出相同的hashCode，所以相同的hashCode，equals不一定为true
        System.out.println(son1.equals(son3));
        System.out.println("son1的hashcode：" + son1.hashCode() + " son3的hashCode：" + son3.hashCode());

        // 在test.java.entity.Teacher中测试clone方法

        // test.java.entity.Son@1b6d3586
        System.out.println(son1.toString());

        // 该对象创建后没有被引用，则下次垃圾回收时该对象会被回收，而垃圾回收需要特定条件才能进行，故而通过
        // System.gc() 方法来手动执行，之后便会调用finalize方法
        new ObjectTest();
        System.gc();
    }
}
