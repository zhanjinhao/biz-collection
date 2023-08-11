package cn.addenda.bc.rbac.pojo.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author addenda
 * @since 2022/10/10 14:30
 */
@Setter
@Getter
@ToString
public class VUserRolePermission {

    private String accessType;

    private String ruleSqcList;

}
