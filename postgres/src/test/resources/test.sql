create schema testdb;
create table testdb.t_user
(
    id            serial primary key,
    varchar_array varchar array,
    decimal_array decimal array
);

insert into testdb.t_user(decimal_array, varchar_array)
values (array[0.1,0.2], array['a', 'b']);

insert into testdb.t_user(decimal_array, varchar_array)
values (array[0.1,0.2], array['a', 'b']);