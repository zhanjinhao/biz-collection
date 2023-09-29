package cn.addenda.bc.rbac;

import cn.addenda.bc.bc.sc.util.SpELUtils;
import cn.addenda.bc.bc.uc.user.UserContext;
import cn.addenda.bc.bc.uc.user.UserInfo;
import cn.addenda.bc.rbac.pojo.entity.User;
import cn.addenda.footprints.core.util.ArrayUtils;
import org.junit.Test;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author addenda
 * @since 2022/11/30 19:54
 */
public class SpringELTest {

    @Test
    public void main() {
        ExpressionParser parser = new SpelExpressionParser();

        StandardEvaluationContext strContext = new StandardEvaluationContext(123);
        String str = parser.parseExpression("T(String).valueOf(#this)").getValue(strContext, String.class);
        System.out.println(str);

        User user1 = new User();
        user1.setUserId("1234");
        user1.setUserEmail("1234@qq.com");
        StandardEvaluationContext pojoContext = new StandardEvaluationContext(user1);
        String pojo = parser.parseExpression("#this.userId").getValue(pojoContext, String.class);
        System.out.println(pojo);

        User user2 = new User();
        user2.setUserId("2234");
        user2.setUserEmail("2234@qq.com");
        StandardEvaluationContext listContext = new StandardEvaluationContext(ArrayUtils.asArrayList(user1, user2));
        String list = parser.parseExpression("#this[1].userId").getValue(listContext, String.class);
        System.out.println(list);

        Map<String, User> map = new HashMap<>();
        map.put("user1", user1);
        StandardEvaluationContext mapContext = new StandardEvaluationContext(map);
        String mapStr = parser.parseExpression("#this['user1'].userId").getValue(mapContext, String.class);
        System.out.println(mapStr);
    }

    @Test
    public void test6() {
        UserContext.runWithCustomUser(() -> {
            ExpressionParser parser = new SpelExpressionParser();
            Expression exp = parser.parseExpression(SpELUtils.USER_ID);
            List<String> list = new ArrayList<>();
            list.add("123");
            StandardEvaluationContext context = new StandardEvaluationContext();
            context.setVariable("spELArgs", list);
            System.out.println(exp.getValue(context));
        }, UserInfo.builder().userId("springeltest").build());
    }

    @Test
    public void test7() {
        System.out.println(SpELUtils.USER_ID.matches("T\\([\\w.]+\\)\\.\\w+\\(\\)"));
    }

}
