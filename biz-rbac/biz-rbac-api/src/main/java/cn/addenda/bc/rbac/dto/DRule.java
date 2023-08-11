package cn.addenda.bc.rbac.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author addenda
 * @since 2022/12/4 14:10
 */
@Setter
@Getter
@ToString
public class DRule implements Serializable {

    private static final long serialVersionUID = 1L;

    private String tableName;

    private String condition;

}
