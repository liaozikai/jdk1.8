package test.java.util;

import test.java.entity.Student;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liaozikai
 * @title: HashMapTest
 * @description: TODO
 */
public class HashMapTest {
    public static void main(String[] args) {
        Map<Integer,String> hm1 = new HashMap<>(5);
        hm1.put(1,"A");
        hm1.put(2,"B");
        hm1.put(3,"C");
        // 长度为3
        System.out.println(hm1.size());
        System.out.println(hm1.isEmpty());
        System.out.println(hm1.get(1));
        System.out.println(hm1.containsKey(1));
        System.out.println(hm1.containsValue("B"));

        Map<Integer,String> hm2 = new HashMap<>(5);
        hm2.putAll(hm1);
        hm2.put(4,"D");
        hm2.entrySet().iterator().forEachRemaining(e->{
            // 输出 1:A 2:B 3:C 4:D
            System.out.println(e.getKey() + ":" + e.getValue());
        });

        hm1.remove(2);
        hm1.entrySet().iterator().forEachRemaining(e->{
            // 输出 1:A 3:C
            System.out.println(e.getKey() + ":" + e.getValue());
        });

        hm1.keySet().forEach(e->{
            // 输出 1 3
            System.out.println(e);
        });
        hm1.values().forEach(e->{
            // 输出A C
            System.out.println(e);
        });

        // 输出A，因为1存在，所以不返回默认的G
        System.out.println(hm1.getOrDefault(1,"G"));
        // 输出G，因为4不存在，所以返回默认的G
        System.out.println(hm1.getOrDefault(4,"G"));
        // 如果key值不存在，则插入key为1，value为G的映射，由于1存在，所以返回A
        System.out.println(hm1.putIfAbsent(1,"G"));
        // 返回null，插入成功
        System.out.println(hm1.putIfAbsent(5,"TG"));
        hm1.entrySet().forEach(e->{
            // 输出 1:A 3:C 5:TG
            System.out.println(e.getKey() + ":" + e.getValue());
        });
        // 由于只有key存在，value不等于A，故而删不掉
        hm1.remove(1,"G");
        hm1.entrySet().forEach(e->{
            // 输出 1:A 3:C 5:TG
            System.out.println(e.getKey() + ":" + e.getValue());
        });

        hm1.remove(1,"A");
        hm1.entrySet().forEach(e->{
            // 输出 3:C 5:TG
            System.out.println(e.getKey() + ":" + e.getValue());
        });

        // 替换不存在的key是不起作用的
        hm1.replace(2,"F");
        hm1.entrySet().forEach(e->{
            // 输出 3:C 5:TG
            System.out.println(e.getKey() + ":" + e.getValue());
        });

        hm1.replace(3,"P");
        hm1.entrySet().forEach(e->{
            // 输出 3:P 5:TG
            System.out.println(e.getKey() + ":" + e.getValue());
        });

        // 由于oldValue的值不为P，故而替换不成功
        hm1.replace(3,"A","G");
        hm1.entrySet().forEach(e->{
            // 输出 3:P 5:TG
            System.out.println(e.getKey() + ":" + e.getValue());
        });

        hm1.replace(3,"P","G");
        hm1.entrySet().forEach(e->{
            // 输出 3:G 5:TG
            System.out.println(e.getKey() + ":" + e.getValue());
        });

        // 如果没有key为4的映射，则插入
        hm1.computeIfAbsent(4, k -> new Integer(4 * 20).toString());
        hm1.entrySet().forEach(e->{
            // 输出 3:G 4:80 5:TG
            System.out.println(e.getKey() + ":" + e.getValue());
        });

        // 不存在key为6，故而直接插入
        hm1.compute(6, (integer, s) -> "BFG");
        hm1.entrySet().forEach(e->{
            // 输出 3:G 4:80 5:TG 6:BFG
            System.out.println(e.getKey() + ":" + e.getValue());
        });

        hm1.computeIfPresent(6,(integer, s) -> "ABC");
        hm1.entrySet().forEach(e->{
            // 输出 3:G 4:80 5:TG 6:ABC
            System.out.println(e.getKey() + ":" + e.getValue());
        });

        // 由于存在key为6的键值对，故而，合并后输出数据
        hm1.merge(6,"DEF",(s, s2) -> s + "AHC");
        hm1.entrySet().forEach(e->{
            // 输出 3:G 4:80 5:TG 6:ABCAHC
            System.out.println(e.getKey() + ":" + e.getValue());
        });

        // 不存在key为7的键值对，则将DEF与7关联起来
        hm1.merge(7,"DEF",(s, s2) -> "AHC");
        hm1.entrySet().forEach(e->{
            // 输出 3:G 4:80 5:TG 6:ABCAHC 7:DEF
            System.out.println(e.getKey() + ":" + e.getValue());
        });

        // 用新构成的数据来替换原先所有的数据
        hm1.replaceAll((i, s) -> i + s);
        hm1.entrySet().forEach(e->{
            // 输出 3:3G 4:480 5:5TG 6:6ABCAHC 7:7DEF
            System.out.println(e.getKey() + ":" + e.getValue());
        });

        Map<String, Student> stu1 = new HashMap<>();
        stu1.put("s1",new Student("学生1"));
        Map<String, Student> stu2 = (Map<String, Student>) ((HashMap<String, Student>) stu1).clone();
        System.out.println(stu2.get("s1").getName());
        stu2.get("s1").setName("学生2");
        // 输出stu1的name可以查出就是浅克隆
        System.out.println(stu1.get("s1").getName());
    }
}
