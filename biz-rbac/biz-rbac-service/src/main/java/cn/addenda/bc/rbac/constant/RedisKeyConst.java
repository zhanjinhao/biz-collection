package cn.addenda.bc.rbac.constant;

public class RedisKeyConst {

    private RedisKeyConst() {
    }

    public static final Long CACHE_DEFAULT_TTL = 30 * 60 * 1000L;

    public static final String USER_SQC_KEY = "rbac:user:sqc:";
    public static final String USER_USERID_KEY = "rbac:user:userid:";

    public static final String MODULE_SQC_KEY = "rbac:module:sqc:";
    public static final String MODULE_MODULECODE_KEY = "rbac:module:modulecode:";

    public static final String ROLE_SQC_KEY = "rbac:role:sqc:";
    public static final String ROLE_ROLECODE_KEY = "rbac:role:rolecode:";

    public static final String RULE_SQC_KEY = "rbac:rule:sqc:";
    public static final String RULE_RULECODE_KEY = "rbac:rule:rulecode:";

}
