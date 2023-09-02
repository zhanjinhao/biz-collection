package cn.addenda.bc.rbac.service;

import cn.addenda.bc.bc.ServiceException;
import cn.addenda.bc.bc.sc.lock.LockHelper;
import cn.addenda.bc.bc.sc.lock.Locked;
import cn.addenda.bc.bc.sc.transaction.TransactionHelper;
import cn.addenda.bc.rbac.manager.ModuleManager;
import cn.addenda.bc.rbac.manager.RoleModuleManager;
import cn.addenda.bc.rbac.pojo.entity.Module;
import cn.addenda.bc.rbac.utils.StatusUtils;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author addenda
 * @since 2022/2/7 17:16
 */
@Component
public class ModuleServiceImpl implements ModuleService {

    @Autowired
    private ModuleManager moduleManager;

    @Autowired
    private RoleModuleManager roleModuleManager;

    @Autowired
    private LockHelper lockHelper;

    @Autowired
    private TransactionHelper transactionHelper;

    @Override
    public Long rootSqc() {
        return Module.ROOT_SQC;
    }

    @Override
    @Locked(prefix = "module:moduleCode", spEL = "#module.moduleCode")
    public Long insert(Module module) {
        assertModule(module);
        if (moduleManager.moduleCodeExists(module.getModuleCode())) {
            throw new ServiceException("moduleCode已存在：" + module.getModuleCode() + "。");
        }
        return lockHelper.lock("module:root", () -> {
            // 特殊处理根目录
            if (Module.PARENT_SQC_OF_ROOT.equals(module.getParentSqc())) {
                if (moduleManager.sqcExists(0L)) {
                    throw new ServiceException("根目录已存在！");
                }
                module.setSqc(0L);
            }
            module.setStatus(StatusUtils.ACTIVE);
            transactionHelper.doTransaction(() -> moduleManager.insert(module));
            return module.getSqc();
        }, 0);
    }

    @Override
    public PageInfo<Module> pageQuery(Integer pageNum, Integer pageSize, Module module) {
        try {
            PageMethod.startPage(pageNum, pageSize);
            List<Module> query = moduleManager.queryByNonNullFields(module);
            return new PageInfo<>(query);
        } finally {
            PageMethod.clearPage();
        }
    }

    @Override
    public Module queryBySqc(Long sqc) {
        return moduleManager.queryBySqc(sqc);
    }

    @Override
    public Boolean update(Module module) {
        assertModule(module);
        if (!moduleManager.sqcExists(module.getSqc())) {
            throw new ServiceException("sqc不存在：" + module.getSqc() + "。");
        }

        return transactionHelper.doTransaction(() -> {
            moduleManager.update(module);
            return true;
        });
    }

    @Override
    public Boolean setStatus(Long sqc, String status) {
        StatusUtils.assertDAndAThrowSe(status);
        if (!moduleManager.sqcExists(sqc)) {
            throw new ServiceException("sqc不存在：" + sqc + "。 ");
        }

        return transactionHelper.doTransaction(() -> {
            moduleManager.setStatus(sqc, status);
            return true;
        });
    }

    @Override
    public Boolean deleteBySqc(Long sqc) {
        // 如果module被角色关联，则不可删除
        if (roleModuleManager.moduleSqcExists(sqc)) {
            throw new ServiceException("此Module正被角色使用，不可删除！");
        }

        return transactionHelper.doTransaction(() -> {
            moduleManager.deleteBySqc(sqc);
            return true;
        });
    }

    @Override
    public Module queryByModuleCode(String moduleCode) {
        return moduleManager.queryByModuleCode(moduleCode);
    }

    private void assertModule(Module module) {
        if (!Module.AT_READ.equals(module.getAccessType())
                && !Module.AT_WRITE.equals(module.getAccessType())) {
            throw new ServiceException("不合法的accessType：" + module.getAccessType() + "。");
        }
        if (!Module.ST_NAVIGATION.equals(module.getShowType())
                && !Module.ST_PAGE.equals(module.getShowType()) && !Module.ST_FUNCTION.equals(module.getShowType())) {
            throw new ServiceException("不合法的showType：" + module.getShowType() + "。");
        }
        if (!Module.RTT_CURRENT.equals(module.getResponseToType())
                && !Module.RTT_NEW.equals(module.getResponseToType()) && !Module.RTT_JUMP.equals(module.getResponseToType())) {
            throw new ServiceException("不合法的responseToType：" + module.getResponseToType() + "。");
        }
    }

}
