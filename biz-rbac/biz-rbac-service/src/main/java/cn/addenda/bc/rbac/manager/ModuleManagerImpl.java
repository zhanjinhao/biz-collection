package cn.addenda.bc.rbac.manager;

import cn.addenda.bc.bc.jc.cache.CacheHelper;
import cn.addenda.bc.rbac.constant.RedisKeyConst;
import cn.addenda.bc.rbac.mapper.ModuleMapper;
import cn.addenda.bc.rbac.pojo.entity.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * @author addenda
 * @since 2022/10/10 11:27
 */
@Component
public class ModuleManagerImpl implements ModuleManager {

    @Autowired
    private ModuleMapper moduleMapper;

    @Autowired
    private CacheHelper redisCacheHelper;

    @Override
    public boolean moduleCodeExists(String moduleCode) {
        Integer integer = moduleMapper.moduleCodeExists(moduleCode);
        return integer != null && integer > 0;
    }

    @Override
    public void insert(Module module) {
        moduleMapper.insert(module);
    }

    @Override
    public boolean sqcExists(Long sqc) {
        Integer integer = moduleMapper.sqcExists(sqc);
        return integer != null && integer > 0;
    }

    @Override
    public void deleteBySqc(Long sqc) {
        moduleMapper.deleteBySqc(sqc);
    }

    @Override
    public void setStatus(Long sqc, String status) {
        Module module = new Module();
        module.setSqc(sqc);
        module.setStatus(status);
        moduleMapper.updateNonNullFieldsBySqc(module);
    }

    @Override
    public Module queryByModuleCode(String moduleCode) {
        return redisCacheHelper.queryWithPpf(RedisKeyConst.MODULE_MODULECODE_KEY,
                moduleCode, Module.class, moduleMapper::queryByModuleCode, RedisKeyConst.CACHE_DEFAULT_TTL);
    }

    @Override
    public List<Module> queryByNonNullFields(Module module) {
        return moduleMapper.queryByNonNullFields(module);
    }

    @Override
    public Module queryBySqc(Long sqc) {
        return redisCacheHelper.queryWithPpf(RedisKeyConst.MODULE_SQC_KEY,
                sqc, Module.class, this::doQueryBySqc, RedisKeyConst.CACHE_DEFAULT_TTL);
    }

    private Module doQueryBySqc(Long sqc) {
        Module module = new Module();
        module.setSqc(sqc);
        List<Module> moduleList = moduleMapper.queryByNonNullFields(module);
        if (moduleList.isEmpty()) {
            return null;
        }
        return moduleList.get(0);
    }

    @Override
    public void update(Module module) {
        redisCacheHelper.acceptWithPpf(
                RedisKeyConst.MODULE_SQC_KEY, module.getSqc(),
                aLong -> moduleMapper.updateNonNullFieldsBySqc(module));
    }
}
