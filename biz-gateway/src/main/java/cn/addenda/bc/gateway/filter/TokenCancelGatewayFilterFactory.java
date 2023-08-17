package cn.addenda.bc.gateway.filter;

import cn.addenda.bc.bc.ServiceException;
import cn.addenda.bc.bc.jc.util.JacksonUtils;
import cn.addenda.bc.bc.sc.result.ControllerResult;
import cn.addenda.bc.bc.sc.result.Status;
import cn.addenda.bc.bc.uc.user.UserConstant;
import cn.addenda.bc.gateway.constant.RedisKeyConstant;
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
import org.springframework.data.redis.core.StringRedisTemplate;
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
import java.util.List;

/**
 * @author addenda
 * @since 2023/8/16 15:59
 */
@Slf4j
@Component
public class TokenCancelGatewayFilterFactory extends AbstractGatewayFilterFactory<TokenCancelGatewayFilterFactory.TokenCancelConfig> {

    public TokenCancelGatewayFilterFactory() {
        super(TokenCancelConfig.class);
    }

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public GatewayFilter apply(TokenCancelConfig config) {
        return new TokenCancelGatewayFilter(config);
    }

    public final class TokenCancelGatewayFilter implements GatewayFilter, Ordered {
        private final TokenCancelConfig config;

        public TokenCancelGatewayFilter(TokenCancelConfig config) {
            this.config = config;
        }

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            ServerHttpRequest request = exchange.getRequest();
            String requestPath = request.getPath().toString();
            // 配置的路径之后，删除TOKEN
            if (ifInPathList(requestPath, config.getPathList())) {
                String userId = exchange.getRequest().getHeaders().getFirst(UserConstant.USER_ID_KEY);
                return chain.filter(exchange.mutate()
                    .response(new TokenCancelResponseDecorator(exchange.getResponse(), userId)).build());
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
            return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 1;
        }
    }


    @Setter
    @Getter
    @ToString
    public static class TokenCancelConfig {

        /**
         * 需要取消token的路径
         */
        private List<String> pathList;

    }


    public class TokenCancelResponseDecorator extends ServerHttpResponseDecorator {

        private final String userId;

        public TokenCancelResponseDecorator(ServerHttpResponse delegate, String userId) {
            super(delegate);
            this.userId = userId;
        }

        @Override
        @SuppressWarnings(value = "unchecked")
        public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
            ServerHttpResponse delegate = getDelegate();
            if (delegate.getStatusCode() == HttpStatus.OK) {
                return super.writeWith(
                    ((Flux<? extends DataBuffer>) body)
                        .doOnNext(dataBuffer -> {
                            String respBody = dataBuffer.toString(StandardCharsets.UTF_8);
                            ControllerResult<?> userInfoDTO = null;
                            try {
                                userInfoDTO = JacksonUtils.stringToObject(respBody, new TypeReference<ControllerResult<?>>() {
                                });
                            } catch (Exception e) {
                            }
                            if (userInfoDTO == null) {
                                String msg = String.format("Cannot cancel token from response. Response data must be ControllerResult's object. Response body is: [%s].", respBody);
                                throw new ServiceException(msg);
                            }
                            Status reqStatus = userInfoDTO.getReqStatus();
                            if (reqStatus != Status.SUCCESS) {
                                String msg = String.format("Cannot cancel token from response. Response status is [%s]. Response body is: [%s].", reqStatus, respBody);
                                throw new ServiceException(msg);
                            }

                            // --------------------------------------------------
                            //   删除用户下的有效token and 删除有效token集合里的token
                            // --------------------------------------------------

                            String token = stringRedisTemplate.opsForValue().get(RedisKeyConstant.TOKENS_USER_PREFIX + userId);
                            stringRedisTemplate.delete(RedisKeyConstant.TOKENS_USER_PREFIX + userId);
                            // 这里事实上时不需要判断token是否为空的，因为这个接口的调用肯定时要走验证的（token必须存在才能过验证）
                            // 但是为了增强鲁棒性，这里还是加了一次判断。"增加空判断"与"退出的最终目的是删除redis里的token"不矛盾
                            if (token != null) {
                                stringRedisTemplate.opsForZSet().remove(RedisKeyConstant.TOKENS_ALL, token);
                            }
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
