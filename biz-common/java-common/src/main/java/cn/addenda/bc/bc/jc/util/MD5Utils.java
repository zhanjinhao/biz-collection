package cn.addenda.bc.bc.jc.util;

import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

public class MD5Utils {

    private MD5Utils() {
    }

    public static String md5(Object obj) {
        String jsonString = JacksonUtils.objectToString(obj);
        byte[] bytes = jsonString.getBytes(StandardCharsets.UTF_8);
        return DigestUtils.md5DigestAsHex(bytes);
    }

}
