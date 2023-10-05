create table t_idempotence_storage_center
(
    id             int auto_increment
        primary key,
    namespace      varchar(50)                        null comment '命名空间',
    prefix         varchar(50)                        null comment '前缀',
    `key`          varchar(100)                       null comment '幂等键',
    consume_mode   varchar(10)                        null comment '消费模式',
    x_id           char(32)                           null comment '消费全局ID',
    consume_status varchar(10)                        null comment '消费状态',
    expire_time    datetime                           null comment '过期时间',
    create_time    datetime default CURRENT_TIMESTAMP null comment '创建时间',
    constraint t_idempotence_storage_center_fullkey
        unique (namespace, prefix, `key`)
);

create index t_idempotence_storage_center_exp
    on t_idempotence_storage_center (expire_time);

create table t_idempotence_storage_center_his
(
    id             int auto_increment
        primary key,
    namespace      varchar(50)                        null comment '命名空间',
    prefix         varchar(50)                        null comment '前缀',
    `key`          varchar(100)                       null comment '幂等键',
    consume_mode   varchar(10)                        null comment '消费模式',
    x_id           char(32)                           null comment '全局消费ID',
    consume_status varchar(10)                        null comment '消费状态',
    expire_time    datetime                           null comment '过期时间',
    create_time    datetime default CURRENT_TIMESTAMP null comment '创建时间'
);

create index t_idempotence_storage_center_his_fullkey
    on t_idempotence_storage_center_his (namespace, prefix, `key`);

create table t_idempotence_exception_log
(
    id              int auto_increment
        primary key,
    namespace       varchar(50)                        null comment '命名空间',
    prefix          varchar(50)                        null comment '前缀',
    `key`           varchar(100)                       null comment '幂等键',
    consume_mode    varchar(10)                        null comment '消费模式',
    x_id            char(32)                           null comment '消费全局ID',
    consume_stage   varchar(20)                        null comment '消费阶段',
    scenario        varchar(10)                        null comment '业务场景：REQUEST、MQ',
    args            text                               null comment '參數',
    exception_msg   text                               null comment '异常信息',
    exception_stack text                               null comment '异常栈',
    create_time     datetime default CURRENT_TIMESTAMP null comment '创建时间'
);

create index t_idempotence_exception_log_fullkey
    on t_idempotence_exception_log (namespace, prefix, `key`);
