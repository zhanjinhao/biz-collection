package cn.addenda.bc.bc.uc.util;

import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

/**
 * @author addenda
 * @since 2023/8/16 13:22
 */
public class JWTUtilsTest {

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            Key KEY = new SecretKeySpec("biz-collection-addenda".getBytes(),
                    SignatureAlgorithm.HS512.getJcaName());
            byte[] encoded = KEY.getEncoded();
            for (byte s : encoded) {
                System.out.print(s);
            }
            System.out.println();
        }
    }

}
