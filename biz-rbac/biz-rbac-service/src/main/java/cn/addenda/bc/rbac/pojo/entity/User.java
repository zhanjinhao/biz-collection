package cn.addenda.bc.rbac.pojo.entity;

import cn.addenda.bc.bc.mc.idfilling.annotation.IdScope;
import cn.addenda.footprints.core.interceptor.baseentity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author addenda
 * @since 2022/1/17 20:28
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@IdScope(scopeName = "tUserSqc", idFieldName = "sqc")
public class User extends BaseEntity {

    private Long sqc;

    /**
     * 唯一索引
     */
    private String userId;

    private String userName;

    private String userEmail;

    private String status;

    public User(Long sqc) {
        this.sqc = sqc;
    }
}
