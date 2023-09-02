create table t_idempotent_exception_log
(
    id              int auto_increment
        primary key,
    namespace       varchar(20)                        null comment '命名空间',
    prefix          varchar(20)                        null comment '前缀',
    `key`           varchar(100)                       null comment '幂等键',
    consume_mode    varchar(10)                        null comment '消费模式',
    args            text                               null comment '參數',
    exception_msg   text                               null comment '异常信息',
    exception_stack text                               null comment '异常栈',
    create_time     datetime default CURRENT_TIMESTAMP null comment '创建时间',
    if_del          tinyint  default 0                 not null comment '删除标志'
);

create index t_idempotent_storage_center_fullkey
    on t_idempotent_exception_log (namespace, prefix, `key`, id, consume_mode);

create table t_idempotent_storage_center
(
    id             int auto_increment
        primary key,
    namespace      varchar(20)                        null comment '命名空间',
    prefix         varchar(20)                        null comment '前缀',
    `key`          varchar(100)                       null comment '幂等键',
    consume_mode   varchar(10)                        null comment '消费模式',
    expire_time    datetime                           null comment '过期时间',
    consume_status varchar(10)                        null comment '消费状态',
    create_time    datetime default CURRENT_TIMESTAMP null comment '创建时间'
);

create index t_idempotent_storage_center_fullkey
    on t_idempotent_storage_center (namespace, prefix, `key`, id, consume_mode);