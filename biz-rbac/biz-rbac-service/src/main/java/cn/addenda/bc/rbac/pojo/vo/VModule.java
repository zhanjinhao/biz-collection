package cn.addenda.bc.rbac.pojo.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author addenda
 * @since 2022/10/10 10:52
 */
@Setter
@Getter
@ToString
public class VModule {

    private String moduleCode;

    private String moduleName;

    private Long parentSqc;

    private String action;

    private String accessType;

    private String showType;

    private String responseToType;

}
