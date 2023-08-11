package cn.addenda.bc.rbac.mapper;

import cn.addenda.bc.rbac.pojo.entity.Module;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @author addenda
 * @since 2022/2/7 15:56
 */
public interface ModuleMapper {

    void insert(Module module);

    Integer moduleCodeExists(@Param("moduleCode") String moduleCode);

    Integer sqcExists(@Param("sqc") Long sqc);

    void deleteBySqc(@Param("sqc") Long sqc);

    void updateNonNullFieldsBySqc(Module module);

    Module queryByModuleCode(@Param("moduleCode") String moduleCode);

    List<Module> queryByNonNullFields(Module module);

}
