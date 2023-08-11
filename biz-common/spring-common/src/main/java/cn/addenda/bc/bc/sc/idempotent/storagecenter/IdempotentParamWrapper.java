package cn.addenda.bc.bc.sc.idempotent.storagecenter;

import cn.addenda.bc.bc.sc.idempotent.ConsumeMode;
import lombok.*;

/**
 * @author addenda
 * @since 2023/7/29 18:07
 */
@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdempotentParamWrapper {

    private String namespace;

    private String prefix;

    private String key;

    private ConsumeMode consumeMode;

    private int timeoutSecs;

    public String getFullKey() {
        return namespace + ":" + prefix + ":" + key + ":" + consumeMode;
    }

    public String getSimpleKey() {
        return prefix + ":" + key;
    }

}
