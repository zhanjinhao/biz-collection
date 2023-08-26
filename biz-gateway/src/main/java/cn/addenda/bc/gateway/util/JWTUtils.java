package cn.addenda.bc.gateway.util;

import cn.addenda.bc.bc.jc.util.JacksonUtils;
import cn.addenda.bc.bc.uc.user.UserConstant;
import cn.addenda.bc.bc.uc.user.UserInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JWTUtils {

    public static final long EXPIRATION = 24 * 60 * 60L;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String ISS = "addenda";
    public static final Key SECRET = new SecretKeySpec("biz-collection-addenda".getBytes(), SignatureAlgorithm.HS512.getJcaName());

    /**
     * 生成用户 Token
     *
     * @param userInfo 用户信息
     * @return 用户访问 Token
     */
    public static String generateToken(UserInfo userInfo) {
        Map<String, Object> customerUserMap = new HashMap<>();
        customerUserMap.put(UserConstant.USER_ID_KEY, userInfo.getUserId());
        customerUserMap.put(UserConstant.USER_NAME_KEY, userInfo.getUsername());
        String jwtToken = Jwts.builder()
            .signWith(SignatureAlgorithm.HS512, SECRET)
            .setIssuedAt(new Date())
            .setIssuer(ISS)
            .setSubject(JacksonUtils.objectToString(customerUserMap))
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION * 1000))
            .compact();
        return TOKEN_PREFIX + jwtToken;
    }

    /**
     * 解析用户 Token
     *
     * @param jwtToken 用户访问 Token
     * @return 用户信息
     */
    public static UserInfo parseToken(String jwtToken) {
        if (StringUtils.hasText(jwtToken)) {
            String actualJwtToken = jwtToken.replace(TOKEN_PREFIX, "");
            try {
                Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(actualJwtToken).getBody();
                Date expiration = claims.getExpiration();
                if (expiration.after(new Date())) {
                    String subject = claims.getSubject();
                    return JacksonUtils.stringToObject(subject, UserInfo.class);
                }
            } catch (ExpiredJwtException ignored) {
            } catch (Exception ex) {
                log.error("JWT Token解析失败，请检查", ex);
            }
        }
        return null;
    }
}