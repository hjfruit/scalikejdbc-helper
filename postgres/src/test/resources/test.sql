create schema testdb;
create table testdb.t_user
(
    id            varchar,
    varchar_array varchar array,
    decimal_array decimal array,
    int_array     integer[] default ARRAY[]:: integer [],
    long_array    bigint[] default ARRAY[]:: integer [],
    parent_id     varchar  default '',
    enum_id       smallint default 0
);

create unique index udxOrPk on testdb.t_user (id);

insert into testdb.t_user(id, decimal_array, varchar_array)
values ('1', array[0.1, 0.2], array['a', 'b']);

insert into testdb.t_user(id, decimal_array, varchar_array)
values ('2', array[0.1, 0.2], array['a', 'b']);

create table testdb.t_enum
(
    id smallint
);

create table testdb.t_json
(
    name json default '{}'
);