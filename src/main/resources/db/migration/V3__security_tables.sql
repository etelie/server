create table security_detail (
  detail_id serial not null unique primary key,
  security_id integer not null unique,
  security_name varchar(100) not null unique,
  security_description varchar(500) not null,
  interest_frequency integer not null,
  compounding_frequency integer default null,
  is_current boolean not null,
  is_fixed_rate boolean not null,
  is_fixed_par boolean not null,
  is_compoundable boolean not null,
  is_auctioned boolean not null,
  is_marketable boolean not null,
  is_callable boolean not null,
  is_taxable_federal boolean not null,
  is_taxable_state boolean not null,
  is_taxable_local boolean not null
);

comment on column security_detail.interest_frequency is 'unit: actions per year';
comment on column security_detail.compounding_frequency is 'unit: actions per year';

create table security_note (
  note_id serial not null unique primary key,
  security_id integer references security_detail(security_id) on delete cascade on update cascade,
  category varchar(30) not null,
  label varchar(100) not null,
  content varchar(2000) not null
);

create index on security_note (security_id);

create table security_price (
  price_id serial not null unique primary key,
  security_id integer references security_detail(security_id) on delete restrict on update cascade,
  purchased_timestamp timestamp not null,
  issued_timestamp timestamp,
  term integer not null,
  par_value numeric(1000, 4),
  discount_price numeric(1000, 4) generated always as (par_value / (interest_rate_fixed + 1)) stored,
  interest_rate_fixed numeric(1000, 4) not null,
  interest_rate_variable numeric(1000, 4) not null,
  yield_to_maturity numeric(1000, 4) not null
);

create index on security_price (security_id);

comment on table security_price is 'rates are annualized';
comment on column security_price.term is 'unit: months';
comment on column security_price.interest_rate_fixed is 'synonymous with discount_rate'
