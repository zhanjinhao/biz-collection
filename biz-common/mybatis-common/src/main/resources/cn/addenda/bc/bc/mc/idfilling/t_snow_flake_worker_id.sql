create table t_snow_flake_worker_id
(
    id       int,
    next_id  int         not null,
    app_name varchar(50) not null
);

create unique index t_snow_flake_worker_id_app_name_uindex
    on t_snow_flake_worker_id (app_name);

alter table t_snow_flake_worker_id
    modify id int auto_increment;

