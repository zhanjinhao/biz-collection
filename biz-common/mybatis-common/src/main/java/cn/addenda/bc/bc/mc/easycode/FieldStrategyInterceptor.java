package cn.addenda.bc.bc.mc.easycode;

import cn.addenda.bc.bc.mc.util.MsIdUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.binding.MapperProxy;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.ParamNameResolver;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import static org.apache.ibatis.executor.keygen.SelectKeyGenerator.SELECT_KEY_SUFFIX;

/**
 * @author addenda
 * @since 2023/6/4 10:19
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update",
                args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class FieldStrategyInterceptor implements Interceptor {

    /**
     * mapper.xml.vm 里的setCondition代码片和queryCondition代码片需要使用两个参数：FieldStrategy和Entity。 <br/>
     * insertByEntity(Entity)/updateByEntity(Entity)/deleteByEntity(Entity)这种写法只传入Entity参数，
     * FieldStrategy参数由注解{@link FieldStrategyController#strategy()}注入。
     * 当有多个参数时，MyBatis用Map存储数据所有参数。key和value规则如下：<br/>
     * <li/> FieldStrategy的key是fieldStrategy，不可配置。
     * <li/> Entity的key默认是entity，可由此参数配置。
     */
    private String aloneParamName = "entity";

    private static final Map<String, FieldStrategyController> FIELD_STRATEGY_CONTROLLER_MAP = new ConcurrentHashMap<>();

    /**
     * Mybatis动态代理的入口：{@link MapperProxy#invoke(Object, Method, Object[])}。<br/>
     * 会调用到MapperMethod的：{@link MapperMethod#execute(SqlSession, Object[])}。<br/>
     * 原始的参数被{@link ParamNameResolver#getNamedParams(Object[])}处理之后传入
     * <ul>
     * <li>{@link Executor#update(MappedStatement, Object)}</li>
     * <li>{@link Executor#query(MappedStatement, Object, RowBounds, ResultHandler, CacheKey, BoundSql)}</li>
     * <li>{@link Executor#query(MappedStatement, Object, RowBounds, ResultHandler)}</li>
     * <li>{@link Executor#queryCursor(MappedStatement, Object, RowBounds)}</li>
     * </ul>
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];
        String msId = mappedStatement.getId();
        FieldStrategyController fieldStrategyController = extractFieldStrategyController(msId);
        if (fieldStrategyController != null) {
            String strategyName = fieldStrategyController.strategy().name();
            FieldStrategy strategy = fieldStrategyController.strategy();

            // 观察org.apache.ibatis.reflection.ParamNameResolver.getNamedParams的实现可知，返回值一共有三种类型
            // - 没有参数时返回null
            // - 有一个参数且没有@Param注解时返回参数本身
            // - 返回MapperMethod.ParamMap
            Object arg = args[1];
            if (arg == null) {
                MapperMethod.ParamMap<Object> paramMap = new MapperMethod.ParamMap<>();
                paramMap.put(strategyName, strategy);
                args[1] = paramMap;
            } else {
                if (arg instanceof MapperMethod.ParamMap) {
                    MapperMethod.ParamMap<Object> paramMap = (MapperMethod.ParamMap<Object>) arg;
                    if (paramMap.containsKey(strategyName)) {
                        String msg = String.format("[%s] has existed in MappedStatement[%s]'s param.", strategyName, msId);
                        throw new EasyCodeException(msg);
                    }
                    paramMap.put(strategyName, strategy);
                }
                // 一个参数且没有@Param时走这个地方
                else {
                    MapperMethod.ParamMap<Object> paramMap = new MapperMethod.ParamMap<>();
                    paramMap.put(strategyName, strategy);
                    // start from 1 instead of 0
                    paramMap.put("arg1", arg);
                    paramMap.put("param1", arg);
                    paramMap.put("1", arg);

                    if (aloneParamName != null && aloneParamName.length() != 0) {
                        paramMap.put(aloneParamName, arg);
                    }
                    args[1] = paramMap;
                }
            }
        }


        return invocation.proceed();
    }

    @Override
    public void setProperties(Properties properties) {
        aloneParamName = (String) properties.get("aloneParamName");
    }

    private FieldStrategyController extractFieldStrategyController(String msId) {
        if (msId.equals(SELECT_KEY_SUFFIX)) {
            return null;
        }
        return FIELD_STRATEGY_CONTROLLER_MAP.computeIfAbsent(msId,
                s -> MsIdUtils.extract(msId, FieldStrategyController.class));
    }

}
