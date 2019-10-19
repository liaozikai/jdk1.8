package test.java.util;

import test.java.entity.Student;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * @author liaozikai
 * @title: ArrayListTest
 * @description: 测试ArrayList
 */
public class ArrayListTest {
    public static void main(String[] args) {
        List<String> al1 = new ArrayList<>();
        // 长度为0
        System.out.println(al1.size());
        // 返回true
        System.out.println(al1.isEmpty());
        al1.add("A");
        // 长度为1 注意，list虽然是由分配容量，但是没有方法获取到分配的容量大小
        // 因为elementData是transient的，获取不到其分配的容量大小
        System.out.println(al1.size());
        // 返回false
        System.out.println(al1.isEmpty());

        // 返回true。注意，虽然在继承体系中，ArrayList继承AbstractList，AbstractList继承自AbstractCollection
        // 而AbstractCollection实现了contain方法，但是ArrayList重写了该方法
        System.out.println(al1.contains("A"));

        // 虽然AbstractList实现了iterator的逻辑，但是ArrayList重写了该方法，并优化了迭代器逻辑
        Iterator iterator = al1.iterator();
        // 返回A
        System.out.println(iterator.next());

        al1.add("B");
        // 该方法主要是将list转换为数组。ArrayList与AbstractCollection的实现是不一样的
        Object[] objs = al1.toArray();
        for(Object str: objs) {
            // 输出A，B
            System.out.println(str);
        }


        // 该方法是有精确类型的，只是必须是有长度的数组
        String str[] = al1.toArray(new String[10]);
        for(String s:str) {
            // 除了输入A，B，还输出8个null，因为String[10]比al1本身长度长，故而取String[10]的长度
            System.out.println(s);
        }

        // 返回false
        System.out.println(al1.remove("C"));
        // 返回true
        System.out.println(al1.remove("A"));

        // 输出B
        al1.forEach(s -> {
            System.out.println(s);
        });
        // 并且B的位置是0.因为删除元素会把后面的元素往前移
        System.out.println(al1.indexOf("B"));
        // 输出B，set方法会返回旧元素，并且替换新元素到指定位置
        System.out.println(al1.set(0,"A"));
        al1.forEach(s->{
            // 只有A存在
            System.out.println(s);
        });
        al1.add("B");

        List<String> al2 = new ArrayList<>();
        al2.add("B");

        // 返回true
        System.out.println(al1.containsAll(al2));

        List<String> al3 = new ArrayList<>();
        al3.add("C");
        al1.addAll(al3);
        al1.forEach(s -> {
            // 输出A B C
            System.out.println(s);
        });

        // 把al2移除
        al1.removeAll(al2);
        al1.forEach(s -> {
            // 输出A C
            System.out.println(s);
        });
        al1.add("D");
        al1.add("B");
        al1.forEach(s->{
            // 输出 A C D B
            System.out.println(s);
        });

        // lambda写法，将D给移除掉
        al1.removeIf(
                t -> t.equals("D")
        );
        // 输出A C B
        al1.forEach(s->{
            System.out.println(s);
        });

        // al1中保留al2的数据
        al1.retainAll(al2);
        al1.forEach(s->{
            // 输出B
            System.out.println(s);
        });

        List<String> linkList = new LinkedList<>();
        linkList.add("E");
        linkList.add("F");
        linkList.add("G");

        // 该构造器将LinkLst转换为ArrayList，其实这就是集合中toArray方法的常用之处。
        List<String> al4 = new ArrayList<>(linkList);
        al4.forEach(s-> {
            System.out.println(s);
        });

        System.out.println(al1.size());
        ((ArrayList<String>) al1).ensureCapacity(12);
        // 获取不到elementData，所以这个方法看不出来扩展的长度，size表示al中拥有的元素个数，
        // 而不是分配的空间长度大小
        System.out.println(al1.size());

        al1.addAll(al4);
        al1.forEach(s-> {
            // 输出 B E F G
            System.out.println(s);
        });

        Object al5 = ((ArrayList<String>) al1).clone();
        if(al5 instanceof  ArrayList) {
            ((ArrayList) al5).forEach(s->{
                // 输出 B E F G
                System.out.println(s);
            });

            ((ArrayList) al5).set(0,"A");
            // 返回A
            System.out.println(((ArrayList) al5).get(0));
            // 返回B 说明对于基本类型和String类型，它的复制是深度的
            System.out.println(al1.get(0));
        }

        List<Student> s1List = new ArrayList<>();
        Student s1 = new Student();
        s1.setName("Hello World");
        s1List.add(s1);

        Object s2List = ((ArrayList<Student>) s1List).clone();
        if(s2List instanceof  ArrayList) {
            s2List = new ArrayList<Student>((ArrayList<Student>) s2List);
            Student student =(Student) ((ArrayList) s2List).get(0);
            System.out.println(student.getName());
            student.setName("Hello Java World");
            // 输出 Hello Java World
            System.out.println(student.getName());
            // 输出 Hello Java World 说明该赋值是浅复制，只对基本类型和String.类型的复制有效
            System.out.println(s1List.get(0).getName());
        }
//        清除后，长度会被重置为0
//        s1List.clear();
//        System.out.println(s1List.size());
        al1.forEach(s->{
            System.out.println(s);
        });

        // 返回true，可以看到equal的实现是单个元素一一比较的
        List<String> al6 = new ArrayList<>(al1);
        System.out.println(al1.equals(al6));

        /**
         * 集合提供了几个方法转化为Spliterator和流的方法啊，
         * 下面只是简单试下转换的方法，之后再专门写Spliterator和Stream的内容
         */
        Spliterator<String> sp1 = al1.spliterator();
        System.out.println(sp1);
        System.out.println(sp1.estimateSize());

        //  关于函数式编程，有篇文章写的很好
        // https://blog.csdn.net/icarusliu/article/details/79495534
        sp1.forEachRemaining(new Consumer<String>() {
            @Override
            public void accept(String s) {
                System.out.println(s);
            }
        });

        // 输入E
        Stream<String> stream = al1.stream();
        stream.filter(s->s.contains("E")).forEach(s -> {
            System.out.println(s);
        });

        // 输出B
        Stream<String> prpallStream = al1.parallelStream();
        prpallStream.filter(s->s.contains("B")).forEach(s -> {
            System.out.println(s);
        });

        // 上面的方法是Collection共有的方法，所有子类去继承和实现
        // 下面的方法是某些子类独有的
        al1.forEach(s -> {
            // al1 的元素是BEFG
            System.out.println(s);
        });
        // 将H插入进去，其他的元素都往后移，与set很大不同
        al1.add(0,"H");
        al1.forEach(s -> {
            // 输出是HBEFG
            System.out.println(s);
        });

        al1.add("B");
        // 输出是5 这是找出最后与B相等的对象下标
        System.out.println(al1.lastIndexOf("B"));

        // al3只有C，addAll是把所有元素加入al1指定位置中，然后
        // 后面的元素往后移
        al1.addAll(3,al3);
        al1.forEach(s -> {
            // 输出是HBECFGB
            System.out.println(s);
        });

        Iterator<String> al1Itr1 = al1.iterator();
        Iterator<String> al1Itr2 = al1.listIterator();
        Iterator<String> al1Itr3 = al1.listIterator(2); // 从第二位开始截断到结尾

        // 输出是HBECFGB
        al1Itr1.forEachRemaining(s-> System.out.println(s));
        // 输出是HBECFGB
        al1Itr2.forEachRemaining(s-> System.out.println(s));
        // 输出是ECFGB
        al1Itr3.forEachRemaining(s-> System.out.println(s));

        // iterator()方法获得的只有几个方法hasNext,remove,next,forEachRemaining,不演示，以listIterator演示
        // 输出7，因为forEachReamining使得指针移到最后
        System.out.println(((ListIterator<String>) al1Itr2).nextIndex());
        // 抛出异常，所以要先判断hasNext，true才能继续
        // System.out.println(al1Itr2.next());
        // 输出6
        System.out.println(((ListIterator<String>) al1Itr2).previousIndex());
        // 输出 false 当前指针已经指到尾部
        System.out.println(((ListIterator<String>) al1Itr2).hasNext());
        // 输出B
        System.out.println(((ListIterator<String>) al1Itr2).previous());
        // 输出 true 由于previous会移动指针，故而再次判断hasNext，会向下移动一位判断
        System.out.println(((ListIterator<String>) al1Itr2).hasNext());
        // 输出B
        System.out.println(al1Itr2.next());
        // 输出true
        System.out.println(((ListIterator<String>) al1Itr2).hasPrevious());
        // 看源码才能好好理解下标与这些方法的关系

        ((ListIterator<String>) al1Itr2).add("Q");
        ((ListIterator<String>) al1Itr2).add("B");
        // add的时候游标往后移，所以输不出什么
        al1Itr2.forEachRemaining(s-> System.out.println(s));

        ((ListIterator<String>) al1Itr2).add("Q");
        ((ListIterator<String>) al1Itr2).add("B");
        al1.forEach(s -> {
            // 由于迭代器指向的还是al数组的位置，故而增加也是al1数组的增加，输出是HBECFGBQB
            System.out.println(s);
        });
        // remove方法和set方法就不写了，跟ArrayList的remove和set方法差不多
        // 由于LinkedList与ArrayList是不同结构的相同实现，故而不写测试。不过
        // 一般我们如果要查找常用ArrayList，而如果操作则使用linkedList
    }
}
