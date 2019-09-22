package test.java.lang;

import test.java.entity.Parent;
import test.java.entity.Son;

public class ObjectTest {
    public static void main(String[] args) {
        Parent firstSon = new Son();
        System.out.println(firstSon.getClass());
    }
}
