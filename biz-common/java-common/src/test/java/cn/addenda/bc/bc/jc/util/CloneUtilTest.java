package cn.addenda.bc.bc.jc.util;

import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * @author addenda
 * @since 2022/2/15
 */
public class CloneUtilTest {

    public static void main(String[] args) {

        List<Son> sons1 = ArrayUtils.asArrayList(new Son("a", "1"), new Son("b", "2"), new Son("c", "2"));
        System.out.println(CloneUtils.cloneByJDKSerialization(sons1));

        List<Son> sons2 = ArrayUtils.asLinkedList(new Son("a", "1"), new Son("b", "2"), new Son("c", "2"));
        System.out.println(CloneUtils.cloneByJDKSerialization(sons2));

        Set<Son> sons3 = ArrayUtils.asHashSet(new Son("a", "1"), new Son("b", "2"), new Son("c", "2"));
        System.out.println(CloneUtils.cloneByJDKSerialization(sons3));
    }

    @Setter
    @Getter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    static class Son implements Serializable {

        private String name;
        private String age;

    }

}
