package cn.addenda.bc.bc.jc.util;

/**
 * @author addenda
 * @since 2023/3/2 19:29
 */
public class StackTraceUtilsTest {


    public static void main(String[] args) {

        System.out.println(StackTraceUtils.getCallerInfo());
        System.out.println(StackTraceUtils.getDetailedCallerInfo());
    }

}
