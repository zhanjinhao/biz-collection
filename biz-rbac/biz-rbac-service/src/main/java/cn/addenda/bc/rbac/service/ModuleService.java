package cn.addenda.bc.rbac.service;

import cn.addenda.bc.rbac.pojo.entity.Module;
import com.github.pagehelper.PageInfo;

/**
 * @author addenda
 * @since 2022/2/7 17:16
 */
public interface ModuleService {

    Long rootSqc();

   Long insert(Module module);

   PageInfo<Module> pageQuery(Integer pageNum, Integer pageSize, Module module);

   Module queryBySqc(Long sqc);

   Boolean update(Module module);

   Boolean setStatus(Long sqc, String status);

   Boolean deleteBySqc(Long sqc);

   Module queryByModuleCode(String moduleCode);

}
