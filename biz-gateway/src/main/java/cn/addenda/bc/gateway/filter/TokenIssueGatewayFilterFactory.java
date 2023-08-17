package cn.addenda.bc.gateway.filter;

import cn.addenda.bc.bc.ServiceException;
import cn.addenda.bc.bc.jc.util.JacksonUtils;
import cn.addenda.bc.bc.sc.result.ControllerResult;
import cn.addenda.bc.bc.sc.result.Status;
import cn.addenda.bc.bc.uc.user.UserInfoDTO;
import cn.addenda.bc.gateway.constant.RedisKeyConstant;
import cn.addenda.bc.gateway.util.JWTUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author addenda
 * @since 2023/8/16 15:59
 */
@Slf4j
@Component
public class TokenIssueGatewayFilterFactory extends AbstractGatewayFilterFactory<TokenIssueGatewayFilterFactory.TokenIssueConfig> {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public TokenIssueGatewayFilterFactory() {
        super(TokenIssueConfig.class);
    }

    @Override
    public GatewayFilter apply(TokenIssueConfig config) {
        return new TokenIssueGatewayFilter(config);
    }

    public final class TokenIssueGatewayFilter implements GatewayFilter, Ordered {
        private final TokenIssueConfig config;

        public TokenIssueGatewayFilter(TokenIssueConfig config) {
            this.config = config;
        }

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            ServerHttpRequest request = exchange.getRequest();
            String requestPath = request.getPath().toString();
            // 配置的路径之后，注入token。
            if (ifInPathList(requestPath, config.getPathList())) {
                return chain.filter(exchange.mutate()
                    .response(new TokenIssueResponseDecorator(exchange.getResponse())).build());
            }
            return chain.filter(exchange);
        }

        private boolean ifInPathList(String requestPath, List<String> pathPreList) {
            if (CollectionUtils.isEmpty(pathPreList)) {
                return false;
            }
            return pathPreList.stream().anyMatch(requestPath::equals);
        }

        @Override
        public int getOrder() {
            return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 3;
        }
    }


    @Setter
    @Getter
    @ToString
    public static class TokenIssueConfig {

        /**
         * 返回值需要注入token的路径
         */
        private List<String> pathList;

    }

    public class TokenIssueResponseDecorator extends ServerHttpResponseDecorator {
        public TokenIssueResponseDecorator(ServerHttpResponse delegate) {
            super(delegate);
        }

        @Override
        @SuppressWarnings(value = "unchecked")
        public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
            ServerHttpResponse delegate = getDelegate();
            if (delegate.getStatusCode() == HttpStatus.OK && body instanceof Flux) {
                return super.writeWith(
                    ((Flux<? extends DataBuffer>) body)
                        .doOnNext(dataBuffer -> {
                            String respBody = dataBuffer.toString(StandardCharsets.UTF_8);
                            ControllerResult<UserInfoDTO> userInfoDTO = null;
                            try {
                                userInfoDTO = JacksonUtils.stringToObject(respBody, new TypeReference<ControllerResult<UserInfoDTO>>() {
                                });
                            } catch (Exception e) {
                            }
                            if (userInfoDTO == null) {
                                String msg = String.format("Cannot issue token from response. Response data must be ControllerResult<UserInfoDTO>'s object. Response body is: [%s].", respBody);
                                throw new ServiceException(msg);
                            }
                            Status reqStatus = userInfoDTO.getReqStatus();
                            if (reqStatus != Status.SUCCESS) {
                                String msg = String.format("Cannot issue token from response. Response status is [%s]. Response body is: [%s].", reqStatus, respBody);
                                throw new ServiceException(msg);
                            }
                            UserInfoDTO result = userInfoDTO.getResult();
                            if (result == null || result.getUserId() == null || result.getUsername() == null) {
                                String msg = String.format("Cannot issue token from response. UserInfoDTO cannot be null. Response body is: [%s].", respBody);
                                throw new ServiceException(msg);
                            }
                            String token = JWTUtils.generateToken(result);
                            // TOKENS_USER_PREFIX:user  ->  存用户的有效token
                            // TOKENS_ALL               ->  存所有有效的token
                            String oldToken = stringRedisTemplate.opsForValue().get(RedisKeyConstant.TOKENS_USER_PREFIX + result.getUserId());
                            stringRedisTemplate.opsForValue().set(RedisKeyConstant.TOKENS_USER_PREFIX + result.getUserId(), token, JWTUtils.EXPIRATION, TimeUnit.SECONDS);
                            if (oldToken != null) {
                                stringRedisTemplate.opsForZSet().remove(RedisKeyConstant.TOKENS_ALL, oldToken);
                            }
                            // todo 起定时器，定时删除TOKENS_ALL里过期的token
                            Set<ZSetOperations.TypedTuple<String>> tuples = new HashSet<>();
                            tuples.add(new DefaultTypedTuple<>(token, (double) (System.currentTimeMillis() + JWTUtils.EXPIRATION * 1000)));
                            stringRedisTemplate.opsForZSet().add(RedisKeyConstant.TOKENS_ALL, tuples);
                            delegate.getHeaders().add(HttpHeaders.AUTHORIZATION, token);
                        })
                );
            }
            return super.writeWith(body);
        }

        @Override
        public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
            return this.writeWith(Flux.from(body).flatMapSequential(p -> p));
        }

    }

}
