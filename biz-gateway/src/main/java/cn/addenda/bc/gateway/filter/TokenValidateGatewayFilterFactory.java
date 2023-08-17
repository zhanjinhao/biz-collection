package cn.addenda.bc.gateway.filter;

import cn.addenda.bc.bc.jc.util.UrlUtils;
import cn.addenda.bc.bc.uc.user.UserConstant;
import cn.addenda.bc.bc.uc.user.UserInfoDTO;
import cn.addenda.bc.gateway.constant.RedisKeyConstant;
import cn.addenda.bc.gateway.util.JWTUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * SpringCloud Gateway Token 拦截器
 */
@Component
public class TokenValidateGatewayFilterFactory extends AbstractGatewayFilterFactory<TokenValidateGatewayFilterFactory.TokenValidateConfig> {

    public TokenValidateGatewayFilterFactory() {
        super(TokenValidateConfig.class);
    }

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public GatewayFilter apply(TokenValidateConfig config) {
        return new TokenValidateGatewayFilter(config);
    }

    private class TokenValidateGatewayFilter implements GatewayFilter, Ordered {

        private final TokenValidateConfig config;

        public TokenValidateGatewayFilter(TokenValidateConfig config) {
            this.config = config;
        }

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            ServerHttpRequest request = exchange.getRequest();
            String requestPath = request.getPath().toString();
            // 不在白名单中，需要走认证 or 在黑名单中，需要走认证
            if ((config.getMode() == TokenMode.WHITE && !ifInPathPreList(requestPath, config.getPathPreList()))
                || config.getMode() == TokenMode.BLACK && ifInPathPreList(requestPath, config.getPathPreList())) {
                String userId = request.getHeaders().getFirst(UserConstant.USER_ID_KEY);

                // only for test.
                if (StringUtils.hasLength(userId)) {
                    String userName = request.getHeaders().getFirst(UserConstant.USER_NAME_KEY);
                    ServerHttpRequest httpRequest = exchange.getRequest().mutate().headers(httpHeaders -> {
                        httpHeaders.set(UserConstant.USER_ID_KEY, userId);
                        if (StringUtils.hasLength(userName)) {
                            httpHeaders.set(UserConstant.USER_NAME_KEY, UrlUtils.encode(userName));
                        }
                    }).build();

                    return chain.filter(exchange.mutate().request(httpRequest).build());
                }

                String token = request.getHeaders().getFirst(UserConstant.AUTHORIZATION);

                // 没有token的时候直接返回未认证
                if (!StringUtils.hasLength(token)) {
                    ServerHttpResponse response = exchange.getResponse();
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    return response.setComplete();
                }

                // token无效的时候，直接返回未认证
                Double score = stringRedisTemplate.opsForZSet().score(RedisKeyConstant.TOKENS_ALL, token);
                if (score == null || score < 0) {
                    ServerHttpResponse response = exchange.getResponse();
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    return response.setComplete();
                }

                // token无法解析出用户信息时，直接返回未认证
                UserInfoDTO userInfo = JWTUtils.parseToken(token);
                if (!validateToken(userInfo)) {
                    ServerHttpResponse response = exchange.getResponse();
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    return response.setComplete();
                }

                ServerHttpRequest httpRequest = exchange.getRequest().mutate().headers(httpHeaders -> {
                    httpHeaders.set(UserConstant.USER_ID_KEY, userInfo.getUserId());
                    httpHeaders.set(UserConstant.USER_NAME_KEY, UrlUtils.encode(userInfo.getUsername()));
                    // 不传token到业务服务
//                    httpHeaders.set(UserConstant.AUTHORIZATION, token);
                }).build();

                return chain.filter(exchange.mutate().request(httpRequest).build());
            }
            return chain.filter(exchange);
        }

        @Override
        public int getOrder() {
            return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 2;
        }

        private boolean ifInPathPreList(String requestPath, List<String> pathPreList) {
            if (CollectionUtils.isEmpty(pathPreList)) {
                return false;
            }
            return pathPreList.stream().anyMatch(requestPath::startsWith);
        }

        private boolean validateToken(UserInfoDTO userInfo) {
            return userInfo != null;
        }
    }


    @Setter
    @Getter
    @ToString
    public static class TokenValidateConfig {

        private TokenMode mode;

        /**
         * 路径前缀
         */
        private List<String> pathPreList;

    }

    public enum TokenMode {
        WHITE, BLACK
    }

}
