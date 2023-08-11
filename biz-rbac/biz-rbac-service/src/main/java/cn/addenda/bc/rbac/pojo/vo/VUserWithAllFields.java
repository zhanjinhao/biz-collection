package cn.addenda.bc.rbac.pojo.vo;

import cn.addenda.bc.rbac.pojo.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author addenda
 * @since 2022/10/16 21:56
 */
@Setter
@Getter
@ToString(callSuper = true)
@JsonIgnoreProperties
public class VUserWithAllFields extends User {



}
