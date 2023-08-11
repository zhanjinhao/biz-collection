package cn.addenda.bc.bc.jc.util;

import java.io.*;
import java.util.Collection;

/**
 * @author addenda
 * @since 2023/05/30
 */
public class CloneUtils {

    private CloneUtils() {
    }

    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T cloneByJDKSerialization(T obj) {
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bout);
            oos.writeObject(obj);
            ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bin);
            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new UtilException("克隆对象出错！", e);
        }
    }

    public static <T extends Serializable> Collection<T> cloneByJDKSerialization(Collection<T> obj) {
        if (obj == null) {
            return null;
        }
        Collection<T> newList = newInstance((Class<Collection<T>>) obj.getClass());
        if (obj.isEmpty()) {
            return newList;
        }
        for (T next : obj) {
            newList.add(cloneByJDKSerialization(next));
        }
        return newList;
    }

    private static <T extends Serializable> Collection<T> newInstance(Class<Collection<T>> collection) {
        try {
            return collection.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new UtilException("反射生成集合对象失败！", e);
        }
    }

}
