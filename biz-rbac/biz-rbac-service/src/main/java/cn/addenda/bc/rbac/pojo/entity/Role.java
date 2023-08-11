package cn.addenda.bc.rbac.pojo.entity;

import cn.addenda.bc.bc.mc.idfilling.annotation.IdScope;
import cn.addenda.footprints.core.interceptor.baseentity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author addenda
 * @since 2022/1/17 20:31
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@IdScope(scopeName = "tRoleSqc", idFieldName = "sqc")
public class Role extends BaseEntity {

    private Long sqc;

    /**
     * 唯一索引
     */
    private String roleCode;

    private String roleName;

    private String status;

    public Role(Long sqc) {
        this.sqc = sqc;
    }
}
