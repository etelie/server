create table importer (
  importer_id serial unique not null primary key,
  name varchar(100) unique not null,
  cron_expression varchar(100) not null,
  cron_comment varchar(300)
);

create table association_importer_security (
  association_id serial unique not null primary key,
  importer_id integer references importer(importer_id) on delete cascade on update cascade,
  security_id integer references security_detail(security_id) on delete cascade on update cascade,
  security_serial_name varchar(200) not null
);

create unique index on association_importer_security (importer_id, security_id);
