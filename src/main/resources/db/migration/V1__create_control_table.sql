create table if not exists control (
  id serial primary key,
  identifier varchar(50) not null unique,
  state integer not null
);
