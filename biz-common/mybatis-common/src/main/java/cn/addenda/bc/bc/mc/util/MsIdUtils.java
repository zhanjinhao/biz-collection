package cn.addenda.bc.bc.mc.util;

import cn.addenda.bc.bc.jc.util.AnnotationUtils;
import cn.addenda.bc.bc.mc.MybatisCommonException;

import java.lang.annotation.Annotation;

/**
 * @author addenda
 * @since 2023/8/10 19:05
 */
public class MsIdUtils {

    public static <T extends Annotation> T extract(String msId, Class<T> tClass) {
        int end = msId.lastIndexOf(".");
        try {
            Class<?> aClass = Class.forName(msId.substring(0, end));
            String methodName = msId.substring(end + 1);
            return AnnotationUtils.extractAnnotationFromMethod(aClass, methodName, tClass);
        } catch (ClassNotFoundException e) {
            String msg = String.format("无法找到对应的Mapper：[%s]。", msId);
            throw new MybatisCommonException(msg, e);
        }
    }
}
