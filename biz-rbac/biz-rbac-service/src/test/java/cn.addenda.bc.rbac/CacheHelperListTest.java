package cn.addenda.bc.rbac;

import cn.addenda.bc.bc.jc.cache.CacheHelper;
import cn.addenda.bc.rbac.constant.RedisKeyConst;
import cn.addenda.bc.rbac.manager.RoleModuleManager;
import cn.addenda.bc.rbac.pojo.entity.Role;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;


/**
 * @author addenda
 * @since 2023/3/2 21:07
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CacheHelperListTest {
    @Autowired
    private RoleModuleManager roleModuleManager;

    @Autowired
    private CacheHelper cacheHelper;

    @Test
    public void test1() {
        List<Role> ppfList = cacheHelper.queryWithPpf("roleOnModule:", 10000L, new TypeReference<List<Role>>() {
        }, roleModuleManager::queryRoleOnModule, RedisKeyConst.CACHE_DEFAULT_TTL);
        System.out.println(ppfList);

        List<Role> rtfList = cacheHelper.queryWithRdf("roleOnModule:", 10000L, new TypeReference<List<Role>>() {
        }, roleModuleManager::queryRoleOnModule, RedisKeyConst.CACHE_DEFAULT_TTL);
        System.out.println(rtfList);

    }

}
