package cn.addenda.bc.bc.jc.util;

import org.junit.Test;

import java.util.*;
import java.util.function.Function;

/**
 * @author addenda
 * @since 2022/11/17 19:18
 */
public class IterableUtilsTest {

    @Test
    public void test1() {
        IterableUtils.acceptInBatches(ArrayUtils.asArrayList("a", "b"), System.out::println, 1);
    }

    @Test
    public void test2() {
        IterableUtils.applyInBatches(ArrayUtils.asArrayList("a", "b"), new Function<Iterable<String>, Iterable<Void>>() {
            @Override
            public Iterable<Void> apply(Iterable<String> objects) {
                System.out.println(objects);
                return null;
            }
        }, 1);
    }


    @Test
    public void test3() {
        IterableUtils.acceptInBatches(
            ArrayUtils.asArrayList("a", "b"),
            ArrayUtils.asArrayList(1, 2),
            (objects, objects2) -> System.out.println(objects.toString() + objects2.toString()), 1);
    }

    @Test
    public void test4() {
        List<String> list = IterableUtils.applyInBatches(
            ArrayUtils.asArrayList("a", "b"),
            ArrayUtils.asArrayList(1, 2),
            (objects, objects2) -> {
                return new ArrayList<>(Collections.singletonList(objects.toString() + objects2.toString()));
            }, 1);
        list.forEach(System.out::println);
    }


    @Test
    public void test5() {
        List<String> list = Arrays.asList("1", "2", "1");
        System.out.println(IterableUtils.deDuplicate(list, Comparator.comparing(String::toString)));
        System.out.println(IterableUtils.deDuplicate(list, String::toString));
        System.out.println(IterableUtils.deDuplicate(list, Comparator.comparing(String::toString), ArrayList::new));
    }

}
