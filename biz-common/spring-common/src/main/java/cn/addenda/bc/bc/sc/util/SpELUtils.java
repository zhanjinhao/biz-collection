package cn.addenda.bc.bc.sc.util;

import cn.addenda.bc.bc.jc.util.JacksonUtils;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * @author addenda
 * @since 2023/7/29 19:08
 */
public class SpELUtils {

    private SpELUtils() {
    }

    private static final ExpressionParser SPEL_PARSER = new SpelExpressionParser();

    private static final LocalVariableTableParameterNameDiscoverer NAME_DISCOVERER = new LocalVariableTableParameterNameDiscoverer();

    public static Object getValue(String spEL, Method method, String spELArgsName, Object... arguments) {
        Expression exp = SPEL_PARSER.parseExpression(spEL);
        StandardEvaluationContext context = new StandardEvaluationContext();
        if (method != null) {
            String[] params = NAME_DISCOVERER.getParameterNames(method);
            if (params != null && params.length != 0) {
                for (int len = 0; len < params.length; len++) {
                    context.setVariable(params[len], arguments[len]);
                }
            }
        }
        context.setVariable(spELArgsName, arguments);
        return exp.getValue(context);
    }

    public static String getKey(String spEL, Method method, String spELArgsName, Object... arguments) {
        // 默认取第一位参数
        if (!StringUtils.hasLength(spEL)) {
            spEL = "#" + spELArgsName + "[0]";
        }

        if (spEL.contains("#")) {
            Object value = SpELUtils.getValue(spEL, method, spELArgsName, arguments);
            if (value == null) {
                String msg = String.format("Cannot get value from arguments: [%s], spEL: [%s].", JacksonUtils.objectToString(arguments), spEL);
                throw new RuntimeException(msg);
            }
            return value.toString();
        }
        return spEL;
    }

}
