package cn.addenda.bc.bc.jc.util;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author addenda
 * @since 2022/2/7 12:38
 */
public class StringUtils {

    private StringUtils() {
    }

    public static String expandWithSpecifiedChar(String str, char specifiedChar, int expectLength) {
        int length = str.length();
        StringBuilder zero = new StringBuilder();
        for (int i = length; i < expectLength; i++) {
            zero.append(specifiedChar);
        }
        return zero.append(str).toString();
    }

    public static String expandWithZero(String str, int expectLength) {
        return expandWithSpecifiedChar(str, '0', expectLength);
    }

    public static String join(String separator, String... values) {
        if (values.length == 0) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i == 0) {
                result.append(!org.springframework.util.StringUtils.hasText(values[i]) ? "" : values[i]);
            } else {
                result.append(!org.springframework.util.StringUtils.hasText(values[i]) ? "" : separator + values[i]);
            }
        }
        return result.toString();
    }

    public static boolean checkIsDigit(String piece) {
        if (!org.springframework.util.StringUtils.hasText(piece)) {
            return false;
        }
        return piece.matches("\\d+");
    }

    public static String joinArrayToString(String[] pieces, int fromIndex, int endIndex) {
        if (pieces == null) {
            return null;
        }
        int length = pieces.length;
        if (endIndex > length || fromIndex < 0) {
            throw new UtilException("数组长度: " + length + ", 起始索引: " + fromIndex + ", 终止索引: " + endIndex);
        }
        return String.join(" ", Arrays.stream(pieces).collect(Collectors.toList()).subList(fromIndex, endIndex));
    }

    public static String joinArrayToString(String content, int fromIndex, int endIndex) {
        if (!org.springframework.util.StringUtils.hasText(content)) {
            return content;
        }
        return joinArrayToString(content.split("\\s+"), fromIndex, endIndex);
    }

    public static String replaceCharAtIndex(String str, int index, char newChar) {
        if (str == null) {
            return null;
        }
        int length = str.length();
        if (index < 0 || index >= length) {
            throw new UtilException("非法的参数，当前字符串：" + str + "，长度：" + index + "，索引值：" + index + "。");
        }

        return str.substring(0, index) + newChar + str.substring(index + 1);
    }

    public static String discardNull(String str) {
        if (str == null) {
            return "";
        }
        return str;
    }

}
