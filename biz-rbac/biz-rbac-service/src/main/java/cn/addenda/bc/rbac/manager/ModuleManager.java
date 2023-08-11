package cn.addenda.bc.rbac.manager;


import cn.addenda.bc.rbac.pojo.entity.Module;

import java.util.List;

/**
 * @author addenda
 * @since 2022/10/10 11:27
 */
public interface ModuleManager {

    boolean moduleCodeExists(String moduleCode);

    void insert(Module module);

    boolean sqcExists(Long sqc);

    void deleteBySqc(Long sqc);

    void setStatus(Long sqc, String status);

    Module queryByModuleCode(String moduleCode);

    List<Module> queryByNonNullFields(Module module);

    Module queryBySqc(Long sqc);

    void update(Module module);

}
