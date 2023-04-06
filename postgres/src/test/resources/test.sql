create schema testdb;
create table testdb.t_user
(
    id            varchar,
    varchar_array varchar array,
    decimal_array decimal array
);

insert into testdb.t_user(id, decimal_array, varchar_array)
values ('1', array[0.1, 0.2], array['a', 'b']);

insert into testdb.t_user(id, decimal_array, varchar_array)
values ('2', array[0.1, 0.2], array['a', 'b']);