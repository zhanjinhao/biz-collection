package cn.addenda.bc.rbac.service;

import cn.addenda.bc.bc.ServiceException;
import cn.addenda.bc.bc.jc.pojo.Ternary;
import cn.addenda.bc.bc.jc.util.BeanUtil;
import cn.addenda.bc.bc.jc.util.IterableUtils;
import cn.addenda.bc.bc.sc.lock.Locked;
import cn.addenda.bc.bc.sc.transaction.TransactionAttrBuilder;
import cn.addenda.bc.bc.sc.transaction.TransactionHelper;
import cn.addenda.bc.rbac.manager.ModuleManager;
import cn.addenda.bc.rbac.manager.RoleManager;
import cn.addenda.bc.rbac.manager.RoleModuleManager;
import cn.addenda.bc.rbac.pojo.bo.BModuleTree;
import cn.addenda.bc.rbac.pojo.entity.Module;
import cn.addenda.bc.rbac.pojo.entity.Role;
import cn.addenda.bc.rbac.pojo.entity.RoleModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.interceptor.TransactionAttribute;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author addenda
 * @since 2022/2/7 17:16
 */
@Component
public class RoleModuleServiceImpl implements RoleModuleService {

    @Autowired
    private RoleModuleManager roleModuleManager;

    @Autowired
    private RoleManager roleManager;

    @Autowired
    private ModuleManager moduleManager;

    @Autowired
    private TransactionHelper transactionHelper;


    @Locked(prefix = "role:roleSqc")
    @Override
    public Boolean save(Long roleSqc, List<Long> moduleSqcList) {
        if (!roleManager.sqcExists(roleSqc)) {
            throw new ServiceException("roleSqc不存在：" + roleSqc + "。");
        }

        TransactionAttribute rrAttribute = TransactionAttrBuilder.newRRBuilder().build();
        return transactionHelper.doTransaction(rrAttribute, () -> {
            // 从数据库查出来角色已经有的模块
            List<RoleModule> roleModuleListFromDb = roleModuleManager.queryModuleOfRole(roleSqc);

            List<Long> moduleSqcListFromDb = roleModuleListFromDb
                    .stream()
                    .map(RoleModule::getModuleSqc)
                    .collect(Collectors.toList());
            Ternary<List<Long>, List<Long>, List<Long>> separate =
                    IterableUtils.separate(moduleSqcListFromDb, moduleSqcList);

            // 数据库有&参数没有，需要删除
            List<Long> deleteList = new ArrayList<>();
            for (Long moduleSqc : separate.getF1()) {
                Map<Long, Long> roleModuleMapFromDb = roleModuleListFromDb
                        .stream()
                        .collect(Collectors.toMap(RoleModule::getModuleSqc, RoleModule::getSqc));
                deleteList.add(roleModuleMapFromDb.get(moduleSqc));
            }

            // 参数有&数据库没有，需要增加
            List<RoleModule> insertList = separate.getF3()
                    .stream()
                    .map(item -> new RoleModule(roleSqc, item))
                    .collect(Collectors.toList());

            roleModuleManager.batchDeleteBySqc(deleteList);
            roleModuleManager.batchInsert(insertList);

            return true;
        });
    }

    @Override
    public BModuleTree queryModuleOfRole(Long roleSqc, String accessType) {
        if (!roleManager.sqcExists(roleSqc)) {
            throw new ServiceException("roleSqc不存在：" + roleSqc + "。");
        }
        List<RoleModule> roleModuleList = roleModuleManager.queryModuleOfRole(roleSqc);

        // 查询出来这个角色下的目录
        List<Module> moduleList = roleModuleList.stream().map(
                item -> moduleManager.queryBySqc(item.getModuleSqc())).collect(Collectors.toList());

        if (Module.AT_LISTEN.equals(accessType)) {
            return getRoot();
        } else if (Module.AT_WRITE.equals(accessType)) {
            moduleList.removeIf(item -> Module.AT_READ.equals(item.getAccessType()));
        } else if (Module.AT_READ.equals(accessType)) {
            // no-op
        } else {
            throw new ServiceException("不合法的accessType！");
        }

        // 按照 <parent, List<child>> 分组
        Map<Long, List<Long>> parentSqcModuleMap = new HashMap<>();
        for (Module module : moduleList) {
            List<Long> tree = parentSqcModuleMap.computeIfAbsent(module.getParentSqc(), s -> new ArrayList<>());
            tree.add(module.getSqc());
        }

        Map<Long, Module> moduleMap = moduleList.stream().collect(Collectors.toMap(Module::getSqc, a -> a));
        BModuleTree root = getRoot();
        Queue<BModuleTree> queue = new LinkedList<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            BModuleTree parent = queue.poll();
            Long sqc = parent.getSqc();
            List<Long> childrenSqc = parentSqcModuleMap.get(sqc);

            if (childrenSqc != null && !childrenSqc.isEmpty()) {
                List<BModuleTree> bModuleTreeList = parent.getBModuleTreeList();
                for (Long childSqc : childrenSqc) {
                    BModuleTree child = BeanUtil.copyProperties(moduleMap.get(childSqc), new BModuleTree());
                    bModuleTreeList.add(child);
                    queue.offer(child);
                }
            }
        }

        return root;
    }

    private BModuleTree getRoot() {
        Module module = moduleManager.queryBySqc(Module.ROOT_SQC);
        return BeanUtil.copyProperties(module, new BModuleTree());
    }

    @Override
    public List<Role> queryRoleOnModule(Long moduleSqc) {
        if (!moduleManager.sqcExists(moduleSqc)) {
            throw new ServiceException("moduleSqc不存在：" + moduleSqc + "。");
        }

        return roleModuleManager.queryRoleOnModule(moduleSqc);
    }

}
