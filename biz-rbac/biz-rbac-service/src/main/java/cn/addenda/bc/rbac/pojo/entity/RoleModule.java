package cn.addenda.bc.rbac.pojo.entity;

import cn.addenda.bc.bc.mc.idfilling.annotation.IdScope;
import cn.addenda.footprints.core.interceptor.baseentity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author addenda
 * @since 2022/1/17 20:45
 */
@Getter
@Setter
@ToString
@IdScope(scopeName = "tRoleModuleSqc", idFieldName = "sqc")
public class RoleModule extends BaseEntity {

    public RoleModule() {
    }

    public RoleModule(long roleSqc, long moduleSqc) {
        this.roleSqc = roleSqc;
        this.moduleSqc = moduleSqc;
    }

    private Long sqc;

    private Long roleSqc;

    private Long moduleSqc;

}
